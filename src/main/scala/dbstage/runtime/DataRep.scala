package dbstage
package runtime

import squid.utils._
import Embedding.Predef._
//import Embedding.IntermediateIROps
import Embedding.SimplePredef.{Rep => Code, _}
import frontend._

import scala.collection.mutable

class DataRep {
  
}

//class DataRep {
//  class LoadingScheme
//  class FromFile extends LoadingScheme
//}
//object InMemoryData extends DataRep {
//  object FromCSV extends FromFile
//}

import scala.io._

trait StorageManager {
  def fromCSV(tbl: Relation, src: Source, separator: String = "|"): DataRep
}

object InMemoryStorageManager extends StorageManager {
  
  //class DataRep(cols: Product) extends runtime.DataRep {
  //class DataRep[Row](tbl: Table) extends runtime.DataRep {
  //abstract class DataRep(val row: Row) extends runtime.DataRep {
  //  val data: IndexedSeq[row.T]
  //}
  
  
  def fromCSV(tbl: Relation, src: Source, separator: String = "|"): DataRep = {
    tbl.columns
    //new DataRep(tbl)
    ???
  }
  
}

trait RowFormat {
  type Repr
  implicit val Repr: IRType[Repr]
  val columns: Seq[Field]
  val parse: Map[String, Code[String]] => Code[Repr]
}
object RowFormat {
  def apply(cols: Seq[Field]): RowFormat = {
    val size = cols.size
    if (size == 1) new SingleColumnFormat(cols.head)
    else if (size > 22) {
      val t = cols.take(size/2)
      CompositeFormat(RowFormat(t), RowFormat(cols.drop(t.size)))
    }
    else new TupleFormat(cols)
  }
}
class SingleColumnFormat(val col: Field) extends RowFormat {
  type Repr = col.T
  implicit val Repr = col.IRTypeT // Note: removing `implicit` creates a compiler crash...
  val columns = col :: Nil
  val parse = (cs: Map[String, Code[String]]) => ir"${col.SerialT.parse}(${cs(col.name)})"
}
//class TupleFormat(val columns: Seq[Field]) extends RowFormat {
case class TupleFormat(columns: Seq[Field]) extends RowFormat {
  val size = columns.size
  val clsSym = base.loadTypSymbol(s"scala.Tuple${size}")
  val objSym = base.loadTypSymbol(s"scala.Tuple${size}$$")
  val mtd = base.loadMtdSymbol(objSym, "apply", None)
  val typs = columns.map(_.IRTypeT.rep).toList
  val typ = base.staticTypeApp(clsSym, typs)
  implicit val Repr = base.IRType[Repr](typ)
  val parse = {
    import base._
    (cs: Map[String, Code[String]]) => {
      IR(methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(columns map (c => 
        ir"${c.SerialT.parse}(${cs(c.name)})".rep): _*)::Nil, typ))
    }
  }
}
case class CompositeFormat(val lhs: RowFormat, val rhs: RowFormat) extends RowFormat {
  import lhs.{Repr=>LR}, rhs.{Repr=>RR}
  type Repr = (lhs.Repr, rhs.Repr)
  //val Repr = irTypeOf[(lhs.Repr,rhs.Repr)]
  lazy val Repr = irTypeOf[(LR,RR)]
  val columns = lhs.columns ++ rhs.columns
  val parse = (cs: Map[String, Code[String]]) => ir"(${lhs.parse(cs)}, ${rhs.parse(cs)})"
}

//abstract class Table(val rf: RowFormat) {
trait Table {
  val rowFmt: RowFormat
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit]
  def loadData(data: Iterator[String], sep: Char = '|'): Unit = {
    val pgrm = mkDataLoader(sep)
    println(s"Generated Program: $pgrm")
    pgrm.compile()(data)
  }
}
object Table {
  def apply(cols: Seq[Field]): Table = new PlainTable(cols)
}
class PlainTable(val cols: Seq[Field]) extends Table {
  val rowFmt: RowFormat = TupleFormat(cols)
  //def loadData(data: Iterator[String], sep: Char = '|'): CrossStage[Unit] = ???
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = ???
}
//class IndexedTable(val keys: Seq[RowFormat], val values: Seq[RowFormat]) extends Table {
case class IndexedTable(keys: Seq[Field], values: Seq[Field], order: Seq[String]) extends Table {
  val keyFmt: RowFormat = RowFormat(keys) // Note: using TupleFormat here may generate references to Tuple1... not sure how that's handled by Scala
  val valFmt: RowFormat = RowFormat(values)
  val rowFmt: RowFormat = CompositeFormat(keyFmt,valFmt)
  
  //type Key = keyFmt.Repr
  //type Val = valFmt.Repr
  import keyFmt.{Repr=>Key}
  import valFmt.{Repr=>Val}
  
  val hashTable = mutable.HashMap[Key,Val]()
  
  //def loadData(data: Iterator[String], sep: Char = '|'): CrossStage[Unit] = {
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = {
    val arr: Code[Array[String]] = ir"arr?:Array[String]"
    //val colMap = columns.zipWithIndex.map(ci => ci._1.name -> ir"$arr(${Const(ci._2)})").toMap
    val colMap = order.zipWithIndex.toMap.mapValues(name => ir"$arr(${Const(name)})")
    val kparser = keyFmt.parse(colMap)
    val vparser = valFmt.parse(colMap)
    //val kparser = keyFmt.parse(keys.zipWithIndex.map(ci => ci._1.name -> ir"$arr(${Const(ci._2)})").toMap)
    //val vparser = valFmt.parse(values.zipWithIndex.map(ci => ci._1.name -> ir"$arr(${Const(ci._2)})").toMap)
    (CrossStage(hashTable) { ht => 
      ir"""(ite: Iterator[String]) =>
        while (ite.hasNext) {
          val str = ite.next
          val arr = str.split(${Const(sep)})
          //println(">"+arr.toList)
          $ht += (${kparser:IR[Key,{val arr:Array[String]}]} -> ${vparser:IR[Val,{val arr:Array[String]}]})
        }
      """
    })//(dbg.implicitType[mutable.HashMap[Key,Val]],irTypeOf[Iterator[String] => Unit])
  }
  
}

/*
// TODO Table stored as hashmaps, compressed arrays of bits, etc
//abstract class Table[Row:IRType] {
trait Table {
  type Row
  implicit val Row: IRType[Row]
  val columns: Seq[Field] = null
  //def parse(columnStrings: Map[String, Code[String]]): Code[Row]
  val parse: Map[String, Code[String]] => Code[Row]
   = null
  //def load(x: Code[T]): CrossStage[Unit]
  //val load: Code[T] => CrossStage[Unit]
  val load: CrossStage[Row => Unit]
   = null
  /** override if better implementation exists */
  def loadMultiple(xs: Code[Iterator[Row]]): CrossStage[Unit] =
    for {
      loader <- load
    } yield ir"val it = $xs; while (it.hasNext) ${loader}(it.next)"
  def find(p: Code[Row => Bool]): CrossStage[Iterator[Row]]
   = ???
  def lift[R](f: Seq[Field] => Code[R]): Code[Row => R]
   = ???
  def show: String = s"Table(${columns mkString ","})"
}
object Table {
  //def apply(cols: Seq[Field]): Table[_] = {
  def apply(cols: Seq[Field]): Table = {
    val size = cols.size
    if (size == 1) SingleColumnTable(cols.head)
    else if (size > 22) {
      val t = cols.take(size/2)
      CompositeOf(Table(t), Table(cols.drop(t.size)))
    }
    else {
      val clsSym = base.loadTypSymbol(s"scala.Tuple${size}")
      val objSym = base.loadTypSymbol(s"scala.Tuple${size}$$")
      val mtd = base.loadMtdSymbol(objSym, "apply", None)
      val typs = cols.map(_.IRTypeT.rep).toList
      val typ = base.staticTypeApp(clsSym, typs)
      //println(clsSym,objSym,mtd,typ)
      
      //new Table[Any]()(base.IRType(typ))
      new Table // extract as PlainTupleTable
      {
        implicit val Row = base.IRType[Row](typ)
        val buffer = mutable.ArrayBuffer[Row]()
        
        override val columns = cols
        
        override val parse = {
          import base._
          //val p = methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(cols map (c => ir"${Const(c.name)}.asInstanceOf[${c.IRTypeT}]".rep): _*)::Nil, staticTypeApp(clsSym, typs))
          //val p = methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(cols map (c => ir"${c.SerialT.parse}(${Const(c.name)})".rep): _*)::Nil, typ)
          //println(p)
          //println(IR(p).compile)
          
          (cs: Map[String, Code[String]]) => {
            IR(methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(cols map (c => 
              ir"${c.SerialT.parse}(${cs(c.name)})".rep): _*)::Nil, typ))
          }
          
        }
        
        override val load: CrossStage[Row => Unit] = CrossStage(buffer)(buf => ir"(x:Row) => { $buf += x; () }")
        
        override def show: String = s"${super.show} =\n\t${buffer mkString "\n\t"}"
        
      }
      
      //???
      //null
    }
  }
}
/*class CompositeTable[T0:IRType,T1:IRType](lhs: Table[T0], rhs: Table[T1]) extends Table[(T0,T1)] {
  
}*/
//class CompositeTable(val lhs: Table, val rhs: Table) extends Table {
trait CompositeTable extends Table {
  val lhs: Table
  val rhs: Table
  //import lhs.{Row}, rhs.{Row}
  import lhs.{Row=>LR}, rhs.{Row=>RR}
  type Row = (lhs.Row, rhs.Row)
  //val Row = irTypeOf[(lhs.Row,rhs.Row)]
  lazy val Row = irTypeOf[(LR,RR)]
}
case class CompositeOf(val lhs: Table, val rhs: Table) extends CompositeTable
//object CompositeTable {
//  def apply(lhs: Table, rhs: Table) = new CompositeTable {
//    
//  }
//}
class SingleColumnTable[T:IRType] extends Table {
  type Row = T
  val Row = implicitly[IRType[T]]
}
object SingleColumnTable {
  val IntType = irTypeOf[Int]
  //def apply(col: Field): Table[_] = {
  def apply(col: Field): Table = {
    col.IRTypeT match {
      //case IntType => ??? // TODO compressed column?
      case _ => new SingleColumnTable[col.T]
    }
  }
}
abstract class IndexedTable(val keys: Seq[Field], val values: Seq[Field]) extends Table {
  
}
case class PrimaryIndexedTable(primaryKeys: Seq[Field], override val values: Seq[Field]) extends IndexedTable(primaryKeys, values) with CompositeTable {
  val cols = primaryKeys ++ values
  val lhs = Table(primaryKeys)
  val rhs = Table(values)
}
//object PrimaryIndexedTable {
//  def apply(primaryKeys: Seq[Field], cols: Seq[Field])
//}

/*
abstract class IndexedTable[T:IRType](val keys: Seq[Field], val cols: Seq[Field]) extends Table[T] {
}
abstract class PrimaryIndexedTable[T:IRType](primaryKeys: Seq[Field], cols: Seq[Field]) extends IndexedTable[T](primaryKeys, cols) {
}
*/


// TODO:
//class CompressedTable extends Table[Int] {
//}
*/





////class CrossStage[T,R](value: T, code: Code[R])
//object CrossStage {
//  //def apply[T,R](value: T)(code: Code[T] => Code[R]): CrossStage[T,R] = ???
//  val values = mutable.WeakHashMap[Int,Any]
//  def apply[T,R](value: T, name: String = "csp")(code: Code[T] => Code[R]): Code[R] = ???
//}
//class CrossStage[R](values: mutable.Buffer[Any], code: Code[R])
import CrossStage.Dict
//class CrossStage[R](values: Dict, code: Code[Dict => R])
class CrossStage[A:IRType](val values: Dict, val code: Code[Dict] => Code[A]) {
  def map[B:IRType](f: Code[A] => Code[B]) = new CrossStage[B](values, code andThen f)
  def flatMap[B:IRType](f: Code[A] => CrossStage[B]) = {
    val cs = f(code(ir"d?:Dict")) // FIXME Q: name clashes? would be safer for now to use placeholders
    new CrossStage[B](values ++ cs.values, (d:Code[Dict]) => (cs.code(d):IR[B,{val d:Dict}]) subs 'd -> d)
  }
  lazy val compile = {
    //val pgrm = ir"(d:Dict) => $code(d)".compile
    val pgrm = ir"$code(_)".compile
    () => pgrm(values)
  }
  
  override def toString: String = {
    val c = code(ir"d?:Dict")
    s"${base.showRep(c.rep)}\n\twhere: ${values map (kv => s"d(${kv._1}) = ${kv._2}; ") mkString}"
  }
  
}
object CrossStage {
  type Dict = Map[Int,Any]
  private var curId = 0
  def freshId = curId alsoDo (curId += 1)
  def apply[T0:IRType,R:IRType](value0: T0)(code: Code[T0] => Code[R]): CrossStage[R] = {
    val id0 = freshId
    //new CrossStage(Map(id0 -> value0), ir"(d:Dict) => ${code}(d(${Const(id0)}).asInstanceOf[T0])")
    new CrossStage(Map(id0 -> value0), (d:Code[Dict]) => code(ir"$d(${Const(id0)}).asInstanceOf[T0]"))
  }
  def apply[T0:IRType,T1:IRType,R:IRType](value0: T0, value1: T1)(code: (Code[T0],Code[T1]) => Code[R]): CrossStage[R] = {
    val id0,id1 = freshId
    //val fun = (d:Code[Dict]) => code(ir"$d(${Const(id0)}).asInstanceOf[T0]", ir"$d(${Const(id1)}).asInstanceOf[T1]")
    new CrossStage(Map(id0 -> value0, id1 -> value1), (d:Code[Dict]) => 
      code(ir"$d(${Const(id0)}).asInstanceOf[T0]", ir"$d(${Const(id1)}).asInstanceOf[T1]"))
  }
}




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
  def fromCSV(tbl: Table, src: Source, separator: String = "|"): DataRep
}

object InMemoryStorageManager extends StorageManager {
  
  //class DataRep(cols: Product) extends runtime.DataRep {
  //class DataRep[Row](tbl: Table) extends runtime.DataRep {
  //abstract class DataRep(val row: Row) extends runtime.DataRep {
  //  val data: IndexedSeq[row.T]
  //}
  
  
  def fromCSV(tbl: Table, src: Source, separator: String = "|"): DataRep = {
    tbl.columns
    //new DataRep(tbl)
    ???
  }
  
}

class MetaTable_OLD[T](cols: Seq[Field]) {
  
  val buffer = mutable.ArrayBuffer[Any]()
  
  def parse(columnStrings: Map[String, Code[String]]): CrossStage[Unit] = {
    println(cols)
    //println(cols map (columnStrings(_.name)))
    println(cols map (f => columnStrings(f.name)))
    
    //ir"(buf: mutable.ArrayBuffer[Any]) => ${}"
    
    CrossStage(buffer)(buf => ir"$buf += ${columnStrings(cols.head.name)}; ()")
    
    //???
  }
  
  
  
}

// TODO MetaTable stored as hashmaps, compressed arrays of bits, etc
abstract class MetaTable[T:IRType] {
  def parse(columnStrings: Map[String, Code[String]]): Code[T]
   = ???
  //def load(x: Code[T]): CrossStage[Unit]
  //val load: Code[T] => CrossStage[Unit]
  val load: CrossStage[T => Unit]
   = ???
  /** override if better implementation exists */
  def loadMultiple(xs: Code[Iterator[T]]): CrossStage[Unit] =
    for {
      loader <- load
    } yield ir"val it = $xs; while (it.hasNext) ${loader}(it.next)"
  def find(p: T => Code[Bool]): CrossStage[Iterator[T]]
   = ???
  def lift[R](f: Seq[Field] => Code[R]): Code[T => R]
   = ???
}
object MetaTable {
  def apply(cols: Seq[Field]): MetaTable[_] = {
    val size = cols.size
    val t = cols.take(size/2)
    //if (size == 1) ??? // TODO
    if (size == 1) SingleColumnTable(cols.head)
    else if (size > 22) new CompositeMetaTable(MetaTable(t), MetaTable(cols.drop(t.size)))
    else {
      val clsSym = base.loadTypSymbol(s"scala.Tuple${size}")
      val objSym = base.loadTypSymbol(s"scala.Tuple${size}$$")
      val mtd = base.loadMtdSymbol(objSym, "apply", None)
      val typs = cols.map(_.IRTypeT.rep).toList
      println(clsSym,objSym,mtd)
      
      {
        import base._
        val p = methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(cols map (c => ir"${Const(c.name)}.asInstanceOf[${c.IRTypeT}]".rep): _*)::Nil, staticTypeApp(clsSym, typs))
        println(p)
        //println(IR(p).compile)
      }
      
      //???
      null
    }
  }
}
class CompositeMetaTable[T0:IRType,T1:IRType](lhs: MetaTable[T0], rhs: MetaTable[T1]) extends MetaTable[(T0,T1)] {
  
}
object SingleColumnTable {
  val IntType = irTypeOf[Int]
  def apply(col: Field): MetaTable[_] = {
    col.IRTypeT match {
      case IntType => ??? // TODO compressed column?
      case _ => ??? // TODO
    }
  }
}

// TODO:
//class CompressedMetaTable extends MetaTable[Int] {
//}





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




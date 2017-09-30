package dbstage
package runtime

import scala.collection.mutable
import scala.io._
import squid.utils._
import Embedding.Predef._
import frontend._
import squid.lib.transparencyPropagating


abstract class FieldReifier {
  def apply(f: FieldRef): Code[f.T]
}
trait RowFormat { thisRow =>
  type Repr
  implicit val Repr: IRType[Repr]
  val columns: Seq[Field]
  
  val parse: Map[String, Code[String]] => Code[Repr]
  protected def getField(f:FieldRef) = columns.find(f conformsTo _).fold(throw new Exception(s"No column '${f}' in $this")) { c => // TODO B/E
    assert(c.IRTypeT <:< f.IRTypeT)
    c
  }
  def get(repr:Embedding.Rep,f:FieldRef): Embedding.Rep
  def lift2[T](fr: FieldReifier => Code[T]): Code[Repr => T] = {
    val repr = Embedding.bindVal(columns.map(_.name).mkString("_"),Repr.rep,Nil)
    Embedding.IR(Embedding.lambda(repr::Nil,
      fr(new FieldReifier {
        def apply(f: FieldRef) = Embedding.IR(get(repr|>Embedding.readVal,f))
      }).rep
    ))
  }
  def lift[T](q: Code[T], uid: Int): Code[Repr => T] = {
    lift2 { fr =>
      val r = q rewrite { case ir"${FieldRef(f)}:Any" => fr(f).asClosedIR } // note: Any, unsound
      //println("Lifting: "+q)
      //println("Lifted : "+r)
      r
    }
  }
  
  // TODO implement everywhere:
  def mkRefs: Code[Repr] = ???
  def runtimeReprOf(values: Any*): Repr = ???
  
  def withId(id: Int): RowFormat{type Repr = thisRow.Repr}
  
  override def toString = s"Row[${Repr.rep}](${columns mkString ","})"
}
object RowFormat {
  def apply(cols: Seq[Field]): RowFormat = {
    val size = cols.size
    if (size == 0) lastWords("Table with no columns")
    if (size == 1) new SingleColumnFormat(cols.head)
    else if (size > MAX_SCALA_TUPLE_ARITY) {
      val t = cols.take(size/2)
      CompositeFormat(RowFormat(t), RowFormat(cols.drop(t.size)))
    }
    else new TupleFormat(cols)
  }
  type Tuple[T] = TupleFormat{type Repr = T}
  type Of[T] = RowFormat{type Repr = T}
}
class SingleColumnFormat(val col: Field) extends RowFormat {
  type Repr = col.T
  implicit val Repr = col.IRTypeT // Note: removing `implicit` creates a compiler crash...
  val columns = col :: Nil
  val parse = (cs: Map[String, Code[String]]) => ir"${col.SerialT.parse}(${cs(col.name)})"
  def get(repr:Embedding.Rep,f:FieldRef): Embedding.Rep = { getField(f); repr }
  override def mkRefs: Code[Repr] = col.toCode
  def withId(id: Int) = {
    import col.SerialT
    SingleColumnFormat[Repr](col.name, Some(id))
  }
  override def runtimeReprOf(values: Any*): Repr = {
    //values |> { case Seq(v) => v }
    assert(values.size === 1)
    values.head.asInstanceOf[Repr]
  }
}
object SingleColumnFormat {
  def apply[S:IRType:Serial](name: String, id: Option[Int] = None) = {
    val c: Field{type T = S} = Field[S](name, id)
    new SingleColumnFormat(c) {
      override val col: Field{type T = S} = c
    }
  }
}
case class TupleFormat(columns: Seq[Field]) extends RowFormat { thisFmt =>
  val size = columns.size
  require(size > 0 && size <= MAX_SCALA_TUPLE_ARITY)
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
        ir"${c.SerialT.parse}(${cs(c.name)}):${c.IRTypeT}".rep): _*)::Nil, typ))  // the ascription ${c.IRTypeT} is to prevent the QQ from using a TypeTag
    }
  }
  def get(repr:Embedding.Rep,f:FieldRef): Embedding.Rep = {
    val c = getField(f)
    base.methodApp(repr,base.loadMtdSymbol(clsSym, "_" + (columns.indexWhere(c conformsTo _)+1), None),Nil,Nil,c.IRTypeT.rep)
  }
  override def mkRefs: Code[Repr] = { // TODO factor with parse
    import base._
    IR(methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(columns map (c => c.toCode.rep) : _*)::Nil, typ))
  }
  def mk(xs:Code[Any]*): Code[Repr] = { // TODO factor with parse
    import base._
    IR(methodApp(staticModule(s"scala.Tuple${size}"), mtd, typs, Args(xs map (_.rep) : _*)::Nil, typ))
  }
  def withId(id: Int) = TupleFormat(columns map (_ withId id)).asInstanceOf[TupleFormat{type Repr = thisFmt.Repr}]
  override def runtimeReprOf(values: Any*): Repr = {
    assert(values.size === size)
    (values match {
      case Seq(v0,v1) => (v0,v1)
      case Seq(v0,v1,v2) => (v0,v1,v2)
      case Seq(v0,v1,v2,v3) => (v0,v1,v2,v3)
      case Seq(v0,v1,v2,v3,v4) => (v0,v1,v2,v3,v4)
      case Seq(v0,v1,v2,v3,v4,v5) => (v0,v1,v2,v3,v4,v5)
      case Seq(v0,v1,v2,v3,v4,v5,v6) => (v0,v1,v2,v3,v4,v5,v6)
      case Seq(v0,v1,v2,v3,v4,v5,v6,v7) => (v0,v1,v2,v3,v4,v5,v6,v7)
      case Seq(v0,v1,v2,v3,v4,v5,v6,v7,v8) => (v0,v1,v2,v3,v4,v5,v6,v7,v8)
      case Seq(v0,v1,v2,v3,v4,v5,v6,v7,v8,v9) => (v0,v1,v2,v3,v4,v5,v6,v7,v8,v9)
      case Seq(v0,v1,v2,v3,v4,v5,v6,v7,v8,v9,v10) => (v0,v1,v2,v3,v4,v5,v6,v7,v8,v9,v10)
      case _ => lastWords(s"Tuple arity not yet supported: $size, for: ${values}")
    }).asInstanceOf[Repr]
  }
}
case class CompositeFormat[L<:RowFormat,R<:RowFormat](val lhs: L, val rhs: R) extends RowFormat { // TODO make abstract class/trait?
  import lhs.{Repr=>LR}, rhs.{Repr=>RR}
  type Repr = (lhs.Repr, rhs.Repr)
  lazy val Repr = irTypeOf[(LR,RR)]
  val columns = lhs.columns ++ rhs.columns
  val parse = (cs: Map[String, Code[String]]) => ir"(${lhs.parse(cs)}, ${rhs.parse(cs)})"
  def get(repr:Embedding.Rep,f:FieldRef): Embedding.Rep = {
    if (lhs.columns.exists(f conformsTo _))
      lhs.get(ir"${Embedding.IR[Repr,Any](repr)}._1".rep,f)
    else {
      assert(rhs.columns.exists(f conformsTo _), s"Field ref $f is not in $this")
      rhs.get(ir"${Embedding.IR[Repr,Any](repr)}._2".rep,f)
    } //alsoApply (println(s"Getting $f $lhs $rhs -> "+_))
  }
  def withId(id: Int) = CompositeFormat(lhs withId id, rhs withId id)
}

trait Table {
  val rowFmt: RowFormat
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit]
  def loadData(data: Iterator[String], sep: Char = '|'): Unit = {
    val pgrm = mkDataLoader(sep)
    println(s"Generated Program: $pgrm")
    pgrm.compile()(data)
  }
  // TODO: implement these everywhere:
  def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = ???
  def pull: CrossStage[() => (rowFmt.Repr => Unit) => Bool] = ???
  def pull2: CrossStage[IteratorRep[rowFmt.Repr]] = ???
}
object Table {
  def apply(cols: Seq[Field]): Table = new PlainTable(cols,0)
}
abstract class SimpleTable extends Table { // TODO merge into Table
  def mkEntryLoader(sep: Char): CrossStage[String => Unit]
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = 
    mkEntryLoader(sep) map (el => ir"(ite: Iterator[String]) => while (ite.hasNext) { val str = ite.next; ${el}(str) }")
}
class PlainTable(val cols: Seq[Field], idxShift: Int) extends SimpleTable {
  val rowFmt: RowFormat = RowFormat(cols)
  import rowFmt.{Repr => Val}
  val buffer = mutable.ArrayBuffer[Val]()
  private[this] val arr: Code[Array[String]] = ir"arr?:Array[String]"
  private[this] val colMap = cols.map(_.name).zipWithIndex.toMap.mapValues(i => ir"$arr(${Const(i+idxShift)})") // TODO factor with IndexedTable
  
  def mkEntryLoader(sep: Char): CrossStage[String => Unit] = CrossStage(buffer)(buf => 
    ir"(str: String) => { val arr = str.split(${Const(sep)}); $buf += ${rowFmt.parse(colMap):IR[Val,{val arr:Array[String]}]}; () }")
  
  override def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = CrossStage(buffer){ buf => ir"$buf.foreach($cont)" }
  
  override def pull: CrossStage[() => (rowFmt.Repr => Unit) => Bool] = CrossStage(buffer){ buf => 
    ir"val it = $buf.iterator; () => (k:rowFmt.Repr => Unit) => if (it.hasNext) { k(it.next); true } else false" }
  override def pull2: CrossStage[IteratorRep[rowFmt.Repr]] = CrossStage(buffer){ buf =>
    ir"val it = $buf.iterator; () => (it.hasNext _) -> (it.next _)" }
}
class SingleColumnTable(val col: Field, idxShift: Int) extends PlainTable(col::Nil, idxShift) {
  override val rowFmt = new SingleColumnFormat(col)
}

trait IndexedTable extends Table {
  val keys: Seq[Field]
  val values: Seq[Field] 
  val order: Seq[String]
  val keyFmt: RowFormat = RowFormat(keys) // Note: using TupleFormat here may generate references to Tuple1... not sure how that's handled by Scalac
  val valFmt: RowFormat = RowFormat(values)
  val rowFmt: CompositeFormat[keyFmt.type,valFmt.type] = CompositeFormat(keyFmt,valFmt)
  private[this] val arr: Code[Array[String]] = ir"arr?:Array[String]"
  private[this] val colMap = order.zipWithIndex.toMap.mapValues(name => ir"$arr(${Const(name)})")
  val kparser = keyFmt.parse(colMap)
  val vparser = valFmt.parse(colMap)
}
case class GeneralIndexedTable(keys: Seq[Field], values: Seq[Field], order: Seq[String]) extends IndexedTable {
  import keyFmt.{Repr=>Key}
  import valFmt.{Repr=>Val}
  val hashTable = mutable.HashMap[Key,mutable.Set[Val]]()
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = {
    CrossStage(hashTable) { ht => 
      ir"""(ite: Iterator[String]) =>
        while (ite.hasNext) {
          val str = ite.next
          val arr = str.split(${Const(sep)})
          //println(">"+arr.toList)
          $ht.getOrElseUpdate(${kparser:IR[Key,{val arr:Array[String]}]},mutable.Set()) += ${vparser:IR[Val,{val arr:Array[String]}]}
        }
      """
    }
  }
}
case class UniqueIndexedTable(keys: Seq[Field], values: Seq[Field], order: Seq[String]) extends IndexedTable {
  import keyFmt.{Repr=>Key}
  import valFmt.{Repr=>Val}
  
  val hashTable = mutable.HashMap[Key,Val]()
  
  def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = {
    CrossStage(hashTable) { ht => 
      ir"""(ite: Iterator[String]) =>
        while (ite.hasNext) {
          val str = ite.next
          val arr = str.split(${Const(sep)})
          //println(">"+arr.toList)
          $ht += (${kparser:IR[Key,{val arr:Array[String]}]} -> ${vparser:IR[Val,{val arr:Array[String]}]})
        }
      """
    }
  }
  
  override def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = CrossStage(hashTable) { ht =>
    ir"""val it = $ht.iterator; loopWhile { it.hasNext && { val kv = it.next; $cont(kv._1->kv._2) } }"""
  }
  
}

case class ColumnStore(val cols: Seq[Field]) extends SimpleTable {
  val rowFmt: TupleFormat = TupleFormat(cols)
  val stores = cols.zipWithIndex map {case (c,i) => new SingleColumnTable(c,i)}
  def mkEntryLoader(sep: Char): CrossStage[String => Unit] =
    (stores.map(_.mkEntryLoader(sep)).fold(CrossStage(())(_ => ir"(str:String) => ()")) {
      case (acc, dl) =>
        for {
          a <- acc
          d <- dl
        } yield ir"(str:String) => {$a(str); $d(str)}"
    }) //alsoApply println
  
  override def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = {
    import Embedding.{hole,IR}
    import scala.collection.mutable.ArrayBuffer
    import ColumnStore.placeHolder
    
    val s = stores.map { case sct =>
      import sct.rowFmt.Repr
      CrossStage(sct.buffer){ buf => ir"$buf(idx?:Int)" }
    }.foldLeft((ls:List[Code[Any]]) => CrossStage()(rowFmt.mk(ls:_*))) {
      case (f, cs) => (ls:List[Code[Any]]) => cs.flatMap(c => f(c :: ls))
    }
    s(Nil) flatMap { tup =>
      CrossStage(stores.head.buffer.size) { len =>
        ir"var i = 0; loopWhile { val idx = i; i = idx+1; idx < $len && $cont(${tup:IR[rowFmt.Repr,{val idx:Int}]}) }"
      }
    }
    
  }
  
}
object ColumnStore {
  @transparencyPropagating def placeHolder[T](id: Int): T = ???
}


// TODO Table stored as hashmaps, compressed arrays of bits, etc

// TODO:
//class CompressedTable extends Table[Int] {
//}



/** Class used to associate values in the current stage with pieces of code that will make use of them when run, later on. 
  * We provide all the composition primitives necessary to build programs with these. */
abstract class CrossStage[A:IRType](val values: Seq[base.Val -> AnyRef]) { thisCS =>
  type Ctx
  val code: IR[A,Ctx]
  
  def map[B:IRType](f: Code[A] => Code[B]): CrossStage[B] = {
    new CrossStage[B](thisCS.values) {
      val code = f(thisCS.code).asClosedIR
    }
  }
  def flatMap[B:IRType](f: Code[A] => CrossStage[B]): CrossStage[B] = {
    val cs = f(code)
    new CrossStage[B](values ++ cs.values) {
      type Ctx = thisCS.Ctx with cs.Ctx
      val code = cs.code
    }
  }
  lazy val valuesAndNames = values.zipWithIndex.map { case (vx, i) => (vx, s"cs$i") }
  lazy val fmt = RowFormat(valuesAndNames.map { case (v -> _, n) => Field(n)(base.IRType(v.typ),null) })
  lazy val vals = fmt.runtimeReprOf(values.map(_._2):_*)
  lazy val body = valuesAndNames.foldLeft(code.rep) {
    case (newCode, (v -> x, n)) => base.letin(v, ir"field[${base.IRType(v.typ)}](${Const(n)})".rep, newCode, code.typ.rep)
  }
  lazy val mkFun: Code[fmt.Repr => A] = fmt.lift(base.IR(body),0)
  lazy val compile: () => A = {
    val f = fmt.lift(base.IR(body),0).asClosedIR.compile.asInstanceOf[Any => A]
    () => f(vals)
  }
  def run: A = mkFun.asClosedIR.run.apply(vals)
  override def toString: String = {
    val map = values.toMap
    val c = base.bottomUpPartial(code.rep) {
      case base.RepDef(bv: base.BoundVal) if map isDefinedAt bv => base.hole(s"cs${values.indexWhere(_._1 == bv)}", bv.typ)
    }
    s"${base.showRep(c)}\n\twhere: ${values.zipWithIndex map {case (kv,i) => s"cs${i} = ${CrossStage.showObject(kv._2)}; "} mkString}"
    // ^ Note: weirdly, when writing `${kv._2|>CrossStage.showObject}` above we get a strange, different result. 
  }
}
object CrossStage {
  def apply[R:IRType]()(code0: Code[R]): CrossStage[R] = new CrossStage[R](Nil) {
    val code = code0.asClosedIR
  }
  def apply[T0:IRType,R:IRType](value0: T0)(codeFun: Code[T0] => Code[R]): CrossStage[R] = {
    val v = base.bindVal("cs", typeRepOf[T0], Nil)
    new CrossStage[R](v -> value0.asInstanceOf[AnyRef] :: Nil) {
      val code = codeFun(base.IR(v |> base.readVal)).asClosedIR
    }
  }
  def apply[T0:IRType,T1:IRType,R:IRType](value0: T0, value1: T1)(code: (Code[T0],Code[T1]) => Code[R]): CrossStage[R] = ???
  def magic[A:IRType,B:IRType](f: Code[A] => CrossStage[B]): CrossStage[A => B] = {
    val cs = f(ir"a?:A") // FIXME: this probably has hygiene problems
    new CrossStage[A => B](cs.values)(irTypeOf[A=>B]) { // Q: why is it necessary to provide the implicit?! even `implicitly[IRType[A=>B]]` works!
      val code = ir"(a:A) => ${cs.code:IR[B,{val a:A}]}"
    }
  }
  def showObject(x:AnyRef) = s"${x.getClass.getName} @ 0x${System.identityHashCode(x).toLong.toHexString}"
}



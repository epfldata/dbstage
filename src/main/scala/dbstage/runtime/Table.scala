package dbstage
package runtime

import scala.collection.mutable
import scala.io._
import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep => Code, _}
import frontend._
import squid.lib.transparencyPropagating


abstract class FieldReifier {
  def apply(f: FieldRef): Code[f.T]
}
trait RowFormat { thisRow =>
  type Repr
  implicit val Repr: IRType[Repr]
  val columns: Seq[Field]
  //lazy val columnsByName: Map[String,Field] = columns.map(c => c.name -> c).toMap
  
  //lazy val columnsByRef: FieldRef => Option[Field] = fr => 
  //  //columnsByName get fr.name flatMap (_ optionIf (_.id == fr.id))
  //  //columnsByName get fr.name filter { c => fr.id forall (frid => Some(frid) == c.id) }
  //  columnsByName get fr.name filter { fr conformsTo _ }
  val parse: Map[String, Code[String]] => Code[Repr]
  //lazy val unparse: Code[Repr] => Code[String, Code[String]] = ??? 
  //def access[T](k: Seq[Field] => Code[T]) = {
  //  def rec(cols: Seq[Field], k2: Seq[Field] => Code[T]) = cols match {
  //    case Seq() => 
  //  }
  //}
  //protected def getField(f:FieldRef) = columns.find(_.name == f.name).fold(throw new Exception(s"No col ${f.name}")) { c => // TODO B/E
  protected def getField(f:FieldRef) = columns.find(f conformsTo _).fold(throw new Exception(s"No column '${f}' in $this")) { c => // TODO B/E
    assert(c.IRTypeT <:< f.IRTypeT)
    c
  }
  def get(repr:Embedding.Rep,f:FieldRef): Embedding.Rep // = ???
  //def lift2[T](k: Map[Field,Code]): Code[Repr => T] = {
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
      //q rewrite { case ir"fieldIn[$tp](${Const(name)},${Const(fuid)})" if fuid == uid =>  ??? }
      //q dbg_rewrite { case ir"${Field(f)}:$tp" => fr(f) } // FIXME: Error:(38, 9) not found: value ClassTag
      //q dbg_rewrite { case ir"${Field(f)}:$tp" => fr(f) }
      //q dbg_rewrite { case ir"${Field(f)}:$tp" => fr(f).asInstanceOf[Code[tp.Typ]] } // FIXME Error:scalac: missing or invalid dependency detected while loading class file 'Field.class'. Could not access type tp in value dbstage.runtime.RowFormat.$anonfun, because it (or its dependencies) are missing.
      val r = q rewrite { case ir"${FieldRef(f)}:Any" => fr(f) } // note: Any, unsound
      //println("Lifting: "+q)
      //println("Lifted : "+r)
      r
      //???
    }
  }
  /*
  def lift[T](q: Code[T], uid: Int): Code[Repr => T] = {
    //val bound = columns.map(c => c.name -> Embedding.IR[Any,Any](Embedding.bindVal(c.name,c.IRTypeT.rep,Nil)|>Embedding.readVal)).toMap
    val bound = columns.map(c => c.name -> Embedding.bindVal(c.name,c.IRTypeT.rep,Nil)).toMap
    def getName[T:IRType](name: String) = (bound get name).fold(throw new Exception(s"No col $name")) { b =>
      Embedding.IR[T,Any](b|>Embedding.readVal)
    }
    val q0 = q rewrite {
      //case ir"field[$t]($name,$uidopt)" =>
      //case ir"field[$tp](${Const(name)},Some(${Const(`uid`)}))" =>
      //case ir"field[$tp](${Const(name)},Some[Int](${Const(fuid)}))" if fuid == uid =>  // TODO support syntax: Const(`uid`)
      case ir"fieldIn[$tp](${Const(name)},${Const(fuid)})" if fuid == uid =>  // TODO support syntax: Const(`uid`)
        //columns.find(_.name == name).fold(throw new Exception("No col")) { c =>
        //  assert(c.IRTypeT <:< tp)
        //}
        //(bound get name).fold(throw new Exception(s"No col $name")) { b =>
        //  //b.asInstanceOf[Code[tp.Typ]]
        //  Embedding.IR[tp.Typ,Any](b|>Embedding.readVal)
        //}
        getName[tp.Typ](name)
      //case ir"field[$tp](${Const(name)},None)" =>
      case ir"field[$tp](${Const(name)})" =>
        getName[tp.Typ](name)
      //case ir"field[$tp]($n)" => // TODO also fieldIn
      //  println(tp,n,i)
      //  ??? // TODO B/E
    }
    println(q0)
    val q1 = columns.foldLeft(q0.rep) {
      case (acc, c) => Embedding.letin(bound(c.name), ir"???".rep, acc, q0.trep)
    }
    println(q1)
    ???
  }
  */
  def mkRefs: Code[Repr] = ???
  
  //def withId(id: Int): this.type // note: type not right
  def withId(id: Int): RowFormat{type Repr = thisRow.Repr}
  
  def runtimeReprOf(values: Any*): Repr = ???
  
  override def toString = s"Row[${Repr.rep}](${columns mkString ","})"
}
object RowFormat {
  def apply(cols: Seq[Field]): RowFormat = {
    val size = cols.size
    if (size == 0) lastWords("Table with no columns")
    if (size == 1) new SingleColumnFormat(cols.head)
    else if (size > 22) {
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
    //base.methodApp(repr,base.loadMtdSymbol(clsSym, "_" + (columns.indexWhere(_.name==c.name)+1), None),Nil,Nil,c.IRTypeT.rep)
    base.methodApp(repr,base.loadMtdSymbol(clsSym, "_" + (columns.indexWhere(c conformsTo _)+1), None),Nil,Nil,c.IRTypeT.rep)
  }
  //def mk(fields: Seq[Code[Any]])
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
//case class CompositeFormat(val lhs: RowFormat, val rhs: RowFormat) extends RowFormat {
case class CompositeFormat[L<:RowFormat,R<:RowFormat](val lhs: L, val rhs: R) extends RowFormat { // TODO make abstract class/trait?
  import lhs.{Repr=>LR}, rhs.{Repr=>RR}
  type Repr = (lhs.Repr, rhs.Repr)
  //val Repr = irTypeOf[(lhs.Repr,rhs.Repr)]
  lazy val Repr = irTypeOf[(LR,RR)]
  val columns = lhs.columns ++ rhs.columns
  val parse = (cs: Map[String, Code[String]]) => ir"(${lhs.parse(cs)}, ${rhs.parse(cs)})"
  def get(repr:Embedding.Rep,f:FieldRef): Embedding.Rep = {
    //val c = getField(f)
    //if (lhs.columns.exists(_.name==f.name)) {
    //  lhs.get(ir"${Embedding.IR[Repr,Any](repr)}._1".rep,f)
    //}
    //else rhs.get(ir"${Embedding.IR[Repr,Any](repr)}._1".rep,f)
    //println(s"Getting $f $lhs $rhs")
    //val r = 
    
    //if (lhs.columns.exists(_.name==f.name))
    //  lhs.get(ir"${Embedding.IR[Repr,Any](repr)}._1".rep,f)
    //else rhs.get(ir"${Embedding.IR[Repr,Any](repr)}._2".rep,f)
    
    if (lhs.columns.exists(f conformsTo _))
      lhs.get(ir"${Embedding.IR[Repr,Any](repr)}._1".rep,f)
    else {
      assert(rhs.columns.exists(f conformsTo _), s"Field ref $f is not in $this")
      rhs.get(ir"${Embedding.IR[Repr,Any](repr)}._2".rep,f)
    }
    
    //println(s"Getting $f $lhs $rhs -> $r"); r
  }
  def withId(id: Int) = CompositeFormat(lhs withId id, rhs withId id)
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
  //def scan: CrossStage[rowFmt.Repr] = ???
  //def scan(k: Seq[Field] => Code[Unit]): CrossStage[Unit] = ???
  //def scan(k: Code[rowFmt.Repr => Unit]): CrossStage[Unit] = ???
  def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = ???
  //def pull: CrossStage[(rowFmt.Repr => Unit) => Bool] = ???
  def pull: CrossStage[() => (rowFmt.Repr => Unit) => Bool] = ???
  //def pull2: CrossStage[(() => Bool, () => rowFmt.Repr)] = ???
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
  //val rowFmt: RowFormat = TupleFormat(cols)
  val rowFmt: RowFormat = RowFormat(cols)
  import rowFmt.{Repr => Val}
  val buffer = mutable.ArrayBuffer[Val]()
  private[this] val arr: Code[Array[String]] = ir"arr?:Array[String]"
  private[this] val colMap = cols.map(_.name).zipWithIndex.toMap.mapValues(i => ir"$arr(${Const(i+idxShift)})") // TODO factor with IndexedTable
  
  ////def loadData(data: Iterator[String], sep: Char = '|'): CrossStage[Unit] = ???
  //def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = CrossStage(buffer)(buf => 
  //  ir"(ite: Iterator[String]) => { val str = ite.next; val arr = str.split(${Const(sep)}); $buf += ${rowFmt.parse(colMap):IR[Val,{val arr:Array[String]}]}; () }")
  def mkEntryLoader(sep: Char): CrossStage[String => Unit] = CrossStage(buffer)(buf => 
    ir"(str: String) => { val arr = str.split(${Const(sep)}); $buf += ${rowFmt.parse(colMap):IR[Val,{val arr:Array[String]}]}; () }")
  
  override def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = CrossStage(buffer){ buf => ir"$buf.foreach($cont)" }
  
  //override def pull: CrossStage[(rowFmt.Repr => Unit) => Bool] = CrossStage(buffer){ buf => 
  //  ir"val it = $buf.iterator; println(it); (k:rowFmt.Repr => Unit) => if (it.hasNext) { k(it.next); true } else false" }
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
  val keyFmt: RowFormat = RowFormat(keys) // Note: using TupleFormat here may generate references to Tuple1... not sure how that's handled by Scala
  val valFmt: RowFormat = RowFormat(values)
  //val rowFmt: RowFormat = CompositeFormat(keyFmt,valFmt)
  //val rowFmt: CompositeFormat{val lhs:keyFmt.type;val rhs: valFmt.type} = CompositeFormat(keyFmt,valFmt)
  val rowFmt: CompositeFormat[keyFmt.type,valFmt.type] = CompositeFormat(keyFmt,valFmt)
  private[this] val arr: Code[Array[String]] = ir"arr?:Array[String]"
  private[this] val colMap = order.zipWithIndex.toMap.mapValues(name => ir"$arr(${Const(name)})")
  val kparser = keyFmt.parse(colMap)
  val vparser = valFmt.parse(colMap)
  //def 
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
  
  //def 
  
  //override def scan(k: Seq[Field] => Code[Unit]): CrossStage[Unit] = CrossStage(hashTable) { ht =>
  //  val inner = k(keys ++ values) // FIXME: Q: can have duplicates across the two?
  //  println(inner)
  //  ir"""$ht.foreach(println)"""
  //  //ir"""$ht.foreach(kv => )"""
  //}
  //override def scan(k: Code[rowFmt.Repr => Unit]): CrossStage[Unit] = CrossStage(hashTable) { ht =>
  //  ir"""$ht.foreach(kv => $k(kv._1->kv._2))"""
  //}
  override def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = CrossStage(hashTable) { ht =>
    ir"""val it = $ht.iterator; loopWhile { it.hasNext && { val kv = it.next; $cont(kv._1->kv._2) } }"""
  }
  
}

case class ColumnStore(val cols: Seq[Field]) extends SimpleTable {
  val rowFmt: TupleFormat = TupleFormat(cols)
  val stores = cols.zipWithIndex map {case (c,i) => new SingleColumnTable(c,i)}
  //def mkDataLoader(sep: Char): CrossStage[Iterator[String] => Unit] = stores.map(_.mkDataLoader(sep)).fold(CrossStage(())(_ => ir"(it:Iterator[String]) => ()")) {
  //  case (acc, dl) => acc flatMap (_ => dl)
  //}
  def mkEntryLoader(sep: Char): CrossStage[String => Unit] = (stores.map(_.mkEntryLoader(sep)).fold(CrossStage(())(_ => ir"(str:String) => ()")) {
    case (acc, dl) => //acc flatMap (a => ir"$a; $dl")
      //println(acc,dl)
      for {
        a <- acc
        d <- dl
      } yield ir"(str:String) => {$a(str); $d(str)}"
  }) //alsoApply println
  
  override def push(cont: Code[rowFmt.Repr => Bool]): CrossStage[Unit] = {
    import Embedding.{hole,IR}
    import scala.collection.mutable.ArrayBuffer
    import ColumnStore.placeHolder
    /*
    //println(ir"List[Any]()")
    val r = 
    stores.map{ sct => 
      import sct.rowFmt.Repr
      CrossStage(sct.buffer){buf=>ir"$buf(idx?:Int)"}
    }.foldLeft[CrossStage[List[Any]]](CrossStage(List[Any]())(_ => ir"List[Any]()")) {
      case (acc, dl) =>
        //println(acc,dl)
        //for {
        //  a <- acc
        //  val ir"List[Any]($xs*)" = a
        ////  d <- dl
        ////  xs2 = xs :+ d
        ////} yield ir"List($xs2*)"
        //} yield ???
        acc.flatMap {
          case ir"Nil" =>
            dl map { d => ir"List[Any]($d)" }
          case ir"List[Any]($xs*)" =>
            dl map { d =>
              val xs2 = xs :+ d
              ir"List($xs2*)"
            }
        }
      
    }
    println(r)
    */
    
    
    // FIXME
    
    //val s = stores.zipWithIndex.map { case (sct,i) =>
    val s = stores.map { case sct =>
      import sct.rowFmt.Repr
      //CrossStage(sct.buffer){ buf => ir"$buf(${Const(i)})" }
      CrossStage(sct.buffer){ buf => ir"$buf(idx?:Int)" }
    //}.reduce((lhs,rhs) => )
    }.foldLeft((ls:List[Code[Any]]) => CrossStage()(rowFmt.mk(ls:_*))) {
      case (f, cs) => (ls:List[Code[Any]]) => cs.flatMap(c => f(c :: ls))
    }
    //println(s)
    //println(s(Nil))
    s(Nil) flatMap { tup =>
      CrossStage(stores.head.buffer.size) { len =>
        ir"var i = 0; loopWhile { val idx = i; i = idx+1; idx < $len && $cont(${tup:IR[rowFmt.Repr,{val idx:Int}]}) }"
      }
    }
    
    //???
    
    /*
    val assoc -> getters = stores.zipWithIndex.map { case (sct,i) => 
      import sct.rowFmt.Repr
      //(i -> CrossStage.freshId) ->
      val fid = CrossStage.freshId
      (fid -> i) ->
      //ir"${IR[ArrayBuffer[Repr],Any](hole(s"BUFF#$i", typeRepOf[ArrayBuffer[Repr]]))}(idx? :Int)" : Code[Any]
      (ir"placeHolder[ArrayBuffer[Repr]](${Const(fid)})(idx? : Int)" : Code[Any]) //: IR[Any,{val idx:Int}] //
    }.unzip
    //val assocMap = assoc.toMap
    //val assocMap = assoc.map(_.swap).toMap
    //println(getters)
    val tuple: IR[rowFmt.Repr,{val idx:Int}] = rowFmt.mk(getters:_*)
    val r = CrossStage(stores.head.buffer.size) { len =>
      ir"var i = 0; loopWhile { val idx = i; i = idx+1; idx < $len && $cont($tuple) }"
    }
    //println(r)
    //println(assoc.toMap.mapValues(stores))
    //val cs = r flatMap (r => new CrossStage[Unit](assoc.toMap.mapValues(stores).mapValues(_.buffer), d => r rewrite {
    val cs = r flatMap (r => new CrossStage[Unit](assoc.toMap.mapValues(stores).mapValues(_.buffer), d => r rewrite {
      //case ir"placeHolder[ArrayBuffer[$t]](${Const(id)})" =>
      case ir"placeHolder[$t](${Const(id)})" =>
        //println(s"placeHolder[$t](${Const(id)})")
        //ir"$d(${Const(assocMap(id))}).asInstanceOf[$t]"
        ir"$d(${Const(id)}).asInstanceOf[$t]"
    }))
    //println(cs)
    //???
    cs
    */
    
  }
  
}
object ColumnStore {
  @transparencyPropagating def placeHolder[T](id: Int): T = ???
}



// TODO Table stored as hashmaps, compressed arrays of bits, etc

// TODO:
//class CompressedTable extends Table[Int] {
//}




//class CrossStage[A:IRType](val values: Seq[base.Val -> Any], val code: IR[A,Ctx]) {
//abstract class CrossStage[A:IRType](val values: Seq[base.Val -> Any]) { cs =>
abstract class CrossStage[A:IRType](val values: Seq[base.Val -> AnyRef]) { thisCS =>
  type Ctx
  val code: IR[A,Ctx]
  def map[B:IRType](f: Code[A] => Code[B]): CrossStage[B] = {
    new CrossStage[B](thisCS.values) {
      val code = f(thisCS.code)
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
  lazy val mkFun: Code[fmt.Repr => A] = {
    val valuesAndNames = values.zipWithIndex.map { case (vx, i) => (vx, s"cs$i") }
    val body = valuesAndNames.foldLeft(code.rep) {
      case (newCode, (v -> x, n)) => base.letin(v, ir"field[${base.IRType(v.typ)}](${Const(n)})".rep, newCode, code.typ.rep)
    }
    val vals = fmt.runtimeReprOf(values.map(_._2):_*)
    fmt.lift(base.IR(body),0)
  }
  lazy val compile: () => A = {
    //val curId = 0
    //val fmt = RowFormat(values.map { case v -> _ => Field(curId.toString alsoDo (curId += 1)) })
    //val fmt = RowFormat(values.zipWithIndex.map { case (v -> _, i) => FieldRef(i.toString)(base.IRType(v.typ)) })
    //val fmt = RowFormat(values.zipWithIndex.map { case (v -> _, i) => Field(i.toString)(base.IRType(v.typ),null) })
    val valuesAndNames = values.zipWithIndex.map { case (vx, i) => (vx, s"cs$i") }
    val fmt = RowFormat(valuesAndNames.map { case (v -> _, n) => Field(n)(base.IRType(v.typ),null) })
    //val p = base.bindVal("csp", fmt.Repr.rep, Nil)
    //val body = values.zipWithIndex.foldLeft(code.rep) {
    //  //case (newCode, (v -> x, i)) => base.letin(v, fmt.lift(ir"field[${base.IRType(v.typ)}](${Const(i.toString)})",0/*unused*/).rep, newCode, code.typ)
    //  case (newCode, (v -> x, i)) => base.letin(v, ir"field[${base.IRType(v.typ)}](${Const(i.toString)})".rep, newCode, code.typ.rep)
    //}
    val body = valuesAndNames.foldLeft(code.rep) {
      case (newCode, (v -> x, n)) => base.letin(v, ir"field[${base.IRType(v.typ)}](${Const(n)})".rep, newCode, code.typ.rep)
    }
    //IR[A,Any](body).
    //println(body)
    //println(fmt.runtimeReprOf(values.map(_._2):_*))
    val vals = fmt.runtimeReprOf(values.map(_._2):_*)
    val f = fmt.lift(base.IR(body),0).compile.asInstanceOf[Any => A]
    //val f = () => fun()
    //???
    () => f(vals)
  }
  //protected def mkFun: Code[A] = {
  //  ???
  //}
  //def run: A = mkFun.run
  def run: A = mkFun.run.apply(vals)
  //override def toString: String = "???"
  override def toString: String = {
    //s"${base.showRep(code.rep)}\n\twhere: ${values map (kv => s"d(${kv._1}) = ${kv._2|>CrossStage.showObject}; ") mkString}"
    val map = values.toMap
    val c = base.bottomUpPartial(code.rep) {
      case base.RepDef(bv: base.BoundVal) if map isDefinedAt bv => base.hole(s"cs${values.indexWhere(_._1 == bv)}", bv.typ)
    }
    //s"${base.showRep(c)}\n\twhere: ${values.zipWithIndex map {case (kv,i) => s"cs${i} = ${kv._2|>CrossStage.showObject}; "} mkString}"
    s"${base.showRep(c)}\n\twhere: ${values.zipWithIndex map {case (kv,i) => s"cs${i} = ${CrossStage.showObject(kv._2)}; "} mkString}"
  }
  //def showObject(x:AnyRef) = ???
}
object CrossStage {
  def apply[R:IRType]()(code0: Code[R]): CrossStage[R] = new CrossStage[R](Nil) {
    val code = code0
  }
  def apply[T0:IRType,R:IRType](value0: T0)(codeFun: Code[T0] => Code[R]): CrossStage[R] = {
    val v = base.bindVal("cs", typeRepOf[T0], Nil)
    new CrossStage[R](v -> value0.asInstanceOf[AnyRef] :: Nil) {
      val code = codeFun(base.IR(v |> base.readVal))
    }
  }
  def apply[T0:IRType,T1:IRType,R:IRType](value0: T0, value1: T1)(code: (Code[T0],Code[T1]) => Code[R]): CrossStage[R] = ???
  def magic[A:IRType,B:IRType](f: Code[A] => CrossStage[B]): CrossStage[A => B] = {
    val cs = f(ir"a?:A")
    //new CrossStage[A => B](cs.values)(dbg.implicitType[A=>B]) {
    new CrossStage[A => B](cs.values)(irTypeOf[A=>B]) { // FIXME: why is it necessary to provide the implicit?! even `implicitly[IRType[A=>B]]` works!
      val code = ir"(a:A) => ${cs.code:IR[B,{val a:A}]}"
    }
  }
  def showObject(x:AnyRef) = s"${x.getClass.getName} @ 0x${System.identityHashCode(x).toLong.toHexString}"
}
/*
import CrossStage.Dict
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
  def run = ir"$code(_)".run.apply(values)
  
  override def toString: String = {
    val c = code(ir"d?:Dict")
    s"${base.showRep(c.rep)}\n\twhere: ${values map (kv => s"d(${kv._1}) = ${kv._2|>showObject}; ") mkString}"
  }
  def showObject(x:AnyRef) = s"${x.getClass} @ 0x${System.identityHashCode(x).toLong.toHexString}"
  
}
object CrossStage { // TODO make these insert dict accesses BEFORE the code, not inside of it!
  type Dict = Map[Int,Any]
  private var curId = 0
  def freshId = curId alsoDo (curId += 1)
  def apply[T0:IRType,R:IRType](value0: T0)(code: Code[T0] => Code[R]): CrossStage[R] = {
    val id0 = freshId
    //new CrossStage(Map(id0 -> value0), ir"(d:Dict) => ${code}(d(${Const(id0)}).asInstanceOf[T0])")
    //new CrossStage(Map(id0 -> value0), (d:Code[Dict]) => code(ir"$d(${Const(id0)}).asInstanceOf[T0]"))
    new CrossStage(Map(id0 -> value0), (d:Code[Dict]) => ir"$code($d(${Const(id0)}).asInstanceOf[T0])")
  }
  def apply[T0:IRType,T1:IRType,R:IRType](value0: T0, value1: T1)(code: (Code[T0],Code[T1]) => Code[R]): CrossStage[R] = {
    val id0,id1 = freshId
    //val fun = (d:Code[Dict]) => code(ir"$d(${Const(id0)}).asInstanceOf[T0]", ir"$d(${Const(id1)}).asInstanceOf[T1]")
    new CrossStage(Map(id0 -> value0, id1 -> value1), (d:Code[Dict]) =>
      code(ir"$d(${Const(id0)}).asInstanceOf[T0]", ir"$d(${Const(id1)}).asInstanceOf[T1]") alsoDo ???) // FIXME not at the right place
      //ir"${code.curried}($d(${Const(id0)}).asInstanceOf[T0])($d(${Const(id1)}).asInstanceOf[T1])") // FIXME in Squid: allow curried HOF insertion
  }
  def magic[A:IRType,B:IRType](f: Code[A] => CrossStage[B]): CrossStage[A => B] = {
    //ir"(a:A) => $f(a)"
    val cs = f(ir"a?:A")
    //new CrossStage[B](cs.values, (a:Code[A]) => (cs.code(a):IR[B,{val a:A}]) subs 'a -> d)
    new CrossStage[A => B](cs.values, (d:Code[Dict]) => ir"(a:A) => ${cs.code(d):IR[B,{val a:A}]}")
  }
}
*/




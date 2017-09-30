package dbstage
package query

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._

//abstract class Operator[T] {
//  //def init(step: Code[T => Unit]): Code[Unit]
//  def init(step: Code[T => Unit]): CrossStage[Unit]
//}

//abstract class AdHocPoly[X] { def apply[T](t: Code[T]): Code[X] => Code[T] }
//object NeatClosure3 {
//  def close[X:IRType,R,S](f: Code[X] => Code[R])(k: (Code[R], AdHocPoly[X]) => S): S = {
//    import base._
//    val fv = freshBoundVal(typeRepOf[X])
//    val body = f(IR(base.readVal(fv)))
//    k(body, new AdHocPoly[X] { 
//      def apply[T](b:Code[T]) = x => 
//        IR(base.inline(fv,b.rep,x.rep))
//    })
//  }
//}

sealed trait QueryPlan {
  val uid = 42 // TODO rm
  val rowFormat: RowFormat = null
  type Row = rowFormat.Repr
  import rowFormat.Repr
  lazy val cost: Double = ???
  //val taggedColumns: Map[String,Field] //= null
  val taggedColumns: Seq[Field] //= null
  //def mkPrgm: CrossStage[Any] = ???
  //def mkPrgm: Operator[rowFormat.Repr] = ???
  //def mkPrgm: CrossStage[Any] = this match {
  //  case Scan(tbl) => 
  //    //tbl.scan(cols => ir"List(${cols.asInstanceOf[Seq[Field{type T=Any}]]}*).foreach(println)") alsoApply println
  //    //tbl.scan(cols => ir"List(${cols map (field2Code) asInstanceOf[Seq[Code[Any]]]}*).foreach(println)") alsoApply println
  //    //tbl.scan(cols => dbg_ir"List(${cols map (field2CodeOf[Any])}*).foreach(println)") alsoApply println // FIXME: this should work!
  //    //tbl.scan { cols => 
  //    //  val xs = cols map (field2CodeOf[Any])
  //    //  ir"List($xs*).foreach(println)" } alsoApply println
  //    ???
  //  case Filter(that, pred) =>
  //    println(that.rowFormat.lift(pred,uid))
  //    //println(that.rowFormat.lift2 { fr => })
  //    ???
  //  case GeneralJoin(lhs, rhs, pred) =>
  //    ???
  //}
  //def printLines
  def push(step: Code[Row => Bool]): CrossStage[Unit] = pushImpl(step) map (_ transformWith FinalizeCode)
  def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = ???
  
  //def pull: CrossStage[(Row => Unit) => Bool] = pullImpl map (_ transformWith FinalizeCode)
  //def pullImpl: CrossStage[(Row => Unit) => Bool] = ???
  def pull: CrossStage[() => (Row => Unit) => Bool] = pullImpl map (_ transformWith FinalizeCode)
  def pullImpl: CrossStage[() => (Row => Unit) => Bool] = ???
  
  def foreachCode(f: Code[Row => Unit]) = push(ir"(x:Row) => {$f(x); true}")
  //def foreach(f: Row => Unit) = (for {
  //  nextF <- CrossStage(f)(identity)
  //  fe <- foreachCode(nextF)
  //} yield fe).compile()
  //def foreach = ir"(f: Row => Unit) => ${
  //  (f: Code[Row => Unit]) => foreachCode(f) } yield fe).compile() }"
  //def foreach = NeatClosure3.close((f: Code[Row => Unit]) => )
  
  //lazy val foreach = (f: Row => Unit) => CrossStage.magic((f: Code[Row => Unit]) => foreachCode(f)).compile()(f)
  //lazy val foreach = CrossStage.magic((f: Code[Row => Unit]) => foreachCode(f)).compile()
  lazy val foreachLiftedCode: CrossStage[(Row => Unit) => Unit] = CrossStage.magic((f: Code[Row => Unit]) => foreachCode(f))
  lazy val foreach = foreachLiftedCode.compile()
  
  // TODO use aggregate of Unit
  //def foreachCode(f: Code[Row => Unit]) = foreachLiftedCode.map(fe => ir"$fe($f)").compile()
  def foreachLifted(f: Code[Row => Unit]) = foreachCode(f).compile()
  
  lazy val mkPull = pull.compile
  def iterator = new Iterator[Row] {
    val curPull = mkPull()()
    //var curElem: Option[Row] = null //Option.empty[Row]
    var curElem = Option.empty[Row]
    def hasNext: Boolean = {
      //if (curElem.isEmpty)
      //  curPull { e => curElem = Some(e) }
      loopWhile {
        curElem.isEmpty && curPull { e => curElem = Some(e) }
      }
      curElem.isDefined
    }
    def next(): Row = {
      assert(hasNext)
      val e = curElem.get
      curElem = None
      e
    }
  }
  
  def pull2: CrossStage[IteratorRep[Row]] = pullImpl2 map (_ transformWith FinalizeCode)
  def pullImpl2: CrossStage[IteratorRep[Row]] = ???
  lazy val mkPull2 = pull2.compile
  def iterator2 = new Iterator[Row] {
    val curPull = mkPull2()()
    def hasNext: Boolean = curPull._1()
    def next(): Row = curPull._2()
  }
  
  //def asIndexedOn(cols: Seq[FieldRef]) = Option.empty[IndexedQueryPlan]
  def asIndexedOn(cols: Set[FieldRef]) = Option.empty[IndexedQueryPlan]
  
  //def join(that: QueryPlan)(pred: Code[Bool]): QueryPlan = {
  //  //asIndexedOn()
  //  println(pred)
  //  //def isEqui(p: Code[Bool]) = 
  //  //isEqui(pred).getOrElse
  //  object Equi { // FIXME direction of equality doesn't actually matter! needs to partition keys // TODO generalize to multi-joins
  //    def unapply(x: Code[Bool]): Option[List[FieldRef -> FieldRef]] = x |>? {
  //      //case ir"(${Field(f0)}:$t0) == (${Field(f1)}:$t1)" =>
  //      case ir"(${Equi(lhs)}:Bool) && (${Equi(rhs)}:Bool)" => lhs ++ rhs
  //      case ir"(${FieldRef(f0)}:$t0) equals (${FieldRef(f1)}:$t1)" =>
  //        //println(f0)
  //        //???
  //        (f0 -> f1) :: Nil
  //    }
  //  }
  //  pred |> Equi.unapply map (equiJoin(that,_)) getOrElse ???
  //  //???
  //}
  //def equiJoin(that: QueryPlan, eqCols: Seq[FieldRef -> FieldRef]) = {
  //  val eqLhs -> eqRhs = eqCols.unzip
  //  asIndexedOn(eqLhs) -> that.asIndexedOn(eqRhs) match {
  //    case Some(indQP) -> _ =>
  //      ???
  //  }
  //}
  //def columnsByRef(fr: FieldRef): Option[FieldRef] = {
  def columnByRef(fr: FieldRef): Option[Field] = {
    //rowFormat.columnsByName.mapValues(_ withId uid) get fr.name filter { fr conformsTo _ }
    //taggedColumns get fr.name filter { fr conformsTo _ }
    //println(s"?? $taggedColumns")
    taggedColumns find { fr conformsTo _ }
    //???
  }
}
object QueryPlan {
  //def equiJoin(lhs: QueryPlan, rhs: QueryPlan, eqCols: Set[FieldRef]) = {
  //  val (lhsf,rhsf) = eqCols.partition { f =>
  //    //(lhs.rowFormat columnsByRef f) -> (rhs.rowFormat columnsByRef f) match {
  //    (lhs columnsByRef f) -> (rhs columnsByRef f) match {
  //      case Some(lf) -> Some(rf) => ???
  //      case Some(lf) -> None => true
  //      case None -> Some(rf) => false
  //      case None -> None => 
  //        println(f,lhs.rowFormat,rhs.rowFormat)
  //        ???
  //      case _ => die // otherwise: Warning:(106, 28) Exhaustivity analysis reached max recursion depth, not all missing cases are reported.
  //    }
  //  }
  def equiJoin(lhs: QueryPlan, rhs: QueryPlan, eqCols: Set[FieldRef -> FieldRef]) = {
    //println(s"Making equiJoin($lhs, $rhs, $eqCols)")
    val (lhsf,rhsf) = eqCols.iterator.map { case f0 -> f1 =>
      (lhs columnByRef f0, lhs columnByRef f1, rhs columnByRef f0, rhs columnByRef f1) match {
        //case (Some(lf), None, None, Some(rf)) => f0 -> f1
        //case (None, Some(lf), Some(rf), None) => f1 -> f0
        case (Some(lf), None, None, Some(rf)) => lf -> rf
        case (None, Some(lf), Some(rf), None) => lf -> rf
        case r => 
          println(f0,f1,r)
          ???
        //case _ => die // otherwise: Warning:(106, 28) Exhaustivity analysis reached max recursion depth, not all missing cases are reported.
      }
    }.toSeq.unzip
    //println(lhsf,rhsf)
    lhs.asIndexedOn(lhsf.toSet) -> rhs.asIndexedOn(rhsf.toSet) //alsoApply println
    match {
      case None -> None =>
        //HashJoin(lhs, rhs, ???, ???)
        HashJoin(lhs, rhs, lhsf, rhsf)
        // TODO the right thing if the keys of one of them are the primary keys
    }
    //???
  }
}

case class Scan(tbl: Table, fromId: Int) extends QueryPlan {
  //override val rowFormat: tbl.rowFmt.type = tbl.rowFmt
  override val rowFormat: tbl.rowFmt.type = tbl.rowFmt.withId(fromId).asInstanceOf[tbl.rowFmt.type] // note: type not right
  val taggedColumns = rowFormat.columns map (_ withId fromId) //map (c => c.name -> c) toMap
  /*
  //override def mkPrgm: CrossStage[Any] = CrossStage(tbl.scan)
  override def mkPrgm: Operator[rowFormat.Repr] = {
    //for { fe <- tbl.foreach } yield ir"()"
    new Operator[rowFormat.Repr] {
      def init(step: Code[rowFormat.Repr => Unit]): CrossStage[Unit] = {
        //tbl.scan(tbl.rowFormat.lift(pred,uid))
        tbl.scan(step)
      }
    }
    //???
  }
  */
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = tbl.push(step)
  //override def asIndexedOn(cols: Seq[FieldRef]) = tbl match {
  //  case tbl: IndexedTable if tbl.keys.size == cols.size && (tbl.keys,cols).zipped.forall(_.name == _.name) => // TODO better cond
  //    Some(new IndexedQueryPlan(this, tbl.keys))
  //    //???
  //  //case tbl: GeneralIndexedTable =>
  //  //  println(s"Nope ${tbl.keys} $cols")
  //  //  None
  //  case _ => 
  //    None
  //}
  //override def pullImpl: CrossStage[(Row => Unit) => Bool] = tbl.pull
  override def pullImpl: CrossStage[() => (Row => Unit) => Bool] = tbl.pull
  override def pullImpl2: CrossStage[IteratorRep[Row]] = tbl.pull2
  override def asIndexedOn(cols: Set[FieldRef]) = tbl match {
    //case tbl: IndexedTable if tbl.keys.size == cols.size && tbl.keys.toSet == cols =>
    case tbl: IndexedTable if tbl.keys.size == cols.size && tbl.keys.forall(c => cols.exists(_ conformsTo c)) =>
      Some(new IndexedQueryPlan(this, tbl.keys))
    //case tbl: IndexedTable =>
    //  println(tbl.keys, cols)
    //  ???
    case _ =>
      None
  }
}
case class Project[T](that: QueryPlan, cols: Seq[Field]) extends QueryPlan {
  //import that.rowFormat.Repr
  import that.rowFormat.{Repr => ThatRepr}
  import rowFormat.Repr
  //override val rowFormat = TupleFormat(cols).asInstanceOf[RowFormat.Tuple[T]]
  override val rowFormat = RowFormat(cols).asInstanceOf[RowFormat.Of[T]]
  
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = {
    //println(rowFormat.mkRefs)
    //that.push
    //???
    that.pushImpl(ir"(${that.rowFormat.lift(rowFormat.mkRefs,uid)}) andThen $step")
  }
  override def pullImpl: CrossStage[() => (Row => Unit) => Bool] =
    //that.pullImpl.map(p => ir"(k:Row => Unit) => $p(r => k(${that.rowFormat.lift(rowFormat.mkRefs,uid)}(r)))")
    that.pullImpl.map(p => ir"() => {val p = $p(); (k:Row => Unit) => p(r => k(${that.rowFormat.lift(rowFormat.mkRefs,uid)}(r)))}")
  override def pullImpl2: CrossStage[IteratorRep[Row]] =
    that.pullImpl2.map(p => ir"() => { val p = $p(); val hn = p._1; val ne = p._2; hn -> (() => ${that.rowFormat.lift(rowFormat.mkRefs,uid)}(ne())) }")
  
  //override val taggedColumns = that.taggedColumns filter (nf => cols.exists(_.name == nf._2.name)) // TODO better algo
  //override val taggedColumns = that.taggedColumns filter (nf => cols.exists(_.name == nf.name)) // TODO better algo // TODO check no name clashes...
  //override val taggedColumns = that.taggedColumns filter (nf => cols.exists(_ conformsTo nf)) // TODO better algo // TODO check no name clashes...
  override val taggedColumns = cols // TODO check no name clashes...
}
case class Filter(that: QueryPlan, pred: Code[Bool]) extends QueryPlan {
  override val rowFormat: that.rowFormat.type = that.rowFormat
  import rowFormat.Repr
  /*
  override def mkPrgm: Operator[rowFormat.Repr] = {
    new Operator[rowFormat.Repr] {
      def init(step: Code[rowFormat.Repr => Unit]): CrossStage[Unit] = {
        that.mkPrgm.init(step)
      }
    }
  }
  */
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] =
    //that.push(ir"(x:Row) => !${rowFormat.lift(pred,uid)}(x) || $step(x)")
    that.pushImpl(ir"(x:Row) => if (${rowFormat.lift(pred,uid)}(x)) $step(x) else true")
  
  // TODO
  override def pullImpl: CrossStage[() => (Row => Unit) => Bool] = 
    //that.pullImpl.map(p => ir"(k:Row => Unit) => while (!${rowFormat.lift(pred,uid)}(x)) $p")
    //that.pullImpl.map(p => ir"val p = $p; () => (k:Row => Unit) => loopWhile { val e = p(); !${rowFormat.lift(pred,uid)}(e) }")
    //
    //that.pullImpl.map(p => ir"val p = $p(); () => (k:Row => Unit) => loopWhile { p(e => !${rowFormat.lift(pred,uid)}(e)) }")
    that.pullImpl.map(p => ir"val p = $p(); () => (k:Row => Unit) => p(e => if (${rowFormat.lift(pred,uid)}(e)) k(e))")
  
  override def pullImpl2: CrossStage[IteratorRep[Row]] = that.pullImpl2.map(p =>
    //ir"() => { val p = $p(); (() => p._1(), () => p._2())}")
    // Using an Option variable:
    ir"""() => {
      val p = $p()
      //(() => p._1(), () => p._2())
      var cur = Option.empty[Row]
      val hn = () => {
        while (cur.isEmpty && p._1()) {
          val next = p._2()
          if (${rowFormat.lift(pred,uid)}(next)) cur = Some(next)
        }
        cur.isDefined
      }
      val ne = () => {
        val res = cur.get
        cur = None
        res
      }
      hn -> ne
    }""")
  
  //override def asIndexedOn(cols: Seq[FieldRef]) = that.asIndexedOn(cols)
  override def asIndexedOn(cols: Set[FieldRef]) = that.asIndexedOn(cols)
  override val taggedColumns = that.taggedColumns
}
abstract class JoinPlan(val lhs: QueryPlan, val rhs: QueryPlan) extends QueryPlan {
  override val rowFormat: CompositeFormat[lhs.rowFormat.type,rhs.rowFormat.type] = CompositeFormat(lhs.rowFormat, rhs.rowFormat)
}
case class NestedLoopJoin(override val lhs: QueryPlan, override val rhs: QueryPlan, pred: Code[Bool]) extends JoinPlan(lhs,rhs) {
  val taggedColumns = ???
}
//case class EquiJoin(lhs: QueryPlan, rhs: QueryPlan, eqCols: Seq[(Field,Field)]) extends QueryPlan {
//  val taggedColumns = ???
//}
case class IndexedJoin(lhs: IndexedTable, rhs: QueryPlan, rhsHash: Seq[Field]) extends QueryPlan {
  val taggedColumns = ???
}
//case class HashJoin(override val lhs: QueryPlan, override val rhs: QueryPlan, lhsHash: Seq[FieldRef], rhsHash: Seq[FieldRef]) extends JoinPlan(lhs,rhs) {
case class HashJoin(override val lhs: QueryPlan, override val rhs: QueryPlan, lhsHash: Seq[Field], rhsHash: Seq[Field]) extends JoinPlan(lhs,rhs) {
  val taggedColumns = lhs.taggedColumns ++ rhs.taggedColumns
  //val lhsTable = GeneralIndexedTable()
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = {
    val lkeys = RowFormat(lhsHash)
    val lvals: lhs.rowFormat.type = lhs.rowFormat
    //val rkeys = RowFormat(rhsHash)
    val rkeys = RowFormat(rhsHash).asInstanceOf[RowFormat{type Repr = lkeys.Repr}]
    val rvals: rhs.rowFormat.type = rhs.rowFormat
    //import lkeys.{Repr => LKeys}, lvals.{Repr => LVals}, rkeys.{Repr => RKeys}, rvals.{Repr => RVals}
    import lkeys.{Repr => LKeys}, lvals.{Repr => LVals}, /*rkeys.{Repr => RKeys},*/ rvals.{Repr => RVals}
    type RKeys = LKeys // no need for import rkeys.{Repr => RKeys} as it is the same as LKeys
    import Embedding.Quasicodes._
    import scala.collection.mutable.{HashMap,HashSet}
    //println(lhs.foreachLiftedCode)
    //println(s">>> ${lkeys.mkRefs}")
    val r = for {
      lfe <- lhs.foreachLiftedCode
      rfe <- rhs.foreachLiftedCode
    //} yield ir"""
    //    val hashTable = new scala.collection.mutable.HashMap[LKeys,LVals]()
    //    ${lfe}(r => hashTable += (${lvals.lift(lkeys.mkRefs,uid)}(r) -> r))
    //    ${rfe}(r => println(hashTable(${rvals.lift(rkeys.mkRefs,uid).asInstanceOf[Code[RKeys] => Code[LKeys]]}(r))))
    //  """
    } yield ir{
        //val hashTable = new HashMap[LKeys,HashSet[LVals]]()  // FIXME not yet handled by LogicFlowNormalizer
        val hashTable = HashMap[LKeys,HashSet[LVals]]()
        //${lfe}(r => hashTable += (${lvals.lift(lkeys.mkRefs,uid)}(r) -> r))
        ${lfe} { r =>
          val k = ${lvals.lift(lkeys.mkRefs,uid)}(r)
          //println(s"Adding $k -> $r");
          hashTable.getOrElseUpdate(k,HashSet()) += r 
        }
        //${rfe}(r => println(hashTable.get(${rvals.lift(rkeys.mkRefs,uid).asInstanceOf[Code[RVals => LKeys]]}(r))))
        ${rfe} { r => 
          //println(r)
          //hashTable.get(${rvals.lift(rkeys.mkRefs,uid).asInstanceOf[Code[RVals => LKeys]]}(r))
          hashTable.get(${rvals.lift(rkeys.mkRefs,uid)}(r))
            .foreach(ts => ts.foreach { t => 
              //println(s"Found $t -> $r"); 
              ${step}(t -> r) 
            })
        }
      }
    //println(r)
    //???
    r
  }
}
//object QueryPlan {
//  def join(lhs)
//}

case class Print(that: QueryPlan) extends QueryPlan {
  import that.rowFormat.Repr
  //override val rowFormat = new SingleColumnFormat(Field[String]("StringRep"))
  override val rowFormat = SingleColumnFormat[String]("StringRep")
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = {
    //val colsToString = rowFormat.columns
    //var colsToString = ir""""|""""
    var colsToString = Const("|")
    //for (c <- that.rowFormat.columns) colsToString = {
    //for (c <- that.taggedColumns.values.asInstanceOf[Iterable[Field]]) colsToString = {
    for (c <- that.taggedColumns) colsToString = {
      //println(s"Print $c ${c.toCode}")
      //ir"$colsToString + ${c.SerialT.unparse}($c)"
      import c._
      ir"""$colsToString + ${SerialT.unparse}($toCode) + "|""""
    }
    //println("CTS "+that.rowFormat)
    //println("CTS "+colsToString,that.rowFormat.lift(colsToString,uid))
    that.pushImpl(ir"(x:that.Row) => $step(${that.rowFormat.lift(colsToString,uid)}(x))")
  }
  //override val taggedColumns = Map[String,Field]() // FIXME? have a specially-generated tag here?
  override val taggedColumns = rowFormat.col :: Nil
}


class IndexedQueryPlan(that: QueryPlan, keys: Seq[Field]) extends QueryPlan {
  val taggedColumns = ???
}


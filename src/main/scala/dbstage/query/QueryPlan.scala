package dbstage
package query

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._


sealed trait QueryPlan {
  import rowFormat.Repr
  
  val uid = 42 // TODO rm
  val rowFormat: RowFormat
  type Row = rowFormat.Repr
  val taggedColumns: Seq[Field]
  lazy val cost: Double = ??? // TODO impl & use
  
  def push(step: Code[Row => Bool]): CrossStage[Unit] = pushImpl(step) map (_ transformWith FinalizeCode)
  def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = ???
  
  def pull: CrossStage[() => (Row => Unit) => Bool] = pullImpl map (_ transformWith FinalizeCode)
  def pullImpl: CrossStage[() => (Row => Unit) => Bool] = ??? // TODO impl everywhere
  
  def foreachCode(f: Code[Row => Unit]) = push(ir"(x:Row) => {$f(x); true}")
  
  lazy val foreachLiftedCode: CrossStage[(Row => Unit) => Unit] = CrossStage.magic((f: Code[Row => Unit]) => foreachCode(f))
  lazy val foreach = foreachLiftedCode.compile()
  
  // TODO use aggregate of Unit
  //def foreachCode(f: Code[Row => Unit]) = foreachLiftedCode.map(fe => ir"$fe($f)").compile()
  def foreachLifted(f: Code[Row => Unit]) = foreachCode(f).compile()
  
  lazy val mkPull = pull.compile
  def iterator = new Iterator[Row] {
    val curPull = mkPull()()
    var curElem = Option.empty[Row]
    def hasNext: Boolean = {
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
  
  def asIndexedOn(cols: Set[FieldRef]) = Option.empty[IndexedQueryPlan]
  
  def columnByRef(fr: FieldRef): Option[Field] = taggedColumns find { fr conformsTo _ }
  
}
object QueryPlan {
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
        HashJoin(lhs, rhs, lhsf, rhsf)
        // TODO the right thing if the keys of one of them are the primary keys
    }
  }
}

case class Scan(tbl: Table, fromId: Int) extends QueryPlan {
  override val rowFormat: tbl.rowFmt.type = tbl.rowFmt.withId(fromId).asInstanceOf[tbl.rowFmt.type] // note: type not right
  val taggedColumns = rowFormat.columns map (_ withId fromId) //map (c => c.name -> c) toMap
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = tbl.push(step)
  override def pullImpl: CrossStage[() => (Row => Unit) => Bool] = tbl.pull
  override def pullImpl2: CrossStage[IteratorRep[Row]] = tbl.pull2
  override def asIndexedOn(cols: Set[FieldRef]) = tbl match {
    case tbl: IndexedTable if tbl.keys.size == cols.size && tbl.keys.forall(c => cols.exists(_ conformsTo c)) =>
      Some(new IndexedQueryPlan(this, tbl.keys))
    case _ =>
      None
  }
}
case class Project[T] protected(that: QueryPlan, cols: Seq[Field]) extends QueryPlan {
  import that.rowFormat.{Repr => ThatRepr}
  import rowFormat.Repr
  
  override val rowFormat = RowFormat(cols).asInstanceOf[RowFormat.Of[T]]
  // ^ we make the assumption that the signatures of `select` match with the format selected by `RowFormat.apply`
  
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = {
    that.pushImpl(ir"(${that.rowFormat.lift(rowFormat.mkRefs,uid)}) andThen $step")
  }
  override def pullImpl: CrossStage[() => (Row => Unit) => Bool] =
    //that.pullImpl.map(p => ir"(k:Row => Unit) => $p(r => k(${that.rowFormat.lift(rowFormat.mkRefs,uid)}(r)))")
    that.pullImpl.map(p => ir"() => {val p = $p(); (k:Row => Unit) => p(r => k(${that.rowFormat.lift(rowFormat.mkRefs,uid)}(r)))}")
  override def pullImpl2: CrossStage[IteratorRep[Row]] =
    that.pullImpl2.map(p => ir"() => { val p = $p(); val hn = p._1; val ne = p._2; hn -> (() => ${that.rowFormat.lift(rowFormat.mkRefs,uid)}(ne())) }")
  
  override val taggedColumns = cols // TODO check no name clashes...
}
case class Filter(that: QueryPlan, pred: Code[Bool]) extends QueryPlan {
  override val rowFormat: that.rowFormat.type = that.rowFormat
  import rowFormat.Repr
  
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
    // Note: Using an Option variable that will be removed by the VarFlattening pass
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
  
  override def asIndexedOn(cols: Set[FieldRef]) = that.asIndexedOn(cols)
  override val taggedColumns = that.taggedColumns
}
abstract class JoinPlan(val lhs: QueryPlan, val rhs: QueryPlan) extends QueryPlan {
  override val rowFormat: CompositeFormat[lhs.rowFormat.type,rhs.rowFormat.type] = CompositeFormat(lhs.rowFormat, rhs.rowFormat)
}
case class NestedLoopJoin(override val lhs: QueryPlan, override val rhs: QueryPlan, pred: Code[Bool]) extends JoinPlan(lhs,rhs) {
  val taggedColumns = ???
}
case class IndexedJoin(lhs: IndexedTable, rhs: QueryPlan, rhsHash: Seq[Field]) extends QueryPlan {
  // TODO
  val rowFormat: RowFormat = ???
  val taggedColumns = ???
}
case class HashJoin(override val lhs: QueryPlan, override val rhs: QueryPlan, lhsHash: Seq[Field], rhsHash: Seq[Field]) extends JoinPlan(lhs,rhs) {
  val taggedColumns = lhs.taggedColumns ++ rhs.taggedColumns
  
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = {
    val lkeys = RowFormat(lhsHash)
    val lvals: lhs.rowFormat.type = lhs.rowFormat
    val rkeys = RowFormat(rhsHash).asInstanceOf[RowFormat{type Repr = lkeys.Repr}]
    val rvals: rhs.rowFormat.type = rhs.rowFormat
    import lkeys.{Repr => LKeys}, lvals.{Repr => LVals}, rvals.{Repr => RVals}
    type RKeys = LKeys // < ^ no need for import rkeys.{Repr => RKeys} as it is the same as LKeys
    import Embedding.Quasicodes._
    import scala.collection.mutable.{HashMap,HashSet}
    
    val r = for {
      lfe <- lhs.foreachLiftedCode
      rfe <- rhs.foreachLiftedCode
    } yield ir{
        //val hashTable = new HashMap[LKeys,HashSet[LVals]]()  // FIXME not yet handled by LogicFlowNormalizer
        val hashTable = HashMap[LKeys,HashSet[LVals]]()
        ${lfe} { r =>
          val k = ${lvals.lift(lkeys.mkRefs,uid)}(r)
          //println(s"Adding $k -> $r");
          hashTable.getOrElseUpdate(k,HashSet()) += r 
        }
        ${rfe} { r => 
          //println(r)
          hashTable.get(${rvals.lift(rkeys.mkRefs,uid)}(r))
            .foreach(ts => ts.foreach { t => 
              //println(s"Found $t -> $r"); 
              ${step}(t -> r) 
            })
        }
      }
    r
  }
  
}

case class Print(that: QueryPlan) extends QueryPlan {
  import that.rowFormat.Repr
  override val rowFormat = SingleColumnFormat[String]("StringRep")
  override def pushImpl(step: Code[Row => Bool]): CrossStage[Unit] = {
    val sep = Const("|")
    var colsToString = sep
    for (c <- that.taggedColumns) colsToString = {
      import c._
      ir"""$colsToString + ${SerialT.unparse}($toCode) + $sep"""
    }
    that.pushImpl(ir"(x:that.Row) => $step(${that.rowFormat.lift(colsToString,uid)}(x))")
  }
  override val taggedColumns = rowFormat.col :: Nil
}


class IndexedQueryPlan(that: QueryPlan, keys: Seq[Field]) extends QueryPlan {
  // TODO
  val rowFormat: RowFormat = ???
  val taggedColumns = ???
}


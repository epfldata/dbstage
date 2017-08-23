package dbstage
package query

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep => Code, _}

sealed trait Query {
  //val uid = Query.curId alsoDo (Query.curId += 1)
  //val uid: Int
  lazy val bestPlan: QueryPlan = ???
  
  def join(that: Query)(pred: Code[Bool]) = Join(this, that, pred)
  def where(pred: Code[Bool]) = Where(this, pred)
  
  //def selectStringRepr(): Select[String] = ToString(this)
  def selectStringRepr(): ToString = ToString(this)
  def select(c0: Field): Select[c0.T] = Select(this, c0 :: Nil)
  def select(c0: Field, c1: Field): Select[(c0.T,c1.T)] = Select(this, c0 :: c1 :: Nil)
  def select(c0: Field, c1: Field, c2: Field): Select[(c0.T,c1.T,c2.T)] = Select(this, c0 :: c1 :: c2 :: Nil)
  def select(c0: Field, c1: Field, c2: Field, c3: Field): Select[(c0.T,c1.T,c2.T,c3.T)] = Select(this, c0 :: c1 :: c2 :: c3 :: Nil)
  def select(c0: Field, c1: Field, c2: Field, c3: Field, c4: Field): Select[(c0.T,c1.T,c2.T,c3.T,c4.T)] = Select(this, c0 :: c1 :: c2 :: c3 :: c4 :: Nil)
  
  def plan: QueryPlan = this match {
    case f @ From(r) => Scan(r.table.value, f.uid)
    case Where(q,p) => Filter(q.plan, p)
    case Select(q,cs) => Project(q.plan, cs)
    case Join(lhs,rhs,p) =>
      //println(p |> EqualityPredicate.unapply)
      p |> EqualityPredicate.unapply map (QueryPlan.equiJoin(lhs.plan,rhs.plan,_)) getOrElse ???
      //???
  }
  
  def pushLines(consume: String => Unit) = {
    val fe = selectStringRepr().plan.foreach
    //val header = plan.rowFormat.columns.map(_.name).mkString("|","|","|")
    val header = plan.rowFormat.columns.map(f => f.name+f.id.fold("")("("+_.toString+")")).mkString("|","|","|")
    consume(header)
    consume(header.map { case '|' => '|' case _ => '-' })
    fe(consume)
  }
  def printLines = pushLines(println)
  //def printLines = {
  //  //println(rowFormat.columns.map(_.name).mkString("|","|","|"))
  //  //selectStringRepr.foreach(println)
  //  val fe = selectStringRepr().plan.foreach
  //  val header = plan.rowFormat.columns.map(_.name).mkString("|","|","|")
  //  println(header)
  //  //println(header.map(_ => '-'))
  //  println(header.map { case '|' => '|' case _ => '-' })
  //  fe(println)
  //}
  
}
object Query {
  private[query] var curId = 0
}
//case class From[R <: Relation](rel: R) extends Query with Dynamic {
//  def selectDynamic(fieldName: String) = ???
//}
//case class From[R <: Relation](rel: R) extends /*FieldForwarder(rel) with */Query {
case class From[R <: Relation](val rel: R) extends FieldForwarder[R](rel) with Query {
  val uid = Query.curId alsoDo (Query.curId += 1)
  override def wrapSelect[R](x: R): R = x |>=? {
    //case f: Field => (f in this).asInstanceOf[R]
    case f: Field => (f withId uid).asInstanceOf[R]
  }
  //override def toString: String = s"From($rel){${System.identityHashCode(this)}}"
  override def toString: String = s"From($rel){${uid}}"
}

case class Where(that: Query, pred: Code[Bool]) extends Query {
  //val uid = that.uid
}

case class Join(lhs: Query, rhs: Query, pred: Code[Bool]) extends Query {
  
}

case class Select[T](that: Query, cols: Seq[Field]) extends Query {
  //override def plan = super.plan.asInstanceOf[QueryPlan{val rowFormat: RowFormat{type Repr = T}}] // TODO better expressed?
  //override def plan = super.plan.asInstanceOf[QueryPlan{type Row = T; val rowFormat: RowFormat{type Repr = T}}] // TODO better expressed?
  override def plan = super.plan.asInstanceOf[Project[T]] // TODO better expressed?
  //override def plan = super.plan.asInstanceOf[QueryPlan{val rowFormat: RowFormat.Tuple[T]}] // TODO better expressed?
}

case class ToString(that: Query) extends Query {
  override def plan: Print = Print(that.plan)
}


object EqualityPredicate {
  //def unapply(x: Code[Bool]): Option[Set[FieldRef]] = x |>? {
  //  case ir"(${EqualityPredicate(lhs)}:Bool) && (${EqualityPredicate(rhs)}:Bool)" => lhs ++ rhs
  //  case ir"(${FieldRef(f0)}:$t0) equals (${FieldRef(f1)}:$t1)" =>
  //    Set(f0, f1)
  //}
  def unapply(x: Code[Bool]): Option[Set[FieldRef -> FieldRef]] = x |>? {
    case ir"(${EqualityPredicate(lhs)}:Bool) && (${EqualityPredicate(rhs)}:Bool)" => lhs ++ rhs
    case ir"(${FieldRef(f0)}:$t0) equals (${FieldRef(f1)}:$t1)" =>
      //(f0 -> f1) :: Nil
      Set(f0 -> f1)
  }
}


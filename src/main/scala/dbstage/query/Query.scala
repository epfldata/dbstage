package dbstage
package query

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep => Code, _}

sealed trait Query {
  val uid = Query.curId alsoDo (Query.curId += 1)
  lazy val bestPlan: QueryPlan = ???
  
  def join(that: Query)(pred: Code[Bool]) = Join(this, that, pred)
  def where(pred: Code[Bool]) = Where(this, pred)
  
  //def selectStringRepr(): Select[String] = ToString(this)
  def selectStringRepr(): ToString = ToString(this)
  def select(c0: Field): Select[c0.T] = Select(this, c0 :: Nil)
  def select(c0: Field, c1: Field): Select[(c0.T,c1.T)] = Select(this, c0 :: c1 :: Nil)
  //def select(c0: Field, c1: Field, c2: Field): TableQuery[(c0.T,c1.T,c2.T)] = ???
  
  def plan: QueryPlan = this match {
    case From(r) => Scan(r.table.value)
    case Where(q,p) => Filter(q.plan, p)
    case Select(q,cs) => Project(q.plan, cs)
  }
  
  def printLines = {
    //println(rowFormat.columns.map(_.name).mkString("|","|","|"))
    //selectStringRepr.foreach(println)
    val fe = selectStringRepr().plan.foreach
    val header = plan.rowFormat.columns.map(_.name).mkString("|","|","|")
    println(header)
    //println(header.map(_ => '-'))
    println(header.map{case'|'=>'|' case _ => '-'})
    fe(println)
  }
  
}
object Query {
  private var curId = 0
}
//case class From[R <: Relation](rel: R) extends Query with Dynamic {
//  def selectDynamic(fieldName: String) = ???
//}
//case class From[R <: Relation](rel: R) extends /*FieldForwarder(rel) with */Query {
case class From[R <: Relation](val rel: R) extends FieldForwarder[R](rel) with Query {
  override def wrapSelect[R](x: R): R = x |>=? {
    case f: Field => (f in this).asInstanceOf[R]
  }
  //override def toString: String = s"From($rel){${System.identityHashCode(this)}}"
  override def toString: String = s"From($rel){${uid}}"
}

case class Where(that: Query, pred: Code[Bool]) extends Query {
  
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

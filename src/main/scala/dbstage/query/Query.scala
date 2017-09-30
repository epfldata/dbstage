package dbstage
package query

import java.io.PrintStream

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._

sealed trait Query {
  //lazy val bestPlan: QueryPlan = ???
  
  def join(that: Query)(pred: Code[Bool]) = Join(this, that, pred)
  def where(pred: Code[Bool]) = Where(this, pred)
  
  def selectStringRepr(): ToString = ToString(this)
  def select(c0: Field): Select[c0.T] = Select(this, c0 :: Nil)
  def select(c0: Field, c1: Field): Select[(c0.T,c1.T)] = Select(this, c0 :: c1 :: Nil)
  def select(c0: Field, c1: Field, c2: Field): Select[(c0.T,c1.T,c2.T)] = Select(this, c0 :: c1 :: c2 :: Nil)
  def select(c0: Field, c1: Field, c2: Field, c3: Field): Select[(c0.T,c1.T,c2.T,c3.T)] = Select(this, c0 :: c1 :: c2 :: c3 :: Nil)
  def select(c0: Field, c1: Field, c2: Field, c3: Field, c4: Field): Select[(c0.T,c1.T,c2.T,c3.T,c4.T)] = Select(this, c0 :: c1 :: c2 :: c3 :: c4 :: Nil)
  // ...
  
  def plan: QueryPlan = this match {
    case f @ From(r) => Scan(r.table.value, f.uid)
    case Where(q,p) => Filter(q.plan, p)
    case Select(q,cs) => Project(q.plan, cs)
    case Join(lhs,rhs,p) =>
      p |> EqualityPredicate.unapply map (QueryPlan.equiJoin(lhs.plan,rhs.plan,_)) getOrElse ???
  }
  
  def pushLines(consume: String => Unit, pushHeader: Bool = true) = {
    val fe = selectStringRepr().plan.foreach
    if (pushHeader) {
      val header = plan.rowFormat.columns.map(f => f.name+f.id.fold("")("("+_.toString+")")).mkString("|","|","|")
      consume(header)
      consume(header.map { case '|' => '|' case _ => '-' })
    }
    fe(consume)
  }
  def printLines(out: PrintStream = System.out) = pushLines(out.println)
  def mkLines(sep: String = "\n"): String = {
    val sb = new StringBuilder
    pushLines(sb ++= _ alsoDo (sb ++= sep))
    sb.result
  }
  
}
object Query {
  private[query] var curId = 0
}

case class From[R <: Relation](val rel: R) extends FieldForwarder[R](rel) with Query {
  val uid = Query.curId alsoDo (Query.curId += 1)
  override def wrapSelect[R](x: R): R = x |>=? {
    case f: Field => (f withId uid).asInstanceOf[R]
  }
  override def toString: String = s"From($rel){${uid}}"
}

case class Where(that: Query, pred: Code[Bool]) extends Query {
  
}

case class Join(lhs: Query, rhs: Query, pred: Code[Bool]) extends Query {
  
}

case class Select[T](that: Query, cols: Seq[Field]) extends Query {
  override def plan = super.plan.asInstanceOf[Project[T]] // TODO better expressed?
}

case class ToString(that: Query) extends Query {
  override def plan: Print = Print(that.plan)
}


object EqualityPredicate {
  def unapply(x: Code[Bool]): Option[Set[FieldRef -> FieldRef]] = x |>? {
    case ir"(${EqualityPredicate(lhs)}:Bool) && (${EqualityPredicate(rhs)}:Bool)" => lhs ++ rhs
    case ir"(${FieldRef(f0)}:$t0) equals (${FieldRef(f1)}:$t1)" =>
      Set(f0 -> f1)
  }
}


package dbstage
package compiler

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode


abstract class QueryPlan[A:CodeType,-C] { // C: context of leaves
  type Ctx // Ctx: context of leaves
}

abstract class Scan[A:CodeType,C](src: Path[A,C]/*, pred: Code[A=>Bool,C]*/) extends QueryPlan[A,C] {
  val v: Variable[A]
  type Ctx = v.Ctx
  override def toString: String = s"Scan ${v|>showV} = ${src}"
}
object Scan {
  def apply[A:CodeType,C](src: Path[A,C], _v: Variable[A]) = new Scan[A,C](src) {
    val v: _v.type = _v
  }
}

abstract class Join[A:CodeType,B:CodeType,R:CodeType,C] extends QueryPlan[R,C] {
  val lhs: QueryPlan[A,C]
  val rhs: QueryPlan[B,C]
  type Ctx = lhs.Ctx & rhs.Ctx
  val pred: Code[Bool,Ctx]
  override def toString: String = s"Join ${blockIndentString(lhs.toString,"|X|")} ${blockIndentString(rhs.toString)}"
}
object Join {
  def apply[A:CodeType,B:CodeType,R:CodeType,C](_lhs: QueryPlan[A,C],_rhs: QueryPlan[B,C])(_pred: Code[Bool,_lhs.Ctx & _rhs.Ctx]) =
    new Join[A,B,R,C] {
      val lhs: _lhs.type = _lhs
      val rhs: _rhs.type = _rhs
      val pred: Code[Bool,Ctx] = _pred
    }
}

abstract case class Reduction[A:CodeType,R:CodeType,C](src: QueryPlan[A,C], mon: StagedMonoid[R,C]) extends QueryPlan[R,C] {
  type Ctx = src.Ctx
  //val pred: Code[Bool,Ctx]
  val pred: QueryRepr[Bool,Ctx] // TODO unnest
  //val expr: Code[R,Ctx]
  val expr: QueryPlan[R,Ctx] // TODO unnest
  //override def toString: String = s"Reduce on ${blockIndentString(expr.toString,"if")} ${blockIndentString(pred.toString,"from")} ${blockIndentString(src.toString)}"
  override def toString: String = s"Reduce on ${blockIndentString(expr.toString)}\nif ${blockIndentString(pred.toString)}\nfrom ${blockIndentString(src.toString)}"
}
object Reduction {
  //def apply[A:CodeType,R:CodeType,C](_src: QueryPlan[A,C], mon: StagedMonoid[R,C])(_pred: Code[Bool,_src.Ctx], _expr: Code[R,_src.Ctx]) =
  def apply[A:CodeType,R:CodeType,C](_src: QueryPlan[A,C], mon: StagedMonoid[R,C])(_pred: QueryRepr[Bool,_src.Ctx], _expr: QueryPlan[R,_src.Ctx]) =
    new Reduction[A,R,C](_src,mon) {
      override val src: _src.type = _src
      //type Ctx = src.Ctx
      val pred/*: Code[Bool,Ctx]*/ = _pred
      val expr/*: Code[R,Ctx]*/ = _expr
    }
}

case class PostProcessed[A:CodeType,R:CodeType,C](src: QueryPlan[A,C], f: Code[A=>R,C]) extends QueryPlan[R,C] {
  override def toString: String = s"process ${blockIndentString(src.toString,"then apply")} ${f|>showC}"
}

abstract class SequencedPlans[A:CodeType,B:CodeType,C] extends QueryPlan[A,C] {
  def B = codeTypeOf[B]
  val v: Variable[B]
  val nestedPlan: QueryPlan[B,C]
  val body: QueryPlan[A,C&v.Ctx]
  override def toString: String = s"let ${v.rep|>base.showRep} = ${nestedPlan.toString|>blockIndentString}\nin ${body}"
}
object SequencedPlans {
  // TODO let-commuting
  def apply[A:CodeType,B:CodeType,C](_v: Variable[B], _nestedPlan: QueryPlan[B,C])(_body: QueryPlan[A,C&_v.Ctx]): QueryPlan[A,C] = 
    new SequencedPlans[A,B,C] {
      val v: _v.type = _v
      val nestedPlan: QueryPlan[B,C] = _nestedPlan
      val body: QueryPlan[A,C&v.Ctx] = _body
    }
}


case class PlainCode[A:CodeType,C](cde: Code[A,C]) extends QueryPlan[A,C] {
  override def toString: String = cde.toString //showC(cde)
}





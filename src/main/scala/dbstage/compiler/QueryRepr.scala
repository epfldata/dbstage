package dbstage
package compiler

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

sealed abstract class QueryRepr[A:CodeType,C] {
  //val cde: Code[A,C]
}
case class UnliftedQuery[A:CodeType,C](cde: Code[A,C]) extends QueryRepr[A,C]

abstract class LiftedQuery[A:CodeType,C]/*(val cde: Code[A,C])*/ extends QueryRepr[A,C]

//case class NestedQuery[A:CodeType,B:CodeType,C](body: HollowedCode[A,B,C], nestedQuery: LiftedQuery[B,C]) extends LiftedQuery[A,C] {
//  override def toString: String = s"let ${body.v.rep|>base.showRep} = $nestedQuery\nin ${body.body}"
//}
abstract class NestedQuery[A:CodeType,B:CodeType,C] extends LiftedQuery[A,C] {
  //val body: HollowedCode[A,B,C]
  val v: Variable[B]
  val nestedQuery: LiftedQuery[B,C]
  val body: QueryRepr[A,C&v.Ctx]
  override def toString: String = s"let ${v.rep|>base.showRep} = $nestedQuery\nin ${body}"
}
object NestedQuery {
  def apply[A:CodeType,B:CodeType,C](_v: Variable[B], _nestedQuery: LiftedQuery[B,C])(_body: => QueryRepr[A,C&_v.Ctx]): NestedQuery[A,B,C] =
    new NestedQuery[A,B,C] {
      val v: _v.type = _v
      val nestedQuery: LiftedQuery[B,C] = _nestedQuery
      lazy val body: QueryRepr[A,C&v.Ctx] = _body
    }
}

case class MonoidEmpty[A:CodeType,C](empty: Code[A,C]) extends LiftedQuery[A,C]


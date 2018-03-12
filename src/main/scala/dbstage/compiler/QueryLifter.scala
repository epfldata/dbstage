package dbstage
package compiler

import query._
import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

/*

depending on purity of nested query, accept or not extraction under uncertain eval context

*/

class QueryLifter {
  
  def apply[A:CodeType,C](q: Code[A,C]): Option[LiftedQuery[A,C]] = liftQuery(q) match {
    case lq: LiftedQuery[A,C] => Some(lq)
    case uq: UnliftedQuery[A,C] => None
  }
  
  //def liftQuery[T:CodeType,C](q: Code[T,C]): LiftedQuery[T,C] = println(s"\n<<-- Rec ${q|>showC}\n") thenReturn (q match {
  //  case _ => die
  //})
  def liftQuery[T:CodeType,C](q: Code[T,C]): QueryRepr[T,C] = {
    println(s"\n<<-- Rec ${q|>showC}\n")
    //object Ins extends Embedding.Inspector[T,C,NestedQuery[T,_,C]] {
    object Ins extends Embedding.Inspector[T,C,LiftedQuery[T,C]] {
      def traverse[S:CodeType](mkHollowed: => HollowedCode[T,S,C]): PartialFunction[Code[S,C], LiftedQuery[T,C]] = {
        case cde @ code"readInt" => 
          //NestedQuery(mkHollowed,MonoidEmpty(code"$cde:S"))
          val h = mkHollowed
          NestedQuery(h.v,MonoidEmpty(code"$cde:S"))(liftQuery(h.body))
      }
    }
    Ins(q) match {
      case Left(cde) => UnliftedQuery(cde)
      case Right(lq) =>
        //println(lq)
        //die
        lq
    }
  }
  
}
object QueryLifter extends QueryLifter

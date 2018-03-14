package dbstage
package compiler

import query.{RecordSyntax=>_,_}
import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

import scala.language.higherKinds

class QueryPlanner {
  
  def apply[A:CodeType,C](q: QueryRepr[A,C]): QueryPlan[A,C] = {
    
    q match {
        
      case c: Comprehension[A,C] =>
        planComprehension(c)
        
      case n: NestedQuery[A,b,C] =>
        implicit val b = n.B
        n.body match {
          case UnliftedQuery(cde) =>
            val v: n.v.type = n.v
            PostProcessed(apply(n.nestedQuery), code"($v:b) => ${cde}")
          case _ =>
            SequencedPlans(n.v,apply(n.nestedQuery))(apply(n.body))
        }
        
      case MonoidMerge(mon,lhs,rhs) =>
        val v0 = new Variable[A]
        val v1 = new Variable[A]
        SequencedPlans(v0,apply(lhs))(
          SequencedPlans[A,A,C&v0.Ctx](v1,apply(rhs))(PlainCode(code"${mon.combine}($v0,$v1)")))
        
      case UnliftedQuery(cde) => PlainCode(cde)
        
      case _ =>
        ??? // TODO
    }
    
  }
  
  def planComprehension[R:CodeType,C](q: Comprehension[R,C]): QueryPlan[R,C] = {
    q.productions match { // Note that `q.productions` has type `IterationBase[R,C]` and thus cannot be `Yield`
      case ite: Iteration[a,R,C] =>
        implicit val a = ite.A
        val scan = Scan(ite.src, ite.v)
        planComprehensionRec[a,R,C](q.mon,scan)(ite.body.asInstanceOf[Productions[R,ite.v.Ctx]])
    }
  }
  
  def planComprehensionRec[A:CodeType,R:CodeType,C](mon: StagedMonoid[R,C], E: QueryPlan[A,C])(prods: Productions[R,E.Ctx]): QueryPlan[R,C] = {
    prods match {
      case ite: Iteration[a,R,E.Ctx] =>
        implicit val a = ite.A
        val src = ite.src.asInstanceOf[Path[a,C]] // TODO runtime check+decision here
        val F = Join[A,a,R,C](E,Scan(src,ite.v))(code"true") // FIXME pred
        planComprehensionRec[R,R,C](mon,F)(ite.body)
      case Yield(p,c) => Reduction(E,mon)(p,apply(c))
    }
  }
  
}
object QueryPlanner extends QueryPlanner

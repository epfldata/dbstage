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

import scala.language.higherKinds

class QueryPlanner {
  
  def apply[A:CodeType,C](q: QueryRepr[A,C]): QueryPlan[A,C] = {
    
    q match {
      case c: Comprehension[A,C] =>
        planComprehension(c)
      case _ =>
        ??? // TODO
    }
    
  }
  
  def planComprehension[R:CodeType,C](q: Comprehension[R,C]): QueryPlan[R,C] = {
    
    q.productions match { // Note that `q.productions` has type `IterationBase[R,C]` and thus cannot be `Yield`
      case ite: Iteration[a,R,C] =>
        implicit val a = ite.A
        val base = Select(Scan(ite.src), ite.vPred)
        planComprehensionRec(q.mon, ite.v)(ite.body)(base)
    }
    
  }
  
  def planComprehensionRec[A:CodeType,R:CodeType,C](mon: StagedMonoid[R,C], v: Variable[A])(prods: Productions[R,C&v.Ctx])(E: QueryPlan[A,C]): QueryPlan[R,C] = {
    prods match {
      case ite: Iteration[a,R,C&v.Ctx] =>
        //planComprehensionRec(mon,ite.v)(ite.body)(Unnest(E, ite.vPred))
        ???
      case Yield(p,c) =>
        // FIXME use p
        Reduction(E,code"(a:A)=>true",mon)
        // code"(x:Any)=>true"
    }
  }
  
  
}
object QueryPlanner extends QueryPlanner

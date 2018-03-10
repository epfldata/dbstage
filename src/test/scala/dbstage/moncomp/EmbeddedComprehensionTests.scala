package dbstage
package moncomp

import org.scalatest.FunSuite
import squid.utils._
import cats.Monoid
import cats.implicits.{catsKernelStdOrderForInt=>_,_}
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import NonEmpty.{Ops,tryWeaken}
import Embedding.Predef._
import Embedding.Quasicodes._

class EmbeddedComprehensionTests extends FunSuite {
  
  test("basics") {
    
    val xs: List[Int] = tryWeaken(list(1,2,3,4))
    //val q = Staged{  }
    //QueryLifter.liftQuery(q.embedded(Embedding))
    QueryLifter.liftQuery(code{abs(
      for { x <- OrderedOps(xs) } yield set(x).orderingBy[Int]
    )})
    
    
  }
  
}

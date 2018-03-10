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

object EmbeddedComprehensionTests {
  
  val xs: List[Int] = tryWeaken(list(1,2,3,4))
  val ys: List[Int] = tryWeaken(list(0,5,10))
  
}
import EmbeddedComprehensionTests._
class EmbeddedComprehensionTests extends FunSuite {
  
  test("basics") {
    
    //val q = Staged{  }
    //QueryLifter.liftQuery(q.embedded(Embedding))
    QueryLifter.liftQuery(code{abs(
      //for { x <- xs } yield set(x).orderingBy[Int]
      for { x <- xs; y <- ys } yield list((x,y))
    )})
    
  }
  
  test("ordering") {
    QueryLifter.liftQuery(code{abs(
      for { x <- xs } yield set(x).orderingBy[Int]
    )})
  }
  
}

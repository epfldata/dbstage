package dbstage
package query

import squid.utils._
import dbstage.compiler._
import org.scalatest.FunSuite
import cats.Monoid
import cats.implicits._
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import Embedding.Quasicodes._

class QueryLiftingTests extends FunSuite {
  import QueryFrontendTests._
  
  // TODO port tests from previous redesign
  
  test("Basics") {
    
    QueryLifter.liftQuery(code{abs(
      for { x <- nel0; y <- nel1 } yield x+y+readInt
    )}) alsoApply println
    
  }
  
  
  
}

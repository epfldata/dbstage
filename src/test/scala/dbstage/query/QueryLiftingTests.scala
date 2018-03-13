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
  
  test("If-then-else Source") {
    
    QueryLifter.liftQuery(code{abs(
      for { x <- nel0; y <- if (x > 0) nel1 else nel0 } yield x+y+readInt
    )}) alsoApply println
    
  }
  
  
  test("Intro Example 1") {
    
    
    
  }
  
  
  import example.paper._
  import example.paper.Data._
  import example.paper.Examples._
  Embedding embed Examples
  
  test("Intro Example 2") {
    
    // FIXME
    QueryLifter.liftQuery(code{
      ex2(depts,emps)
    }) alsoApply println
    
  }
  
  
  
  
  test("N9") {
    QueryLifter.liftQuery(code{
      for { x <- xs; if (for { y <- ys } yield ExistsAny(y > 0)) } yield ListOf(x)
    }) alsoApply println
  }
  
  
}

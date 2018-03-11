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
  
  
  test("N4") {
    QueryLifter.liftQuery(code{abs(
      for { x <- xs; xgt0 <- (if (x > 0) list(x) else list(0)); y <- ys } yield list(xgt0)
    )})
  }
  
  test("N9") {
    QueryLifter.liftQuery(code{abs(
      //for { y <- ys } yield Any(y > 0)
      for { x <- xs; if (for { y <- ys } yield Any(y > 0)).value } yield list(x)
    )})
    //println(Monoid[Bool].empty)
  }
  
  test("N10") {
    QueryLifter.liftQuery(code{abs(
      //for { x <- xs } yield for { y <- ys } yield list(x+y) // worked without any special handling
      for { x <- xs } yield for { y <- ys } yield list(x+y).orderingBy[Int]
    )})
  }
  
  test("unnest join sources") {
    
    // TODO
    
  }
  
  
  // it's not really possible to unnest while remaining in monoid comprehension form; but these transformations can be
  // used while searching for efficient plans based on a cost heuristic
  
  test("Query A") {
    // for { d <- depts } yield d ~ (for { e <- emps; if e.dno == d.dno } yield e)
    // -->
    // for { d <- depts; e <- emps } yield Bag(e).filter(e.dno == d.dno).groupBy(d)  // in this case no post-projection needed
    // -- or notice the condition is an eq, and yield:
    // val empsByDno = for { e <- emps } yield Bag(e).groupBy(e.dno)
    // for { d <- depts } yield d ~ empsByDno.getOrElse(d.dno, Set.empty)
    
    
  }
  
  test("Query B") {
    // for { a <- as } yield All(for { b <- bs } yield Any(b == a))
    // -->
    // for { (a,cond) <- (for { a <- as; b <- bs } yield Set(b).filter(a == b).groupBy(a)) } yield All(cond)
    
    
  }
  
  test("Query C") {
    // for { e <- emps } yield e ~ (for { c <- e.children; if (for { d <- e.manager.children } yield All(c.age > d.age)) } yield count())
    // ie
    // for { e <- emps } yield e ~ (for { c <- e.children } yield count(for { d <- e.manager.children } yield All(c.age > d.age)))
    // -->
    // val join = for { e <- emps; c <- e.children; d <- e.manager.children } yield All(c.age > d.age).groupBy(e~c)
    // for { (true,e~c) <- join } yield count().groupBy(e)
    
    
  }
  
  test("Query D") {
    // for { s <- students
    //       if All(for { c <- courses; if c.title=="DB" } yield Any(for { t <- transcripts; if t.id==s.id && t.cno==c.cno } yield true))
    //       // ie:
    //       if All(for { c <- courses; if c.title=="DB" } yield Any(for { t <- transcripts } yield t.id==s.id && t.cno==c.cno))
    // } yield s
    // -->
    // val join = for { s <- students; c <- courses; if c.title=="DB"; t <- transcripts } yield Any(t.id==s.id && t.cno==c.cno).groupBy(s~c)
    // for { (taken,s~c) <- join } yield All(taken).groupBy(s).project[Student]
    
    
  }
  
  
  
  
}

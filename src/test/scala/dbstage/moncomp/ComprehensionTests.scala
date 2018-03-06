package dbstage
package moncomp

import org.scalatest.FunSuite
import squid.utils._
import cats.Monoid
import cats.implicits._

class ComprehensionTests extends FunSuite {
  
  test("valid and invalid") {
    
    ((xs: Set[Int]) => for { x <- xs } yield set(x))
    
    ((xs: Bag[Int]) => for { x <- xs } yield set(x))
    
    ((xs: Bag[Int]) => for { x <- xs } yield bag(x))
    
    ((xs: List[Int]) => for { x <- xs } yield bag(x))
    
    ((xs: Bag[Int], ys: Bag[Int]) => (for { x <- xs; y <- ys } yield bag(x,y)) : Bag[Int])
    
    ((xs: List[Int], ys: Bag[Int]) => (for { x <- xs; y <- ys } yield bag(x,y)) : Bag[Int])
    ((xs: Bag[Int], ys: List[Int]) => (for { x <- xs; y <- ys } yield bag(x,y)) : Bag[Int])
    
    //Debug show
    //((xs: Set[Int]) => for { x <- xs } yield bag(x))
    
    assertDoesNotCompile("(xs: Set[Int]) => for { x <- xs } yield bag(x)")
    // could not find implicit value for parameter M: dbstage.moncomp.CommutativeIdempotentMonoid[M]
    
    assertDoesNotCompile("(xs: Bag[Int]) => for { x <- xs } yield list(x)")
    // could not find implicit value for parameter M: cats.kernel.CommutativeMonoid[M]
    
    assertDoesNotCompile("((xs: List[Int], ys: Bag[Int]) => for { x <- xs; y <- ys } yield list(x,y))")
    assertDoesNotCompile("((xs: Bag[Int], ys: List[Int]) => for { x <- xs; y <- ys } yield list(x,y))")
    
  }
  
  test("aggregates invalid") {
    
    //implicitly[CommutativeIdempotentSemigroup[Min[Int]]]
    //implicitly[CommutativeIdempotentMonoid[Indexing[Int,Min[Int]]]]
    //implicitly[CommutativeIdempotentSemigroup[NonEmpty[Set[Int]]]]
    //implicitly[CommutativeIdempotentMonoid[Int Indexing NonEmpty[Set[Int]]]]
    //implicitly[(Min[Int] ~ Max[Int]) IntoMonoid (Option[Min[Int]] ~ Option[Max[Int]])]
    
    ((xs: Set[Int]) => (for { x <- xs } yield min(x)) : Option[Min[Int]])
    
    ((xs: Set[Int]) => (for { x <- xs } yield min(x) groupingBy (x % 2)) : Int Indexing Min[Int])
    
    ((xs: Set[Int]) => (for { x <- xs } yield set(x) groupingBy (x % 2)) : Int Indexing NonEmpty[Set[Int]])
    
    ((xs: Set[Int]) => (for { x <- xs } yield avg(countIf(x > 0).value.toDouble) groupingBy (x % 2)) : Int Indexing Avg[Double])
    
    ((xs: Set[Int], ys: Set[Int]) => (for { x <- xs; y <- ys } yield set((x,y)) groupingBy (x % y)) : Int Indexing NonEmpty[Set[(Int,Int)]])
    
    //((xs: Set[Int]) => (for { x <- xs } yield min(x) ~ max(x)) : Option[Min[Int]] ~ Option[Max[Int]])
    /* ^ now the implicit for this is low-prio */
    ((xs: Set[Int]) => (for { x <- xs } yield min(x) ~ max(x)) : Option[Min[Int] ~ Max[Int]])
    /* ^ better, because does not capture impossible values like None ~ Some(_) */
    
    
    
  }
  
  test("streaming") {
    
    val strm = Streamed.from(0)
    
    var count = 0
    
    val res = for {
      x <- strm
      _ <- count += 1  // TODO () <-
    } yield stream(x + 1)
    
    println(count)
    println(res.as.iterator.take(3).toList)
    println(count)
    
    
    assertDoesNotCompile("for { x <- strm } yield max(x)") // could not find implicit value for parameter M: dbstage.moncomp.IncrementalMonoid[M]
    
    
    
  }
  
  
  
}

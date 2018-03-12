package dbstage
package query

import org.scalatest.FunSuite
import squid.utils._
import cats.Monoid
import cats.implicits._
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup

object QueryFrontendTests {
  
  val nel0 = ListOf(1,2,3)
  val nel1 = ListOf(0,4,8,12)
  val l0: ListOf[Int] = nel0
  val l1: ListOf[Int] = nel1
  
}
class QueryFrontendTests extends FunSuite {
  import QueryFrontendTests._
  
  // TODO port tests from previous redesign
  
  test("Translucent Types") {
    
    val n: NonZero[Nat] = mkNonZero(mkNat(1))
    val m = n |+| n
    assert((m: NonZero[Nat]) == 2)
    
    implicitly[Monoid[Nat]]
    implicitly[CommutativeSemigroup[NonZero[Nat]]]
    
    assertDoesNotCompile("implicitly[Monoid[NonZero[Nat]]]") // could not find implicit value  -- NonZero and Nat's Wraps instances are private
    
  }
  
  def same[A](x:A,y:A) = assert(x == y)
  
  test("Non-Empty Comprehensions") {
    
    the[ListOf[Int] OrderedSourceOf Int]
    the[ListOf[Int] FiniteSourceOf Int]
    
    the[NonEmpty[ListOf[Int]] OrderedSourceOf Int]
    the[NonEmpty[ListOf[Int]] FiniteSourceOf Int]
    the[NonEmpty[ListOf[Int]] NonEmptySourceOf Int]
    
    NonEmptyOrderedOps(nel0)
    
    same(for { x <- nel0 } yield x, nel0.toList.sum)
    
    same(for { x <- nel0; y <- nel1 } yield x * y, nel0.toList.sum * nel1.toList.sum)
    
    same[NonEmpty[ListOf[Int]]](for { x <- nel0 } yield ListOf(x), nel0)
    
    same((for { x <- nel0; y <- nel1 } yield List(x,y)).toList, (for { x <- nel0.toList; y <- nel1.toList } yield List(x,y)).flatten)
    
    
  }
  
  
  
}
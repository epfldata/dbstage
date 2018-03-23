package dbstage
package query

import org.scalatest.FunSuite
import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.implicits._
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup

object QueryFrontendTests {
  
  val nel0 = ListOf(1,2,3)
  val nel1 = ListOf(0,4,8,12)
  val l0: ListOf[Int] = nel0
  val l1: ListOf[Int] = nel1
  
  val xs = l0
  val ys = l1
  
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
  
  import example.paper._
  import example.paper.Data._
  import example.paper.Examples._
  
  test("Intro Example 2") {
    
    val r0 = ex2(depts,emps)
    //println(r0)
    
    val r1 = ex2ManualHashJoin(depts,emps)
    //println(r1)
    
    //val r2 = SetOf(r1.map(xs => ListOf(xs:_*)):_*)
    val r2 = ListOf.of(r1:_*)
    //println(r2)
    
    //assert(r0 == SetOf(r1.map(xs => ListOf(xs:_*)):_*))
    assert(r0 == r2)
    
  }
  
  
  test("HasJobs Example") {
    import example._
    
    assert(p[Age] > 18)
    assert(!(p[Age] > 100))
    assert(p[Name] != "Joe")
    assert(p[Name] == "Jo")
    
    println(avgJobSalaries(MultiSetOf(p),MultiSetOf.empty,MultiSetOf(j)))
    
  }
  
  test("Phantom Subtype Instances") {
    import example._
    
    //implicitly[Age Wraps Int]
    //implicitly[Monoid[Age]]
    val s = Age(1) |+| Age(2)
    println(s: Age)
    
  }
  
  
  @field type Color <: String
  //@field type Size <: Int
  @field type Size <: String
  @field type Cost <: Int
  //Debug show
  //{object A{@field type C <: Int}}
  
  test("Data Cube") {
    
    val sales = ListOf(
      Size("large") ~ Color("blue") ~ Cost(10),
      Size("large") ~ Color("red") ~ Cost(20),
      Size("small") ~ Color("blue") ~ Cost(30),
      Size("small") ~ Color("red") ~ Cost(40)
    //) // FIXME ambiguous implicit: method monoid in object ~ and method commutativeSemigroup in object ~
    ) : ListOf[Size ~ Color ~ Cost]
    
    def cube2[A,B,C:Semigroup](a: A, b: B)(c: C) = c ~ c.groupBy(a) ~ c.groupBy(b) ~ c.groupBy(a ~ b)
    
    val res = for { s <- sales } yield cube2(s[Size],s[Color])(s[Cost])
    println(res : Cost ~ Map[Size,Cost] ~ Map[Color,Cost] ~ Map[Size ~ Color,Cost])
    
    //Debug show
    //implicitly[Monoid[Cost]]
    //IntoMonoid.infer(Cost(0))
    //(for { s <- sales } yield s[Cost])
    //sales.map[Cost,Cost](s => s[Cost])
    //  : Cost.Typ
    //  : Cost
    
  }
  
  
  
  test("Dummy Ex") {
    
    val ys = ListOf(0,1,2)
    
    //println(for { y <- ys } yield SetOf(for { x <- new ListOf(0 to y) if x*2 > y } yield x * y))
    println(for { y <- ys } yield SetOf(for { x <- new ListOf(y to y*2) if x*2 > y } yield 
      print(s"+$x*$y") thenReturn
      x * y) alsoDo println)
    
  }
  
  
}

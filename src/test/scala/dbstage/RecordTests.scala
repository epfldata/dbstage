package dbstage

import Embedding.Predef._
import cats.Monoid
import squid.utils._
//import frontend._
//import query._
import example._
import cats.implicits._

import org.scalatest.FunSuite

class RecordTests extends FunSuite {
  
  test("Fields are Monoids") {
    
    //import FieldBase.monoid
    implicitly[Monoid[Age]]
    //implicitly[Monoid[Age]](monoid)
    
    assert((Age(4) |+| Age(6)) == Age(10))
    assert((Name("A") ~ Age(4) |+| Name("B") ~ Age(6)) == Name("AB") ~ Age(10))
    
    import RecordDefs._
    //val a = Age2(123) ~ Name2("OK") ~ Address2("DTC")
    //val b: Age ~ Name2 ~ Address2 = 123 ~ "OK" ~ "DTC" // nope
    
  }
  
  
}

class RecordEmbeddingTests extends FunSuite {
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  test("Removal of Abstractions") {
    
    //implicitly[cats.Monoid[Name]](FieldBase.monoid)
    
    //import RecordDefs._
    //println(code"123:Age")
    //println(code"123:Age2")
    //println(code"Set.empty[Lol.T]")
    //println(code"Set.empty[Lol.F[Int]]")
    //println(dbg_code"??? : Lol.T") // FIXME uses a TypeTag
    //println(dbg_code"??? : Lol.F[Int]")
    
    val p = code{
      Name("A") ~ Age(4) |+| Name("B") ~ Age(6)
    } alsoApply println
    //println(p.compile)
    
    
  }
  
  test("Removal of Abstractions 2") {
    import RecordDefs._
    println(Name2("A") ~ Age2(4))
    
    val p = code{
      Name2("A") ~ Age2(4) |+| Name2("B") ~ Age2(6)
    } alsoApply println
    
    code"Age2(Age2.deapply(Age2(3)))" alsoApply println
    
  }
  test("Removal of Abstractions 3") {
    
    //implicitly[Name3 Wraps String]//(FieldBase.wraps)
    //Monoid[Name3](monoidWrap[Name3,String])
    //Monoid[Name3]
    println(Name3("A") ~ Age3(4))
    
    val p = code{
      Name3("A") ~ Age3(4) |+| Name3("B") ~ Age3(6)
    } alsoApply println
    
    code"Age3(FieldBase.wraps[Age3].deapply(Age3(3)))" alsoApply println
    
  }
  
}

import scala.language.higherKinds
object Lol { type T <: Int; type F[A] <: A }

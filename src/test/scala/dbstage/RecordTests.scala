package dbstage

import Embedding.Predef._
import squid.utils._
//import frontend._
//import query._
import example._
import cats.implicits._

import org.scalatest.FunSuite

class RecordTests extends FunSuite {
  
  test("Fields are Monoids") {
    
    assert((Age(4) |+| Age(6)) == Age(10))
    assert((Name("A") ~ Age(4) |+| Name("B") ~ Age(6)) == Name("AB") ~ Age(10))
    
    import RecordDefs._
    val a = Age2(123) ~ Name2("OK") ~ Address2("DTC")
    //val b: Age ~ Name2 ~ Address2 = 123 ~ "OK" ~ "DTC" // nope
    
  }
  
  
}

class RecordEmbeddingTests extends FunSuite {
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  test("Removal of Abstractions") {
    
    //implicitly[cats.Monoid[Name]](FieldBase.monoid)
    
    import RecordDefs._
    println(code"123:Age")
    println(code"123:Age2")
    
    val p = code{
      Name("A") ~ Age(4) |+| Name("B") ~ Age(6)
    } alsoApply println
    //println(p.compile)
    
    
  }
  
}

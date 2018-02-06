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
    
  }
  
  
}

class RecordEmbeddingTests extends FunSuite {
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  test("Removal of Abstractions") {
    
    println(code{
      Name("A") ~ Age(4) |+| Name("B") ~ Age(6)
    })
    
  }
  
}

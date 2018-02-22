package dbstage

import Embedding.Predef._
import cats.Monoid
import squid.utils._
import example._
import cats.implicits._

import RecordDefs.{p,j}

import org.scalatest.FunSuite

class RecordTests extends FunSuite {
  
  test("Field Selection") {
    
    assert(p.select[Name] == Name("Jo"))
    assert(p.select[Age] == Age(42))
    assert(p.select[Address] == Address("DTC"))
    
  }
  
  test("Field Access") {
    
    assert(p[Name] == "Jo")
    assert(p(Name) == "Jo")
    
    assert(p[Age] == 42)
    assert(p(Age) == 42)
    
    assert(p[Address] == "DTC")
    assert(p(Address) == "DTC")
    
    // Note: this doesn't work although CanAccess[A,R] extends (A => R), so it's surprising:
    assertDoesNotCompile(""" p:Age """)
    
    // Note: these don't work, which is expected:
    assertDoesNotCompile(""" Age(0):Int """)
    assertDoesNotCompile(""" 0:Age """)
    
  }
  
  test("Fields are Monoids") {
    
    implicitly[Monoid[Age]]
    
    assert((Age(4) |+| Age(6)) == Age(10))
    assert((Name("A") ~ Age(4) |+| Name("B") ~ Age(6)) == Name("AB") ~ Age(10))
    
  }
  
  /*
  test("Funky Fields Stuff") {
    //val p2 = p.map[Age](_ + 1)
    //val p2 = p.focus[Age].map(_ + 1) // missing parameter type
    //val p2 = p.focus[Age].map[Int](_ + 1)
    //val p2 = p.focus(Age).map(_ + 1) // ok
    val p2 = p.map(Age)(_ + 1)
    // ^ TODO impl
  }
  */
  
}

class RecordEmbeddingTests extends FunSuite {
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  test("Removal of Wrapping Abstractions") {
    
    assert(code{Name("A") ~ Age(4) |+| Name(readLine) ~ Age(readInt)} =~= 
           code{Name("A" + readLine) ~ Age(4 + readInt)})
    
    assert(code{Name(readDouble.toString) ~ Age(readDouble.toInt) |+| Name(readLine) ~ Age(readInt)} =~= 
           code{ // effects order is maintained:
             val rds = readDouble.toString
             val rdi = readDouble.toInt
             Name(rds + readLine) ~ Age(rdi + readInt)})
    
    assert(code{Name("A") ~ Age(4) |+| Name("B") ~ Age(6)} =~= 
           code{Name("A" + ${Const("B")}) ~ Age(4 + ${Const(6)})})
    
  }
  
  test("Removal of Access Abstractions") {
    
    assert(code"p[Age]" =~= code"Age.deapply(p.lhs.lhs.rhs)")
    
    assert(code"p(Age)" =~= code"Age.deapply(p.lhs.lhs.rhs)")
    
    assert(code{ val a = Name("Jo") ~ Age(readInt) ~ Gender(Male); a[Age] + a[Age] }
       =~= code{ val r = readInt; r + r })
    
    assert(code{ val a = Name(readLine) ~ Age(readInt) ~ Gender(Male); a[Age] + (Address("DTC") ~ Age(1))[Age] }
       =~= code{ readLine; readInt + 1 })
    
  }
  
  test("Removal of Project Abstraction") {
    
    assert(code"p.project[Age ~ Name]" =~= code"p.lhs.lhs.rhs ~ p.lhs.lhs.lhs")
    
  }
  
}

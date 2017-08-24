package dbstage
import example.{Person => PersonClass, _}

import Embedding.Predef._
import squid.utils._
import frontend._
import query._

import org.scalatest.FunSuite

class Basics extends FunSuite {
  
  def sameLines(q: Query)(model: String*) = {
    val modSet = model.toSet
    q.pushLines({ line =>
      assert(modSet(line), s"$line not in $modSet")
    }, pushHeader = false)
  }
  
  def Filtering(P: PersonClass) = {
    import P._
    
    P.loadDataFromFile("data/persons.csv", compileCode = false)
    
    val q0 = from(P) where ir"$Age > 18" where ir"$Sex == Male" select (Name,Age)
    sameLines(q0)(
      "|john smith|23|", 
      "|bob parker|41|", 
      "|hugh carper|67|")
    
  }
  
  test("Filtering") {
    case object P extends PersonClass
    P.indexByKeys = false
    
    Filtering(P)
    
  }
  
  test("Filtering IBK") {
    case object P extends PersonClass
    P.indexByKeys = true
    
    Filtering(P)
    
  }
  
  test("Filtering CS") {
    case object P extends PersonClass
    P.indexByKeys = false
    P.columnStore = true
    
    Filtering(P)
    
  }
  
  def Joining(P: PersonClass) = {
    import P._
    
    P.loadDataFromFile("data/persons.csv", compileCode = false)
    
    val m = from(P)
    val f = from(P)
    val q = ((m where ir"$Sex == Male") join (f where ir"$Sex == Female"))(ir"${m.Age} == ${f.Age}") select (m.Age, m.Name, f.Name, m.Id, f.Id)
    sameLines(q)(
      "|41|bob parker|julia kenn|1|6|", 
      "|7|toto ronto|derpita derpa|5|7|")
    
  }
  
  test("Joining") {
    case object P extends PersonClass
    P.indexByKeys = false
    
    Joining(P)
    
  }
  
  test("Joining IBK") {
    case object P extends PersonClass
    P.indexByKeys = true
    
    Joining(P)
    
  }
  
  test("Joining CS") {
    case object P extends PersonClass
    P.indexByKeys = false
    P.columnStore = true
    
    Joining(P)
    
  }
  
  
  
}

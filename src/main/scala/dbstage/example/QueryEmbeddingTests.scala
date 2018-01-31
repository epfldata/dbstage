package dbstage
package example

import squid.utils._
import cats.implicits._

import dbstage2.{Record,::,NoFields,Project,Access,FieldModule}
import dbstage2.example.RecordTestsDefs._
import dbstage2.example.{Name,Age,Salary,PersonId,JobId,Gender,Address,JobTitle}
import ExampleRelations.{ps,js,hs}

import dbstage2.Embedding.Predef._
import dbstage2.Embedding.Quasicodes._

object QueryEmbeddingTests extends App {{
  
  val ageLimit = 18
  
  val q0 = code{ //(ageLimit: Int) =>
    for {
      p <- ps
      if p[Age] > ageLimit
      j <- js
      //_ <- hs.naturalJoin(p).naturalJoin(j)
      hj <- hs
      if p[PersonId] == hj[PersonId] && j[JobId] == hj[JobId]
      //() = println(s"Processing:  $p  |  $j")
      //if {println(s"Processing:  $p  |  $j"); true}
    } yield j[Salary]
  }
  
  println(q0)
  //println(q0.run) // Interpreting overloaded methods that differ only by their return type is not supported: List(method withFilter, method withFilter)
  //println(q0.compile)
  
  val q0l = QueryCompiler.compile(q0) alsoApply println
  println(q0l.plans)
  
  
  
}}

object ExampleRelations {
  
  val ps = Relation.of(List(
    PersonId(1) :: Name("Jo") :: Age(42) :: Gender(true) :: Address("DTC") :: NoFields,
    PersonId(0) :: Name("Ed") :: Age(15) :: Gender(true) :: Address("HUH") :: NoFields,
    PersonId(4) :: Name("Vi") :: Age(23) :: Gender(false) :: Address("NYC") :: NoFields,
    PersonId(5) :: Name("Em") :: Age(37) :: Gender(false) :: Address("DTC") :: NoFields,
    PersonId(2) :: Name("Al") :: Age(65) :: Gender(true) :: Address("NYC") :: NoFields
  ))
  
  val js = Relation.of(List(
    JobId(0) :: JobTitle("Researcher") :: Salary(420) :: Address("NYC") :: NoFields,
    JobId(10) :: JobTitle("Wanker") :: Salary(0) :: Address("UYB") :: NoFields,
    JobId(11) :: JobTitle("Pebble Collector") :: Salary(1) :: Address("WDC") :: NoFields
  ))
  
  val hs = Relation.of(List(
    PersonId(1) :: JobId(0) :: NoFields,
    PersonId(0) :: JobId(10) :: NoFields,
    PersonId(5) :: JobId(11) :: NoFields,
    PersonId(4) :: JobId(0) :: NoFields
  ))
  
}

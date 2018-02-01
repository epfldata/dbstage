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

object StagedQueryComposition extends App {
  
  val q = for {
    p <- ps.staged
    q <- ps.staged
    _ <- Where(code"$p[Age] > $p[Age]") // FIXME
    //() <- Where(???)
  } yield code"$p -> $q"
  println(q)
  
}

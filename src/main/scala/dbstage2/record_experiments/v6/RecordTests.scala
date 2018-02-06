package dbstagetests
package record_experiments.v6

import squid.utils._

import Ops._
import RecordAccess._

import cats.Monoid
import cats.instances.all._
//import cats.implicits._
//import cats.syntax.all._
import cats.syntax.semigroup._


object RecordTests extends App {
  import dbstage2.example._
  
  //println(Name("A").value)
  
  val p = Name("Jo") ~ Age(42) ~ Gender(true) ~ Address("DTC")
  
  println(p)
  println(p[Age])
  println(p[Address])
  
  println(p.project[Name ~ Name])
  println(p.project[Name ~ Address ~ Gender])
  println(p.project[Name ~ Address ~ Gender].apply[Address])  // note: doesn't work without explicit `apply`
  
  
  
  
  
}

object TypeRecordTests extends App {
  
  //implicitly[(Int~String) RecordAccess Int] alsoApply println
  
  val r = 1 ~ "ok" ~ 'ko
  
  println(r.select[Int])
  println(r.select[String])
  println(r.select[Symbol])
  
  val amb = r ~ 42
  //println(amb[Int]) // ambiguous
  
  println(amb)
  println(123 ~ amb)
  println(123 ~ amb ~ 456)
  
  implicitly[Project[Int,Int]]
  implicitly[Project[Int ~ String,Int]]
  implicitly[Project[Int ~ String ~ Symbol,Int]]
  implicitly[Project[Int ~ String, Int ~ String]]
  
  println(r.project[Int ~ String ~ Symbol])
  println(r.project[Int ~ String])
  println(r.project[Int ~ Symbol])
  println(r.project[String ~ Int])
  println(r.project[String ~ Symbol])
  
  println(amb.project[String ~ Symbol])
  println(amb.project[Symbol ~ String])
  println(amb.project[String])
  //println(amb.project[Int ~ Symbol])  // impli not found (due to ambiguity)
  //println(amb.project[Int])  // ambiguous
  
  /*
  implicitly[Int |> Monoid]
  implicitly[Int RecordOf Monoid]
  implicitly[String |> Monoid]
  implicitly[(Int ~ String) RecordOf Monoid]
  */
  
  //catsSyntaxSemigroup(1 ~ "ok")
  println((1 ~ "ok") |+| (2 ~ "ko"))
  //Debug.show(if (false)(1 ~ "ok") |+| (2 ~ "ko"))
  
  
  println("Ok.")
  
}

object HigherKindedRecordTests extends App {
  
  //implicitly[(Int~String) Record (? =:= Int)] // nope
  //implicitly[(Int~String) Record (? =:= Int)](fromLHS) // nope
  //implicitly[(Int~String) Record (? =:= Int)](fromLHS[Int, String, ? =:= Int]) // nope
  //implicitly[(Int~String) Record (? =:= Int)](fromLHS[Int, String, ? =:= Int](fromF[Int,? =:= Int])) // yes...
  
  //implicitly[Int Record (? =:= Int)] // nope
  //implicitly[Int Record ({type F[T]=T=:=Int})#F] // nope
  
  //type F[T]=T=:=Int
  //implicitly[Int RecordAccess F] // yes
  //implicitly[Int Record ({type G[T]=F[T]})#G] // nope
  
}

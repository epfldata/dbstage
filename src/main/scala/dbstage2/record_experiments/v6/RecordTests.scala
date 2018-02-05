package dbstage2
package record_experiments.v6

import squid.utils._

import Ops._
import Record._

object RecordTests extends App {
  
  //implicitly[(Int~String) Record (? =:= Int)] // nope
  //implicitly[(Int~String) Record (? =:= Int)](fromLHS) // nope
  //implicitly[(Int~String) Record (? =:= Int)](fromLHS[Int, String, ? =:= Int]) // nope
  //implicitly[(Int~String) Record (? =:= Int)](fromLHS[Int, String, ? =:= Int](fromF[Int,? =:= Int])) // yes...
  
  //implicitly[Int Record (? =:= Int)] // nope
  //implicitly[Int Record ({type F[T]=T=:=Int})#F] // nope
  
  type F[T]=T=:=Int
  implicitly[Int Record F] // yes
  //implicitly[Int Record ({type G[T]=F[T]})#G] // nope
  
  println("Ok.")
  
}

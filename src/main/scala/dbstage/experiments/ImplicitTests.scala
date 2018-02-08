package dbstage.experiments

import scala.language.higherKinds

object ImplicitTests extends App {
  
  class Imp[T]
  
  class T0
  object T0 {
    //implicit def imp[T0] = new Imp[T0]
    //implicit def imp = new Imp[T0]
    implicit def imp[A] = new Imp[A]
  }
  
  //implicitly[Imp[Int]] // could not find implicit value
  
  println(implicitly[Imp[T0]])
  
  type T1 <: T0
  
  println(implicitly[Imp[T1]]) // works!
  
  type T1U >: T0
  
  //println(implicitly[Imp[T1U]]) // nope
  
  type T2
  object T2 {
    implicit def imp[A] = new Imp[A]
  }
  
  //println(implicitly[Imp[T2]]) // does not work (Scalac problem!)
  
  type T3[A]
  
  //println(implicitly[Imp[T3[T0]]]) // does not work
  
  class T3C[A]
  
  println(implicitly[Imp[T3C[T0]]]) // works...
  
  type T4 <: T3[T0]
  
  //println(implicitly[Imp[T4]]) // does not work
  
  type T5 <: T3C[T0]
  
  println(implicitly[Imp[T5]]) // works...
  
  type T5U >: T3C[T0]
  
  //println(implicitly[Imp[T5U]]) // nope
  
  
  
  
}


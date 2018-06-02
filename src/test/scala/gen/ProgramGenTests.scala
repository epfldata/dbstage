package gen

import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

object Gen extends ProgramGen
import Gen._

object Person extends Class {
  val name = param[String]
  val age = param[Int]
  val isMajor = method(c{$(age) > 18})
}

object ProgramGenTests extends App {
  
  //println(Person)
  println(Person.showCode)
  
  
}

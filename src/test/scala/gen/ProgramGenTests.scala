package gen

import squid.utils._
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
  
  //println(Person(c{42})) // assertion failed: Wrong number of parameters in Person(42) for class Person(val name: String, val age: Int)
  //println(Person(c{42}, c{"Bob"})) // assertion failed: Type of argument code"42": CodeType(Int(42)) is incompatible with type of parameter val name = val name: String; in in Person(42, "Bob")
  println(Person(c{"Bob"}, c{42}))
  
  /*
  import scala.tools.reflect.ToolBox
  import scala.reflect.runtime.{universe => sru}
  val toolBox = sru.runtimeMirror(getClass.getClassLoader).mkToolBox()
  toolBox.eval(Person.toScalaTree) alsoApply println
  */
}

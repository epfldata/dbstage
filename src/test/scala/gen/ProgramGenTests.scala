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
  
  import scala.tools.reflect.ToolBox
  import scala.reflect.runtime.{universe => sru}
  val toolBox = sru.runtimeMirror(getClass.getClassLoader).mkToolBox()
  toolBox.eval(Person.toScalaTree) alsoApply println
  
}

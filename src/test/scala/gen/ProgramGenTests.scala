package gen

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

//object Gen extends ProgramGen {
object Gen extends Embedding.ProgramGen {
  val root = Root.apply
  
  object Person extends Class {
    val name = param[String]
    val age = param[Int]
    val isMajor = method(c{$(age) > 18})
  }
  
}
import Gen._
import Person.{Self => PersonT}  // FIXME not importing this uses the abstract type :-/

object ProgramGenTests extends App {
  
  //println(Person)
  println(Person.showCode)
  
  //println(Person(c{42})) // assertion failed: Wrong number of parameters in Person(42) for class Person(val name: String, val age: Int)
  //println(Person(c{42}, c{"Bob"})) // assertion failed: Type of argument code"42": CodeType(Int(42)) is incompatible with type of parameter val name = val name: String; in in Person(42, "Bob")
  val inst = Person(c{"Bob"}, c{42})
  println(inst.rep.dfn)
  println(inst)
  //println(inst.run) // Could not find overloading index -1 for method gen.Gen.<init>; perhaps a quasiquote has not been recompiled atfer a change in the source of the quoted code?
  //println(inst.compile) // scala.tools.reflect.ToolBoxError: reflective compilation has failed: no arguments allowed for nullary constructor Person: ()gen.Gen.Person
  println(code"(p: PersonT) => ${Person.name}(p) * ${Person.age}($inst)")
  
  /*
  import scala.tools.reflect.ToolBox
  import scala.reflect.runtime.{universe => sru}
  val toolBox = sru.runtimeMirror(getClass.getClassLoader).mkToolBox()
  toolBox.eval(Person.toScalaTree) alsoApply println
  */
}

/*

Notes:

Can define a ClassWith[T] type that bounds its Repr to T and uses the evidence to find type params and fields; so we can pass a
structural type and use the methods on Repr in QQs! – just needs reflective methods on record types as a feature

A crucial part of the system will be the generation-time cycle detector and recovery!
Currently it's all too easy to get a stack overflow...
TODO: precise when cyclic errors should happen and what exactly is alloed –– for sure mutually recursive methods and classes are allowed! 

*/
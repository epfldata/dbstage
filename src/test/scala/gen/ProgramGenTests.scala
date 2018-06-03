package gen

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

//object Gen extends ProgramGen {
object Gen extends Embedding.ProgramGen {
  val root = Root.thisEnclosingInstance
  
  object Person extends Class {
    //println("Creating Person class")
    val name = param[String]
    val age = param[Int]
    val isMajor = method(c{$(age) > 18})
    val upcaseName = field(c"$name.toUpperCase")
    effect(c{println(s"Person created! ${$(name)}")})
    //val countDown: Method[Int => Unit] = method(c"(n: Int) => { println(n.toString); if (n > 0) $countDown(n-1) }") // SOF
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
  //println(code"$inst")
  
  println(Person.age(inst))
  //println(inst.age) // okay: value age is not a member of dbstage.Embedding.Code[gen.Gen.Person.Self,Any]
  //println((s:PersonT) => s.age) // okay: Unquote syntax ${...} cannot be used outside of a quasiquote.
  
  //println(inst.run) // Could not find overloading index -1 for method gen.Gen.<init>; perhaps a quasiquote has not been recompiled atfer a change in the source of the quoted code?
  //println(inst.compile) // scala.tools.reflect.ToolBoxError: reflective compilation has failed: no arguments allowed for nullary constructor Person: ()gen.Gen.Person
  //val p = code"(p: PersonT) => ${Person.name}(p) * ${Person.age}($inst)"
  val p = code"(p: PersonT) => p.name * $inst.age"
  println(p)
  //base debugFor 
  println(p rewrite {
    //case code"(${Person(Seq(xs))}:PersonT)|>$${Person.age}" => ???
    //case code"$${tryInline(Person.age)}(${Person(Seq(xs))}:PersonT)" => ???  // PROBLEM
    //case dbg_code"${Person.age}.apply(${Person(Seq(xs))}:PersonT)" => ???  // PROBLEM
    //
    //case code"${Person.age(p)}:Int" => println(p); code"0" // ok, matches
    
    case code"${Person.age(Person(name,code"$age:Int"))}:Int" => println(name,age); age // type error was due to patmat virt
    // FIXME doesn't match:
    case code"(${Person(name,code"$age:Int")}:PersonT).age" => println(name,age); age // type error was due to patmat virt
  })
  //println(inst.rep.dfn)
  //base debugFor 
  //println(inst match {
  //println(code"${Person.age}apply$inst" match {
  println(code"$inst.age" match {
    //case Person(Seq(xs)) => xs
    //case Person(xs@_*) => xs
    case Person(name,age) => (name,age)
    case Person.age(Person(name,age)) => (name,age)
    case _ => // Person ctor now effectful; and it it were not, its field access would now be inlined!
  })
  
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



// --- OLD ---

EDIT: this was fixed
Q: why isn't 'new Person', which is effectful, scheduled in:
    code"""((p_0: gen.Gen.Person) => {
      val x_1 = Predef.augmentString(p_0.name);
      x_1.*(new gen.Gen.Person("Bob", 42).age)
    })"""


*/
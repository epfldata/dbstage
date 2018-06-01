package dbstage
package db

import squid.utils._
import Embedding.Predef._

object DbgTests extends App {
  
  val mydb = Database(
    Relation("Person")(
      Column[String]("Name"),
      Column[Int]("Age"),
      Column[Bool]("Gender"),
    ),
  )
  
  val inst = mydb.instance(DatabaseConfig())
  println(inst)
  println(inst.tables.head._2.readInto)
  //println(inst.tables.head.readAll)
  
}

/*
object PgrmGenTests extends App {
  
  object MyProgram extends ScalaUnit {
    //object MyClass extends Class {
    //val MyClass = new Class { // FIXME: sourcecode https://github.com/lihaoyi/sourcecode/pull/48
    val MyClass = new Class()("MyClass") {
      val f = method(code"(x: Int) => x * 2")
      val g = method(code"$f($h)")
      val h = method(code"$f(0) + 1")
    }
  }
  //println(MyProgram.MyClass)
  println(MyProgram)
  //println(MyProgram.definitions)
  
}
*/


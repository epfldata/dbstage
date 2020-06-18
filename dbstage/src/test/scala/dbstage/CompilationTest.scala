package dbstage

import org.scalatest._
import example.Person
import example.Demo

class TypingTester extends FunSuite {
  test("Demo") {    
    // Generate code for Demo:
    val genCode =
      Demo.compile
        .replaceAll("example.Person", "Person")
        .replaceAll("example.Job", "Job")
        .replaceAll("example.Company", "Company")
    
    import os._
    write.over(
      pwd/'generated/'src/'main/'scala/'dbstage/"Demo.scala",
      genCode,
      createFolders = true)
    
  }
}

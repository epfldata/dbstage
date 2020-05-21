package dbstage

import org.scalatest._
import example.Person
import example.MyDatabase

class TypingTester extends FunSuite {
  
  test("MyDatabase") {
    
    // Example query:
    println(MyDatabase.allMinors.rep)
    
    // Generate code for MyDatabase:
    val genCode =
      MyDatabase.compile
        .replaceAll("example.Person", "Person") // temporary hack
        .replaceAll("example.Job", "Job")
        .replaceAll("example.Citizen", "Citizen")
    
    import os._
    write.over(
      pwd/'generated/'src/'test/'scala/'dbstage/"MyDatabase.scala",
      genCode,
      createFolders = true)
    
  }
  
}

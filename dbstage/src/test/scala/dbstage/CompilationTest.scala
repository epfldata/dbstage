package dbstage

import org.scalatest._
import example.Person
import example.MyDatabase
import example.Queries
import example.Insertions

class TypingTester extends FunSuite {
  
  // test("MyDatabase") {
    
  //   // Example query:
  //   // println(MyDatabase.allMinors.rep)
    
  //   // Generate code for MyDatabase:
  //   val genCode =
  //     MyDatabase.compile
  //       .replaceAll("example.Person", "Person") // temporary hack
  //       .replaceAll("example.Job", "Job")
  //       .replaceAll("example.Citizen", "Citizen")
    
  //   import os._
  //   write.over(
  //     pwd/'generated/'src/'test/'scala/'dbstage/"MyDatabase.scala",
  //     genCode,
  //     createFolders = true)
    
  // }

  test("TPCH") {
    val genCode =
      Queries.compile
        .replaceAll("example.Customer", "Customer")
        .replaceAll("example.Lineitem", "Lineitem")
        .replaceAll("example.Nation", "Nation")
        .replaceAll("example.Order", "Order")
        .replaceAll("example.Part", "Part")
        .replaceAll("example.PartSupp", "PartSupp")
        .replaceAll("example.Region", "Region")
        .replaceAll("example.Supplier", "Supplier")
        .replaceAll("example.TPCH.get_key_part_supp", "get_key_part_supp")
        .replaceAll("example.TPCH.get_key_line_item", "get_key_line_item")
    
    import os._
    write.over(
      pwd/'generated/'src/'test/'scala/'dbstage/"TPCH.scala",
      genCode,
      createFolders = true)
  }
}

package example

import dbstage.deep._
import IR.Predef._
import IR.Quasicodes._


// For now, we will define the database in a programmatic way as follows.
// But eventually, I'm hoping we can support lifting a Scala object that
// does the database definition, as in:
//    @lift def MyDatabase {
//      class Person(val name: String, val age: Int)
//      val tbl = new Table[Person]
//      def myQuery = { ... }
//    }
object MyDatabase extends StagedDatabase {
  
  val personCls = Person.reflect(IR)
  
  // Example table:
  val personsTable = new TableRep[Person](personCls)
  
  // Example query:
  val allMinors = query[Int](code{
    $(personsTable).view.filter(_.isMinor).size
  })
  
}


import squid.quasi.{lift, dbg_lift}

@lift
class Person(val name: String, val age: Int) {
  def isMinor = age < 18
}


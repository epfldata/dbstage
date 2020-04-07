package example

import dbstage.deep._
import IR.Predef._
import IR.Quasicodes._
import dbstage.lang.TableView._
import dbstage.lang.Str


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
  register(personCls)
  
  // Example query:
  val allMinors = query[Int](code{
    all[Person].filter(_.isMinor).size
  })
  
  val allOld = query[Int](code{
    all[Person].map(p => new Person(new Str("Test"), p.age+100)).size
  })

  val sizes = query[Int](code{
    all[Person].map(p => new Person(p.name, p.name.strlen.toInt)).size
  })

  val charAtQuery = query[Int](code{
    all[Person].map(p => new Person(new Str(p.name.charAt(2).toString), p.age)).size
  })

  val allOld2 = query[Int](code{
    all[Person].map(p => {p.age = p.age + 10; p}).size
  })

  // val insert = query[Person=>TableView[Person]](code{
  //   p: Person => $(personsTable).insert(p)
  // })
}


import squid.quasi.{lift, dbg_lift}

// Why not case class?
@lift
case class Person(var name: Str, var age: Int) {
  def isMinor = age < 18
}


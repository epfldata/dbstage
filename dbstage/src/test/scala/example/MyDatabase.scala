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
  val jobCls = Job.reflect(IR)
  register(jobCls)
  
  // Example query:
  val allMinors = query[Int](code{
    all[Person].filter(_.isMinor).size
  })

  val allOld = query[Int](code{
    all[Person].map(p => new Person(p.salary, p.name, p.age+100, p.job)).size
  })
  
  val allOld3 = query[Int](code{
    all[Person].map(p => new Person(0, new Str("Test"), p.age+100, p.job)).size
  })

  val sizes = query[Int](code{
    all[Person].map(p => new Person(p.salary, p.name, p.name.strlen.toInt, p.job)).size
  })

  val charAtQuery = query[Int](code{
    all[Person].map(p => new Person(p.salary, new Str(p.name.charAt(2).toString), p.age, p.job)).size
  })

  val allOld2 = query[Int](code{
    all[Person].map(p => {p.age = p.age + 10; p}).size
  })

  val joinQuery = query[Int](code{
    all[Person].join(all[Person]).size
  })

  val mapQuery = query[Int](code{
    all[Person].map(p => p.job).map(job => job.size).map(size => size+100).size
  })

  val insertions = query[Unit](code{
    val epfl = new Job(10000, new Str("EPFL"))
    val lucien = new Person(1000, new Str("Lucien"), 21, epfl)
    val john = new Person(100, new Str("John"), 16, epfl)
  })

  val sizePersonQuery = query[Int](code{
    all[Person].size
  })

  val sizeJobQuery = query[Int](code{
    all[Job].size
  })

  val sizeStrQuery = query[Int](code{
    all[Str].size
  })

  val sumAges = query[Int](code{
    all[Person].aggregate[Int](0, (person, sum) => person.age + sum)
  })

}


import squid.quasi.lift

@lift
class Person(var salary: Int, var name: Str, var age: Int, var job: Job) {
  def isMinor = age < 18
}

@lift
class Job(var size: Int, var enterprise: Str) {
}


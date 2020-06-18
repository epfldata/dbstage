package example

import squid.quasi.lift
import dbstage.deep._
import IR.Predef._
import IR.Quasicodes._
import dbstage.lang._
import dbstage.lang.TableView._
import scala.io.Source
import java.awt.RenderingHints.Key

object Demo extends StagedDatabase {
  val personCls = Person.reflect(IR)
  val personTable = register(personCls)
  val jobCls = Job.reflect(IR)
  val jobTable = register(jobCls)
  val companyCls = Company.reflect(IR)
  val companyTable = register(companyCls)

  val insertCompanies = query[Unit](code{
    for (line <- Source.fromFile("./data/companies.csv").getLines()) {
      val fields = line.split(",")

      val key = fields(0).toLong
      val name = new Str(fields(1))

      new Company(key, name)
    }
  })

  val insertJobs = query[Unit](code{
    for (line <- Source.fromFile("./data/jobs.csv").getLines()) {
      val fields = line.split(",")

      val key = fields(0).toLong
      val jobName = new Str(fields(1))
      val company = $(companyTable.getAtKey)(fields(2).toLong)

      new Job(key, jobName, company)
    }
  })

  val insertPersons = query[Unit](code{
    for (line <- Source.fromFile("./data/persons.csv").getLines()) {
      val fields = line.split(",")

      val key = fields(0).toLong
      val name = new Str(fields(1))
      val age = fields(2).toInt
      val salary = fields(3).toDouble
      val job = $(jobTable.getAtKey)(fields(4).toLong)

      new Person(key, name, age, salary, job)
    }
  })

  val printData = query[Unit](code{
    all[Person].forEach(p => {
      println(s"${p.name.string}, ${p.age} years old, earns ${p.salary} at ${p.job.company.name.string} as a ${p.job.jobName.string}")
    })
  })
}

@lift
class Person(val key: Long, val name: Str, var age: Int, var salary: Double, var job: Job) extends KeyedRecord {
  def isMinor = age < 18
}

@lift
class Job(val key: Long, val jobName: Str, val company: Company) extends KeyedRecord {
}

@lift
class Company(val key: Long, val name: Str) extends KeyedRecord {
}

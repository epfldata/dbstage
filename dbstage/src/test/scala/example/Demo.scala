package example

import squid.quasi.lift
import dbstage.deep._
import IR.Predef._
import IR.Quasicodes._
import dbstage.lang._
import dbstage.lang.TableView._
import dbstage.lang.Table._
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

  val deleteDave = query[Unit](code{
    all[Person].filter(p => p.name.strcmp("Dave") == 0).forEach(p => delete(p))
  })

  val printData = query[Unit](code{
    all[Person].forEach(p => {
      println(s"${p.name.string}, ${p.age} years old, earns ${p.salary} at ${p.job.company.name.string} as a ${p.job.jobName.string}")
    })
  })

  val printDataColumns = query[Unit](code{
    all[Person].forEach(p => {
      println(s"${p.name.string.padTo(7, " ").mkString} | ${p.age.toString.padTo(2, " ").mkString} | ${p.salary.toString.padTo(7, " ").mkString} | ${p.job.company.name.string.padTo(7, " ").mkString} | ${p.job.jobName.string.padTo(20, " ").mkString}")
    })
  })

  val computeMinimumSalary = query[(String, Double)](code{
    all[Person].aggregate[(String, Double)](("", Double.MaxValue), (p, acc) => {
      if (p.salary < acc._2) {
        (p.name.string, p.salary)
      } else acc
    })
  })

  val computeSalaryCompany = query[Unit](code{
    all[Company].map(c => {
      all[Person].filter(p => p.job.company == c)
                 .aggregate[(String, Double)]((c.name.string, 0), (p, acc) => (acc._1, acc._2 + p.salary))
    }).forEach(c => println(s"${c._1} has cumulated salary ${c._2}"))
  })

  val computeBiggestSalaryDifferenceCommunityManager = query[(String, String, Double)](code{
    val communityManagers = all[Person].filter(p => p.job.jobName.strcmp("Community Manager") == 0)

    communityManagers.join(communityManagers).aggregate[(String, String, Double)](("", "", Double.MinValue), (persons, acc) => {
      val diff = math.abs(persons._1.salary - persons._2.salary)

      if (diff > acc._3) {
        (persons._1.name.string, persons._2.name.string, diff)
      } else acc
    })
  })

  val increaseRobertSalaryBy750 = query[Unit](code{
    all[Person].filter(p => p.name.strcmp("Robert") == 0).forEach(p => p.salary += 750)
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

package dbstage.example.paper

import dbstage.desugar
import dbstage.query._
import squid.utils._
import squid.quasi.embed

import scala.collection.mutable

case class Department(name: String)
case class Employee(name: String, dept: Department)

object Data {
  val dA = Department("A")
  val dB = Department("B")
  val depts = ListOf(dA,dB)
  val emps = ListOf(Employee("Bob",dA),Employee("Alice",dA),Employee("Jane",dB))
  
  // dummy
  //type Json = String
  //def asJson(x: Any) = x.toString
  //type Json = Any
  type Json
  //def asJson(x: Any) = x
  def asJson(x: Any) = (x match {
    case (d,ls:collection.Set[_]) => (d,SetOf.of(ls.toList:_*))
    case x => x
  }).asInstanceOf[Json]
  
}

@embed
object Examples {
  
  import Data.{asJson,Json}
  
  @desugar def ex2(departments: ListOf[Department], employees: ListOf[Employee]) = {
    //for { d <- departments } yield ListOf((d, for { e <- employees } yield SetOf(e))|>Data.asJson)
    for { d <- departments } yield ListOf((d, for { e <- employees if e.dept == d } yield SetOf(e)) |> asJson)
  }
  
  @desugar def ex2ManualHashJoin(departments: ListOf[Department], employees: ListOf[Employee]) = {
    val empsPerDept = mutable.HashMap[Department, mutable.HashSet[Employee]]()
    val eit = employees.iterator
    while (eit.hasNext) {
      val e = eit.next
      empsPerDept.getOrElseUpdate(e.dept, mutable.HashSet.empty) += e
    }
    val buffer = mutable.ListBuffer[Json]()
    val dit = departments.iterator
    while (dit.hasNext) {
      val d = dit.next
      buffer += (d,empsPerDept(d)) |> asJson
    }
    buffer.result()
  }
  
  
  import dbstage.example._
  
  @desugar def avgJobSalaries(persons: Bag[Person], hasJob: Bag[HasJob], jobs: Bag[Job]) = {
    //(for { j <- (persons naturalJoin hasJob naturalJoin jobs).project[Job]
    //      if j[JobType].value == "Private Sector"
    //    } yield count() ~ avg(j[Salary]) groupBy j[JobTitle]
    //) .orderBy [Avg[Salary]] .filter (x => x[Count] > 100)
  }
  
}

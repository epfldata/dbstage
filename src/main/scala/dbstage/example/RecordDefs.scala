package dbstage
package example

import squid.utils._

case class PersonId(value: Int) extends AnyVal with Field[Int]
case class Name(value: String) extends AnyVal with Field[String]
case class Age(value: Int) extends AnyVal with Field[Int]
case class Gender(value: Bool) extends AnyVal with Field[Bool]
case class Address(value: String) extends AnyVal with Field[String]

case class JobId(value: Int) extends AnyVal with Field[Int]
case class JobTitle(value: String) extends AnyVal with Field[String]
case class Salary(value: Int) extends AnyVal with Field[Int]

object RecordDefs {
  
  type Person = Name ~ Age ~ Gender ~ Address
  type IdPerson = PersonId ~ Person
  type Job = JobTitle ~ Salary ~ Address
  type IdJob = JobId ~ Job
  type HasJob = PersonId ~ JobId
  
  val p = Name("Jo") ~ Age(42) ~ Gender(true) ~ Address("DTC")
  val j = JobTitle("Researcher") ~ Salary(420) ~ Address("NYC")
  
  val recordSet = p ~ j
  assert(p == recordSet.select[Person])
  assert(j == recordSet.select[Job])
  
  
}

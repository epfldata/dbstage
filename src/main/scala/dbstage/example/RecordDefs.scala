package dbstage
package example

import squid.utils._

@field class PersonId(v: Int)
@field class Name(v: String)
@field class Age(v: Int)
@field class Gender(v: Bool)
@field class Address(v: String)

@field class JobId(v: Int)
@field class JobTitle(v: String)
@field class Salary(v: Int)

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

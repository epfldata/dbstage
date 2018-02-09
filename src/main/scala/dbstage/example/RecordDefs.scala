package dbstage
package example

import squid.quasi.embed
import squid.utils._

@field class PersonId(v: Int)
@field class Name(v: String)
@field class Age(v: Int)
//@field class Gender(v: Bool)
@field class Address(v: String)

@field class JobId(v: Int)
@field class JobTitle(v: String)
@field class Salary(v: Int)

sealed abstract class Gender
case object Male extends Gender
case object Female extends Gender

//@embed trait EmbeddedGenderDefs { }
//object Gender extends (Gender Wraps Bool) with EmbeddedGenderDefs {

//object Gender extends (Gender Wraps Bool) {
//  protected def applyImpl(v: Bool) = if (v) Male else Female
//  protected def deapplyImpl(x: Gender) = x === Male
object Gender extends (Gender Wraps Gender) {
  protected def applyImpl(v: Gender) = v
  protected def deapplyImpl(x: Gender) = x
  
  implicit object Read extends Read[Gender] { def apply(str: String) = str match {
    case "M" => Male
    case "F" => Female
  }}
}

object RecordDefs {
  
  type Person = Name ~ Age ~ Gender ~ Address
  type IdPerson = PersonId ~ Person
  type Job = JobTitle ~ Salary ~ Address
  type IdJob = JobId ~ Job
  type HasJob = PersonId ~ JobId
  
  val p = Name("Jo") ~ Age(42) ~ Gender(Male) ~ Address("DTC")
  val j = JobTitle("Researcher") ~ Salary(420) ~ Address("NYC")
  
  val recordSet = p ~ j
  assert(p == recordSet.select[Person])
  assert(j == recordSet.select[Job])
  
  
}

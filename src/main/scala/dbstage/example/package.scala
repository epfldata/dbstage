package dbstage

package object example {
  
  @field type PersonId <: Int
  @field type Name <: String
  @field type Age <: Int
  //@field type Gender <: Bool
  @field type Address <: String
  
  @field type JobId <: Int
  @field type JobTitle <: String
  @field type Salary <: Int
  
  
  sealed abstract class Gender
  case object Male extends Gender
  case object Female extends Gender
  
  object Gender extends (Gender Wraps Gender) {
    protected def applyImpl(v: Gender) = v
    protected def deapplyImpl(x: Gender) = x
    
    /*
    implicit object Read extends Read[Gender] { def apply(str: String) = str match {
      case "M" => Male
      case "F" => Female
    }}
    */
  }
  
  import query._
  
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

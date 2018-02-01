package dbstage2
package records.v2

import squid.utils._

case object Name extends Field[String]
case object Age extends Field[Int]
case object Gender extends Field[Bool]

case object JobTitle extends Field[String]
case object Salary extends Field[Int]

// doesn't really work (needs much more)
//object Person extends (Name.type :: Age.type :: Gender.type :: NoFields.type)

object RecordTests extends App {
  
  val Person = Name :: Age :: Gender :: NoFields
  type PersonType = Name.type :: Age.type :: Gender.type :: NoFields.type
  println(Person: PersonType)
  
  import Access._
  
  //println(Access.accessFirst[Name.type,Person.type])
  //println(Access.accessNext[Name.type,Age.type,Person.type])
  
  println(implicitly[Access[Name.type,Person.type]])
  println(implicitly[Access[Age.type,Person.type]])
  println(implicitly[Access[Gender.type,Person.type]])
  // if T in Access is not contravariant, we need to write:
  //println(implicitly[Access[Name.type,PersonType]])
  //println(implicitly[Access[Age.type,PersonType]])
  //println(implicitly[Access[Gender.type,PersonType]])
  
  val r = Person.mk("a")(42)(true)
  //println(r: Person.Rep)
  println(Age[Person.type](r))
  //println(Age(r)) // doesn't work anymore!!!
  //println(Name(r))
  
  
  
}

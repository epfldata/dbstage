package dbstage
package example

import squid.utils._

@field class PersonId(value: Int)
@field class Name(value: String)
@field class Age(value: Int)
@field class Gender(value: Bool)
@field class Address(value: String)

@field class JobId(value: Int)
@field class JobTitle(value: String)
@field class Salary(value: Int)

case class Age3(value: Int) extends Field[Int]
case class Name3(value: String) extends Field[String]

//case class Age2(value: Int) extends Field[Int]
//object Age2 extends (Int => Age2) {
//  //implicit object comp extends CompanionOf
//}

/*
case class PersonId(value: Int) extends AnyVal with Field[Int]
case class Name(value: String) extends AnyVal with Field[String]
case class Age(value: Int) extends AnyVal with Field[Int]
case class Gender(value: Bool) extends AnyVal with Field[Bool]
case class Address(value: String) extends AnyVal with Field[String]

case class JobId(value: Int) extends AnyVal with Field[Int]
case class JobTitle(value: String) extends AnyVal with Field[String]
case class Salary(value: Int) extends AnyVal with Field[Int]
*/

//case class Age(value: Int) extends AnyVal with Field[Int]
//object Age extends FieldModule[Age] {
//  implicit object builder extends BuildField[Age] { def apply(a: Int): Age = Age(a) }
//}
//@field type Age = Int
//@field class Age extends Int
//@field class Age(value: Int)
//@field case class Age(value: Int)

object RecordDefs {
  /*
  //@field type Age = Int
  case class Age2(value: Int) extends Field[Int]
  implicit object Age2 extends (Int => Age2) {
    //implicit object comp extends CompanionOf
  }
  case class Name2(value: String) extends Field[String]
  implicit object Name2 extends (String => Name2) {
    //implicit object comp extends CompanionOf
  }
  case class Address2(value: String) extends Field[String]
  implicit object Address2 extends (String => Address2) {
    //implicit object comp extends CompanionOf
  }
  */
  type Age2 <: Int
  //type Age2 <: Field[Int]
  //object Age2 extends (Int => Age2) { def apply(x:Int) = x.asInstanceOf[Age2] }
  //implicit object Age2 extends Wraps[Age2,Int]((x:Int) => x.asInstanceOf[Age2], x => x)
  implicit val Age2 = Wraps[Age2,Int]((x:Int) => x.asInstanceOf[Age2], x => x)
  //implicit object Age2 extends Wraps[Age2,Int]((x:Int) => x.asInstanceOf[Age2], x => x.asInstanceOf[Int])
  type Name2 <: String
  //type Name2 <: Field[String]
  //object Name2 extends (String => Name2) { def apply(x:String) = x.asInstanceOf[Name2] }
  //implicit object Name2 extends Wraps[Name2,String]((x:String) => x.asInstanceOf[Name2], x => x)
  implicit val Name2 = Wraps[Name2,String]((x:String) => x.asInstanceOf[Name2], x => x)
  //implicit object Name2 extends Wraps[Name2,String]((x:String) => x.asInstanceOf[Name2], x => x.asInstanceOf[String])
  
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

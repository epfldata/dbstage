package dbstage2
//package _perso_tests
package records.v1

import squid.utils._

case object Name extends Field[String]
case object Age extends Field[Int]
case object Gender extends Field[Bool]

case object JobTitle extends Field[String]
case object Salary extends Field[Int]

object RecordTests extends App {
  //import Record._
  
  val Person = Name :: Age :: Gender :: NoFields
  println(Person: Name.type :: Age.type :: Gender.type :: NoFields.type)
  
  val r = Person.mk("a")(42)(true)
  println(r : Person.Rep)
  //println(Age(r) : Int)
  
  val Job = JobTitle :: Salary :: NoFields
  val s = Job.mk("Engineer")(420)
  
  //import Job.FieldBaseOps
  import Job.{FieldBaseOps,accessFirst,accessNext}
  import Job.t.{accessFirst=>af2,accessNext=>an2}
  println(JobTitle(s))
  //println(accessNext[Salary.type])
  println(Salary(s))
  
}

object EmbeddedRecordTests extends App {
  import Embedding.Predef._
  import Embedding.Quasicodes._
  import RecordTests.Person
  
  //println(code"Age".typ)
  //println(code"RecordTests.Person".typ)
  
  //val p = code{
  //  val r = Person.mk("a")(42)(true)
  //  Age(r)
  //}
  //println(p)
  
  def bundle[T,C,R<:Record](v: Variable[R#Rep])(c: Code[T,C & v.Ctx]) = {
    println(c.Typ)
    c
  }
  val r = new Variable[Person.Rep]
  //bundle(r)(code{Age(${r.toCode})}) alsoApply println
  
  
  
}

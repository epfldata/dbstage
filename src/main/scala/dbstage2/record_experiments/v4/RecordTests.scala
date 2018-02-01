package dbstagetests
package v4

import squid.utils._

//import NoFields.NoFields
import Ops._

// desired syntax:
// @field class Name(value: String)

case class Name(value: String) extends AnyVal with Field[String]
//object Name extends FieldModule[Name]

case class Age(value: Int) extends AnyVal with Field[Int]
case class Gender(value: Bool) extends AnyVal with Field[Bool]
case class Address(value: String) extends AnyVal with Field[String]

case class JobTitle(value: String) extends AnyVal with Field[String]
case class Salary(value: Int) extends AnyVal with Field[Int]

object SimpleRecordTests {
  type Person = Name :: Age :: NoFields
  val p = Name("Jo") :: Age(42) :: NoFields
}

object RecordTests extends App {
  
  //val Person = Name :: Age :: Gender :: NoFields
  
  val p = Name("Jo") :: Age(42) :: Gender(true) :: Address("DTC") :: NoFields
  type Person = Name :: Age :: Gender :: Address :: NoFields
  println(p: Person)
  
  println(p(Name))
  println(p(Age))
  //println(p(Age)(Access.accessNext))
  //println(p(Age)(Access.accessNext[Age,Name,p.type,Int]))
  //Age:(Int=>Field[Int])
  
  val j = JobTitle("Engineer") :: Salary(420) :: Address("NYC") :: NoFields
  type Job = JobTitle :: Salary :: Address :: NoFields
  println(j: Job)
  
  println(p(Address))
  
  //println(p(JobTitle)) // oops! doesn't compile anymore (good)
  //println(j(Age)) // oops! doesn't compile anymore (good)
  //println(j(Gender)) // no, ok
  //println(j[Int,Age](Age)) // doesn't compile anymore (good)
  //println(implicitly[Access[Age,j.type,Int]])
  //println(implicitly[Access[Age,JobTitle :: Salary :: NoFields,Int]]) // doesn't compile anymore (good)
  
  //def test[X<:Singleton] = ()
  //test[Age] // no
  //test[Age.type] // yes
  
  val pair = (p,j)
  
  println(pair(Name),pair(Age))
  println(pair(JobTitle),pair(Salary))
  //println(pair(Address)) // ambiguous implicit: good!!!
  println(pair(Address)(Access.accessLeft))
  println(pair(Address)(Access.accessRight))
  
  // Let's shadow pair's address:
  val pair2 = Address("LOL") :: pair 
  println(pair2(Name),pair2(JobTitle))
  println(pair2(Address))
  
  
  def nice(ps:List[Person],js:List[Job]) = ps.zip(js).filter(_(Age) > 18).map(p => (p(Name), p(Salary)))
  
  
  // Projections
  
  implicitly[Person Project NoFields](Project.projectNone)
  implicitly[Person Project NoFields]
  //implicitly[Person Project (Name :: NoFields)](Project.projectNext[Name,Person,NoFields,String])
  //implicitly[Person Project (Name :: NoFields)](Project.projectNext)
  //implicitly[Person Project (Name :: NoFields)]
  //implicitly[Person Project (Age :: NoFields)]
  
  p.project[NoFields]
  //p.project[Name :: NoFields]
  
  
  
}

import dbstage2.Embedding
import Embedding.Predef._
import Embedding.Quasicodes._

//object Person extends Relation[JobTitle :: Salary :: Address :: NoFields]
object Person extends Relation[Name :: Age :: Gender :: Address :: NoFields]

object RelationTests extends App {
  
  //val s: Name :: Age :: Gender :: Address :: NoFields = ???
  //s(Name)
  
  //println(Person.load(code{ (r:Person.Rec) =>
  //  r(Name) * r(Age)
  //  
  //  //val s: JobTitle :: Salary :: Address :: NoFields = r
  //  //r(Name)(Access.accessFirst)
  //  //s(Name)
  //
  //}))
  
  Person.load(code{ (r:Person.Rec) => r(Name) * r(Age) }) alsoApply println
  
  
}
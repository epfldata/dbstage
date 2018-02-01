package dbstagetests
package v5

import squid.utils._

import Ops._

// desired syntax:
// @field class Name(value: String)

case class Name(value: String) extends AnyVal with Field[String]
case class Age(value: Int) extends AnyVal with Field[Int]
case class Gender(value: Bool) extends AnyVal with Field[Bool]
case class Address(value: String) extends AnyVal with Field[String]

case class JobTitle(value: String) extends AnyVal with Field[String]
case class Salary(value: Int) extends AnyVal with Field[Int]

object SimpleRecordTests extends App {
  type Person = Name :: Age :: NoFields
  val p = Name("Jo") :: Age(42) :: NoFields
  
  implicitly[Name Access Person] // alsoApply println
  implicitly[Age Access Person] // alsoApply println
  
  p(Age) alsoApply println
  
  implicitly[Person Project NoFields]
  implicitly[(Age::NoFields) Project (Age::NoFields)]
  implicitly[Person Project (Age::NoFields)]
  
  p alsoApply println
  p.project[Age :: NoFields] alsoApply println
  
  
  
}

object RecordTestsDefs {
  val p = Name("Jo") :: Age(42) :: Gender(true) :: Address("DTC") :: NoFields
  type Person = Name :: Age :: Gender :: Address :: NoFields
  val j = JobTitle("Researcher") :: Salary(420) :: Address("NYC") :: NoFields
  type Job = JobTitle :: Salary :: Address :: NoFields
}
import RecordTestsDefs._

object RecordTestsNewAccessSyntax extends App {
  println(p.get[Name], p.get[Age])
  val pair = (p,j)
  println(pair.get[Name],pair.get[JobTitle])
}

object RecordTests extends App {
  
  //val Person = Name :: Age :: Gender :: NoFields
  
  //val p = Name("Jo") :: Age(42) :: Gender(true) :: Address("DTC") :: NoFields
  //type Person = Name :: Age :: Gender :: Address :: NoFields
  println(p: Person)
  
  println(p(Name))
  println(p(Age))
  //println(p(Age)(Access.accessNext))
  //println(p(Age)(Access.accessNext[Age,Name,p.type,Int]))
  //Age:(Int=>Field[Int])
  
  //val j = JobTitle("Researcher") :: Salary(420) :: Address("NYC") :: NoFields
  //type Job = JobTitle :: Salary :: Address :: NoFields
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
  
  //p.project[NoFields]
  //p.project[Name :: NoFields]
  p.project[Age :: Address :: NoFields] alsoApply println
  
  def nicest(ps:List[Person],js:List[Job]) = ps.zip(js).filter(_(Age) > 18).map(_.project[Name :: Salary :: NoFields])
  println(nicest(p::Nil,j::Nil))
  
  
  
  
}

object RecordPairingTests extends App {
  //import RecordTests.{Person,Job,p,j}
  //RecordTests.main(args) // required init
  
  
  //implicitly[PairUp[Address::NoFields,Person]]/*(PairUp.hasPair)*/ alsoApply println
  //implicitly[PairUp[Person,Address::NoFields]] alsoApply println
  //implicitly[PairUp[Person,Address::NoFields]](PairUp.hasPair) alsoApply println
  //implicitly[PairUp[Person,Address::NoFields]](PairUp.hasPair[Address,Person,NoFields,String]) alsoApply println
  implicitly[PairUp[Person,Job]] //alsoApply println
  
  println(p,j)
  p.pairUpWith(j) alsoApply println
  
  
}

object RecordAcessTypesTests extends App {
  
  //type ![F] = ({type L[R]=Access[F,R]})#L
  
  // Can be done in Dotty; this works:
  /*
      scala> type Access[F,R]
      scala> type ![F] = [R] => Access[F,R]
      // defined alias type ! = [F] => [R] =>  <: ([F, R] => Any)[F, R]
      scala> def foo[R: ![Int]: ![Boolean]] = ()
      def foo
        [R]
          (implicit evidence$1: ([F] => [R] =>  <: ([F, R] => Any)[F, R])[Int][R],
            evidence$2
          : ([F] => [R] =>  <: ([F, R] => Any)[F, R])[Boolean][R]): Unit
      scala> implicit def accI: Access[Int,String] = ???
      implicit val accI:  <: ([F, R] => Any)[Int, String]
      scala> implicit def accI: Access[Boolean,String] = ???
      implicit val accI:  <: ([F, R] => Any)[Boolean, String]
      scala> foo[String]
      scala.NotImplementedError: an implementation is missing
  */
  
  
  
}

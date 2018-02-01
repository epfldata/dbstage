package dbstage2
package v3

import squid.utils._

//import FieldModule.Field

case object Name extends Field[String]
case object Age extends Field[Int]
case object Gender extends Field[Bool]

case object JobTitle extends Field[String]
case object Salary extends Field[Int]

/*
  
  Conclusion here is that it's possible to use an auxiliary type class to determine the type 
   
*/


// doesn't really work (needs much more)
//object Person extends (Name.type :: Age.type :: Gender.type :: NoFields.type)

object RecordTests extends App {
  
  val Person = Name :: Age :: Gender :: NoFields
  type PersonType = Name.type :: Age.type :: Gender.type :: NoFields.type
  println(Person: PersonType)
  
  object PersonR extends Relation(Name :: Age :: Gender :: NoFields)
  
  //println(implicitly[RecordRep[Person.type,_]])
  //println(implicitly[RecordRep[PersonType,_]])
  //println(implicitly[RecordRep[NoFields.type,_]])
  
  //import RecordRep._
  
  println(implicitly[RecordRep[NoFields.type]])
  println(implicitly[RecordRep[Gender.type::NoFields.type]]) // nope.. now yes
  println(implicitly[RecordRep[Person.type]]) // nope.. now yes
  
  //println(RecordRep.Rep2[Gender.type :: NoFields.type])
  //println(RecordRep.Rep2[Bool, NoFields.type] : RecordRep[Gender.type :: NoFields.type])
  //println(RecordRep.Rep2[Bool, Gender.type, NoFields.type])
  //println(implicitly[RecordRep[Gender.type :: NoFields.type]](RecordRep.Rep2)) // no
  //println(implicitly[RecordRep[Gender.type :: NoFields.type]](RecordRep.Rep2[Bool, Gender.type, NoFields.type])) // no
  
  
  //println(RecordRep.Rep2[Gender.type,NoFields.type] : RecordRep[Gender.type :: NoFields.type])
  //println(implicitly[RecordRep[Gender.type :: NoFields.type]](RecordRep.Rep2)) // nope
  //println(implicitly[RecordRep[Gender.type :: NoFields.type]](RecordRep.Rep2[Gender.type,NoFields.type]))
  //println(RecordRep.Rep2[Age.type,Gender.type :: NoFields.type])
  
  
  //println(FieldRep.frep[Int] : FieldRep[Age.type])
  //println(implicitly[FieldRep[Age.type]]) // no
  
  
  val r = ("Jo",(42,(true,())))
  println(Person.mkRep.apply( r ))
  
  
  //import Record.ConsOps
  //println(Person("Jo"))
  //println(Person.apply)
  //println(Person.apply("Jo")) // Error:(52, 23) _1.R.Mk[_1.R.Rep] does not take parameters
  
  //implicitly[RecordRep[Person.type]].mk("Jo") // nope: Error:(59, 40) _1.Mk[_1.Rep] does not take parameters
  //val m = implicitly[RecordRep[Person.type]].mk; m("Jo") // nope: inferred existential
  //val m = implicitly[RecordRep[Person.type]]; r: m.Rep // nope
  
  val m = implicitly[RecordRep[Person.type]{type Rep = (String,(Int,(Boolean,Unit)))}]
  println(r: m.Rep) // WORKS!!
  val n = implicitly[RecordRep[Person.type]{type Rep = (String,(Int,(Boolean,Unit))); type Mk[T] = String => Int => Boolean => T}]
  n.mk("Jo") // WORKS!!
  println(n.mk("Jo")(42)(true))
  
  def getRecRep[Rec<:Record] = new {
    def apply[R]()(implicit ev: RecordRep[Rec]{type Rep = R}) = ev
  }
  import scala.language.reflectiveCalls
  val m0 = getRecRep[Person.type]()
  println(r: m0.Rep) // WORKS!!
  
  val m1 = shapeless.the[RecordRep[Person.type]]
  println(r: m1.Rep) // WORKS!!
  println(m1.mk("Jo")(42)(true))
  
  // ---
  
  
  println(implicitly[Age.type Access (Name.type :: Age.type :: NoFields.type)]) // works!
  println(implicitly[Age.type Access (Name.type :: Age.type :: NoFields.type)])
  //println(Age(r)) // nope, we lose the info on r's type, which is just a tuple here!
  
  
  // ---
  
  //import PersonR.FieldOps
  //lazy val p: Person.Rep = ???
  //lazy val p = PersonR.getRep
  //println(Age(p)) // needs PersonR.FieldOps import
  //println(Age(p:PersonR.RecTrans)) // needs PersonR.FieldOps import
  //println(Age.test)
  //println(implicitly[PersonR.Access[String,Name.type,PersonR.Rec]])
  //println(implicitly[PersonR.Access[String,Name.type]])
  
  
  //println(Person.rep)
  
}

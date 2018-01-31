package dbstage
import dbstage2.{FieldModule, ::, Project, Record, Access, PairUp, NoFields}
import dbstage2.example.RecordTestsDefs._
import dbstage2.example._
import squid.utils._
import cats.{Semigroup, Monoid}
import dbstage2.FieldPair
import squid.lib.transparencyPropagating
import dbstage2.Embedding.Predef.{CodeType,codeTypeOf}
//import cats._
//import cats.instances.tuple
//import cats.instances.all
import cats.implicits._

object RelationTests extends App {
  import example.ExampleRelations._
  
  //Debug.show
  //{(j:Job) => Salary.get(j)}
  //{(j:Job) => FieldFunOps[Int,Salary](Salary).get(j)}
  
  val q0 =
  //Debug.show
  { (persons: Relation[IdPerson], jobs: Relation[IdJob], hasjob: Relation[HasJob]) => {
    for {
      p <- persons
      if p[Age] > 18
      j <- jobs
      hj <- hasjob
      if p[PersonId] == hj[PersonId] && j[JobId] == hj[JobId]
    //} yield Count() ~ Bag(p[Name] :: j[Salary] :: NoFields)
    //} yield (Count(), Bag(p[Name] :: Salary(j[Salary]) :: NoFields).sortBy[Salary])
    //} yield (Count(), Bag(p[Name] :: Salary(j) :: NoFields).sortBy[Salary])
    //} yield (Count(), Bag(p[Name] :: j.field[Salary] :: NoFields).orderBy[Salary])
    } yield Count() :: Bag(p[Name] :: j.field[Salary] :: NoFields).orderBy[Salary]  // :: NoFields
  }}
  
  //Debug.show
  { (persons: Relation[IdPerson], jobs: Relation[IdJob], hasjob: Relation[HasJob]) => {
    for {
      p <- persons
      if p[Age] > 18
      j <- jobs
      hj <- hasjob.naturalJoin(p).naturalJoin(j)
    } yield (Count(), Bag(p[Name] :: Salary(j[Salary]) :: NoFields).orderBy[Salary])
  }}
  
  println(q0(ps,js,hs))
  
  val q1 =
  //Debug.show
  {() => for {
      boy <- ps
      if boy[Gender]
      girl <- ps
      if !girl[Gender]
    //} yield Bag((boy[Name], girl[Name]))
    } yield Bag(boy[Name] :: girl[Name] :: Age(math.abs(boy[Age] - girl[Age])) :: NoFields).orderBy[Age]
  }
  println(q1())
  
  //val p = ::(Age(1),())
  //println(p[Age])
  
}
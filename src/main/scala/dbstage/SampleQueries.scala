package dbstage

import Embedding.Predef._
import frontend._
import query._
import Main.Person
import Main.HasJob

object OlderThan18 extends App {
  import Person._
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  
  val p = from(Person)
  val j = from(HasJob)
  
  //println(p.Age)
  //println(p.Age.toCode)
  
  //val q0 = p where ir"${p.Age} > 18" select (p.Name)
  val q0 = from(Person) where ir"$Age > 18" select (Name,Age)
  println(q0)
  val q1 = (p join j)(ir"${p.Id} == ${j.PersonId}")
  println(q1)
  //println(q1.plan)
  
  //println(q0.plan)
  //println(q0.plan.foreach(x => println(x)))
  val fe = q0.plan.foreach
  //fe(x => println(x._2:Int))
  fe { case (name, age) => assert(age > 18); println(s"$name $age") }
  
  
  
}

object PotentialCouples extends App {
  import Person._
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  
  val m = from(Person)
  val f = from(Person)
  //val m, f = from(Person)
  
  val males = m where (ir"$Sex == true")
  males.printLines
  //males.selectStringRepr().plan.foreach(println)
  
  val females = f where (ir"$Sex == false")
  females.printLines
  
  val q = (males join females)(ir"${m.Age} == ${f.Age}")
  println(q)
  println(q.plan)
  
  
  
}
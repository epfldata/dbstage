package dbstage

import Embedding.Predef._
import frontend._
import query._
import Main.Person
import Main.HasJob

object OlderThan18 extends App {
  import Person._
  Person.indexByKeys = false
  //Person.columnStore = true
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  //Person.loadDataFromFile("data/persons.csv")
  
  val p = from(Person)
  val j = from(HasJob)
  //p.printLines
  
  //println(p.Age)
  //println(p.Age.toCode)
  /*
  //val q0 = p where ir"${p.Age} > 18" select (p.Name)
  val q0 = from(Person) where ir"$Age > 18" select (Name,Age)
  println(q0)
  val q1 = (p join j)(ir"${p.Id} == ${j.PersonId}")
  println(q1)
  //println(q1.plan)
  
  
  // Pushing:
  
  //println(q0.plan)
  //println(q0.plan.foreach(x => println(x)))
  val fe = q0.plan.foreach
  //fe(x => println(x._2:Int))
  fe { case (name, age) => assert(age > 18); println(s"$name $age") }
  */
  
  // Pulling:
  val it = (p select (Name,Age)).plan.iterator
  while (it.hasNext) {
    val (name, age) = it.next()
    assert(age > 0)
    println(s"$name $age")
  }
  
}

object PotentialCouples extends App {
  import Person._
  //Person.indexByKeys = false  // TODO missing impl
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  
  val m = from(Person)
  val f = from(Person)
  //val m, f = from(Person)
  
  val males = m where (ir"$Sex == true")
  //males.printLines
  //males.selectStringRepr().plan.foreach(println)
  
  val females = f where (ir"$Sex == false")
  //females.printLines
  
  val q = (males join females)(ir"${m.Age} == ${f.Age}")
  println(q)
  println(q.plan)
  q.printLines
  //println(q.plan.foreach(println))
  
  
}
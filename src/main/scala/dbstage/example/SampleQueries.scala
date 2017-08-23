package dbstage
package example

import Embedding.Predef._
import squid.utils._
import frontend._
import query._

case object Person extends Relation {
  val Id = Column[Int]("Id", primary = true)
  val Name = Column[String]("Name")
  val Age = Column[Int]("Age")
  val Sex = Column[Sex]("Sex")
}
case object HasJob extends Relation {
  val PersonId = Column[Int]("PId", foreign = Person.Id)
  val Title = Column[String]("Title")
  val Salary = Column[Option[Int]]("Salary")
}

object OlderThan18 extends App {
  import Person._
  Person.indexByKeys = false
  //Person.columnStore = true
  
  //Person.loadDataFromFile("data/persons.csv", compileCode = false)
  Person.loadDataFromFile("data/persons.csv")
  
  val p = from(Person)
  val j = from(HasJob)
  //p.printLines
  
  //println(p.Age)
  //println(p.Age.toCode)
  ///*
  //val q0 = p where ir"${p.Age} > 18" select (p.Name)
  //val q0 = from(Person) where ir"$Age > 18" select (Name,Age)
  val q0 = from(Person) where ir"$Age > 18" where ir"$Sex == Male" select (Name,Age)
  println(q0)
  val q1 = (p join j)(ir"${p.Id} == ${j.PersonId}")
  println(q1)
  //println(q1.plan)
  
  
  // Pushing:
  
  ///*
  //println(q0.plan)
  //println(q0.plan.foreach(x => println(x)))
  val fe = q0.plan.foreach
  //fe(x => println(x._2:Int))
  fe { case (name, age) => assert(age > 18); println(s"$name $age") }
  //*/
  
  // Pulling:
  val it = (q0 select (Name,Age)).plan.iterator
  //val it = (q0 select (Name,Age)).plan.iterator2
  while (it.hasNext) {
    val (name, age) = it.next()
    assert(age > 18)
    println(s"$name $age")
  }
  
}

object OlderThan18_ColStore extends App {
  import Person._
  Person.indexByKeys = false // can also enable
  Person.columnStore = true
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  
  val q0 = from(Person) where ir"$Age > 18" where ir"$Sex == Male" select (Name,Age)
  q0.printLines
  
}

/*
TODO: allow syntax: `val m = from(Person) where (ir"$Sex == Male"); ... m.Age ...`
TODO: allow naming of sources? as in: `val p = from(Person, "p")`
*/
object PotentialCouples extends App {
  import Person._
  //Person.indexByKeys = false  // TODO missing impl
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  
  val m = from(Person)
  val f = from(Person)
  //val m, f = from(Person)
  
  val males = m where (ir"$Sex == Male")
  //males.printLines
  //males.selectStringRepr().plan.foreach(println)
  
  val females = f where (ir"$Sex == Female")
  //females.printLines
  
  ///*
  val q = (males join females)(ir"${m.Age} == ${f.Age}")
  println(q)
  println(q.plan)
  q.printLines
  //println(q.plan.foreach(println))
  //*/
  
  val q2 = (males join females)(ir"${m.Age} == ${f.Age}") select (m.Age, m.Name, f.Name, m.Id, f.Id)
  //q2.plan.foreachLifted(ir"println(_:Any)")
  q2.printLines
  
}

object PotentialCouples_ColStore extends App {
  import Person._
  Person.indexByKeys = false
  Person.columnStore = true
  
  Person.loadDataFromFile("data/persons.csv", compileCode = false)
  
  val m = from(Person)
  val f = from(Person)
  //val males = m where (ir"$Sex == Male")
  //val females = f where (ir"$Sex == Female")
  //val q = (males join females)(ir"${m.Age} == ${f.Age}") select (m.Age, m.Name, f.Name, m.Id, f.Id)
  val q = ((m where ir"$Sex == Male") join (f where ir"$Sex == Female"))(ir"${m.Age} == ${f.Age}") select (m.Age, m.Name, f.Name, m.Id, f.Id)
  //val q = ((m where ir"(${Sex.toCode}.asInstanceOf[Sex]) == Male") join (f where ir"$Sex == Female"))(ir"${m.Age} == ${f.Age}") select (m.Age, m.Name, f.Name, m.Id, f.Id)
  q.printLines
  
}

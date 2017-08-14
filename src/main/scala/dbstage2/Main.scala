package dbstage2

import dbstage2.Embedding.Predef._
//import Serial._

object Main extends App {
  
  val PersonId = ColumnOf[Int]("person-id")
  val Name = ColumnOf[String]("name")
  val Age = ColumnOf[Int]("age")
  val JobTitle = ColumnOf[String]("job")
  val Salary = ColumnOf[Option[Int]]("salary")
  
  //val Persons = Schema(PersonId primary, Name, Age)
  //val Jobs    = Schema(PersonId foreignIn persons.schema, JobTitle, Salary)
  
  //println(ir"$Age > 0")
  
  //From (Persons,Jobs) { (p,j) =>
  //  SELECT()
  //}
  
  
  //impliticly
  //val R = new Relation(Column[String]("name"), Column[Int]("age"))
  //val S = new Schema(Column[String]("name"), Column[Int]("age"))
  /*
  //import SQL._
  import InterpretAnalyticSQL._
  
  val PersonId = Column[Int]("person-id")
  val Name = Column[String]("name")
  val Age = Column[Int]("age")
  val JobTitle = Column[String]("job")
  val Salary = Column[Option[Int]]("salary")
  
  val persons = fromFile("persons.csv", Schema(PersonId primary, Name, Age))
  val jobs    = fromFile("jobs.csv",    Schema(PersonId foreignIn persons.schema, JobTitle, Salary))
  
  val averageAgePerJob = (jobs naturalJoin persons groupBy Salary average Age)
  println(averageAgePerJob)
  println(averageAgePerJob.run)
  println(averageAgePerJob.run())
  
  val under18highEarners = (jobs naturalJoinWhere persons)(Age < 18 && Salary > 42)
  println(under18highEarners)
  println(under18highEarners.run)
  
  // TODO
  //val unemployed = 
  */
  
  
  
  
}

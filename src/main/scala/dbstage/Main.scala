package dbstage

import Embedding.Predef._
import frontend._

object Main extends App {
  
  // TODO syntax sugar:
  // @column type Name = String
  // ~>
  // val Name = Column[String]("Name")
  
  case object Person extends Relation {
    val Id = Column[Int]("Id", primary = true)
    val Name = Column[String]("Name")
    val Age = Column[Int]("Age")
  }
  case object HasJob extends Relation {
    val PersonId = Column[Int]("PId", foreign = Person.Id)
    val Title = Column[String]("Title")
    val Salary = Column[Option[Int]]("Salary")
  }
  
  import query._
  
  //val p0 = Scan(Person.table.value)
  val p0 = Filter(Scan(Person.table.value), ir"${Person.Age} > 18")
  //println(p0.mkPrgm.init(ir"{(x:Any) => println(x)}"))
  //val pgrm0 = p0.push(ir"{(x:Any) => println(x); true}")
  //println(pgrm0)
  //println(pgrm0.map(_ transformWith LogicFlow))
  //println(pgrm0.compile())
  //p0.printLines.foreach(println)
  //val pgrm0 = Print(p0).foreachCode(ir"{(x:String) => println(x)}")
  //val pgrm0 = Print(p0).foreach(println)
  //println(pgrm0)
  
  
  //System exit -1
  //???
  
  // register queries
  
  // val q0 = ...
  // queries += q0, ...
  // or: specializeLoadingFor(q0, ...) { ... }
  
  // load data
  
  Person.loadDataFromFile("data/persons.csv")
  //Person.loadDataFromFile("data/persons.csv")
  HasJob.loadDataFromFile("data/jobs.csv")
  //HasJob.loadDataFromFile("data/jobs.csv")
  
  // execute query
  p0.foreach(println)
  Print(p0).foreach(println)
  
  // println(q0.run)
  
  
  
  
  
}

/*

select: may contain different fields/columns from different tables as well as aggregates
from: express as instanciation?

val p = new PersonQuery


*/

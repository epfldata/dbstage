package dbstage

import Embedding.Predef._
import frontend._

object Main extends App {
  
  // TODO syntax sugar:
  // @column type Name = String
  // ~>
  // val Name = Column[String]("Name")
  
  case object Person extends Table {
    val Id = Column[Int]("Id", primary = true)
    val Name = Column[String]("Name")
    val Age = Column[Int]("Age")
  }
  case object HasJob extends Table {
    val PersonId = Column[Int]("PId", foreign = Person.Id)
    val Title = Column[String]("Title")
    val Salary = Column[Option[Int]]("Salary")
  }
  
  Person.loadDataFromFile("data/persons.csv")
  HasJob.loadDataFromFile("data/jobs.csv")
  
  
  
  
  
  
}

/*

select: may contain different fields/columns from different tables as well as aggregates
from: express as instanciation?

val p = new PersonQuery


*/

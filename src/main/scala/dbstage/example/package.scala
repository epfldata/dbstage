package dbstage

import Embedding.Predef._

package object example extends RecordDefs {
  
  //val persons = new InputFile[IdPerson]("data/Persons.csv")
  val personsSource = new StagedInputFile[IdPerson]("data/Persons.csv")
  
  val persons = new SimpleDataTable[IdPerson] ++= personsSource
  for { _ <- 1 until 50 } persons ++= personsSource
  
  
  
}

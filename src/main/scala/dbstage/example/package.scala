package dbstage

import Embedding.Predef._

package object example extends RecordDefs {
  
  //val persons = new InputFile[IdPerson]("data/Persons.csv")
  val persons = new StagedInputFile[IdPerson]("data/Persons.csv")
  
  
  
}

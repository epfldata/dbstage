package dbstage
package example

import squid.utils._
import RecordDefs._
import Embedding.Predef._

object SimpleQueries extends App {
  
  //implicitly[RecordRead[String ~ Int]]
  //implicitly[RecordRead[Age]]
  //implicitly[RecordRead[Age ~ Name]]
  //implicitly[RecordRead[Age ~ Name]]
  
  //import Staged.apply  // note: makes the compiler stack-overflow!!
  
  //"data/persons.tbl"
  
  val f = new InputFile[IdPerson]("data/Persons.csv")
  println(f.iterator.next)
  
  val sf = new StagedInputFile[IdPerson]("data/Persons.csv")
  println(sf.stagedIterator.compile.next)
  //println(sf.stagedIterator)
  
  //println(Staged.apply(1).embedded(Embedding))
  //println(the[Staged[Separator]])
  
  
}

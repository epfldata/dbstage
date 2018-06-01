package dbstage

object TmpTests extends App {
  import Embedding.Predef._
  
  //println(reflect.runtime.universe.typeTag[dbstage.query.NonEmpty[As]])
  println(reflect.runtime.universe.typeTag[dbstage.query.NonEmpty[Int]])
  //def foo[As] = {
  def foo[As:CodeType] = {
  //def foo[As:reflect.runtime.universe.TypeTag] = {
    //println(reflect.runtime.universe.typeTag[dbstage.query.NonEmpty[As]])
    code"Option.empty[dbstage.query.NonEmpty[As]]"
  }
  foo[String]
  
  
}

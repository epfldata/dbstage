package dbstage2
package example

import squid.utils._

object PolymorphicTests extends App {
  import RecordTestsDefs._
  
  val P0 = {
    import Embedding.Predef._
    Relation[Job]()
      .withPrimaryKeys[JobTitle :: Address :: NoFields]
  }
  
  def q0(qhk: QueryHK) = {
    import qhk.base.Predef._
    import qhk.base.Quasicodes._
    code{
      P0.queryHK.filter(_[Salary]>18).filter(_[Address]=="NYC").count
    }
  }
  println(q0(StagedQuery))
  println(q0(PlainQuery).rep)
  //println(q0(PlainQuery).run) // TODO impl run
  
}

package dbstage2
package example

import squid.utils._

object CrossStageTests extends App {
  import RecordTestsDefs._
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  //val P0 = Relation[Person]()
  //  .withPrimaryKeys[Name :: Address :: NoFields]
  val P0 = Relation[Job]()
    .withPrimaryKeys[JobTitle :: Address :: NoFields]
  
  {
    //val P1 = Relation[Person]()
    //  .withPrimaryKeys[JobTitle :: NoFields]
    val P1 = Relation[Job]()
      .withPrimaryKeys[JobTitle :: NoFields]
    
    val db = Database2(P0, P1)
    
    //println(db)
    //db.analyse(code{
    //  (P0.query.filter(_[Age]>18).count, P1.query.count)
    //})
    
    /*
    val qp = db.lift(code{
      //P0.query.filter(_[Age]>18).count
      P1.query.filter(_[Salary]>18).count
    })
    println(qp)
    */
    
    //println(dbg.implicitType[PlainQuery.Filter[Int]])
    
    //import PlainQuery.base.Predef.implicitType
    
    println(db.liftHK(code{
      //P0.queryHK.filter(_[Age]>18).count
      P1.queryHK.filter(_[Salary]>18).filter(_[Address]=="NYC").count
    }))
    
    
    
  }
    
  
}

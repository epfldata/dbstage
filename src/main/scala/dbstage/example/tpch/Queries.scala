package dbstage
package example.tpch

import squid.utils._
import cats.instances.all.{
  catsKernelStdOrderForString=>_,
  _}
import squid.quasi.{embed,phase}

// TODO @embed to be able to compile
@embed
object Queries extends App {
  
  
  
  // this is actually from TPC-DS:
  /*
  @phase('Sugar)
  def Q4(orders: DataSource[Order], lineitem: DataSource[LineItem]) = {
    
    val DATE = Date("1993-07-01")
    
    val res = for {
      o <- orders
      //if {println(s"? O $o");true}
      if o[OrderDate] >= DATE
      if o[OrderDate] < DATE + Interval(3)
      //if {println(s"√ O $o");true}
      if (for {
            l <- lineitem
            //if {println(s"? L $l");true}
            if l[OrderKey] === o[OrderKey]
            if l[CommitDate] < l[ReceiptDate]
            //if {println(s"√ L $l");true}
          } yield Bag(l)).nonEmpty
    //} yield (Bag(o.select[OrderPriority] :: NoFields).orderBy[OrderPriority], 
    //    //Count())
    //    OrderCount(Count()))
    //} yield (Bag(o.select[OrderPriority] :: OrderCount(Count()) :: NoFields).groupBy[OrderPriority].orderBy[OrderPriority])
    //} yield (GroupedBag(o.select[OrderPriority] :: NoFields)(OrderCount(Count())).orderBy[OrderPriority])
    //} yield Bag(Count()).groupBy(o.select[OrderPriority]).orderBy(_[OrderPriority])
    } yield Bag(Count()).groupBy(o.select[OrderPriority]).orderByGroupKey
    
    res
  }
  */
  
  @phase('Sugar)
  def Q4(orders: DataSource[Order], lineitem: DataSource[LineItem]) = {
    for {
      o <- orders
      l <- lineitem
      if o[OrderKey] == l[OrderKey] && l[CommitDate] < l[ReceiptDate]  // join predicates
      //_ <- Bag(println(s"J $o >< $l"))
      
      //if o[OrderDate] >= Date("1993-08-01") && o[OrderDate] < Date("1993-11-01")  // original – too selective for small data set
      if o[OrderDate] >= Date("1993-08-01") && o[OrderDate] < Date("1994-11-01")
      
      _ <- Bag(println(s"S $o"))
      
    //} yield Bag(o.select[OrderPriority]) ~ Count() groupBy (o[OrderPriority]) orderBy (_[OrderPriority])
    //} yield Count() groupByAndSelect o.p[OrderPriority] orderBy (_[OrderPriority])
    //} yield Bag(o.p[OrderPriority]).limit(1) ~ Count() groupBy o[OrderPriority] orderBy (_.lhs.head[OrderPriority]) // works, but ugly!
    //} yield Key(o.p[OrderPriority]) ~ Count() groupBy o[OrderPriority] orderBy /*(_.p[Key[String]].value)*/ (_.p[Key[OrderPriority]]) // works, but direct usage of Key is unsafe.../
    } yield Count() groupBy o.p[OrderPriority] withKey() orderBy (_.p[Key[OrderPriority]]) // works, but direct usage of Key is unsafe...
    
  }
  
  //implicitly[Semigroup[Key[String]]]
  
  // FIXME allow syntax `_[Key[OrderPriority]]` without requiring full Wraps (have an Unwrap subclass without re-wrapping capa)
  
  
}

object QueryTests extends App {
  import Queries._
  
  Embedding.embed(Queries)
  
  val orders = new InputFile[Order]("data/orders.tbl.1")
  val lineitem = new InputFile[LineItem]("data/lineitem.tbl.1")
  
  println(Q4(orders, lineitem))
  
  ///*
  //println(Staged(Q4(orders, lineitem)).embedded(Embedding))
  import Embedding.Predef._
  //import Staged.apply
  val q = example.QueryEmbedding.compileQuery(Staged(Q4(orders, lineitem)))
  //println(q)
  println(q.compile.apply())
  //*/
  
  
  
  
  
}

package dbstage
package example.tpch

import squid.utils._
import cats.instances.all.{
  catsKernelStdOrderForString=>_,
  catsKernelStdOrderForInt=>_,
  _}
import squid.quasi.{embed,phase}

// TODO @embed to be able to inline and optimize the code of these queries
//@squid.quasi.dbg_embed
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
  
  @desugar
  def Q4(orders: DataSource[Order], lineitem: DataSource[LineItem]) = {
    (for {
      o <- orders
      l <- lineitem
      if o[OrderKey] == l[OrderKey] // natural join predicate
      
      
      /* The natural-join-based forms take too long to compile because of the PairUp implicit resolution part (could be
       * easily be sped-up by macro, though)
       * Note: the normalization part is not the problem, as writing this compiles promptly:
       *   if o.normalize.apply[OrderKey] == l.normalize.apply[OrderKey] */
      
      //o <- orders
      //l <- lineitem.naturallyJoining(o)
      //l ~ o <- lineitem naturalJoin orders  // FIXME[Squid] Embedding Error: Unsupported feature (patmat)
      
      // does no seem to finish compiling, even after making PairUp normalize both of its inputs:
      //l <- lineitem
      //o <- orders.naturallyJoining(l)
      //o ~ l <- orders naturalJoin lineitem
      
      
      if l[CommitDate] < l[ReceiptDate]  // join predicate
      
      //if o[OrderDate] >= Date("1993-08-01") && o[OrderDate] < Date("1993-11-01")  // original – too selective for small data set
      //if o[OrderDate] >= Date("1993-08-01") && o[OrderDate] < Date("1994-11-01")
      if o[OrderDate] >= Date("1993-08-01") && o[OrderDate] < Date("1996-11-01")
      
      _ <- Bag(println(s"S $o"))
      
    //} yield Count() groupingBy o.p[OrderPriority]).orderBy[OrderPriority](desc = true) // original
    } yield Count() groupBy o.p[OrderPriority]).orderBy[Count](desc = true)
    
  }
  
  
}

object QueryTests {
  import Queries._
  Embedding.embed(Queries)
  
  val orders = new InputFile[Order]("data/orders.tbl.1")
    .withPrimaryKey[OrderKey]
  val lineitem = new InputFile[LineItem]("data/lineitem.tbl.1")
  // .withPrimaryKey[?]
  
  def main(args: Array[String]) = {
    
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
  
}


// TODO allow syntax `_[Key[OrderPriority]]` without requiring full Wraps (have an Unwrap subclass without re-wrapping capa)


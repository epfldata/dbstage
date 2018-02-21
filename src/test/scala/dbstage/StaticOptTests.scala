package dbstage

import squid.utils._
import QueryOptimizer.{optimize,dbg_optimize}
import example.tpch.Queries._

object StaticOptTests extends App {
  import example.tpch.QueryTests
  import QueryTests.{orders,lineitem}
  //val (orders,lineitem) = (QueryTests.orders, QueryTests.lineitem)
  
  Debug show
  optimize{
    Q4(orders,lineitem)
  }
  
  
  
}

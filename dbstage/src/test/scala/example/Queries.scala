package example

import dbstage.deep._
import IR.Predef._
import IR.Quasicodes._
import dbstage.lang._
import dbstage.lang.TableView._
import scala.io.Source

object Queries extends StagedDatabase {
  val nationCls = Nation.reflect(IR)
  register(nationCls)
  val regionCls = Region.reflect(IR)
  register(regionCls)
  val partCls = Part.reflect(IR)
  register(partCls)
  val supplierCls = Supplier.reflect(IR)
  register(supplierCls)
  val partsuppCls = PartSupp.reflect(IR)
  register(partsuppCls)
  val customerCls = Customer.reflect(IR)
  register(customerCls)
  val orderCls = Order.reflect(IR)
  register(orderCls)
  val lineitemCls = Lineitem.reflect(IR)
  register(lineitemCls)

  val tpch_query_6 = query[Double](code{
    all[Lineitem].filter(li => li.l_quantity < 24 &&
                                li.l_discount >= 0.05 &&
                                li.l_discount <= 0.07 &&
                                li.l_shipdate.strcmp("1994-01-01") >= 0 &&
                                li.l_shipdate.strcmp("1995-01-01") < 0)
                  .aggregate[Double](0, (li, acc) => acc + li.l_extendedprice * li.l_discount)
  })

  val big_scan = query[Double](code{
    all[Lineitem].aggregate[Double](0.0, (li, acc) => acc + li.l_tax)
  })

  val key_join = query[Double](code{
    all[Lineitem].filter(li => {
      val ps = li.l_partsupp
      val p = ps.ps_part
      ps.ps_supplycost < 50.0 && p.p_brand.strcmp("Brand#44") != 0
    }).aggregate[Double](0.0, (li, acc) => acc + li.l_extendedprice * li.l_discount)
  })

  val cross_join = query[Double](code{
    all[Supplier].join(all[Supplier])
      .map(suppliers => math.abs(suppliers._1.s_acctbal - suppliers._2.s_acctbal))
      .aggregate[Double](Double.MinValue, _ max _)
  })
}

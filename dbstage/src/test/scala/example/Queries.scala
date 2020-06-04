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

  val q6 = query[Double](code{
    all[Lineitem].filter(li => li.l_quantity < 24 &&
                                li.l_discount > 0.05 &&
                                li.l_discount < 0.07 &&
                                li.l_shipdate.strcmp(new Str("1994-01-01")) > 0 &&
                                li.l_shipdate.strcmp(new Str("1995-01-01")) < 0)
                  .aggregate[Double](0, (li, acc) => acc + li.l_extendedprice * li.l_discount)
  })

  val pq1 = query[Double](code{
    all[Customer].aggregate[Double](0, (c, acc) => acc + c.c_acctbal)
  })
}

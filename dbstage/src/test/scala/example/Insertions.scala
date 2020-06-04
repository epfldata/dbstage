package example

import dbstage.deep._
import IR.Predef._
import IR.Quasicodes._
import dbstage.lang._
import dbstage.lang.TableView._
import scala.io.Source

object Insertions extends StagedDatabase {
  val nationCls = Nation.reflect(IR)
  val nationTable = register(nationCls)

  val regionCls = Region.reflect(IR)
  val regionTable = register(regionCls)

  val partCls = Part.reflect(IR)
  val partTable = register(partCls)

  val supplierCls = Supplier.reflect(IR)
  val supplierTable = register(supplierCls)

  val partsuppCls = PartSupp.reflect(IR)
  val partsuppTable = register(partsuppCls)

  val customerCls = Customer.reflect(IR)
  val customerTable = register(customerCls)
  
  val orderCls = Order.reflect(IR)
  val orderTable = register(orderCls)

  val lineitemCls = Lineitem.reflect(IR)
  val lineitemTable = register(lineitemCls)

  val printAllSizes = query[Unit](code{
    println("Number of regions: " + $(regionTable.getSize))
    println("Number of nations: " + $(nationTable.getSize))
    println("Number of parts: " + $(partTable.getSize))
    println("Number of suppliers: " + $(supplierTable.getSize))
    println("Number of part suppliers: " + $(partsuppTable.getSize))
    println("Number of customers: " + $(customerTable.getSize))
    println("Number of orders: " + $(orderTable.getSize))
    println("Number of lineitems: " + $(lineitemTable.getSize))
  })

  val insertRegions = query[Unit](code{
    val filename = "./tpch/region.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")
      val r = new Region(fields(0).toLong,
                          new Str(fields(1)),
                          new Str(fields(2)))
    }
  })

  val insertNations = query[Unit](code{
    val filename = "./tpch/nation.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")
      val n = new Nation(fields(0).toLong,
                          new Str(fields(1)),
                          $(regionTable.getAtKey)(fields(2).toLong),
                          new Str(fields(3)))
    }
  })

  val insertParts = query[Unit](code{
    val filename = "./tpch/part.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")

      val key = fields(0).toLong
      val p_type = new Str(fields(1))
      val p_size = fields(2).toInt
      val p_brand = new Str(fields(3))
      val p_name = new Str(fields(4))
      val p_container = new Str(fields(5))
      val p_mfgr = new Str(fields(6))
      val p_retailprice = fields(7).toDouble
      val p_comment = new Str(fields(8))

      val p = new Part(key,
                      p_name,
                      p_mfgr,
                      p_brand,
                      p_type,
                      p_size,
                      p_container,
                      p_retailprice,
                      p_comment)
    }
  })

  val insertSuppliers = query[Unit](code{
    val filename = "./tpch/supplier.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")

      val key = fields(0).toLong
      val nation_key = fields(1).toLong
      val s_comment = new Str(fields(2))
      val s_name = new Str(fields(3))
      val s_address = new Str(fields(4))
      val s_phone = new Str(fields(5))
      val s_acctbal = fields(6).toDouble

      val s_nation = $(nationTable.getAtKey)(nation_key)

      val s = new Supplier(key,
                          s_name,
                          s_address,
                          s_nation,
                          s_phone,
                          s_acctbal,
                          s_comment)
    }
  })

  val insertPartSupps = query[Unit](code{
    val filename = "./tpch/partsupp.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")

      val key_part = fields(0).toLong
      val key_supp = fields(1).toLong
      val ps_supplycost = fields(2).toDouble
      val ps_availqty = fields(3).toInt
      val ps_comment = new Str(fields(4))

      val key = get_key_part_supp(key_part, key_supp)
      val ps_part = $(partTable.getAtKey)(key_part)
      val ps_supp = $(supplierTable.getAtKey)(key_supp)

      val ps = new PartSupp(key,
                            ps_part,
                            ps_supp,
                            ps_availqty,
                            ps_supplycost,
                            ps_comment)
    }
  })

  val insertCustomers = query[Unit](code{
    val filename = "./tpch/customer.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")
      val c = new Customer(fields(0).toLong,
                          new Str(fields(1)),
                          $(nationTable.getAtKey)(fields(2).toLong),
                          new Str(fields(3)),
                          new Str(fields(4)),
                          new Str(fields(5)),
                          fields(6).toDouble,
                          new Str(fields(7)))
    }
  })

  val insertOrders = query[Unit](code{
    val filename = "./tpch/orders.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")

      val o_orderdate = new Str(fields(0))
      val key = fields(1).toLong
      val cust_key = fields(2).toLong
      val o_orderpriority = new Str(fields(3))
      val o_shippriority = fields(4).toInt
      val o_clerk = new Str(fields(5))
      val o_orderstatus = new Str(fields(6))
      val o_totalprice = fields(7).toDouble
      val o_comment = new Str(fields(8))

      val o_cust = $(customerTable.getAtKey)(cust_key)

      val o = new Order(key,
                        o_cust,
                        o_orderstatus,
                        o_totalprice,
                        o_orderdate,
                        o_orderpriority,
                        o_clerk,
                        o_shippriority,
                        o_comment)
    }
  })

  val insertLineitems = query[Unit](code{
    val filename = "./tpch/lineitem.csv"
    for (line <- Source.fromFile(filename).getLines) {
      val fields = line.substring(1, line.length-1).split("','")

      val l_shipdate = new Str(fields(0))
      val key_order = fields(1).toLong
      val l_discount = fields(2).toDouble
      val l_extendedprice = fields(3).toDouble
      val key_supp = fields(4).toLong
      val l_quantity = fields(5).toLong
      val l_returnflag = new Str(fields(6))
      val key_part = fields(7).toLong
      val l_linestatus = new Str(fields(8))
      val l_tax = fields(9).toDouble
      val l_commitdate = new Str(fields(10))
      val l_receiptdate = new Str(fields(11))
      val l_shipmode = new Str(fields(12))
      val l_linenumber = fields(13).toLong
      val l_shipinstruct = new Str(fields(14))
      val l_comment = new Str(fields(15))

      val key = get_key_line_item(key_order, l_linenumber)
      val partSupp = $(partsuppTable.getAtKey)(get_key_part_supp(key_part, key_supp))
      val order = $(orderTable.getAtKey)(key_order)

      val l = new Lineitem(key,
                        order,
                        partSupp,
                        l_linenumber,
                        l_quantity,
                        l_extendedprice,
                        l_discount,
                        l_tax,
                        l_returnflag,
                        l_linestatus,
                        l_shipdate,
                        l_commitdate,
                        l_receiptdate,
                        l_shipinstruct,
                        l_shipmode,
                        l_comment)
    }
  })

  def get_key_part_supp(key_part: Long, key_supp: Long): Long = key_part * 10000L + key_supp // There are 10'000 suppliers
  def get_key_line_item(key_order: Long, linenumber: Long): Long = linenumber * 1500000L + key_order // There are 1'500'000 orders
}

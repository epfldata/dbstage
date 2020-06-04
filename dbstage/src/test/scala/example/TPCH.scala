package example

import dbstage.deep._
import dbstage.lang._
import squid.quasi.lift

@lift
class Nation(
  val key: Long,
  val n_name: Str,
  val n_region: Region,
  val n_comment: Str
) extends KeyedRecord

@lift
class Region(
  val key: Long,
  val r_name: Str,
  val r_comment: Str
) extends KeyedRecord

@lift
class Part(
  val key: Long,
  val p_name: Str,
  val p_mfgr: Str,
  val p_brand: Str,
  val p_type: Str,
  val p_size: Int,
  val p_container: Str,
  val p_retailprice: Double,
  val p_comment: Str
) extends KeyedRecord

@lift
class Supplier(
  val key: Long,
  val s_name: Str,
  val s_address: Str,
  val s_nation: Nation,
  val s_phone: Str,
  val s_acctbal: Double,
  val s_comment: Str
) extends KeyedRecord

@lift
class PartSupp(
  val key: Long,
  val ps_part: Part,
  val ps_supp: Supplier,
  val ps_availqty: Int,
  val ps_supplycost: Double,
  val ps_comment: Str
) extends KeyedRecord

@lift
class Customer(
  val key: Long,
  var c_mktsegment: Str,
  var c_nation: Nation,
  var c_name: Str,
  var c_address: Str,
  var c_phone: Str,
  var c_acctbal: Double,
  var c_comment: Str
) extends KeyedRecord

@lift
class Order(
  val key: Long,
  val o_cust: Customer,
  val o_orderstatus: Str,
  val o_totalprice: Double,
  val o_orderdate: Str,
  val o_orderpriority: Str,
  val o_clerk: Str,
  val o_shippriority: Int,
  val o_comment: Str
) extends KeyedRecord

@lift
class Lineitem(
  val key: Long,
  val l_order: Order,
  val l_partsupp: PartSupp,
  val l_linenumber: Long,
  val l_quantity: Long,
  val l_extendedprice: Double,
  val l_discount: Double,
  val l_tax: Double,
  val l_returnflag: Str,
  val l_linestatus: Str,
  val l_shipdate: Str,
  val l_commitdate: Str,
  val l_receiptdate: Str,
  val l_shipinstruct: Str,
  val l_shipmode: Str,
  val l_comment: Str
) extends KeyedRecord
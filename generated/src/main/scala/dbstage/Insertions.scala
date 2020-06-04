package main

import scala.collection.mutable
import scala.scalanative.unsafe._
import lib.string._
import lib.str._
import lib.LMDBTable
import lib.LMDBTable._

object Insertions {
  val sizeInt = sizeof[Int].toInt
  val sizeLong = sizeof[Long].toInt
  val sizeDouble = sizeof[Double].toInt

  type CustomerData = CStruct14[
    Long,
    Long,
    Str,
    Long,
    Nation,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Double,
    Long,
    Str
  ]
  type Customer = Ptr[CustomerData]
  val sizeCustomer = sizeof[CustomerData]
  lazy val Customer_table_51492a90 = new LMDBTable[Customer]("Customer")

  type LineitemData = CStruct26[
    Long,
    Long,
    Order,
    Long,
    PartSupp,
    Long,
    Long,
    Double,
    Double,
    Double,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str
  ]
  type Lineitem = Ptr[LineitemData]
  val sizeLineitem = sizeof[LineitemData]
  lazy val Lineitem_table_75ebe2e6 = new LMDBTable[Lineitem]("Lineitem")

  type NationData = CStruct7[Long, Long, Str, Long, Region, Long, Str]
  type Nation = Ptr[NationData]
  val sizeNation = sizeof[NationData]
  lazy val Nation_table_351048d2 = new LMDBTable[Nation]("Nation")

  type OrderData = CStruct15[
    Long,
    Long,
    Customer,
    Long,
    Str,
    Double,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Int,
    Long,
    Str
  ]
  type Order = Ptr[OrderData]
  val sizeOrder = sizeof[OrderData]
  lazy val Order_table_1dc4326 = new LMDBTable[Order]("Order")

  type PartData = CStruct15[
    Long,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Long,
    Str,
    Int,
    Long,
    Str,
    Double,
    Long,
    Str
  ]
  type Part = Ptr[PartData]
  val sizePart = sizeof[PartData]
  lazy val Part_table_348994ca = new LMDBTable[Part]("Part")

  type PartSuppData =
    CStruct9[Long, Long, Part, Long, Supplier, Int, Double, Long, Str]
  type PartSupp = Ptr[PartSuppData]
  val sizePartSupp = sizeof[PartSuppData]
  lazy val PartSupp_table_1edfac6f = new LMDBTable[PartSupp]("PartSupp")

  type RegionData = CStruct5[Long, Long, Str, Long, Str]
  type Region = Ptr[RegionData]
  val sizeRegion = sizeof[RegionData]
  lazy val Region_table_29be21be = new LMDBTable[Region]("Region")

  type SupplierData = CStruct12[
    Long,
    Long,
    Str,
    Long,
    Str,
    Long,
    Nation,
    Long,
    Str,
    Double,
    Long,
    Str
  ]
  type Supplier = Ptr[SupplierData]
  val sizeSupplier = sizeof[SupplierData]
  lazy val Supplier_table_4a9892dc = new LMDBTable[Supplier]("Supplier")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_324f6081 = new LMDBTable[CString]("Str")

  def toCString_51100e73(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_97b763e(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Customer_4d539bc2(value: Customer): Customer = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
      value._9 = null
      value._11 = null
      value._14 = null
    }
    value
  }
  def empty_pointers_Lineitem_12aa5724(value: Lineitem): Lineitem = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._12 = null
      value._14 = null
      value._16 = null
      value._18 = null
      value._20 = null
      value._22 = null
      value._24 = null
      value._26 = null
    }
    value
  }
  def empty_pointers_Nation_15118928(value: Nation): Nation = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
    }
    value
  }
  def empty_pointers_Order_54bf3855(value: Order): Order = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._8 = null
      value._10 = null
      value._12 = null
      value._15 = null
    }
    value
  }
  def empty_pointers_Part_563248b(value: Part): Part = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
      value._9 = null
      value._12 = null
      value._15 = null
    }
    value
  }
  def empty_pointers_PartSupp_45305074(value: PartSupp): PartSupp = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._9 = null
    }
    value
  }
  def empty_pointers_Region_5c71d933(value: Region): Region = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Supplier_71212f78(value: Supplier): Supplier = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
      value._9 = null
      value._12 = null
    }
    value
  }

  def get_Customer_6dfa0aab(key: Long)(implicit zone: Zone): Customer = {
    val value = Customer_table_51492a90.get(key)
    empty_pointers_Customer_4d539bc2(value)
  }
  def get_Lineitem_2a0e4490(key: Long)(implicit zone: Zone): Lineitem = {
    val value = Lineitem_table_75ebe2e6.get(key)
    empty_pointers_Lineitem_12aa5724(value)
  }
  def get_Nation_78e4b8a3(key: Long)(implicit zone: Zone): Nation = {
    val value = Nation_table_351048d2.get(key)
    empty_pointers_Nation_15118928(value)
  }
  def get_Order_460228(key: Long)(implicit zone: Zone): Order = {
    val value = Order_table_1dc4326.get(key)
    empty_pointers_Order_54bf3855(value)
  }
  def get_Part_3d54efd9(key: Long)(implicit zone: Zone): Part = {
    val value = Part_table_348994ca.get(key)
    empty_pointers_Part_563248b(value)
  }
  def get_PartSupp_65260627(key: Long)(implicit zone: Zone): PartSupp = {
    val value = PartSupp_table_1edfac6f.get(key)
    empty_pointers_PartSupp_45305074(value)
  }
  def get_Region_385a8b80(key: Long)(implicit zone: Zone): Region = {
    val value = Region_table_29be21be.get(key)
    empty_pointers_Region_5c71d933(value)
  }
  def get_Supplier_8bd99e1(key: Long)(implicit zone: Zone): Supplier = {
    val value = Supplier_table_4a9892dc.get(key)
    empty_pointers_Supplier_71212f78(value)
  }

  def get_Str_120d8508(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_324f6081.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Customer_474fae50(data_el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_51492a90.put(
      data_el._1,
      sizeCustomer,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Lineitem_2ba52bb2(data_el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_75ebe2e6.put(
      data_el._1,
      sizeLineitem,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Nation_4fb3ddb6(data_el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_351048d2.put(
      data_el._1,
      sizeNation,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Order_31e8248d(data_el: Order)(implicit zone: Zone): Unit = {
    Order_table_1dc4326.put(
      data_el._1,
      sizeOrder,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Part_bef21ce(data_el: Part)(implicit zone: Zone): Unit = {
    Part_table_348994ca.put(
      data_el._1,
      sizePart,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_PartSupp_99573ba(data_el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_1edfac6f.put(
      data_el._1,
      sizePartSupp,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Region_589a4153(data_el: Region)(implicit zone: Zone): Unit = {
    Region_table_29be21be.put(
      data_el._1,
      sizeRegion,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Supplier_28a6ca81(data_el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_4a9892dc.put(
      data_el._1,
      sizeSupplier,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_5ab56f0a(str: Str)(implicit zone: Zone): Unit = {
    Str_table_324f6081.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Customer_6c69a7cf(el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_51492a90.delete(el._1)
  }
  def delete_Lineitem_1d554877(el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_75ebe2e6.delete(el._1)
  }
  def delete_Nation_1b36c97f(el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_351048d2.delete(el._1)
  }
  def delete_Order_45a3f82c(el: Order)(implicit zone: Zone): Unit = {
    Order_table_1dc4326.delete(el._1)
  }
  def delete_Part_64557888(el: Part)(implicit zone: Zone): Unit = {
    Part_table_348994ca.delete(el._1)
  }
  def delete_PartSupp_53f48285(el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_1edfac6f.delete(el._1)
  }
  def delete_Region_7e24c4b5(el: Region)(implicit zone: Zone): Unit = {
    Region_table_29be21be.delete(el._1)
  }
  def delete_Str_241c21c5(el: Str)(implicit zone: Zone): Unit = {
    Str_table_324f6081.delete(el._1)
  }
  def delete_Supplier_6bc29cf6(el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_4a9892dc.delete(el._1)
  }

  def fill_in_first_values(li: Lineitem, key: Long, order: Order, ps: PartSupp, linenumber: Long, quantity: Long, extendedprice: Double): Unit = {
    li._1 = key
    li._2 = order._1
    li._3 = order
    li._4 = ps._1
    li._5 = ps
    li._6 = linenumber
    li._7 = quantity
    li._8 = extendedprice
  }

  def fill_in_middle_values(li: Lineitem, discount: Double, tax: Double, returnflag: Str, linestatus: Str, shipdate: Str, commitdate: Str): Unit = {
    li._9 = discount
    li._10 = tax
    li._11 = returnflag._1
    li._12 = returnflag
    li._13 = linestatus._1
    li._14 = linestatus
    li._15 = shipdate._1
    li._16 = shipdate
    li._17 = commitdate._1
    li._18 = commitdate
  }

  def fill_in_last_values(li: Lineitem, receiptdate: Str, shipinstruct: Str, shipmode: Str, comment: Str): Unit = {
    li._19 = receiptdate._1
    li._20 = receiptdate
    li._21 = shipinstruct._1
    li._22 = shipinstruct
    li._23 = shipmode._1
    li._24 = shipmode
    li._25 = comment._1
    li._26 = comment
  }

  def init_Lineitem_67f9e7f6(key: Long, order: Order, ps: PartSupp, linenumber: Long, quantity: Long, extendedprice: Double,
                              discount: Double, tax: Double, returnflag: Str, linestatus: Str, shipdate: Str, commitdate: Str,
                              receiptdate: Str, shipinstruct: Str, shipmode: Str, comment: Str)(implicit zone: Zone): Lineitem = {
    val new_val = alloc[LineitemData]
    fill_in_first_values(new_val, key, order, ps, linenumber, quantity, extendedprice)
    fill_in_middle_values(new_val, discount, tax, returnflag, linestatus, shipdate, commitdate)
    fill_in_last_values(new_val, receiptdate, shipinstruct, shipmode, comment)
    put_Lineitem_2ba52bb2(new_val)
    get_Lineitem_2a0e4490(new_val._1)
  }
  def init_Order_5b418f3f(
      params: Tuple9[Long, Customer, Str, Double, Str, Str, Str, Int, Str]
  )(implicit zone: Zone): Order = {
    val new_val = alloc[OrderData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    new_val._6 = params._4
    new_val._7 = params._5._1
    new_val._8 = params._5
    new_val._9 = params._6._1
    new_val._10 = params._6
    new_val._11 = params._7._1
    new_val._12 = params._7
    new_val._13 = params._8
    new_val._14 = params._9._1
    new_val._15 = params._9
    put_Order_31e8248d(new_val)
    get_Order_460228(new_val._1)
  }
  def init_Customer_6151c55b(
      params: Tuple8[Long, Str, Nation, Str, Str, Str, Double, Str]
  )(implicit zone: Zone): Customer = {
    val new_val = alloc[CustomerData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    new_val._8 = params._5._1
    new_val._9 = params._5
    new_val._10 = params._6._1
    new_val._11 = params._6
    new_val._12 = params._7
    new_val._13 = params._8._1
    new_val._14 = params._8
    put_Customer_474fae50(new_val)
    get_Customer_6dfa0aab(new_val._1)
  }
  def init_PartSupp_4cec652c(
      params: Tuple6[Long, Part, Supplier, Int, Double, Str]
  )(implicit zone: Zone): PartSupp = {
    val new_val = alloc[PartSuppData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    new_val._6 = params._4
    new_val._7 = params._5
    new_val._8 = params._6._1
    new_val._9 = params._6
    put_PartSupp_99573ba(new_val)
    get_PartSupp_65260627(new_val._1)
  }
  def init_Supplier_2b62f0e1(
      params: Tuple7[Long, Str, Str, Nation, Str, Double, Str]
  )(implicit zone: Zone): Supplier = {
    val new_val = alloc[SupplierData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    new_val._8 = params._5._1
    new_val._9 = params._5
    new_val._10 = params._6
    new_val._11 = params._7._1
    new_val._12 = params._7
    put_Supplier_28a6ca81(new_val)
    get_Supplier_8bd99e1(new_val._1)
  }
  def init_Part_516ccbbb(
      params: Tuple9[Long, Str, Str, Str, Str, Int, Str, Double, Str]
  )(implicit zone: Zone): Part = {
    val new_val = alloc[PartData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    new_val._8 = params._5._1
    new_val._9 = params._5
    new_val._10 = params._6
    new_val._11 = params._7._1
    new_val._12 = params._7
    new_val._13 = params._8
    new_val._14 = params._9._1
    new_val._15 = params._9
    put_Part_bef21ce(new_val)
    get_Part_3d54efd9(new_val._1)
  }
  def init_Region_54c10d6d(
      params: Tuple3[Long, Str, Str]
  )(implicit zone: Zone): Region = {
    val new_val = alloc[RegionData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    put_Region_589a4153(new_val)
    get_Region_385a8b80(new_val._1)
  }
  def init_Nation_4b258f82(
      params: Tuple4[Long, Str, Region, Str]
  )(implicit zone: Zone): Nation = {
    val new_val = alloc[NationData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Nation_4fb3ddb6(new_val)
    get_Nation_78e4b8a3(new_val._1)
  }
  def init_Str_1191023f(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_324f6081.getNextKey
    new_val._2 = param
    put_Str_5ab56f0a(new_val)
    get_Str_120d8508(new_val._1)
  }

  def charAt_308365f5(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_1c8b10f7(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_c_acctbal_17643d60(
      this_3ad62e70: Customer
  )(implicit zone: Zone): Double = {
    this_3ad62e70._12
  }
  def get_c_address_2180bf75(
      this_3ad62e70: Customer
  )(implicit zone: Zone): Str = {
    if (this_3ad62e70._9 == null) {
      this_3ad62e70._9 = get_Str_120d8508(this_3ad62e70._8)
    }
    this_3ad62e70._9
  }
  def get_c_comment_5499c346(
      this_3ad62e70: Customer
  )(implicit zone: Zone): Str = {
    if (this_3ad62e70._14 == null) {
      this_3ad62e70._14 = get_Str_120d8508(this_3ad62e70._13)
    }
    this_3ad62e70._14
  }
  def get_c_mktsegment_5abb04ec(
      this_3ad62e70: Customer
  )(implicit zone: Zone): Str = {
    if (this_3ad62e70._3 == null) {
      this_3ad62e70._3 = get_Str_120d8508(this_3ad62e70._2)
    }
    this_3ad62e70._3
  }
  def get_c_name_6c02ade6(this_3ad62e70: Customer)(implicit zone: Zone): Str = {
    if (this_3ad62e70._7 == null) {
      this_3ad62e70._7 = get_Str_120d8508(this_3ad62e70._6)
    }
    this_3ad62e70._7
  }
  def get_c_nation_48dddc04(
      this_3ad62e70: Customer
  )(implicit zone: Zone): Nation = {
    if (this_3ad62e70._5 == null) {
      this_3ad62e70._5 = get_Nation_78e4b8a3(this_3ad62e70._4)
    }
    this_3ad62e70._5
  }
  def get_c_phone_52c4a5aa(
      this_3ad62e70: Customer
  )(implicit zone: Zone): Str = {
    if (this_3ad62e70._11 == null) {
      this_3ad62e70._11 = get_Str_120d8508(this_3ad62e70._10)
    }
    this_3ad62e70._11
  }
  def get_key_29cead0a(this_505a477a: Lineitem)(implicit zone: Zone): Long = {
    this_505a477a._1
  }
  def get_l_comment_f5569cb(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._26 == null) {
      this_505a477a._26 = get_Str_120d8508(this_505a477a._25)
    }
    this_505a477a._26
  }
  def get_l_commitdate_784b647e(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._18 == null) {
      this_505a477a._18 = get_Str_120d8508(this_505a477a._17)
    }
    this_505a477a._18
  }
  def get_l_discount_7a289d9a(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Double = {
    this_505a477a._9
  }
  def get_l_extendedprice_26d2c928(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Double = {
    this_505a477a._8
  }
  def get_l_linenumber_2ede708(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Long = {
    this_505a477a._6
  }
  def get_l_linestatus_21a61bbf(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._14 == null) {
      this_505a477a._14 = get_Str_120d8508(this_505a477a._13)
    }
    this_505a477a._14
  }
  def get_l_order_1f5c965c(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Order = {
    if (this_505a477a._3 == null) {
      this_505a477a._3 = get_Order_460228(this_505a477a._2)
    }
    this_505a477a._3
  }
  def get_l_partsupp_2f778716(
      this_505a477a: Lineitem
  )(implicit zone: Zone): PartSupp = {
    if (this_505a477a._5 == null) {
      this_505a477a._5 = get_PartSupp_65260627(this_505a477a._4)
    }
    this_505a477a._5
  }
  def get_l_quantity_42cfe057(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Long = {
    this_505a477a._7
  }
  def get_l_receiptdate_53a56052(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._20 == null) {
      this_505a477a._20 = get_Str_120d8508(this_505a477a._19)
    }
    this_505a477a._20
  }
  def get_l_returnflag_28b0e20c(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._12 == null) {
      this_505a477a._12 = get_Str_120d8508(this_505a477a._11)
    }
    this_505a477a._12
  }
  def get_l_shipdate_3bec9f3b(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._16 == null) {
      this_505a477a._16 = get_Str_120d8508(this_505a477a._15)
    }
    this_505a477a._16
  }
  def get_l_shipinstruct_69c0d49(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._22 == null) {
      this_505a477a._22 = get_Str_120d8508(this_505a477a._21)
    }
    this_505a477a._22
  }
  def get_l_shipmode_7ec34a38(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_505a477a._24 == null) {
      this_505a477a._24 = get_Str_120d8508(this_505a477a._23)
    }
    this_505a477a._24
  }
  def get_l_tax_51cc676e(
      this_505a477a: Lineitem
  )(implicit zone: Zone): Double = {
    this_505a477a._10
  }
  def get_n_comment_68168df7(this_e862f2b: Nation)(implicit zone: Zone): Str = {
    if (this_e862f2b._7 == null) {
      this_e862f2b._7 = get_Str_120d8508(this_e862f2b._6)
    }
    this_e862f2b._7
  }
  def get_n_name_ce08847(this_e862f2b: Nation)(implicit zone: Zone): Str = {
    if (this_e862f2b._3 == null) {
      this_e862f2b._3 = get_Str_120d8508(this_e862f2b._2)
    }
    this_e862f2b._3
  }
  def get_n_region_3ea579a2(
      this_e862f2b: Nation
  )(implicit zone: Zone): Region = {
    if (this_e862f2b._5 == null) {
      this_e862f2b._5 = get_Region_385a8b80(this_e862f2b._4)
    }
    this_e862f2b._5
  }
  def get_o_clerk_5dc0aad4(this_1e5f62a: Order)(implicit zone: Zone): Str = {
    if (this_1e5f62a._12 == null) {
      this_1e5f62a._12 = get_Str_120d8508(this_1e5f62a._11)
    }
    this_1e5f62a._12
  }
  def get_o_comment_55eaa39b(this_1e5f62a: Order)(implicit zone: Zone): Str = {
    if (this_1e5f62a._15 == null) {
      this_1e5f62a._15 = get_Str_120d8508(this_1e5f62a._14)
    }
    this_1e5f62a._15
  }
  def get_o_cust_4097a8ed(
      this_1e5f62a: Order
  )(implicit zone: Zone): Customer = {
    if (this_1e5f62a._3 == null) {
      this_1e5f62a._3 = get_Customer_6dfa0aab(this_1e5f62a._2)
    }
    this_1e5f62a._3
  }
  def get_o_orderdate_6e6d5273(
      this_1e5f62a: Order
  )(implicit zone: Zone): Str = {
    if (this_1e5f62a._8 == null) {
      this_1e5f62a._8 = get_Str_120d8508(this_1e5f62a._7)
    }
    this_1e5f62a._8
  }
  def get_o_orderpriority_3623d360(
      this_1e5f62a: Order
  )(implicit zone: Zone): Str = {
    if (this_1e5f62a._10 == null) {
      this_1e5f62a._10 = get_Str_120d8508(this_1e5f62a._9)
    }
    this_1e5f62a._10
  }
  def get_o_orderstatus_36d55844(
      this_1e5f62a: Order
  )(implicit zone: Zone): Str = {
    if (this_1e5f62a._5 == null) {
      this_1e5f62a._5 = get_Str_120d8508(this_1e5f62a._4)
    }
    this_1e5f62a._5
  }
  def get_o_shippriority_4bf84eb3(
      this_1e5f62a: Order
  )(implicit zone: Zone): Int = {
    this_1e5f62a._13
  }
  def get_o_totalprice_4f826375(
      this_1e5f62a: Order
  )(implicit zone: Zone): Double = {
    this_1e5f62a._6
  }
  def get_p_brand_7b9962e8(this_67120136: Part)(implicit zone: Zone): Str = {
    if (this_67120136._7 == null) {
      this_67120136._7 = get_Str_120d8508(this_67120136._6)
    }
    this_67120136._7
  }
  def get_p_comment_721d26fe(this_67120136: Part)(implicit zone: Zone): Str = {
    if (this_67120136._15 == null) {
      this_67120136._15 = get_Str_120d8508(this_67120136._14)
    }
    this_67120136._15
  }
  def get_p_container_2ed86a03(
      this_67120136: Part
  )(implicit zone: Zone): Str = {
    if (this_67120136._12 == null) {
      this_67120136._12 = get_Str_120d8508(this_67120136._11)
    }
    this_67120136._12
  }
  def get_p_mfgr_5934d55f(this_67120136: Part)(implicit zone: Zone): Str = {
    if (this_67120136._5 == null) {
      this_67120136._5 = get_Str_120d8508(this_67120136._4)
    }
    this_67120136._5
  }
  def get_p_name_698a72b3(this_67120136: Part)(implicit zone: Zone): Str = {
    if (this_67120136._3 == null) {
      this_67120136._3 = get_Str_120d8508(this_67120136._2)
    }
    this_67120136._3
  }
  def get_p_retailprice_72bb83aa(
      this_67120136: Part
  )(implicit zone: Zone): Double = {
    this_67120136._13
  }
  def get_p_size_1f5e778c(this_67120136: Part)(implicit zone: Zone): Int = {
    this_67120136._10
  }
  def get_p_type_603fc715(this_67120136: Part)(implicit zone: Zone): Str = {
    if (this_67120136._9 == null) {
      this_67120136._9 = get_Str_120d8508(this_67120136._8)
    }
    this_67120136._9
  }
  def get_ps_availqty_74124071(
      this_5a1a2e4b: PartSupp
  )(implicit zone: Zone): Int = {
    this_5a1a2e4b._6
  }
  def get_ps_comment_181cbb68(
      this_5a1a2e4b: PartSupp
  )(implicit zone: Zone): Str = {
    if (this_5a1a2e4b._9 == null) {
      this_5a1a2e4b._9 = get_Str_120d8508(this_5a1a2e4b._8)
    }
    this_5a1a2e4b._9
  }
  def get_ps_part_605c011(
      this_5a1a2e4b: PartSupp
  )(implicit zone: Zone): Part = {
    if (this_5a1a2e4b._3 == null) {
      this_5a1a2e4b._3 = get_Part_3d54efd9(this_5a1a2e4b._2)
    }
    this_5a1a2e4b._3
  }
  def get_ps_supp_d66ceec(
      this_5a1a2e4b: PartSupp
  )(implicit zone: Zone): Supplier = {
    if (this_5a1a2e4b._5 == null) {
      this_5a1a2e4b._5 = get_Supplier_8bd99e1(this_5a1a2e4b._4)
    }
    this_5a1a2e4b._5
  }
  def get_ps_supplycost_6b9106f9(
      this_5a1a2e4b: PartSupp
  )(implicit zone: Zone): Double = {
    this_5a1a2e4b._7
  }
  def get_r_comment_3e867884(
      this_162b3718: Region
  )(implicit zone: Zone): Str = {
    if (this_162b3718._5 == null) {
      this_162b3718._5 = get_Str_120d8508(this_162b3718._4)
    }
    this_162b3718._5
  }
  def get_r_name_52cd016(this_162b3718: Region)(implicit zone: Zone): Str = {
    if (this_162b3718._3 == null) {
      this_162b3718._3 = get_Str_120d8508(this_162b3718._2)
    }
    this_162b3718._3
  }
  def get_s_acctbal_29b5c0a6(
      this_6669b884: Supplier
  )(implicit zone: Zone): Double = {
    this_6669b884._10
  }
  def get_s_address_247acf4c(
      this_6669b884: Supplier
  )(implicit zone: Zone): Str = {
    if (this_6669b884._5 == null) {
      this_6669b884._5 = get_Str_120d8508(this_6669b884._4)
    }
    this_6669b884._5
  }
  def get_s_comment_47b507b5(
      this_6669b884: Supplier
  )(implicit zone: Zone): Str = {
    if (this_6669b884._12 == null) {
      this_6669b884._12 = get_Str_120d8508(this_6669b884._11)
    }
    this_6669b884._12
  }
  def get_s_name_6cb33a5f(this_6669b884: Supplier)(implicit zone: Zone): Str = {
    if (this_6669b884._3 == null) {
      this_6669b884._3 = get_Str_120d8508(this_6669b884._2)
    }
    this_6669b884._3
  }
  def get_s_nation_6b04679f(
      this_6669b884: Supplier
  )(implicit zone: Zone): Nation = {
    if (this_6669b884._7 == null) {
      this_6669b884._7 = get_Nation_78e4b8a3(this_6669b884._6)
    }
    this_6669b884._7
  }
  def get_s_phone_19f6d51c(
      this_6669b884: Supplier
  )(implicit zone: Zone): Str = {
    if (this_6669b884._9 == null) {
      this_6669b884._9 = get_Str_120d8508(this_6669b884._8)
    }
    this_6669b884._9
  }
  def get_string_3854c77(this_7691f138: Str)(implicit zone: Zone): String = {
    fromCString_97b763e(this_7691f138._2)
  }

  def set_c_acctbal_50e3c5b7(this_3ad62e70: Customer, c_acctbal: Double)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._12 = c_acctbal
  }
  def set_c_address_205eedc2(this_3ad62e70: Customer, c_address: Str)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._8 = c_address._1
    this_3ad62e70._9 = c_address
  }
  def set_c_comment_41faa1b2(this_3ad62e70: Customer, c_comment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._13 = c_comment._1
    this_3ad62e70._14 = c_comment
  }
  def set_c_mktsegment_ffa0a47(this_3ad62e70: Customer, c_mktsegment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._2 = c_mktsegment._1
    this_3ad62e70._3 = c_mktsegment
  }
  def set_c_name_77acbd52(this_3ad62e70: Customer, c_name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._6 = c_name._1
    this_3ad62e70._7 = c_name
  }
  def set_c_nation_20cb7e98(this_3ad62e70: Customer, c_nation: Nation)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._4 = c_nation._1
    this_3ad62e70._5 = c_nation
  }
  def set_c_phone_3428bc02(this_3ad62e70: Customer, c_phone: Str)(
      implicit zone: Zone
  ): Unit = {
    this_3ad62e70._10 = c_phone._1
    this_3ad62e70._11 = c_phone
  }

  def get_key_part_supp(key_part: Long, key_supp: Long): Long = key_part * 10000L + key_supp // There are 10'000 suppliers
  def get_key_line_item(key_order: Long, linenumber: Long): Long = key_order * 7L + linenumber // There are 7 line numbers

  def insertCustomers(from: Int, to: Int): Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/customer.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines().drop(from).take(to - from);
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = toCString_51100e73(x_6);
        val x_8 = init_Str_1191023f(x_7);
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toLong;
        val x_12 = Nation_table_351048d2.get(x_11);
        val x_13 = empty_pointers_Nation_15118928(x_12);
        val x_14 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(3);
        val x_15 = toCString_51100e73(x_14);
        val x_16 = init_Str_1191023f(x_15);
        val x_17 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(4);
        val x_18 = toCString_51100e73(x_17);
        val x_19 = init_Str_1191023f(x_18);
        val x_20 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(5);
        val x_21 = toCString_51100e73(x_20);
        val x_22 = init_Str_1191023f(x_21);
        val x_23 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(6);
        val x_24 = scala.Predef.augmentString(x_23);
        val x_25 = x_24.toDouble;
        val x_26 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(7);
        val x_27 = toCString_51100e73(x_26);
        val x_28 = init_Str_1191023f(x_27);
        val x_29 = init_Customer_6151c55b(
          scala.Tuple8(x_5, x_8, x_13, x_16, x_19, x_22, x_25, x_28)
        );
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertLineitems(from: Int, to: Int): Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/lineitem.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines().drop(from).take(to - from);
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val line = line_2.substring(1, line_2.length() - 1)
        val fields = line.split("\',\'")
        val x_3 = fields(0);
        val x_4 = toCString_51100e73(x_3);
        val x_5 = init_Str_1191023f(x_4);
        val x_6 = fields(1);
        val x_7 = scala.Predef.augmentString(x_6);
        val x_8 = x_7.toLong;
        val x_9 = fields(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toDouble;
        val x_12 = fields(3);
        val x_13 = scala.Predef.augmentString(x_12);
        val x_14 = x_13.toDouble;
        val x_15 = fields(4);
        val x_16 = scala.Predef.augmentString(x_15);
        val x_17 = x_16.toLong;
        val x_18 = fields(5);
        val x_19 = scala.Predef.augmentString(x_18);
        val x_20 = x_19.toLong;
        val x_21 = fields(6);
        val x_22 = toCString_51100e73(x_21);
        val x_23 = init_Str_1191023f(x_22);
        val x_24 = fields(7);
        val x_25 = scala.Predef.augmentString(x_24);
        val x_26 = x_25.toLong;
        val x_27 = fields(8);
        val x_28 = toCString_51100e73(x_27);
        val x_29 = init_Str_1191023f(x_28);
        val x_30 = fields(9);
        val x_31 = scala.Predef.augmentString(x_30);
        val x_32 = x_31.toDouble;
        val x_33 = fields(10);
        val x_34 = toCString_51100e73(x_33);
        val x_35 = init_Str_1191023f(x_34);
        val x_36 = fields(11);
        val x_37 = toCString_51100e73(x_36);
        val x_38 = init_Str_1191023f(x_37);
        val x_39 = fields(12);
        val x_40 = toCString_51100e73(x_39);
        val x_41 = init_Str_1191023f(x_40);
        val x_42 = fields(13);
        val x_43 = scala.Predef.augmentString(x_42);
        val x_44 = x_43.toLong;
        val x_48 = fields(14);
        val x_49 = toCString_51100e73(x_48);
        val x_50 = init_Str_1191023f(x_49);
        val x_51 = fields(15);
        val x_52 = toCString_51100e73(x_51);
        val x_53 = init_Str_1191023f(x_52);
        val key_54 = get_key_line_item(x_8, x_44);
        val key_55 = get_key_part_supp(x_26, x_17);
        val x_56 = PartSupp_table_1edfac6f.get(key_55);
        val x_57 = empty_pointers_PartSupp_45305074(x_56);
        val x_58 = Order_table_1dc4326.get(x_8);
        val x_59 = empty_pointers_Order_54bf3855(x_58);
        val l_60 = init_Lineitem_67f9e7f6(
          key_54,
          x_59,
          x_57,
          x_44,
          x_20,
          x_14,
          x_11,
          x_32,
          x_23,
          x_29,
          x_5,
          x_35,
          x_38,
          x_50,
          x_41,
          x_53
        );
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertNations: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/nation.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = toCString_51100e73(x_6);
        val x_8 = init_Str_1191023f(x_7);
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toLong;
        val x_12 = Region_table_29be21be.get(x_11);
        val x_13 = empty_pointers_Region_5c71d933(x_12);
        val x_14 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(3);
        val x_15 = toCString_51100e73(x_14);
        val x_16 = init_Str_1191023f(x_15);
        val x_17 = init_Nation_4b258f82(scala.Tuple4(x_5, x_8, x_13, x_16));
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertOrders(from: Int, to: Int): Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/orders.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines().drop(from).take(to - from);
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = toCString_51100e73(x_3);
        val x_5 = init_Str_1191023f(x_4);
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = scala.Predef.augmentString(x_6);
        val x_8 = x_7.toLong;
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toLong;
        val x_12 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(3);
        val x_13 = toCString_51100e73(x_12);
        val x_14 = init_Str_1191023f(x_13);
        val x_15 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(4);
        val x_16 = scala.Predef.augmentString(x_15);
        val x_17 = x_16.toInt;
        val x_18 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(5);
        val x_19 = toCString_51100e73(x_18);
        val x_20 = init_Str_1191023f(x_19);
        val x_21 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(6);
        val x_22 = toCString_51100e73(x_21);
        val x_23 = init_Str_1191023f(x_22);
        val x_24 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(7);
        val x_25 = scala.Predef.augmentString(x_24);
        val x_26 = x_25.toDouble;
        val x_27 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(8);
        val x_28 = toCString_51100e73(x_27);
        val x_29 = init_Str_1191023f(x_28);
        val x_30 = Customer_table_51492a90.get(x_11);
        val x_31 = empty_pointers_Customer_4d539bc2(x_30);
        val o_32 = init_Order_5b418f3f(
          scala.Tuple9(x_8, x_31, x_23, x_26, x_5, x_14, x_20, x_17, x_29)
        );
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertPartSupps(from: Int, to: Int): Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/partsupp.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines().drop(from).take(to - from);
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = scala.Predef.augmentString(x_6);
        val x_8 = x_7.toLong;
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toDouble;
        val x_12 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(3);
        val x_13 = scala.Predef.augmentString(x_12);
        val x_14 = x_13.toInt;
        val x_15 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(4);
        val x_16 = toCString_51100e73(x_15);
        val x_17 = init_Str_1191023f(x_16);
        val key_18 = get_key_part_supp(x_5, x_8);
        val x_19 = Part_table_348994ca.get(x_5);
        val x_20 = empty_pointers_Part_563248b(x_19);
        val x_21 = Supplier_table_4a9892dc.get(x_8);
        val x_22 = empty_pointers_Supplier_71212f78(x_21);
        val ps_23 = init_PartSupp_4cec652c(
          scala.Tuple6(key_18, x_20, x_22, x_14, x_11, x_17)
        );
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertParts(from: Int, to: Int): Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/part.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines().drop(from).take(to - from);
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = toCString_51100e73(x_6);
        val x_8 = init_Str_1191023f(x_7);
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toInt;
        val x_12 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(3);
        val x_13 = toCString_51100e73(x_12);
        val x_14 = init_Str_1191023f(x_13);
        val x_15 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(4);
        val x_16 = toCString_51100e73(x_15);
        val x_17 = init_Str_1191023f(x_16);
        val x_18 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(5);
        val x_19 = toCString_51100e73(x_18);
        val x_20 = init_Str_1191023f(x_19);
        val x_21 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(6);
        val x_22 = toCString_51100e73(x_21);
        val x_23 = init_Str_1191023f(x_22);
        val x_24 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(7);
        val x_25 = scala.Predef.augmentString(x_24);
        val x_26 = x_25.toDouble;
        val x_27 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(8);
        val x_28 = toCString_51100e73(x_27);
        val x_29 = init_Str_1191023f(x_28);
        val p_30 = init_Part_516ccbbb(
          scala.Tuple9(x_5, x_17, x_23, x_14, x_8, x_11, x_20, x_26, x_29)
        );
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertRegions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/region.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = toCString_51100e73(x_6);
        val x_8 = init_Str_1191023f(x_7);
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = toCString_51100e73(x_9);
        val x_11 = init_Str_1191023f(x_10);
        val x_12 = init_Region_54c10d6d(scala.Tuple3(x_5, x_8, x_11));
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def insertSuppliers: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./tpch/supplier.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[scala.Unit](((line_2: java.lang.String) => {
        val x_3 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(1);
        val x_7 = scala.Predef.augmentString(x_6);
        val x_8 = x_7.toLong;
        val x_9 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(2);
        val x_10 = toCString_51100e73(x_9);
        val x_11 = init_Str_1191023f(x_10);
        val x_12 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(3);
        val x_13 = toCString_51100e73(x_12);
        val x_14 = init_Str_1191023f(x_13);
        val x_15 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(4);
        val x_16 = toCString_51100e73(x_15);
        val x_17 = init_Str_1191023f(x_16);
        val x_18 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(5);
        val x_19 = toCString_51100e73(x_18);
        val x_20 = init_Str_1191023f(x_19);
        val x_21 = line_2.substring(1, line_2.length().-(1)).split("\',\'")(6);
        val x_22 = scala.Predef.augmentString(x_21);
        val x_23 = x_22.toDouble;
        val x_24 = Nation_table_351048d2.get(x_8);
        val x_25 = empty_pointers_Nation_15118928(x_24);
        val s_26 = init_Supplier_2b62f0e1(
          scala.Tuple7(x_5, x_14, x_17, x_25, x_20, x_23, x_11)
        );
        ()
      }));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
  def printAllSizes: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_324f6081.dbiOpen();
      Nation_table_351048d2.dbiOpen();
      Region_table_29be21be.dbiOpen();
      Part_table_348994ca.dbiOpen();
      Supplier_table_4a9892dc.dbiOpen();
      PartSupp_table_1edfac6f.dbiOpen();
      Customer_table_51492a90.dbiOpen();
      Order_table_1dc4326.dbiOpen();
      Lineitem_table_75ebe2e6.dbiOpen();
      val x_0 = Region_table_29be21be.size;
      scala.Predef.println("Number of regions: ".+(x_0));
      val x_1 = Nation_table_351048d2.size;
      scala.Predef.println("Number of nations: ".+(x_1));
      val x_2 = Part_table_348994ca.size;
      scala.Predef.println("Number of parts: ".+(x_2));
      val x_3 = Supplier_table_4a9892dc.size;
      scala.Predef.println("Number of suppliers: ".+(x_3));
      val x_4 = PartSupp_table_1edfac6f.size;
      scala.Predef.println("Number of part suppliers: ".+(x_4));
      val x_5 = Customer_table_51492a90.size;
      scala.Predef.println("Number of customers: ".+(x_5));
      val x_6 = Order_table_1dc4326.size;
      scala.Predef.println("Number of orders: ".+(x_6));
      val x_7 = Lineitem_table_75ebe2e6.size;
      scala.Predef.println("Number of lineitems: ".+(x_7));
      val x_8 = Str_table_324f6081.size;
      scala.Predef.println("Number of str's: ".+(x_8));
      LMDBTable.txnCommit();
      Str_table_324f6081.dbiClose();
      Nation_table_351048d2.dbiClose();
      Region_table_29be21be.dbiClose();
      Part_table_348994ca.dbiClose();
      Supplier_table_4a9892dc.dbiClose();
      PartSupp_table_1edfac6f.dbiClose();
      Customer_table_51492a90.dbiClose();
      Order_table_1dc4326.dbiClose();
      Lineitem_table_75ebe2e6.dbiClose()
    }
  }
}

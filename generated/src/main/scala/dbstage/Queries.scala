package main

import scala.collection.mutable
import scala.scalanative.unsafe._
import lib.string._
import lib.str._
import lib.LMDBTable
import lib.LMDBTable._

object Queries {
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
  lazy val Customer_table_649072f0 = new LMDBTable[Customer]("Customer")

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
  lazy val Lineitem_table_2e1b204d = new LMDBTable[Lineitem]("Lineitem")

  type NationData = CStruct7[Long, Long, Str, Long, Region, Long, Str]
  type Nation = Ptr[NationData]
  val sizeNation = sizeof[NationData]
  lazy val Nation_table_6ea0e4d4 = new LMDBTable[Nation]("Nation")

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
  lazy val Order_table_254c6d2e = new LMDBTable[Order]("Order")

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
  lazy val Part_table_1ba452ca = new LMDBTable[Part]("Part")

  type PartSuppData =
    CStruct9[Long, Long, Part, Long, Supplier, Int, Double, Long, Str]
  type PartSupp = Ptr[PartSuppData]
  val sizePartSupp = sizeof[PartSuppData]
  lazy val PartSupp_table_6cca10bb = new LMDBTable[PartSupp]("PartSupp")

  type RegionData = CStruct5[Long, Long, Str, Long, Str]
  type Region = Ptr[RegionData]
  val sizeRegion = sizeof[RegionData]
  lazy val Region_table_5a5cfb2 = new LMDBTable[Region]("Region")

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
  lazy val Supplier_table_4346f735 = new LMDBTable[Supplier]("Supplier")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_7acddf95 = new LMDBTable[CString]("Str")

  def toCString_463e57bf(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_6dcdc895(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Customer_3a5ef3ef(value: Customer): Customer = {
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
  def empty_pointers_Lineitem_1018cda7(value: Lineitem): Lineitem = {
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
  def empty_pointers_Nation_46b68e8(value: Nation): Nation = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
    }
    value
  }
  def empty_pointers_Order_1d4f58c8(value: Order): Order = {
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
  def empty_pointers_Part_50c31105(value: Part): Part = {
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
  def empty_pointers_PartSupp_6e09b4f(value: PartSupp): PartSupp = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._9 = null
    }
    value
  }
  def empty_pointers_Region_59139f53(value: Region): Region = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Supplier_3efd15a1(value: Supplier): Supplier = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
      value._9 = null
      value._12 = null
    }
    value
  }

  def get_Customer_76fe7042(key: Long)(implicit zone: Zone): Customer = {
    val value = Customer_table_649072f0.get(key)
    empty_pointers_Customer_3a5ef3ef(value)
  }
  def get_Lineitem_74c6b731(key: Long)(implicit zone: Zone): Lineitem = {
    val value = Lineitem_table_2e1b204d.get(key)
    empty_pointers_Lineitem_1018cda7(value)
  }
  def get_Nation_316a47fd(key: Long)(implicit zone: Zone): Nation = {
    val value = Nation_table_6ea0e4d4.get(key)
    empty_pointers_Nation_46b68e8(value)
  }
  def get_Order_676c1c6c(key: Long)(implicit zone: Zone): Order = {
    val value = Order_table_254c6d2e.get(key)
    empty_pointers_Order_1d4f58c8(value)
  }
  def get_Part_62151ca3(key: Long)(implicit zone: Zone): Part = {
    val value = Part_table_1ba452ca.get(key)
    empty_pointers_Part_50c31105(value)
  }
  def get_PartSupp_23ab40ef(key: Long)(implicit zone: Zone): PartSupp = {
    val value = PartSupp_table_6cca10bb.get(key)
    empty_pointers_PartSupp_6e09b4f(value)
  }
  def get_Region_401dbec8(key: Long)(implicit zone: Zone): Region = {
    val value = Region_table_5a5cfb2.get(key)
    empty_pointers_Region_59139f53(value)
  }
  def get_Supplier_471555db(key: Long)(implicit zone: Zone): Supplier = {
    val value = Supplier_table_4346f735.get(key)
    empty_pointers_Supplier_3efd15a1(value)
  }

  def get_Str_dafa99e(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_7acddf95.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Customer_747a3cfc(data_el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_649072f0.put(
      data_el._1,
      sizeCustomer,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Lineitem_59bc2745(data_el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_2e1b204d.put(
      data_el._1,
      sizeLineitem,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Nation_34ea1a24(data_el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_6ea0e4d4.put(
      data_el._1,
      sizeNation,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Order_4479b67e(data_el: Order)(implicit zone: Zone): Unit = {
    Order_table_254c6d2e.put(
      data_el._1,
      sizeOrder,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Part_6141efd1(data_el: Part)(implicit zone: Zone): Unit = {
    Part_table_1ba452ca.put(
      data_el._1,
      sizePart,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_PartSupp_597f6ab3(data_el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_6cca10bb.put(
      data_el._1,
      sizePartSupp,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Region_3da07082(data_el: Region)(implicit zone: Zone): Unit = {
    Region_table_5a5cfb2.put(
      data_el._1,
      sizeRegion,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Supplier_3463fb0e(data_el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_4346f735.put(
      data_el._1,
      sizeSupplier,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_5a32a534(str: Str)(implicit zone: Zone): Unit = {
    Str_table_7acddf95.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Customer_6db00597(el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_649072f0.delete(el._1)
  }
  def delete_Lineitem_22641610(el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_2e1b204d.delete(el._1)
  }
  def delete_Nation_9446781(el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_6ea0e4d4.delete(el._1)
  }
  def delete_Order_4ecd25f2(el: Order)(implicit zone: Zone): Unit = {
    Order_table_254c6d2e.delete(el._1)
  }
  def delete_Part_23bc0d01(el: Part)(implicit zone: Zone): Unit = {
    Part_table_1ba452ca.delete(el._1)
  }
  def delete_PartSupp_21ea5bf0(el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_6cca10bb.delete(el._1)
  }
  def delete_Region_bb84823(el: Region)(implicit zone: Zone): Unit = {
    Region_table_5a5cfb2.delete(el._1)
  }
  def delete_Str_2c7d1e13(el: Str)(implicit zone: Zone): Unit = {
    Str_table_7acddf95.delete(el._1)
  }
  def delete_Supplier_6feaca98(el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_4346f735.delete(el._1)
  }

  def charAt_18ded961(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strcmp_7efe36d5(params: Tuple2[Str, CString])(implicit zone: Zone): Int =
    strcmp(params._1._2, params._2)
  def strlen_4a62178e(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_c_acctbal_3c445c7f(
      this_afe0b59: Customer
  )(implicit zone: Zone): Double = {
    this_afe0b59._12
  }
  def get_c_address_1d85efc0(
      this_afe0b59: Customer
  )(implicit zone: Zone): Str = {
    if (this_afe0b59._9 == null) {
      this_afe0b59._9 = get_Str_dafa99e(this_afe0b59._8)
    }
    this_afe0b59._9
  }
  def get_c_comment_11768aa6(
      this_afe0b59: Customer
  )(implicit zone: Zone): Str = {
    if (this_afe0b59._14 == null) {
      this_afe0b59._14 = get_Str_dafa99e(this_afe0b59._13)
    }
    this_afe0b59._14
  }
  def get_c_mktsegment_144dfce9(
      this_afe0b59: Customer
  )(implicit zone: Zone): Str = {
    if (this_afe0b59._3 == null) {
      this_afe0b59._3 = get_Str_dafa99e(this_afe0b59._2)
    }
    this_afe0b59._3
  }
  def get_c_name_af4edbb(this_afe0b59: Customer)(implicit zone: Zone): Str = {
    if (this_afe0b59._7 == null) {
      this_afe0b59._7 = get_Str_dafa99e(this_afe0b59._6)
    }
    this_afe0b59._7
  }
  def get_c_nation_383de8ca(
      this_afe0b59: Customer
  )(implicit zone: Zone): Nation = {
    if (this_afe0b59._5 == null) {
      this_afe0b59._5 = get_Nation_316a47fd(this_afe0b59._4)
    }
    this_afe0b59._5
  }
  def get_c_phone_1dbb66d1(this_afe0b59: Customer)(implicit zone: Zone): Str = {
    if (this_afe0b59._11 == null) {
      this_afe0b59._11 = get_Str_dafa99e(this_afe0b59._10)
    }
    this_afe0b59._11
  }
  def get_key_4b5ecb68(this_66ea7b3b: Lineitem)(implicit zone: Zone): Long = {
    this_66ea7b3b._1
  }
  def get_l_comment_1c721171(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._26 == null) {
      this_66ea7b3b._26 = get_Str_dafa99e(this_66ea7b3b._25)
    }
    this_66ea7b3b._26
  }
  def get_l_commitdate_5a3961c0(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._18 == null) {
      this_66ea7b3b._18 = get_Str_dafa99e(this_66ea7b3b._17)
    }
    this_66ea7b3b._18
  }
  def get_l_discount_6fbffa1f(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Double = {
    this_66ea7b3b._9
  }
  def get_l_extendedprice_5287690b(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Double = {
    this_66ea7b3b._8
  }
  def get_l_linenumber_5e7213b3(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Long = {
    this_66ea7b3b._6
  }
  def get_l_linestatus_4fd45edc(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._14 == null) {
      this_66ea7b3b._14 = get_Str_dafa99e(this_66ea7b3b._13)
    }
    this_66ea7b3b._14
  }
  def get_l_order_5fab3a3b(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Order = {
    if (this_66ea7b3b._3 == null) {
      this_66ea7b3b._3 = get_Order_676c1c6c(this_66ea7b3b._2)
    }
    this_66ea7b3b._3
  }
  def get_l_partsupp_368dcff4(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): PartSupp = {
    if (this_66ea7b3b._5 == null) {
      this_66ea7b3b._5 = get_PartSupp_23ab40ef(this_66ea7b3b._4)
    }
    this_66ea7b3b._5
  }
  def get_l_quantity_782cbd1d(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Long = {
    this_66ea7b3b._7
  }
  def get_l_receiptdate_15e33e7a(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._20 == null) {
      this_66ea7b3b._20 = get_Str_dafa99e(this_66ea7b3b._19)
    }
    this_66ea7b3b._20
  }
  def get_l_returnflag_139fa41f(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._12 == null) {
      this_66ea7b3b._12 = get_Str_dafa99e(this_66ea7b3b._11)
    }
    this_66ea7b3b._12
  }
  def get_l_shipdate_4bb53bf7(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._16 == null) {
      this_66ea7b3b._16 = get_Str_dafa99e(this_66ea7b3b._15)
    }
    this_66ea7b3b._16
  }
  def get_l_shipinstruct_56ecb3c9(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._22 == null) {
      this_66ea7b3b._22 = get_Str_dafa99e(this_66ea7b3b._21)
    }
    this_66ea7b3b._22
  }
  def get_l_shipmode_5af04780(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_66ea7b3b._24 == null) {
      this_66ea7b3b._24 = get_Str_dafa99e(this_66ea7b3b._23)
    }
    this_66ea7b3b._24
  }
  def get_l_tax_34a1af9d(
      this_66ea7b3b: Lineitem
  )(implicit zone: Zone): Double = {
    this_66ea7b3b._10
  }
  def get_n_comment_65e8c39a(
      this_56a9507d: Nation
  )(implicit zone: Zone): Str = {
    if (this_56a9507d._7 == null) {
      this_56a9507d._7 = get_Str_dafa99e(this_56a9507d._6)
    }
    this_56a9507d._7
  }
  def get_n_name_3dfc54e0(this_56a9507d: Nation)(implicit zone: Zone): Str = {
    if (this_56a9507d._3 == null) {
      this_56a9507d._3 = get_Str_dafa99e(this_56a9507d._2)
    }
    this_56a9507d._3
  }
  def get_n_region_498c985(
      this_56a9507d: Nation
  )(implicit zone: Zone): Region = {
    if (this_56a9507d._5 == null) {
      this_56a9507d._5 = get_Region_401dbec8(this_56a9507d._4)
    }
    this_56a9507d._5
  }
  def get_o_clerk_3b79af5f(this_182e4bd3: Order)(implicit zone: Zone): Str = {
    if (this_182e4bd3._12 == null) {
      this_182e4bd3._12 = get_Str_dafa99e(this_182e4bd3._11)
    }
    this_182e4bd3._12
  }
  def get_o_comment_64fdb784(this_182e4bd3: Order)(implicit zone: Zone): Str = {
    if (this_182e4bd3._15 == null) {
      this_182e4bd3._15 = get_Str_dafa99e(this_182e4bd3._14)
    }
    this_182e4bd3._15
  }
  def get_o_cust_266b8e7f(
      this_182e4bd3: Order
  )(implicit zone: Zone): Customer = {
    if (this_182e4bd3._3 == null) {
      this_182e4bd3._3 = get_Customer_76fe7042(this_182e4bd3._2)
    }
    this_182e4bd3._3
  }
  def get_o_orderdate_4c7129b2(
      this_182e4bd3: Order
  )(implicit zone: Zone): Str = {
    if (this_182e4bd3._8 == null) {
      this_182e4bd3._8 = get_Str_dafa99e(this_182e4bd3._7)
    }
    this_182e4bd3._8
  }
  def get_o_orderpriority_3a047434(
      this_182e4bd3: Order
  )(implicit zone: Zone): Str = {
    if (this_182e4bd3._10 == null) {
      this_182e4bd3._10 = get_Str_dafa99e(this_182e4bd3._9)
    }
    this_182e4bd3._10
  }
  def get_o_orderstatus_33cca567(
      this_182e4bd3: Order
  )(implicit zone: Zone): Str = {
    if (this_182e4bd3._5 == null) {
      this_182e4bd3._5 = get_Str_dafa99e(this_182e4bd3._4)
    }
    this_182e4bd3._5
  }
  def get_o_shippriority_49aedd17(
      this_182e4bd3: Order
  )(implicit zone: Zone): Int = {
    this_182e4bd3._13
  }
  def get_o_totalprice_c574129(
      this_182e4bd3: Order
  )(implicit zone: Zone): Double = {
    this_182e4bd3._6
  }
  def get_p_brand_43da4904(this_15eb3143: Part)(implicit zone: Zone): Str = {
    if (this_15eb3143._7 == null) {
      this_15eb3143._7 = get_Str_dafa99e(this_15eb3143._6)
    }
    this_15eb3143._7
  }
  def get_p_comment_7c23720d(this_15eb3143: Part)(implicit zone: Zone): Str = {
    if (this_15eb3143._15 == null) {
      this_15eb3143._15 = get_Str_dafa99e(this_15eb3143._14)
    }
    this_15eb3143._15
  }
  def get_p_container_4acb2aa9(
      this_15eb3143: Part
  )(implicit zone: Zone): Str = {
    if (this_15eb3143._12 == null) {
      this_15eb3143._12 = get_Str_dafa99e(this_15eb3143._11)
    }
    this_15eb3143._12
  }
  def get_p_mfgr_6e67b9a(this_15eb3143: Part)(implicit zone: Zone): Str = {
    if (this_15eb3143._5 == null) {
      this_15eb3143._5 = get_Str_dafa99e(this_15eb3143._4)
    }
    this_15eb3143._5
  }
  def get_p_name_1765902b(this_15eb3143: Part)(implicit zone: Zone): Str = {
    if (this_15eb3143._3 == null) {
      this_15eb3143._3 = get_Str_dafa99e(this_15eb3143._2)
    }
    this_15eb3143._3
  }
  def get_p_retailprice_70b12bd0(
      this_15eb3143: Part
  )(implicit zone: Zone): Double = {
    this_15eb3143._13
  }
  def get_p_size_63bcc90d(this_15eb3143: Part)(implicit zone: Zone): Int = {
    this_15eb3143._10
  }
  def get_p_type_5633544b(this_15eb3143: Part)(implicit zone: Zone): Str = {
    if (this_15eb3143._9 == null) {
      this_15eb3143._9 = get_Str_dafa99e(this_15eb3143._8)
    }
    this_15eb3143._9
  }
  def get_ps_availqty_14fa2bb8(
      this_6d6c626e: PartSupp
  )(implicit zone: Zone): Int = {
    this_6d6c626e._6
  }
  def get_ps_comment_118b7c8b(
      this_6d6c626e: PartSupp
  )(implicit zone: Zone): Str = {
    if (this_6d6c626e._9 == null) {
      this_6d6c626e._9 = get_Str_dafa99e(this_6d6c626e._8)
    }
    this_6d6c626e._9
  }
  def get_ps_part_5d56f3fd(
      this_6d6c626e: PartSupp
  )(implicit zone: Zone): Part = {
    if (this_6d6c626e._3 == null) {
      this_6d6c626e._3 = get_Part_62151ca3(this_6d6c626e._2)
    }
    this_6d6c626e._3
  }
  def get_ps_supp_5327852b(
      this_6d6c626e: PartSupp
  )(implicit zone: Zone): Supplier = {
    if (this_6d6c626e._5 == null) {
      this_6d6c626e._5 = get_Supplier_471555db(this_6d6c626e._4)
    }
    this_6d6c626e._5
  }
  def get_ps_supplycost_1645d31c(
      this_6d6c626e: PartSupp
  )(implicit zone: Zone): Double = {
    this_6d6c626e._7
  }
  def get_r_comment_36435301(
      this_5ce09a7b: Region
  )(implicit zone: Zone): Str = {
    if (this_5ce09a7b._5 == null) {
      this_5ce09a7b._5 = get_Str_dafa99e(this_5ce09a7b._4)
    }
    this_5ce09a7b._5
  }
  def get_r_name_59a2963f(this_5ce09a7b: Region)(implicit zone: Zone): Str = {
    if (this_5ce09a7b._3 == null) {
      this_5ce09a7b._3 = get_Str_dafa99e(this_5ce09a7b._2)
    }
    this_5ce09a7b._3
  }
  def get_s_acctbal_39ac0e35(
      this_61477324: Supplier
  )(implicit zone: Zone): Double = {
    this_61477324._10
  }
  def get_s_address_15db995a(
      this_61477324: Supplier
  )(implicit zone: Zone): Str = {
    if (this_61477324._5 == null) {
      this_61477324._5 = get_Str_dafa99e(this_61477324._4)
    }
    this_61477324._5
  }
  def get_s_comment_5885e6b0(
      this_61477324: Supplier
  )(implicit zone: Zone): Str = {
    if (this_61477324._12 == null) {
      this_61477324._12 = get_Str_dafa99e(this_61477324._11)
    }
    this_61477324._12
  }
  def get_s_name_20160d7(this_61477324: Supplier)(implicit zone: Zone): Str = {
    if (this_61477324._3 == null) {
      this_61477324._3 = get_Str_dafa99e(this_61477324._2)
    }
    this_61477324._3
  }
  def get_s_nation_5b051b04(
      this_61477324: Supplier
  )(implicit zone: Zone): Nation = {
    if (this_61477324._7 == null) {
      this_61477324._7 = get_Nation_316a47fd(this_61477324._6)
    }
    this_61477324._7
  }
  def get_s_phone_12dd5877(
      this_61477324: Supplier
  )(implicit zone: Zone): Str = {
    if (this_61477324._9 == null) {
      this_61477324._9 = get_Str_dafa99e(this_61477324._8)
    }
    this_61477324._9
  }
  def get_string_6c145f56(this_651a8cfa: Str)(implicit zone: Zone): String = {
    fromCString_6dcdc895(this_651a8cfa._2)
  }

  def set_c_acctbal_52e71dbc(this_afe0b59: Customer, c_acctbal: Double)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._12 = c_acctbal
  }
  def set_c_address_2916e96(this_afe0b59: Customer, c_address: Str)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._8 = c_address._1
    this_afe0b59._9 = c_address
  }
  def set_c_comment_1947b4fe(this_afe0b59: Customer, c_comment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._13 = c_comment._1
    this_afe0b59._14 = c_comment
  }
  def set_c_mktsegment_73274097(this_afe0b59: Customer, c_mktsegment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._2 = c_mktsegment._1
    this_afe0b59._3 = c_mktsegment
  }
  def set_c_name_30215d5f(this_afe0b59: Customer, c_name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._6 = c_name._1
    this_afe0b59._7 = c_name
  }
  def set_c_nation_39cd6013(this_afe0b59: Customer, c_nation: Nation)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._4 = c_nation._1
    this_afe0b59._5 = c_nation
  }
  def set_c_phone_1d09c390(this_afe0b59: Customer, c_phone: Str)(
      implicit zone: Zone
  ): Unit = {
    this_afe0b59._10 = c_phone._1
    this_afe0b59._11 = c_phone
  }

  def pq1: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7acddf95.dbiOpen();
      Nation_table_6ea0e4d4.dbiOpen();
      Region_table_5a5cfb2.dbiOpen();
      Part_table_1ba452ca.dbiOpen();
      Supplier_table_4346f735.dbiOpen();
      PartSupp_table_6cca10bb.dbiOpen();
      Customer_table_649072f0.dbiOpen();
      Order_table_254c6d2e.dbiOpen();
      Lineitem_table_2e1b204d.dbiOpen();
      var res_0: scala.Double = 0.0;
      val cursor_1 = Customer_table_649072f0.cursorOpen();
      val x_2 = Customer_table_649072f0.first(cursor_1);
      val x_3 = empty_pointers_Customer_3a5ef3ef(x_2);
      var v_4: Customer = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_c_acctbal_3c445c7f(row_6);
            res_0 = x_7.+(x_8);
            true
          })
      }) {
        val x_9 = Customer_table_649072f0.next(cursor_1);
        val x_10 = empty_pointers_Customer_3a5ef3ef(x_9);
        v_4 = x_10
      };
      Customer_table_649072f0.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7acddf95.dbiClose();
      Nation_table_6ea0e4d4.dbiClose();
      Region_table_5a5cfb2.dbiClose();
      Part_table_1ba452ca.dbiClose();
      Supplier_table_4346f735.dbiClose();
      PartSupp_table_6cca10bb.dbiClose();
      Customer_table_649072f0.dbiClose();
      Order_table_254c6d2e.dbiClose();
      Lineitem_table_2e1b204d.dbiClose();
      x_11
    }
  }

  def q6: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7acddf95.dbiOpen();
      Nation_table_6ea0e4d4.dbiOpen();
      Region_table_5a5cfb2.dbiOpen();
      Part_table_1ba452ca.dbiOpen();
      Supplier_table_4346f735.dbiOpen();
      PartSupp_table_6cca10bb.dbiOpen();
      Customer_table_649072f0.dbiOpen();
      Order_table_254c6d2e.dbiOpen();
      Lineitem_table_2e1b204d.dbiOpen();
      val start_date = toCString_463e57bf("1994-01-01")
      val end_date = toCString_463e57bf("1995-01-01")
      var res_0: scala.Double = 0.0;
      val cursor_1 = Lineitem_table_2e1b204d.cursorOpen();
      val x_2 = Lineitem_table_2e1b204d.last(cursor_1);
      val x_3 = empty_pointers_Lineitem_1018cda7(x_2);
      var v_4: Lineitem = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_l_quantity_782cbd1d(row_6);
            val x_9 = x_7
              .<(24)
              .&&({
                val x_8 = get_l_discount_6fbffa1f(row_6);
                x_8.>(0.05)
              });
            val x_11 = x_9.&&({
              val x_10 = get_l_discount_6fbffa1f(row_6);
              x_10.<(0.07)
            });
            val x_16 = x_11.&&({
              val x_12 = get_l_shipdate_4bb53bf7(row_6);
              val x_15 = strcmp_7efe36d5(scala.Tuple2(x_12, start_date));
              x_15.asInstanceOf[scala.Int].>(0)
            });
            val x_21 = x_16.&&({
              val x_17 = get_l_shipdate_4bb53bf7(row_6);
              val x_20 = strcmp_7efe36d5(scala.Tuple2(x_17, end_date));
              x_20.asInstanceOf[scala.Int].<(0)
            });
            if (x_21) {
              val x_22 = res_0;
              val x_23 = get_l_extendedprice_5287690b(row_6);
              val x_24 = get_l_discount_6fbffa1f(row_6);
              res_0 = x_22.+(x_23.*(x_24));
              true
            } else
              true
          })
      }) {
        val x_25 = Lineitem_table_2e1b204d.prev(cursor_1);
        val x_26 = empty_pointers_Lineitem_1018cda7(x_25);
        v_4 = x_26
      };
      Lineitem_table_2e1b204d.cursorClose(cursor_1);
      val x_27 = res_0;
      LMDBTable.txnCommit();
      Str_table_7acddf95.dbiClose();
      Nation_table_6ea0e4d4.dbiClose();
      Region_table_5a5cfb2.dbiClose();
      Part_table_1ba452ca.dbiClose();
      Supplier_table_4346f735.dbiClose();
      PartSupp_table_6cca10bb.dbiClose();
      Customer_table_649072f0.dbiClose();
      Order_table_254c6d2e.dbiClose();
      Lineitem_table_2e1b204d.dbiClose();
      x_27
    }
  }
}

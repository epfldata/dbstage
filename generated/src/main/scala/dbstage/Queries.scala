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
  lazy val Customer_table_2a7998ab = new LMDBTable[Customer]("Customer")

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
  lazy val Lineitem_table_3683486f = new LMDBTable[Lineitem]("Lineitem")

  type NationData = CStruct7[Long, Long, Str, Long, Region, Long, Str]
  type Nation = Ptr[NationData]
  val sizeNation = sizeof[NationData]
  lazy val Nation_table_45dc9fb6 = new LMDBTable[Nation]("Nation")

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
  lazy val Order_table_37f8d849 = new LMDBTable[Order]("Order")

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
  lazy val Part_table_4bce9764 = new LMDBTable[Part]("Part")

  type PartSuppData =
    CStruct9[Long, Long, Part, Long, Supplier, Int, Double, Long, Str]
  type PartSupp = Ptr[PartSuppData]
  val sizePartSupp = sizeof[PartSuppData]
  lazy val PartSupp_table_67a27e42 = new LMDBTable[PartSupp]("PartSupp")

  type RegionData = CStruct5[Long, Long, Str, Long, Str]
  type Region = Ptr[RegionData]
  val sizeRegion = sizeof[RegionData]
  lazy val Region_table_46227c2f = new LMDBTable[Region]("Region")

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
  lazy val Supplier_table_37e9ce32 = new LMDBTable[Supplier]("Supplier")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_8230a1c = new LMDBTable[CString]("Str")

  def toCString_7c072534(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_2911fd9f(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Customer_23a3e6af(value: Customer): Customer = {
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
  def empty_pointers_Lineitem_727442bc(value: Lineitem): Lineitem = {
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
  def empty_pointers_Nation_2f5353ae(value: Nation): Nation = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
    }
    value
  }
  def empty_pointers_Order_6a9a1480(value: Order): Order = {
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
  def empty_pointers_Part_56b001e2(value: Part): Part = {
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
  def empty_pointers_PartSupp_601c82a(value: PartSupp): PartSupp = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._9 = null
    }
    value
  }
  def empty_pointers_Region_337a3567(value: Region): Region = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Supplier_97e199c(value: Supplier): Supplier = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
      value._9 = null
      value._12 = null
    }
    value
  }

  def get_Customer_5f17b640(key: Long)(implicit zone: Zone): Customer = {
    val value = Customer_table_2a7998ab.get(key)
    empty_pointers_Customer_23a3e6af(value)
  }
  def get_Lineitem_57df7510(key: Long)(implicit zone: Zone): Lineitem = {
    val value = Lineitem_table_3683486f.get(key)
    empty_pointers_Lineitem_727442bc(value)
  }
  def get_Nation_35eca381(key: Long)(implicit zone: Zone): Nation = {
    val value = Nation_table_45dc9fb6.get(key)
    empty_pointers_Nation_2f5353ae(value)
  }
  def get_Order_4ca7fe59(key: Long)(implicit zone: Zone): Order = {
    val value = Order_table_37f8d849.get(key)
    empty_pointers_Order_6a9a1480(value)
  }
  def get_Part_2e60e43b(key: Long)(implicit zone: Zone): Part = {
    val value = Part_table_4bce9764.get(key)
    empty_pointers_Part_56b001e2(value)
  }
  def get_PartSupp_2bb6312(key: Long)(implicit zone: Zone): PartSupp = {
    val value = PartSupp_table_67a27e42.get(key)
    empty_pointers_PartSupp_601c82a(value)
  }
  def get_Region_529b286f(key: Long)(implicit zone: Zone): Region = {
    val value = Region_table_46227c2f.get(key)
    empty_pointers_Region_337a3567(value)
  }
  def get_Supplier_21bd4153(key: Long)(implicit zone: Zone): Supplier = {
    val value = Supplier_table_37e9ce32.get(key)
    empty_pointers_Supplier_97e199c(value)
  }

  def get_Str_8d53863(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_8230a1c.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Customer_512f6998(data_el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_2a7998ab.put(
      data_el._1,
      sizeCustomer,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Lineitem_5a7ceb82(data_el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_3683486f.put(
      data_el._1,
      sizeLineitem,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Nation_11009e47(data_el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_45dc9fb6.put(
      data_el._1,
      sizeNation,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Order_30b7aa24(data_el: Order)(implicit zone: Zone): Unit = {
    Order_table_37f8d849.put(
      data_el._1,
      sizeOrder,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Part_1afe7603(data_el: Part)(implicit zone: Zone): Unit = {
    Part_table_4bce9764.put(
      data_el._1,
      sizePart,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_PartSupp_27e3be25(data_el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_67a27e42.put(
      data_el._1,
      sizePartSupp,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Region_3204fe38(data_el: Region)(implicit zone: Zone): Unit = {
    Region_table_46227c2f.put(
      data_el._1,
      sizeRegion,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Supplier_3fef84d6(data_el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_37e9ce32.put(
      data_el._1,
      sizeSupplier,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_56c8b346(str: Str)(implicit zone: Zone): Unit = {
    Str_table_8230a1c.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Customer_3a0a0964(el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_2a7998ab.delete(el._1)
  }
  def delete_Lineitem_29cbdfe0(el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_3683486f.delete(el._1)
  }
  def delete_Nation_44af8eb8(el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_45dc9fb6.delete(el._1)
  }
  def delete_Order_3a0c1050(el: Order)(implicit zone: Zone): Unit = {
    Order_table_37f8d849.delete(el._1)
  }
  def delete_Part_50277f5c(el: Part)(implicit zone: Zone): Unit = {
    Part_table_4bce9764.delete(el._1)
  }
  def delete_PartSupp_27e49e67(el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_67a27e42.delete(el._1)
  }
  def delete_Region_1f861e6d(el: Region)(implicit zone: Zone): Unit = {
    Region_table_46227c2f.delete(el._1)
  }
  def delete_Str_4552035d(el: Str)(implicit zone: Zone): Unit = {
    Str_table_8230a1c.delete(el._1)
  }
  def delete_Supplier_2ee44934(el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_37e9ce32.delete(el._1)
  }

  def charAt_3df6b8c1(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strcmp_6652b149(params: Tuple2[Str, CString])(implicit zone: Zone): Int =
    strcmp(params._1._2, params._2)
  def strlen_7c09b6a(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_c_acctbal_607d8833(
      this_2d80db73: Customer
  )(implicit zone: Zone): Double = {
    this_2d80db73._12
  }
  def get_c_address_7dccb1de(
      this_2d80db73: Customer
  )(implicit zone: Zone): Str = {
    if (this_2d80db73._9 == null) {
      this_2d80db73._9 = get_Str_8d53863(this_2d80db73._8)
    }
    this_2d80db73._9
  }
  def get_c_comment_1260221(
      this_2d80db73: Customer
  )(implicit zone: Zone): Str = {
    if (this_2d80db73._14 == null) {
      this_2d80db73._14 = get_Str_8d53863(this_2d80db73._13)
    }
    this_2d80db73._14
  }
  def get_c_mktsegment_56800bf9(
      this_2d80db73: Customer
  )(implicit zone: Zone): Str = {
    if (this_2d80db73._3 == null) {
      this_2d80db73._3 = get_Str_8d53863(this_2d80db73._2)
    }
    this_2d80db73._3
  }
  def get_c_name_5ab03d0a(this_2d80db73: Customer)(implicit zone: Zone): Str = {
    if (this_2d80db73._7 == null) {
      this_2d80db73._7 = get_Str_8d53863(this_2d80db73._6)
    }
    this_2d80db73._7
  }
  def get_c_nation_42ea25c3(
      this_2d80db73: Customer
  )(implicit zone: Zone): Nation = {
    if (this_2d80db73._5 == null) {
      this_2d80db73._5 = get_Nation_35eca381(this_2d80db73._4)
    }
    this_2d80db73._5
  }
  def get_c_phone_73ec00b5(
      this_2d80db73: Customer
  )(implicit zone: Zone): Str = {
    if (this_2d80db73._11 == null) {
      this_2d80db73._11 = get_Str_8d53863(this_2d80db73._10)
    }
    this_2d80db73._11
  }
  def get_key_7ee63329(this_25e502f2: Lineitem)(implicit zone: Zone): Long = {
    this_25e502f2._1
  }
  def get_l_comment_6db1c50b(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._26 == null) {
      this_25e502f2._26 = get_Str_8d53863(this_25e502f2._25)
    }
    this_25e502f2._26
  }
  def get_l_commitdate_7ecb99a0(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._18 == null) {
      this_25e502f2._18 = get_Str_8d53863(this_25e502f2._17)
    }
    this_25e502f2._18
  }
  def get_l_discount_7334f619(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Double = {
    this_25e502f2._9
  }
  def get_l_extendedprice_3c04fc2d(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Double = {
    this_25e502f2._8
  }
  def get_l_linenumber_5ffeff3b(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Long = {
    this_25e502f2._6
  }
  def get_l_linestatus_7dda6bfd(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._14 == null) {
      this_25e502f2._14 = get_Str_8d53863(this_25e502f2._13)
    }
    this_25e502f2._14
  }
  def get_l_order_298ade61(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Order = {
    if (this_25e502f2._3 == null) {
      this_25e502f2._3 = get_Order_4ca7fe59(this_25e502f2._2)
    }
    this_25e502f2._3
  }
  def get_l_partsupp_3274bae9(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): PartSupp = {
    if (this_25e502f2._5 == null) {
      this_25e502f2._5 = get_PartSupp_2bb6312(this_25e502f2._4)
    }
    this_25e502f2._5
  }
  def get_l_quantity_1adb8cbb(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Long = {
    this_25e502f2._7
  }
  def get_l_receiptdate_34f0899e(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._20 == null) {
      this_25e502f2._20 = get_Str_8d53863(this_25e502f2._19)
    }
    this_25e502f2._20
  }
  def get_l_returnflag_4b7630b7(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._12 == null) {
      this_25e502f2._12 = get_Str_8d53863(this_25e502f2._11)
    }
    this_25e502f2._12
  }
  def get_l_shipdate_5cc268c2(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._16 == null) {
      this_25e502f2._16 = get_Str_8d53863(this_25e502f2._15)
    }
    this_25e502f2._16
  }
  def get_l_shipinstruct_1aba89b5(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._22 == null) {
      this_25e502f2._22 = get_Str_8d53863(this_25e502f2._21)
    }
    this_25e502f2._22
  }
  def get_l_shipmode_2a365f05(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_25e502f2._24 == null) {
      this_25e502f2._24 = get_Str_8d53863(this_25e502f2._23)
    }
    this_25e502f2._24
  }
  def get_l_tax_503f317c(
      this_25e502f2: Lineitem
  )(implicit zone: Zone): Double = {
    this_25e502f2._10
  }
  def get_n_comment_514d12ab(
      this_1ff4c7c1: Nation
  )(implicit zone: Zone): Str = {
    if (this_1ff4c7c1._7 == null) {
      this_1ff4c7c1._7 = get_Str_8d53863(this_1ff4c7c1._6)
    }
    this_1ff4c7c1._7
  }
  def get_n_name_499ba1f0(this_1ff4c7c1: Nation)(implicit zone: Zone): Str = {
    if (this_1ff4c7c1._3 == null) {
      this_1ff4c7c1._3 = get_Str_8d53863(this_1ff4c7c1._2)
    }
    this_1ff4c7c1._3
  }
  def get_n_region_6086cd6e(
      this_1ff4c7c1: Nation
  )(implicit zone: Zone): Region = {
    if (this_1ff4c7c1._5 == null) {
      this_1ff4c7c1._5 = get_Region_529b286f(this_1ff4c7c1._4)
    }
    this_1ff4c7c1._5
  }
  def get_o_clerk_60144daf(this_59f5d470: Order)(implicit zone: Zone): Str = {
    if (this_59f5d470._12 == null) {
      this_59f5d470._12 = get_Str_8d53863(this_59f5d470._11)
    }
    this_59f5d470._12
  }
  def get_o_comment_7414f47c(this_59f5d470: Order)(implicit zone: Zone): Str = {
    if (this_59f5d470._15 == null) {
      this_59f5d470._15 = get_Str_8d53863(this_59f5d470._14)
    }
    this_59f5d470._15
  }
  def get_o_cust_222fba2d(
      this_59f5d470: Order
  )(implicit zone: Zone): Customer = {
    if (this_59f5d470._3 == null) {
      this_59f5d470._3 = get_Customer_5f17b640(this_59f5d470._2)
    }
    this_59f5d470._3
  }
  def get_o_orderdate_558ebc4c(
      this_59f5d470: Order
  )(implicit zone: Zone): Str = {
    if (this_59f5d470._8 == null) {
      this_59f5d470._8 = get_Str_8d53863(this_59f5d470._7)
    }
    this_59f5d470._8
  }
  def get_o_orderpriority_1735ea67(
      this_59f5d470: Order
  )(implicit zone: Zone): Str = {
    if (this_59f5d470._10 == null) {
      this_59f5d470._10 = get_Str_8d53863(this_59f5d470._9)
    }
    this_59f5d470._10
  }
  def get_o_orderstatus_44d14be9(
      this_59f5d470: Order
  )(implicit zone: Zone): Str = {
    if (this_59f5d470._5 == null) {
      this_59f5d470._5 = get_Str_8d53863(this_59f5d470._4)
    }
    this_59f5d470._5
  }
  def get_o_shippriority_1b706f8c(
      this_59f5d470: Order
  )(implicit zone: Zone): Int = {
    this_59f5d470._13
  }
  def get_o_totalprice_3bbcdc72(
      this_59f5d470: Order
  )(implicit zone: Zone): Double = {
    this_59f5d470._6
  }
  def get_p_brand_4d22f236(this_3627fb0: Part)(implicit zone: Zone): Str = {
    if (this_3627fb0._7 == null) {
      this_3627fb0._7 = get_Str_8d53863(this_3627fb0._6)
    }
    this_3627fb0._7
  }
  def get_p_comment_55b6e811(this_3627fb0: Part)(implicit zone: Zone): Str = {
    if (this_3627fb0._15 == null) {
      this_3627fb0._15 = get_Str_8d53863(this_3627fb0._14)
    }
    this_3627fb0._15
  }
  def get_p_container_8b4f199(this_3627fb0: Part)(implicit zone: Zone): Str = {
    if (this_3627fb0._12 == null) {
      this_3627fb0._12 = get_Str_8d53863(this_3627fb0._11)
    }
    this_3627fb0._12
  }
  def get_p_mfgr_183b90c0(this_3627fb0: Part)(implicit zone: Zone): Str = {
    if (this_3627fb0._5 == null) {
      this_3627fb0._5 = get_Str_8d53863(this_3627fb0._4)
    }
    this_3627fb0._5
  }
  def get_p_name_425262b3(this_3627fb0: Part)(implicit zone: Zone): Str = {
    if (this_3627fb0._3 == null) {
      this_3627fb0._3 = get_Str_8d53863(this_3627fb0._2)
    }
    this_3627fb0._3
  }
  def get_p_retailprice_2046ec88(
      this_3627fb0: Part
  )(implicit zone: Zone): Double = {
    this_3627fb0._13
  }
  def get_p_size_fd1e920(this_3627fb0: Part)(implicit zone: Zone): Int = {
    this_3627fb0._10
  }
  def get_p_type_4ba8d58a(this_3627fb0: Part)(implicit zone: Zone): Str = {
    if (this_3627fb0._9 == null) {
      this_3627fb0._9 = get_Str_8d53863(this_3627fb0._8)
    }
    this_3627fb0._9
  }
  def get_ps_availqty_d8becc5(
      this_1abe61b6: PartSupp
  )(implicit zone: Zone): Int = {
    this_1abe61b6._6
  }
  def get_ps_comment_d139cbd(
      this_1abe61b6: PartSupp
  )(implicit zone: Zone): Str = {
    if (this_1abe61b6._9 == null) {
      this_1abe61b6._9 = get_Str_8d53863(this_1abe61b6._8)
    }
    this_1abe61b6._9
  }
  def get_ps_part_1a1c10(this_1abe61b6: PartSupp)(implicit zone: Zone): Part = {
    if (this_1abe61b6._3 == null) {
      this_1abe61b6._3 = get_Part_2e60e43b(this_1abe61b6._2)
    }
    this_1abe61b6._3
  }
  def get_ps_supp_69407e74(
      this_1abe61b6: PartSupp
  )(implicit zone: Zone): Supplier = {
    if (this_1abe61b6._5 == null) {
      this_1abe61b6._5 = get_Supplier_21bd4153(this_1abe61b6._4)
    }
    this_1abe61b6._5
  }
  def get_ps_supplycost_3bff4a5d(
      this_1abe61b6: PartSupp
  )(implicit zone: Zone): Double = {
    this_1abe61b6._7
  }
  def get_r_comment_7ab2bc34(
      this_2a98dfeb: Region
  )(implicit zone: Zone): Str = {
    if (this_2a98dfeb._5 == null) {
      this_2a98dfeb._5 = get_Str_8d53863(this_2a98dfeb._4)
    }
    this_2a98dfeb._5
  }
  def get_r_name_10ebcec8(this_2a98dfeb: Region)(implicit zone: Zone): Str = {
    if (this_2a98dfeb._3 == null) {
      this_2a98dfeb._3 = get_Str_8d53863(this_2a98dfeb._2)
    }
    this_2a98dfeb._3
  }
  def get_s_acctbal_3d8c2966(
      this_5107c32a: Supplier
  )(implicit zone: Zone): Double = {
    this_5107c32a._10
  }
  def get_s_address_52b071ff(
      this_5107c32a: Supplier
  )(implicit zone: Zone): Str = {
    if (this_5107c32a._5 == null) {
      this_5107c32a._5 = get_Str_8d53863(this_5107c32a._4)
    }
    this_5107c32a._5
  }
  def get_s_comment_1c6378ef(
      this_5107c32a: Supplier
  )(implicit zone: Zone): Str = {
    if (this_5107c32a._12 == null) {
      this_5107c32a._12 = get_Str_8d53863(this_5107c32a._11)
    }
    this_5107c32a._12
  }
  def get_s_name_7312e6da(this_5107c32a: Supplier)(implicit zone: Zone): Str = {
    if (this_5107c32a._3 == null) {
      this_5107c32a._3 = get_Str_8d53863(this_5107c32a._2)
    }
    this_5107c32a._3
  }
  def get_s_nation_2f0926c7(
      this_5107c32a: Supplier
  )(implicit zone: Zone): Nation = {
    if (this_5107c32a._7 == null) {
      this_5107c32a._7 = get_Nation_35eca381(this_5107c32a._6)
    }
    this_5107c32a._7
  }
  def get_s_phone_7e679550(
      this_5107c32a: Supplier
  )(implicit zone: Zone): Str = {
    if (this_5107c32a._9 == null) {
      this_5107c32a._9 = get_Str_8d53863(this_5107c32a._8)
    }
    this_5107c32a._9
  }
  def get_string_1d0433b7(this_79443e73: Str)(implicit zone: Zone): String = {
    fromCString_2911fd9f(this_79443e73._2)
  }

  def set_c_acctbal_4ed8e708(this_2d80db73: Customer, c_acctbal: Double)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._12 = c_acctbal
  }
  def set_c_address_6e20c460(this_2d80db73: Customer, c_address: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._8 = c_address._1
    this_2d80db73._9 = c_address
  }
  def set_c_comment_75535f5e(this_2d80db73: Customer, c_comment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._13 = c_comment._1
    this_2d80db73._14 = c_comment
  }
  def set_c_mktsegment_7cba165b(this_2d80db73: Customer, c_mktsegment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._2 = c_mktsegment._1
    this_2d80db73._3 = c_mktsegment
  }
  def set_c_name_b30914c(this_2d80db73: Customer, c_name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._6 = c_name._1
    this_2d80db73._7 = c_name
  }
  def set_c_nation_635b578e(this_2d80db73: Customer, c_nation: Nation)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._4 = c_nation._1
    this_2d80db73._5 = c_nation
  }
  def set_c_phone_c1d5d14(this_2d80db73: Customer, c_phone: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2d80db73._10 = c_phone._1
    this_2d80db73._11 = c_phone
  }

  def big_scan: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_8230a1c.dbiOpen();
      Nation_table_45dc9fb6.dbiOpen();
      Region_table_46227c2f.dbiOpen();
      Part_table_4bce9764.dbiOpen();
      Supplier_table_37e9ce32.dbiOpen();
      PartSupp_table_67a27e42.dbiOpen();
      Customer_table_2a7998ab.dbiOpen();
      Order_table_37f8d849.dbiOpen();
      Lineitem_table_3683486f.dbiOpen();
      var res_0: scala.Double = 0.0;
      val cursor_1 = Lineitem_table_3683486f.cursorOpen();
      val x_2 = Lineitem_table_3683486f.last(cursor_1);
      val x_3 = empty_pointers_Lineitem_727442bc(x_2);
      var v_4: Lineitem = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_l_tax_503f317c(row_6);
            res_0 = x_7.+(x_8);
            true
          })
      }) {
        val x_9 = Lineitem_table_3683486f.prev(cursor_1);
        val x_10 = empty_pointers_Lineitem_727442bc(x_9);
        v_4 = x_10
      };
      Lineitem_table_3683486f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_8230a1c.dbiClose();
      Nation_table_45dc9fb6.dbiClose();
      Region_table_46227c2f.dbiClose();
      Part_table_4bce9764.dbiClose();
      Supplier_table_37e9ce32.dbiClose();
      PartSupp_table_67a27e42.dbiClose();
      Customer_table_2a7998ab.dbiClose();
      Order_table_37f8d849.dbiClose();
      Lineitem_table_3683486f.dbiClose();
      x_11
    }
  }
  def cross_join: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_8230a1c.dbiOpen();
      Nation_table_45dc9fb6.dbiOpen();
      Region_table_46227c2f.dbiOpen();
      Part_table_4bce9764.dbiOpen();
      Supplier_table_37e9ce32.dbiOpen();
      PartSupp_table_67a27e42.dbiOpen();
      Customer_table_2a7998ab.dbiOpen();
      Order_table_37f8d849.dbiOpen();
      Lineitem_table_3683486f.dbiOpen();
      var res_0: scala.Double = scala.Double.MinValue;
      val cursor_1 = Supplier_table_37e9ce32.cursorOpen();
      val x_2 = Supplier_table_37e9ce32.last(cursor_1);
      val x_3 = empty_pointers_Supplier_97e199c(x_2);
      var v_4: Supplier = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Supplier_table_37e9ce32.cursorOpen();
            val x_8 = Supplier_table_37e9ce32.last(cursor_7);
            val x_9 = empty_pointers_Supplier_97e199c(x_8);
            var v_10: Supplier = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 = get_s_acctbal_3d8c2966(row1_6);
                  val x_14 = get_s_acctbal_3d8c2966(row2_12);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.doubleWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Supplier_table_37e9ce32.prev(cursor_7);
              val x_20 = empty_pointers_Supplier_97e199c(x_19);
              v_10 = x_20
            };
            Supplier_table_37e9ce32.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Supplier_table_37e9ce32.prev(cursor_1);
        val x_22 = empty_pointers_Supplier_97e199c(x_21);
        v_4 = x_22
      };
      Supplier_table_37e9ce32.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_8230a1c.dbiClose();
      Nation_table_45dc9fb6.dbiClose();
      Region_table_46227c2f.dbiClose();
      Part_table_4bce9764.dbiClose();
      Supplier_table_37e9ce32.dbiClose();
      PartSupp_table_67a27e42.dbiClose();
      Customer_table_2a7998ab.dbiClose();
      Order_table_37f8d849.dbiClose();
      Lineitem_table_3683486f.dbiClose();
      x_23
    }
  }
  def key_join: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_8230a1c.dbiOpen();
      Nation_table_45dc9fb6.dbiOpen();
      Region_table_46227c2f.dbiOpen();
      Part_table_4bce9764.dbiOpen();
      Supplier_table_37e9ce32.dbiOpen();
      PartSupp_table_67a27e42.dbiOpen();
      Customer_table_2a7998ab.dbiOpen();
      Order_table_37f8d849.dbiOpen();
      Lineitem_table_3683486f.dbiOpen();

      val x_11 = toCString_7c072534("Brand#44");

      var res_0: scala.Double = 0.0;
      val cursor_1 = Lineitem_table_3683486f.cursorOpen();
      val x_2 = Lineitem_table_3683486f.last(cursor_1);
      val x_3 = empty_pointers_Lineitem_727442bc(x_2);
      var v_4: Lineitem = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val ps_7 = get_l_partsupp_3274bae9(row_6);
            val p_8 = get_ps_part_1a1c10(ps_7);
            val x_9 = get_ps_supplycost_3bff4a5d(ps_7);
            val x_13 = x_9
              .<(50.0)
              .&&({
                val x_10 = get_p_brand_4d22f236(p_8);
                // val x_11 = toCString_7c072534("Brand#44");
                val x_12 = strcmp_6652b149(scala.Tuple2(x_10, x_11));
                x_12.asInstanceOf[scala.Int].!=(0)
              });
            if (x_13) {
              val x_14 = res_0;
              val x_15 = get_l_extendedprice_3c04fc2d(row_6);
              val x_16 = get_l_discount_7334f619(row_6);
              res_0 = x_14.+(x_15.*(x_16));
              true
            } else
              true
          })
      }) {
        val x_17 = Lineitem_table_3683486f.prev(cursor_1);
        val x_18 = empty_pointers_Lineitem_727442bc(x_17);
        v_4 = x_18
      };
      Lineitem_table_3683486f.cursorClose(cursor_1);
      val x_19 = res_0;
      LMDBTable.txnCommit();
      Str_table_8230a1c.dbiClose();
      Nation_table_45dc9fb6.dbiClose();
      Region_table_46227c2f.dbiClose();
      Part_table_4bce9764.dbiClose();
      Supplier_table_37e9ce32.dbiClose();
      PartSupp_table_67a27e42.dbiClose();
      Customer_table_2a7998ab.dbiClose();
      Order_table_37f8d849.dbiClose();
      Lineitem_table_3683486f.dbiClose();
      x_19
    }
  }
  def tpch_query_6: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_8230a1c.dbiOpen();
      Nation_table_45dc9fb6.dbiOpen();
      Region_table_46227c2f.dbiOpen();
      Part_table_4bce9764.dbiOpen();
      Supplier_table_37e9ce32.dbiOpen();
      PartSupp_table_67a27e42.dbiOpen();
      Customer_table_2a7998ab.dbiOpen();
      Order_table_37f8d849.dbiOpen();
      Lineitem_table_3683486f.dbiOpen();

      val x_13 = toCString_7c072534("1994-01-01");
      val x_17 = toCString_7c072534("1995-01-01");

      var res_0: scala.Double = 0.0;
      val cursor_1 = Lineitem_table_3683486f.cursorOpen();
      val x_2 = Lineitem_table_3683486f.last(cursor_1);
      val x_3 = empty_pointers_Lineitem_727442bc(x_2);
      var v_4: Lineitem = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_l_quantity_1adb8cbb(row_6);
            val x_9 = x_7
              .<(24)
              .&&({
                val x_8 = get_l_discount_7334f619(row_6);
                x_8.>=(0.05)
              });
            val x_11 = x_9.&&({
              val x_10 = get_l_discount_7334f619(row_6);
              x_10.<=(0.07)
            });
            val x_15 = x_11.&&({
              val x_12 = get_l_shipdate_5cc268c2(row_6);
              // val x_13 = toCString_7c072534("1994-01-01");
              val x_14 = strcmp_6652b149(scala.Tuple2(x_12, x_13));
              x_14.asInstanceOf[scala.Int].>=(0)
            });
            val x_19 = x_15.&&({
              val x_16 = get_l_shipdate_5cc268c2(row_6);
              // val x_17 = toCString_7c072534("1995-01-01");
              val x_18 = strcmp_6652b149(scala.Tuple2(x_16, x_17));
              x_18.asInstanceOf[scala.Int].<(0)
            });
            if (x_19) {
              val x_20 = res_0;
              val x_21 = get_l_extendedprice_3c04fc2d(row_6);
              val x_22 = get_l_discount_7334f619(row_6);
              res_0 = x_20.+(x_21.*(x_22));
              true
            } else
              true
          })
      }) {
        val x_23 = Lineitem_table_3683486f.prev(cursor_1);
        val x_24 = empty_pointers_Lineitem_727442bc(x_23);
        v_4 = x_24
      };
      Lineitem_table_3683486f.cursorClose(cursor_1);
      val x_25 = res_0;
      LMDBTable.txnCommit();
      Str_table_8230a1c.dbiClose();
      Nation_table_45dc9fb6.dbiClose();
      Region_table_46227c2f.dbiClose();
      Part_table_4bce9764.dbiClose();
      Supplier_table_37e9ce32.dbiClose();
      PartSupp_table_67a27e42.dbiClose();
      Customer_table_2a7998ab.dbiClose();
      Order_table_37f8d849.dbiClose();
      Lineitem_table_3683486f.dbiClose();
      x_25
    }
  }
}

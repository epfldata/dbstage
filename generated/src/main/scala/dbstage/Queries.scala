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
  lazy val Customer_table_39b9b25e = new LMDBTable[Customer]("Customer")

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
  lazy val Lineitem_table_50ef93b7 = new LMDBTable[Lineitem]("Lineitem")

  type NationData = CStruct7[Long, Long, Str, Long, Region, Long, Str]
  type Nation = Ptr[NationData]
  val sizeNation = sizeof[NationData]
  lazy val Nation_table_1d230f23 = new LMDBTable[Nation]("Nation")

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
  lazy val Order_table_283a5c04 = new LMDBTable[Order]("Order")

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
  lazy val Part_table_5ce5201f = new LMDBTable[Part]("Part")

  type PartSuppData =
    CStruct9[Long, Long, Part, Long, Supplier, Int, Double, Long, Str]
  type PartSupp = Ptr[PartSuppData]
  val sizePartSupp = sizeof[PartSuppData]
  lazy val PartSupp_table_70bb721f = new LMDBTable[PartSupp]("PartSupp")

  type RegionData = CStruct5[Long, Long, Str, Long, Str]
  type Region = Ptr[RegionData]
  val sizeRegion = sizeof[RegionData]
  lazy val Region_table_7564313c = new LMDBTable[Region]("Region")

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
  lazy val Supplier_table_fc489e8 = new LMDBTable[Supplier]("Supplier")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_7a54a5d7 = new LMDBTable[CString]("Str")

  def toCString_51d41b08(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_5ed2ddf(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Customer_6c7a0236(value: Customer): Customer = {
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
  def empty_pointers_Lineitem_29085f9d(value: Lineitem): Lineitem = {
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
  def empty_pointers_Nation_183bc571(value: Nation): Nation = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
    }
    value
  }
  def empty_pointers_Order_322546fc(value: Order): Order = {
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
  def empty_pointers_Part_ef2993c(value: Part): Part = {
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
  def empty_pointers_PartSupp_656dcbc8(value: PartSupp): PartSupp = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._9 = null
    }
    value
  }
  def empty_pointers_Region_23bb8a59(value: Region): Region = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Supplier_3bb13a74(value: Supplier): Supplier = {
    if (value != null) {
      value._3 = null
      value._5 = null
      value._7 = null
      value._9 = null
      value._12 = null
    }
    value
  }

  def get_Customer_21907f47(key: Long)(implicit zone: Zone): Customer = {
    val value = Customer_table_39b9b25e.get(key)
    empty_pointers_Customer_6c7a0236(value)
  }
  def get_Lineitem_2a28c336(key: Long)(implicit zone: Zone): Lineitem = {
    val value = Lineitem_table_50ef93b7.get(key)
    empty_pointers_Lineitem_29085f9d(value)
  }
  def get_Nation_553e7d68(key: Long)(implicit zone: Zone): Nation = {
    val value = Nation_table_1d230f23.get(key)
    empty_pointers_Nation_183bc571(value)
  }
  def get_Order_67ee8f21(key: Long)(implicit zone: Zone): Order = {
    val value = Order_table_283a5c04.get(key)
    empty_pointers_Order_322546fc(value)
  }
  def get_Part_4526bedc(key: Long)(implicit zone: Zone): Part = {
    val value = Part_table_5ce5201f.get(key)
    empty_pointers_Part_ef2993c(value)
  }
  def get_PartSupp_29fbf43d(key: Long)(implicit zone: Zone): PartSupp = {
    val value = PartSupp_table_70bb721f.get(key)
    empty_pointers_PartSupp_656dcbc8(value)
  }
  def get_Region_f880957(key: Long)(implicit zone: Zone): Region = {
    val value = Region_table_7564313c.get(key)
    empty_pointers_Region_23bb8a59(value)
  }
  def get_Supplier_1c8c1800(key: Long)(implicit zone: Zone): Supplier = {
    val value = Supplier_table_fc489e8.get(key)
    empty_pointers_Supplier_3bb13a74(value)
  }

  def get_Str_6c2c8513(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_7a54a5d7.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Customer_1a00deb7(data_el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_39b9b25e.put(
      data_el._1,
      sizeCustomer,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Lineitem_558dbe11(data_el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_50ef93b7.put(
      data_el._1,
      sizeLineitem,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Nation_293df0b8(data_el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_1d230f23.put(
      data_el._1,
      sizeNation,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Order_21b1ec91(data_el: Order)(implicit zone: Zone): Unit = {
    Order_table_283a5c04.put(
      data_el._1,
      sizeOrder,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Part_3dd86b76(data_el: Part)(implicit zone: Zone): Unit = {
    Part_table_5ce5201f.put(
      data_el._1,
      sizePart,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_PartSupp_51d27ea(data_el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_70bb721f.put(
      data_el._1,
      sizePartSupp,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Region_4363bfc1(data_el: Region)(implicit zone: Zone): Unit = {
    Region_table_7564313c.put(
      data_el._1,
      sizeRegion,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Supplier_6d980658(data_el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_fc489e8.put(
      data_el._1,
      sizeSupplier,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_13e55346(str: Str)(implicit zone: Zone): Unit = {
    Str_table_7a54a5d7.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Customer_c6f5997(el: Customer)(implicit zone: Zone): Unit = {
    Customer_table_39b9b25e.delete(el._1)
  }
  def delete_Lineitem_35a9443f(el: Lineitem)(implicit zone: Zone): Unit = {
    Lineitem_table_50ef93b7.delete(el._1)
  }
  def delete_Nation_12007001(el: Nation)(implicit zone: Zone): Unit = {
    Nation_table_1d230f23.delete(el._1)
  }
  def delete_Order_72811264(el: Order)(implicit zone: Zone): Unit = {
    Order_table_283a5c04.delete(el._1)
  }
  def delete_Part_4544e42(el: Part)(implicit zone: Zone): Unit = {
    Part_table_5ce5201f.delete(el._1)
  }
  def delete_PartSupp_7b236a4b(el: PartSupp)(implicit zone: Zone): Unit = {
    PartSupp_table_70bb721f.delete(el._1)
  }
  def delete_Region_6ab29d56(el: Region)(implicit zone: Zone): Unit = {
    Region_table_7564313c.delete(el._1)
  }
  def delete_Str_105ab056(el: Str)(implicit zone: Zone): Unit = {
    Str_table_7a54a5d7.delete(el._1)
  }
  def delete_Supplier_365ada41(el: Supplier)(implicit zone: Zone): Unit = {
    Supplier_table_fc489e8.delete(el._1)
  }

  def charAt_62825e03(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strcmp_771ceab4(params: Tuple2[Str, CString])(implicit zone: Zone): Int =
    strcmp(params._1._2, params._2)
  def strlen_64f063a4(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_c_acctbal_2f5e1838(
      this_2a7e11f2: Customer
  )(implicit zone: Zone): Double = {
    this_2a7e11f2._12
  }
  def get_c_address_305bd9f1(
      this_2a7e11f2: Customer
  )(implicit zone: Zone): Str = {
    if (this_2a7e11f2._9 == null) {
      this_2a7e11f2._9 = get_Str_6c2c8513(this_2a7e11f2._8)
    }
    this_2a7e11f2._9
  }
  def get_c_comment_4ff40826(
      this_2a7e11f2: Customer
  )(implicit zone: Zone): Str = {
    if (this_2a7e11f2._14 == null) {
      this_2a7e11f2._14 = get_Str_6c2c8513(this_2a7e11f2._13)
    }
    this_2a7e11f2._14
  }
  def get_c_mktsegment_31db9287(
      this_2a7e11f2: Customer
  )(implicit zone: Zone): Str = {
    if (this_2a7e11f2._3 == null) {
      this_2a7e11f2._3 = get_Str_6c2c8513(this_2a7e11f2._2)
    }
    this_2a7e11f2._3
  }
  def get_c_name_6e1bc35c(this_2a7e11f2: Customer)(implicit zone: Zone): Str = {
    if (this_2a7e11f2._7 == null) {
      this_2a7e11f2._7 = get_Str_6c2c8513(this_2a7e11f2._6)
    }
    this_2a7e11f2._7
  }
  def get_c_nation_e11b3f(
      this_2a7e11f2: Customer
  )(implicit zone: Zone): Nation = {
    if (this_2a7e11f2._5 == null) {
      this_2a7e11f2._5 = get_Nation_553e7d68(this_2a7e11f2._4)
    }
    this_2a7e11f2._5
  }
  def get_c_phone_6fbb863d(
      this_2a7e11f2: Customer
  )(implicit zone: Zone): Str = {
    if (this_2a7e11f2._11 == null) {
      this_2a7e11f2._11 = get_Str_6c2c8513(this_2a7e11f2._10)
    }
    this_2a7e11f2._11
  }
  def get_key_32a84ccb(this_2996ee7: Lineitem)(implicit zone: Zone): Long = {
    this_2996ee7._1
  }
  def get_l_comment_3a189142(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._26 == null) {
      this_2996ee7._26 = get_Str_6c2c8513(this_2996ee7._25)
    }
    this_2996ee7._26
  }
  def get_l_commitdate_9216252(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._18 == null) {
      this_2996ee7._18 = get_Str_6c2c8513(this_2996ee7._17)
    }
    this_2996ee7._18
  }
  def get_l_discount_4006dbc7(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Double = {
    this_2996ee7._9
  }
  def get_l_extendedprice_30b81918(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Double = {
    this_2996ee7._8
  }
  def get_l_linenumber_4e541ccc(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Long = {
    this_2996ee7._6
  }
  def get_l_linestatus_4c65c4a6(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._14 == null) {
      this_2996ee7._14 = get_Str_6c2c8513(this_2996ee7._13)
    }
    this_2996ee7._14
  }
  def get_l_order_3a4be60e(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Order = {
    if (this_2996ee7._3 == null) {
      this_2996ee7._3 = get_Order_67ee8f21(this_2996ee7._2)
    }
    this_2996ee7._3
  }
  def get_l_partsupp_4a4b4886(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): PartSupp = {
    if (this_2996ee7._5 == null) {
      this_2996ee7._5 = get_PartSupp_29fbf43d(this_2996ee7._4)
    }
    this_2996ee7._5
  }
  def get_l_quantity_24978f7f(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Long = {
    this_2996ee7._7
  }
  def get_l_receiptdate_46353fbf(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._20 == null) {
      this_2996ee7._20 = get_Str_6c2c8513(this_2996ee7._19)
    }
    this_2996ee7._20
  }
  def get_l_returnflag_232bcae1(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._12 == null) {
      this_2996ee7._12 = get_Str_6c2c8513(this_2996ee7._11)
    }
    this_2996ee7._12
  }
  def get_l_shipdate_3334a97a(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._16 == null) {
      this_2996ee7._16 = get_Str_6c2c8513(this_2996ee7._15)
    }
    this_2996ee7._16
  }
  def get_l_shipinstruct_2b402414(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._22 == null) {
      this_2996ee7._22 = get_Str_6c2c8513(this_2996ee7._21)
    }
    this_2996ee7._22
  }
  def get_l_shipmode_44e72e04(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Str = {
    if (this_2996ee7._24 == null) {
      this_2996ee7._24 = get_Str_6c2c8513(this_2996ee7._23)
    }
    this_2996ee7._24
  }
  def get_l_tax_396698bb(
      this_2996ee7: Lineitem
  )(implicit zone: Zone): Double = {
    this_2996ee7._10
  }
  def get_n_comment_54578e5(this_2f0f8994: Nation)(implicit zone: Zone): Str = {
    if (this_2f0f8994._7 == null) {
      this_2f0f8994._7 = get_Str_6c2c8513(this_2f0f8994._6)
    }
    this_2f0f8994._7
  }
  def get_n_name_78aff147(this_2f0f8994: Nation)(implicit zone: Zone): Str = {
    if (this_2f0f8994._3 == null) {
      this_2f0f8994._3 = get_Str_6c2c8513(this_2f0f8994._2)
    }
    this_2f0f8994._3
  }
  def get_n_region_75810da9(
      this_2f0f8994: Nation
  )(implicit zone: Zone): Region = {
    if (this_2f0f8994._5 == null) {
      this_2f0f8994._5 = get_Region_f880957(this_2f0f8994._4)
    }
    this_2f0f8994._5
  }
  def get_o_clerk_83dafd3(this_261169a4: Order)(implicit zone: Zone): Str = {
    if (this_261169a4._12 == null) {
      this_261169a4._12 = get_Str_6c2c8513(this_261169a4._11)
    }
    this_261169a4._12
  }
  def get_o_comment_f9f425a(this_261169a4: Order)(implicit zone: Zone): Str = {
    if (this_261169a4._15 == null) {
      this_261169a4._15 = get_Str_6c2c8513(this_261169a4._14)
    }
    this_261169a4._15
  }
  def get_o_cust_720efa45(
      this_261169a4: Order
  )(implicit zone: Zone): Customer = {
    if (this_261169a4._3 == null) {
      this_261169a4._3 = get_Customer_21907f47(this_261169a4._2)
    }
    this_261169a4._3
  }
  def get_o_orderdate_5f7ff1e9(
      this_261169a4: Order
  )(implicit zone: Zone): Str = {
    if (this_261169a4._8 == null) {
      this_261169a4._8 = get_Str_6c2c8513(this_261169a4._7)
    }
    this_261169a4._8
  }
  def get_o_orderpriority_3ad99ee2(
      this_261169a4: Order
  )(implicit zone: Zone): Str = {
    if (this_261169a4._10 == null) {
      this_261169a4._10 = get_Str_6c2c8513(this_261169a4._9)
    }
    this_261169a4._10
  }
  def get_o_orderstatus_4f218671(
      this_261169a4: Order
  )(implicit zone: Zone): Str = {
    if (this_261169a4._5 == null) {
      this_261169a4._5 = get_Str_6c2c8513(this_261169a4._4)
    }
    this_261169a4._5
  }
  def get_o_shippriority_4a788102(
      this_261169a4: Order
  )(implicit zone: Zone): Int = {
    this_261169a4._13
  }
  def get_o_totalprice_6aa8d11d(
      this_261169a4: Order
  )(implicit zone: Zone): Double = {
    this_261169a4._6
  }
  def get_p_brand_5f2173d5(this_1c706c24: Part)(implicit zone: Zone): Str = {
    if (this_1c706c24._7 == null) {
      this_1c706c24._7 = get_Str_6c2c8513(this_1c706c24._6)
    }
    this_1c706c24._7
  }
  def get_p_comment_515f99a2(this_1c706c24: Part)(implicit zone: Zone): Str = {
    if (this_1c706c24._15 == null) {
      this_1c706c24._15 = get_Str_6c2c8513(this_1c706c24._14)
    }
    this_1c706c24._15
  }
  def get_p_container_7dd38aad(
      this_1c706c24: Part
  )(implicit zone: Zone): Str = {
    if (this_1c706c24._12 == null) {
      this_1c706c24._12 = get_Str_6c2c8513(this_1c706c24._11)
    }
    this_1c706c24._12
  }
  def get_p_mfgr_625dc87f(this_1c706c24: Part)(implicit zone: Zone): Str = {
    if (this_1c706c24._5 == null) {
      this_1c706c24._5 = get_Str_6c2c8513(this_1c706c24._4)
    }
    this_1c706c24._5
  }
  def get_p_name_36b6a03a(this_1c706c24: Part)(implicit zone: Zone): Str = {
    if (this_1c706c24._3 == null) {
      this_1c706c24._3 = get_Str_6c2c8513(this_1c706c24._2)
    }
    this_1c706c24._3
  }
  def get_p_retailprice_1b89bdc7(
      this_1c706c24: Part
  )(implicit zone: Zone): Double = {
    this_1c706c24._13
  }
  def get_p_size_7a6ecaf9(this_1c706c24: Part)(implicit zone: Zone): Int = {
    this_1c706c24._10
  }
  def get_p_type_76fcfbfb(this_1c706c24: Part)(implicit zone: Zone): Str = {
    if (this_1c706c24._9 == null) {
      this_1c706c24._9 = get_Str_6c2c8513(this_1c706c24._8)
    }
    this_1c706c24._9
  }
  def get_ps_availqty_2379ce95(
      this_75c8326e: PartSupp
  )(implicit zone: Zone): Int = {
    this_75c8326e._6
  }
  def get_ps_comment_2fccf101(
      this_75c8326e: PartSupp
  )(implicit zone: Zone): Str = {
    if (this_75c8326e._9 == null) {
      this_75c8326e._9 = get_Str_6c2c8513(this_75c8326e._8)
    }
    this_75c8326e._9
  }
  def get_ps_part_3e7ee420(
      this_75c8326e: PartSupp
  )(implicit zone: Zone): Part = {
    if (this_75c8326e._3 == null) {
      this_75c8326e._3 = get_Part_4526bedc(this_75c8326e._2)
    }
    this_75c8326e._3
  }
  def get_ps_supp_a91fac3(
      this_75c8326e: PartSupp
  )(implicit zone: Zone): Supplier = {
    if (this_75c8326e._5 == null) {
      this_75c8326e._5 = get_Supplier_1c8c1800(this_75c8326e._4)
    }
    this_75c8326e._5
  }
  def get_ps_supplycost_67718062(
      this_75c8326e: PartSupp
  )(implicit zone: Zone): Double = {
    this_75c8326e._7
  }
  def get_r_comment_666b9f4f(
      this_2ffd3174: Region
  )(implicit zone: Zone): Str = {
    if (this_2ffd3174._5 == null) {
      this_2ffd3174._5 = get_Str_6c2c8513(this_2ffd3174._4)
    }
    this_2ffd3174._5
  }
  def get_r_name_632936a3(this_2ffd3174: Region)(implicit zone: Zone): Str = {
    if (this_2ffd3174._3 == null) {
      this_2ffd3174._3 = get_Str_6c2c8513(this_2ffd3174._2)
    }
    this_2ffd3174._3
  }
  def get_s_acctbal_4e802ec7(
      this_8b0d834: Supplier
  )(implicit zone: Zone): Double = {
    this_8b0d834._10
  }
  def get_s_address_742b41bf(
      this_8b0d834: Supplier
  )(implicit zone: Zone): Str = {
    if (this_8b0d834._5 == null) {
      this_8b0d834._5 = get_Str_6c2c8513(this_8b0d834._4)
    }
    this_8b0d834._5
  }
  def get_s_comment_5e9dedb7(
      this_8b0d834: Supplier
  )(implicit zone: Zone): Str = {
    if (this_8b0d834._12 == null) {
      this_8b0d834._12 = get_Str_6c2c8513(this_8b0d834._11)
    }
    this_8b0d834._12
  }
  def get_s_name_50e2ff28(this_8b0d834: Supplier)(implicit zone: Zone): Str = {
    if (this_8b0d834._3 == null) {
      this_8b0d834._3 = get_Str_6c2c8513(this_8b0d834._2)
    }
    this_8b0d834._3
  }
  def get_s_nation_1fdb05e5(
      this_8b0d834: Supplier
  )(implicit zone: Zone): Nation = {
    if (this_8b0d834._7 == null) {
      this_8b0d834._7 = get_Nation_553e7d68(this_8b0d834._6)
    }
    this_8b0d834._7
  }
  def get_s_phone_31539918(this_8b0d834: Supplier)(implicit zone: Zone): Str = {
    if (this_8b0d834._9 == null) {
      this_8b0d834._9 = get_Str_6c2c8513(this_8b0d834._8)
    }
    this_8b0d834._9
  }
  def get_string_474e9e55(this_922ad0: Str)(implicit zone: Zone): String = {
    fromCString_5ed2ddf(this_922ad0._2)
  }

  def set_c_acctbal_65fd8370(this_2a7e11f2: Customer, c_acctbal: Double)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._12 = c_acctbal
  }
  def set_c_address_5f4cf5ea(this_2a7e11f2: Customer, c_address: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._8 = c_address._1
    this_2a7e11f2._9 = c_address
  }
  def set_c_comment_5b0104a9(this_2a7e11f2: Customer, c_comment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._13 = c_comment._1
    this_2a7e11f2._14 = c_comment
  }
  def set_c_mktsegment_52eeeaba(this_2a7e11f2: Customer, c_mktsegment: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._2 = c_mktsegment._1
    this_2a7e11f2._3 = c_mktsegment
  }
  def set_c_name_31267b6f(this_2a7e11f2: Customer, c_name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._6 = c_name._1
    this_2a7e11f2._7 = c_name
  }
  def set_c_nation_9fd3b04(this_2a7e11f2: Customer, c_nation: Nation)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._4 = c_nation._1
    this_2a7e11f2._5 = c_nation
  }
  def set_c_phone_5a8a52de(this_2a7e11f2: Customer, c_phone: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2a7e11f2._10 = c_phone._1
    this_2a7e11f2._11 = c_phone
  }

  def big_scan: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7a54a5d7.dbiOpen();
      Nation_table_1d230f23.dbiOpen();
      Region_table_7564313c.dbiOpen();
      Part_table_5ce5201f.dbiOpen();
      Supplier_table_fc489e8.dbiOpen();
      PartSupp_table_70bb721f.dbiOpen();
      Customer_table_39b9b25e.dbiOpen();
      Order_table_283a5c04.dbiOpen();
      Lineitem_table_50ef93b7.dbiOpen();
      var res_0: scala.Double = 0.0;
      val cursor_1 = Lineitem_table_50ef93b7.cursorOpen();
      val x_2 = Lineitem_table_50ef93b7.last(cursor_1);
      val x_3 = empty_pointers_Lineitem_29085f9d(x_2);
      var v_4: Lineitem = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_l_tax_396698bb(row_6);
            res_0 = x_7.+(x_8);
            true
          })
      }) {
        val x_9 = Lineitem_table_50ef93b7.prev(cursor_1);
        val x_10 = empty_pointers_Lineitem_29085f9d(x_9);
        v_4 = x_10
      };
      Lineitem_table_50ef93b7.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7a54a5d7.dbiClose();
      Nation_table_1d230f23.dbiClose();
      Region_table_7564313c.dbiClose();
      Part_table_5ce5201f.dbiClose();
      Supplier_table_fc489e8.dbiClose();
      PartSupp_table_70bb721f.dbiClose();
      Customer_table_39b9b25e.dbiClose();
      Order_table_283a5c04.dbiClose();
      Lineitem_table_50ef93b7.dbiClose();
      x_11
    }
  }
  def cross_join: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7a54a5d7.dbiOpen();
      Nation_table_1d230f23.dbiOpen();
      Region_table_7564313c.dbiOpen();
      Part_table_5ce5201f.dbiOpen();
      Supplier_table_fc489e8.dbiOpen();
      PartSupp_table_70bb721f.dbiOpen();
      Customer_table_39b9b25e.dbiOpen();
      Order_table_283a5c04.dbiOpen();
      Lineitem_table_50ef93b7.dbiOpen();
      var res_0: scala.Double = scala.Double.MinValue;
      val cursor_1 = Supplier_table_fc489e8.cursorOpen();
      val x_2 = Supplier_table_fc489e8.last(cursor_1);
      val x_3 = empty_pointers_Supplier_3bb13a74(x_2);
      var v_4: Supplier = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Supplier_table_fc489e8.cursorOpen();
            val x_8 = Supplier_table_fc489e8.last(cursor_7);
            val x_9 = empty_pointers_Supplier_3bb13a74(x_8);
            var v_10: Supplier = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 = get_s_acctbal_4e802ec7(row1_6);
                  val x_14 = get_s_acctbal_4e802ec7(row2_12);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.doubleWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Supplier_table_fc489e8.prev(cursor_7);
              val x_20 = empty_pointers_Supplier_3bb13a74(x_19);
              v_10 = x_20
            };
            Supplier_table_fc489e8.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Supplier_table_fc489e8.prev(cursor_1);
        val x_22 = empty_pointers_Supplier_3bb13a74(x_21);
        v_4 = x_22
      };
      Supplier_table_fc489e8.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_7a54a5d7.dbiClose();
      Nation_table_1d230f23.dbiClose();
      Region_table_7564313c.dbiClose();
      Part_table_5ce5201f.dbiClose();
      Supplier_table_fc489e8.dbiClose();
      PartSupp_table_70bb721f.dbiClose();
      Customer_table_39b9b25e.dbiClose();
      Order_table_283a5c04.dbiClose();
      Lineitem_table_50ef93b7.dbiClose();
      x_23
    }
  }
  def key_join: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7a54a5d7.dbiOpen();
      Nation_table_1d230f23.dbiOpen();
      Region_table_7564313c.dbiOpen();
      Part_table_5ce5201f.dbiOpen();
      Supplier_table_fc489e8.dbiOpen();
      PartSupp_table_70bb721f.dbiOpen();
      Customer_table_39b9b25e.dbiOpen();
      Order_table_283a5c04.dbiOpen();
      Lineitem_table_50ef93b7.dbiOpen();
      val brand_0 = toCString("Brand#44");
      var res_1: scala.Double = 0.0;
      val cursor_2 = Lineitem_table_50ef93b7.cursorOpen();
      val x_3 = Lineitem_table_50ef93b7.last(cursor_2);
      val x_4 = empty_pointers_Lineitem_29085f9d(x_3);
      var v_5: Lineitem = x_4;
      while ({
        val x_6 = v_5;
        x_6
          .!=(null)
          .&&({
            val row_7 = v_5;
            val ps_8 = get_l_partsupp_4a4b4886(row_7);
            val p_9 = get_ps_part_3e7ee420(ps_8);
            val x_10 = get_ps_supplycost_67718062(ps_8);
            val x_13 = x_10
              .<(50.0)
              .&&({
                val x_11 = get_p_brand_5f2173d5(p_9);
                val x_12 = strcmp_771ceab4(scala.Tuple2(x_11, brand_0));
                x_12.asInstanceOf[scala.Int].!=(0)
              });
            if (x_13) {
              val x_14 = res_1;
              val x_15 = get_l_extendedprice_30b81918(row_7);
              val x_16 = get_l_discount_4006dbc7(row_7);
              res_1 = x_14.+(x_15.*(x_16));
              true
            } else
              true
          })
      }) {
        val x_17 = Lineitem_table_50ef93b7.prev(cursor_2);
        val x_18 = empty_pointers_Lineitem_29085f9d(x_17);
        v_5 = x_18
      };
      Lineitem_table_50ef93b7.cursorClose(cursor_2);
      val x_19 = res_1;
      LMDBTable.txnCommit();
      Str_table_7a54a5d7.dbiClose();
      Nation_table_1d230f23.dbiClose();
      Region_table_7564313c.dbiClose();
      Part_table_5ce5201f.dbiClose();
      Supplier_table_fc489e8.dbiClose();
      PartSupp_table_70bb721f.dbiClose();
      Customer_table_39b9b25e.dbiClose();
      Order_table_283a5c04.dbiClose();
      Lineitem_table_50ef93b7.dbiClose();
      x_19
    }
  }
  def tpch_query_6: Double = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7a54a5d7.dbiOpen();
      Nation_table_1d230f23.dbiOpen();
      Region_table_7564313c.dbiOpen();
      Part_table_5ce5201f.dbiOpen();
      Supplier_table_fc489e8.dbiOpen();
      PartSupp_table_70bb721f.dbiOpen();
      Customer_table_39b9b25e.dbiOpen();
      Order_table_283a5c04.dbiOpen();
      Lineitem_table_50ef93b7.dbiOpen();
      val s1_0 = toCString("1994-01-01");
      val s2_1 = toCString("1995-01-01");
      var res_2: scala.Double = 0.0;
      val cursor_3 = Lineitem_table_50ef93b7.cursorOpen();
      val x_4 = Lineitem_table_50ef93b7.last(cursor_3);
      val x_5 = empty_pointers_Lineitem_29085f9d(x_4);
      var v_6: Lineitem = x_5;
      while ({
        val x_7 = v_6;
        x_7
          .!=(null)
          .&&({
            val row_8 = v_6;
            val x_9 = get_l_quantity_24978f7f(row_8);
            val x_11 = x_9
              .<(24)
              .&&({
                val x_10 = get_l_discount_4006dbc7(row_8);
                x_10.>=(0.05)
              });
            val x_13 = x_11.&&({
              val x_12 = get_l_discount_4006dbc7(row_8);
              x_12.<=(0.07)
            });
            val x_16 = x_13.&&({
              val x_14 = get_l_shipdate_3334a97a(row_8);
              val x_15 = strcmp_771ceab4(scala.Tuple2(x_14, s1_0));
              x_15.asInstanceOf[scala.Int].>=(0)
            });
            val x_19 = x_16.&&({
              val x_17 = get_l_shipdate_3334a97a(row_8);
              val x_18 = strcmp_771ceab4(scala.Tuple2(x_17, s2_1));
              x_18.asInstanceOf[scala.Int].<(0)
            });
            if (x_19) {
              val x_20 = res_2;
              val x_21 = get_l_extendedprice_30b81918(row_8);
              val x_22 = get_l_discount_4006dbc7(row_8);
              res_2 = x_20.+(x_21.*(x_22));
              true
            } else
              true
          })
      }) {
        val x_23 = Lineitem_table_50ef93b7.prev(cursor_3);
        val x_24 = empty_pointers_Lineitem_29085f9d(x_23);
        v_6 = x_24
      };
      Lineitem_table_50ef93b7.cursorClose(cursor_3);
      val x_25 = res_2;
      LMDBTable.txnCommit();
      Str_table_7a54a5d7.dbiClose();
      Nation_table_1d230f23.dbiClose();
      Region_table_7564313c.dbiClose();
      Part_table_5ce5201f.dbiClose();
      Supplier_table_fc489e8.dbiClose();
      PartSupp_table_70bb721f.dbiClose();
      Customer_table_39b9b25e.dbiClose();
      Order_table_283a5c04.dbiClose();
      Lineitem_table_50ef93b7.dbiClose();
      x_25
    }
  }
}

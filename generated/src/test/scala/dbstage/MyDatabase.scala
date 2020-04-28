import scala.collection.mutable
import scala.scalanative.unsafe._
import lib.string._
import lib.str._
import lib.LMDBTable
import lib.LMDBTable._

object MyDatabase {
  type PersonKey = Long
  type PersonData = CStruct2[Int, Int]
  type Person = Ptr[PersonData]
  lazy val Person_table_355c14ad = new LMDBTable[Person]("Person")

  def get_Person_56e0d800(table: LMDBTable[Person], key: Long)(
      implicit zone: Zone
  ): Person = {
    val ptr0 = table.get(key)
    val size0 = 0L
    val size1 = sizeof[Int].toInt
    val size2 = sizeof[Int].toInt
    val size = size1 + size2
    val ptr1 = ptr0 + size0
    val value1 = intget(ptr1, size1)
    val ptr2 = ptr1 + size1
    val value2 = intget(ptr2, size2)
    init_Person_11da6f5d(value1, value2)
  }

  def put_Person_20f4f50c(table: LMDBTable[Person], fields: Person)(
      implicit zone: Zone
  ): Unit = {
    val key = table.size
    val size0 = 0L
    val size1 = sizeof[Int]
    val size2 = sizeof[Int]
    val size = size1 + size2
    val value0 = alloc[Byte](size)
    val value1 = value0 + size0
    intcpy(value1, fields._1, size1)
    val value2 = value1 + size1
    intcpy(value2, fields._2, size2)
    val value = value0
    table.put(key, size, value)
  }

  def init_Str_4bbbe4e4(str: String)(implicit zone: Zone): CString =
    toCString(str)

  def strlen_53cbb413(param: CString)(implicit zone: Zone): Long = strlen(param)
  def charAt_6cbc915e(params: Tuple2[CString, Long])(
      implicit zone: Zone
  ): Byte = charAt(params._1, params._2)

  def init_Person_11da6f5d(
      params: Tuple2[Int, Int]
  )(implicit zone: Zone): Person = {
    val this_92b3da3: Person = alloc[PersonData]
    this_92b3da3._1 = params._1
    this_92b3da3._2 = params._2
    this_92b3da3
  }

  def isMinor_75641a6(this_92b3da3: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_43c21818(this_92b3da3);
    x_0.<(18)
  }

  def salaray_321dcd0a(this_92b3da3: Person)(implicit zone: Zone): Int =
    this_92b3da3._1
  def age_43c21818(this_92b3da3: Person)(implicit zone: Zone): Int =
    this_92b3da3._2

  def `age_=_7230c91c`(this_92b3da3: Person, age: Int)(
      implicit zone: Zone
  ): Unit = this_92b3da3._2 = age
  def `salaray_=_5e1b74a1`(this_92b3da3: Person, salaray: Int)(
      implicit zone: Zone
  ): Unit = this_92b3da3._1 = salaray

  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_355c14ad.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_56e0d800(Person_table_355c14ad, i_4);
            val x_6 = isMinor_75641a6(x_5);
            if (x_6.asInstanceOf[scala.Boolean]) {
              val x_7 = res_0;
              res_0 = x_7.+(1);
              true
            } else
              true
          })
      }) {
        val x_8 = i_2;
        i_2 = x_8.+(1)
      };
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_355c14ad.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_56e0d800(Person_table_355c14ad, i_4);
            val x_6 = age_43c21818(x_5);
            `age_=_7230c91c`(x_5, x_6.+(10));
            val x_7 = res_0;
            res_0 = x_7.+(1);
            true
          })
      }) {
        val x_8 = i_2;
        i_2 = x_8.+(1)
      };
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_355c14ad.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_56e0d800(Person_table_355c14ad, i_4);
            val x_6 = salaray_321dcd0a(x_5);
            val x_7 = age_43c21818(x_5);
            val x_8 = init_Person_11da6f5d(scala.Tuple2(x_6, x_7.+(100)));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = i_2;
        i_2 = x_10.+(1)
      };
      res_0
    }
  }
}

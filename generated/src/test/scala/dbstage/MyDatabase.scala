import scala.collection.mutable
import scala.scalanative.unsafe._
import lib.string._
import lib.str._
import lib.LMDBTable
import lib.LMDBTable._

object MyDatabase {
  val sizeInt = sizeof[Int].toInt
  val sizeLong = sizeof[Long].toInt
  val sizeDouble = sizeof[Double].toInt

  class Job(
      val key: Long,
      var size: Int,
      var enterpriseId: Long,
      var enterprise: Str = null
  )
  val sizeJob = sizeInt + sizeLong
  lazy val Job_table_465293c2 = new LMDBTable[Job]("Job")

  class Person(
      val key: Long,
      var salary: Int,
      var nameId: Long,
      var name: Str = null,
      var age: Int,
      var jobId: Long,
      var job: Job = null
  )
  val sizePerson = sizeInt + sizeLong + sizeInt + sizeLong
  lazy val Person_table_41bb2654 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_54c1661c = new LMDBTable[Str]("Str")

  def toCString_5c1cb64e(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_49bd99c6(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_465293c2.get(key)
    fromPtrByte_Job_1613ec9e(key, value)
  }
  def get_Person_69a5e272(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_41bb2654.get(key)
    fromPtrByte_Person_70aaf879(key, value)
  }
  def get_Str_458d42a0(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_54c1661c.get(key)
    fromPtrByte_Str_605b84c4(key, value)
  }

  def put_Job_3745cf1a(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_18f206e6(data_el)
    Job_table_465293c2.put(key, size, value)
  }
  def put_Person_77b77a8e(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_4d66d379(data_el)
    Person_table_41bb2654.put(key, size, value)
  }
  def put_Str_66deaa42(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_7f7a87a8(data_el)
    Str_table_54c1661c.put(key, size, value)
  }

  def fromPtrByte_Job_1613ec9e(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_51f72ad4_key(key, (value1, value2))
  }
  def fromPtrByte_Person_70aaf879(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Person = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    val value3 = intget(ptr3, sizeInt)
    val ptr4 = ptr3 + sizeInt
    val value4 = longget(ptr4, sizeLong)
    val ptr5 = ptr4 + sizeLong
    init_Person_5e4dfee3_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Str_605b84c4(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_79756227(key, value1)
  }

  def toPtrByte_Job_18f206e6(
      new_value: Job
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = sizeJob
    val value1 = alloc[Byte](size)
    intcpy(value1, new_value.size, sizeInt)
    val value2 = value1 + sizeInt
    longcpy(value2, new_value.enterpriseId, sizeLong)
    val value3 = value2 + sizeLong
    (key, size, value1)
  }
  def toPtrByte_Person_4d66d379(
      new_value: Person
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = sizePerson
    val value1 = alloc[Byte](size)
    intcpy(value1, new_value.salary, sizeInt)
    val value2 = value1 + sizeInt
    longcpy(value2, new_value.nameId, sizeLong)
    val value3 = value2 + sizeLong
    intcpy(value3, new_value.age, sizeInt)
    val value4 = value3 + sizeInt
    longcpy(value4, new_value.jobId, sizeLong)
    val value5 = value4 + sizeLong
    (key, size, value1)
  }
  def toPtrByte_Str_7f7a87a8(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Job_51f72ad4_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_51f72ad4_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_465293c2.size, params._1, params._2, null)
    put_Job_3745cf1a(new_val)
    new_val
  }
  def init_Job_51f72ad4(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_51f72ad4(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_465293c2.size, params._1, params._2.key, params._2)
    put_Job_3745cf1a(new_val)
    new_val
  }
  def init_Person_5e4dfee3_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_5e4dfee3_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_41bb2654.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_77b77a8e(new_val)
    new_val
  }
  def init_Person_5e4dfee3(key: Long, params: Tuple4[Int, Str, Int, Job])(
      implicit zone: Zone
  ): Person = {
    val new_val = new Person(
      key,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    new_val
  }
  def init_Person_5e4dfee3(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_41bb2654.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_77b77a8e(new_val)
    new_val
  }
  def init_Str_79756227(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_79756227(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_54c1661c.size, param)
    put_Str_66deaa42(new_val)
    new_val
  }

  def isMinor_7cdf02b9(this_5204f022: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_7a227403(this_5204f022);
    x_0.<(18)
  }

  def charAt_714cd732(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_1e7aac22(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_7a227403(this_5204f022: Person)(implicit zone: Zone): Int = {
    this_5204f022.age
  }
  def enterprise_180dd013(this_1b13eb30: Job)(implicit zone: Zone): Str = {
    if (this_1b13eb30.enterprise == null) {
      this_1b13eb30.enterprise = get_Str_458d42a0(this_1b13eb30.enterpriseId)
    }
    this_1b13eb30.enterprise
  }
  def job_73c74b4a(this_5204f022: Person)(implicit zone: Zone): Job = {
    if (this_5204f022.job == null) {
      this_5204f022.job = get_Job_49bd99c6(this_5204f022.jobId)
    }
    this_5204f022.job
  }
  def name_49fa25d1(this_5204f022: Person)(implicit zone: Zone): Str = {
    if (this_5204f022.name == null) {
      this_5204f022.name = get_Str_458d42a0(this_5204f022.nameId)
    }
    this_5204f022.name
  }
  def salary_71f0c345(this_5204f022: Person)(implicit zone: Zone): Int = {
    this_5204f022.salary
  }
  def size_70b0820(this_1b13eb30: Job)(implicit zone: Zone): Int = {
    this_1b13eb30.size
  }
  def string_4edf8e90(this_1d08c837: Str)(implicit zone: Zone): CString = {
    this_1d08c837.string
  }

  def `age_=_4c02190f`(this_5204f022: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5204f022.age = age
  }
  def `enterprise_=_529b4fe`(this_1b13eb30: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_1b13eb30.enterpriseId = enterprise.key
    this_1b13eb30.enterprise = enterprise
  }
  def `job_=_535b2d2d`(this_5204f022: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_5204f022.jobId = job.key
    this_5204f022.job = job
  }
  def `name_=_70791527`(this_5204f022: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_5204f022.nameId = name.key
    this_5204f022.name = name
  }
  def `salary_=_2cefb046`(this_5204f022: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5204f022.salary = salary
  }
  def `size_=_64a97f61`(this_1b13eb30: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1b13eb30.size = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = isMinor_7cdf02b9(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_10
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = salary_71f0c345(x_7);
            val x_9 = name_49fa25d1(x_7);
            val x_10 = age_7a227403(x_7);
            val x_11 = job_73c74b4a(x_7);
            val x_12 =
              init_Person_5e4dfee3(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_14
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = age_7a227403(x_7);
            `age_=_4c02190f`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_10
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = toCString_5c1cb64e("Test");
            val x_9 = init_Str_79756227(x_8);
            val x_10 = age_7a227403(x_7);
            val x_11 = job_73c74b4a(x_7);
            val x_12 =
              init_Person_5e4dfee3(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_14
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
              val x_8 = salary_71f0c345(x_7);
              val x_9 = name_49fa25d1(x_7);
              val x_10 = charAt_714cd732(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_5c1cb64e(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_79756227(x_11);
              val x_13 = age_7a227403(x_7);
              val x_14 = job_73c74b4a(x_7);
              val x_15 =
                init_Person_5e4dfee3(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              res_0 = x_16.+(1);
              true
            }
          )
      }) {
        val x_17 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_17
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_18
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      val x_0 = toCString_5c1cb64e("EPFL");
      val x_1 = init_Str_79756227(x_0);
      val x_2 = init_Job_51f72ad4(scala.Tuple2(10000, x_1));
      val x_3 = toCString_5c1cb64e("Lucien");
      val x_4 = init_Str_79756227(x_3);
      val x_5 = init_Person_5e4dfee3(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_5c1cb64e("John");
      val x_7 = init_Str_79756227(x_6);
      val x_8 = init_Person_5e4dfee3(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val cursor_8 = Person_table_41bb2654.cursorOpen();
            val x_9 = Person_table_41bb2654.first(cursor_8);
            var v_10: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_9;
            while ({
              val x_11 = v_10;
              x_11._2
                .!=(null)
                .&&({
                  val x_12 = v_10;
                  val x_13 = v_10;
                  val x_14 = fromPtrByte_Person_70aaf879(x_12._1, x_13._2);
                  val x_15 = res_0;
                  res_0 = x_15.+(1);
                  true
                })
            }) {
              val x_16 = Person_table_41bb2654.next(cursor_8);
              v_10 = x_16
            };
            Person_table_41bb2654.cursorClose(cursor_8);
            true
          })
      }) {
        val x_17 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_17
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_18
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val row_8 = job_73c74b4a(x_7);
            val row_9 = size_70b0820(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_12
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      val cursor_0 = Person_table_41bb2654.cursorOpen();
      val x_1 = Person_table_41bb2654.first(cursor_0);
      var v_2: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_1;
      while ({
        val x_3 = v_2;
        x_3._2
          .!=(null)
          .&&({
            val x_4 = v_2;
            val x_5 = v_2;
            val x_6 = fromPtrByte_Person_70aaf879(x_4._1, x_5._2);
            val x_7 = age_7a227403(x_6);
            scala.Predef.println(x_7);
            true
          })
      }) {
        val x_8 = Person_table_41bb2654.next(cursor_0);
        v_2 = x_8
      };
      Person_table_41bb2654.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_465293c2.cursorOpen();
      val x_2 = Job_table_465293c2.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_1613ec9e(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_465293c2.next(cursor_1);
        v_3 = x_9
      };
      Job_table_465293c2.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_10
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_9
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_10
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_54c1661c.cursorOpen();
      val x_2 = Str_table_54c1661c.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_605b84c4(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_54c1661c.next(cursor_1);
        v_3 = x_9
      };
      Str_table_54c1661c.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_10
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = salary_71f0c345(x_7);
            val x_9 = name_49fa25d1(x_7);
            val x_10 = name_49fa25d1(x_7);
            val x_11 = strlen_1e7aac22(x_10);
            val x_12 = job_73c74b4a(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_5e4dfee3(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_16
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_54c1661c.dbiOpen();
      Person_table_41bb2654.dbiOpen();
      Job_table_465293c2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_41bb2654.cursorOpen();
      val x_2 = Person_table_41bb2654.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70aaf879(x_5._1, x_6._2);
            val x_8 = res_0;
            val x_9 = age_7a227403(x_7);
            res_0 = x_9.+(x_8);
            true
          })
      }) {
        val x_10 = Person_table_41bb2654.next(cursor_1);
        v_3 = x_10
      };
      Person_table_41bb2654.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_54c1661c.dbiClose();
      Person_table_41bb2654.dbiClose();
      Job_table_465293c2.dbiClose();
      x_11
    }
  }
}

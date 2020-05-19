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
  lazy val Job_table_57ce6106 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_666bc262 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_7bc9c2f5 = new LMDBTable[Str]("Str")

  def toCString_235b3876(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_a92a27d(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_57ce6106.get(key)
    fromPtrByte_Job_3ff9111d(key, value)
  }
  def get_Person_3ad9357e(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_666bc262.get(key)
    fromPtrByte_Person_70f0a3d1(key, value)
  }
  def get_Str_52c1685d(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_7bc9c2f5.get(key)
    fromPtrByte_Str_4d5fbac1(key, value)
  }

  def put_Job_3db83a06(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_64e22547(data_el)
    Job_table_57ce6106.put(key, size, value)
  }
  def put_Person_435d8ca7(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_4f2f1701(data_el)
    Person_table_666bc262.put(key, size, value)
  }
  def put_Str_4af42556(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_36604196(data_el)
    Str_table_7bc9c2f5.put(key, size, value)
  }

  def fromPtrByte_Job_3ff9111d(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_52c337bf_key(key, (value1, value2))
  }
  def fromPtrByte_Person_70f0a3d1(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_1eb73de7_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Str_4d5fbac1(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_25931111(key, value1)
  }

  def toPtrByte_Job_64e22547(
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
  def toPtrByte_Person_4f2f1701(
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
  def toPtrByte_Str_36604196(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Job_52c337bf_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_52c337bf_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_57ce6106.size, params._1, params._2, null)
    put_Job_3db83a06(new_val)
    new_val
  }
  def init_Job_52c337bf(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_52c337bf(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_57ce6106.size, params._1, params._2.key, params._2)
    put_Job_3db83a06(new_val)
    new_val
  }
  def init_Person_1eb73de7_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_1eb73de7_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_666bc262.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_435d8ca7(new_val)
    new_val
  }
  def init_Person_1eb73de7(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_1eb73de7(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_666bc262.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_435d8ca7(new_val)
    new_val
  }
  def init_Str_25931111(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_25931111(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_7bc9c2f5.size, param)
    put_Str_4af42556(new_val)
    new_val
  }

  def isMinor_359bedd0(this_2ccf825e: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_6fdd8593(this_2ccf825e);
    x_0.<(18)
  }

  def charAt_22f77dbf(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_51584261(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_6fdd8593(this_2ccf825e: Person)(implicit zone: Zone): Int = {
    this_2ccf825e.age
  }
  def enterprise_fce79f8(this_5c7fbf0b: Job)(implicit zone: Zone): Str = {
    if (this_5c7fbf0b.enterprise == null) {
      this_5c7fbf0b.enterprise = get_Str_52c1685d(this_5c7fbf0b.enterpriseId)
    }
    this_5c7fbf0b.enterprise
  }
  def job_1b3d0433(this_2ccf825e: Person)(implicit zone: Zone): Job = {
    if (this_2ccf825e.job == null) {
      this_2ccf825e.job = get_Job_a92a27d(this_2ccf825e.jobId)
    }
    this_2ccf825e.job
  }
  def name_31a418b1(this_2ccf825e: Person)(implicit zone: Zone): Str = {
    if (this_2ccf825e.name == null) {
      this_2ccf825e.name = get_Str_52c1685d(this_2ccf825e.nameId)
    }
    this_2ccf825e.name
  }
  def salary_1ec7b137(this_2ccf825e: Person)(implicit zone: Zone): Int = {
    this_2ccf825e.salary
  }
  def size_62ba0aae(this_5c7fbf0b: Job)(implicit zone: Zone): Int = {
    this_5c7fbf0b.size
  }
  def string_11d58656(this_63505c7e: Str)(implicit zone: Zone): CString = {
    this_63505c7e.string
  }

  def `age_=_2ccf6098`(this_2ccf825e: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2ccf825e.age = age
  }
  def `enterprise_=_7eb7751`(this_5c7fbf0b: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_5c7fbf0b.enterpriseId = enterprise.key
    this_5c7fbf0b.enterprise = enterprise
  }
  def `job_=_65d226c1`(this_2ccf825e: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_2ccf825e.jobId = job.key
    this_2ccf825e.job = job
  }
  def `name_=_59655242`(this_2ccf825e: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2ccf825e.nameId = name.key
    this_2ccf825e.name = name
  }
  def `salary_=_ef93256`(this_2ccf825e: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2ccf825e.salary = salary
  }
  def `size_=_52f49d27`(this_5c7fbf0b: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5c7fbf0b.size = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = isMinor_359bedd0(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_666bc262.next(cursor_1);
        v_3 = x_10
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = salary_1ec7b137(x_7);
            val x_9 = name_31a418b1(x_7);
            val x_10 = age_6fdd8593(x_7);
            val x_11 = job_1b3d0433(x_7);
            val x_12 =
              init_Person_1eb73de7(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_666bc262.next(cursor_1);
        v_3 = x_14
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = age_6fdd8593(x_7);
            `age_=_2ccf6098`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_666bc262.next(cursor_1);
        v_3 = x_10
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = Str("Test");
            val x_9 = age_6fdd8593(x_7);
            val x_10 = job_1b3d0433(x_7);
            val x_11 =
              init_Person_1eb73de7(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_666bc262.next(cursor_1);
        v_3 = x_13
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_14 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_14
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
              val x_8 = salary_1ec7b137(x_7);
              val x_9 = name_31a418b1(x_7);
              val x_10 = charAt_22f77dbf(scala.Tuple2(x_9, 2L));
              val x_11 = Str(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = age_6fdd8593(x_7);
              val x_13 = job_1b3d0433(x_7);
              val x_14 =
                init_Person_1eb73de7(scala.Tuple4(x_8, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_666bc262.next(cursor_1);
        v_3 = x_16
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_17
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      val x_0 = Str("EPFL");
      val x_1 = init_Job_52c337bf(scala.Tuple2(10000, x_0));
      val x_2 = Str("Lucien");
      val x_3 = init_Person_1eb73de7(scala.Tuple4(1000, x_2, 21, x_1));
      val x_4 = Str("John");
      val x_5 = init_Person_1eb73de7(scala.Tuple4(100, x_4, 16, x_1));
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val cursor_8 = Person_table_666bc262.cursorOpen();
            val x_9 = Person_table_666bc262.first(cursor_8);
            var v_10: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_9;
            while ({
              val x_11 = v_10;
              x_11._2
                .!=(null)
                .&&({
                  val x_12 = v_10;
                  val x_13 = v_10;
                  val x_14 = fromPtrByte_Person_70f0a3d1(x_12._1, x_13._2);
                  val x_15 =
                    age_6fdd8593(((scala.Tuple2(x_7, x_14)): scala.Any)._1);
                  val x_16 =
                    age_6fdd8593(((scala.Tuple2(x_7, x_14)): scala.Any)._2);
                  val x_17 = scala.math.`package`.abs(x_15.-(x_16));
                  val x_18 = res_0;
                  val x_19 = scala.Predef.intWrapper(x_17);
                  val x_20 = x_19.max(x_18);
                  res_0 = x_20;
                  true
                })
            }) {
              val x_21 = Person_table_666bc262.next(cursor_8);
              v_10 = x_21
            };
            Person_table_666bc262.cursorClose(cursor_8);
            true
          })
      }) {
        val x_22 = Person_table_666bc262.next(cursor_1);
        v_3 = x_22
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val row_8 = job_1b3d0433(x_7);
            val row_9 = size_62ba0aae(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_666bc262.next(cursor_1);
        v_3 = x_12
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      val cursor_0 = Person_table_666bc262.cursorOpen();
      val x_1 = Person_table_666bc262.first(cursor_0);
      var v_2: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_1;
      while ({
        val x_3 = v_2;
        x_3._2
          .!=(null)
          .&&({
            val x_4 = v_2;
            val x_5 = v_2;
            val x_6 = fromPtrByte_Person_70f0a3d1(x_4._1, x_5._2);
            val x_7 = age_6fdd8593(x_6);
            scala.Predef.println(x_7);
            true
          })
      }) {
        val x_8 = Person_table_666bc262.next(cursor_0);
        v_2 = x_8
      };
      Person_table_666bc262.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_57ce6106.cursorOpen();
      val x_2 = Job_table_57ce6106.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_3ff9111d(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_57ce6106.next(cursor_1);
        v_3 = x_9
      };
      Job_table_57ce6106.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_10
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_666bc262.next(cursor_1);
        v_3 = x_9
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_10
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_7bc9c2f5.cursorOpen();
      val x_2 = Str_table_7bc9c2f5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_4d5fbac1(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_7bc9c2f5.next(cursor_1);
        v_3 = x_9
      };
      Str_table_7bc9c2f5.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_10
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = salary_1ec7b137(x_7);
            val x_9 = name_31a418b1(x_7);
            val x_10 = name_31a418b1(x_7);
            val x_11 = strlen_51584261(x_10);
            val x_12 = job_1b3d0433(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_1eb73de7(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_666bc262.next(cursor_1);
        v_3 = x_16
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7bc9c2f5.dbiOpen();
      Person_table_666bc262.dbiOpen();
      Job_table_57ce6106.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_666bc262.cursorOpen();
      val x_2 = Person_table_666bc262.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_70f0a3d1(x_5._1, x_6._2);
            val x_8 = res_0;
            val x_9 = age_6fdd8593(x_7);
            res_0 = x_9.+(x_8);
            true
          })
      }) {
        val x_10 = Person_table_666bc262.next(cursor_1);
        v_3 = x_10
      };
      Person_table_666bc262.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7bc9c2f5.dbiClose();
      Person_table_666bc262.dbiClose();
      Job_table_57ce6106.dbiClose();
      x_11
    }
  }
}

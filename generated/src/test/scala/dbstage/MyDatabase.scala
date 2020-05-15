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
  lazy val Job_table_39204160 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_3a296cff = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_779777a9 = new LMDBTable[Str]("Str")

  def toCString_64f5464f(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_62bcf13a(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_39204160.get(key)
    fromPtrByte_Job_2edae177(key, value)
  }
  def get_Person_5a21d3da(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_3a296cff.get(key)
    fromPtrByte_Person_2cb5041c(key, value)
  }
  def get_Str_26b950ca(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_779777a9.get(key)
    fromPtrByte_Str_230950b0(key, value)
  }

  def put_Job_7375e3ff(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_1fa6cfc1(data_el)
    Job_table_39204160.put(key, size, value)
  }
  def put_Person_7a539857(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_1011e779(data_el)
    Person_table_3a296cff.put(key, size, value)
  }
  def put_Str_f94e39b(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_5322881b(data_el)
    Str_table_779777a9.put(key, size, value)
  }

  def fromPtrByte_Job_2edae177(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_7656444c_key(key, (value1, value2))
  }
  def fromPtrByte_Person_2cb5041c(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_42e499eb_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Str_230950b0(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_68fbb46a(key, value1)
  }

  def toPtrByte_Job_1fa6cfc1(
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
  def toPtrByte_Person_1011e779(
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
  def toPtrByte_Str_5322881b(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Job_7656444c_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_7656444c_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_39204160.size, params._1, params._2, null)
    put_Job_7375e3ff(new_val)
    new_val
  }
  def init_Job_7656444c(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_7656444c(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_39204160.size, params._1, params._2.key, params._2)
    put_Job_7375e3ff(new_val)
    new_val
  }
  def init_Person_42e499eb_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_42e499eb_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_3a296cff.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_7a539857(new_val)
    new_val
  }
  def init_Person_42e499eb(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_42e499eb(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_3a296cff.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_7a539857(new_val)
    new_val
  }
  def init_Str_68fbb46a(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_68fbb46a(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_779777a9.size, param)
    put_Str_f94e39b(new_val)
    new_val
  }

  def isMinor_221c96fe(this_7a456025: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_17e864b4(this_7a456025);
    x_0.<(18)
  }

  def charAt_6d050d72(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_69ec280e(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_17e864b4(this_7a456025: Person)(implicit zone: Zone): Int = {
    this_7a456025.age
  }
  def enterprise_29b6d7c4(this_63ad2855: Job)(implicit zone: Zone): Str = {
    if (this_63ad2855.enterprise == null) {
      this_63ad2855.enterprise = get_Str_26b950ca(this_63ad2855.enterpriseId)
    }
    this_63ad2855.enterprise
  }
  def job_9fcd2c4(this_7a456025: Person)(implicit zone: Zone): Job = {
    if (this_7a456025.job == null) {
      this_7a456025.job = get_Job_62bcf13a(this_7a456025.jobId)
    }
    this_7a456025.job
  }
  def name_2329b4bb(this_7a456025: Person)(implicit zone: Zone): Str = {
    if (this_7a456025.name == null) {
      this_7a456025.name = get_Str_26b950ca(this_7a456025.nameId)
    }
    this_7a456025.name
  }
  def salary_13b628c9(this_7a456025: Person)(implicit zone: Zone): Int = {
    this_7a456025.salary
  }
  def size_c21d58c(this_63ad2855: Job)(implicit zone: Zone): Int = {
    this_63ad2855.size
  }
  def string_63245c3e(this_16dae98: Str)(implicit zone: Zone): CString = {
    this_16dae98.string
  }

  def `age_=_6c1f7066`(this_7a456025: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_7a456025.age = age
  }
  def `enterprise_=_5465e7a0`(this_63ad2855: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_63ad2855.enterpriseId = enterprise.key
    this_63ad2855.enterprise = enterprise
  }
  def `job_=_5af334f1`(this_7a456025: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_7a456025.jobId = job.key
    this_7a456025.job = job
  }
  def `name_=_2fff725c`(this_7a456025: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_7a456025.nameId = name.key
    this_7a456025.name = name
  }
  def `salary_=_3874ab85`(this_7a456025: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_7a456025.salary = salary
  }
  def `size_=_43d713cc`(this_63ad2855: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_63ad2855.size = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = isMinor_221c96fe(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_10
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = salary_13b628c9(x_7);
            val x_9 = name_2329b4bb(x_7);
            val x_10 = age_17e864b4(x_7);
            val x_11 = job_9fcd2c4(x_7);
            val x_12 =
              init_Person_42e499eb(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_14
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = age_17e864b4(x_7);
            `age_=_6c1f7066`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_10
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = toCString_64f5464f("Test");
            val x_9 = init_Str_68fbb46a(x_8);
            val x_10 = age_17e864b4(x_7);
            val x_11 = job_9fcd2c4(x_7);
            val x_12 =
              init_Person_42e499eb(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_14
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
              val x_8 = salary_13b628c9(x_7);
              val x_9 = name_2329b4bb(x_7);
              val x_10 = charAt_6d050d72(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_64f5464f(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_68fbb46a(x_11);
              val x_13 = age_17e864b4(x_7);
              val x_14 = job_9fcd2c4(x_7);
              val x_15 =
                init_Person_42e499eb(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              res_0 = x_16.+(1);
              true
            }
          )
      }) {
        val x_17 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_17
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_18
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      val x_0 = toCString_64f5464f("EPFL");
      val x_1 = init_Str_68fbb46a(x_0);
      val x_2 = init_Job_7656444c(scala.Tuple2(10000, x_1));
      val x_3 = toCString_64f5464f("Lucien");
      val x_4 = init_Str_68fbb46a(x_3);
      val x_5 = init_Person_42e499eb(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_64f5464f("John");
      val x_7 = init_Str_68fbb46a(x_6);
      val x_8 = init_Person_42e499eb(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val cursor_8 = Person_table_3a296cff.cursorOpen();
            val x_9 = Person_table_3a296cff.first(cursor_8);
            var v_10: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_9;
            while ({
              val x_11 = v_10;
              x_11._2
                .!=(null)
                .&&({
                  val x_12 = v_10;
                  val x_13 = v_10;
                  val x_14 = fromPtrByte_Person_2cb5041c(x_12._1, x_13._2);
                  val x_15 =
                    age_17e864b4(((scala.Tuple2(x_7, x_14)): scala.Any)._1);
                  val x_16 =
                    age_17e864b4(((scala.Tuple2(x_7, x_14)): scala.Any)._2);
                  val x_17 = scala.math.`package`.abs(x_15.-(x_16));
                  val x_18 = res_0;
                  val x_19 = scala.Predef.intWrapper(x_17);
                  val x_20 = x_19.max(x_18);
                  res_0 = x_20;
                  true
                })
            }) {
              val x_21 = Person_table_3a296cff.next(cursor_8);
              v_10 = x_21
            };
            Person_table_3a296cff.cursorClose(cursor_8);
            true
          })
      }) {
        val x_22 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_22
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val row_8 = job_9fcd2c4(x_7);
            val row_9 = size_c21d58c(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_12
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      val cursor_0 = Person_table_3a296cff.cursorOpen();
      val x_1 = Person_table_3a296cff.first(cursor_0);
      var v_2: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_1;
      while ({
        val x_3 = v_2;
        x_3._2
          .!=(null)
          .&&({
            val x_4 = v_2;
            val x_5 = v_2;
            val x_6 = fromPtrByte_Person_2cb5041c(x_4._1, x_5._2);
            val x_7 = age_17e864b4(x_6);
            scala.Predef.println(x_7);
            true
          })
      }) {
        val x_8 = Person_table_3a296cff.next(cursor_0);
        v_2 = x_8
      };
      Person_table_3a296cff.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_39204160.cursorOpen();
      val x_2 = Job_table_39204160.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_2edae177(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_39204160.next(cursor_1);
        v_3 = x_9
      };
      Job_table_39204160.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_10
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_9
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_10
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_779777a9.cursorOpen();
      val x_2 = Str_table_779777a9.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_230950b0(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_779777a9.next(cursor_1);
        v_3 = x_9
      };
      Str_table_779777a9.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_10
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = salary_13b628c9(x_7);
            val x_9 = name_2329b4bb(x_7);
            val x_10 = name_2329b4bb(x_7);
            val x_11 = strlen_69ec280e(x_10);
            val x_12 = job_9fcd2c4(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_42e499eb(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_16
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_779777a9.dbiOpen();
      Person_table_3a296cff.dbiOpen();
      Job_table_39204160.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3a296cff.cursorOpen();
      val x_2 = Person_table_3a296cff.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2cb5041c(x_5._1, x_6._2);
            val x_8 = res_0;
            val x_9 = age_17e864b4(x_7);
            res_0 = x_9.+(x_8);
            true
          })
      }) {
        val x_10 = Person_table_3a296cff.next(cursor_1);
        v_3 = x_10
      };
      Person_table_3a296cff.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_779777a9.dbiClose();
      Person_table_3a296cff.dbiClose();
      Job_table_39204160.dbiClose();
      x_11
    }
  }
}

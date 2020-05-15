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
  lazy val Job_table_451fe04a = new LMDBTable[Job]("Job")

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
  lazy val Person_table_75294a47 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_99e1a8f = new LMDBTable[Str]("Str")

  def toCString_5c0f87dd(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Str_205f1786(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_99e1a8f.get(key)
    fromPtrByte_Str_596c3446(key, value)
  }
  def get_Job_274cf981(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_451fe04a.get(key)
    fromPtrByte_Job_4e4d3f(key, value)
  }
  def get_Person_4781f07c(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_75294a47.get(key)
    fromPtrByte_Person_1fba272a(key, value)
  }

  def put_Str_7313f224(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_3b59f723(data_el)
    Str_table_99e1a8f.put(key, size, value)
  }
  def put_Job_302e390b(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_6501321a(data_el)
    Job_table_451fe04a.put(key, size, value)
  }
  def put_Person_285468a3(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_44ba30c2(data_el)
    Person_table_75294a47.put(key, size, value)
  }

  def fromPtrByte_Str_596c3446(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_50e91ae(key, value1)
  }
  def fromPtrByte_Job_4e4d3f(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_66985028_key(key, (value1, value2))
  }
  def fromPtrByte_Person_1fba272a(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_4afc71f3_key(key, (value1, value2, value3, value4))
  }

  def toPtrByte_Str_3b59f723(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Job_6501321a(
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
  def toPtrByte_Person_44ba30c2(
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

  def init_Job_66985028_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_66985028_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_451fe04a.size, params._1, params._2, null)
    put_Job_302e390b(new_val)
    new_val
  }
  def init_Job_66985028(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_66985028(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_451fe04a.size, params._1, params._2.key, params._2)
    put_Job_302e390b(new_val)
    new_val
  }
  def init_Person_4afc71f3_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_4afc71f3_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_75294a47.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_285468a3(new_val)
    new_val
  }
  def init_Person_4afc71f3(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_4afc71f3(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_75294a47.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_285468a3(new_val)
    new_val
  }
  def init_Str_50e91ae(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_50e91ae(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_99e1a8f.size, param)
    put_Str_7313f224(new_val)
    new_val
  }

  def isMinor_3772e0e5(this_1d5adcbb: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_5367be44(this_1d5adcbb);
    x_0.<(18)
  }

  def charAt_1e20a648(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_20b565c9(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def size_28f7e618(this_2bc4fb09: Job)(implicit zone: Zone): Int = {
    this_2bc4fb09.size
  }
  def enterprise_23e745e2(this_2bc4fb09: Job)(implicit zone: Zone): Str = {
    if (this_2bc4fb09.enterprise == null) {
      this_2bc4fb09.enterprise = get_Str_205f1786(this_2bc4fb09.enterpriseId)
    }
    this_2bc4fb09.enterprise
  }
  def name_43687661(this_1d5adcbb: Person)(implicit zone: Zone): Str = {
    if (this_1d5adcbb.name == null) {
      this_1d5adcbb.name = get_Str_205f1786(this_1d5adcbb.nameId)
    }
    this_1d5adcbb.name
  }
  def age_5367be44(this_1d5adcbb: Person)(implicit zone: Zone): Int = {
    this_1d5adcbb.age
  }
  def job_f3ea89(this_1d5adcbb: Person)(implicit zone: Zone): Job = {
    if (this_1d5adcbb.job == null) {
      this_1d5adcbb.job = get_Job_274cf981(this_1d5adcbb.jobId)
    }
    this_1d5adcbb.job
  }
  def salary_17230ba9(this_1d5adcbb: Person)(implicit zone: Zone): Int = {
    this_1d5adcbb.salary
  }
  def string_ef294fa(this_6266ffc9: Str)(implicit zone: Zone): CString = {
    this_6266ffc9.string
  }

  def `age_=_1e601b1d`(this_1d5adcbb: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1d5adcbb.age = age
  }
  def `job_=_4af196f`(this_1d5adcbb: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_1d5adcbb.jobId = job.key
    this_1d5adcbb.job = job
  }
  def `size_=_43405929`(this_2bc4fb09: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2bc4fb09.size = size
  }
  def `salary_=_5bef7a08`(this_1d5adcbb: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1d5adcbb.salary = salary
  }
  def `enterprise_=_45c93a9b`(this_2bc4fb09: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2bc4fb09.enterpriseId = enterprise.key
    this_2bc4fb09.enterprise = enterprise
  }
  def `name_=_39d1272c`(this_1d5adcbb: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_1d5adcbb.nameId = name.key
    this_1d5adcbb.name = name
  }

  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = salary_17230ba9(x_7);
            val x_9 = name_43687661(x_7);
            val x_10 = name_43687661(x_7);
            val x_11 = strlen_20b565c9(x_10);
            val x_12 = job_f3ea89(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_4afc71f3(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_75294a47.next(cursor_1);
        v_3 = x_16
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_17
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val row_8 = job_f3ea89(x_7);
            val row_9 = size_28f7e618(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_75294a47.next(cursor_1);
        v_3 = x_12
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_13
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_75294a47.next(cursor_1);
        v_3 = x_9
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_10
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val cursor_8 = Person_table_75294a47.cursorOpen();
            val x_9 = Person_table_75294a47.first(cursor_8);
            var v_10: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_9;
            while ({
              val x_11 = v_10;
              x_11._2
                .!=(null)
                .&&({
                  val x_12 = v_10;
                  val x_13 = v_10;
                  val x_14 = fromPtrByte_Person_1fba272a(x_12._1, x_13._2);
                  val x_15 = res_0;
                  res_0 = x_15.+(1);
                  true
                })
            }) {
              val x_16 = Person_table_75294a47.next(cursor_8);
              v_10 = x_16
            };
            Person_table_75294a47.cursorClose(cursor_8);
            true
          })
      }) {
        val x_17 = Person_table_75294a47.next(cursor_1);
        v_3 = x_17
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_18
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = age_5367be44(x_7);
            `age_=_1e601b1d`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_75294a47.next(cursor_1);
        v_3 = x_10
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_11
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = res_0;
            val x_9 = age_5367be44(x_7);
            res_0 = x_9.+(x_8);
            true
          })
      }) {
        val x_10 = Person_table_75294a47.next(cursor_1);
        v_3 = x_10
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_11
    }
  }
  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = isMinor_3772e0e5(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_75294a47.next(cursor_1);
        v_3 = x_10
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = salary_17230ba9(x_7);
            val x_9 = name_43687661(x_7);
            val x_10 = age_5367be44(x_7);
            val x_11 = job_f3ea89(x_7);
            val x_12 =
              init_Person_4afc71f3(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_75294a47.next(cursor_1);
        v_3 = x_14
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_15
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
            val x_8 = toCString_5c0f87dd("Test");
            val x_9 = init_Str_50e91ae(x_8);
            val x_10 = age_5367be44(x_7);
            val x_11 = job_f3ea89(x_7);
            val x_12 =
              init_Person_4afc71f3(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_75294a47.next(cursor_1);
        v_3 = x_14
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_15
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_99e1a8f.cursorOpen();
      val x_2 = Str_table_99e1a8f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_596c3446(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_99e1a8f.next(cursor_1);
        v_3 = x_9
      };
      Str_table_99e1a8f.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_10
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_75294a47.cursorOpen();
      val x_2 = Person_table_75294a47.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_1fba272a(x_5._1, x_6._2);
              val x_8 = salary_17230ba9(x_7);
              val x_9 = name_43687661(x_7);
              val x_10 = charAt_1e20a648(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_5c0f87dd(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_50e91ae(x_11);
              val x_13 = age_5367be44(x_7);
              val x_14 = job_f3ea89(x_7);
              val x_15 =
                init_Person_4afc71f3(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              res_0 = x_16.+(1);
              true
            }
          )
      }) {
        val x_17 = Person_table_75294a47.next(cursor_1);
        v_3 = x_17
      };
      Person_table_75294a47.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_18
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      val x_0 = toCString_5c0f87dd("EPFL");
      val x_1 = init_Str_50e91ae(x_0);
      val x_2 = init_Job_66985028(scala.Tuple2(10000, x_1));
      val x_3 = toCString_5c0f87dd("Lucien");
      val x_4 = init_Str_50e91ae(x_3);
      val x_5 = init_Person_4afc71f3(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_5c0f87dd("John");
      val x_7 = init_Str_50e91ae(x_6);
      val x_8 = init_Person_4afc71f3(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose()
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      val cursor_0 = Person_table_75294a47.cursorOpen();
      val x_1 = Person_table_75294a47.first(cursor_0);
      var v_2: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_1;
      while ({
        val x_3 = v_2;
        x_3._2
          .!=(null)
          .&&({
            val x_4 = v_2;
            val x_5 = v_2;
            val x_6 = fromPtrByte_Person_1fba272a(x_4._1, x_5._2);
            val x_7 = age_5367be44(x_6);
            scala.Predef.println(x_7);
            true
          })
      }) {
        val x_8 = Person_table_75294a47.next(cursor_0);
        v_2 = x_8
      };
      Person_table_75294a47.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_99e1a8f.dbiOpen();
      Person_table_75294a47.dbiOpen();
      Job_table_451fe04a.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_451fe04a.cursorOpen();
      val x_2 = Job_table_451fe04a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_4e4d3f(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_451fe04a.next(cursor_1);
        v_3 = x_9
      };
      Job_table_451fe04a.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_99e1a8f.dbiClose();
      Person_table_75294a47.dbiClose();
      Job_table_451fe04a.dbiClose();
      x_10
    }
  }
}

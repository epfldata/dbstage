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
  lazy val Job_table_6363a051 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_7a502ed3 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_36c9b455 = new LMDBTable[Str]("Str")

  def toCString_80469e(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_7ed2e7a0(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_6363a051.get(key)
    fromPtrByte_Job_5c28051(key, value)
  }
  def get_Str_7a82b29e(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_36c9b455.get(key)
    fromPtrByte_Str_7ce4fa52(key, value)
  }
  def get_Person_25560aa(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_7a502ed3.get(key)
    fromPtrByte_Person_6bd36597(key, value)
  }

  def put_Job_6f373ff2(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_46ce57b1(data_el)
    Job_table_6363a051.put(key, size, value)
  }
  def put_Str_4a14ad86(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_77710030(data_el)
    Str_table_36c9b455.put(key, size, value)
  }
  def put_Person_28cd603d(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_7e5318aa(data_el)
    Person_table_7a502ed3.put(key, size, value)
  }

  def fromPtrByte_Job_5c28051(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_38556a67_key(key, (value1, value2))
  }
  def fromPtrByte_Str_7ce4fa52(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_4d12eb0f(key, value1)
  }
  def fromPtrByte_Person_6bd36597(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_3e5810e9_key(key, (value1, value2, value3, value4))
  }

  def toPtrByte_Job_46ce57b1(
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
  def toPtrByte_Str_77710030(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Person_7e5318aa(
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

  def init_Job_38556a67_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_38556a67_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_6363a051.size, params._1, params._2, null)
    put_Job_6f373ff2(new_val)
    new_val
  }
  def init_Job_38556a67(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_38556a67(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_6363a051.size, params._1, params._2.key, params._2)
    put_Job_6f373ff2(new_val)
    new_val
  }
  def init_Person_3e5810e9_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_3e5810e9_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_7a502ed3.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_28cd603d(new_val)
    new_val
  }
  def init_Person_3e5810e9(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_3e5810e9(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_7a502ed3.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_28cd603d(new_val)
    new_val
  }
  def init_Str_4d12eb0f(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_4d12eb0f(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_36c9b455.size, param)
    put_Str_4a14ad86(new_val)
    new_val
  }

  def isMinor_fa55205(this_6732a836: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_15785756(this_6732a836);
    x_0.<(18)
  }

  def charAt_4f1eb5ec(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_9c4559d(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_15785756(this_6732a836: Person)(implicit zone: Zone): Int = {
    this_6732a836.age
  }
  def job_3503a555(this_6732a836: Person)(implicit zone: Zone): Job = {
    if (this_6732a836.job == null) {
      this_6732a836.job = get_Job_7ed2e7a0(this_6732a836.jobId)
    }
    this_6732a836.job
  }
  def salary_1e4066d5(this_6732a836: Person)(implicit zone: Zone): Int = {
    this_6732a836.salary
  }
  def string_4b2f7ed2(this_1343c126: Str)(implicit zone: Zone): CString = {
    this_1343c126.string
  }
  def size_2ea5312(this_621ecb76: Job)(implicit zone: Zone): Int = {
    this_621ecb76.size
  }
  def name_2a6b7717(this_6732a836: Person)(implicit zone: Zone): Str = {
    if (this_6732a836.name == null) {
      this_6732a836.name = get_Str_7a82b29e(this_6732a836.nameId)
    }
    this_6732a836.name
  }
  def enterprise_3780da41(this_621ecb76: Job)(implicit zone: Zone): Str = {
    if (this_621ecb76.enterprise == null) {
      this_621ecb76.enterprise = get_Str_7a82b29e(this_621ecb76.enterpriseId)
    }
    this_621ecb76.enterprise
  }

  def `age_=_178ac8ce`(this_6732a836: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6732a836.age = age
  }
  def `size_=_5ff7fc31`(this_621ecb76: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_621ecb76.size = size
  }
  def `job_=_52e43b2c`(this_6732a836: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_6732a836.jobId = job.key
    this_6732a836.job = job
  }
  def `salary_=_3e05f0bc`(this_6732a836: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6732a836.salary = salary
  }
  def `enterprise_=_7f88830a`(this_621ecb76: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_621ecb76.enterpriseId = enterprise.key
    this_621ecb76.enterprise = enterprise
  }
  def `name_=_1ffeeebe`(this_6732a836: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_6732a836.nameId = name.key
    this_6732a836.name = name
  }

  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      val x_0 = toCString_80469e("EPFL");
      val x_1 = init_Str_4d12eb0f(x_0);
      val x_2 = init_Job_38556a67(scala.Tuple2(10000, x_1));
      val x_3 = toCString_80469e("Lucien");
      val x_4 = init_Str_4d12eb0f(x_3);
      val x_5 = init_Person_3e5810e9(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_80469e("John");
      val x_7 = init_Str_4d12eb0f(x_6);
      val x_8 = init_Person_3e5810e9(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose()
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val x_8 = salary_1e4066d5(x_7);
            val x_9 = name_2a6b7717(x_7);
            val x_10 = name_2a6b7717(x_7);
            val x_11 = strlen_9c4559d(x_10);
            val x_12 = job_3503a555(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_3e5810e9(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_16
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_17
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_36c9b455.cursorOpen();
      val x_2 = Str_table_36c9b455.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_7ce4fa52(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_36c9b455.next(cursor_1);
        v_3 = x_9
      };
      Str_table_36c9b455.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_10
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_6363a051.cursorOpen();
      val x_2 = Job_table_6363a051.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_5c28051(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_6363a051.next(cursor_1);
        v_3 = x_9
      };
      Job_table_6363a051.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_10
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val x_8 = salary_1e4066d5(x_7);
            val x_9 = name_2a6b7717(x_7);
            val x_10 = age_15785756(x_7);
            val x_11 = job_3503a555(x_7);
            val x_12 =
              init_Person_3e5810e9(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_14
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_15
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val row_8 = job_3503a555(x_7);
            val row_9 = size_2ea5312(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_12
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_13
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_9
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_10
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val x_8 = toCString_80469e("Test");
            val x_9 = init_Str_4d12eb0f(x_8);
            val x_10 = age_15785756(x_7);
            val x_11 = job_3503a555(x_7);
            val x_12 =
              init_Person_3e5810e9(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_14
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_15
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            true
          })
      }) {
        val x_8 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_8
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_9
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
              val x_8 = age_15785756(x_7);
              if (x_8.==(21))
                delete_Person_27d75332(x_7)
              else
                ();
              val x_9 = salary_1e4066d5(x_7);
              val x_10 = name_2a6b7717(x_7);
              val x_11 = charAt_4f1eb5ec(scala.Tuple2(x_10, 2L));
              val x_12 =
                toCString_80469e(x_11.asInstanceOf[scala.Byte].toString());
              val x_13 = init_Str_4d12eb0f(x_12);
              val x_14 = age_15785756(x_7);
              val x_15 = job_3503a555(x_7);
              val x_16 =
                init_Person_3e5810e9(scala.Tuple4(x_9, x_13, x_14, x_15));
              val x_17 = res_0;
              res_0 = x_17.+(1);
              true
            }
          )
      }) {
        val x_18 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_18
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_19 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_19
    }
  }
  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val x_8 = isMinor_fa55205(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_10
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_11
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_36c9b455.dbiOpen();
      Person_table_7a502ed3.dbiOpen();
      Job_table_6363a051.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7a502ed3.cursorOpen();
      val x_2 = Person_table_7a502ed3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_6bd36597(x_5._1, x_6._2);
            val x_8 = age_15785756(x_7);
            `age_=_178ac8ce`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_7a502ed3.next(cursor_1);
        v_3 = x_10
      };
      Person_table_7a502ed3.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_36c9b455.dbiClose();
      Person_table_7a502ed3.dbiClose();
      Job_table_6363a051.dbiClose();
      x_11
    }
  }
}

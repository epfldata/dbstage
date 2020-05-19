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

  class Citizen(val key: Long, var nameId: Long, var name: Str = null)
  val sizeCitizen = 0 + sizeLong
  lazy val Citizen_table_29364668 = new LMDBTable[example.Citizen]("Citizen")

  class Job(
      val key: Long,
      var size: Int,
      var enterpriseId: Long,
      var enterprise: Str = null
  )
  val sizeJob = 0 + sizeInt + sizeLong
  lazy val Job_table_3391c2b5 = new LMDBTable[Job]("Job")

  class Person(
      val key: Long,
      var salary: Int,
      var nameId: Long,
      var name: Str = null,
      var age: Int,
      var jobId: Long,
      var job: Job = null
  )
  val sizePerson = 0 + sizeInt + sizeLong + sizeInt + sizeLong
  lazy val Person_table_47494b6f = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_1a86b6a6 = new LMDBTable[Str]("Str")

  def toCString_118b698(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Citizen_1ea2ddbb(key: Long)(implicit zone: Zone): example.Citizen = {
    val value = Citizen_table_29364668.get(key)
    fromPtrByte_Citizen_31be7eac(key, value)
  }
  def get_Job_6c1e85c4(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_3391c2b5.get(key)
    fromPtrByte_Job_5a4a1481(key, value)
  }
  def get_Person_26d09289(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_47494b6f.get(key)
    fromPtrByte_Person_2e17ddf5(key, value)
  }
  def get_Str_341ce439(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_1a86b6a6.get(key)
    fromPtrByte_Str_2cb474ce(key, value)
  }

  def put_Citizen_5ae1f3cb(
      data_el: example.Citizen
  )(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Citizen_98ff3ef(data_el)
    Citizen_table_29364668.put(key, size, value)
  }
  def put_Job_4c472185(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_5debca02(data_el)
    Job_table_3391c2b5.put(key, size, value)
  }
  def put_Person_6f0b705d(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_3aa1a369(data_el)
    Person_table_47494b6f.put(key, size, value)
  }
  def put_Str_190d537e(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_6cb30c64(data_el)
    Str_table_1a86b6a6.put(key, size, value)
  }

  def fromPtrByte_Citizen_31be7eac(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): example.Citizen = {
    val value1 = longget(ptr1, sizeLong)
    val ptr2 = ptr1 + sizeLong
    init_Citizen_1abd15c8_key(key, value1)
  }
  def fromPtrByte_Job_5a4a1481(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_413df6b4_key(key, (value1, value2))
  }
  def fromPtrByte_Person_2e17ddf5(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_60d36306_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Str_2cb474ce(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_1bc28f4f(key, value1)
  }

  def toPtrByte_Citizen_98ff3ef(
      new_value: example.Citizen
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = sizeCitizen
    val value1 = alloc[Byte](size)
    longcpy(value1, new_value.nameId, sizeLong)
    val value2 = value1 + sizeLong
    (key, size, value1)
  }
  def toPtrByte_Job_5debca02(
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
  def toPtrByte_Person_3aa1a369(
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
  def toPtrByte_Str_6cb30c64(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Citizen_1abd15c8_key(key: Long, params: Tuple2[Long, Long])(
      implicit zone: Zone
  ): example.Citizen = {
    val new_val = new Citizen(key, params._1, null)
    new_val
  }
  def init_Citizen_1abd15c8_key(
      params: Tuple2[Long, Long]
  )(implicit zone: Zone): example.Citizen = {
    val new_val = new Citizen(Citizen_table_29364668.size, params._1, null)
    put_Citizen_5ae1f3cb(new_val)
    new_val
  }
  def init_Citizen_1abd15c8(key: Long, params: Tuple2[Str, Long])(
      implicit zone: Zone
  ): example.Citizen = {
    val new_val = new Citizen(key, params._1.key, params._1)
    new_val
  }
  def init_Citizen_1abd15c8(
      params: Tuple2[Str, Long]
  )(implicit zone: Zone): example.Citizen = {
    val new_val =
      new Citizen(Citizen_table_29364668.size, params._1.key, params._1)
    put_Citizen_5ae1f3cb(new_val)
    new_val
  }
  def init_Job_413df6b4_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_413df6b4_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_3391c2b5.size, params._1, params._2, null)
    put_Job_4c472185(new_val)
    new_val
  }
  def init_Job_413df6b4(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_413df6b4(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_3391c2b5.size, params._1, params._2.key, params._2)
    put_Job_4c472185(new_val)
    new_val
  }
  def init_Person_60d36306_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_60d36306_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_47494b6f.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_6f0b705d(new_val)
    new_val
  }
  def init_Person_60d36306(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_60d36306(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_47494b6f.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_6f0b705d(new_val)
    new_val
  }
  def init_Str_1bc28f4f(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_1bc28f4f(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_1a86b6a6.size, param)
    put_Str_190d537e(new_val)
    new_val
  }

  def isMinor_78a6f575(this_74ab911a: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_5a8cb271(this_74ab911a);
    x_0.<(18)
  }
  def showKey_4399ede8(
      this_11634a1b: example.Citizen
  )(implicit zone: Zone): String = {
    val x_0 = key_567b3c83(this_11634a1b);
    x_0.toString()
  }

  def charAt_20fa1f5f(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_2587d0a6(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_5a8cb271(this_74ab911a: Person)(implicit zone: Zone): Int = {
    this_74ab911a.age
  }
  def enterprise_10b3ab9e(this_776d7b14: Job)(implicit zone: Zone): Str = {
    if (this_776d7b14.enterprise == null) {
      this_776d7b14.enterprise = get_Str_341ce439(this_776d7b14.enterpriseId)
    }
    this_776d7b14.enterprise
  }
  def job_7383e49(this_74ab911a: Person)(implicit zone: Zone): Job = {
    if (this_74ab911a.job == null) {
      this_74ab911a.job = get_Job_6c1e85c4(this_74ab911a.jobId)
    }
    this_74ab911a.job
  }
  def key_567b3c83(
      this_11634a1b: example.Citizen
  )(implicit zone: Zone): Long = {
    this_11634a1b.key
  }
  def name_29cfa710(
      this_11634a1b: example.Citizen
  )(implicit zone: Zone): Str = {
    if (this_11634a1b.name == null) {
      this_11634a1b.name = get_Str_341ce439(this_11634a1b.nameId)
    }
    this_11634a1b.name
  }
  def name_70f96d34(this_74ab911a: Person)(implicit zone: Zone): Str = {
    if (this_74ab911a.name == null) {
      this_74ab911a.name = get_Str_341ce439(this_74ab911a.nameId)
    }
    this_74ab911a.name
  }
  def salary_7d1bd7ee(this_74ab911a: Person)(implicit zone: Zone): Int = {
    this_74ab911a.salary
  }
  def size_3b976472(this_776d7b14: Job)(implicit zone: Zone): Int = {
    this_776d7b14.size
  }
  def string_6f34b337(this_7ebe11ea: Str)(implicit zone: Zone): CString = {
    this_7ebe11ea.string
  }

  def `age_=_3d4b0c5e`(this_74ab911a: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_74ab911a.age = age
  }
  def `enterprise_=_1666ae7c`(this_776d7b14: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_776d7b14.enterpriseId = enterprise.key
    this_776d7b14.enterprise = enterprise
  }
  def `job_=_261989b8`(this_74ab911a: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_74ab911a.jobId = job.key
    this_74ab911a.job = job
  }
  def `name_=_34039970`(this_74ab911a: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_74ab911a.nameId = name.key
    this_74ab911a.name = name
  }
  def `salary_=_514f4a52`(this_74ab911a: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_74ab911a.salary = salary
  }
  def `size_=_72791b49`(this_776d7b14: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_776d7b14.size = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = isMinor_78a6f575(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_10
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = salary_7d1bd7ee(x_7);
            val x_9 = name_70f96d34(x_7);
            val x_10 = age_5a8cb271(x_7);
            val x_11 = job_7383e49(x_7);
            val x_12 =
              init_Person_60d36306(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_14
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = age_5a8cb271(x_7);
            `age_=_3d4b0c5e`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_10
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = Str("Test");
            val x_9 = age_5a8cb271(x_7);
            val x_10 = job_7383e49(x_7);
            val x_11 =
              init_Person_60d36306(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_13
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_14 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_14
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
              val x_8 = salary_7d1bd7ee(x_7);
              val x_9 = name_70f96d34(x_7);
              val x_10 = charAt_20fa1f5f(scala.Tuple2(x_9, 2L));
              val x_11 = Str(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = age_5a8cb271(x_7);
              val x_13 = job_7383e49(x_7);
              val x_14 =
                init_Person_60d36306(scala.Tuple4(x_8, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_16
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_17
    }
  }
  def insertionAtKey: example.Citizen = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      val x_0 = Str("Bob");
      val x_1 = init_Citizen_1abd15c8(scala.Tuple2(x_0, 42L));
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      val x_0 = Str("EPFL");
      val x_1 = init_Job_413df6b4(scala.Tuple2(10000, x_0));
      val x_2 = Str("Lucien");
      val x_3 = init_Person_60d36306(scala.Tuple4(1000, x_2, 21, x_1));
      val x_4 = Str("John");
      val x_5 = init_Person_60d36306(scala.Tuple4(100, x_4, 16, x_1));
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val cursor_8 = Person_table_47494b6f.cursorOpen();
            val x_9 = Person_table_47494b6f.first(cursor_8);
            var v_10: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_9;
            while ({
              val x_11 = v_10;
              x_11._2
                .!=(null)
                .&&({
                  val x_12 = v_10;
                  val x_13 = v_10;
                  val x_14 = fromPtrByte_Person_2e17ddf5(x_12._1, x_13._2);
                  val x_15 =
                    age_5a8cb271(((scala.Tuple2(x_7, x_14)): scala.Any)._1);
                  val x_16 =
                    age_5a8cb271(((scala.Tuple2(x_7, x_14)): scala.Any)._2);
                  val x_17 = scala.math.`package`.abs(x_15.-(x_16));
                  val x_18 = res_0;
                  val x_19 = scala.Predef.intWrapper(x_17);
                  val x_20 = x_19.max(x_18);
                  res_0 = x_20;
                  true
                })
            }) {
              val x_21 = Person_table_47494b6f.next(cursor_8);
              v_10 = x_21
            };
            Person_table_47494b6f.cursorClose(cursor_8);
            true
          })
      }) {
        val x_22 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_22
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val row_8 = job_7383e49(x_7);
            val row_9 = size_3b976472(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_12
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      val cursor_0 = Person_table_47494b6f.cursorOpen();
      val x_1 = Person_table_47494b6f.first(cursor_0);
      var v_2: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_1;
      while ({
        val x_3 = v_2;
        x_3._2
          .!=(null)
          .&&({
            val x_4 = v_2;
            val x_5 = v_2;
            val x_6 = fromPtrByte_Person_2e17ddf5(x_4._1, x_5._2);
            val x_7 = age_5a8cb271(x_6);
            scala.Predef.println(x_7);
            true
          })
      }) {
        val x_8 = Person_table_47494b6f.next(cursor_0);
        v_2 = x_8
      };
      Person_table_47494b6f.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_3391c2b5.cursorOpen();
      val x_2 = Job_table_3391c2b5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_5a4a1481(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_3391c2b5.next(cursor_1);
        v_3 = x_9
      };
      Job_table_3391c2b5.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_10
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_9
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_10
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_1a86b6a6.cursorOpen();
      val x_2 = Str_table_1a86b6a6.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_2cb474ce(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_1a86b6a6.next(cursor_1);
        v_3 = x_9
      };
      Str_table_1a86b6a6.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_10
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = salary_7d1bd7ee(x_7);
            val x_9 = name_70f96d34(x_7);
            val x_10 = name_70f96d34(x_7);
            val x_11 = strlen_2587d0a6(x_10);
            val x_12 = job_7383e49(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_60d36306(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_16
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_1a86b6a6.dbiOpen();
      Person_table_47494b6f.dbiOpen();
      Job_table_3391c2b5.dbiOpen();
      Citizen_table_29364668.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_47494b6f.cursorOpen();
      val x_2 = Person_table_47494b6f.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_2e17ddf5(x_5._1, x_6._2);
            val x_8 = res_0;
            val x_9 = age_5a8cb271(x_7);
            res_0 = x_9.+(x_8);
            true
          })
      }) {
        val x_10 = Person_table_47494b6f.next(cursor_1);
        v_3 = x_10
      };
      Person_table_47494b6f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_1a86b6a6.dbiClose();
      Person_table_47494b6f.dbiClose();
      Job_table_3391c2b5.dbiClose();
      Citizen_table_29364668.dbiClose();
      x_11
    }
  }
}

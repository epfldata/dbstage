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
  lazy val Person_table_1d31793e = new LMDBTable[Person]("Person")

  class Job(
      val key: Long,
      var size: Int,
      var enterpriseId: Long,
      var enterprise: Str = null
  )
  val sizeJob = sizeInt + sizeLong
  lazy val Job_table_11886c9c = new LMDBTable[Job]("Job")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_68dce0af = new LMDBTable[Str]("Str")

  def toCString_19397905(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Str_523f30f5(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_68dce0af.get(key)
    fromPtrByte_Str_5de96a7f(key, value)
  }
  def get_Person_3720a2d3(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_1d31793e.get(key)
    fromPtrByte_Person_1c8ae6(key, value)
  }
  def get_Job_a9fb896(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_11886c9c.get(key)
    fromPtrByte_Job_210c9cda(key, value)
  }

  def put_Str_6eb7a40b(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_191bf4b6(data_el)
    Str_table_68dce0af.put(key, size, value)
  }
  def put_Person_7c28cdce(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_2af4dc4(data_el)
    Person_table_1d31793e.put(key, size, value)
  }
  def put_Job_69944404(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_3a5038b9(data_el)
    Job_table_11886c9c.put(key, size, value)
  }

  def fromPtrByte_Str_5de96a7f(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_2bd10cd9(key, value1)
  }
  def fromPtrByte_Person_1c8ae6(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_6ebcec5_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Job_210c9cda(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_6d18face_key(key, (value1, value2))
  }

  def toPtrByte_Str_191bf4b6(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Person_2af4dc4(
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
  def toPtrByte_Job_3a5038b9(
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

  def init_Job_6d18face_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_6d18face_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_11886c9c.size, params._1, params._2, null)
    put_Job_69944404(new_val)
    new_val
  }
  def init_Job_6d18face(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_6d18face(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_11886c9c.size, params._1, params._2.key, params._2)
    put_Job_69944404(new_val)
    new_val
  }
  def init_Person_6ebcec5_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_6ebcec5_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_1d31793e.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_7c28cdce(new_val)
    new_val
  }
  def init_Person_6ebcec5(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_6ebcec5(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_1d31793e.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_7c28cdce(new_val)
    new_val
  }
  def init_Str_2bd10cd9(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_2bd10cd9(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_68dce0af.size, param)
    put_Str_6eb7a40b(new_val)
    new_val
  }

  def isMinor_334421e4(this_dd14842: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_2e4ccea4(this_dd14842);
    x_0.<(18)
  }

  def charAt_6aec7dbc(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_180cc43(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_2e4ccea4(this_dd14842: Person)(implicit zone: Zone): Int = {
    this_dd14842.age
  }
  def job_6271f1d7(this_dd14842: Person)(implicit zone: Zone): Job = {
    if (this_dd14842.job == null) {
      this_dd14842.job = get_Job_a9fb896(this_dd14842.jobId)
    }
    this_dd14842.job
  }
  def salary_6514db0e(this_dd14842: Person)(implicit zone: Zone): Int = {
    this_dd14842.salary
  }
  def string_58d52ca(this_22fc7ee9: Str)(implicit zone: Zone): CString = {
    this_22fc7ee9.string
  }
  def size_2550075a(this_67036de7: Job)(implicit zone: Zone): Int = {
    this_67036de7.size
  }
  def name_48c2a56e(this_dd14842: Person)(implicit zone: Zone): Str = {
    if (this_dd14842.name == null) {
      this_dd14842.name = get_Str_523f30f5(this_dd14842.nameId)
    }
    this_dd14842.name
  }
  def enterprise_bcddbd3(this_67036de7: Job)(implicit zone: Zone): Str = {
    if (this_67036de7.enterprise == null) {
      this_67036de7.enterprise = get_Str_523f30f5(this_67036de7.enterpriseId)
    }
    this_67036de7.enterprise
  }

  def `age_=_793415e5`(this_dd14842: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_dd14842.age = age
  }
  def `size_=_533ceb72`(this_67036de7: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_67036de7.size = size
  }
  def `job_=_212ba65f`(this_dd14842: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_dd14842.jobId = job.key
    this_dd14842.job = job
  }
  def `salary_=_2f507929`(this_dd14842: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_dd14842.salary = salary
  }
  def `enterprise_=_7612da1b`(this_67036de7: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_67036de7.enterpriseId = enterprise.key
    this_67036de7.enterprise = enterprise
  }
  def `name_=_42c1d8d0`(this_dd14842: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_dd14842.nameId = name.key
    this_dd14842.name = name
  }

  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = salary_6514db0e(x_7);
            val x_9 = name_48c2a56e(x_7);
            val x_10 = age_2e4ccea4(x_7);
            val x_11 = job_6271f1d7(x_7);
            val x_12 =
              init_Person_6ebcec5(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_14
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_15
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val row_8 = job_6271f1d7(x_7);
            val row_9 = size_2550075a(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_12
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_13
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            true
          })
      }) {
        val x_8 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_8
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_9
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_11886c9c.cursorOpen();
      val x_2 = Job_table_11886c9c.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_210c9cda(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_11886c9c.next(cursor_1);
        v_3 = x_9
      };
      Job_table_11886c9c.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_10
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_68dce0af.cursorOpen();
      val x_2 = Str_table_68dce0af.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_5de96a7f(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_68dce0af.next(cursor_1);
        v_3 = x_9
      };
      Str_table_68dce0af.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_10
    }
  }
  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = isMinor_334421e4(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_10
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_11
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = salary_6514db0e(x_7);
            val x_9 = name_48c2a56e(x_7);
            val x_10 = name_48c2a56e(x_7);
            val x_11 = strlen_180cc43(x_10);
            val x_12 = job_6271f1d7(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_6ebcec5(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_16
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = res_0;
            val x_9 = age_2e4ccea4(x_7);
            res_0 = x_9.+(x_8);
            true
          })
      }) {
        val x_10 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_10
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_11
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      val x_0 = toCString_19397905("EPFL");
      val x_1 = init_Str_2bd10cd9(x_0);
      val x_2 = init_Job_6d18face(scala.Tuple2(10000, x_1));
      val x_3 = toCString_19397905("Lucien");
      val x_4 = init_Str_2bd10cd9(x_3);
      val x_5 = init_Person_6ebcec5(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_19397905("John");
      val x_7 = init_Str_2bd10cd9(x_6);
      val x_8 = init_Person_6ebcec5(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose()
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = age_2e4ccea4(x_7);
            if (x_8.==(21))
              delete_Person_54adc3e(x_7)
            else
              ();
            val x_9 = salary_6514db0e(x_7);
            val x_10 = name_48c2a56e(x_7);
            val x_11 = charAt_6aec7dbc(scala.Tuple2(x_10, 2L));
            val x_12 =
              toCString_19397905(x_11.asInstanceOf[scala.Byte].toString());
            val x_13 = init_Str_2bd10cd9(x_12);
            val x_14 = age_2e4ccea4(x_7);
            val x_15 = job_6271f1d7(x_7);
            val x_16 = init_Person_6ebcec5(scala.Tuple4(x_9, x_13, x_14, x_15));
            val x_17 = res_0;
            res_0 = x_17.+(1);
            true
          })
      }) {
        val x_18 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_18
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_19 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_19
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      val cursor_0 = Person_table_1d31793e.cursorOpen();
      val x_1 = Person_table_1d31793e.first(cursor_0);
      var v_2: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_1;
      while ({
        val x_3 = v_2;
        x_3._2
          .!=(null)
          .&&({
            val x_4 = v_2;
            val x_5 = v_2;
            val x_6 = fromPtrByte_Person_1c8ae6(x_4._1, x_5._2);
            val x_7 = age_2e4ccea4(x_6);
            scala.Predef.println(x_7);
            true
          })
      }) {
        val x_8 = Person_table_1d31793e.next(cursor_0);
        v_2 = x_8
      };
      Person_table_1d31793e.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose()
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = toCString_19397905("Test");
            val x_9 = init_Str_2bd10cd9(x_8);
            val x_10 = age_2e4ccea4(x_7);
            val x_11 = job_6271f1d7(x_7);
            val x_12 =
              init_Person_6ebcec5(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_14
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = age_2e4ccea4(x_7);
            `age_=_793415e5`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_10
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_11
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_68dce0af.dbiOpen();
      Person_table_1d31793e.dbiOpen();
      Job_table_11886c9c.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1d31793e.cursorOpen();
      val x_2 = Person_table_1d31793e.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1c8ae6(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_1d31793e.next(cursor_1);
        v_3 = x_9
      };
      Person_table_1d31793e.cursorClose(cursor_1);
      val x_10 = res_0;
      LMDBTable.txnCommit();
      Str_table_68dce0af.dbiClose();
      Person_table_1d31793e.dbiClose();
      Job_table_11886c9c.dbiClose();
      x_10
    }
  }
}

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
  lazy val Job_table_5deb4b63 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_30fa7b4a = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_60b50861 = new LMDBTable[Str]("Str")

  def toCString_1903d8ed(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Str_3549d995(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_60b50861.get(key)
    fromPtrByte_Str_7df7304a(key, value)
  }
  def get_Job_dc68e1d(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_5deb4b63.get(key)
    fromPtrByte_Job_320abb61(key, value)
  }
  def get_Person_32896755(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_30fa7b4a.get(key)
    fromPtrByte_Person_49f79a8f(key, value)
  }

  def put_Str_602c7a47(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_196605f1(data_el)
    Str_table_60b50861.put(key, size, value)
  }
  def put_Job_27b28c00(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_4da06437(data_el)
    Job_table_5deb4b63.put(key, size, value)
  }
  def put_Person_daff155(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_ed25c8a(data_el)
    Person_table_30fa7b4a.put(key, size, value)
  }

  def fromPtrByte_Str_7df7304a(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_7001ebfb(key, value1)
  }
  def fromPtrByte_Job_320abb61(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_2793a60b_key(key, (value1, value2))
  }
  def fromPtrByte_Person_49f79a8f(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_1571c5a3_key(key, (value1, value2, value3, value4))
  }

  def toPtrByte_Str_196605f1(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Job_4da06437(
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
  def toPtrByte_Person_ed25c8a(
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

  def init_Job_2793a60b_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_2793a60b_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_5deb4b63.size, params._1, params._2, null)
    put_Job_27b28c00(new_val)
    new_val
  }
  def init_Job_2793a60b(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_2793a60b(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_5deb4b63.size, params._1, params._2.key, params._2)
    put_Job_27b28c00(new_val)
    new_val
  }
  def init_Person_1571c5a3_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_1571c5a3_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_30fa7b4a.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_daff155(new_val)
    new_val
  }
  def init_Person_1571c5a3(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_1571c5a3(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_30fa7b4a.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_daff155(new_val)
    new_val
  }
  def init_Str_7001ebfb(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_7001ebfb(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_60b50861.size, param)
    put_Str_602c7a47(new_val)
    new_val
  }

  def isMinor_617d6126(this_496566c2: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_5298ddfd(this_496566c2);
    x_0.<(18)
  }

  def charAt_7d1e8e6(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_1a5ef230(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def job_7d8e4a0e(this_496566c2: Person)(implicit zone: Zone): Job = {
    if (this_496566c2.job == null) {
      this_496566c2.job = get_Job_dc68e1d(this_496566c2.jobId)
    }
    this_496566c2.job
  }
  def salary_102ce8fd(this_496566c2: Person)(implicit zone: Zone): Int = {
    this_496566c2.salary
  }
  def string_ca3f9ba(this_67fda5cf: Str)(implicit zone: Zone): CString = {
    this_67fda5cf.string
  }
  def enterprise_7912a636(this_6a82c4fb: Job)(implicit zone: Zone): Str = {
    if (this_6a82c4fb.enterprise == null) {
      this_6a82c4fb.enterprise = get_Str_3549d995(this_6a82c4fb.enterpriseId)
    }
    this_6a82c4fb.enterprise
  }
  def size_76435d20(this_6a82c4fb: Job)(implicit zone: Zone): Int = {
    this_6a82c4fb.size
  }
  def age_5298ddfd(this_496566c2: Person)(implicit zone: Zone): Int = {
    this_496566c2.age
  }
  def name_1df3815(this_496566c2: Person)(implicit zone: Zone): Str = {
    if (this_496566c2.name == null) {
      this_496566c2.name = get_Str_3549d995(this_496566c2.nameId)
    }
    this_496566c2.name
  }

  def `job_=_6acc2db9`(this_496566c2: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_496566c2.jobId = job.key
    this_496566c2.job = job
  }
  def `salary_=_6b92f8da`(this_496566c2: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_496566c2.salary = salary
  }
  def `enterprise_=_7f2dcee1`(this_6a82c4fb: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_6a82c4fb.enterpriseId = enterprise.key
    this_6a82c4fb.enterprise = enterprise
  }
  def `name_=_30ca8185`(this_496566c2: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_496566c2.nameId = name.key
    this_496566c2.name = name
  }
  def `age_=_3b60c3c0`(this_496566c2: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_496566c2.age = age
  }
  def `size_=_47c8b85e`(this_6a82c4fb: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6a82c4fb.size = size
  }

  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
              val x_8 = age_5298ddfd(x_7);
              // if (x_8.==(21))
              //   delete_Person_4f2398b4(x_7)
              // else
              //   ();
              val x_9 = salary_102ce8fd(x_7);
              val x_10 = name_1df3815(x_7);
              val x_11 = charAt_7d1e8e6(scala.Tuple2(x_10, 2L));
              val x_12 =
                toCString_1903d8ed(x_11.asInstanceOf[scala.Byte].toString());
              val x_13 = init_Str_7001ebfb(x_12);
              val x_14 = age_5298ddfd(x_7);
              val x_15 = job_7d8e4a0e(x_7);
              val x_16 =
                init_Person_1571c5a3(scala.Tuple4(x_9, x_13, x_14, x_15));
              val x_17 = res_0;
              res_0 = x_17.+(1);
              true
            }
          )
      }) {
        val x_18 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_18
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def insertions = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val x_0 = toCString_1903d8ed("EPFL");
      val x_1 = init_Str_7001ebfb(x_0);
      val x_2 = init_Job_2793a60b(scala.Tuple2(10000, x_1));
      val x_3 = toCString_1903d8ed("Lucien");
      val x_4 = init_Str_7001ebfb(x_3);
      val x_5 = init_Person_1571c5a3(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_1903d8ed("John");
      val x_7 = init_Str_7001ebfb(x_6);
      val x_8 = init_Person_1571c5a3(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose()
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val x_8 = age_5298ddfd(x_7);
            `age_=_3b60c3c0`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_10
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def sizeJobQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Job_table_5deb4b63.cursorOpen();
      val x_2 = Job_table_5deb4b63.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Job_320abb61(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Job_table_5deb4b63.next(cursor_1);
        v_3 = x_9
      };
      LMDBTable.txnCommit();
      Job_table_5deb4b63.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def sizePersonQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_9
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def sizeStrQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Str_table_60b50861.cursorOpen();
      val x_2 = Str_table_60b50861.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Str_7df7304a(x_5._1, x_6._2);
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Str_table_60b50861.next(cursor_1);
        v_3 = x_9
      };
      LMDBTable.txnCommit();
      Str_table_60b50861.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val x_8 = isMinor_617d6126(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_10
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def joinQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            true
          })
      }) {
        val x_8 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_8
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val x_8 = salary_102ce8fd(x_7);
            val x_9 = name_1df3815(x_7);
            val x_10 = age_5298ddfd(x_7);
            val x_11 = job_7d8e4a0e(x_7);
            val x_12 =
              init_Person_1571c5a3(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_14
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val x_8 = toCString_1903d8ed("Test");
            val x_9 = init_Str_7001ebfb(x_8);
            val x_10 = age_5298ddfd(x_7);
            val x_11 = job_7d8e4a0e(x_7);
            val x_12 =
              init_Person_1571c5a3(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_14
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val x_8 = salary_102ce8fd(x_7);
            val x_9 = name_1df3815(x_7);
            val x_10 = name_1df3815(x_7);
            val x_11 = strlen_1a5ef230(x_10);
            val x_12 = job_7d8e4a0e(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_1571c5a3(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_16
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
  def mapQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      LMDBTable.txnBegin();
      Str_table_60b50861.dbiOpen();
      Person_table_30fa7b4a.dbiOpen();
      Job_table_5deb4b63.dbiOpen();
      val cursor_1 = Person_table_30fa7b4a.cursorOpen();
      val x_2 = Person_table_30fa7b4a.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_49f79a8f(x_5._1, x_6._2);
            val row_8 = job_7d8e4a0e(x_7);
            val row_9 = size_76435d20(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_30fa7b4a.next(cursor_1);
        v_3 = x_12
      };
      LMDBTable.txnCommit();
      Person_table_30fa7b4a.cursorClose(cursor_1);
      Str_table_60b50861.dbiClose();
      Person_table_30fa7b4a.dbiClose();
      Job_table_5deb4b63.dbiClose();
      res_0
    }
  }
}

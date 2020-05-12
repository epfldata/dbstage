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
  lazy val Job_table_45400659 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_1ffd45a6 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_3fe31ee8 = new LMDBTable[Str]("Str")

  def toCString_36e51489(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_21c44f13(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_45400659.get(key)
    fromPtrByte_Job_6f7157b(key, value)
  }
  def get_Person_59d9cb71(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_1ffd45a6.get(key)
    fromPtrByte_Person_3f9b46fe(key, value)
  }
  def get_Str_648436cf(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_3fe31ee8.get(key)
    fromPtrByte_Str_67bb41fc(key, value)
  }

  def put_Job_6e268f61(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_2b51f68a(data_el)
    Job_table_45400659.put(key, size, value)
  }
  def put_Person_57020667(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_603572a(data_el)
    Person_table_1ffd45a6.put(key, size, value)
  }
  def put_Str_29448b25(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_73435fab(data_el)
    Str_table_3fe31ee8.put(key, size, value)
  }

  def fromPtrByte_Job_6f7157b(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_72143ac5_key(key, (value1, value2))
  }
  def fromPtrByte_Person_3f9b46fe(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_611dac9a_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Str_67bb41fc(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_66b05f57(key, value1)
  }

  def toPtrByte_Job_2b51f68a(
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
  def toPtrByte_Person_603572a(
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
  def toPtrByte_Str_73435fab(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Job_72143ac5_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_72143ac5_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_45400659.size, params._1, params._2, null)
    put_Job_6e268f61(new_val)
    new_val
  }
  def init_Job_72143ac5(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_72143ac5(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_45400659.size, params._1, params._2.key, params._2)
    put_Job_6e268f61(new_val)
    new_val
  }
  def init_Person_611dac9a_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_611dac9a_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_1ffd45a6.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_57020667(new_val)
    new_val
  }
  def init_Person_611dac9a(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_611dac9a(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_1ffd45a6.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_57020667(new_val)
    new_val
  }
  def init_Str_66b05f57(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_66b05f57(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_3fe31ee8.size, param)
    put_Str_29448b25(new_val)
    new_val
  }

  def isMinor_50677e8c(this_d810eed: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_3a7b9662(this_d810eed);
    x_0.<(18)
  }

  def charAt_1e64ff26(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_6712c1ac(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def job_f44b67e(this_d810eed: Person)(implicit zone: Zone): Job = {
    if (this_d810eed.job == null) {
      this_d810eed.job = get_Job_21c44f13(this_d810eed.jobId)
    }
    this_d810eed.job
  }
  def salary_7eaa199b(this_d810eed: Person)(implicit zone: Zone): Int = {
    this_d810eed.salary
  }
  def string_1a403836(this_4bb13422: Str)(implicit zone: Zone): CString = {
    this_4bb13422.string
  }
  def enterprise_2b03731f(this_49b9a655: Job)(implicit zone: Zone): Str = {
    if (this_49b9a655.enterprise == null) {
      this_49b9a655.enterprise = get_Str_648436cf(this_49b9a655.enterpriseId)
    }
    this_49b9a655.enterprise
  }
  def size_15ec747(this_49b9a655: Job)(implicit zone: Zone): Int = {
    this_49b9a655.size
  }
  def age_3a7b9662(this_d810eed: Person)(implicit zone: Zone): Int = {
    this_d810eed.age
  }
  def name_2f167036(this_d810eed: Person)(implicit zone: Zone): Str = {
    if (this_d810eed.name == null) {
      this_d810eed.name = get_Str_648436cf(this_d810eed.nameId)
    }
    this_d810eed.name
  }

  def `job_=_6fbfa8c3`(this_d810eed: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_d810eed.jobId = job.key
    this_d810eed.job = job
  }
  def `salary_=_38876662`(this_d810eed: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_d810eed.salary = salary
  }
  def `enterprise_=_74c070f`(this_49b9a655: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_49b9a655.enterpriseId = enterprise.key
    this_49b9a655.enterprise = enterprise
  }
  def `name_=_532650cc`(this_d810eed: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_d810eed.nameId = name.key
    this_d810eed.name = name
  }
  def `age_=_41cd03db`(this_d810eed: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_d810eed.age = age
  }
  def `size_=_427ad9f6`(this_49b9a655: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_49b9a655.size = size
  }

  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val x_10 = salary_7eaa199b(x_9);
            val x_11 = name_2f167036(x_9);
            val x_12 = age_3a7b9662(x_9);
            val x_13 = job_f44b67e(x_9);
            val x_14 =
              init_Person_611dac9a(scala.Tuple4(x_10, x_11, x_12.+(100), x_13));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_16
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def mapQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val row_10 = job_f44b67e(x_9);
            val row_11 = size_15ec747(row_10);
            val row_12 = row_11.+(100);
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_14
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val x_10 = age_3a7b9662(x_9);
            `age_=_41cd03db`(x_9, x_10.+(10));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_12
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def insertions = Zone { implicit zone =>
    {
      val x_0 = toCString_36e51489("EPFL");
      val x_1 = init_Str_66b05f57(x_0);
      val x_2 = init_Job_72143ac5(scala.Tuple2(10000, x_1));
      val x_3 = toCString_36e51489("Lucien");
      val x_4 = init_Str_66b05f57(x_3);
      val x_5 = init_Person_611dac9a(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_36e51489("John");
      val x_7 = init_Str_66b05f57(x_6);
      val x_8 = init_Person_611dac9a(scala.Tuple4(100, x_7, 16, x_2));
      ()
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val x_10 = toCString_36e51489("Test");
            val x_11 = init_Str_66b05f57(x_10);
            val x_12 = age_3a7b9662(x_9);
            val x_13 = job_f44b67e(x_9);
            val x_14 =
              init_Person_611dac9a(scala.Tuple4(0, x_11, x_12.+(100), x_13));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_16
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def joinQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            true
          })
      }) {
        val x_10 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_10
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val x_10 = salary_7eaa199b(x_9);
            val x_11 = name_2f167036(x_9);
            val x_12 = name_2f167036(x_9);
            val x_13 = strlen_6712c1ac(x_12);
            val x_14 = job_f44b67e(x_9);
            val x_15 = x_13.asInstanceOf[scala.Long].toInt;
            val x_16 =
              init_Person_611dac9a(scala.Tuple4(x_10, x_11, x_15, x_14));
            val x_17 = res_0;
            res_0 = x_17.+(1);
            true
          })
      }) {
        val x_18 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_18
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val x_10 = isMinor_50677e8c(x_9);
            if (x_10.asInstanceOf[scala.Boolean]) {
              val x_11 = res_0;
              res_0 = x_11.+(1);
              true
            } else
              true
          })
      }) {
        val x_12 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_12
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val txn_1 = Person_table_1ffd45a6.txn();
      val dbi_2 = Person_table_1ffd45a6.dbi(txn_1);
      val cursor_3 = Person_table_1ffd45a6.cursor(txn_1, dbi_2);
      val x_4 = Person_table_1ffd45a6.first(cursor_3);
      var v_5: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_4;
      while ({
        val x_6 = v_5;
        x_6._2
          .!=(null)
          .&&({
            val x_7 = v_5;
            val x_8 = v_5;
            val x_9 = fromPtrByte_Person_3f9b46fe(x_7._1, x_8._2);
            val x_10 = age_3a7b9662(x_9);
            if (x_10.==(21))
              delete_Person_5cf8a9c7(x_9)
            else
              ();
            val x_11 = salary_7eaa199b(x_9);
            val x_12 = name_2f167036(x_9);
            val x_13 = charAt_1e64ff26(scala.Tuple2(x_12, 2L));
            val x_14 =
              toCString_36e51489(x_13.asInstanceOf[scala.Byte].toString());
            val x_15 = init_Str_66b05f57(x_14);
            val x_16 = age_3a7b9662(x_9);
            val x_17 = job_f44b67e(x_9);
            val x_18 =
              init_Person_611dac9a(scala.Tuple4(x_11, x_15, x_16, x_17));
            val x_19 = res_0;
            res_0 = x_19.+(1);
            true
          })
      }) {
        val x_20 = Person_table_1ffd45a6.next(cursor_3);
        v_5 = x_20
      };
      Person_table_1ffd45a6.closeCursor(cursor_3);
      Person_table_1ffd45a6.closeDbi(dbi_2);
      Person_table_1ffd45a6.commitTxn(txn_1);
      res_0
    }
  }
}

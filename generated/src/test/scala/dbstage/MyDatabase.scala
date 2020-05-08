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
  lazy val Person_table_127eac15 = new LMDBTable[Person]("Person")

  class Job(
      val key: Long,
      var size: Int,
      var enterpriseId: Long,
      var enterprise: Str = null
  )
  val sizeJob = sizeInt + sizeLong
  lazy val Job_table_209426b = new LMDBTable[Job]("Job")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_6c032951 = new LMDBTable[Str]("Str")

  def toCString_333002e0(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Str_69a7ef1c(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_6c032951.get(key)
    fromPtrByte_Str_1c1b146f(key, value)
  }
  def get_Person_461247bd(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_127eac15.get(key)
    fromPtrByte_Person_5ab3c7ca(key, value)
  }
  def get_Job_54674b32(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_209426b.get(key)
    fromPtrByte_Job_2ee8b7c(key, value)
  }

  def put_Str_5cc6ecc2(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_32161f69(data_el)
    Str_table_6c032951.put(key, size, value)
  }
  def put_Person_5425b1ad(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_3ed5c5ea(data_el)
    Person_table_127eac15.put(key, size, value)
  }
  def put_Job_3d0157f0(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_56ca1117(data_el)
    Job_table_209426b.put(key, size, value)
  }

  def fromPtrByte_Str_1c1b146f(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_543a5bf6(key, value1)
  }
  def fromPtrByte_Person_5ab3c7ca(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_17728c5e_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Job_2ee8b7c(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_307de4ad_key(key, (value1, value2))
  }

  def toPtrByte_Str_32161f69(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Person_3ed5c5ea(
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
  def toPtrByte_Job_56ca1117(
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

  def init_Job_307de4ad_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_307de4ad_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_209426b.size, params._1, params._2, null)
    put_Job_3d0157f0(new_val)
    new_val
  }
  def init_Job_307de4ad(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_307de4ad(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_209426b.size, params._1, params._2.key, params._2)
    put_Job_3d0157f0(new_val)
    new_val
  }
  def init_Person_17728c5e_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_17728c5e_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_127eac15.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_5425b1ad(new_val)
    new_val
  }
  def init_Person_17728c5e(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_17728c5e(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_127eac15.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_5425b1ad(new_val)
    new_val
  }
  def init_Str_543a5bf6(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_543a5bf6(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_6c032951.size, param)
    put_Str_5cc6ecc2(new_val)
    new_val
  }

  def isMinor_74c1a55a(this_6ff6f921: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_640d3736(this_6ff6f921);
    x_0.<(18)
  }

  def charAt_11f68dc0(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_79505e5(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def string_627b3cf6(this_52ee28f3: Str)(implicit zone: Zone): CString = {
    this_52ee28f3.string
  }
  def size_4deeb771(this_78198f11: Job)(implicit zone: Zone): Int = {
    this_78198f11.size
  }
  def enterprise_5a7966b7(this_78198f11: Job)(implicit zone: Zone): Str = {
    if (this_78198f11.enterprise == null) {
      this_78198f11.enterprise = get_Str_69a7ef1c(this_78198f11.enterpriseId)
    }
    this_78198f11.enterprise
  }
  def age_640d3736(this_6ff6f921: Person)(implicit zone: Zone): Int = {
    this_6ff6f921.age
  }
  def name_2c439812(this_6ff6f921: Person)(implicit zone: Zone): Str = {
    if (this_6ff6f921.name == null) {
      this_6ff6f921.name = get_Str_69a7ef1c(this_6ff6f921.nameId)
    }
    this_6ff6f921.name
  }
  def job_17e48de0(this_6ff6f921: Person)(implicit zone: Zone): Job = {
    if (this_6ff6f921.job == null) {
      this_6ff6f921.job = get_Job_54674b32(this_6ff6f921.jobId)
    }
    this_6ff6f921.job
  }
  def salary_2f09e552(this_6ff6f921: Person)(implicit zone: Zone): Int = {
    this_6ff6f921.salary
  }

  def `enterprise_=_5d1cb827`(this_78198f11: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_78198f11.enterpriseId = enterprise.key
    this_78198f11.enterprise = enterprise
  }
  def `name_=_4043d3ec`(this_6ff6f921: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_6ff6f921.nameId = name.key
    this_6ff6f921.name = name
  }
  def `age_=_7165f2c0`(this_6ff6f921: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6ff6f921.age = age
  }
  def `size_=_33355f66`(this_78198f11: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_78198f11.size = size
  }
  def `salary_=_bd014a7`(this_6ff6f921: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6ff6f921.salary = salary
  }
  def `job_=_55526a82`(this_6ff6f921: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_6ff6f921.jobId = job.key
    this_6ff6f921.job = job
  }

  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            val x_8 = salary_2f09e552(x_7);
            val x_9 = name_2c439812(x_7);
            val x_10 = age_640d3736(x_7);
            val x_11 = job_17e48de0(x_7);
            val x_12 =
              init_Person_17728c5e(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_127eac15.next(cursor_1);
        v_3 = x_14
      };
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            val x_8 = isMinor_74c1a55a(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_127eac15.next(cursor_1);
        v_3 = x_10
      };
      res_0
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            val x_8 = toCString_333002e0("Test");
            val x_9 = init_Str_543a5bf6(x_8);
            val x_10 = age_640d3736(x_7);
            val x_11 = job_17e48de0(x_7);
            val x_12 =
              init_Person_17728c5e(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_127eac15.next(cursor_1);
        v_3 = x_14
      };
      res_0
    }
  }
  def joinQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            true
          })
      }) {
        val x_8 = Person_table_127eac15.next(cursor_1);
        v_3 = x_8
      };
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            val x_8 = age_640d3736(x_7);
            `age_=_7165f2c0`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_127eac15.next(cursor_1);
        v_3 = x_10
      };
      res_0
    }
  }
  def mapQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            val row_8 = job_17e48de0(x_7);
            val row_9 = size_4deeb771(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_127eac15.next(cursor_1);
        v_3 = x_12
      };
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
            val x_8 = salary_2f09e552(x_7);
            val x_9 = name_2c439812(x_7);
            val x_10 = name_2c439812(x_7);
            val x_11 = strlen_79505e5(x_10);
            val x_12 = job_17e48de0(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_17728c5e(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_127eac15.next(cursor_1);
        v_3 = x_16
      };
      res_0
    }
  }
  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_127eac15.cursor;
      val x_2 = Person_table_127eac15.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_5ab3c7ca(x_5._1, x_6._2);
              val x_8 = salary_2f09e552(x_7);
              val x_9 = name_2c439812(x_7);
              val x_10 = charAt_11f68dc0(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_333002e0(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_543a5bf6(x_11);
              val x_13 = age_640d3736(x_7);
              val x_14 = job_17e48de0(x_7);
              val x_15 =
                init_Person_17728c5e(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              res_0 = x_16.+(1);
              true
            }
          )
      }) {
        val x_17 = Person_table_127eac15.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
  def insertions = Zone { implicit zone =>
    {
      val x_0 = toCString_333002e0("EPFL");
      val x_1 = init_Str_543a5bf6(x_0);
      val x_2 = init_Job_307de4ad(scala.Tuple2(10000, x_1));
      val x_3 = toCString_333002e0("Lucien");
      val x_4 = init_Str_543a5bf6(x_3);
      val x_5 = init_Person_17728c5e(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_333002e0("John");
      val x_7 = init_Str_543a5bf6(x_6);
      val x_8 = init_Person_17728c5e(scala.Tuple4(100, x_7, 16, x_2));
      ()
    }
  }
}

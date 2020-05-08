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
  lazy val Job_table_595f6b24 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_26b0cfc3 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_5facbfa2 = new LMDBTable[Str]("Str")

  def toCString_4ffb2fc5(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_77da9841(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_595f6b24.get(key)
    fromPtrByte_Job_4d316fea(key, value)
  }
  def get_Person_77daced7(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_26b0cfc3.get(key)
    fromPtrByte_Person_61c90478(key, value)
  }
  def get_Str_12692f02(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_5facbfa2.get(key)
    fromPtrByte_Str_3152b1f9(key, value)
  }

  def put_Job_65945005(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_3e5e79f3(data_el)
    Job_table_595f6b24.put(key, size, value)
  }
  def put_Person_2a3843c7(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_e4af2cf(data_el)
    Person_table_26b0cfc3.put(key, size, value)
  }
  def put_Str_20374603(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_67643692(data_el)
    Str_table_5facbfa2.put(key, size, value)
  }

  def fromPtrByte_Job_4d316fea(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_117c46ca_key(key, (value1, value2))
  }
  def fromPtrByte_Person_61c90478(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_5737b4c6_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Str_3152b1f9(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_514df84b(key, value1)
  }

  def toPtrByte_Job_3e5e79f3(
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
  def toPtrByte_Person_e4af2cf(
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
  def toPtrByte_Str_67643692(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Job_117c46ca_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_117c46ca_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_595f6b24.size, params._1, params._2, null)
    put_Job_65945005(new_val)
    new_val
  }
  def init_Job_117c46ca(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_117c46ca(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_595f6b24.size, params._1, params._2.key, params._2)
    put_Job_65945005(new_val)
    new_val
  }
  def init_Person_5737b4c6_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_5737b4c6_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_26b0cfc3.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_2a3843c7(new_val)
    new_val
  }
  def init_Person_5737b4c6(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_5737b4c6(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_26b0cfc3.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_2a3843c7(new_val)
    new_val
  }
  def init_Str_514df84b(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_514df84b(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_5facbfa2.size, param)
    put_Str_20374603(new_val)
    new_val
  }

  def isMinor_3c8c75cf(this_55db2559: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_2b40bde6(this_55db2559);
    x_0.<(18)
  }

  def charAt_33874358(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_7312b9c6(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def string_4b63d387(this_29124763: Str)(implicit zone: Zone): CString = {
    this_29124763.string
  }
  def size_2d599fa4(this_2d68baa6: Job)(implicit zone: Zone): Int = {
    this_2d68baa6.size
  }
  def enterprise_301962b9(this_2d68baa6: Job)(implicit zone: Zone): Str = {
    if (this_2d68baa6.enterprise == null) {
      this_2d68baa6.enterprise = get_Str_12692f02(this_2d68baa6.enterpriseId)
    }
    this_2d68baa6.enterprise
  }
  def age_2b40bde6(this_55db2559: Person)(implicit zone: Zone): Int = {
    this_55db2559.age
  }
  def name_4cba6efd(this_55db2559: Person)(implicit zone: Zone): Str = {
    if (this_55db2559.name == null) {
      this_55db2559.name = get_Str_12692f02(this_55db2559.nameId)
    }
    this_55db2559.name
  }
  def job_19906306(this_55db2559: Person)(implicit zone: Zone): Job = {
    if (this_55db2559.job == null) {
      this_55db2559.job = get_Job_77da9841(this_55db2559.jobId)
    }
    this_55db2559.job
  }
  def salary_78d57b2f(this_55db2559: Person)(implicit zone: Zone): Int = {
    this_55db2559.salary
  }

  def `enterprise_=_55bd4e02`(this_2d68baa6: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2d68baa6.enterpriseId = enterprise.key
    this_2d68baa6.enterprise = enterprise
  }
  def `name_=_409b9561`(this_55db2559: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_55db2559.nameId = name.key
    this_55db2559.name = name
  }
  def `age_=_58ba750a`(this_55db2559: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_55db2559.age = age
  }
  def `size_=_4c43d22e`(this_2d68baa6: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2d68baa6.size = size
  }
  def `salary_=_73b158fa`(this_55db2559: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_55db2559.salary = salary
  }
  def `job_=_6f9deb70`(this_55db2559: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_55db2559.jobId = job.key
    this_55db2559.job = job
  }

  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
              val x_8 = salary_78d57b2f(x_7);
              val x_9 = name_4cba6efd(x_7);
              val x_10 = charAt_33874358(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_4ffb2fc5(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_514df84b(x_11);
              val x_13 = age_2b40bde6(x_7);
              val x_14 = job_19906306(x_7);
              val x_15 =
                init_Person_5737b4c6(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              res_0 = x_16.+(1);
              true
            }
          )
      }) {
        val x_17 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
  def mapQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            val row_8 = job_19906306(x_7);
            val row_9 = size_2d599fa4(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_12
      };
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            val x_8 = salary_78d57b2f(x_7);
            val x_9 = name_4cba6efd(x_7);
            val x_10 = name_4cba6efd(x_7);
            val x_11 = strlen_7312b9c6(x_10);
            val x_12 = job_19906306(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_5737b4c6(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_16
      };
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            val x_8 = salary_78d57b2f(x_7);
            val x_9 = name_4cba6efd(x_7);
            val x_10 = age_2b40bde6(x_7);
            val x_11 = job_19906306(x_7);
            val x_12 =
              init_Person_5737b4c6(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_14
      };
      res_0
    }
  }
  def joinQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            true
          })
      }) {
        val x_8 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_8
      };
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            val x_8 = age_2b40bde6(x_7);
            `age_=_58ba750a`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_10
      };
      res_0
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            val x_8 = toCString_4ffb2fc5("Test");
            val x_9 = init_Str_514df84b(x_8);
            val x_10 = age_2b40bde6(x_7);
            val x_11 = job_19906306(x_7);
            val x_12 =
              init_Person_5737b4c6(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_14
      };
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_26b0cfc3.cursor;
      val x_2 = Person_table_26b0cfc3.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_61c90478(x_5._1, x_6._2);
            val x_8 = isMinor_3c8c75cf(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_26b0cfc3.next(cursor_1);
        v_3 = x_10
      };
      res_0
    }
  }
}

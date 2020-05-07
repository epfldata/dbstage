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
  lazy val Person_table_774a700d = new LMDBTable[Person]("Person")

  class Job(
      val key: Long,
      var size: Int,
      var enterpriseId: Long,
      var enterprise: Str = null
  )
  val sizeJob = sizeInt + sizeLong
  lazy val Job_table_3d79a701 = new LMDBTable[Job]("Job")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_e29ef69 = new LMDBTable[Str]("Str")

  def toCString_4b974d89(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Person_5e5b9398(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_774a700d.get(key)
    fromPtrByte_Person_1f4865bf(key, value)
  }
  def get_Job_c3cdb3d(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_3d79a701.get(key)
    fromPtrByte_Job_2f5166df(key, value)
  }
  def get_Str_5240bc53(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_e29ef69.get(key)
    fromPtrByte_Str_50053298(key, value)
  }

  def put_Person_32116da4(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_65e4f76f(data_el)
    Person_table_774a700d.put(key, size, value)
  }
  def put_Job_264c8140(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_5e049d23(data_el)
    Job_table_3d79a701.put(key, size, value)
  }
  def put_Str_5d94781a(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_64d14569(data_el)
    Str_table_e29ef69.put(key, size, value)
  }

  def fromPtrByte_Person_1f4865bf(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_6507c83f_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Job_2f5166df(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_11408fbd_key(key, (value1, value2))
  }
  def fromPtrByte_Str_50053298(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_3defa85(key, value1)
  }

  def toPtrByte_Person_65e4f76f(
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
  def toPtrByte_Job_5e049d23(
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
  def toPtrByte_Str_64d14569(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }

  def init_Job_11408fbd_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_11408fbd_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_3d79a701.size, params._1, params._2, null)
    put_Job_264c8140(new_val)
    new_val
  }
  def init_Job_11408fbd(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_11408fbd(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_3d79a701.size, params._1, params._2.key, params._2)
    put_Job_264c8140(new_val)
    new_val
  }
  def init_Person_6507c83f_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_6507c83f_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_774a700d.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_32116da4(new_val)
    new_val
  }
  def init_Person_6507c83f(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_6507c83f(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_774a700d.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_32116da4(new_val)
    new_val
  }
  def init_Str_3defa85(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_3defa85(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_e29ef69.size, param)
    put_Str_5d94781a(new_val)
    new_val
  }

  def isMinor_42437486(this_1e906dd0: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_69f626e(this_1e906dd0);
    x_0.<(18)
  }

  def charAt_2e73991a(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_7f9c67f6(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def job_3a5f25f3(this_1e906dd0: Person)(implicit zone: Zone): Job = {
    if (this_1e906dd0.job == null) {
      this_1e906dd0.job = get_Job_c3cdb3d(this_1e906dd0.jobId)
    }
    this_1e906dd0.job
  }
  def salary_26e05699(this_1e906dd0: Person)(implicit zone: Zone): Int = {
    this_1e906dd0.salary
  }
  def enterprise_5905f07b(this_5b6543be: Job)(implicit zone: Zone): Str = {
    if (this_5b6543be.enterprise == null) {
      this_5b6543be.enterprise = get_Str_5240bc53(this_5b6543be.enterpriseId)
    }
    this_5b6543be.enterprise
  }
  def size_67d88ee7(this_5b6543be: Job)(implicit zone: Zone): Int = {
    this_5b6543be.size
  }
  def name_246b4f52(this_1e906dd0: Person)(implicit zone: Zone): Str = {
    if (this_1e906dd0.name == null) {
      this_1e906dd0.name = get_Str_5240bc53(this_1e906dd0.nameId)
    }
    this_1e906dd0.name
  }
  def string_1b91d8(this_7643dbe3: Str)(implicit zone: Zone): CString = {
    this_7643dbe3.string
  }
  def age_69f626e(this_1e906dd0: Person)(implicit zone: Zone): Int = {
    this_1e906dd0.age
  }

  def `size_=_77412dcd`(this_5b6543be: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5b6543be.size = size
  }
  def `job_=_58795bc2`(this_1e906dd0: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_1e906dd0.jobId = job.key
    this_1e906dd0.job = job
  }
  def `salary_=_34326f41`(this_1e906dd0: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1e906dd0.salary = salary
  }
  def `name_=_58b7d4b6`(this_1e906dd0: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_1e906dd0.nameId = name.key
    this_1e906dd0.name = name
  }
  def `enterprise_=_26e6a528`(this_5b6543be: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_5b6543be.enterpriseId = enterprise.key
    this_5b6543be.enterprise = enterprise
  }
  def `age_=_34d35d7d`(this_1e906dd0: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1e906dd0.age = age
  }

  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            val x_8 = age_69f626e(x_7);
            `age_=_34d35d7d`(x_7, x_8.+(10));
            val x_9 = res_0;
            val x_12 =
              ((x$1_10: Person, acc_11: scala.Int) => acc_11.+(1))((), x_9);
            res_0 = x_12;
            true
          })
      }) {
        val x_13 = Person_table_774a700d.next(cursor_1);
        v_3 = x_13
      };
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            val x_8 = salary_26e05699(x_7);
            val x_9 = name_246b4f52(x_7);
            val x_10 = age_69f626e(x_7);
            val x_11 = job_3a5f25f3(x_7);
            val x_12 =
              init_Person_6507c83f(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            val x_16 =
              ((x$1_14: Person, acc_15: scala.Int) => acc_15.+(1))(x_12, x_13);
            res_0 = x_16;
            true
          })
      }) {
        val x_17 = Person_table_774a700d.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            val x_8 = toCString_4b974d89("Test");
            val x_9 = init_Str_3defa85(x_8);
            val x_10 = age_69f626e(x_7);
            val x_11 = job_3a5f25f3(x_7);
            val x_12 =
              init_Person_6507c83f(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            val x_16 =
              ((x$1_14: Person, acc_15: scala.Int) => acc_15.+(1))(x_12, x_13);
            res_0 = x_16;
            true
          })
      }) {
        val x_17 = Person_table_774a700d.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            val x_8 = salary_26e05699(x_7);
            val x_9 = name_246b4f52(x_7);
            val x_10 = name_246b4f52(x_7);
            val x_11 = strlen_7f9c67f6(x_10);
            val x_12 = job_3a5f25f3(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_6507c83f(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            val x_18 =
              ((x$1_16: Person, acc_17: scala.Int) => acc_17.+(1))(x_14, x_15);
            res_0 = x_18;
            true
          })
      }) {
        val x_19 = Person_table_774a700d.next(cursor_1);
        v_3 = x_19
      };
      res_0
    }
  }
  def mapQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            val row_8 = job_3a5f25f3(x_7);
            val row_9 = size_67d88ee7(row_8);
            val row_10 = row_9.+(100);
            val x_11 = res_0;
            val x_14 = ((x$1_12: scala.Int, acc_13: scala.Int) => acc_13.+(1))(
              row_10,
              x_11
            );
            res_0 = x_14;
            true
          })
      }) {
        val x_15 = Person_table_774a700d.next(cursor_1);
        v_3 = x_15
      };
      res_0
    }
  }
  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
              val x_8 = age_69f626e(x_7);
              if (x_8.==(21))
                dbstage.lang.TableView.delete[Person](x_7)
              else
                ();
              val x_9 = salary_26e05699(x_7);
              val x_10 = name_246b4f52(x_7);
              val x_11 = charAt_2e73991a(scala.Tuple2(x_10, 2L));
              val x_12 =
                toCString_4b974d89(x_11.asInstanceOf[scala.Byte].toString());
              val x_13 = init_Str_3defa85(x_12);
              val x_14 = age_69f626e(x_7);
              val x_15 = job_3a5f25f3(x_7);
              val x_16 =
                init_Person_6507c83f(scala.Tuple4(x_9, x_13, x_14, x_15));
              val x_17 = res_0;
              val x_20 = ((x$1_18: Person, acc_19: scala.Int) => acc_19.+(1))(
                x_16,
                x_17
              );
              res_0 = x_20;
              true
            }
          )
      }) {
        val x_21 = Person_table_774a700d.next(cursor_1);
        v_3 = x_21
      };
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            val x_8 = isMinor_42437486(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              val x_12 =
                ((x$1_10: Person, acc_11: scala.Int) => acc_11.+(1))(x_7, x_9);
              res_0 = x_12;
              true
            } else
              true
          })
      }) {
        val x_13 = Person_table_774a700d.next(cursor_1);
        v_3 = x_13
      };
      res_0
    }
  }
  def joinQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_774a700d.cursor;
      val x_2 = Person_table_774a700d.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1f4865bf(x_5._1, x_6._2);
            true
          })
      }) {
        val x_8 = Person_table_774a700d.next(cursor_1);
        v_3 = x_8
      };
      res_0
    }
  }
}

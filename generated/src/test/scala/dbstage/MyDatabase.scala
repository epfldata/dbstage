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
      var entrepriseId: Long,
      var entreprise: Str = null
  )
  val sizeJob = sizeInt + sizeLong
  lazy val Job_table_7ca23d4 = new LMDBTable[Job]("Job")

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
  lazy val Person_table_3dabdab5 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_269ae98 = new LMDBTable[Str]("Str")

  def toCString_62532d4e(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Str_189e1511(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_269ae98.get(key)
    fromPtrByte_Str_57cc6c1e(key, value)
  }
  def get_Job_5c8063c7(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_7ca23d4.get(key)
    fromPtrByte_Job_635c7211(key, value)
  }
  def get_Person_c6fe5a(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_3dabdab5.get(key)
    fromPtrByte_Person_681848d5(key, value)
  }

  def put_Str_423cac3b(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_753fa8e8(data_el)
    Str_table_269ae98.put(key, size, value)
  }
  def put_Job_1380e648(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_14257b55(data_el)
    Job_table_7ca23d4.put(key, size, value)
  }
  def put_Person_3c8a5e09(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_1bf07438(data_el)
    Person_table_3dabdab5.put(key, size, value)
  }

  def fromPtrByte_Str_57cc6c1e(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_176c7189(key, value1)
  }
  def fromPtrByte_Job_635c7211(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_4517cf3e_key(key, (value1, value2))
  }
  def fromPtrByte_Person_681848d5(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_66a693b2_key(key, (value1, value2, value3, value4))
  }

  def toPtrByte_Str_753fa8e8(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Job_14257b55(
      new_value: Job
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = sizeJob
    val value1 = alloc[Byte](size)
    intcpy(value1, new_value.size, sizeInt)
    val value2 = value1 + sizeInt
    longcpy(value2, new_value.entrepriseId, sizeLong)
    val value3 = value2 + sizeLong
    (key, size, value1)
  }
  def toPtrByte_Person_1bf07438(
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

  def init_Job_4517cf3e_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_4517cf3e_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_7ca23d4.size, params._1, params._2, null)
    put_Job_1380e648(new_val)
    new_val
  }
  def init_Job_4517cf3e(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_4517cf3e(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_7ca23d4.size, params._1, params._2.key, params._2)
    put_Job_1380e648(new_val)
    new_val
  }
  def init_Person_66a693b2_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_66a693b2_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_3dabdab5.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_3c8a5e09(new_val)
    new_val
  }
  def init_Person_66a693b2(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_66a693b2(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_3dabdab5.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_3c8a5e09(new_val)
    new_val
  }
  def init_Str_176c7189(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_176c7189(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_269ae98.size, param)
    put_Str_423cac3b(new_val)
    new_val
  }

  def isMinor_1dbc36fd(this_2ef9c237: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_4598d09e(this_2ef9c237);
    x_0.<(18)
  }

  def charAt_4329c752(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_6eb1a6a9(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_4598d09e(this_2ef9c237: Person)(implicit zone: Zone): Int = {
    this_2ef9c237.age
  }
  def job_27e9a68(this_2ef9c237: Person)(implicit zone: Zone): Job = {
    if (this_2ef9c237.job == null) {
      this_2ef9c237.job = get_Job_5c8063c7(this_2ef9c237.jobId)
    }
    this_2ef9c237.job
  }
  def salary_6199f060(this_2ef9c237: Person)(implicit zone: Zone): Int = {
    this_2ef9c237.salary
  }
  def entreprise_69e4e54c(this_7504a8c: Job)(implicit zone: Zone): Str = {
    if (this_7504a8c.entreprise == null) {
      this_7504a8c.entreprise = get_Str_189e1511(this_7504a8c.entrepriseId)
    }
    this_7504a8c.entreprise
  }
  def string_6071a326(this_23e026f7: Str)(implicit zone: Zone): CString = {
    this_23e026f7.string
  }
  def size_162ad89a(this_7504a8c: Job)(implicit zone: Zone): Int = {
    this_7504a8c.size
  }
  def name_389e20cf(this_2ef9c237: Person)(implicit zone: Zone): Str = {
    if (this_2ef9c237.name == null) {
      this_2ef9c237.name = get_Str_189e1511(this_2ef9c237.nameId)
    }
    this_2ef9c237.name
  }

  def `entreprise_=_12e4a763`(this_7504a8c: Job, entreprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_7504a8c.entrepriseId = entreprise.key
    this_7504a8c.entreprise = entreprise
  }
  def `age_=_171f0fee`(this_2ef9c237: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2ef9c237.age = age
  }
  def `size_=_5008dcef`(this_7504a8c: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_7504a8c.size = size
  }
  def `job_=_20297b10`(this_2ef9c237: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_2ef9c237.jobId = job.key
    this_2ef9c237.job = job
  }
  def `salary_=_17410fc9`(this_2ef9c237: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2ef9c237.salary = salary
  }
  def `name_=_78ce12b0`(this_2ef9c237: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2ef9c237.nameId = name.key
    this_2ef9c237.name = name
  }

  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dabdab5.cursor;
      val x_2 = Person_table_3dabdab5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_681848d5(x_5._1, x_6._2);
              val x_8 = salary_6199f060(x_7);
              val x_9 = name_389e20cf(x_7);
              val x_10 = charAt_4329c752(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_62532d4e(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_176c7189(x_11);
              val x_13 = age_4598d09e(x_7);
              val x_14 = job_27e9a68(x_7);
              val x_15 =
                init_Person_66a693b2(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              res_0 = x_16.+(1);
              true
            }
          )
      }) {
        val x_17 = Person_table_3dabdab5.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dabdab5.cursor;
      val x_2 = Person_table_3dabdab5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_681848d5(x_5._1, x_6._2);
            val x_8 = age_4598d09e(x_7);
            `age_=_171f0fee`(x_7, x_8.+(10));
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_3dabdab5.next(cursor_1);
        v_3 = x_10
      };
      res_0
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dabdab5.cursor;
      val x_2 = Person_table_3dabdab5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_681848d5(x_5._1, x_6._2);
            val x_8 = toCString_62532d4e("Test");
            val x_9 = init_Str_176c7189(x_8);
            val x_10 = age_4598d09e(x_7);
            val x_11 = job_27e9a68(x_7);
            val x_12 =
              init_Person_66a693b2(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_3dabdab5.next(cursor_1);
        v_3 = x_14
      };
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dabdab5.cursor;
      val x_2 = Person_table_3dabdab5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_681848d5(x_5._1, x_6._2);
            val x_8 = isMinor_1dbc36fd(x_7);
            if (x_8.asInstanceOf[scala.Boolean]) {
              val x_9 = res_0;
              res_0 = x_9.+(1);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_3dabdab5.next(cursor_1);
        v_3 = x_10
      };
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dabdab5.cursor;
      val x_2 = Person_table_3dabdab5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_681848d5(x_5._1, x_6._2);
            val x_8 = salary_6199f060(x_7);
            val x_9 = name_389e20cf(x_7);
            val x_10 = age_4598d09e(x_7);
            val x_11 = job_27e9a68(x_7);
            val x_12 =
              init_Person_66a693b2(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_3dabdab5.next(cursor_1);
        v_3 = x_14
      };
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dabdab5.cursor;
      val x_2 = Person_table_3dabdab5.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_681848d5(x_5._1, x_6._2);
            val x_8 = salary_6199f060(x_7);
            val x_9 = name_389e20cf(x_7);
            val x_10 = name_389e20cf(x_7);
            val x_11 = strlen_6eb1a6a9(x_10);
            val x_12 = job_27e9a68(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_66a693b2(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            res_0 = x_15.+(1);
            true
          })
      }) {
        val x_16 = Person_table_3dabdab5.next(cursor_1);
        v_3 = x_16
      };
      res_0
    }
  }
}

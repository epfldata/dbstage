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
  lazy val Person_table_4e881a54 = new LMDBTable[Person]("Person")

  class Job(
      val key: Long,
      var size: Int,
      var entrepriseId: Long,
      var entreprise: Str = null
  )
  val sizeJob = sizeInt + sizeLong
  lazy val Job_table_2c2e78d9 = new LMDBTable[Job]("Job")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_1e21778c = new LMDBTable[Str]("Str")

  def toCString_1a8adc7c(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Str_5ee1f6c7(key: Long)(implicit zone: Zone): Str = {
    val value = Str_table_1e21778c.get(key)
    fromPtrByte_Str_4be97521(key, value)
  }
  def get_Person_7781ade7(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_4e881a54.get(key)
    fromPtrByte_Person_1244b85b(key, value)
  }
  def get_Job_264709ba(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_2c2e78d9.get(key)
    fromPtrByte_Job_77c18e4b(key, value)
  }

  def put_Str_ebe3b92(data_el: Str)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Str_10aaeae4(data_el)
    Str_table_1e21778c.put(key, size, value)
  }
  def put_Person_5739e806(data_el: Person)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Person_28b435af(data_el)
    Person_table_4e881a54.put(key, size, value)
  }
  def put_Job_6f1e1ee6(data_el: Job)(implicit zone: Zone): Unit = {
    val (key, size, value) = toPtrByte_Job_1083d5cf(data_el)
    Job_table_2c2e78d9.put(key, size, value)
  }

  def fromPtrByte_Str_4be97521(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Str = {
    val value1 = ptr1
    init_Str_7eefeaf8(key, value1)
  }
  def fromPtrByte_Person_1244b85b(key: Long, ptr1: Ptr[Byte])(
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
    init_Person_7aa6f248_key(key, (value1, value2, value3, value4))
  }
  def fromPtrByte_Job_77c18e4b(key: Long, ptr1: Ptr[Byte])(
      implicit zone: Zone
  ): Job = {
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_69535606_key(key, (value1, value2))
  }

  def toPtrByte_Str_10aaeae4(
      new_value: Str
  )(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
    val key = new_value.key
    val size = strlen(new_value.string).toInt
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    (key, size, value1)
  }
  def toPtrByte_Person_28b435af(
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
  def toPtrByte_Job_1083d5cf(
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

  def init_Job_69535606_key(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_69535606_key(
      params: Tuple2[Int, Long]
  )(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_2c2e78d9.size, params._1, params._2, null)
    put_Job_6f1e1ee6(new_val)
    new_val
  }
  def init_Job_69535606(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_69535606(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_2c2e78d9.size, params._1, params._2.key, params._2)
    put_Job_6f1e1ee6(new_val)
    new_val
  }
  def init_Person_7aa6f248_key(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_7aa6f248_key(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_4e881a54.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_5739e806(new_val)
    new_val
  }
  def init_Person_7aa6f248(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_7aa6f248(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_4e881a54.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_5739e806(new_val)
    new_val
  }
  def init_Str_7eefeaf8(key: Long, param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_7eefeaf8(param: CString)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_1e21778c.size, param)
    put_Str_ebe3b92(new_val)
    new_val
  }

  def isMinor_af97494(this_8566774: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_7d98983c(this_8566774);
    x_0.<(18)
  }

  def charAt_5c05c87a(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1.string, params._2)
  def strlen_57c0ab8a(param: Str)(implicit zone: Zone): Long =
    strlen(param.string)

  def age_7d98983c(this_8566774: Person)(implicit zone: Zone): Int = {
    this_8566774.age
  }
  def job_3c2853e6(this_8566774: Person)(implicit zone: Zone): Job = {
    if (this_8566774.job == null) {
      this_8566774.job = get_Job_264709ba(this_8566774.jobId)
    }
    this_8566774.job
  }
  def salary_6e662312(this_8566774: Person)(implicit zone: Zone): Int = {
    this_8566774.salary
  }
  def entreprise_45a5a8c7(this_32f67509: Job)(implicit zone: Zone): Str = {
    if (this_32f67509.entreprise == null) {
      this_32f67509.entreprise = get_Str_5ee1f6c7(this_32f67509.entrepriseId)
    }
    this_32f67509.entreprise
  }
  def string_6b352218(this_ae0aa91: Str)(implicit zone: Zone): CString = {
    this_ae0aa91.string
  }
  def size_157cebfb(this_32f67509: Job)(implicit zone: Zone): Int = {
    this_32f67509.size
  }
  def name_4a5d24f5(this_8566774: Person)(implicit zone: Zone): Str = {
    if (this_8566774.name == null) {
      this_8566774.name = get_Str_5ee1f6c7(this_8566774.nameId)
    }
    this_8566774.name
  }

  def `entreprise_=_313975b2`(this_32f67509: Job, entreprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_32f67509.entrepriseId = entreprise.key
    this_32f67509.entreprise = entreprise
  }
  def `age_=_2188d46a`(this_8566774: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_8566774.age = age
  }
  def `size_=_4dbfde47`(this_32f67509: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_32f67509.size = size
  }
  def `job_=_5b45125e`(this_8566774: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_8566774.jobId = job.key
    this_8566774.job = job
  }
  def `salary_=_21ab7861`(this_8566774: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_8566774.salary = salary
  }
  def `name_=_4667c3ba`(this_8566774: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_8566774.nameId = name.key
    this_8566774.name = name
  }

  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4e881a54.cursor;
      val x_2 = Person_table_4e881a54.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
              val x_5 = v_3;
              val x_6 = v_3;
              val x_7 = fromPtrByte_Person_1244b85b(x_5._1, x_6._2);
              val x_8 = salary_6e662312(x_7);
              val x_9 = name_4a5d24f5(x_7);
              val x_10 = charAt_5c05c87a(scala.Tuple2(x_9, 2L));
              val x_11 =
                toCString_1a8adc7c(x_10.asInstanceOf[scala.Byte].toString());
              val x_12 = init_Str_7eefeaf8(x_11);
              val x_13 = age_7d98983c(x_7);
              val x_14 = job_3c2853e6(x_7);
              val x_15 =
                init_Person_7aa6f248(scala.Tuple4(x_8, x_12, x_13, x_14));
              val x_16 = res_0;
              val x_19 = ((x$1_17: Person, acc_18: scala.Int) => acc_18.+(1))(
                x_15,
                x_16
              );
              res_0 = x_19;
              true
            }
          )
      }) {
        val x_20 = Person_table_4e881a54.next(cursor_1);
        v_3 = x_20
      };
      res_0
    }
  }
  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4e881a54.cursor;
      val x_2 = Person_table_4e881a54.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1244b85b(x_5._1, x_6._2);
            val x_8 = isMinor_af97494(x_7);
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
        val x_13 = Person_table_4e881a54.next(cursor_1);
        v_3 = x_13
      };
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4e881a54.cursor;
      val x_2 = Person_table_4e881a54.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1244b85b(x_5._1, x_6._2);
            val x_8 = salary_6e662312(x_7);
            val x_9 = name_4a5d24f5(x_7);
            val x_10 = name_4a5d24f5(x_7);
            val x_11 = strlen_57c0ab8a(x_10);
            val x_12 = job_3c2853e6(x_7);
            val x_13 = x_11.asInstanceOf[scala.Long].toInt;
            val x_14 = init_Person_7aa6f248(scala.Tuple4(x_8, x_9, x_13, x_12));
            val x_15 = res_0;
            val x_18 =
              ((x$1_16: Person, acc_17: scala.Int) => acc_17.+(1))(x_14, x_15);
            res_0 = x_18;
            true
          })
      }) {
        val x_19 = Person_table_4e881a54.next(cursor_1);
        v_3 = x_19
      };
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4e881a54.cursor;
      val x_2 = Person_table_4e881a54.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1244b85b(x_5._1, x_6._2);
            val x_8 = age_7d98983c(x_7);
            `age_=_2188d46a`(x_7, x_8.+(10));
            val x_9 = res_0;
            val x_12 =
              ((x$1_10: Person, acc_11: scala.Int) => acc_11.+(1))(x_7, x_9);
            res_0 = x_12;
            true
          })
      }) {
        val x_13 = Person_table_4e881a54.next(cursor_1);
        v_3 = x_13
      };
      res_0
    }
  }
  def insertions = Zone { implicit zone =>
    {
      val x_0 = toCString_1a8adc7c("EPFL");
      val x_1 = init_Str_7eefeaf8(x_0);
      val x_2 = init_Job_69535606(scala.Tuple2(10000, x_1));
      val x_3 = toCString_1a8adc7c("Lucien");
      val x_4 = init_Str_7eefeaf8(x_3);
      val x_5 = init_Person_7aa6f248(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_1a8adc7c("John");
      val x_7 = init_Str_7eefeaf8(x_6);
      val x_8 = init_Person_7aa6f248(scala.Tuple4(100, x_7, 16, x_2));
      ()
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4e881a54.cursor;
      val x_2 = Person_table_4e881a54.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1244b85b(x_5._1, x_6._2);
            val x_8 = toCString_1a8adc7c("Test");
            val x_9 = init_Str_7eefeaf8(x_8);
            val x_10 = age_7d98983c(x_7);
            val x_11 = job_3c2853e6(x_7);
            val x_12 =
              init_Person_7aa6f248(scala.Tuple4(0, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            val x_16 =
              ((x$1_14: Person, acc_15: scala.Int) => acc_15.+(1))(x_12, x_13);
            res_0 = x_16;
            true
          })
      }) {
        val x_17 = Person_table_4e881a54.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4e881a54.cursor;
      val x_2 = Person_table_4e881a54.first(cursor_1);
      var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
      while ({
        val x_4 = v_3;
        x_4._2
          .!=(null)
          .&&({
            val x_5 = v_3;
            val x_6 = v_3;
            val x_7 = fromPtrByte_Person_1244b85b(x_5._1, x_6._2);
            val x_8 = salary_6e662312(x_7);
            val x_9 = name_4a5d24f5(x_7);
            val x_10 = age_7d98983c(x_7);
            val x_11 = job_3c2853e6(x_7);
            val x_12 =
              init_Person_7aa6f248(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
            val x_13 = res_0;
            val x_16 =
              ((x$1_14: Person, acc_15: scala.Int) => acc_15.+(1))(x_12, x_13);
            res_0 = x_16;
            true
          })
      }) {
        val x_17 = Person_table_4e881a54.next(cursor_1);
        v_3 = x_17
      };
      res_0
    }
  }
}

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
  lazy val Job_table_4258810a = new LMDBTable[Job]("Job")

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
  lazy val Person_table_2d20b595 = new LMDBTable[Person]("Person")

  class Str(val key: Long, var string: CString)
  lazy val Str_table_18fb3c78 = new LMDBTable[Str]("Str")

  def get_Job_528ddca3(table: LMDBTable[Job], key: Long)(
      implicit zone: Zone
  ): Job = {
    val ptr1 = table.get(key)
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    init_Job_7c1b873a(key, (value1, value2))
  }
  def get_Str_5cdf65a3(table: LMDBTable[Str], key: Long)(
      implicit zone: Zone
  ): Str = {
    val ptr1 = table.get(key)
    val value1 = ptr1
    init_Str_1b3e92f5(key, value1)
  }
  def get_Person_276a3df5(table: LMDBTable[Person], key: Long)(
      implicit zone: Zone
  ): Person = {
    val ptr1 = table.get(key)
    val value1 = intget(ptr1, sizeInt)
    val ptr2 = ptr1 + sizeInt
    val value2 = longget(ptr2, sizeLong)
    val ptr3 = ptr2 + sizeLong
    val value3 = intget(ptr3, sizeInt)
    val ptr4 = ptr3 + sizeInt
    val value4 = longget(ptr4, sizeLong)
    val ptr5 = ptr4 + sizeLong
    init_Person_7e84c75(key, (value1, value2, value3, value4))
  }

  def put_Job_27659363(table: LMDBTable[Job], new_value: Job)(
      implicit zone: Zone
  ): Unit = {
    val key = table.size
    val size = sizeJob
    val value1 = alloc[Byte](size)
    intcpy(value1, new_value.size, sizeInt)
    val value2 = value1 + sizeInt
    longcpy(value2, new_value.enterpriseId, sizeLong)
    val value3 = value2 + sizeLong
    table.put(key, size, value1)
  }
  def put_Str_5cfeb25b(table: LMDBTable[Str], new_value: Str)(
      implicit zone: Zone
  ): Unit = {
    val key = table.size
    val size = strlen(new_value.string)
    val value1 = alloc[Byte](size)
    strcpy(value1, new_value.string)
    table.put(key, size, value1)
  }
  def put_Person_11d8ffd8(table: LMDBTable[Person], new_value: Person)(
      implicit zone: Zone
  ): Unit = {
    val key = table.size
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
    table.put(key, size, value1)
  }

  def init_Job_7c1b873a(key: Long, params: Tuple2[Int, Long])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2, null)
    new_val
  }
  def init_Job_7c1b873a(params: Tuple2[Int, Long])(implicit zone: Zone): Job = {
    val new_val = new Job(Job_table_4258810a.size, params._1, params._2, null)
    put_Job_27659363(Job_table_4258810a, new_val)
    new_val
  }
  def init_Job_7c1b873a(key: Long, params: Tuple2[Int, Str])(
      implicit zone: Zone
  ): Job = {
    val new_val = new Job(key, params._1, params._2.key, params._2)
    new_val
  }
  def init_Job_7c1b873a(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val =
      new Job(Job_table_4258810a.size, params._1, params._2.key, params._2)
    put_Job_27659363(Job_table_4258810a, new_val)
    new_val
  }
  def init_Person_7e84c75(key: Long, params: Tuple4[Int, Long, Int, Long])(
      implicit zone: Zone
  ): Person = {
    val new_val =
      new Person(key, params._1, params._2, null, params._3, params._4, null)
    new_val
  }
  def init_Person_7e84c75(
      params: Tuple4[Int, Long, Int, Long]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_2d20b595.size,
      params._1,
      params._2,
      null,
      params._3,
      params._4,
      null
    )
    put_Person_11d8ffd8(Person_table_2d20b595, new_val)
    new_val
  }
  def init_Person_7e84c75(key: Long, params: Tuple4[Int, Str, Int, Job])(
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
  def init_Person_7e84c75(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = new Person(
      Person_table_2d20b595.size,
      params._1,
      params._2.key,
      params._2,
      params._3,
      params._4.key,
      params._4
    )
    put_Person_11d8ffd8(Person_table_2d20b595, new_val)
    new_val
  }
  def init_Str_1b3e92f5(key: Long, param: String)(implicit zone: Zone): Str = {
    val new_val = new Str(key, param)
    new_val
  }
  def init_Str_1b3e92f5(param: String)(implicit zone: Zone): Str = {
    val new_val = new Str(Str_table_18fb3c78.size, param)
    put_Str_5cfeb25b(Str_table_18fb3c78, new_val)
    new_val
  }

  def isMinor_159a5b14(this_3df80e68: Person)(implicit zone: Zone): Boolean = {
    val x_0 = age_541164c9(this_3df80e68);
    x_0.<(18)
  }

  def charAt_2ad92f15(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1, params._2)
  def strlen_1328570c(param: Str)(implicit zone: Zone): Long = strlen(param)

  def string_271c79c4(this_3262600a: Str)(implicit zone: Zone): String = {
    this_3262600a.string
  }
  def job_76ddca1c(this_3df80e68: Person)(implicit zone: Zone): Job = {
    if (this_3df80e68.job == null) {
      this_3df80e68.job =
        get_Job_528ddca3(Job_table_4258810a, this_3df80e68.jobId)
    }
    this_3df80e68.job
  }
  def enterprise_b529b17(this_2ccecaaf: Job)(implicit zone: Zone): Str = {
    if (this_2ccecaaf.enterprise == null) {
      this_2ccecaaf.enterprise =
        get_Str_5cdf65a3(Str_table_18fb3c78, this_2ccecaaf.enterpriseId)
    }
    this_2ccecaaf.enterprise
  }
  def salary_3d84459f(this_3df80e68: Person)(implicit zone: Zone): Int = {
    this_3df80e68.salary
  }
  def age_541164c9(this_3df80e68: Person)(implicit zone: Zone): Int = {
    this_3df80e68.age
  }
  def size_7912d9b2(this_2ccecaaf: Job)(implicit zone: Zone): Int = {
    this_2ccecaaf.size
  }
  def name_618761a6(this_3df80e68: Person)(implicit zone: Zone): Str = {
    if (this_3df80e68.name == null) {
      this_3df80e68.name =
        get_Str_5cdf65a3(Str_table_18fb3c78, this_3df80e68.nameId)
    }
    this_3df80e68.name
  }

  def `salary_=_54c59eb8`(this_3df80e68: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_3df80e68.salary = salary
  }
  def `job_=_3c1af746`(this_3df80e68: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_3df80e68.jobId = job.key
    this_3df80e68.job = job
  }
  def `name_=_7382f949`(this_3df80e68: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_3df80e68.nameId = name.key
    this_3df80e68.name = name
  }
  def `age_=_5797fa08`(this_3df80e68: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_3df80e68.age = age
  }
  def `size_=_2f5f9e1c`(this_2ccecaaf: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2ccecaaf.size = size
  }
  def `enterprise_=_4494ec76`(this_2ccecaaf: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2ccecaaf.enterpriseId = enterprise.key
    this_2ccecaaf.enterprise = enterprise
  }

  def allMinors = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_2d20b595.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_276a3df5(Person_table_2d20b595, i_4);
            val x_6 = isMinor_159a5b14(x_5);
            if (x_6.asInstanceOf[scala.Boolean]) {
              val x_7 = res_0;
              res_0 = x_7.+(1);
              true
            } else
              true
          })
      }) {
        val x_8 = i_2;
        i_2 = x_8.+(1)
      };
      res_0
    }
  }
  def sizes = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_2d20b595.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_276a3df5(Person_table_2d20b595, i_4);
            val x_6 = salary_3d84459f(x_5);
            val x_7 = name_618761a6(x_5);
            val x_8 = name_618761a6(x_5);
            val x_9 = strlen_1328570c(x_8);
            val x_10 = job_76ddca1c(x_5);
            val x_11 = x_9.asInstanceOf[scala.Long].toInt;
            val x_12 = init_Person_7e84c75(scala.Tuple4(x_6, x_7, x_11, x_10));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = i_2;
        i_2 = x_14.+(1)
      };
      res_0
    }
  }
  def allOld = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_2d20b595.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_276a3df5(Person_table_2d20b595, i_4);
            val x_6 = salary_3d84459f(x_5);
            val x_7 = name_618761a6(x_5);
            val x_8 = age_541164c9(x_5);
            val x_9 = job_76ddca1c(x_5);
            val x_10 =
              init_Person_7e84c75(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = i_2;
        i_2 = x_12.+(1)
      };
      res_0
    }
  }
  def allOld3 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_2d20b595.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
              val i_4 = i_2;
              val x_5 = get_Person_276a3df5(Person_table_2d20b595, i_4);
              val x_6 = init_Str_1b3e92f5("Test");
              val x_7 = age_541164c9(x_5);
              val x_8 = job_76ddca1c(x_5);
              val x_9 =
                init_Person_7e84c75(scala.Tuple4(0, x_6, x_7.+(100), x_8));
              val x_10 = res_0;
              res_0 = x_10.+(1);
              true
            }
          )
      }) {
        val x_11 = i_2;
        i_2 = x_11.+(1)
      };
      res_0
    }
  }
  def charAtQuery = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_2d20b595.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_276a3df5(Person_table_2d20b595, i_4);
            val x_6 = salary_3d84459f(x_5);
            val x_7 = name_618761a6(x_5);
            val x_8 = charAt_2ad92f15(scala.Tuple2(x_7, 2L));
            val x_9 = init_Str_1b3e92f5(x_8.asInstanceOf[scala.Byte].toString());
            val x_10 = age_541164c9(x_5);
            val x_11 = job_76ddca1c(x_5);
            val x_12 = init_Person_7e84c75(scala.Tuple4(x_6, x_9, x_10, x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = i_2;
        i_2 = x_14.+(1)
      };
      res_0
    }
  }
  def allOld2 = Zone { implicit zone =>
    {
      var res_0: scala.Int = 0;
      val size_1 = Person_table_2d20b595.size;
      var i_2: scala.Int = 0;
      while ({
        val x_3 = i_2;
        x_3
          .<(size_1)
          .&&({
            val i_4 = i_2;
            val x_5 = get_Person_276a3df5(Person_table_2d20b595, i_4);
            val x_6 = age_541164c9(x_5);
            `age_=_5797fa08`(x_5, x_6.+(10));
            val x_7 = res_0;
            res_0 = x_7.+(1);
            true
          })
      }) {
        val x_8 = i_2;
        i_2 = x_8.+(1)
      };
      res_0
    }
  }
}

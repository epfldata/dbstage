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

  type CitizenData = CStruct3[Long, Long, Str]
  type Citizen = Ptr[CitizenData]
  val sizeCitizen = sizeof[CitizenData]
  lazy val Citizen_table_6ed13d8 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_2ae30072 = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_1960eedb = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_61d08083 = new LMDBTable[CString]("Str")

  def toCString_f4d2cb2(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Citizen_3d9f4db9(key: Long)(implicit zone: Zone): Citizen = {
    Citizen_table_6ed13d8.get(key)
  }
  def get_Job_23977ae7(key: Long)(implicit zone: Zone): Job = {
    Job_table_2ae30072.get(key)
  }
  def get_Person_46a6228e(key: Long)(implicit zone: Zone): Person = {
    Person_table_1960eedb.get(key)
  }

  def get_Str_2068be54(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_61d08083.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_6d3fa955(data_el: Citizen)(implicit zone: Zone): Unit = {
    val ptr3 = data_el._3
    data_el._3 = null

    Citizen_table_6ed13d8.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
    data_el._3 = ptr3

  }
  def put_Job_64006762(data_el: Job)(implicit zone: Zone): Unit = {
    val ptr4 = data_el._4
    data_el._4 = null

    Job_table_2ae30072.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
    data_el._4 = ptr4

  }
  def put_Person_354decc2(data_el: Person)(implicit zone: Zone): Unit = {
    val ptr4 = data_el._4
    data_el._4 = null
    val ptr7 = data_el._7
    data_el._7 = null

    Person_table_1960eedb.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
    data_el._4 = ptr4
    data_el._7 = ptr7

  }

  def put_Str_3c64def8(str: Str)(implicit zone: Zone): Unit = {
    Str_table_61d08083.put(str._1, strlen(str._2) + 1, str._2)
  }

  def init_Citizen_58112ebb(
      params: Tuple2[Str, Long]
  )(implicit zone: Zone): Citizen = {
    val key = Citizen_table_6ed13d8.getNextKey
    val new_val = alloc[CitizenData]
    new_val._1 = key
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._4 = params._2
    put_Citizen_6d3fa955(new_val)
    get_Citizen_3d9f4db9(key)
  }
  def init_Job_15be7642(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val key = Job_table_2ae30072.getNextKey
    val new_val = alloc[JobData]
    new_val._1 = key
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_64006762(new_val)
    get_Job_23977ae7(key)
  }
  def init_Person_190573bb(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val key = Person_table_1960eedb.getNextKey
    val new_val = alloc[PersonData]
    new_val._1 = key
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_354decc2(new_val)
    get_Person_46a6228e(key)
  }
  def init_Str_3b9bcc69(param: CString)(implicit zone: Zone): Str = {
    val key = Str_table_61d08083.getNextKey
    val new_val = alloc[StrData]
    new_val._1 = key
    new_val._2 = param
    put_Str_3c64def8(new_val)
    get_Str_2068be54(key)
  }

  def isMinor_29c8baa5(this_5bf3778e: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_4d594122(this_5bf3778e);
    x_0.<(18)
  }
  def showKey_64859f53(this_46e93d11: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_48e5cad7(this_46e93d11);
    x_0.toString()
  }

  def charAt_1ebeca9d(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_37cb5cd0(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_4d594122(this_5bf3778e: Person)(implicit zone: Zone): Int = {
    this_5bf3778e._5
  }
  def get_enterprise_51ec40e8(this_67d95a9e: Job)(implicit zone: Zone): Str = {
    if (this_67d95a9e._4 == null) {
      this_67d95a9e._4 = get_Str_2068be54(this_67d95a9e._3)
    }
    this_67d95a9e._4
  }
  def get_job_201c667d(this_5bf3778e: Person)(implicit zone: Zone): Job = {
    if (this_5bf3778e._7 == null) {
      this_5bf3778e._7 = get_Job_23977ae7(this_5bf3778e._6)
    }
    this_5bf3778e._7
  }
  def get_key_48e5cad7(this_46e93d11: Citizen)(implicit zone: Zone): Long = {
    this_46e93d11._4
  }
  def get_name_41cf0b17(this_46e93d11: Citizen)(implicit zone: Zone): Str = {
    if (this_46e93d11._3 == null) {
      this_46e93d11._3 = get_Str_2068be54(this_46e93d11._2)
    }
    this_46e93d11._3
  }
  def get_name_6c6fa41a(this_5bf3778e: Person)(implicit zone: Zone): Str = {
    if (this_5bf3778e._4 == null) {
      this_5bf3778e._4 = get_Str_2068be54(this_5bf3778e._3)
    }
    this_5bf3778e._4
  }
  def get_salary_5fb7527f(this_5bf3778e: Person)(implicit zone: Zone): Int = {
    this_5bf3778e._2
  }
  def get_size_1cdbe87a(this_67d95a9e: Job)(implicit zone: Zone): Int = {
    this_67d95a9e._2
  }
  def get_string_2575df45(this_34abf085: Str)(implicit zone: Zone): CString = {
    this_34abf085._2
  }

  def set_age_70df2eee(this_5bf3778e: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5bf3778e._5 = age
    put_Person_354decc2(this_5bf3778e)
  }
  def set_enterprise_58a4b447(this_67d95a9e: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_67d95a9e._3 = enterprise._1
    this_67d95a9e._4 = enterprise
    put_Job_64006762(this_67d95a9e)
  }
  def set_job_204cdb02(this_5bf3778e: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_5bf3778e._6 = job._1
    this_5bf3778e._7 = job
    put_Person_354decc2(this_5bf3778e)
  }
  def set_name_296d39bb(this_5bf3778e: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_5bf3778e._3 = name._1
    this_5bf3778e._4 = name
    put_Person_354decc2(this_5bf3778e)
  }
  def set_salary_79680735(this_5bf3778e: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5bf3778e._2 = salary
    put_Person_354decc2(this_5bf3778e)
  }
  def set_size_41392d3(this_67d95a9e: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_67d95a9e._2 = size
    put_Job_64006762(this_67d95a9e)
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_29c8baa5(row_5);
            if (x_6.asInstanceOf[scala.Boolean]) {
              val x_7 = res_0;
              res_0 = x_7.+(1);
              true
            } else
              true
          })
      }) {
        val x_8 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_8
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_9
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_5fb7527f(row_5);
            val x_7 = get_name_6c6fa41a(row_5);
            val x_8 = get_age_4d594122(row_5);
            val x_9 = get_job_201c667d(row_5);
            val x_10 =
              init_Person_190573bb(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_12
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_13
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_4d594122(row_5);
            set_age_70df2eee(row_5, x_6.+(10));
            val x_7 = res_0;
            res_0 = x_7.+(1);
            true
          })
      }) {
        val x_8 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_8
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_9
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = toCString_f4d2cb2("Test");
            val x_7 = init_Str_3b9bcc69(x_6);
            val x_8 = get_age_4d594122(row_5);
            val x_9 = get_job_201c667d(row_5);
            val x_10 =
              init_Person_190573bb(scala.Tuple4(0, x_7, x_8.+(100), x_9));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_12
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_13
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
              val row_5 = v_3;
              val x_6 = get_salary_5fb7527f(row_5);
              val x_7 = get_name_6c6fa41a(row_5);
              val x_8 = charAt_1ebeca9d(scala.Tuple2(x_7, 2L));
              val x_9 =
                toCString_f4d2cb2(x_8.asInstanceOf[scala.Byte].toString());
              val x_10 = init_Str_3b9bcc69(x_9);
              val x_11 = get_age_4d594122(row_5);
              val x_12 = get_job_201c667d(row_5);
              val x_13 =
                init_Person_190573bb(scala.Tuple4(x_6, x_10, x_11, x_12));
              val x_14 = res_0;
              res_0 = x_14.+(1);
              true
            }
          )
      }) {
        val x_15 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_15
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_16 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_16
    }
  }
  def insertionAtKey: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      val x_0 = toCString_f4d2cb2("Bob");
      val x_1 = init_Str_3b9bcc69(x_0);
      init_Citizen_58112ebb(scala.Tuple2(x_1, 42L));
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      val x_0 = toCString_f4d2cb2("EPFL");
      val x_1 = init_Str_3b9bcc69(x_0);
      val x_2 = init_Job_15be7642(scala.Tuple2(10000, x_1));
      val x_3 = toCString_f4d2cb2("Lucien");
      val x_4 = init_Str_3b9bcc69(x_3);
      val x_5 = init_Person_190573bb(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_f4d2cb2("John");
      val x_7 = init_Str_3b9bcc69(x_6);
      val x_8 = init_Person_190573bb(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row1_5 = v_3;
            val cursor_6 = Person_table_1960eedb.cursorOpen();
            val x_7 = Person_table_1960eedb.first(cursor_6);
            var v_8: scala.Any = x_7;
            while ({
              val x_9 = v_8;
              x_9
                .!=(null)
                .&&({
                  val row2_10 = v_8;
                  val x_11 = get_age_4d594122(
                    ((scala.Tuple2(row1_5, row2_10)): scala.Any)._1
                  );
                  val x_12 = get_age_4d594122(
                    ((scala.Tuple2(row1_5, row2_10)): scala.Any)._2
                  );
                  val x_13 = scala.math.`package`.abs(x_11.-(x_12));
                  val x_14 = res_0;
                  val x_15 = scala.Predef.intWrapper(x_13);
                  val x_16 = x_15.max(x_14);
                  res_0 = x_16;
                  true
                })
            }) {
              val x_17 = Person_table_1960eedb.next(cursor_6);
              v_8 = x_17
            };
            Person_table_1960eedb.cursorClose(cursor_6);
            true
          })
      }) {
        val x_18 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_18
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_19 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_19
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val row_6 = get_job_201c667d(row_5);
            val row_7 = get_size_1cdbe87a(row_6);
            val row_8 = row_7.+(100);
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_10
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_11
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      val cursor_0 = Person_table_1960eedb.cursorOpen();
      val x_1 = Person_table_1960eedb.first(cursor_0);
      var v_2: Person = x_1;
      while ({
        val x_3 = v_2;
        x_3
          .!=(null)
          .&&({
            val row_4 = v_2;
            val x_5 = get_age_4d594122(row_4);
            scala.Predef.println(x_5);
            true
          })
      }) {
        val x_6 = Person_table_1960eedb.next(cursor_0);
        v_2 = x_6
      };
      Person_table_1960eedb.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_2ae30072.cursorOpen();
      val x_2 = Job_table_2ae30072.first(cursor_1);
      var v_3: Job = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = res_0;
            res_0 = x_6.+(1);
            true
          })
      }) {
        val x_7 = Job_table_2ae30072.next(cursor_1);
        v_3 = x_7
      };
      Job_table_2ae30072.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_8
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = res_0;
            res_0 = x_6.+(1);
            true
          })
      }) {
        val x_7 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_7
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_8
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_61d08083.cursorOpen();
      val x_2 = Str_table_61d08083.first(cursor_1);
      var v_3: Str = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = res_0;
            res_0 = x_6.+(1);
            true
          })
      }) {
        val x_7 = Str_table_61d08083.next(cursor_1);
        v_3 = x_7
      };
      Str_table_61d08083.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_8
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_5fb7527f(row_5);
            val x_7 = get_name_6c6fa41a(row_5);
            val x_8 = get_name_6c6fa41a(row_5);
            val x_9 = strlen_37cb5cd0(x_8);
            val x_10 = get_job_201c667d(row_5);
            val x_11 = x_9.asInstanceOf[scala.Long].toInt;
            val x_12 = init_Person_190573bb(scala.Tuple4(x_6, x_7, x_11, x_10));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_14
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_15
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_61d08083.dbiOpen();
      Person_table_1960eedb.dbiOpen();
      Job_table_2ae30072.dbiOpen();
      Citizen_table_6ed13d8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_1960eedb.cursorOpen();
      val x_2 = Person_table_1960eedb.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = res_0;
            val x_7 = get_age_4d594122(row_5);
            res_0 = x_7.+(x_6);
            true
          })
      }) {
        val x_8 = Person_table_1960eedb.next(cursor_1);
        v_3 = x_8
      };
      Person_table_1960eedb.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_61d08083.dbiClose();
      Person_table_1960eedb.dbiClose();
      Job_table_2ae30072.dbiClose();
      Citizen_table_6ed13d8.dbiClose();
      x_9
    }
  }
}

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

  type CitizenData = CStruct4[Long, Long, Str, Int]
  type Citizen = Ptr[CitizenData]
  val sizeCitizen = sizeof[CitizenData]
  lazy val Citizen_table_1dce1ae8 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_78a82b2d = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_33938670 = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_62f8eaa7 = new LMDBTable[CString]("Str")

  def toCString_f0ca54a(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_36c406c5(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Citizen_406bd82a(value: Citizen): Citizen = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_45756a2(value: Job): Job = {
    if (value != null) {
      value._4 = null
    }
    value
  }
  def empty_pointers_Person_266726fd(value: Person): Person = {
    if (value != null) {
      value._4 = null
      value._7 = null
    }
    value
  }

  def get_Citizen_836533f(key: Long)(implicit zone: Zone): Citizen = {
    val value = Citizen_table_1dce1ae8.get(key)
    empty_pointers_Citizen_406bd82a(value)
  }
  def get_Job_1602709b(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_78a82b2d.get(key)
    empty_pointers_Job_45756a2(value)
  }
  def get_Person_28dec6bf(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_33938670.get(key)
    empty_pointers_Person_266726fd(value)
  }

  def get_Str_37914d97(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_62f8eaa7.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_d845ff1(data_el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_1dce1ae8.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_8877072(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_78a82b2d.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_5632b4e3(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_33938670.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_6bf2a647(str: Str)(implicit zone: Zone): Unit = {
    Str_table_62f8eaa7.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Citizen_34c7dcb4(el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_1dce1ae8.delete(el._1)
  }
  def delete_Job_4a7984f5(el: Job)(implicit zone: Zone): Unit = {
    Job_table_78a82b2d.delete(el._1)
  }
  def delete_Person_3e937c4a(el: Person)(implicit zone: Zone): Unit = {
    Person_table_33938670.delete(el._1)
  }
  def delete_Str_462201a2(el: Str)(implicit zone: Zone): Unit = {
    Str_table_62f8eaa7.delete(el._1)
  }

  def init_Citizen_497e52ec(
      params: Tuple3[Str, Long, Int]
  )(implicit zone: Zone): Citizen = {
    val new_val = alloc[CitizenData]
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._1 = params._2
    new_val._4 = params._3
    put_Citizen_d845ff1(new_val)
    get_Citizen_836533f(new_val._1)
  }
  def init_Job_453d68f0(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = Job_table_78a82b2d.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_8877072(new_val)
    get_Job_1602709b(new_val._1)
  }
  def init_Person_541a1274(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = alloc[PersonData]
    new_val._1 = Person_table_33938670.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_5632b4e3(new_val)
    get_Person_28dec6bf(new_val._1)
  }
  def init_Str_6addfded(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_62f8eaa7.getNextKey
    new_val._2 = param
    put_Str_6bf2a647(new_val)
    get_Str_37914d97(new_val._1)
  }

  def isMinor_1b5995bf(this_5c41ac7c: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_10ed72dc(this_5c41ac7c);
    x_0.<(18)
  }
  def showKey_2916679e(this_e57c3e7: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_4eb88f30(this_e57c3e7);
    x_0.toString()
  }

  def charAt_40dc1994(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_5e80d026(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_7196df1b(this_e57c3e7: Citizen)(implicit zone: Zone): Int = {
    this_e57c3e7._4
  }
  def get_age_10ed72dc(this_5c41ac7c: Person)(implicit zone: Zone): Int = {
    this_5c41ac7c._5
  }
  def get_enterprise_4e059edb(this_1366e902: Job)(implicit zone: Zone): Str = {
    if (this_1366e902._4 == null) {
      this_1366e902._4 = get_Str_37914d97(this_1366e902._3)
    }
    this_1366e902._4
  }
  def get_job_23d915b8(this_5c41ac7c: Person)(implicit zone: Zone): Job = {
    if (this_5c41ac7c._7 == null) {
      this_5c41ac7c._7 = get_Job_1602709b(this_5c41ac7c._6)
    }
    this_5c41ac7c._7
  }
  def get_key_4eb88f30(this_e57c3e7: Citizen)(implicit zone: Zone): Long = {
    this_e57c3e7._1
  }
  def get_name_64f068df(this_e57c3e7: Citizen)(implicit zone: Zone): Str = {
    if (this_e57c3e7._3 == null) {
      this_e57c3e7._3 = get_Str_37914d97(this_e57c3e7._2)
    }
    this_e57c3e7._3
  }
  def get_name_407aa3ba(this_5c41ac7c: Person)(implicit zone: Zone): Str = {
    if (this_5c41ac7c._4 == null) {
      this_5c41ac7c._4 = get_Str_37914d97(this_5c41ac7c._3)
    }
    this_5c41ac7c._4
  }
  def get_salary_226b1e28(this_5c41ac7c: Person)(implicit zone: Zone): Int = {
    this_5c41ac7c._2
  }
  def get_size_4974ab08(this_1366e902: Job)(implicit zone: Zone): Int = {
    this_1366e902._2
  }
  def get_string_3b579574(this_2ea59469: Str)(implicit zone: Zone): String = {
    fromCString_36c406c5(this_2ea59469._2)
  }

  def set_age_53770d66(this_e57c3e7: Citizen, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_e57c3e7._4 = age
  }
  def set_age_772fe44f(this_5c41ac7c: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5c41ac7c._5 = age
  }
  def set_enterprise_1b1bf061(this_1366e902: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_1366e902._3 = enterprise._1
    this_1366e902._4 = enterprise
  }
  def set_job_706cc259(this_5c41ac7c: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_5c41ac7c._6 = job._1
    this_5c41ac7c._7 = job
  }
  def set_name_54760efc(this_5c41ac7c: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_5c41ac7c._3 = name._1
    this_5c41ac7c._4 = name
  }
  def set_salary_5c7385a1(this_5c41ac7c: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_5c41ac7c._2 = salary
  }
  def set_size_18affeb6(this_1366e902: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1366e902._2 = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = isMinor_1b5995bf(row_6);
            if (x_7.asInstanceOf[scala.Boolean]) {
              val x_8 = res_0;
              res_0 = x_8.+(1);
              true
            } else
              true
          })
      }) {
        val x_9 = Person_table_33938670.prev(cursor_1);
        val x_10 = empty_pointers_Person_266726fd(x_9);
        v_4 = x_10
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_226b1e28(row_6);
            val x_8 = get_name_407aa3ba(row_6);
            val x_9 = get_age_10ed72dc(row_6);
            val x_10 = get_job_23d915b8(row_6);
            val x_11 =
              init_Person_541a1274(scala.Tuple4(x_7, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_33938670.prev(cursor_1);
        val x_14 = empty_pointers_Person_266726fd(x_13);
        v_4 = x_14
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_age_10ed72dc(row_6);
            set_age_772fe44f(row_6, x_7.+(10));
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_33938670.prev(cursor_1);
        val x_10 = empty_pointers_Person_266726fd(x_9);
        v_4 = x_10
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = toCString_f0ca54a("Test");
            val x_8 = init_Str_6addfded(x_7);
            val x_9 = get_age_10ed72dc(row_6);
            val x_10 = get_job_23d915b8(row_6);
            val x_11 =
              init_Person_541a1274(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_33938670.prev(cursor_1);
        val x_14 = empty_pointers_Person_266726fd(x_13);
        v_4 = x_14
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
              val row_6 = v_4;
              val x_7 = get_salary_226b1e28(row_6);
              val x_8 = get_name_407aa3ba(row_6);
              val x_9 = charAt_40dc1994(scala.Tuple2(x_8, 2L));
              val x_10 =
                toCString_f0ca54a(x_9.asInstanceOf[scala.Byte].toString());
              val x_11 = init_Str_6addfded(x_10);
              val x_12 = get_age_10ed72dc(row_6);
              val x_13 = get_job_23d915b8(row_6);
              val x_14 =
                init_Person_541a1274(scala.Tuple4(x_7, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_33938670.prev(cursor_1);
        val x_17 = empty_pointers_Person_266726fd(x_16);
        v_4 = x_17
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_18
    }
  }
  def deleteAllMajors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Person_table_33938670.cursorOpen();
      val x_1 = Person_table_33938670.last(cursor_0);
      val x_2 = empty_pointers_Person_266726fd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_1b5995bf(row_5);
            val x_7 = x_6.asInstanceOf[scala.Boolean].`unary_!`;
            if (x_7)
              delete_Person_3e937c4a(row_5)
            else
              ();
            true
          })
      }) {
        val x_8 = Person_table_33938670.prev(cursor_0);
        val x_9 = empty_pointers_Person_266726fd(x_8);
        v_3 = x_9
      };
      Person_table_33938670.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def deleteAllMinors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Person_table_33938670.cursorOpen();
      val x_1 = Person_table_33938670.last(cursor_0);
      val x_2 = empty_pointers_Person_266726fd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_1b5995bf(row_5);
            if (x_6.asInstanceOf[scala.Boolean])
              delete_Person_3e937c4a(row_5)
            else
              ();
            true
          })
      }) {
        val x_7 = Person_table_33938670.prev(cursor_0);
        val x_8 = empty_pointers_Person_266726fd(x_7);
        v_3 = x_8
      };
      Person_table_33938670.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def economicCrisis: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Job_table_78a82b2d.cursorOpen();
      val x_1 = Job_table_78a82b2d.last(cursor_0);
      val x_2 = empty_pointers_Job_45756a2(x_1);
      var v_3: Job = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            delete_Job_4a7984f5(row_5);
            true
          })
      }) {
        val x_6 = Job_table_78a82b2d.prev(cursor_0);
        val x_7 = empty_pointers_Job_45756a2(x_6);
        v_3 = x_7
      };
      Job_table_78a82b2d.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def insertOldPeople: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Person_table_33938670.cursorOpen();
      val x_1 = Person_table_33938670.last(cursor_0);
      val x_2 = empty_pointers_Person_266726fd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_226b1e28(row_5);
            val x_7 = get_name_407aa3ba(row_5);
            val x_8 = get_age_10ed72dc(row_5);
            val x_9 = get_job_23d915b8(row_5);
            init_Person_541a1274(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            true
          })
      }) {
        val x_10 = Person_table_33938670.prev(cursor_0);
        val x_11 = empty_pointers_Person_266726fd(x_10);
        v_3 = x_11
      };
      Person_table_33938670.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def insertionAtKey: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val x_0 = toCString_f0ca54a("Bob");
      val x_1 = init_Str_6addfded(x_0);
      init_Citizen_497e52ec(scala.Tuple3(x_1, 42L, 12));
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def insertionAtKey2: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val x_0 = toCString_f0ca54a("Alice");
      val x_1 = init_Str_6addfded(x_0);
      init_Citizen_497e52ec(scala.Tuple3(x_1, 42L, 100));
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val x_0 = toCString_f0ca54a("EPFL");
      val x_1 = init_Str_6addfded(x_0);
      val x_2 = init_Job_453d68f0(scala.Tuple2(10000, x_1));
      val x_3 = toCString_f0ca54a("Lucien");
      val x_4 = init_Str_6addfded(x_3);
      val x_5 = init_Person_541a1274(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_f0ca54a("John");
      val x_7 = init_Str_6addfded(x_6);
      val x_8 = init_Person_541a1274(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = -2147483648;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Person_table_33938670.cursorOpen();
            val x_8 = Person_table_33938670.last(cursor_7);
            val x_9 = empty_pointers_Person_266726fd(x_8);
            var v_10: Person = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 = get_age_10ed72dc(row1_6);
                  val x_14 = get_age_10ed72dc(row2_12);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.intWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Person_table_33938670.prev(cursor_7);
              val x_20 = empty_pointers_Person_266726fd(x_19);
              v_10 = x_20
            };
            Person_table_33938670.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Person_table_33938670.prev(cursor_1);
        val x_22 = empty_pointers_Person_266726fd(x_21);
        v_4 = x_22
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val row_7 = get_job_23d915b8(row_6);
            val row_8 = get_size_4974ab08(row_7);
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_33938670.prev(cursor_1);
        val x_11 = empty_pointers_Person_266726fd(x_10);
        v_4 = x_11
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_12 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_12
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Person_table_33938670.cursorOpen();
      val x_1 = Person_table_33938670.last(cursor_0);
      val x_2 = empty_pointers_Person_266726fd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_10ed72dc(row_5);
            scala.Predef.println(x_6);
            true
          })
      }) {
        val x_7 = Person_table_33938670.prev(cursor_0);
        val x_8 = empty_pointers_Person_266726fd(x_7);
        v_3 = x_8
      };
      Person_table_33938670.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def printAllCitizens: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Citizen_table_1dce1ae8.cursorOpen();
      val x_1 = Citizen_table_1dce1ae8.last(cursor_0);
      val x_2 = empty_pointers_Citizen_406bd82a(x_1);
      var v_3: Citizen = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Citizen: (", ", ", ", ", ")");
            val x_7 = get_name_64f068df(row_5);
            val x_8 = get_string_3b579574(x_7);
            val x_9 = get_key_4eb88f30(row_5);
            val x_10 = get_age_7196df1b(row_5);
            val x_11 = x_6.s(x_8, x_9, x_10);
            scala.Predef.println(x_11);
            true
          })
      }) {
        val x_12 = Citizen_table_1dce1ae8.prev(cursor_0);
        val x_13 = empty_pointers_Citizen_406bd82a(x_12);
        v_3 = x_13
      };
      Citizen_table_1dce1ae8.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def printAllPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      val cursor_0 = Person_table_33938670.cursorOpen();
      val x_1 = Person_table_33938670.last(cursor_0);
      val x_2 = empty_pointers_Person_266726fd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Person: (", ", ", ", ", ", ", ")");
            val x_7 = get_salary_226b1e28(row_5);
            val x_8 = get_name_407aa3ba(row_5);
            val x_9 = get_string_3b579574(x_8);
            val x_10 = get_age_10ed72dc(row_5);
            val x_11 = get_job_23d915b8(row_5);
            val x_12 = x_6.s(x_7, x_9, x_10, x_11);
            scala.Predef.println(x_12);
            true
          })
      }) {
        val x_13 = Person_table_33938670.prev(cursor_0);
        val x_14 = empty_pointers_Person_266726fd(x_13);
        v_3 = x_14
      };
      Person_table_33938670.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def queryInsideMap: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            var res_7: scala.Int = 0;
            val cursor_8 = Person_table_33938670.cursorOpen();
            val x_9 = Person_table_33938670.last(cursor_8);
            val x_10 = empty_pointers_Person_266726fd(x_9);
            var v_11: Person = x_10;
            while ({
              val x_12 = v_11;
              x_12
                .!=(null)
                .&&({
                  val row_13 = v_11;
                  val x_14 = res_7;
                  res_7 = x_14.+(1);
                  true
                })
            }) {
              val x_15 = Person_table_33938670.prev(cursor_8);
              val x_16 = empty_pointers_Person_266726fd(x_15);
              v_11 = x_16
            };
            Person_table_33938670.cursorClose(cursor_8);
            val x_17 = res_7;
            val x_18 = res_0;
            res_0 = x_17.+(x_18);
            true
          })
      }) {
        val x_19 = Person_table_33938670.prev(cursor_1);
        val x_20 = empty_pointers_Person_266726fd(x_19);
        v_4 = x_20
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_21 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_21
    }
  }
  def queryWhile: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var i_0: scala.Int = 0;
      val x_1 = toCString_f0ca54a("Test name");
      val x_2 = init_Str_6addfded(x_1);
      val x_3 = toCString_f0ca54a("Google");
      val x_4 = init_Str_6addfded(x_3);
      val job_5 = init_Job_453d68f0(scala.Tuple2(20, x_4));
      while ({
        val x_6 = i_0;
        x_6.<(5)
      }) {
        val p_7 = init_Person_541a1274(scala.Tuple4(10, x_2, 20, job_5));
        val x_8 = i_0;
        i_0 = x_8.+(1)
      };
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose()
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_226b1e28(row_6);
            val x_8 = get_name_407aa3ba(row_6);
            val x_9 = get_name_407aa3ba(row_6);
            val x_10 = strlen_5e80d026(x_9);
            val x_11 = get_job_23d915b8(row_6);
            val x_12 = x_10.asInstanceOf[scala.Long].toInt;
            val x_13 = init_Person_541a1274(scala.Tuple4(x_7, x_8, x_12, x_11));
            val x_14 = res_0;
            res_0 = x_14.+(1);
            true
          })
      }) {
        val x_15 = Person_table_33938670.prev(cursor_1);
        val x_16 = empty_pointers_Person_266726fd(x_15);
        v_4 = x_16
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_62f8eaa7.dbiOpen();
      Person_table_33938670.dbiOpen();
      Job_table_78a82b2d.dbiOpen();
      Citizen_table_1dce1ae8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_33938670.cursorOpen();
      val x_2 = Person_table_33938670.last(cursor_1);
      val x_3 = empty_pointers_Person_266726fd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_age_10ed72dc(row_6);
            res_0 = x_8.+(x_7);
            true
          })
      }) {
        val x_9 = Person_table_33938670.prev(cursor_1);
        val x_10 = empty_pointers_Person_266726fd(x_9);
        v_4 = x_10
      };
      Person_table_33938670.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_62f8eaa7.dbiClose();
      Person_table_33938670.dbiClose();
      Job_table_78a82b2d.dbiClose();
      Citizen_table_1dce1ae8.dbiClose();
      x_11
    }
  }
}

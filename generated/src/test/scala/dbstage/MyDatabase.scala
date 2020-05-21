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
  lazy val Citizen_table_6fff9271 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_178ca23d = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_3dfc8080 = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_34cc2fc2 = new LMDBTable[CString]("Str")

  def toCString_27b35f30(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def empty_pointers_Citizen_64edfca4(value: Citizen): Citizen = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_55655d8c(value: Job): Job = {
    if (value != null) {
      value._4 = null
    }
    value
  }
  def empty_pointers_Person_360b7494(value: Person): Person = {
    if (value != null) {
      value._4 = null
      value._7 = null
    }
    value
  }

  def get_Citizen_3c88591f(key: Long)(implicit zone: Zone): Citizen = {
    val value = Citizen_table_6fff9271.get(key)
    empty_pointers_Citizen_64edfca4(value)
  }
  def get_Job_370b9af6(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_178ca23d.get(key)
    empty_pointers_Job_55655d8c(value)
  }
  def get_Person_487d28a6(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_3dfc8080.get(key)
    empty_pointers_Person_360b7494(value)
  }

  def get_Str_25bf4ae9(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_34cc2fc2.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_5b01bfe0(data_el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_6fff9271.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_30b6425c(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_178ca23d.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_11af64f4(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_3dfc8080.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_4e188c2a(str: Str)(implicit zone: Zone): Unit = {
    Str_table_34cc2fc2.put(str._1, strlen(str._2) + 1, str._2)
  }

  def init_Citizen_64b9d8b(
      params: Tuple3[Str, Long, Int]
  )(implicit zone: Zone): Citizen = {
    val new_val = alloc[CitizenData]
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._1 = params._2
    new_val._4 = params._3
    put_Citizen_5b01bfe0(new_val)
    get_Citizen_3c88591f(new_val._1)
  }
  def init_Job_5da91b5f(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = Job_table_178ca23d.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_30b6425c(new_val)
    get_Job_370b9af6(new_val._1)
  }
  def init_Person_3aea8411(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = alloc[PersonData]
    new_val._1 = Person_table_3dfc8080.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_11af64f4(new_val)
    get_Person_487d28a6(new_val._1)
  }
  def init_Str_6858d488(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_34cc2fc2.getNextKey
    new_val._2 = param
    put_Str_4e188c2a(new_val)
    get_Str_25bf4ae9(new_val._1)
  }

  def isMinor_c2c0e50(this_240cdc0f: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_5599fa99(this_240cdc0f);
    x_0.<(18)
  }
  def showKey_706a03d5(this_3b35329a: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_61325e43(this_3b35329a);
    x_0.toString()
  }

  def charAt_11de99e9(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_158dbf1c(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_a4542a(this_3b35329a: Citizen)(implicit zone: Zone): Int = {
    this_3b35329a._4
  }
  def get_age_5599fa99(this_240cdc0f: Person)(implicit zone: Zone): Int = {
    this_240cdc0f._5
  }
  def get_enterprise_7ed0c5b8(this_4c7f4cf6: Job)(implicit zone: Zone): Str = {
    if (this_4c7f4cf6._4 == null) {
      this_4c7f4cf6._4 = get_Str_25bf4ae9(this_4c7f4cf6._3)
    }
    this_4c7f4cf6._4
  }
  def get_job_68cd8165(this_240cdc0f: Person)(implicit zone: Zone): Job = {
    if (this_240cdc0f._7 == null) {
      this_240cdc0f._7 = get_Job_370b9af6(this_240cdc0f._6)
    }
    this_240cdc0f._7
  }
  def get_key_61325e43(this_3b35329a: Citizen)(implicit zone: Zone): Long = {
    this_3b35329a._1
  }
  def get_name_44b99c37(this_3b35329a: Citizen)(implicit zone: Zone): Str = {
    if (this_3b35329a._3 == null) {
      this_3b35329a._3 = get_Str_25bf4ae9(this_3b35329a._2)
    }
    this_3b35329a._3
  }
  def get_name_721770aa(this_240cdc0f: Person)(implicit zone: Zone): Str = {
    if (this_240cdc0f._4 == null) {
      this_240cdc0f._4 = get_Str_25bf4ae9(this_240cdc0f._3)
    }
    this_240cdc0f._4
  }
  def get_salary_a129655(this_240cdc0f: Person)(implicit zone: Zone): Int = {
    this_240cdc0f._2
  }
  def get_size_ed0f8db(this_4c7f4cf6: Job)(implicit zone: Zone): Int = {
    this_4c7f4cf6._2
  }
  def get_string_6008512(this_4fdb2758: Str)(implicit zone: Zone): CString = {
    this_4fdb2758._2
  }

  def set_age_30b8bd4e(this_3b35329a: Citizen, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_3b35329a._4 = age
  }
  def set_age_63f140b2(this_240cdc0f: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_240cdc0f._5 = age
  }
  def set_enterprise_12186389(this_4c7f4cf6: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_4c7f4cf6._3 = enterprise._1
    this_4c7f4cf6._4 = enterprise
  }
  def set_job_67bfee49(this_240cdc0f: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_240cdc0f._6 = job._1
    this_240cdc0f._7 = job
  }
  def set_name_2df6b459(this_240cdc0f: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_240cdc0f._3 = name._1
    this_240cdc0f._4 = name
  }
  def set_salary_446b9585(this_240cdc0f: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_240cdc0f._2 = salary
  }
  def set_size_153eb2aa(this_4c7f4cf6: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_4c7f4cf6._2 = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = isMinor_c2c0e50(row_6);
            if (x_7.asInstanceOf[scala.Boolean]) {
              val x_8 = res_0;
              res_0 = x_8.+(1);
              true
            } else
              true
          })
      }) {
        val x_9 = Person_table_3dfc8080.next(cursor_1);
        val x_10 = empty_pointers_Person_360b7494(x_9);
        v_4 = x_10
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_a129655(row_6);
            val x_8 = get_name_721770aa(row_6);
            val x_9 = get_age_5599fa99(row_6);
            val x_10 = get_job_68cd8165(row_6);
            val x_11 =
              init_Person_3aea8411(scala.Tuple4(x_7, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_3dfc8080.next(cursor_1);
        val x_14 = empty_pointers_Person_360b7494(x_13);
        v_4 = x_14
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_age_5599fa99(row_6);
            set_age_63f140b2(row_6, x_7.+(10));
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_3dfc8080.next(cursor_1);
        val x_10 = empty_pointers_Person_360b7494(x_9);
        v_4 = x_10
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = toCString_27b35f30("Test");
            val x_8 = init_Str_6858d488(x_7);
            val x_9 = get_age_5599fa99(row_6);
            val x_10 = get_job_68cd8165(row_6);
            val x_11 =
              init_Person_3aea8411(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_3dfc8080.next(cursor_1);
        val x_14 = empty_pointers_Person_360b7494(x_13);
        v_4 = x_14
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
              val row_6 = v_4;
              val x_7 = get_salary_a129655(row_6);
              val x_8 = get_name_721770aa(row_6);
              val x_9 = charAt_11de99e9(scala.Tuple2(x_8, 2L));
              val x_10 =
                toCString_27b35f30(x_9.asInstanceOf[scala.Byte].toString());
              val x_11 = init_Str_6858d488(x_10);
              val x_12 = get_age_5599fa99(row_6);
              val x_13 = get_job_68cd8165(row_6);
              val x_14 =
                init_Person_3aea8411(scala.Tuple4(x_7, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_3dfc8080.next(cursor_1);
        val x_17 = empty_pointers_Person_360b7494(x_16);
        v_4 = x_17
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_18
    }
  }
  def insertionAtKey: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      val x_0 = toCString_27b35f30("Bob");
      val x_1 = init_Str_6858d488(x_0);
      init_Citizen_64b9d8b(scala.Tuple3(x_1, 42L, 12));
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose()
    }
  }
  def insertionAtKey2: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      val x_0 = toCString_27b35f30("Alice");
      val x_1 = init_Str_6858d488(x_0);
      init_Citizen_64b9d8b(scala.Tuple3(x_1, 42L, 100));
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      val x_0 = toCString_27b35f30("EPFL");
      val x_1 = init_Str_6858d488(x_0);
      val x_2 = init_Job_5da91b5f(scala.Tuple2(10000, x_1));
      val x_3 = toCString_27b35f30("Lucien");
      val x_4 = init_Str_6858d488(x_3);
      val x_5 = init_Person_3aea8411(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_27b35f30("John");
      val x_7 = init_Str_6858d488(x_6);
      val x_8 = init_Person_3aea8411(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Person_table_3dfc8080.cursorOpen();
            val x_8 = Person_table_3dfc8080.first(cursor_7);
            val x_9 = empty_pointers_Person_360b7494(x_8);
            var v_10 = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 =
                    get_age_5599fa99(((scala.Tuple2(row1_6, row2_12)))._1);
                  val x_14 =
                    get_age_5599fa99(((scala.Tuple2(row1_6, row2_12)))._2);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.intWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Person_table_3dfc8080.next(cursor_7);
              val x_20 = empty_pointers_Person_360b7494(x_19);
              v_10 = x_20
            };
            Person_table_3dfc8080.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Person_table_3dfc8080.next(cursor_1);
        val x_22 = empty_pointers_Person_360b7494(x_21);
        v_4 = x_22
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val row_7 = get_job_68cd8165(row_6);
            val row_8 = get_size_ed0f8db(row_7);
            val row_9 = row_8.+(100);
            val x_10 = res_0;
            res_0 = x_10.+(1);
            true
          })
      }) {
        val x_11 = Person_table_3dfc8080.next(cursor_1);
        val x_12 = empty_pointers_Person_360b7494(x_11);
        v_4 = x_12
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      val cursor_0 = Person_table_3dfc8080.cursorOpen();
      val x_1 = Person_table_3dfc8080.first(cursor_0);
      val x_2 = empty_pointers_Person_360b7494(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_5599fa99(row_5);
            scala.Predef.println(x_6);
            true
          })
      }) {
        val x_7 = Person_table_3dfc8080.next(cursor_0);
        val x_8 = empty_pointers_Person_360b7494(x_7);
        v_3 = x_8
      };
      Person_table_3dfc8080.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose()
    }
  }
  def printAllCitizens: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      val cursor_0 = Citizen_table_6fff9271.cursorOpen();
      val x_1 = Citizen_table_6fff9271.first(cursor_0);
      val x_2 = empty_pointers_Citizen_64edfca4(x_1);
      var v_3: Citizen = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Citizen: (", ", ", ", ", ")");
            val x_7 = get_name_44b99c37(row_5);
            val x_8 = get_string_6008512(x_7);
            val x_9 = get_key_61325e43(row_5);
            val x_10 = get_age_a4542a(row_5);
            val x_11 = x_6.s(x_8, x_9, x_10);
            scala.Predef.println(x_11);
            true
          })
      }) {
        val x_12 = Citizen_table_6fff9271.next(cursor_0);
        val x_13 = empty_pointers_Citizen_64edfca4(x_12);
        v_3 = x_13
      };
      Citizen_table_6fff9271.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose()
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_a129655(row_6);
            val x_8 = get_name_721770aa(row_6);
            val x_9 = get_name_721770aa(row_6);
            val x_10 = strlen_158dbf1c(x_9);
            val x_11 = get_job_68cd8165(row_6);
            val x_12 = x_10.asInstanceOf[scala.Long].toInt;
            val x_13 = init_Person_3aea8411(scala.Tuple4(x_7, x_8, x_12, x_11));
            val x_14 = res_0;
            res_0 = x_14.+(1);
            true
          })
      }) {
        val x_15 = Person_table_3dfc8080.next(cursor_1);
        val x_16 = empty_pointers_Person_360b7494(x_15);
        v_4 = x_16
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_34cc2fc2.dbiOpen();
      Person_table_3dfc8080.dbiOpen();
      Job_table_178ca23d.dbiOpen();
      Citizen_table_6fff9271.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3dfc8080.cursorOpen();
      val x_2 = Person_table_3dfc8080.first(cursor_1);
      val x_3 = empty_pointers_Person_360b7494(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_age_5599fa99(row_6);
            res_0 = x_8.+(x_7);
            true
          })
      }) {
        val x_9 = Person_table_3dfc8080.next(cursor_1);
        val x_10 = empty_pointers_Person_360b7494(x_9);
        v_4 = x_10
      };
      Person_table_3dfc8080.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_34cc2fc2.dbiClose();
      Person_table_3dfc8080.dbiClose();
      Job_table_178ca23d.dbiClose();
      Citizen_table_6fff9271.dbiClose();
      x_11
    }
  }
}

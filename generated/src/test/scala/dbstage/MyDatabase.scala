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
  lazy val Citizen_table_5288a960 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_67ed213f = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_4247d528 = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_512cd58a = new LMDBTable[CString]("Str")

  def toCString_46f8a566(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_4812805f(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Citizen_5cabcfc1(value: Citizen): Citizen = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_6f8f2156(value: Job): Job = {
    if (value != null) {
      value._4 = null
    }
    value
  }
  def empty_pointers_Person_538f9951(value: Person): Person = {
    if (value != null) {
      value._4 = null
      value._7 = null
    }
    value
  }

  def get_Citizen_6e6abcbd(key: Long)(implicit zone: Zone): Citizen = {
    val value = Citizen_table_5288a960.get(key)
    empty_pointers_Citizen_5cabcfc1(value)
  }
  def get_Job_6c3da376(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_67ed213f.get(key)
    empty_pointers_Job_6f8f2156(value)
  }
  def get_Person_7a2eaa36(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_4247d528.get(key)
    empty_pointers_Person_538f9951(value)
  }

  def get_Str_55c4f133(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_512cd58a.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_6e703062(data_el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_5288a960.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_6f5718c9(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_67ed213f.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_6027600f(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_4247d528.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_12985851(str: Str)(implicit zone: Zone): Unit = {
    Str_table_512cd58a.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Citizen_713e7d36(el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_5288a960.delete(el._1)
  }
  def delete_Job_21b4e990(el: Job)(implicit zone: Zone): Unit = {
    Job_table_67ed213f.delete(el._1)
  }
  def delete_Person_78a32a6f(el: Person)(implicit zone: Zone): Unit = {
    Person_table_4247d528.delete(el._1)
  }
  def delete_Str_4062538d(el: Str)(implicit zone: Zone): Unit = {
    Str_table_512cd58a.delete(el._1)
  }

  def init_Citizen_31b2fae7(
      params: Tuple3[Str, Long, Int]
  )(implicit zone: Zone): Citizen = {
    val new_val = alloc[CitizenData]
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._1 = params._2
    new_val._4 = params._3
    put_Citizen_6e703062(new_val)
    get_Citizen_6e6abcbd(new_val._1)
  }
  def init_Job_76aedf64(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = Job_table_67ed213f.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_6f5718c9(new_val)
    get_Job_6c3da376(new_val._1)
  }
  def init_Person_76780285(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = alloc[PersonData]
    new_val._1 = Person_table_4247d528.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_6027600f(new_val)
    get_Person_7a2eaa36(new_val._1)
  }
  def init_Str_42035c99(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_512cd58a.getNextKey
    new_val._2 = param
    put_Str_12985851(new_val)
    get_Str_55c4f133(new_val._1)
  }

  def isMinor_52678d0b(this_134969df: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_30105fe6(this_134969df);
    x_0.<(18)
  }
  def showKey_7e97bcbd(this_49de9273: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_3e90167f(this_49de9273);
    x_0.toString()
  }

  def charAt_5b3da77b(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_48b2effb(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_6b009671(this_49de9273: Citizen)(implicit zone: Zone): Int = {
    this_49de9273._4
  }
  def get_age_30105fe6(this_134969df: Person)(implicit zone: Zone): Int = {
    this_134969df._5
  }
  def get_enterprise_1872e88f(this_1074ea16: Job)(implicit zone: Zone): Str = {
    if (this_1074ea16._4 == null) {
      this_1074ea16._4 = get_Str_55c4f133(this_1074ea16._3)
    }
    this_1074ea16._4
  }
  def get_job_ac48c60(this_134969df: Person)(implicit zone: Zone): Job = {
    if (this_134969df._7 == null) {
      this_134969df._7 = get_Job_6c3da376(this_134969df._6)
    }
    this_134969df._7
  }
  def get_key_3e90167f(this_49de9273: Citizen)(implicit zone: Zone): Long = {
    this_49de9273._1
  }
  def get_name_65183057(this_49de9273: Citizen)(implicit zone: Zone): Str = {
    if (this_49de9273._3 == null) {
      this_49de9273._3 = get_Str_55c4f133(this_49de9273._2)
    }
    this_49de9273._3
  }
  def get_name_528b52b8(this_134969df: Person)(implicit zone: Zone): Str = {
    if (this_134969df._4 == null) {
      this_134969df._4 = get_Str_55c4f133(this_134969df._3)
    }
    this_134969df._4
  }
  def get_salary_5d32fef6(this_134969df: Person)(implicit zone: Zone): Int = {
    this_134969df._2
  }
  def get_size_733bcebc(this_1074ea16: Job)(implicit zone: Zone): Int = {
    this_1074ea16._2
  }
  def get_string_5f9354de(this_5785d204: Str)(implicit zone: Zone): String = {
    fromCString_4812805f(this_5785d204._2)
  }

  def set_age_77280368(this_49de9273: Citizen, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_49de9273._4 = age
  }
  def set_age_69dd5898(this_134969df: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_134969df._5 = age
  }
  def set_enterprise_73dcb148(this_1074ea16: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_1074ea16._3 = enterprise._1
    this_1074ea16._4 = enterprise
  }
  def set_job_4458df18(this_134969df: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_134969df._6 = job._1
    this_134969df._7 = job
  }
  def set_name_48bec45c(this_134969df: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_134969df._3 = name._1
    this_134969df._4 = name
  }
  def set_salary_4ba45c4b(this_134969df: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_134969df._2 = salary
  }
  def set_size_5de4e286(this_1074ea16: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1074ea16._2 = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = isMinor_52678d0b(row_6);
            if (x_7.asInstanceOf[scala.Boolean]) {
              val x_8 = res_0;
              res_0 = x_8.+(1);
              true
            } else
              true
          })
      }) {
        val x_9 = Person_table_4247d528.prev(cursor_1);
        val x_10 = empty_pointers_Person_538f9951(x_9);
        v_4 = x_10
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_5d32fef6(row_6);
            val x_8 = get_name_528b52b8(row_6);
            val x_9 = get_age_30105fe6(row_6);
            val x_10 = get_job_ac48c60(row_6);
            val x_11 =
              init_Person_76780285(scala.Tuple4(x_7, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_4247d528.prev(cursor_1);
        val x_14 = empty_pointers_Person_538f9951(x_13);
        v_4 = x_14
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_age_30105fe6(row_6);
            set_age_69dd5898(row_6, x_7.+(10));
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_4247d528.prev(cursor_1);
        val x_10 = empty_pointers_Person_538f9951(x_9);
        v_4 = x_10
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = toCString_46f8a566("Test");
            val x_8 = init_Str_42035c99(x_7);
            val x_9 = get_age_30105fe6(row_6);
            val x_10 = get_job_ac48c60(row_6);
            val x_11 =
              init_Person_76780285(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_4247d528.prev(cursor_1);
        val x_14 = empty_pointers_Person_538f9951(x_13);
        v_4 = x_14
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
              val row_6 = v_4;
              val x_7 = get_salary_5d32fef6(row_6);
              val x_8 = get_name_528b52b8(row_6);
              val x_9 = charAt_5b3da77b(scala.Tuple2(x_8, 2L));
              val x_10 =
                toCString_46f8a566(x_9.asInstanceOf[scala.Byte].toString());
              val x_11 = init_Str_42035c99(x_10);
              val x_12 = get_age_30105fe6(row_6);
              val x_13 = get_job_ac48c60(row_6);
              val x_14 =
                init_Person_76780285(scala.Tuple4(x_7, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_4247d528.prev(cursor_1);
        val x_17 = empty_pointers_Person_538f9951(x_16);
        v_4 = x_17
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_18
    }
  }
  def deleteAllMajors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Person_table_4247d528.cursorOpen();
      val x_1 = Person_table_4247d528.last(cursor_0);
      val x_2 = empty_pointers_Person_538f9951(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_52678d0b(row_5);
            val x_7 = x_6.asInstanceOf[scala.Boolean].`unary_!`;
            if (x_7)
              delete_Person_78a32a6f(row_5)
            else
              ();
            true
          })
      }) {
        val x_8 = Person_table_4247d528.prev(cursor_0);
        val x_9 = empty_pointers_Person_538f9951(x_8);
        v_3 = x_9
      };
      Person_table_4247d528.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def deleteAllMinors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Person_table_4247d528.cursorOpen();
      val x_1 = Person_table_4247d528.last(cursor_0);
      val x_2 = empty_pointers_Person_538f9951(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_52678d0b(row_5);
            if (x_6.asInstanceOf[scala.Boolean])
              delete_Person_78a32a6f(row_5)
            else
              ();
            true
          })
      }) {
        val x_7 = Person_table_4247d528.prev(cursor_0);
        val x_8 = empty_pointers_Person_538f9951(x_7);
        v_3 = x_8
      };
      Person_table_4247d528.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def economicCrisis: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Job_table_67ed213f.cursorOpen();
      val x_1 = Job_table_67ed213f.last(cursor_0);
      val x_2 = empty_pointers_Job_6f8f2156(x_1);
      var v_3: Job = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            delete_Job_21b4e990(row_5);
            true
          })
      }) {
        val x_6 = Job_table_67ed213f.prev(cursor_0);
        val x_7 = empty_pointers_Job_6f8f2156(x_6);
        v_3 = x_7
      };
      Job_table_67ed213f.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def insertOldPeople: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Person_table_4247d528.cursorOpen();
      val x_1 = Person_table_4247d528.last(cursor_0);
      val x_2 = empty_pointers_Person_538f9951(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_5d32fef6(row_5);
            val x_7 = get_name_528b52b8(row_5);
            val x_8 = get_age_30105fe6(row_5);
            val x_9 = get_job_ac48c60(row_5);
            init_Person_76780285(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            true
          })
      }) {
        val x_10 = Person_table_4247d528.prev(cursor_0);
        val x_11 = empty_pointers_Person_538f9951(x_10);
        v_3 = x_11
      };
      Person_table_4247d528.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def insertionAtKey: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val x_0 = toCString_46f8a566("Bob");
      val x_1 = init_Str_42035c99(x_0);
      init_Citizen_31b2fae7(scala.Tuple3(x_1, 42L, 12));
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def insertionAtKey2: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val x_0 = toCString_46f8a566("Alice");
      val x_1 = init_Str_42035c99(x_0);
      init_Citizen_31b2fae7(scala.Tuple3(x_1, 42L, 100));
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val x_0 = toCString_46f8a566("EPFL");
      val x_1 = init_Str_42035c99(x_0);
      val x_2 = init_Job_76aedf64(scala.Tuple2(10000, x_1));
      val x_3 = toCString_46f8a566("Lucien");
      val x_4 = init_Str_42035c99(x_3);
      val x_5 = init_Person_76780285(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_46f8a566("John");
      val x_7 = init_Str_42035c99(x_6);
      val x_8 = init_Person_76780285(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = -2147483648;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Person_table_4247d528.cursorOpen();
            val x_8 = Person_table_4247d528.last(cursor_7);
            val x_9 = empty_pointers_Person_538f9951(x_8);
            var v_10: Person = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 = get_age_30105fe6(row1_6);
                  val x_14 = get_age_30105fe6(row2_12);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.intWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Person_table_4247d528.prev(cursor_7);
              val x_20 = empty_pointers_Person_538f9951(x_19);
              v_10 = x_20
            };
            Person_table_4247d528.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Person_table_4247d528.prev(cursor_1);
        val x_22 = empty_pointers_Person_538f9951(x_21);
        v_4 = x_22
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val row_7 = get_job_ac48c60(row_6);
            val row_8 = get_size_733bcebc(row_7);
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_4247d528.prev(cursor_1);
        val x_11 = empty_pointers_Person_538f9951(x_10);
        v_4 = x_11
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_12 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_12
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Person_table_4247d528.cursorOpen();
      val x_1 = Person_table_4247d528.last(cursor_0);
      val x_2 = empty_pointers_Person_538f9951(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_30105fe6(row_5);
            scala.Predef.println(x_6);
            true
          })
      }) {
        val x_7 = Person_table_4247d528.prev(cursor_0);
        val x_8 = empty_pointers_Person_538f9951(x_7);
        v_3 = x_8
      };
      Person_table_4247d528.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def printAllCitizens: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Citizen_table_5288a960.cursorOpen();
      val x_1 = Citizen_table_5288a960.last(cursor_0);
      val x_2 = empty_pointers_Citizen_5cabcfc1(x_1);
      var v_3: Citizen = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Citizen: (", ", ", ", ", ")");
            val x_7 = get_name_65183057(row_5);
            val x_8 = get_string_5f9354de(x_7);
            val x_9 = get_key_3e90167f(row_5);
            val x_10 = get_age_6b009671(row_5);
            val x_11 = x_6.s(x_8, x_9, x_10);
            scala.Predef.println(x_11);
            true
          })
      }) {
        val x_12 = Citizen_table_5288a960.prev(cursor_0);
        val x_13 = empty_pointers_Citizen_5cabcfc1(x_12);
        v_3 = x_13
      };
      Citizen_table_5288a960.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def printAllPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      val cursor_0 = Person_table_4247d528.cursorOpen();
      val x_1 = Person_table_4247d528.last(cursor_0);
      val x_2 = empty_pointers_Person_538f9951(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Person: (", ", ", ", ", ", ", ")");
            val x_7 = get_salary_5d32fef6(row_5);
            val x_8 = get_name_528b52b8(row_5);
            val x_9 = get_string_5f9354de(x_8);
            val x_10 = get_age_30105fe6(row_5);
            val x_11 = get_job_ac48c60(row_5);
            val x_12 = x_6.s(x_7, x_9, x_10, x_11);
            scala.Predef.println(x_12);
            true
          })
      }) {
        val x_13 = Person_table_4247d528.prev(cursor_0);
        val x_14 = empty_pointers_Person_538f9951(x_13);
        v_3 = x_14
      };
      Person_table_4247d528.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose()
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_5d32fef6(row_6);
            val x_8 = get_name_528b52b8(row_6);
            val x_9 = get_name_528b52b8(row_6);
            val x_10 = strlen_48b2effb(x_9);
            val x_11 = get_job_ac48c60(row_6);
            val x_12 = x_10.asInstanceOf[scala.Long].toInt;
            val x_13 = init_Person_76780285(scala.Tuple4(x_7, x_8, x_12, x_11));
            val x_14 = res_0;
            res_0 = x_14.+(1);
            true
          })
      }) {
        val x_15 = Person_table_4247d528.prev(cursor_1);
        val x_16 = empty_pointers_Person_538f9951(x_15);
        v_4 = x_16
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_512cd58a.dbiOpen();
      Person_table_4247d528.dbiOpen();
      Job_table_67ed213f.dbiOpen();
      Citizen_table_5288a960.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4247d528.cursorOpen();
      val x_2 = Person_table_4247d528.last(cursor_1);
      val x_3 = empty_pointers_Person_538f9951(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_age_30105fe6(row_6);
            res_0 = x_8.+(x_7);
            true
          })
      }) {
        val x_9 = Person_table_4247d528.prev(cursor_1);
        val x_10 = empty_pointers_Person_538f9951(x_9);
        v_4 = x_10
      };
      Person_table_4247d528.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_512cd58a.dbiClose();
      Person_table_4247d528.dbiClose();
      Job_table_67ed213f.dbiClose();
      Citizen_table_5288a960.dbiClose();
      x_11
    }
  }
}

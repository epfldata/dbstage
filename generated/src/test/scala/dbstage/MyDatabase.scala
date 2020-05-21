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
  lazy val Citizen_table_1a1636d2 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_3a9c4854 = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_4a96e947 = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_4f95b95 = new LMDBTable[CString]("Str")

  def toCString_2ff24d7e(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Citizen_77f47e3c(key: Long)(implicit zone: Zone): Citizen = {
    Citizen_table_1a1636d2.get(key)
  }
  def get_Job_a556d54(key: Long)(implicit zone: Zone): Job = {
    Job_table_3a9c4854.get(key)
  }
  def get_Person_443eaa78(key: Long)(implicit zone: Zone): Person = {
    Person_table_4a96e947.get(key)
  }

  def get_Str_791a963c(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_4f95b95.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_100f29fe(data_el: Citizen)(implicit zone: Zone): Unit = {
    val ptr3 = data_el._3
    data_el._3 = null

    Citizen_table_1a1636d2.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
    data_el._3 = ptr3

  }
  def put_Job_65248fa8(data_el: Job)(implicit zone: Zone): Unit = {
    val ptr4 = data_el._4
    data_el._4 = null

    Job_table_3a9c4854.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
    data_el._4 = ptr4

  }
  def put_Person_467ef71c(data_el: Person)(implicit zone: Zone): Unit = {
    val ptr4 = data_el._4
    data_el._4 = null
    val ptr7 = data_el._7
    data_el._7 = null

    Person_table_4a96e947.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
    data_el._4 = ptr4
    data_el._7 = ptr7

  }

  def put_Str_349d359d(str: Str)(implicit zone: Zone): Unit = {
    Str_table_4f95b95.put(str._1, strlen(str._2) + 1, str._2)
  }

  def init_Citizen_4f5d7c50(
      params: Tuple2[Str, Long]
  )(implicit zone: Zone): Citizen = {
    val key = Citizen_table_1a1636d2.getNextKey
    val new_val = alloc[CitizenData]
    new_val._1 = key
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._4 = params._2
    put_Citizen_100f29fe(new_val)
    get_Citizen_77f47e3c(key)
  }
  def init_Job_1301e07e(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val key = Job_table_3a9c4854.getNextKey
    val new_val = alloc[JobData]
    new_val._1 = key
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_65248fa8(new_val)
    get_Job_a556d54(key)
  }
  def init_Person_451590f4(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val key = Person_table_4a96e947.getNextKey
    val new_val = alloc[PersonData]
    new_val._1 = key
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_467ef71c(new_val)
    get_Person_443eaa78(key)
  }
  def init_Str_70bc5754(param: CString)(implicit zone: Zone): Str = {
    val key = Str_table_4f95b95.getNextKey
    val new_val = alloc[StrData]
    new_val._1 = key
    new_val._2 = param
    put_Str_349d359d(new_val)
    get_Str_791a963c(key)
  }

  def isMinor_687e43cc(this_33565c5d: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_e992a23(this_33565c5d);
    x_0.<(18)
  }
  def showKey_79e7a060(this_b9c8105: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_3bcf763e(this_b9c8105);
    x_0.toString()
  }

  def charAt_2c534d1b(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_5d5a59ae(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_e992a23(this_33565c5d: Person)(implicit zone: Zone): Int = {
    this_33565c5d._5
  }
  def get_enterprise_7219a65d(this_40cfebf1: Job)(implicit zone: Zone): Str = {
    if (this_40cfebf1._4 == null) {
      this_40cfebf1._4 = get_Str_791a963c(this_40cfebf1._3)
    }
    this_40cfebf1._4
  }
  def get_job_5b0462b7(this_33565c5d: Person)(implicit zone: Zone): Job = {
    if (this_33565c5d._7 == null) {
      this_33565c5d._7 = get_Job_a556d54(this_33565c5d._6)
    }
    this_33565c5d._7
  }
  def get_key_3bcf763e(this_b9c8105: Citizen)(implicit zone: Zone): Long = {
    this_b9c8105._4
  }
  def get_name_74e1b6de(this_b9c8105: Citizen)(implicit zone: Zone): Str = {
    if (this_b9c8105._3 == null) {
      this_b9c8105._3 = get_Str_791a963c(this_b9c8105._2)
    }
    this_b9c8105._3
  }
  def get_name_4b4e4bd0(this_33565c5d: Person)(implicit zone: Zone): Str = {
    if (this_33565c5d._4 == null) {
      this_33565c5d._4 = get_Str_791a963c(this_33565c5d._3)
    }
    this_33565c5d._4
  }
  def get_salary_74da27aa(this_33565c5d: Person)(implicit zone: Zone): Int = {
    this_33565c5d._2
  }
  def get_size_cb3f25c(this_40cfebf1: Job)(implicit zone: Zone): Int = {
    this_40cfebf1._2
  }
  def get_string_759df252(this_4cb437fa: Str)(implicit zone: Zone): CString = {
    this_4cb437fa._2
  }

  def set_age_51e8b69f(this_33565c5d: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_33565c5d._5 = age
    put_Person_467ef71c(this_33565c5d)
  }
  def set_enterprise_60df9996(this_40cfebf1: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_40cfebf1._3 = enterprise._1
    this_40cfebf1._4 = enterprise
    put_Job_65248fa8(this_40cfebf1)
  }
  def set_job_5548d5d1(this_33565c5d: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_33565c5d._6 = job._1
    this_33565c5d._7 = job
    put_Person_467ef71c(this_33565c5d)
  }
  def set_name_4180cda9(this_33565c5d: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_33565c5d._3 = name._1
    this_33565c5d._4 = name
    put_Person_467ef71c(this_33565c5d)
  }
  def set_salary_6d6bbcdc(this_33565c5d: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_33565c5d._2 = salary
    put_Person_467ef71c(this_33565c5d)
  }
  def set_size_71e48bce(this_40cfebf1: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_40cfebf1._2 = size
    put_Job_65248fa8(this_40cfebf1)
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_687e43cc(row_5);
            if (x_6.asInstanceOf[scala.Boolean]) {
              val x_7 = res_0;
              res_0 = x_7.+(1);
              true
            } else
              true
          })
      }) {
        val x_8 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_8
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_9
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_74da27aa(row_5);
            val x_7 = get_name_4b4e4bd0(row_5);
            val x_8 = get_age_e992a23(row_5);
            val x_9 = get_job_5b0462b7(row_5);
            val x_10 =
              init_Person_451590f4(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_12
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_13
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_e992a23(row_5);
            set_age_51e8b69f(row_5, x_6.+(10));
            val x_7 = res_0;
            res_0 = x_7.+(1);
            true
          })
      }) {
        val x_8 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_8
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_9
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = Str("Test");
            val x_7 = get_age_e992a23(row_5);
            val x_8 = get_job_5b0462b7(row_5);
            val x_9 =
              init_Person_451590f4(scala.Tuple4(0, x_6, x_7.+(100), x_8));
            val x_10 = res_0;
            res_0 = x_10.+(1);
            true
          })
      }) {
        val x_11 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_11
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_12 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_12
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_74da27aa(row_5);
            val x_7 = get_name_4b4e4bd0(row_5);
            val x_8 = charAt_2c534d1b(scala.Tuple2(x_7, 2L));
            val x_9 = Str(x_8.asInstanceOf[scala.Byte].toString());
            val x_10 = get_age_e992a23(row_5);
            val x_11 = get_job_5b0462b7(row_5);
            val x_12 = init_Person_451590f4(scala.Tuple4(x_6, x_9, x_10, x_11));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_14
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_15
    }
  }
  def insertionAtKey: Citizen = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      val x_0 = Str("Bob");
      val x_1 = init_Citizen_4f5d7c50(scala.Tuple2(x_0, 42L));
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      val x_0 = Str("EPFL");
      val x_1 = init_Job_1301e07e(scala.Tuple2(10000, x_0));
      val x_2 = Str("Lucien");
      val x_3 = init_Person_451590f4(scala.Tuple4(1000, x_2, 21, x_1));
      val x_4 = Str("John");
      val x_5 = init_Person_451590f4(scala.Tuple4(100, x_4, 16, x_1));
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row1_5 = v_3;
            val cursor_6 = Person_table_4a96e947.cursorOpen();
            val x_7 = Person_table_4a96e947.first(cursor_6);
            var v_8: scala.Any = x_7;
            while ({
              val x_9 = v_8;
              x_9
                .!=(null)
                .&&({
                  val row2_10 = v_8;
                  val x_11 = get_age_e992a23(
                    ((scala.Tuple2(row1_5, row2_10)): scala.Any)._1
                  );
                  val x_12 = get_age_e992a23(
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
              val x_17 = Person_table_4a96e947.next(cursor_6);
              v_8 = x_17
            };
            Person_table_4a96e947.cursorClose(cursor_6);
            true
          })
      }) {
        val x_18 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_18
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_19 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_19
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val row_6 = get_job_5b0462b7(row_5);
            val row_7 = get_size_cb3f25c(row_6);
            val row_8 = row_7.+(100);
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_10
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_11
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      val cursor_0 = Person_table_4a96e947.cursorOpen();
      val x_1 = Person_table_4a96e947.first(cursor_0);
      var v_2: Person = x_1;
      while ({
        val x_3 = v_2;
        x_3
          .!=(null)
          .&&({
            val row_4 = v_2;
            val x_5 = get_age_e992a23(row_4);
            scala.Predef.println(x_5);
            true
          })
      }) {
        val x_6 = Person_table_4a96e947.next(cursor_0);
        v_2 = x_6
      };
      Person_table_4a96e947.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_3a9c4854.cursorOpen();
      val x_2 = Job_table_3a9c4854.first(cursor_1);
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
        val x_7 = Job_table_3a9c4854.next(cursor_1);
        v_3 = x_7
      };
      Job_table_3a9c4854.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_8
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
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
        val x_7 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_7
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_8
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_4f95b95.cursorOpen();
      val x_2 = Str_table_4f95b95.first(cursor_1);
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
        val x_7 = Str_table_4f95b95.next(cursor_1);
        v_3 = x_7
      };
      Str_table_4f95b95.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_8
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_74da27aa(row_5);
            val x_7 = get_name_4b4e4bd0(row_5);
            val x_8 = get_name_4b4e4bd0(row_5);
            val x_9 = strlen_5d5a59ae(x_8);
            val x_10 = get_job_5b0462b7(row_5);
            val x_11 = x_9.asInstanceOf[scala.Long].toInt;
            val x_12 = init_Person_451590f4(scala.Tuple4(x_6, x_7, x_11, x_10));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_14
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_15
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4f95b95.dbiOpen();
      Person_table_4a96e947.dbiOpen();
      Job_table_3a9c4854.dbiOpen();
      Citizen_table_1a1636d2.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_4a96e947.cursorOpen();
      val x_2 = Person_table_4a96e947.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = res_0;
            val x_7 = get_age_e992a23(row_5);
            res_0 = x_7.+(x_6);
            true
          })
      }) {
        val x_8 = Person_table_4a96e947.next(cursor_1);
        v_3 = x_8
      };
      Person_table_4a96e947.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_4f95b95.dbiClose();
      Person_table_4a96e947.dbiClose();
      Job_table_3a9c4854.dbiClose();
      Citizen_table_1a1636d2.dbiClose();
      x_9
    }
  }
}

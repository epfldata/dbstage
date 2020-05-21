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

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_30a5862 = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_7ff1c63e = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_13e8736f = new LMDBTable[CString]("Str")

  def toCString_2c81235d(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def get_Job_7d99b7e7(key: Long)(implicit zone: Zone): Job = {
    Job_table_30a5862.get(key)
  }
  def get_Person_ca84e5c(key: Long)(implicit zone: Zone): Person = {
    Person_table_7ff1c63e.get(key)
  }

  def get_Str_5c8b27ad(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_13e8736f.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Job_2045e26(data_el: Job)(implicit zone: Zone): Unit = {
    val ptr4 = data_el._4
    data_el._4 = null

    Job_table_30a5862.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
    data_el._4 = ptr4

  }
  def put_Person_5006a2f0(data_el: Person)(implicit zone: Zone): Unit = {
    val ptr4 = data_el._4
    data_el._4 = null
    val ptr7 = data_el._7
    data_el._7 = null

    Person_table_7ff1c63e.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
    data_el._4 = ptr4
    data_el._7 = ptr7

  }

  def put_Str_7a5b66e(str: Str)(implicit zone: Zone): Unit = {
    Str_table_13e8736f.put(str._1, strlen(str._2) + 1, str._2)
  }

  def init_Job_12bf7507(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val key = Job_table_30a5862.getNextKey
    val new_val = alloc[JobData]
    new_val._1 = key
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_2045e26(new_val)
    get_Job_7d99b7e7(key)
  }
  def init_Person_724f4ff0(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val key = Person_table_7ff1c63e.getNextKey
    val new_val = alloc[PersonData]
    new_val._1 = key
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_5006a2f0(new_val)
    get_Person_ca84e5c(key)
  }
  def init_Str_3c96b2e1(param: CString)(implicit zone: Zone): Str = {
    val key = Str_table_13e8736f.getNextKey
    val new_val = alloc[StrData]
    new_val._1 = key
    new_val._2 = param
    put_Str_7a5b66e(new_val)
    get_Str_5c8b27ad(key)
  }

  def isMinor_6cc684ed(this_2c547893: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_4dd7278e(this_2c547893);
    x_0.<(18)
  }

  def charAt_449f8a52(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_1dce79aa(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_4dd7278e(this_2c547893: Person)(implicit zone: Zone): Int = {
    this_2c547893._5
  }
  def get_enterprise_65dc9d00(this_17b79f41: Job)(implicit zone: Zone): Str = {
    if (this_17b79f41._4 == null) {
      this_17b79f41._4 = get_Str_5c8b27ad(this_17b79f41._3)
    }
    this_17b79f41._4
  }
  def get_job_cde223a(this_2c547893: Person)(implicit zone: Zone): Job = {
    if (this_2c547893._7 == null) {
      this_2c547893._7 = get_Job_7d99b7e7(this_2c547893._6)
    }
    this_2c547893._7
  }
  def get_name_26cfb4c9(this_2c547893: Person)(implicit zone: Zone): Str = {
    if (this_2c547893._4 == null) {
      this_2c547893._4 = get_Str_5c8b27ad(this_2c547893._3)
    }
    this_2c547893._4
  }
  def get_salary_da97ac6(this_2c547893: Person)(implicit zone: Zone): Int = {
    this_2c547893._2
  }
  def get_size_c9cc2ee(this_17b79f41: Job)(implicit zone: Zone): Int = {
    this_17b79f41._2
  }
  def get_string_71aeeeb5(this_2a1c75b5: Str)(implicit zone: Zone): CString = {
    this_2a1c75b5._2
  }

  def set_age_f291221(this_2c547893: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2c547893._5 = age
    put_Person_5006a2f0(this_2c547893)
  }
  def set_enterprise_65f767f2(this_17b79f41: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_17b79f41._3 = enterprise._1
    this_17b79f41._4 = enterprise
    put_Job_2045e26(this_17b79f41)
  }
  def set_job_20549dbe(this_2c547893: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_2c547893._6 = job._1
    this_2c547893._7 = job
    put_Person_5006a2f0(this_2c547893)
  }
  def set_name_67ab2b8f(this_2c547893: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2c547893._3 = name._1
    this_2c547893._4 = name
    put_Person_5006a2f0(this_2c547893)
  }
  def set_salary_60b5bd6e(this_2c547893: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2c547893._2 = salary
    put_Person_5006a2f0(this_2c547893)
  }
  def set_size_3e239e7d(this_17b79f41: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_17b79f41._2 = size
    put_Job_2045e26(this_17b79f41)
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_6cc684ed(row_5);
            if (x_6.asInstanceOf[scala.Boolean]) {
              val x_7 = res_0;
              res_0 = x_7.+(1);
              true
            } else
              true
          })
      }) {
        val x_8 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_8
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_9
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_da97ac6(row_5);
            val x_7 = get_name_26cfb4c9(row_5);
            val x_8 = get_age_4dd7278e(row_5);
            val x_9 = get_job_cde223a(row_5);
            val x_10 =
              init_Person_724f4ff0(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_12
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_13
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_4dd7278e(row_5);
            set_age_f291221(row_5, x_6.+(10));
            val x_7 = res_0;
            res_0 = x_7.+(1);
            true
          })
      }) {
        val x_8 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_8
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_9
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = toCString_2c81235d("Test");
            val x_7 = init_Str_3c96b2e1(x_6);
            val x_8 = get_age_4dd7278e(row_5);
            val x_9 = get_job_cde223a(row_5);
            val x_10 =
              init_Person_724f4ff0(scala.Tuple4(0, x_7, x_8.+(100), x_9));
            val x_11 = res_0;
            res_0 = x_11.+(1);
            true
          })
      }) {
        val x_12 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_12
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_13
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
              val row_5 = v_3;
              val x_6 = get_salary_da97ac6(row_5);
              val x_7 = get_name_26cfb4c9(row_5);
              val x_8 = charAt_449f8a52(scala.Tuple2(x_7, 2L));
              val x_9 =
                toCString_2c81235d(x_8.asInstanceOf[scala.Byte].toString());
              val x_10 = init_Str_3c96b2e1(x_9);
              val x_11 = get_age_4dd7278e(row_5);
              val x_12 = get_job_cde223a(row_5);
              val x_13 =
                init_Person_724f4ff0(scala.Tuple4(x_6, x_10, x_11, x_12));
              val x_14 = res_0;
              res_0 = x_14.+(1);
              true
            }
          )
      }) {
        val x_15 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_15
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_16 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_16
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      val x_0 = toCString_2c81235d("EPFL");
      val x_1 = init_Str_3c96b2e1(x_0);
      val x_2 = init_Job_12bf7507(scala.Tuple2(10000, x_1));
      val x_3 = toCString_2c81235d("Lucien");
      val x_4 = init_Str_3c96b2e1(x_3);
      val x_5 = init_Person_724f4ff0(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_2c81235d("John");
      val x_7 = init_Str_3c96b2e1(x_6);
      val x_8 = init_Person_724f4ff0(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row1_5 = v_3;
            val cursor_6 = Person_table_7ff1c63e.cursorOpen();
            val x_7 = Person_table_7ff1c63e.first(cursor_6);
            var v_8: scala.Any = x_7;
            while ({
              val x_9 = v_8;
              x_9
                .!=(null)
                .&&({
                  val row2_10 = v_8;
                  val x_11 = get_age_4dd7278e(
                    ((scala.Tuple2(row1_5, row2_10)): scala.Any)._1
                  );
                  val x_12 = get_age_4dd7278e(
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
              val x_17 = Person_table_7ff1c63e.next(cursor_6);
              v_8 = x_17
            };
            Person_table_7ff1c63e.cursorClose(cursor_6);
            true
          })
      }) {
        val x_18 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_18
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_19 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_19
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val row_6 = get_job_cde223a(row_5);
            val row_7 = get_size_c9cc2ee(row_6);
            val row_8 = row_7.+(100);
            val x_9 = res_0;
            res_0 = x_9.+(1);
            true
          })
      }) {
        val x_10 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_10
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_11
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      val cursor_0 = Person_table_7ff1c63e.cursorOpen();
      val x_1 = Person_table_7ff1c63e.first(cursor_0);
      var v_2: Person = x_1;
      while ({
        val x_3 = v_2;
        x_3
          .!=(null)
          .&&({
            val row_4 = v_2;
            val x_5 = get_age_4dd7278e(row_4);
            scala.Predef.println(x_5);
            true
          })
      }) {
        val x_6 = Person_table_7ff1c63e.next(cursor_0);
        v_2 = x_6
      };
      Person_table_7ff1c63e.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose()
    }
  }
  def sizeJobQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Job_table_30a5862.cursorOpen();
      val x_2 = Job_table_30a5862.first(cursor_1);
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
        val x_7 = Job_table_30a5862.next(cursor_1);
        v_3 = x_7
      };
      Job_table_30a5862.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_8
    }
  }
  def sizePersonQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
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
        val x_7 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_7
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_8
    }
  }
  def sizeStrQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Str_table_13e8736f.cursorOpen();
      val x_2 = Str_table_13e8736f.first(cursor_1);
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
        val x_7 = Str_table_13e8736f.next(cursor_1);
        v_3 = x_7
      };
      Str_table_13e8736f.cursorClose(cursor_1);
      val x_8 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_8
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: scala.Any = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_da97ac6(row_5);
            val x_7 = get_name_26cfb4c9(row_5);
            val x_8 = get_name_26cfb4c9(row_5);
            val x_9 = strlen_1dce79aa(x_8);
            val x_10 = get_job_cde223a(row_5);
            val x_11 = x_9.asInstanceOf[scala.Long].toInt;
            val x_12 = init_Person_724f4ff0(scala.Tuple4(x_6, x_7, x_11, x_10));
            val x_13 = res_0;
            res_0 = x_13.+(1);
            true
          })
      }) {
        val x_14 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_14
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_15
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_13e8736f.dbiOpen();
      Person_table_7ff1c63e.dbiOpen();
      Job_table_30a5862.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_7ff1c63e.cursorOpen();
      val x_2 = Person_table_7ff1c63e.first(cursor_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = res_0;
            val x_7 = get_age_4dd7278e(row_5);
            res_0 = x_7.+(x_6);
            true
          })
      }) {
        val x_8 = Person_table_7ff1c63e.next(cursor_1);
        v_3 = x_8
      };
      Person_table_7ff1c63e.cursorClose(cursor_1);
      val x_9 = res_0;
      LMDBTable.txnCommit();
      Str_table_13e8736f.dbiClose();
      Person_table_7ff1c63e.dbiClose();
      Job_table_30a5862.dbiClose();
      x_9
    }
  }
}

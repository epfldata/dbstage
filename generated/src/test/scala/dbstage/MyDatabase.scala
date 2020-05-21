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
  lazy val Citizen_table_73fc9146 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_2d37f98a = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_24c6b85f = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_7f7f78e9 = new LMDBTable[CString]("Str")

  def toCString_689eb0e1(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_7a77efd1(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Citizen_3796fd6a(value: Citizen): Citizen = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_49ae19bc(value: Job): Job = {
    if (value != null) {
      value._4 = null
    }
    value
  }
  def empty_pointers_Person_57e74e35(value: Person): Person = {
    if (value != null) {
      value._4 = null
      value._7 = null
    }
    value
  }

  def get_Citizen_71f7576b(key: Long)(implicit zone: Zone): Citizen = {
    val value = Citizen_table_73fc9146.get(key)
    empty_pointers_Citizen_3796fd6a(value)
  }
  def get_Job_710a9992(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_2d37f98a.get(key)
    empty_pointers_Job_49ae19bc(value)
  }
  def get_Person_48500df6(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_24c6b85f.get(key)
    empty_pointers_Person_57e74e35(value)
  }

  def get_Str_252b742f(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_7f7f78e9.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_6e2c23ee(data_el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_73fc9146.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_4bcff1b0(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_2d37f98a.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_3386a82(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_24c6b85f.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_7d4cecc6(str: Str)(implicit zone: Zone): Unit = {
    Str_table_7f7f78e9.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Citizen_6aaa075(el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_73fc9146.delete(el._1)
  }
  def delete_Job_1c1a56ba(el: Job)(implicit zone: Zone): Unit = {
    Job_table_2d37f98a.delete(el._1)
  }
  def delete_Person_6a8228e8(el: Person)(implicit zone: Zone): Unit = {
    Person_table_24c6b85f.delete(el._1)
  }
  def delete_Str_19059733(el: Str)(implicit zone: Zone): Unit = {
    Str_table_7f7f78e9.delete(el._1)
  }

  def init_Citizen_53c7f0c3(
      params: Tuple3[Str, Long, Int]
  )(implicit zone: Zone): Citizen = {
    val new_val = alloc[CitizenData]
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._1 = params._2
    new_val._4 = params._3
    put_Citizen_6e2c23ee(new_val)
    get_Citizen_71f7576b(new_val._1)
  }
  def init_Job_371992f3(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = Job_table_2d37f98a.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_4bcff1b0(new_val)
    get_Job_710a9992(new_val._1)
  }
  def init_Person_7c69b6a6(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = alloc[PersonData]
    new_val._1 = Person_table_24c6b85f.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_3386a82(new_val)
    get_Person_48500df6(new_val._1)
  }
  def init_Str_7b995fb(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_7f7f78e9.getNextKey
    new_val._2 = param
    put_Str_7d4cecc6(new_val)
    get_Str_252b742f(new_val._1)
  }

  def isMinor_6aec5aa6(this_1aad96a0: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_4e1e69bb(this_1aad96a0);
    x_0.<(18)
  }
  def showKey_42e5a9de(this_6cafb9a1: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_68a92ff1(this_6cafb9a1);
    x_0.toString()
  }

  def charAt_137ee4b0(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_2a8749f8(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_43584e70(this_6cafb9a1: Citizen)(implicit zone: Zone): Int = {
    this_6cafb9a1._4
  }
  def get_age_4e1e69bb(this_1aad96a0: Person)(implicit zone: Zone): Int = {
    this_1aad96a0._5
  }
  def get_enterprise_79df69f8(this_2c855e8e: Job)(implicit zone: Zone): Str = {
    if (this_2c855e8e._4 == null) {
      this_2c855e8e._4 = get_Str_252b742f(this_2c855e8e._3)
    }
    this_2c855e8e._4
  }
  def get_job_2cdd93bb(this_1aad96a0: Person)(implicit zone: Zone): Job = {
    if (this_1aad96a0._7 == null) {
      this_1aad96a0._7 = get_Job_710a9992(this_1aad96a0._6)
    }
    this_1aad96a0._7
  }
  def get_key_68a92ff1(this_6cafb9a1: Citizen)(implicit zone: Zone): Long = {
    this_6cafb9a1._1
  }
  def get_name_2d77739e(this_6cafb9a1: Citizen)(implicit zone: Zone): Str = {
    if (this_6cafb9a1._3 == null) {
      this_6cafb9a1._3 = get_Str_252b742f(this_6cafb9a1._2)
    }
    this_6cafb9a1._3
  }
  def get_name_2b803990(this_1aad96a0: Person)(implicit zone: Zone): Str = {
    if (this_1aad96a0._4 == null) {
      this_1aad96a0._4 = get_Str_252b742f(this_1aad96a0._3)
    }
    this_1aad96a0._4
  }
  def get_salary_291205b1(this_1aad96a0: Person)(implicit zone: Zone): Int = {
    this_1aad96a0._2
  }
  def get_size_69920b63(this_2c855e8e: Job)(implicit zone: Zone): Int = {
    this_2c855e8e._2
  }
  def get_string_55099488(this_7f6f7e4c: Str)(implicit zone: Zone): String = {
    fromCString_7a77efd1(this_7f6f7e4c._2)
  }

  def set_age_1d68fbfa(this_6cafb9a1: Citizen, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6cafb9a1._4 = age
  }
  def set_age_5536d977(this_1aad96a0: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1aad96a0._5 = age
  }
  def set_enterprise_4db70361(this_2c855e8e: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_2c855e8e._3 = enterprise._1
    this_2c855e8e._4 = enterprise
  }
  def set_job_175a8606(this_1aad96a0: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_1aad96a0._6 = job._1
    this_1aad96a0._7 = job
  }
  def set_name_76c67a96(this_1aad96a0: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_1aad96a0._3 = name._1
    this_1aad96a0._4 = name
  }
  def set_salary_15d8e858(this_1aad96a0: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_1aad96a0._2 = salary
  }
  def set_size_60015d89(this_2c855e8e: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2c855e8e._2 = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = isMinor_6aec5aa6(row_6);
            if (x_7.asInstanceOf[scala.Boolean]) {
              val x_8 = res_0;
              res_0 = x_8.+(1);
              true
            } else
              true
          })
      }) {
        val x_9 = Person_table_24c6b85f.next(cursor_1);
        val x_10 = empty_pointers_Person_57e74e35(x_9);
        v_4 = x_10
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_291205b1(row_6);
            val x_8 = get_name_2b803990(row_6);
            val x_9 = get_age_4e1e69bb(row_6);
            val x_10 = get_job_2cdd93bb(row_6);
            val x_11 =
              init_Person_7c69b6a6(scala.Tuple4(x_7, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_24c6b85f.next(cursor_1);
        val x_14 = empty_pointers_Person_57e74e35(x_13);
        v_4 = x_14
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_age_4e1e69bb(row_6);
            set_age_5536d977(row_6, x_7.+(10));
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_24c6b85f.next(cursor_1);
        val x_10 = empty_pointers_Person_57e74e35(x_9);
        v_4 = x_10
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = toCString_689eb0e1("Test");
            val x_8 = init_Str_7b995fb(x_7);
            val x_9 = get_age_4e1e69bb(row_6);
            val x_10 = get_job_2cdd93bb(row_6);
            val x_11 =
              init_Person_7c69b6a6(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_24c6b85f.next(cursor_1);
        val x_14 = empty_pointers_Person_57e74e35(x_13);
        v_4 = x_14
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
              val row_6 = v_4;
              val x_7 = get_salary_291205b1(row_6);
              val x_8 = get_name_2b803990(row_6);
              val x_9 = charAt_137ee4b0(scala.Tuple2(x_8, 2L));
              val x_10 =
                toCString_689eb0e1(x_9.asInstanceOf[scala.Byte].toString());
              val x_11 = init_Str_7b995fb(x_10);
              val x_12 = get_age_4e1e69bb(row_6);
              val x_13 = get_job_2cdd93bb(row_6);
              val x_14 =
                init_Person_7c69b6a6(scala.Tuple4(x_7, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_24c6b85f.next(cursor_1);
        val x_17 = empty_pointers_Person_57e74e35(x_16);
        v_4 = x_17
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_18
    }
  }
  def deleteAllMinors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val cursor_0 = Person_table_24c6b85f.cursorOpen();
      val x_1 = Person_table_24c6b85f.first(cursor_0);
      val x_2 = empty_pointers_Person_57e74e35(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_6aec5aa6(row_5);
            if (x_6.asInstanceOf[scala.Boolean])
              delete_Person_6a8228e8(row_5)
            else
              ();
            true
          })
      }) {
        val x_7 = Person_table_24c6b85f.next(cursor_0);
        val x_8 = empty_pointers_Person_57e74e35(x_7);
        v_3 = x_8
      };
      Person_table_24c6b85f.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def insertionAtKey: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val x_0 = toCString_689eb0e1("Bob");
      val x_1 = init_Str_7b995fb(x_0);
      init_Citizen_53c7f0c3(scala.Tuple3(x_1, 42L, 12));
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def insertionAtKey2: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val x_0 = toCString_689eb0e1("Alice");
      val x_1 = init_Str_7b995fb(x_0);
      init_Citizen_53c7f0c3(scala.Tuple3(x_1, 42L, 100));
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val x_0 = toCString_689eb0e1("EPFL");
      val x_1 = init_Str_7b995fb(x_0);
      val x_2 = init_Job_371992f3(scala.Tuple2(10000, x_1));
      val x_3 = toCString_689eb0e1("Lucien");
      val x_4 = init_Str_7b995fb(x_3);
      val x_5 = init_Person_7c69b6a6(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_689eb0e1("John");
      val x_7 = init_Str_7b995fb(x_6);
      val x_8 = init_Person_7c69b6a6(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Person_table_24c6b85f.cursorOpen();
            val x_8 = Person_table_24c6b85f.first(cursor_7);
            val x_9 = empty_pointers_Person_57e74e35(x_8);
            var v_10 = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 =
                    get_age_4e1e69bb(((scala.Tuple2(row1_6, row2_12)))._1);
                  val x_14 =
                    get_age_4e1e69bb(((scala.Tuple2(row1_6, row2_12)))._2);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.intWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Person_table_24c6b85f.next(cursor_7);
              val x_20 = empty_pointers_Person_57e74e35(x_19);
              v_10 = x_20
            };
            Person_table_24c6b85f.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Person_table_24c6b85f.next(cursor_1);
        val x_22 = empty_pointers_Person_57e74e35(x_21);
        v_4 = x_22
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val row_7 = get_job_2cdd93bb(row_6);
            val row_8 = get_size_69920b63(row_7);
            val row_9 = row_8.+(100);
            val x_10 = res_0;
            res_0 = x_10.+(1);
            true
          })
      }) {
        val x_11 = Person_table_24c6b85f.next(cursor_1);
        val x_12 = empty_pointers_Person_57e74e35(x_11);
        v_4 = x_12
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val cursor_0 = Person_table_24c6b85f.cursorOpen();
      val x_1 = Person_table_24c6b85f.first(cursor_0);
      val x_2 = empty_pointers_Person_57e74e35(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_4e1e69bb(row_5);
            scala.Predef.println(x_6);
            true
          })
      }) {
        val x_7 = Person_table_24c6b85f.next(cursor_0);
        val x_8 = empty_pointers_Person_57e74e35(x_7);
        v_3 = x_8
      };
      Person_table_24c6b85f.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def printAllCitizens: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val cursor_0 = Citizen_table_73fc9146.cursorOpen();
      val x_1 = Citizen_table_73fc9146.first(cursor_0);
      val x_2 = empty_pointers_Citizen_3796fd6a(x_1);
      var v_3: Citizen = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Citizen: (", ", ", ", ", ")");
            val x_7 = get_name_2d77739e(row_5);
            val x_8 = get_string_55099488(x_7);
            val x_9 = get_key_68a92ff1(row_5);
            val x_10 = get_age_43584e70(row_5);
            val x_11 = x_6.s(x_8, x_9, x_10);
            scala.Predef.println(x_11);
            true
          })
      }) {
        val x_12 = Citizen_table_73fc9146.next(cursor_0);
        val x_13 = empty_pointers_Citizen_3796fd6a(x_12);
        v_3 = x_13
      };
      Citizen_table_73fc9146.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def printAllPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      val cursor_0 = Person_table_24c6b85f.cursorOpen();
      val x_1 = Person_table_24c6b85f.first(cursor_0);
      val x_2 = empty_pointers_Person_57e74e35(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Person: (", ", ", ", ", ", ", ")");
            val x_7 = get_salary_291205b1(row_5);
            val x_8 = get_name_2b803990(row_5);
            val x_9 = get_string_55099488(x_8);
            val x_10 = get_age_4e1e69bb(row_5);
            val x_11 = get_job_2cdd93bb(row_5);
            val x_12 = x_6.s(x_7, x_9, x_10, x_11);
            scala.Predef.println(x_12);
            true
          })
      }) {
        val x_13 = Person_table_24c6b85f.next(cursor_0);
        val x_14 = empty_pointers_Person_57e74e35(x_13);
        v_3 = x_14
      };
      Person_table_24c6b85f.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose()
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_291205b1(row_6);
            val x_8 = get_name_2b803990(row_6);
            val x_9 = get_name_2b803990(row_6);
            val x_10 = strlen_2a8749f8(x_9);
            val x_11 = get_job_2cdd93bb(row_6);
            val x_12 = x_10.asInstanceOf[scala.Long].toInt;
            val x_13 = init_Person_7c69b6a6(scala.Tuple4(x_7, x_8, x_12, x_11));
            val x_14 = res_0;
            res_0 = x_14.+(1);
            true
          })
      }) {
        val x_15 = Person_table_24c6b85f.next(cursor_1);
        val x_16 = empty_pointers_Person_57e74e35(x_15);
        v_4 = x_16
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_7f7f78e9.dbiOpen();
      Person_table_24c6b85f.dbiOpen();
      Job_table_2d37f98a.dbiOpen();
      Citizen_table_73fc9146.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_24c6b85f.cursorOpen();
      val x_2 = Person_table_24c6b85f.first(cursor_1);
      val x_3 = empty_pointers_Person_57e74e35(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_age_4e1e69bb(row_6);
            res_0 = x_8.+(x_7);
            true
          })
      }) {
        val x_9 = Person_table_24c6b85f.next(cursor_1);
        val x_10 = empty_pointers_Person_57e74e35(x_9);
        v_4 = x_10
      };
      Person_table_24c6b85f.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_7f7f78e9.dbiClose();
      Person_table_24c6b85f.dbiClose();
      Job_table_2d37f98a.dbiClose();
      Citizen_table_73fc9146.dbiClose();
      x_11
    }
  }
}

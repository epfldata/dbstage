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
  lazy val Citizen_table_35525eb8 = new LMDBTable[Citizen]("Citizen")

  type JobData = CStruct4[Long, Int, Long, Str]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_91892d0 = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Int, Long, Str, Int, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_3918d5f3 = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_741fa527 = new LMDBTable[CString]("Str")

  def toCString_539f9282(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_1edc0433(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Citizen_398ca795(value: Citizen): Citizen = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_7fb493af(value: Job): Job = {
    if (value != null) {
      value._4 = null
    }
    value
  }
  def empty_pointers_Person_4dfbdbbd(value: Person): Person = {
    if (value != null) {
      value._4 = null
      value._7 = null
    }
    value
  }

  def get_Citizen_74bb3ddf(key: Long)(implicit zone: Zone): Citizen = {
    val value = Citizen_table_35525eb8.get(key)
    empty_pointers_Citizen_398ca795(value)
  }
  def get_Job_28d78cd2(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_91892d0.get(key)
    empty_pointers_Job_7fb493af(value)
  }
  def get_Person_73a55b8c(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_3918d5f3.get(key)
    empty_pointers_Person_4dfbdbbd(value)
  }

  def get_Str_60905076(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_741fa527.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Citizen_4d68dc7e(data_el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_35525eb8.put(
      data_el._1,
      sizeCitizen,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_59cd781d(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_91892d0.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_6d3193(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_3918d5f3.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_621cd208(str: Str)(implicit zone: Zone): Unit = {
    Str_table_741fa527.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Citizen_32abbae9(el: Citizen)(implicit zone: Zone): Unit = {
    Citizen_table_35525eb8.delete(el._1)
  }
  def delete_Job_3623a226(el: Job)(implicit zone: Zone): Unit = {
    Job_table_91892d0.delete(el._1)
  }
  def delete_Person_3cfdd15a(el: Person)(implicit zone: Zone): Unit = {
    Person_table_3918d5f3.delete(el._1)
  }
  def delete_Str_78d7e9b3(el: Str)(implicit zone: Zone): Unit = {
    Str_table_741fa527.delete(el._1)
  }

  def init_Citizen_7e12fd9(
      params: Tuple3[Str, Long, Int]
  )(implicit zone: Zone): Citizen = {
    val new_val = alloc[CitizenData]
    new_val._2 = params._1._1
    new_val._3 = params._1
    new_val._1 = params._2
    new_val._4 = params._3
    put_Citizen_4d68dc7e(new_val)
    get_Citizen_74bb3ddf(new_val._1)
  }
  def init_Job_34cf9296(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = Job_table_91892d0.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    put_Job_59cd781d(new_val)
    get_Job_28d78cd2(new_val._1)
  }
  def init_Person_3d8d04fe(
      params: Tuple4[Int, Str, Int, Job]
  )(implicit zone: Zone): Person = {
    val new_val = alloc[PersonData]
    new_val._1 = Person_table_3918d5f3.getNextKey
    new_val._2 = params._1
    new_val._3 = params._2._1
    new_val._4 = params._2
    new_val._5 = params._3
    new_val._6 = params._4._1
    new_val._7 = params._4
    put_Person_6d3193(new_val)
    get_Person_73a55b8c(new_val._1)
  }
  def init_Str_421a7cae(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_741fa527.getNextKey
    new_val._2 = param
    put_Str_621cd208(new_val)
    get_Str_60905076(new_val._1)
  }

  def isMinor_2ee6ac9c(this_71e6c1cf: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_3b1b8f3a(this_71e6c1cf);
    x_0.<(18)
  }
  def showKey_6892973c(this_28e4e183: Citizen)(implicit zone: Zone): String = {
    val x_0 = get_key_1ea34fbe(this_28e4e183);
    x_0.toString()
  }

  def charAt_a8337c9(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strlen_3f72b764(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_74c98492(this_28e4e183: Citizen)(implicit zone: Zone): Int = {
    this_28e4e183._4
  }
  def get_age_3b1b8f3a(this_71e6c1cf: Person)(implicit zone: Zone): Int = {
    this_71e6c1cf._5
  }
  def get_enterprise_26d73d2c(this_6cacbc4f: Job)(implicit zone: Zone): Str = {
    if (this_6cacbc4f._4 == null) {
      this_6cacbc4f._4 = get_Str_60905076(this_6cacbc4f._3)
    }
    this_6cacbc4f._4
  }
  def get_job_611bd107(this_71e6c1cf: Person)(implicit zone: Zone): Job = {
    if (this_71e6c1cf._7 == null) {
      this_71e6c1cf._7 = get_Job_28d78cd2(this_71e6c1cf._6)
    }
    this_71e6c1cf._7
  }
  def get_key_1ea34fbe(this_28e4e183: Citizen)(implicit zone: Zone): Long = {
    this_28e4e183._1
  }
  def get_name_59ce34a2(this_28e4e183: Citizen)(implicit zone: Zone): Str = {
    if (this_28e4e183._3 == null) {
      this_28e4e183._3 = get_Str_60905076(this_28e4e183._2)
    }
    this_28e4e183._3
  }
  def get_name_5089a510(this_71e6c1cf: Person)(implicit zone: Zone): Str = {
    if (this_71e6c1cf._4 == null) {
      this_71e6c1cf._4 = get_Str_60905076(this_71e6c1cf._3)
    }
    this_71e6c1cf._4
  }
  def get_salary_4f28dc70(this_71e6c1cf: Person)(implicit zone: Zone): Int = {
    this_71e6c1cf._2
  }
  def get_size_7084af7b(this_6cacbc4f: Job)(implicit zone: Zone): Int = {
    this_6cacbc4f._2
  }
  def get_string_457b71d0(this_5847eeed: Str)(implicit zone: Zone): String = {
    fromCString_1edc0433(this_5847eeed._2)
  }

  def set_age_63e77048(this_28e4e183: Citizen, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_28e4e183._4 = age
  }
  def set_age_39c0e0a1(this_71e6c1cf: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_71e6c1cf._5 = age
  }
  def set_enterprise_277a52e0(this_6cacbc4f: Job, enterprise: Str)(
      implicit zone: Zone
  ): Unit = {
    this_6cacbc4f._3 = enterprise._1
    this_6cacbc4f._4 = enterprise
  }
  def set_job_7fc71377(this_71e6c1cf: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_71e6c1cf._6 = job._1
    this_71e6c1cf._7 = job
  }
  def set_name_5f99d4ea(this_71e6c1cf: Person, name: Str)(
      implicit zone: Zone
  ): Unit = {
    this_71e6c1cf._3 = name._1
    this_71e6c1cf._4 = name
  }
  def set_salary_61e2f250(this_71e6c1cf: Person, salary: Int)(
      implicit zone: Zone
  ): Unit = {
    this_71e6c1cf._2 = salary
  }
  def set_size_147a51de(this_6cacbc4f: Job, size: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6cacbc4f._2 = size
  }

  def allMinors: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = isMinor_2ee6ac9c(row_6);
            if (x_7.asInstanceOf[scala.Boolean]) {
              val x_8 = res_0;
              res_0 = x_8.+(1);
              true
            } else
              true
          })
      }) {
        val x_9 = Person_table_3918d5f3.prev(cursor_1);
        val x_10 = empty_pointers_Person_4dfbdbbd(x_9);
        v_4 = x_10
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_11
    }
  }
  def allOld: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_4f28dc70(row_6);
            val x_8 = get_name_5089a510(row_6);
            val x_9 = get_age_3b1b8f3a(row_6);
            val x_10 = get_job_611bd107(row_6);
            val x_11 =
              init_Person_3d8d04fe(scala.Tuple4(x_7, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_3918d5f3.prev(cursor_1);
        val x_14 = empty_pointers_Person_4dfbdbbd(x_13);
        v_4 = x_14
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_15
    }
  }
  def allOld2: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_age_3b1b8f3a(row_6);
            set_age_39c0e0a1(row_6, x_7.+(10));
            val x_8 = res_0;
            res_0 = x_8.+(1);
            true
          })
      }) {
        val x_9 = Person_table_3918d5f3.prev(cursor_1);
        val x_10 = empty_pointers_Person_4dfbdbbd(x_9);
        v_4 = x_10
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_11
    }
  }
  def allOld3: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = toCString_539f9282("Test");
            val x_8 = init_Str_421a7cae(x_7);
            val x_9 = get_age_3b1b8f3a(row_6);
            val x_10 = get_job_611bd107(row_6);
            val x_11 =
              init_Person_3d8d04fe(scala.Tuple4(0, x_8, x_9.+(100), x_10));
            val x_12 = res_0;
            res_0 = x_12.+(1);
            true
          })
      }) {
        val x_13 = Person_table_3918d5f3.prev(cursor_1);
        val x_14 = empty_pointers_Person_4dfbdbbd(x_13);
        v_4 = x_14
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_15 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_15
    }
  }
  def charAtQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
              val row_6 = v_4;
              val x_7 = get_salary_4f28dc70(row_6);
              val x_8 = get_name_5089a510(row_6);
              val x_9 = charAt_a8337c9(scala.Tuple2(x_8, 2L));
              val x_10 =
                toCString_539f9282(x_9.asInstanceOf[scala.Byte].toString());
              val x_11 = init_Str_421a7cae(x_10);
              val x_12 = get_age_3b1b8f3a(row_6);
              val x_13 = get_job_611bd107(row_6);
              val x_14 =
                init_Person_3d8d04fe(scala.Tuple4(x_7, x_11, x_12, x_13));
              val x_15 = res_0;
              res_0 = x_15.+(1);
              true
            }
          )
      }) {
        val x_16 = Person_table_3918d5f3.prev(cursor_1);
        val x_17 = empty_pointers_Person_4dfbdbbd(x_16);
        v_4 = x_17
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_18 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_18
    }
  }
  def deleteAllMajors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val cursor_0 = Person_table_3918d5f3.cursorOpen();
      val x_1 = Person_table_3918d5f3.last(cursor_0);
      val x_2 = empty_pointers_Person_4dfbdbbd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_2ee6ac9c(row_5);
            val x_7 = x_6.asInstanceOf[scala.Boolean].`unary_!`;
            if (x_7)
              delete_Person_3cfdd15a(row_5)
            else
              ();
            true
          })
      }) {
        val x_8 = Person_table_3918d5f3.prev(cursor_0);
        val x_9 = empty_pointers_Person_4dfbdbbd(x_8);
        v_3 = x_9
      };
      Person_table_3918d5f3.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def deleteAllMinors: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val cursor_0 = Person_table_3918d5f3.cursorOpen();
      val x_1 = Person_table_3918d5f3.last(cursor_0);
      val x_2 = empty_pointers_Person_4dfbdbbd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = isMinor_2ee6ac9c(row_5);
            if (x_6.asInstanceOf[scala.Boolean])
              delete_Person_3cfdd15a(row_5)
            else
              ();
            true
          })
      }) {
        val x_7 = Person_table_3918d5f3.prev(cursor_0);
        val x_8 = empty_pointers_Person_4dfbdbbd(x_7);
        v_3 = x_8
      };
      Person_table_3918d5f3.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def insertOldPeople: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val cursor_0 = Person_table_3918d5f3.cursorOpen();
      val x_1 = Person_table_3918d5f3.last(cursor_0);
      val x_2 = empty_pointers_Person_4dfbdbbd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_salary_4f28dc70(row_5);
            val x_7 = get_name_5089a510(row_5);
            val x_8 = get_age_3b1b8f3a(row_5);
            val x_9 = get_job_611bd107(row_5);
            init_Person_3d8d04fe(scala.Tuple4(x_6, x_7, x_8.+(100), x_9));
            true
          })
      }) {
        val x_10 = Person_table_3918d5f3.prev(cursor_0);
        val x_11 = empty_pointers_Person_4dfbdbbd(x_10);
        v_3 = x_11
      };
      Person_table_3918d5f3.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def insertionAtKey: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val x_0 = toCString_539f9282("Bob");
      val x_1 = init_Str_421a7cae(x_0);
      init_Citizen_7e12fd9(scala.Tuple3(x_1, 42L, 12));
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def insertionAtKey2: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val x_0 = toCString_539f9282("Alice");
      val x_1 = init_Str_421a7cae(x_0);
      init_Citizen_7e12fd9(scala.Tuple3(x_1, 42L, 100));
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def insertions: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val x_0 = toCString_539f9282("EPFL");
      val x_1 = init_Str_421a7cae(x_0);
      val x_2 = init_Job_34cf9296(scala.Tuple2(10000, x_1));
      val x_3 = toCString_539f9282("Lucien");
      val x_4 = init_Str_421a7cae(x_3);
      val x_5 = init_Person_3d8d04fe(scala.Tuple4(1000, x_4, 21, x_2));
      val x_6 = toCString_539f9282("John");
      val x_7 = init_Str_421a7cae(x_6);
      val x_8 = init_Person_3d8d04fe(scala.Tuple4(100, x_7, 16, x_2));
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def joinQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 2147483647;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row1_6 = v_4;
            val cursor_7 = Person_table_3918d5f3.cursorOpen();
            val x_8 = Person_table_3918d5f3.last(cursor_7);
            val x_9 = empty_pointers_Person_4dfbdbbd(x_8);
            var v_10 = x_9;
            while ({
              val x_11 = v_10;
              x_11
                .!=(null)
                .&&({
                  val row2_12 = v_10;
                  val x_13 =
                    get_age_3b1b8f3a(((scala.Tuple2(row1_6, row2_12)))._1);
                  val x_14 =
                    get_age_3b1b8f3a(((scala.Tuple2(row1_6, row2_12)))._2);
                  val x_15 = scala.math.`package`.abs(x_13.-(x_14));
                  val x_16 = res_0;
                  val x_17 = scala.Predef.intWrapper(x_15);
                  val x_18 = x_17.max(x_16);
                  res_0 = x_18;
                  true
                })
            }) {
              val x_19 = Person_table_3918d5f3.prev(cursor_7);
              val x_20 = empty_pointers_Person_4dfbdbbd(x_19);
              v_10 = x_20
            };
            Person_table_3918d5f3.cursorClose(cursor_7);
            true
          })
      }) {
        val x_21 = Person_table_3918d5f3.prev(cursor_1);
        val x_22 = empty_pointers_Person_4dfbdbbd(x_21);
        v_4 = x_22
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_23 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_23
    }
  }
  def mapQuery: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val row_7 = get_job_611bd107(row_6);
            val row_8 = get_size_7084af7b(row_7);
            val row_9 = row_8.+(100);
            val x_10 = res_0;
            res_0 = x_10.+(1);
            true
          })
      }) {
        val x_11 = Person_table_3918d5f3.prev(cursor_1);
        val x_12 = empty_pointers_Person_4dfbdbbd(x_11);
        v_4 = x_12
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_13 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_13
    }
  }
  def printAllAges: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val cursor_0 = Person_table_3918d5f3.cursorOpen();
      val x_1 = Person_table_3918d5f3.last(cursor_0);
      val x_2 = empty_pointers_Person_4dfbdbbd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_age_3b1b8f3a(row_5);
            scala.Predef.println(x_6);
            true
          })
      }) {
        val x_7 = Person_table_3918d5f3.prev(cursor_0);
        val x_8 = empty_pointers_Person_4dfbdbbd(x_7);
        v_3 = x_8
      };
      Person_table_3918d5f3.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def printAllCitizens: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val cursor_0 = Citizen_table_35525eb8.cursorOpen();
      val x_1 = Citizen_table_35525eb8.last(cursor_0);
      val x_2 = empty_pointers_Citizen_398ca795(x_1);
      var v_3: Citizen = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Citizen: (", ", ", ", ", ")");
            val x_7 = get_name_59ce34a2(row_5);
            val x_8 = get_string_457b71d0(x_7);
            val x_9 = get_key_1ea34fbe(row_5);
            val x_10 = get_age_74c98492(row_5);
            val x_11 = x_6.s(x_8, x_9, x_10);
            scala.Predef.println(x_11);
            true
          })
      }) {
        val x_12 = Citizen_table_35525eb8.prev(cursor_0);
        val x_13 = empty_pointers_Citizen_398ca795(x_12);
        v_3 = x_13
      };
      Citizen_table_35525eb8.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def printAllPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      val cursor_0 = Person_table_3918d5f3.cursorOpen();
      val x_1 = Person_table_3918d5f3.last(cursor_0);
      val x_2 = empty_pointers_Person_4dfbdbbd(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("Person: (", ", ", ", ", ", ", ")");
            val x_7 = get_salary_4f28dc70(row_5);
            val x_8 = get_name_5089a510(row_5);
            val x_9 = get_string_457b71d0(x_8);
            val x_10 = get_age_3b1b8f3a(row_5);
            val x_11 = get_job_611bd107(row_5);
            val x_12 = x_6.s(x_7, x_9, x_10, x_11);
            scala.Predef.println(x_12);
            true
          })
      }) {
        val x_13 = Person_table_3918d5f3.prev(cursor_0);
        val x_14 = empty_pointers_Person_4dfbdbbd(x_13);
        v_3 = x_14
      };
      Person_table_3918d5f3.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose()
    }
  }
  def sizes: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4 = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = get_salary_4f28dc70(row_6);
            val x_8 = get_name_5089a510(row_6);
            val x_9 = get_name_5089a510(row_6);
            val x_10 = strlen_3f72b764(x_9);
            val x_11 = get_job_611bd107(row_6);
            val x_12 = x_10.asInstanceOf[scala.Long].toInt;
            val x_13 = init_Person_3d8d04fe(scala.Tuple4(x_7, x_8, x_12, x_11));
            val x_14 = res_0;
            res_0 = x_14.+(1);
            true
          })
      }) {
        val x_15 = Person_table_3918d5f3.prev(cursor_1);
        val x_16 = empty_pointers_Person_4dfbdbbd(x_15);
        v_4 = x_16
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_17 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_17
    }
  }
  def sumAges: Int = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_741fa527.dbiOpen();
      Person_table_3918d5f3.dbiOpen();
      Job_table_91892d0.dbiOpen();
      Citizen_table_35525eb8.dbiOpen();
      var res_0: scala.Int = 0;
      val cursor_1 = Person_table_3918d5f3.cursorOpen();
      val x_2 = Person_table_3918d5f3.last(cursor_1);
      val x_3 = empty_pointers_Person_4dfbdbbd(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_age_3b1b8f3a(row_6);
            res_0 = x_8.+(x_7);
            true
          })
      }) {
        val x_9 = Person_table_3918d5f3.prev(cursor_1);
        val x_10 = empty_pointers_Person_4dfbdbbd(x_9);
        v_4 = x_10
      };
      Person_table_3918d5f3.cursorClose(cursor_1);
      val x_11 = res_0;
      LMDBTable.txnCommit();
      Str_table_741fa527.dbiClose();
      Person_table_3918d5f3.dbiClose();
      Job_table_91892d0.dbiClose();
      Citizen_table_35525eb8.dbiClose();
      x_11
    }
  }
}

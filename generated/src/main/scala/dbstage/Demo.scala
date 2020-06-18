package main

import scala.collection.mutable
import scala.scalanative.unsafe._
import lib.string._
import lib.str._
import lib.LMDBTable
import lib.LMDBTable._

object Demo {
  val sizeInt = sizeof[Int].toInt
  val sizeLong = sizeof[Long].toInt
  val sizeDouble = sizeof[Double].toInt

  type CompanyData = CStruct3[Long, Long, Str]
  type Company = Ptr[CompanyData]
  val sizeCompany = sizeof[CompanyData]
  lazy val Company_table_5a23800e = new LMDBTable[Company]("Company")

  type JobData = CStruct5[Long, Long, Str, Long, Company]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_6d5e3661 = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Long, Str, Int, Double, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_42e11bff = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_44998cfa = new LMDBTable[CString]("Str")

  def toCString_27c4797(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_1e0d9487(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Company_4e744424(value: Company): Company = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_50c17c3a(value: Job): Job = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Person_73fe802b(value: Person): Person = {
    if (value != null) {
      value._3 = null
      value._7 = null
    }
    value
  }

  def get_Company_35a383c8(key: Long)(implicit zone: Zone): Company = {
    val value = Company_table_5a23800e.get(key)
    empty_pointers_Company_4e744424(value)
  }
  def get_Job_599ab43c(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_6d5e3661.get(key)
    empty_pointers_Job_50c17c3a(value)
  }
  def get_Person_2613399(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_42e11bff.get(key)
    empty_pointers_Person_73fe802b(value)
  }

  def get_Str_5190426(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_44998cfa.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Company_2c8d7a8e(data_el: Company)(implicit zone: Zone): Unit = {
    Company_table_5a23800e.put(
      data_el._1,
      sizeCompany,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_203a1077(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_6d5e3661.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_2b4c0212(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_42e11bff.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_66b90b69(str: Str)(implicit zone: Zone): Unit = {
    Str_table_44998cfa.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Company_1a79957c(el: Company)(implicit zone: Zone): Unit = {
    Company_table_5a23800e.delete(el._1)
  }
  def delete_Job_34b52719(el: Job)(implicit zone: Zone): Unit = {
    Job_table_6d5e3661.delete(el._1)
  }
  def delete_Person_2fa82321(el: Person)(implicit zone: Zone): Unit = {
    Person_table_42e11bff.delete(el._1)
  }
  def delete_Str_c6b8fcc(el: Str)(implicit zone: Zone): Unit = {
    Str_table_44998cfa.delete(el._1)
  }

  def init_Company_c6a3b96(
      params: Tuple2[Long, Str]
  )(implicit zone: Zone): Company = {
    val new_val = alloc[CompanyData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    put_Company_2c8d7a8e(new_val)
    get_Company_35a383c8(new_val._1)
  }
  def init_Job_94e2d0(
      params: Tuple3[Long, Str, Company]
  )(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    put_Job_203a1077(new_val)
    get_Job_599ab43c(new_val._1)
  }
  def init_Person_e580a58(
      params: Tuple5[Long, Str, Int, Double, Job]
  )(implicit zone: Zone): Person = {
    val new_val = alloc[PersonData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3
    new_val._5 = params._4
    new_val._6 = params._5._1
    new_val._7 = params._5
    put_Person_2b4c0212(new_val)
    get_Person_2613399(new_val._1)
  }
  def init_Str_63078b4(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_44998cfa.getNextKey
    new_val._2 = param
    put_Str_66b90b69(new_val)
    get_Str_5190426(new_val._1)
  }

  def isMinor_2548d07c(this_2ce93004: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_55fb25bb(this_2ce93004);
    x_0.<(18)
  }

  def charAt_3297e88e(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strcmp_30eaa229(params: Tuple2[Str, CString])(implicit zone: Zone): Int =
    strcmp(params._1._2, params._2)
  def strlen_383876f1(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_55fb25bb(this_2ce93004: Person)(implicit zone: Zone): Int = {
    this_2ce93004._4
  }
  def get_company_4bfab7c7(this_5b18e13c: Job)(implicit zone: Zone): Company = {
    if (this_5b18e13c._5 == null) {
      this_5b18e13c._5 = get_Company_35a383c8(this_5b18e13c._4)
    }
    this_5b18e13c._5
  }
  def get_job_7c5bd76f(this_2ce93004: Person)(implicit zone: Zone): Job = {
    if (this_2ce93004._7 == null) {
      this_2ce93004._7 = get_Job_599ab43c(this_2ce93004._6)
    }
    this_2ce93004._7
  }
  def get_jobName_247a7734(this_5b18e13c: Job)(implicit zone: Zone): Str = {
    if (this_5b18e13c._3 == null) {
      this_5b18e13c._3 = get_Str_5190426(this_5b18e13c._2)
    }
    this_5b18e13c._3
  }
  def get_key_26adbfd0(this_38f77386: Company)(implicit zone: Zone): Long = {
    this_38f77386._1
  }
  def get_name_46ff4c5(this_38f77386: Company)(implicit zone: Zone): Str = {
    if (this_38f77386._3 == null) {
      this_38f77386._3 = get_Str_5190426(this_38f77386._2)
    }
    this_38f77386._3
  }
  def get_name_73dbe0ab(this_2ce93004: Person)(implicit zone: Zone): Str = {
    if (this_2ce93004._3 == null) {
      this_2ce93004._3 = get_Str_5190426(this_2ce93004._2)
    }
    this_2ce93004._3
  }
  def get_salary_7033c615(
      this_2ce93004: Person
  )(implicit zone: Zone): Double = {
    this_2ce93004._5
  }
  def get_string_4099b680(this_3b2bb4fc: Str)(implicit zone: Zone): String = {
    fromCString_1e0d9487(this_3b2bb4fc._2)
  }

  def set_age_5b518d44(this_2ce93004: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_2ce93004._4 = age
  }
  def set_job_6b4c3e5a(this_2ce93004: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_2ce93004._6 = job._1
    this_2ce93004._7 = job
  }
  def set_salary_65b71959(this_2ce93004: Person, salary: Double)(
      implicit zone: Zone
  ): Unit = {
    this_2ce93004._5 = salary
  }

  def computeBiggestSalaryDifferenceCommunityManager: (String, String, Double) =
    Zone { implicit zone =>
      {
        LMDBTable.txnBegin();
        Str_table_44998cfa.dbiOpen();
        Person_table_42e11bff.dbiOpen();
        Job_table_6d5e3661.dbiOpen();
        Company_table_5a23800e.dbiOpen();
        var res_0
            : scala.Tuple3[java.lang.String, java.lang.String, scala.Double] =
          scala.Tuple3("", "", scala.Double.MinValue);
        val cursor_1 = Person_table_42e11bff.cursorOpen();
        val x_2 = Person_table_42e11bff.last(cursor_1);
        val x_3 = empty_pointers_Person_73fe802b(x_2);
        var v_4: Person = x_3;
        while ({
          val x_5 = v_4;
          x_5
            .!=(null)
            .&&({
              val row_6 = v_4;
              val x_7 = get_job_7c5bd76f(row_6);
              val x_8 = get_jobName_247a7734(x_7);
              val x_9 = toCString("Community Manager");
              val x_10 = strcmp_30eaa229(scala.Tuple2(x_8, x_9));
              val x_11 = x_10.asInstanceOf[scala.Int].==(0);
              if (x_11) {
                val cursor_12 = Person_table_42e11bff.cursorOpen();
                val x_13 = Person_table_42e11bff.last(cursor_12);
                val x_14 = empty_pointers_Person_73fe802b(x_13);
                var v_15: Person = x_14;
                while ({
                  val x_16 = v_15;
                  x_16
                    .!=(null)
                    .&&({
                      val row_17 = v_15;
                      val x_18 = get_job_7c5bd76f(row_17);
                      val x_19 = get_jobName_247a7734(x_18);
                      val x_20 = toCString("Community Manager");
                      val x_21 = strcmp_30eaa229(scala.Tuple2(x_19, x_20));
                      val x_22 = x_21.asInstanceOf[scala.Int].==(0);
                      if (x_22) {
                        val x_23 = res_0;
                        val x_24 = get_salary_7033c615(row_6);
                        val x_25 = get_salary_7033c615(row_17);
                        val x_26 = scala.math.`package`.abs(x_24.-(x_25));
                        val x_27 = x_26.>(x_23._3);
                        val x_32 = if (x_27) {
                          val x_28 = get_name_73dbe0ab(row_6);
                          val x_29 = get_string_4099b680(x_28);
                          val x_30 = get_name_73dbe0ab(row_17);
                          val x_31 = get_string_4099b680(x_30);
                          scala.Tuple3(x_29, x_31, x_26)
                        } else
                          x_23;
                        res_0 = x_32;
                        true
                      } else
                        true
                    })
                }) {
                  val x_33 = Person_table_42e11bff.prev(cursor_12);
                  val x_34 = empty_pointers_Person_73fe802b(x_33);
                  v_15 = x_34
                };
                Person_table_42e11bff.cursorClose(cursor_12);
                true
              } else
                true
            })
        }) {
          val x_35 = Person_table_42e11bff.prev(cursor_1);
          val x_36 = empty_pointers_Person_73fe802b(x_35);
          v_4 = x_36
        };
        Person_table_42e11bff.cursorClose(cursor_1);
        val x_37 = res_0;
        LMDBTable.txnCommit();
        Str_table_44998cfa.dbiClose();
        Person_table_42e11bff.dbiClose();
        Job_table_6d5e3661.dbiClose();
        Company_table_5a23800e.dbiClose();
        x_37
      }
    }
  def computeMinimumSalary: (String, Double) = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      var res_0: scala.Tuple2[java.lang.String, scala.Double] =
        scala.Tuple2("", 1.7976931348623157e308);
      val cursor_1 = Person_table_42e11bff.cursorOpen();
      val x_2 = Person_table_42e11bff.last(cursor_1);
      val x_3 = empty_pointers_Person_73fe802b(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_salary_7033c615(row_6);
            val x_9 = x_8.<(x_7._2);
            val x_13 = if (x_9) {
              val x_10 = get_name_73dbe0ab(row_6);
              val x_11 = get_string_4099b680(x_10);
              val x_12 = get_salary_7033c615(row_6);
              scala.Tuple2(x_11, x_12)
            } else
              x_7;
            res_0 = x_13;
            true
          })
      }) {
        val x_14 = Person_table_42e11bff.prev(cursor_1);
        val x_15 = empty_pointers_Person_73fe802b(x_14);
        v_4 = x_15
      };
      Person_table_42e11bff.cursorClose(cursor_1);
      val x_16 = res_0;
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose();
      x_16
    }
  }
  def deleteDave: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val cursor_0 = Person_table_42e11bff.cursorOpen();
      val x_1 = Person_table_42e11bff.last(cursor_0);
      val x_2 = empty_pointers_Person_73fe802b(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_name_73dbe0ab(row_5);
            val x_7 = toCString("Dave");
            val x_8 = strcmp_30eaa229(scala.Tuple2(x_6, x_7));
            val x_9 = x_8.asInstanceOf[scala.Int].==(0);
            if (x_9) {
              delete_Person_2fa82321(row_5);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_42e11bff.prev(cursor_0);
        val x_11 = empty_pointers_Person_73fe802b(x_10);
        v_3 = x_11
      };
      Person_table_42e11bff.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
  def increaseRobertSalaryBy750: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val cursor_0 = Person_table_42e11bff.cursorOpen();
      val x_1 = Person_table_42e11bff.last(cursor_0);
      val x_2 = empty_pointers_Person_73fe802b(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_name_73dbe0ab(row_5);
            val x_7 = toCString("Robert");
            val x_8 = strcmp_30eaa229(scala.Tuple2(x_6, x_7));
            val x_9 = x_8.asInstanceOf[scala.Int].==(0);
            if (x_9) {
              val x_10 = get_salary_7033c615(row_5);
              set_salary_65b71959(row_5, x_10.+(750));
              true
            } else
              true
          })
      }) {
        val x_11 = Person_table_42e11bff.prev(cursor_0);
        val x_12 = empty_pointers_Person_73fe802b(x_11);
        v_3 = x_12
      };
      Person_table_42e11bff.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
  def insertCompanies: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/companies.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Company](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_27c4797(x_6);
        val x_8 = init_Str_63078b4(x_7);
        init_Company_c6a3b96(scala.Tuple2(x_5, x_8))
      }));
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
  def insertJobs: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/jobs.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Job](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_27c4797(x_6);
        val x_8 = init_Str_63078b4(x_7);
        val x_9 = line_2.split(",")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toLong;
        val x_12 = Company_table_5a23800e.get(x_11);
        val x_13 = empty_pointers_Company_4e744424(x_12);
        init_Job_94e2d0(scala.Tuple3(x_5, x_8, x_13))
      }));
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
  def insertPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/persons.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Person](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_27c4797(x_6);
        val x_8 = init_Str_63078b4(x_7);
        val x_9 = line_2.split(",")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toInt;
        val x_12 = line_2.split(",")(3);
        val x_13 = scala.Predef.augmentString(x_12);
        val x_14 = x_13.toDouble;
        val x_15 = line_2.split(",")(4);
        val x_16 = scala.Predef.augmentString(x_15);
        val x_17 = x_16.toLong;
        val x_18 = Job_table_6d5e3661.get(x_17);
        val x_19 = empty_pointers_Job_50c17c3a(x_18);
        init_Person_e580a58(scala.Tuple5(x_5, x_8, x_11, x_14, x_19))
      }));
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
  def printData: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val cursor_0 = Person_table_42e11bff.cursorOpen();
      val x_1 = Person_table_42e11bff.last(cursor_0);
      val x_2 = empty_pointers_Person_73fe802b(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext(
              "",
              ", ",
              " years old, earns ",
              " at ",
              " as a ",
              ""
            );
            val x_7 = get_name_73dbe0ab(row_5);
            val x_8 = get_string_4099b680(x_7);
            val x_9 = get_age_55fb25bb(row_5);
            val x_10 = get_salary_7033c615(row_5);
            val x_11 = get_job_7c5bd76f(row_5);
            val x_12 = get_company_4bfab7c7(x_11);
            val x_13 = get_name_46ff4c5(x_12);
            val x_14 = get_string_4099b680(x_13);
            val x_15 = get_job_7c5bd76f(row_5);
            val x_16 = get_jobName_247a7734(x_15);
            val x_17 = get_string_4099b680(x_16);
            val x_18 = x_6.s(x_8, x_9, x_10, x_14, x_17);
            scala.Predef.println(x_18);
            true
          })
      }) {
        val x_19 = Person_table_42e11bff.prev(cursor_0);
        val x_20 = empty_pointers_Person_73fe802b(x_19);
        v_3 = x_20
      };
      Person_table_42e11bff.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
  def printDataColumns: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_44998cfa.dbiOpen();
      Person_table_42e11bff.dbiOpen();
      Job_table_6d5e3661.dbiOpen();
      Company_table_5a23800e.dbiOpen();
      val cursor_0 = Person_table_42e11bff.cursorOpen();
      val x_1 = Person_table_42e11bff.last(cursor_0);
      val x_2 = empty_pointers_Person_73fe802b(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("", " | ", " | ", " | ", " | ", "");
            val x_7 = get_name_73dbe0ab(row_5);
            val x_8 = get_string_4099b680(x_7);
            val x_9 = scala.Predef.augmentString(x_8);
            val x_10 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_11 =
              x_9.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](7, " ")(x_10);
            val x_12 = x_11.mkString;
            val x_13 = get_age_55fb25bb(row_5);
            val x_14 = scala.Predef.augmentString(x_13.toString());
            val x_15 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_16 =
              x_14.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](2, " ")(x_15);
            val x_17 = x_16.mkString;
            val x_18 = get_salary_7033c615(row_5);
            val x_19 = scala.Predef.augmentString(x_18.toString());
            val x_20 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_21 =
              x_19.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](7, " ")(x_20);
            val x_22 = x_21.mkString;
            val x_23 = get_job_7c5bd76f(row_5);
            val x_24 = get_company_4bfab7c7(x_23);
            val x_25 = get_name_46ff4c5(x_24);
            val x_26 = get_string_4099b680(x_25);
            val x_27 = scala.Predef.augmentString(x_26);
            val x_28 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_29 =
              x_27.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](7, " ")(x_28);
            val x_30 = x_29.mkString;
            val x_31 = get_job_7c5bd76f(row_5);
            val x_32 = get_jobName_247a7734(x_31);
            val x_33 = get_string_4099b680(x_32);
            val x_34 = scala.Predef.augmentString(x_33);
            val x_35 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_36 =
              x_34.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](20, " ")(x_35);
            val x_37 = x_36.mkString;
            val x_38 = x_6.s(x_12, x_17, x_22, x_30, x_37);
            scala.Predef.println(x_38);
            true
          })
      }) {
        val x_39 = Person_table_42e11bff.prev(cursor_0);
        val x_40 = empty_pointers_Person_73fe802b(x_39);
        v_3 = x_40
      };
      Person_table_42e11bff.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_44998cfa.dbiClose();
      Person_table_42e11bff.dbiClose();
      Job_table_6d5e3661.dbiClose();
      Company_table_5a23800e.dbiClose()
    }
  }
}

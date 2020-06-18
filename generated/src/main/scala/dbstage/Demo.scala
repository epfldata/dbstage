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
  lazy val Company_table_46cf9bcc = new LMDBTable[Company]("Company")

  type JobData = CStruct5[Long, Long, Str, Long, Company]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_260385e = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Long, Str, Int, Double, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_211aa43b = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_30c10d09 = new LMDBTable[CString]("Str")

  def toCString_a6af126(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_9cb9afd(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Company_4d6278a0(value: Company): Company = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_1659c54b(value: Job): Job = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Person_7a5b7e38(value: Person): Person = {
    if (value != null) {
      value._3 = null
      value._7 = null
    }
    value
  }

  def get_Company_3a99360c(key: Long)(implicit zone: Zone): Company = {
    val value = Company_table_46cf9bcc.get(key)
    empty_pointers_Company_4d6278a0(value)
  }
  def get_Job_64e0ee87(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_260385e.get(key)
    empty_pointers_Job_1659c54b(value)
  }
  def get_Person_3ccb1ccd(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_211aa43b.get(key)
    empty_pointers_Person_7a5b7e38(value)
  }

  def get_Str_1d401fd2(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_30c10d09.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Company_6e9a7320(data_el: Company)(implicit zone: Zone): Unit = {
    Company_table_46cf9bcc.put(
      data_el._1,
      sizeCompany,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_ded3ca2(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_260385e.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_692474a6(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_211aa43b.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_740748df(str: Str)(implicit zone: Zone): Unit = {
    Str_table_30c10d09.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Company_679edbfd(el: Company)(implicit zone: Zone): Unit = {
    Company_table_46cf9bcc.delete(el._1)
  }
  def delete_Job_29113661(el: Job)(implicit zone: Zone): Unit = {
    Job_table_260385e.delete(el._1)
  }
  def delete_Person_2918fc09(el: Person)(implicit zone: Zone): Unit = {
    Person_table_211aa43b.delete(el._1)
  }
  def delete_Str_2c15423c(el: Str)(implicit zone: Zone): Unit = {
    Str_table_30c10d09.delete(el._1)
  }

  def init_Company_6f0b3154(
      params: Tuple2[Long, Str]
  )(implicit zone: Zone): Company = {
    val new_val = alloc[CompanyData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    put_Company_6e9a7320(new_val)
    get_Company_3a99360c(new_val._1)
  }
  def init_Job_35982c0e(
      params: Tuple3[Long, Str, Company]
  )(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    put_Job_ded3ca2(new_val)
    get_Job_64e0ee87(new_val._1)
  }
  def init_Person_5afae0e1(
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
    put_Person_692474a6(new_val)
    get_Person_3ccb1ccd(new_val._1)
  }
  def init_Str_4b831cb0(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_30c10d09.getNextKey
    new_val._2 = param
    put_Str_740748df(new_val)
    get_Str_1d401fd2(new_val._1)
  }

  def isMinor_423ffab5(this_6e5db53a: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_3955be83(this_6e5db53a);
    x_0.<(18)
  }

  def charAt_156f9af7(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strcmp_2e5a7343(params: Tuple2[Str, CString])(implicit zone: Zone): Int =
    strcmp(params._1._2, params._2)
  def strlen_498d8509(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_3955be83(this_6e5db53a: Person)(implicit zone: Zone): Int = {
    this_6e5db53a._4
  }
  def get_company_4f14301e(this_5054f63a: Job)(implicit zone: Zone): Company = {
    if (this_5054f63a._5 == null) {
      this_5054f63a._5 = get_Company_3a99360c(this_5054f63a._4)
    }
    this_5054f63a._5
  }
  def get_job_2f575857(this_6e5db53a: Person)(implicit zone: Zone): Job = {
    if (this_6e5db53a._7 == null) {
      this_6e5db53a._7 = get_Job_64e0ee87(this_6e5db53a._6)
    }
    this_6e5db53a._7
  }
  def get_jobName_2aa3899e(this_5054f63a: Job)(implicit zone: Zone): Str = {
    if (this_5054f63a._3 == null) {
      this_5054f63a._3 = get_Str_1d401fd2(this_5054f63a._2)
    }
    this_5054f63a._3
  }
  def get_key_33cb00be(this_5db15fd6: Company)(implicit zone: Zone): Long = {
    this_5db15fd6._1
  }
  def get_name_26cb6370(this_5db15fd6: Company)(implicit zone: Zone): Str = {
    if (this_5db15fd6._3 == null) {
      this_5db15fd6._3 = get_Str_1d401fd2(this_5db15fd6._2)
    }
    this_5db15fd6._3
  }
  def get_name_1092f14e(this_6e5db53a: Person)(implicit zone: Zone): Str = {
    if (this_6e5db53a._3 == null) {
      this_6e5db53a._3 = get_Str_1d401fd2(this_6e5db53a._2)
    }
    this_6e5db53a._3
  }
  def get_salary_470a4365(
      this_6e5db53a: Person
  )(implicit zone: Zone): Double = {
    this_6e5db53a._5
  }
  def get_string_518253dc(this_2c2f75ca: Str)(implicit zone: Zone): String = {
    fromCString_9cb9afd(this_2c2f75ca._2)
  }

  def set_age_6f346f9d(this_6e5db53a: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_6e5db53a._4 = age
  }
  def set_job_de4809f(this_6e5db53a: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_6e5db53a._6 = job._1
    this_6e5db53a._7 = job
  }
  def set_salary_5faa8cb6(this_6e5db53a: Person, salary: Double)(
      implicit zone: Zone
  ): Unit = {
    this_6e5db53a._5 = salary
  }

  def computeBiggestSalaryDifferenceCommunityManager: (String, String, Double) =
    Zone { implicit zone =>
      {
        LMDBTable.txnBegin();
        Str_table_30c10d09.dbiOpen();
        Person_table_211aa43b.dbiOpen();
        Job_table_260385e.dbiOpen();
        Company_table_46cf9bcc.dbiOpen();
        var res_0
            : scala.Tuple3[java.lang.String, java.lang.String, scala.Double] =
          scala.Tuple3("", "", scala.Double.MinValue);
        val cursor_1 = Person_table_211aa43b.cursorOpen();
        val x_2 = Person_table_211aa43b.last(cursor_1);
        val x_3 = empty_pointers_Person_7a5b7e38(x_2);
        var v_4: Person = x_3;
        while ({
          val x_5 = v_4;
          x_5
            .!=(null)
            .&&({
              val row_6 = v_4;
              val x_7 = get_job_2f575857(row_6);
              val x_8 = get_jobName_2aa3899e(x_7);
              val x_9 = toCString("Community Manager");
              val x_10 = strcmp_2e5a7343(scala.Tuple2(x_8, x_9));
              val x_11 = x_10.asInstanceOf[scala.Int].==(0);
              if (x_11) {
                val cursor_12 = Person_table_211aa43b.cursorOpen();
                val x_13 = Person_table_211aa43b.last(cursor_12);
                val x_14 = empty_pointers_Person_7a5b7e38(x_13);
                var v_15: Person = x_14;
                while ({
                  val x_16 = v_15;
                  x_16
                    .!=(null)
                    .&&({
                      val row_17 = v_15;
                      val x_18 = get_job_2f575857(row_17);
                      val x_19 = get_jobName_2aa3899e(x_18);
                      val x_20 = toCString("Community Manager");
                      val x_21 = strcmp_2e5a7343(scala.Tuple2(x_19, x_20));
                      val x_22 = x_21.asInstanceOf[scala.Int].==(0);
                      if (x_22) {
                        val x_23 = res_0;
                        val x_24 = get_salary_470a4365(row_6);
                        val x_25 = get_salary_470a4365(row_17);
                        val x_26 = scala.math.`package`.abs(x_24.-(x_25));
                        val x_27 = x_26.>(x_23._3);
                        val x_32 = if (x_27) {
                          val x_28 = get_name_1092f14e(row_6);
                          val x_29 = get_string_518253dc(x_28);
                          val x_30 = get_name_1092f14e(row_17);
                          val x_31 = get_string_518253dc(x_30);
                          scala.Tuple3(x_29, x_31, x_26)
                        } else
                          x_23;
                        res_0 = x_32;
                        true
                      } else
                        true
                    })
                }) {
                  val x_33 = Person_table_211aa43b.prev(cursor_12);
                  val x_34 = empty_pointers_Person_7a5b7e38(x_33);
                  v_15 = x_34
                };
                Person_table_211aa43b.cursorClose(cursor_12);
                true
              } else
                true
            })
        }) {
          val x_35 = Person_table_211aa43b.prev(cursor_1);
          val x_36 = empty_pointers_Person_7a5b7e38(x_35);
          v_4 = x_36
        };
        Person_table_211aa43b.cursorClose(cursor_1);
        val x_37 = res_0;
        LMDBTable.txnCommit();
        Str_table_30c10d09.dbiClose();
        Person_table_211aa43b.dbiClose();
        Job_table_260385e.dbiClose();
        Company_table_46cf9bcc.dbiClose();
        x_37
      }
    }
  def computeMinimumSalary: (String, Double) = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      var res_0: scala.Tuple2[java.lang.String, scala.Double] =
        scala.Tuple2("", 1.7976931348623157e308);
      val cursor_1 = Person_table_211aa43b.cursorOpen();
      val x_2 = Person_table_211aa43b.last(cursor_1);
      val x_3 = empty_pointers_Person_7a5b7e38(x_2);
      var v_4: Person = x_3;
      while ({
        val x_5 = v_4;
        x_5
          .!=(null)
          .&&({
            val row_6 = v_4;
            val x_7 = res_0;
            val x_8 = get_salary_470a4365(row_6);
            val x_9 = x_8.<(x_7._2);
            val x_13 = if (x_9) {
              val x_10 = get_name_1092f14e(row_6);
              val x_11 = get_string_518253dc(x_10);
              val x_12 = get_salary_470a4365(row_6);
              scala.Tuple2(x_11, x_12)
            } else
              x_7;
            res_0 = x_13;
            true
          })
      }) {
        val x_14 = Person_table_211aa43b.prev(cursor_1);
        val x_15 = empty_pointers_Person_7a5b7e38(x_14);
        v_4 = x_15
      };
      Person_table_211aa43b.cursorClose(cursor_1);
      val x_16 = res_0;
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose();
      x_16
    }
  }
  def computeSalaryCompany: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val cursor_0 = Company_table_46cf9bcc.cursorOpen();
      val x_1 = Company_table_46cf9bcc.last(cursor_0);
      val x_2 = empty_pointers_Company_4d6278a0(x_1);
      var v_3: Company = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_name_26cb6370(row_5);
            val x_7 = get_string_518253dc(x_6);
            var res_8: scala.Tuple2[java.lang.String, scala.Double] =
              scala.Tuple2(x_7, 0.0);
            val cursor_9 = Person_table_211aa43b.cursorOpen();
            val x_10 = Person_table_211aa43b.last(cursor_9);
            val x_11 = empty_pointers_Person_7a5b7e38(x_10);
            var v_12: Person = x_11;
            while ({
              val x_13 = v_12;
              x_13
                .!=(null)
                .&&({
                  val row_14 = v_12;
                  val x_15 = get_job_2f575857(row_14);
                  val x_16 = get_company_4f14301e(x_15);
                  val x_17 = x_16.==(row_5);
                  if (x_17) {
                    val x_18 = res_8;
                    val x_19 = get_salary_470a4365(row_14);
                    val x_20 = x_18._2.+(x_19);
                    res_8 = scala.Tuple2(x_18._1, x_20);
                    true
                  } else
                    true
                })
            }) {
              val x_21 = Person_table_211aa43b.prev(cursor_9);
              val x_22 = empty_pointers_Person_7a5b7e38(x_21);
              v_12 = x_22
            };
            Person_table_211aa43b.cursorClose(cursor_9);
            val x_23 = res_8;
            val x_24 = scala.StringContext("", " has cumulated salary ", "");
            val x_25 = x_24.s(x_23._1, x_23._2);
            scala.Predef.println(x_25);
            true
          })
      }) {
        val x_26 = Company_table_46cf9bcc.prev(cursor_0);
        val x_27 = empty_pointers_Company_4d6278a0(x_26);
        v_3 = x_27
      };
      Company_table_46cf9bcc.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def deleteDave: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val cursor_0 = Person_table_211aa43b.cursorOpen();
      val x_1 = Person_table_211aa43b.last(cursor_0);
      val x_2 = empty_pointers_Person_7a5b7e38(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_name_1092f14e(row_5);
            val x_7 = toCString("Dave");
            val x_8 = strcmp_2e5a7343(scala.Tuple2(x_6, x_7));
            val x_9 = x_8.asInstanceOf[scala.Int].==(0);
            if (x_9) {
              delete_Person_2918fc09(row_5);
              true
            } else
              true
          })
      }) {
        val x_10 = Person_table_211aa43b.prev(cursor_0);
        val x_11 = empty_pointers_Person_7a5b7e38(x_10);
        v_3 = x_11
      };
      Person_table_211aa43b.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def increaseRobertSalaryBy750: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val cursor_0 = Person_table_211aa43b.cursorOpen();
      val x_1 = Person_table_211aa43b.last(cursor_0);
      val x_2 = empty_pointers_Person_7a5b7e38(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = get_name_1092f14e(row_5);
            val x_7 = toCString("Robert");
            val x_8 = strcmp_2e5a7343(scala.Tuple2(x_6, x_7));
            val x_9 = x_8.asInstanceOf[scala.Int].==(0);
            if (x_9) {
              val x_10 = get_salary_470a4365(row_5);
              set_salary_5faa8cb6(row_5, x_10.+(750));
              true
            } else
              true
          })
      }) {
        val x_11 = Person_table_211aa43b.prev(cursor_0);
        val x_12 = empty_pointers_Person_7a5b7e38(x_11);
        v_3 = x_12
      };
      Person_table_211aa43b.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def insertCompanies: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/companies.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Company](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_a6af126(x_6);
        val x_8 = init_Str_4b831cb0(x_7);
        init_Company_6f0b3154(scala.Tuple2(x_5, x_8))
      }));
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def insertJobs: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/jobs.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Job](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_a6af126(x_6);
        val x_8 = init_Str_4b831cb0(x_7);
        val x_9 = line_2.split(",")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toLong;
        val x_12 = Company_table_46cf9bcc.get(x_11);
        val x_13 = empty_pointers_Company_4d6278a0(x_12);
        init_Job_35982c0e(scala.Tuple3(x_5, x_8, x_13))
      }));
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def insertPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/persons.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Person](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_a6af126(x_6);
        val x_8 = init_Str_4b831cb0(x_7);
        val x_9 = line_2.split(",")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toInt;
        val x_12 = line_2.split(",")(3);
        val x_13 = scala.Predef.augmentString(x_12);
        val x_14 = x_13.toDouble;
        val x_15 = line_2.split(",")(4);
        val x_16 = scala.Predef.augmentString(x_15);
        val x_17 = x_16.toLong;
        val x_18 = Job_table_260385e.get(x_17);
        val x_19 = empty_pointers_Job_1659c54b(x_18);
        init_Person_5afae0e1(scala.Tuple5(x_5, x_8, x_11, x_14, x_19))
      }));
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def printData: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val cursor_0 = Person_table_211aa43b.cursorOpen();
      val x_1 = Person_table_211aa43b.last(cursor_0);
      val x_2 = empty_pointers_Person_7a5b7e38(x_1);
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
            val x_7 = get_name_1092f14e(row_5);
            val x_8 = get_string_518253dc(x_7);
            val x_9 = get_age_3955be83(row_5);
            val x_10 = get_salary_470a4365(row_5);
            val x_11 = get_job_2f575857(row_5);
            val x_12 = get_company_4f14301e(x_11);
            val x_13 = get_name_26cb6370(x_12);
            val x_14 = get_string_518253dc(x_13);
            val x_15 = get_job_2f575857(row_5);
            val x_16 = get_jobName_2aa3899e(x_15);
            val x_17 = get_string_518253dc(x_16);
            val x_18 = x_6.s(x_8, x_9, x_10, x_14, x_17);
            scala.Predef.println(x_18);
            true
          })
      }) {
        val x_19 = Person_table_211aa43b.prev(cursor_0);
        val x_20 = empty_pointers_Person_7a5b7e38(x_19);
        v_3 = x_20
      };
      Person_table_211aa43b.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
  def printDataColumns: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_30c10d09.dbiOpen();
      Person_table_211aa43b.dbiOpen();
      Job_table_260385e.dbiOpen();
      Company_table_46cf9bcc.dbiOpen();
      val cursor_0 = Person_table_211aa43b.cursorOpen();
      val x_1 = Person_table_211aa43b.last(cursor_0);
      val x_2 = empty_pointers_Person_7a5b7e38(x_1);
      var v_3: Person = x_2;
      while ({
        val x_4 = v_3;
        x_4
          .!=(null)
          .&&({
            val row_5 = v_3;
            val x_6 = scala.StringContext("", " | ", " | ", " | ", " | ", "");
            val x_7 = get_name_1092f14e(row_5);
            val x_8 = get_string_518253dc(x_7);
            val x_9 = scala.Predef.augmentString(x_8);
            val x_10 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_11 =
              x_9.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](7, " ")(x_10);
            val x_12 = x_11.mkString;
            val x_13 = get_age_3955be83(row_5);
            val x_14 = scala.Predef.augmentString(x_13.toString());
            val x_15 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_16 =
              x_14.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](2, " ")(x_15);
            val x_17 = x_16.mkString;
            val x_18 = get_salary_470a4365(row_5);
            val x_19 = scala.Predef.augmentString(x_18.toString());
            val x_20 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_21 =
              x_19.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](7, " ")(x_20);
            val x_22 = x_21.mkString;
            val x_23 = get_job_2f575857(row_5);
            val x_24 = get_company_4f14301e(x_23);
            val x_25 = get_name_26cb6370(x_24);
            val x_26 = get_string_518253dc(x_25);
            val x_27 = scala.Predef.augmentString(x_26);
            val x_28 = scala.Predef.fallbackStringCanBuildFrom[scala.Any];
            val x_29 =
              x_27.padTo[scala.Any, scala.collection.immutable.IndexedSeq[
                scala.Any
              ]](7, " ")(x_28);
            val x_30 = x_29.mkString;
            val x_31 = get_job_2f575857(row_5);
            val x_32 = get_jobName_2aa3899e(x_31);
            val x_33 = get_string_518253dc(x_32);
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
        val x_39 = Person_table_211aa43b.prev(cursor_0);
        val x_40 = empty_pointers_Person_7a5b7e38(x_39);
        v_3 = x_40
      };
      Person_table_211aa43b.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_30c10d09.dbiClose();
      Person_table_211aa43b.dbiClose();
      Job_table_260385e.dbiClose();
      Company_table_46cf9bcc.dbiClose()
    }
  }
}

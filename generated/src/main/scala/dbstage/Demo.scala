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
  lazy val Company_table_63c312df = new LMDBTable[Company]("Company")

  type JobData = CStruct5[Long, Long, Str, Long, Company]
  type Job = Ptr[JobData]
  val sizeJob = sizeof[JobData]
  lazy val Job_table_3b06003f = new LMDBTable[Job]("Job")

  type PersonData = CStruct7[Long, Long, Str, Int, Double, Long, Job]
  type Person = Ptr[PersonData]
  val sizePerson = sizeof[PersonData]
  lazy val Person_table_4f6627f4 = new LMDBTable[Person]("Person")

  type StrData = CStruct2[Long, CString]
  type Str = Ptr[StrData]
  lazy val Str_table_4e9297c2 = new LMDBTable[CString]("Str")

  def toCString_50b5b1bf(string: String)(implicit zone: Zone): CString = {
    toCString(string)
  }

  def fromCString_5bb9446e(string: CString)(implicit zone: Zone): String = {
    fromCString(string)
  }

  def empty_pointers_Company_13a6ee98(value: Company): Company = {
    if (value != null) {
      value._3 = null
    }
    value
  }
  def empty_pointers_Job_4262a847(value: Job): Job = {
    if (value != null) {
      value._3 = null
      value._5 = null
    }
    value
  }
  def empty_pointers_Person_7cc0e2c9(value: Person): Person = {
    if (value != null) {
      value._3 = null
      value._7 = null
    }
    value
  }

  def get_Company_1401bc3e(key: Long)(implicit zone: Zone): Company = {
    val value = Company_table_63c312df.get(key)
    empty_pointers_Company_13a6ee98(value)
  }
  def get_Job_30774208(key: Long)(implicit zone: Zone): Job = {
    val value = Job_table_3b06003f.get(key)
    empty_pointers_Job_4262a847(value)
  }
  def get_Person_6780e4d1(key: Long)(implicit zone: Zone): Person = {
    val value = Person_table_4f6627f4.get(key)
    empty_pointers_Person_7cc0e2c9(value)
  }

  def get_Str_1c2d9442(key: Long)(implicit zone: Zone): Str = {
    val value = alloc[StrData]
    val string = Str_table_4e9297c2.get(key)
    value._1 = key
    value._2 = string
    value
  }

  def put_Company_5d43bb5(data_el: Company)(implicit zone: Zone): Unit = {
    Company_table_63c312df.put(
      data_el._1,
      sizeCompany,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }
  def put_Job_fb95e05(data_el: Job)(implicit zone: Zone): Unit = {
    Job_table_3b06003f.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
  }
  def put_Person_6f7f954f(data_el: Person)(implicit zone: Zone): Unit = {
    Person_table_4f6627f4.put(
      data_el._1,
      sizePerson,
      data_el.asInstanceOf[Ptr[Byte]]
    )
  }

  def put_Str_1f809366(str: Str)(implicit zone: Zone): Unit = {
    Str_table_4e9297c2.put(str._1, strlen(str._2) + 1, str._2)
  }

  def delete_Company_6de715d0(el: Company)(implicit zone: Zone): Unit = {
    Company_table_63c312df.delete(el._1)
  }
  def delete_Job_76e7b0a9(el: Job)(implicit zone: Zone): Unit = {
    Job_table_3b06003f.delete(el._1)
  }
  def delete_Person_254c5c4f(el: Person)(implicit zone: Zone): Unit = {
    Person_table_4f6627f4.delete(el._1)
  }
  def delete_Str_528ffe52(el: Str)(implicit zone: Zone): Unit = {
    Str_table_4e9297c2.delete(el._1)
  }

  def init_Company_cd6c843(
      params: Tuple2[Long, Str]
  )(implicit zone: Zone): Company = {
    val new_val = alloc[CompanyData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    put_Company_5d43bb5(new_val)
    get_Company_1401bc3e(new_val._1)
  }
  def init_Job_1769d5a5(
      params: Tuple3[Long, Str, Company]
  )(implicit zone: Zone): Job = {
    val new_val = alloc[JobData]
    new_val._1 = params._1
    new_val._2 = params._2._1
    new_val._3 = params._2
    new_val._4 = params._3._1
    new_val._5 = params._3
    put_Job_fb95e05(new_val)
    get_Job_30774208(new_val._1)
  }
  def init_Person_53e9e22d(
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
    put_Person_6f7f954f(new_val)
    get_Person_6780e4d1(new_val._1)
  }
  def init_Str_63137699(param: CString)(implicit zone: Zone): Str = {
    val new_val = alloc[StrData]
    new_val._1 = Str_table_4e9297c2.getNextKey
    new_val._2 = param
    put_Str_1f809366(new_val)
    get_Str_1c2d9442(new_val._1)
  }

  def isMinor_4d1c09d(this_22dd16ce: Person)(implicit zone: Zone): Boolean = {
    val x_0 = get_age_4f2b8de1(this_22dd16ce);
    x_0.<(18)
  }

  def charAt_4fd54dea(params: Tuple2[Str, Long])(implicit zone: Zone): Byte =
    charAt(params._1._2, params._2)
  def strcmp_4f059def(params: Tuple2[Str, CString])(implicit zone: Zone): Int =
    strcmp(params._1._2, params._2)
  def strlen_4b6d9a99(param: Str)(implicit zone: Zone): Long = strlen(param._2)

  def get_age_4f2b8de1(this_22dd16ce: Person)(implicit zone: Zone): Int = {
    this_22dd16ce._4
  }
  def get_company_13c1b9bd(this_33e43783: Job)(implicit zone: Zone): Company = {
    if (this_33e43783._5 == null) {
      this_33e43783._5 = get_Company_1401bc3e(this_33e43783._4)
    }
    this_33e43783._5
  }
  def get_job_642c340(this_22dd16ce: Person)(implicit zone: Zone): Job = {
    if (this_22dd16ce._7 == null) {
      this_22dd16ce._7 = get_Job_30774208(this_22dd16ce._6)
    }
    this_22dd16ce._7
  }
  def get_jobName_29ffeed2(this_33e43783: Job)(implicit zone: Zone): Str = {
    if (this_33e43783._3 == null) {
      this_33e43783._3 = get_Str_1c2d9442(this_33e43783._2)
    }
    this_33e43783._3
  }
  def get_key_2527c6db(this_3bb59c41: Company)(implicit zone: Zone): Long = {
    this_3bb59c41._1
  }
  def get_name_356f49fc(this_3bb59c41: Company)(implicit zone: Zone): Str = {
    if (this_3bb59c41._3 == null) {
      this_3bb59c41._3 = get_Str_1c2d9442(this_3bb59c41._2)
    }
    this_3bb59c41._3
  }
  def get_name_6330a8fd(this_22dd16ce: Person)(implicit zone: Zone): Str = {
    if (this_22dd16ce._3 == null) {
      this_22dd16ce._3 = get_Str_1c2d9442(this_22dd16ce._2)
    }
    this_22dd16ce._3
  }
  def get_salary_74f0674e(
      this_22dd16ce: Person
  )(implicit zone: Zone): Double = {
    this_22dd16ce._5
  }
  def get_string_2095f2af(this_6f7a7615: Str)(implicit zone: Zone): String = {
    fromCString_5bb9446e(this_6f7a7615._2)
  }

  def set_age_622cc070(this_22dd16ce: Person, age: Int)(
      implicit zone: Zone
  ): Unit = {
    this_22dd16ce._4 = age
  }
  def set_job_6ec1cc52(this_22dd16ce: Person, job: Job)(
      implicit zone: Zone
  ): Unit = {
    this_22dd16ce._6 = job._1
    this_22dd16ce._7 = job
  }
  def set_salary_68e83b36(this_22dd16ce: Person, salary: Double)(
      implicit zone: Zone
  ): Unit = {
    this_22dd16ce._5 = salary
  }

  def insertCompanies: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4e9297c2.dbiOpen();
      Person_table_4f6627f4.dbiOpen();
      Job_table_3b06003f.dbiOpen();
      Company_table_63c312df.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/companies.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Company](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_50b5b1bf(x_6);
        val x_8 = init_Str_63137699(x_7);
        init_Company_cd6c843(scala.Tuple2(x_5, x_8))
      }));
      LMDBTable.txnCommit();
      Str_table_4e9297c2.dbiClose();
      Person_table_4f6627f4.dbiClose();
      Job_table_3b06003f.dbiClose();
      Company_table_63c312df.dbiClose()
    }
  }
  def insertJobs: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4e9297c2.dbiOpen();
      Person_table_4f6627f4.dbiOpen();
      Job_table_3b06003f.dbiOpen();
      Company_table_63c312df.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/jobs.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Job](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_50b5b1bf(x_6);
        val x_8 = init_Str_63137699(x_7);
        val x_9 = line_2.split(",")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toLong;
        val x_12 = Company_table_63c312df.get(x_11);
        val x_13 = empty_pointers_Company_13a6ee98(x_12);
        init_Job_1769d5a5(scala.Tuple3(x_5, x_8, x_13))
      }));
      LMDBTable.txnCommit();
      Str_table_4e9297c2.dbiClose();
      Person_table_4f6627f4.dbiClose();
      Job_table_3b06003f.dbiClose();
      Company_table_63c312df.dbiClose()
    }
  }
  def insertPersons: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4e9297c2.dbiOpen();
      Person_table_4f6627f4.dbiOpen();
      Job_table_3b06003f.dbiOpen();
      Company_table_63c312df.dbiOpen();
      val x_0 = scala.io.Source
        .fromFile("./data/persons.csv")(scala.io.Codec.fallbackSystemCodec);
      val x_1 = x_0.getLines();
      x_1.foreach[Person](((line_2: java.lang.String) => {
        val x_3 = line_2.split(",")(0);
        val x_4 = scala.Predef.augmentString(x_3);
        val x_5 = x_4.toLong;
        val x_6 = line_2.split(",")(1);
        val x_7 = toCString_50b5b1bf(x_6);
        val x_8 = init_Str_63137699(x_7);
        val x_9 = line_2.split(",")(2);
        val x_10 = scala.Predef.augmentString(x_9);
        val x_11 = x_10.toInt;
        val x_12 = line_2.split(",")(3);
        val x_13 = scala.Predef.augmentString(x_12);
        val x_14 = x_13.toDouble;
        val x_15 = line_2.split(",")(4);
        val x_16 = scala.Predef.augmentString(x_15);
        val x_17 = x_16.toLong;
        val x_18 = Job_table_3b06003f.get(x_17);
        val x_19 = empty_pointers_Job_4262a847(x_18);
        init_Person_53e9e22d(scala.Tuple5(x_5, x_8, x_11, x_14, x_19))
      }));
      LMDBTable.txnCommit();
      Str_table_4e9297c2.dbiClose();
      Person_table_4f6627f4.dbiClose();
      Job_table_3b06003f.dbiClose();
      Company_table_63c312df.dbiClose()
    }
  }
  def printData: Unit = Zone { implicit zone =>
    {
      LMDBTable.txnBegin();
      Str_table_4e9297c2.dbiOpen();
      Person_table_4f6627f4.dbiOpen();
      Job_table_3b06003f.dbiOpen();
      Company_table_63c312df.dbiOpen();
      val cursor_0 = Person_table_4f6627f4.cursorOpen();
      val x_1 = Person_table_4f6627f4.last(cursor_0);
      val x_2 = empty_pointers_Person_7cc0e2c9(x_1);
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
            val x_7 = get_name_6330a8fd(row_5);
            val x_8 = get_string_2095f2af(x_7);
            val x_9 = get_age_4f2b8de1(row_5);
            val x_10 = get_salary_74f0674e(row_5);
            val x_11 = get_job_642c340(row_5);
            val x_12 = get_company_13c1b9bd(x_11);
            val x_13 = get_name_356f49fc(x_12);
            val x_14 = get_string_2095f2af(x_13);
            val x_15 = get_job_642c340(row_5);
            val x_16 = get_jobName_29ffeed2(x_15);
            val x_17 = get_string_2095f2af(x_16);
            val x_18 = x_6.s(x_8, x_9, x_10, x_14, x_17);
            scala.Predef.println(x_18);
            true
          })
      }) {
        val x_19 = Person_table_4f6627f4.prev(cursor_0);
        val x_20 = empty_pointers_Person_7cc0e2c9(x_19);
        v_3 = x_20
      };
      Person_table_4f6627f4.cursorClose(cursor_0);
      LMDBTable.txnCommit();
      Str_table_4e9297c2.dbiClose();
      Person_table_4f6627f4.dbiClose();
      Job_table_3b06003f.dbiClose();
      Company_table_63c312df.dbiClose()
    }
  }
}


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

      type JobData = CStruct4[Long,Int,Long, Str]
type Job = Ptr[JobData]
val sizeJob = sizeof[JobData]
lazy val Job_table_7a96b96a = new LMDBTable[Job]("Job")

type PersonData = CStruct7[Long,Int,Long, Str,Int,Long, Job]
type Person = Ptr[PersonData]
val sizePerson = sizeof[PersonData]
lazy val Person_table_78ca2162 = new LMDBTable[Person]("Person")


      type StrData = CStruct2[Long, CString]
type Str = Ptr[StrData]
lazy val Str_table_3a859b49 = new LMDBTable[CString]("Str")


      def toCString_1244d153(string: String)(implicit zone: Zone): CString = {
toCString(string)
}

      def empty_pointers_Job_568d8538(value: Job): Job = {
if(value != null) {value._4 = null
}
value
}
def empty_pointers_Person_380952d6(value: Person): Person = {
if(value != null) {value._4 = null
value._7 = null
}
value
}

      def get_Job_38d49c22(key: Long)(implicit zone: Zone): Job = {
val value = Job_table_7a96b96a.get(key)
empty_pointers_Job_568d8538(value)
}
def get_Person_620a712a(key: Long)(implicit zone: Zone): Person = {
val value = Person_table_78ca2162.get(key)
empty_pointers_Person_380952d6(value)
}

      def get_Str_58148c16(key: Long)(implicit zone: Zone): Str = {
val value = alloc[StrData]
val string = Str_table_3a859b49.get(key)
value._1 = key
value._2 = string
value
}

      def put_Job_2af9fa20(data_el: Job)(implicit zone: Zone): Unit = {
Job_table_7a96b96a.put(data_el._1, sizeJob, data_el.asInstanceOf[Ptr[Byte]])
}
def put_Person_36d046d1(data_el: Person)(implicit zone: Zone): Unit = {
Person_table_78ca2162.put(data_el._1, sizePerson, data_el.asInstanceOf[Ptr[Byte]])
}

      def put_Str_7e2e200(str: Str)(implicit zone: Zone): Unit = {
Str_table_3a859b49.put(str._1, strlen(str._2)+1, str._2)
}

      def init_Job_3173234d(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
val key = Job_table_7a96b96a.getNextKey
val new_val = alloc[JobData]
new_val._1 = key
new_val._2 = params._1
new_val._3 = params._2._1
new_val._4 = params._2
put_Job_2af9fa20(new_val)
get_Job_38d49c22(key)
}
def init_Person_a895cd3(params: Tuple4[Int, Str, Int, Job])(implicit zone: Zone): Person = {
val key = Person_table_78ca2162.getNextKey
val new_val = alloc[PersonData]
new_val._1 = key
new_val._2 = params._1
new_val._3 = params._2._1
new_val._4 = params._2
new_val._5 = params._3
new_val._6 = params._4._1
new_val._7 = params._4
put_Person_36d046d1(new_val)
get_Person_620a712a(key)
}
def init_Str_5b2c902d(param: CString)(implicit zone: Zone): Str = {
val key = Str_table_3a859b49.getNextKey
val new_val = alloc[StrData]
new_val._1 = key
new_val._2 = param
put_Str_7e2e200(new_val)
get_Str_58148c16(key)
}

      def isMinor_5db1c5ec(this_3f9d209e: Person)(implicit zone: Zone): Boolean = {
  val x_0 = get_age_43b18b40(this_3f9d209e);
  x_0.<(18)
}

      def charAt_74d11fb8(params: Tuple2[Str, Long])(implicit zone: Zone): Byte = charAt(params._1._2,params._2)
def strlen_a1bde46(param: Str)(implicit zone: Zone): Long = strlen(param._2)

      def get_age_43b18b40(this_3f9d209e: Person)(implicit zone: Zone): Int = {
this_3f9d209e._5
}
def get_enterprise_7401b5aa(this_53498ad3: Job)(implicit zone: Zone): Str = {
if (this_53498ad3._4 == null) {
this_53498ad3._4 = get_Str_58148c16(this_53498ad3._3)
}
this_53498ad3._4
}
def get_job_282916b9(this_3f9d209e: Person)(implicit zone: Zone): Job = {
if (this_3f9d209e._7 == null) {
this_3f9d209e._7 = get_Job_38d49c22(this_3f9d209e._6)
}
this_3f9d209e._7
}
def get_name_52bfeae2(this_3f9d209e: Person)(implicit zone: Zone): Str = {
if (this_3f9d209e._4 == null) {
this_3f9d209e._4 = get_Str_58148c16(this_3f9d209e._3)
}
this_3f9d209e._4
}
def get_salary_4d3fed74(this_3f9d209e: Person)(implicit zone: Zone): Int = {
this_3f9d209e._2
}
def get_size_34eabd9c(this_53498ad3: Job)(implicit zone: Zone): Int = {
this_53498ad3._2
}
def get_string_5416e966(this_7953e1fc: Str)(implicit zone: Zone): CString = {
this_7953e1fc._2
}

      def set_age_4b3870e7(this_3f9d209e: Person, age : Int)(implicit zone: Zone): Unit = {
this_3f9d209e._5 = age 
}
def set_enterprise_5c1e7f43(this_53498ad3: Job, enterprise : Str)(implicit zone: Zone): Unit = {
this_53498ad3._3 = enterprise ._1
this_53498ad3._4 = enterprise 
}
def set_job_4057b9b5(this_3f9d209e: Person, job : Job)(implicit zone: Zone): Unit = {
this_3f9d209e._6 = job ._1
this_3f9d209e._7 = job 
}
def set_name_48e05610(this_3f9d209e: Person, name : Str)(implicit zone: Zone): Unit = {
this_3f9d209e._3 = name ._1
this_3f9d209e._4 = name 
}
def set_salary_47b10d3d(this_3f9d209e: Person, salary : Int)(implicit zone: Zone): Unit = {
this_3f9d209e._2 = salary 
}
def set_size_68fa96ef(this_53498ad3: Job, size : Int)(implicit zone: Zone): Unit = {
this_53498ad3._2 = size 
}

      def allMinors: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4: Person = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = isMinor_5db1c5ec(row_6);
      if (x_7.asInstanceOf[scala.Boolean])
        {
          val x_8 = res_0;
          res_0 = x_8.+(1);
          true
        }
      else
        true
    })
  }) 
    {
      val x_9 = Person_table_78ca2162.next(cursor_1);
      val x_10 = empty_pointers_Person_380952d6(x_9);
      v_4 = x_10
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_11 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_11
}
}
def allOld: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = get_salary_4d3fed74(row_6);
      val x_8 = get_name_52bfeae2(row_6);
      val x_9 = get_age_43b18b40(row_6);
      val x_10 = get_job_282916b9(row_6);
      val x_11 = init_Person_a895cd3(scala.Tuple4(x_7, x_8, x_9.+(100), x_10));
      val x_12 = res_0;
      res_0 = x_12.+(1);
      true
    })
  }) 
    {
      val x_13 = Person_table_78ca2162.next(cursor_1);
      val x_14 = empty_pointers_Person_380952d6(x_13);
      v_4 = x_14
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_15 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_15
}
}
def allOld2: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = get_age_43b18b40(row_6);
      set_age_4b3870e7(row_6, x_7.+(10));
      val x_8 = res_0;
      res_0 = x_8.+(1);
      true
    })
  }) 
    {
      val x_9 = Person_table_78ca2162.next(cursor_1);
      val x_10 = empty_pointers_Person_380952d6(x_9);
      v_4 = x_10
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_11 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_11
}
}
def allOld3: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = toCString_1244d153("Test");
      val x_8 = init_Str_5b2c902d(x_7);
      val x_9 = get_age_43b18b40(row_6);
      val x_10 = get_job_282916b9(row_6);
      val x_11 = init_Person_a895cd3(scala.Tuple4(0, x_8, x_9.+(100), x_10));
      val x_12 = res_0;
      res_0 = x_12.+(1);
      true
    })
  }) 
    {
      val x_13 = Person_table_78ca2162.next(cursor_1);
      val x_14 = empty_pointers_Person_380952d6(x_13);
      v_4 = x_14
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_15 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_15
}
}
def charAtQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = get_salary_4d3fed74(row_6);
      val x_8 = get_name_52bfeae2(row_6);
      val x_9 = charAt_74d11fb8(scala.Tuple2(x_8, 2L));
      val x_10 = toCString_1244d153(x_9.asInstanceOf[scala.Byte].toString());
      val x_11 = init_Str_5b2c902d(x_10);
      val x_12 = get_age_43b18b40(row_6);
      val x_13 = get_job_282916b9(row_6);
      val x_14 = init_Person_a895cd3(scala.Tuple4(x_7, x_11, x_12, x_13));
      val x_15 = res_0;
      res_0 = x_15.+(1);
      true
    })
  }) 
    {
      val x_16 = Person_table_78ca2162.next(cursor_1);
      val x_17 = empty_pointers_Person_380952d6(x_16);
      v_4 = x_17
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_18 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_18
}
}
def insertions: Unit = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  val x_0 = toCString_1244d153("EPFL");
  val x_1 = init_Str_5b2c902d(x_0);
  val x_2 = init_Job_3173234d(scala.Tuple2(10000, x_1));
  val x_3 = toCString_1244d153("Lucien");
  val x_4 = init_Str_5b2c902d(x_3);
  val x_5 = init_Person_a895cd3(scala.Tuple4(1000, x_4, 21, x_2));
  val x_6 = toCString_1244d153("John");
  val x_7 = init_Str_5b2c902d(x_6);
  val x_8 = init_Person_a895cd3(scala.Tuple4(100, x_7, 16, x_2));
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose()
}
}
def joinQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 2147483647;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row1_6 = v_4;
      val cursor_7 = Person_table_78ca2162.cursorOpen();
      val x_8 = Person_table_78ca2162.first(cursor_7);
      val x_9 = empty_pointers_Person_380952d6(x_8);
      var v_10 = x_9;
      while ({
        val x_11 = v_10;
        x_11.!=(null).&&({
          val row2_12 = v_10;
          val x_13 = get_age_43b18b40(((scala.Tuple2(row1_6, row2_12)))._1);
          val x_14 = get_age_43b18b40(((scala.Tuple2(row1_6, row2_12)))._2);
          val x_15 = scala.math.`package`.abs(x_13.-(x_14));
          val x_16 = res_0;
          val x_17 = scala.Predef.intWrapper(x_15);
          val x_18 = x_17.max(x_16);
          res_0 = x_18;
          true
        })
      }) 
        {
          val x_19 = Person_table_78ca2162.next(cursor_7);
          val x_20 = empty_pointers_Person_380952d6(x_19);
          v_10 = x_20
        }
      ;
      Person_table_78ca2162.cursorClose(cursor_7);
      true
    })
  }) 
    {
      val x_21 = Person_table_78ca2162.next(cursor_1);
      val x_22 = empty_pointers_Person_380952d6(x_21);
      v_4 = x_22
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_23 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_23
}
}
def mapQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val row_7 = get_job_282916b9(row_6);
      val row_8 = get_size_34eabd9c(row_7);
      val row_9 = row_8.+(100);
      val x_10 = res_0;
      res_0 = x_10.+(1);
      true
    })
  }) 
    {
      val x_11 = Person_table_78ca2162.next(cursor_1);
      val x_12 = empty_pointers_Person_380952d6(x_11);
      v_4 = x_12
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_13 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_13
}
}
def printAllAges: Unit = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  val cursor_0 = Person_table_78ca2162.cursorOpen();
  val x_1 = Person_table_78ca2162.first(cursor_0);
  val x_2 = empty_pointers_Person_380952d6(x_1);
  var v_3: Person = x_2;
  while ({
    val x_4 = v_3;
    x_4.!=(null).&&({
      val row_5 = v_3;
      val x_6 = get_age_43b18b40(row_5);
      scala.Predef.println(x_6);
      true
    })
  }) 
    {
      val x_7 = Person_table_78ca2162.next(cursor_0);
      val x_8 = empty_pointers_Person_380952d6(x_7);
      v_3 = x_8
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_0);
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose()
}
}
def sizes: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4 = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = get_salary_4d3fed74(row_6);
      val x_8 = get_name_52bfeae2(row_6);
      val x_9 = get_name_52bfeae2(row_6);
      val x_10 = strlen_a1bde46(x_9);
      val x_11 = get_job_282916b9(row_6);
      val x_12 = x_10.asInstanceOf[scala.Long].toInt;
      val x_13 = init_Person_a895cd3(scala.Tuple4(x_7, x_8, x_12, x_11));
      val x_14 = res_0;
      res_0 = x_14.+(1);
      true
    })
  }) 
    {
      val x_15 = Person_table_78ca2162.next(cursor_1);
      val x_16 = empty_pointers_Person_380952d6(x_15);
      v_4 = x_16
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_17 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_17
}
}
def sumAges: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_3a859b49.dbiOpen();
  Person_table_78ca2162.dbiOpen();
  Job_table_7a96b96a.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_78ca2162.cursorOpen();
  val x_2 = Person_table_78ca2162.first(cursor_1);
  val x_3 = empty_pointers_Person_380952d6(x_2);
  var v_4: Person = x_3;
  while ({
    val x_5 = v_4;
    x_5.!=(null).&&({
      val row_6 = v_4;
      val x_7 = res_0;
      val x_8 = get_age_43b18b40(row_6);
      res_0 = x_8.+(x_7);
      true
    })
  }) 
    {
      val x_9 = Person_table_78ca2162.next(cursor_1);
      val x_10 = empty_pointers_Person_380952d6(x_9);
      v_4 = x_10
    }
  ;
  Person_table_78ca2162.cursorClose(cursor_1);
  val x_11 = res_0;
  LMDBTable.txnCommit();
  Str_table_3a859b49.dbiClose();
  Person_table_78ca2162.dbiClose();
  Job_table_7a96b96a.dbiClose();
  x_11
}
}
    }
    
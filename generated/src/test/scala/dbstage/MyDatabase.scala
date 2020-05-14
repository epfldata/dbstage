
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

      class Job(val key: Long, var size : Int,var enterpriseId: Long, var enterprise : Str = null)
val sizeJob = sizeInt+sizeLong
lazy val Job_table_6a0fde14 = new LMDBTable[Job]("Job")

class Person(val key: Long, var salary : Int,var nameId: Long, var name : Str = null,var age : Int,var jobId: Long, var job : Job = null)
val sizePerson = sizeInt+sizeLong+sizeInt+sizeLong
lazy val Person_table_2ec2a849 = new LMDBTable[Person]("Person")


      class Str(val key: Long, var string : CString)
lazy val Str_table_59c9d4bf = new LMDBTable[Str]("Str")


      def toCString_30e96c31(string: String)(implicit zone: Zone): CString = {
toCString(string)
}

      def get_Str_8c2cc0b(key: Long)(implicit zone: Zone): Str = {
val value = Str_table_59c9d4bf.get(key)
fromPtrByte_Str_1fb98f6f(key, value)
}
def get_Job_405a2c3d(key: Long)(implicit zone: Zone): Job = {
val value = Job_table_6a0fde14.get(key)
fromPtrByte_Job_7331a1c6(key, value)
}
def get_Person_df73558(key: Long)(implicit zone: Zone): Person = {
val value = Person_table_2ec2a849.get(key)
fromPtrByte_Person_134b11ae(key, value)
}

      def put_Str_1df86d2d(data_el: Str)(implicit zone: Zone): Unit = {
val (key, size, value) = toPtrByte_Str_2991c0b2(data_el)
Str_table_59c9d4bf.put(key, size, value)
}
def put_Job_5b185a4f(data_el: Job)(implicit zone: Zone): Unit = {
val (key, size, value) = toPtrByte_Job_5a5c0f21(data_el)
Job_table_6a0fde14.put(key, size, value)
}
def put_Person_22641e63(data_el: Person)(implicit zone: Zone): Unit = {
val (key, size, value) = toPtrByte_Person_7c2b85ce(data_el)
Person_table_2ec2a849.put(key, size, value)
}

      def fromPtrByte_Str_1fb98f6f(key: Long, ptr1: Ptr[Byte])(implicit zone: Zone): Str = {
val value1 = ptr1
init_Str_1a8d36b(key, value1)
}
def fromPtrByte_Job_7331a1c6(key: Long, ptr1: Ptr[Byte])(implicit zone: Zone): Job = {
val value1 = intget(ptr1, sizeInt)
val ptr2 = ptr1 + sizeInt
val value2 = longget(ptr2, sizeLong)
val ptr3 = ptr2 + sizeLong
init_Job_25165d97_key(key, (value1,value2))
}
def fromPtrByte_Person_134b11ae(key: Long, ptr1: Ptr[Byte])(implicit zone: Zone): Person = {
val value1 = intget(ptr1, sizeInt)
val ptr2 = ptr1 + sizeInt
val value2 = longget(ptr2, sizeLong)
val ptr3 = ptr2 + sizeLong
val value3 = intget(ptr3, sizeInt)
val ptr4 = ptr3 + sizeInt
val value4 = longget(ptr4, sizeLong)
val ptr5 = ptr4 + sizeLong
init_Person_6dc8444b_key(key, (value1,value2,value3,value4))
}

      def toPtrByte_Str_2991c0b2(new_value: Str)(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
val key = new_value.key
val size = strlen(new_value.string).toInt
val value1 = alloc[Byte](size)
strcpy(value1, new_value.string )
(key, size, value1)
}
def toPtrByte_Job_5a5c0f21(new_value: Job)(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
val key = new_value.key
val size = sizeJob
val value1 = alloc[Byte](size)
intcpy(value1, new_value.size , sizeInt)
val value2 = value1 + sizeInt
longcpy(value2, new_value.enterpriseId, sizeLong)
val value3 = value2 + sizeLong
(key, size, value1)
}
def toPtrByte_Person_7c2b85ce(new_value: Person)(implicit zone: Zone): (Long, Int, Ptr[Byte]) = {
val key = new_value.key
val size = sizePerson
val value1 = alloc[Byte](size)
intcpy(value1, new_value.salary , sizeInt)
val value2 = value1 + sizeInt
longcpy(value2, new_value.nameId, sizeLong)
val value3 = value2 + sizeLong
intcpy(value3, new_value.age , sizeInt)
val value4 = value3 + sizeInt
longcpy(value4, new_value.jobId, sizeLong)
val value5 = value4 + sizeLong
(key, size, value1)
}

      def init_Job_25165d97_key(key: Long, params: Tuple2[Int, Long])(implicit zone: Zone): Job = {
val new_val = new Job(key, params._1,params._2, null)
new_val
}
def init_Job_25165d97_key(params: Tuple2[Int, Long])(implicit zone: Zone): Job = {
val new_val = new Job(Job_table_6a0fde14.size, params._1,params._2, null)
put_Job_5b185a4f(new_val)
new_val
}
def init_Job_25165d97(key: Long, params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
val new_val = new Job(key, params._1,params._2.key, params._2)
new_val
}
def init_Job_25165d97(params: Tuple2[Int, Str])(implicit zone: Zone): Job = {
val new_val = new Job(Job_table_6a0fde14.size, params._1,params._2.key, params._2)
put_Job_5b185a4f(new_val)
new_val
}
def init_Person_6dc8444b_key(key: Long, params: Tuple4[Int, Long, Int, Long])(implicit zone: Zone): Person = {
val new_val = new Person(key, params._1,params._2, null,params._3,params._4, null)
new_val
}
def init_Person_6dc8444b_key(params: Tuple4[Int, Long, Int, Long])(implicit zone: Zone): Person = {
val new_val = new Person(Person_table_2ec2a849.size, params._1,params._2, null,params._3,params._4, null)
put_Person_22641e63(new_val)
new_val
}
def init_Person_6dc8444b(key: Long, params: Tuple4[Int, Str, Int, Job])(implicit zone: Zone): Person = {
val new_val = new Person(key, params._1,params._2.key, params._2,params._3,params._4.key, params._4)
new_val
}
def init_Person_6dc8444b(params: Tuple4[Int, Str, Int, Job])(implicit zone: Zone): Person = {
val new_val = new Person(Person_table_2ec2a849.size, params._1,params._2.key, params._2,params._3,params._4.key, params._4)
put_Person_22641e63(new_val)
new_val
}
def init_Str_1a8d36b(key: Long, param: CString)(implicit zone: Zone): Str = {
val new_val = new Str(key, param)
new_val
}
def init_Str_1a8d36b(param: CString)(implicit zone: Zone): Str = {
val new_val = new Str(Str_table_59c9d4bf.size, param)
put_Str_1df86d2d(new_val)
new_val
}

      def isMinor_7e99e44f(this_4d9cf4c: Person)(implicit zone: Zone): Boolean = {
  val x_0 = age_2243162b(this_4d9cf4c);
  x_0.<(18)
}

      def charAt_1b934752(params: Tuple2[Str, Long])(implicit zone: Zone): Byte = charAt(params._1.string,params._2)
def strlen_3db39a0b(param: Str)(implicit zone: Zone): Long = strlen(param.string)

      def age_2243162b(this_4d9cf4c: Person)(implicit zone: Zone): Int = {
this_4d9cf4c.age 
}
def job_320da484(this_4d9cf4c: Person)(implicit zone: Zone): Job = {
if (this_4d9cf4c.job  == null) {
this_4d9cf4c.job  = get_Job_405a2c3d(this_4d9cf4c.jobId)
}
this_4d9cf4c.job 
}
def salary_134f030c(this_4d9cf4c: Person)(implicit zone: Zone): Int = {
this_4d9cf4c.salary 
}
def string_1508360d(this_12c66d32: Str)(implicit zone: Zone): CString = {
this_12c66d32.string 
}
def size_8d3962d(this_780e4163: Job)(implicit zone: Zone): Int = {
this_780e4163.size 
}
def name_57a52a37(this_4d9cf4c: Person)(implicit zone: Zone): Str = {
if (this_4d9cf4c.name  == null) {
this_4d9cf4c.name  = get_Str_8c2cc0b(this_4d9cf4c.nameId)
}
this_4d9cf4c.name 
}
def enterprise_3c9904ac(this_780e4163: Job)(implicit zone: Zone): Str = {
if (this_780e4163.enterprise  == null) {
this_780e4163.enterprise  = get_Str_8c2cc0b(this_780e4163.enterpriseId)
}
this_780e4163.enterprise 
}

      def `age_=_76c1d51d`(this_4d9cf4c: Person, age : Int)(implicit zone: Zone): Unit = {
this_4d9cf4c.age  = age 
}
def `size_=_3158903c`(this_780e4163: Job, size : Int)(implicit zone: Zone): Unit = {
this_780e4163.size  = size 
}
def `job_=_5619cc5a`(this_4d9cf4c: Person, job : Job)(implicit zone: Zone): Unit = {
this_4d9cf4c.jobId = job .key
this_4d9cf4c.job  = job 
}
def `salary_=_1c361ea0`(this_4d9cf4c: Person, salary : Int)(implicit zone: Zone): Unit = {
this_4d9cf4c.salary  = salary 
}
def `enterprise_=_64275906`(this_780e4163: Job, enterprise : Str)(implicit zone: Zone): Unit = {
this_780e4163.enterpriseId = enterprise .key
this_780e4163.enterprise  = enterprise 
}
def `name_=_110becce`(this_4d9cf4c: Person, name : Str)(implicit zone: Zone): Unit = {
this_4d9cf4c.nameId = name .key
this_4d9cf4c.name  = name 
}

      def allOld: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = salary_134f030c(x_7);
      val x_9 = name_57a52a37(x_7);
      val x_10 = age_2243162b(x_7);
      val x_11 = job_320da484(x_7);
      val x_12 = init_Person_6dc8444b(scala.Tuple4(x_8, x_9, x_10.+(100), x_11));
      val x_13 = res_0;
      res_0 = x_13.+(1);
      true
    })
  }) 
    {
      val x_14 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_14
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_15 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_15
}
}
def joinQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      true
    })
  }) 
    {
      val x_8 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_8
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_9 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_9
}
}
def sizeStrQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Str_table_59c9d4bf.cursorOpen();
  val x_2 = Str_table_59c9d4bf.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Str_1fb98f6f(x_5._1, x_6._2);
      val x_8 = res_0;
      res_0 = x_8.+(1);
      true
    })
  }) 
    {
      val x_9 = Str_table_59c9d4bf.next(cursor_1);
      v_3 = x_9
    }
  ;
  Str_table_59c9d4bf.cursorClose(cursor_1);
  val x_10 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_10
}
}
def allOld2: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = age_2243162b(x_7);
      `age_=_76c1d51d`(x_7, x_8.+(10));
      val x_9 = res_0;
      res_0 = x_9.+(1);
      true
    })
  }) 
    {
      val x_10 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_10
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_11 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_11
}
}
def sizes: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = salary_134f030c(x_7);
      val x_9 = name_57a52a37(x_7);
      val x_10 = name_57a52a37(x_7);
      val x_11 = strlen_3db39a0b(x_10);
      val x_12 = job_320da484(x_7);
      val x_13 = x_11.asInstanceOf[scala.Long].toInt;
      val x_14 = init_Person_6dc8444b(scala.Tuple4(x_8, x_9, x_13, x_12));
      val x_15 = res_0;
      res_0 = x_15.+(1);
      true
    })
  }) 
    {
      val x_16 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_16
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_17 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_17
}
}
def allOld3: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = toCString_30e96c31("Test");
      val x_9 = init_Str_1a8d36b(x_8);
      val x_10 = age_2243162b(x_7);
      val x_11 = job_320da484(x_7);
      val x_12 = init_Person_6dc8444b(scala.Tuple4(0, x_9, x_10.+(100), x_11));
      val x_13 = res_0;
      res_0 = x_13.+(1);
      true
    })
  }) 
    {
      val x_14 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_14
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_15 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_15
}
}
def sizePersonQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = res_0;
      res_0 = x_8.+(1);
      true
    })
  }) 
    {
      val x_9 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_9
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_10 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_10
}
}
def insertions: Unit = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  val x_0 = toCString_30e96c31("EPFL");
  val x_1 = init_Str_1a8d36b(x_0);
  val x_2 = init_Job_25165d97(scala.Tuple2(10000, x_1));
  val x_3 = toCString_30e96c31("Lucien");
  val x_4 = init_Str_1a8d36b(x_3);
  val x_5 = init_Person_6dc8444b(scala.Tuple4(1000, x_4, 21, x_2));
  val x_6 = toCString_30e96c31("John");
  val x_7 = init_Str_1a8d36b(x_6);
  val x_8 = init_Person_6dc8444b(scala.Tuple4(100, x_7, 16, x_2));
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose()
}
}
def allMinors: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = isMinor_7e99e44f(x_7);
      if (x_8.asInstanceOf[scala.Boolean])
        {
          val x_9 = res_0;
          res_0 = x_9.+(1);
          true
        }
      else
        true
    })
  }) 
    {
      val x_10 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_10
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_11 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_11
}
}
def sizeJobQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Job_table_6a0fde14.cursorOpen();
  val x_2 = Job_table_6a0fde14.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Job_7331a1c6(x_5._1, x_6._2);
      val x_8 = res_0;
      res_0 = x_8.+(1);
      true
    })
  }) 
    {
      val x_9 = Job_table_6a0fde14.next(cursor_1);
      v_3 = x_9
    }
  ;
  Job_table_6a0fde14.cursorClose(cursor_1);
  val x_10 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_10
}
}
def charAtQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = age_2243162b(x_7);
      if (x_8.==(21))
        delete_Person_d0db2ed(x_7)
      else
        ();
      val x_9 = salary_134f030c(x_7);
      val x_10 = name_57a52a37(x_7);
      val x_11 = charAt_1b934752(scala.Tuple2(x_10, 2L));
      val x_12 = toCString_30e96c31(x_11.asInstanceOf[scala.Byte].toString());
      val x_13 = init_Str_1a8d36b(x_12);
      val x_14 = age_2243162b(x_7);
      val x_15 = job_320da484(x_7);
      val x_16 = init_Person_6dc8444b(scala.Tuple4(x_9, x_13, x_14, x_15));
      val x_17 = res_0;
      res_0 = x_17.+(1);
      true
    })
  }) 
    {
      val x_18 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_18
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_19 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_19
}
}
def mapQuery: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val row_8 = job_320da484(x_7);
      val row_9 = size_8d3962d(row_8);
      val row_10 = row_9.+(100);
      val x_11 = res_0;
      res_0 = x_11.+(1);
      true
    })
  }) 
    {
      val x_12 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_12
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_13 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_13
}
}
def sumAges: Int = Zone { implicit zone => 
{
  LMDBTable.txnBegin();
  Str_table_59c9d4bf.dbiOpen();
  Person_table_2ec2a849.dbiOpen();
  Job_table_6a0fde14.dbiOpen();
  var res_0: scala.Int = 0;
  val cursor_1 = Person_table_2ec2a849.cursorOpen();
  val x_2 = Person_table_2ec2a849.first(cursor_1);
  var v_3: scala.Tuple2[scala.Long, Ptr[scala.Byte]] = x_2;
  while ({
    val x_4 = v_3;
    x_4._2.!=(null).&&({
      val x_5 = v_3;
      val x_6 = v_3;
      val x_7 = fromPtrByte_Person_134b11ae(x_5._1, x_6._2);
      val x_8 = res_0;
      val x_9 = age_2243162b(x_7);
      res_0 = x_9.+(x_8);
      true
    })
  }) 
    {
      val x_10 = Person_table_2ec2a849.next(cursor_1);
      v_3 = x_10
    }
  ;
  Person_table_2ec2a849.cursorClose(cursor_1);
  val x_11 = res_0;
  LMDBTable.txnCommit();
  Str_table_59c9d4bf.dbiClose();
  Person_table_2ec2a849.dbiClose();
  Job_table_6a0fde14.dbiClose();
  x_11
}
}
    }
    
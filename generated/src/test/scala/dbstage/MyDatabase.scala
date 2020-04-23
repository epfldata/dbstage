
    import scala.collection.mutable
    import scala.scalanative.unsafe._
    import lib.string._
    import lib.str._
    import lib.LMDBTable
    import lib.LMDBTable._

    object MyDatabase {
      type PersonKey = Long
type PersonData = CStruct2[CString, Int]
type Person = Ptr[PersonData]
lazy val Person_table_7febee50 = new LMDBTable[Person](init_environment("Person"))


      def get_Person_4a66a83(table: LMDBTable[Person], key: Long)(implicit zone: Zone): Person = {
table.get(key)
}

      def put_Person_55d5f68b(table: LMDBTable[Person], fields: Person)(implicit zone: Zone): Unit = {
val key = table.size
val size0 = 0l
val size1 = strlen(fields._1)
val size2 = sizeof[Int]
val size = size1+size2
val value0 = alloc[Byte](size)
val value1 = value0 + size0
strcpy(value1, fields._1)
val value2 = value1 + size1
intcpy(value2, fields._2, size2)
val value = value0
table.put(key, size, value)
}

      def init_Str_f4b87c5(str: String)(implicit zone: Zone): CString = toCString(str)

      def strlen_66b6d65e(param: CString)(implicit zone: Zone): Long = strlen(param)
def charAt_5b8117bd(params: Tuple2[CString, Long])(implicit zone: Zone): Byte = charAt(params._1,params._2)

      def init_Person_3e0dd1ed(params: Tuple2[CString, Int])(implicit zone: Zone): Person = {
val this_5ee5206a: Person = alloc[PersonData]
this_5ee5206a._1 = params._1
this_5ee5206a._2 = params._2
this_5ee5206a
}

      def isMinor_1b9b6559(this_5ee5206a: Person)(implicit zone: Zone): Boolean = {
  val x_0 = age_208b256f(this_5ee5206a);
  x_0.<(18)
}

      def age_208b256f(this_5ee5206a: Person)(implicit zone: Zone): Int = this_5ee5206a._2
def name_ddbcb1a(this_5ee5206a: Person)(implicit zone: Zone): CString = this_5ee5206a._1

      def `name_=_7880adb`(this_5ee5206a: Person, name : CString)(implicit zone: Zone): Unit = this_5ee5206a._1 = name 
def `age_=_6108fb81`(this_5ee5206a: Person, age : Int)(implicit zone: Zone): Unit = this_5ee5206a._2 = age 

      def sizes = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7febee50.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = get_Person_4a66a83(Person_table_7febee50, i_4);
      val x_6 = name_ddbcb1a(x_5);
      val x_7 = name_ddbcb1a(x_5);
      val x_8 = strlen_66b6d65e(x_7);
      val x_9 = init_Person_3e0dd1ed(scala.Tuple2(x_6, x_8.toInt));
      val x_10 = res_0;
      res_0 = x_10.+(1);
      true
    })
  }) 
    {
      val x_11 = i_2;
      i_2 = x_11.+(1)
    }
  ;
  res_0
}
}
def charAtQuery = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7febee50.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = get_Person_4a66a83(Person_table_7febee50, i_4);
      val x_6 = name_ddbcb1a(x_5);
      val x_7 = charAt_5b8117bd(scala.Tuple2(x_6, 2L));
      val x_8 = init_Str_f4b87c5(x_7.toString());
      val x_9 = age_208b256f(x_5);
      val x_10 = init_Person_3e0dd1ed(scala.Tuple2(x_8, x_9));
      val x_11 = res_0;
      res_0 = x_11.+(1);
      true
    })
  }) 
    {
      val x_12 = i_2;
      i_2 = x_12.+(1)
    }
  ;
  res_0
}
}
def allMinors = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7febee50.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = get_Person_4a66a83(Person_table_7febee50, i_4);
      val x_6 = isMinor_1b9b6559(x_5);
      if (x_6.asInstanceOf[scala.Boolean])
        {
          val x_7 = res_0;
          res_0 = x_7.+(1);
          true
        }
      else
        true
    })
  }) 
    {
      val x_8 = i_2;
      i_2 = x_8.+(1)
    }
  ;
  res_0
}
}
def allOld = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7febee50.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = get_Person_4a66a83(Person_table_7febee50, i_4);
      val x_6 = init_Str_f4b87c5("Test");
      val x_7 = age_208b256f(x_5);
      val x_8 = init_Person_3e0dd1ed(scala.Tuple2(x_6, x_7.+(100)));
      val x_9 = res_0;
      res_0 = x_9.+(1);
      true
    })
  }) 
    {
      val x_10 = i_2;
      i_2 = x_10.+(1)
    }
  ;
  res_0
}
}
def allOld2 = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7febee50.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = get_Person_4a66a83(Person_table_7febee50, i_4);
      val x_6 = age_208b256f(x_5);
      `age_=_6108fb81`(x_5, x_6.+(10));
      val x_7 = res_0;
      res_0 = x_7.+(1);
      true
    })
  }) 
    {
      val x_8 = i_2;
      i_2 = x_8.+(1)
    }
  ;
  res_0
}
}
    }
    
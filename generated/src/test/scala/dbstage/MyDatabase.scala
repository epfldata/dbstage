
    import scala.collection.mutable
    import scala.scalanative.unsafe._
    import lib.string._
    import lib.str._

    object MyDatabase {
      def init_Str_b3cf49a(str: String)(implicit zone: Zone): CString = toCString(str)

      def strlen_36fdaeab(param: CString)(implicit zone: Zone): Long = strlen(param)
def charAt_3e89b9ed(params: Tuple2[CString, Long])(implicit zone: Zone): Byte = charAt(params._1,params._2)

      type PersonKey = Long
type PersonData = CStruct2[CString, Int]
type Person = Ptr[PersonData]

      def init_Person_772d1c96(params: Tuple2[CString, Int])(implicit zone: Zone): Person = {
val this_70016091: Person = alloc[PersonData]
this_70016091._1 = params._1
this_70016091._2 = params._2
this_70016091
}

      def isMinor_2807c330(this_70016091: Person)(implicit zone: Zone): Boolean = {
  val x_0 = age_5c3e516b(this_70016091);
  x_0.<(18)
}

      def name_7be2ac3b(this_70016091: Person)(implicit zone: Zone): CString = this_70016091._1
def age_5c3e516b(this_70016091: Person)(implicit zone: Zone): Int = this_70016091._2

      def `name_=_69ef0166`(this_70016091: Person, name : CString)(implicit zone: Zone): Unit = this_70016091._1 = name 
def `age_=_41e082e9`(this_70016091: Person, age : Int)(implicit zone: Zone): Unit = this_70016091._2 = age 

      val Person_table_7d20d573 = mutable.ArrayBuffer.empty[Person]

      def allOld2 = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7d20d573.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_7d20d573(i_4);
      val x_6 = age_5c3e516b(x_5);
      `age_=_41e082e9`(x_5, x_6.+(10));
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
def sizes = Zone { implicit zone => 
{
  var res_0: scala.Int = 0;
  val size_1 = Person_table_7d20d573.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_7d20d573(i_4);
      val x_6 = name_7be2ac3b(x_5);
      val x_7 = name_7be2ac3b(x_5);
      val x_8 = strlen_36fdaeab(x_7);
      val x_9 = init_Person_772d1c96(scala.Tuple2(x_6, x_8.toInt));
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
  val size_1 = Person_table_7d20d573.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_7d20d573(i_4);
      val x_6 = name_7be2ac3b(x_5);
      val x_7 = charAt_3e89b9ed(scala.Tuple2(x_6, 2L));
      val x_8 = init_Str_b3cf49a(x_7.toString());
      val x_9 = age_5c3e516b(x_5);
      val x_10 = init_Person_772d1c96(scala.Tuple2(x_8.asInstanceOf[CString], x_9));
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
  val size_1 = Person_table_7d20d573.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_7d20d573(i_4);
      val x_6 = isMinor_2807c330(x_5);
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
  val size_1 = Person_table_7d20d573.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_7d20d573(i_4);
      val x_6 = init_Str_b3cf49a("Test");
      val x_7 = age_5c3e516b(x_5);
      val x_8 = init_Person_772d1c96(scala.Tuple2(x_6.asInstanceOf[CString], x_7.+(100)));
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
    }
    
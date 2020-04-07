
    import scala.collection.mutable
    import scala.scalanative.unsafe._
    import lib.string._
    import lib.str._

    object MyDatabase {
      def init_Str_1e58a134(str: String): CString = toCString(str)

      def strlen_6ad4d2f(param: CString): Long = strlen(param)
def charAt_1cee59b3(params: Tuple2[CString, Long]): Byte = charAt(params._1,params._2)

      type Person = CStruct2[CString, Int]

      def init_Person_1524c52d(params: Tuple2[CString, Int]): Person = {
val this_54addf13: Person = alloc[Person]
this_54addf13._1 = params._1
this_54addf13._2 = params._2
this_54addf13
}

      def isMinor_bcb503c(this_54addf13: Person): Boolean = {
  val x_0 = age_16514789(this_54addf13);
  x_0.<(18)
}

      def name_4b9bd792(this_54addf13: Person): CString = this_54addf13._1
def age_16514789(this_54addf13: Person): Int = this_54addf13._2

      def `name_=_1f622073`(this_54addf13: Person, name : CString): Unit = this_54addf13._1 = name 
def `age_=_79572e92`(this_54addf13: Person, age : Int): Unit = this_54addf13._2 = age 

      val Person_table_18a2d083 = mutable.ArrayBuffer.empty[Person]

      def sizes = {
  var res_0: scala.Int = 0;
  val size_1 = Person_table_18a2d083.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_18a2d083(i_4);
      val x_6 = name_4b9bd792(x_5);
      val x_7 = name_4b9bd792(x_5);
      val x_8 = strlen_6ad4d2f(x_7);
      val x_9 = init_Person_1524c52d(scala.Tuple2(x_6, x_8.toInt));
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
def charAtQuery = {
  var res_0: scala.Int = 0;
  val size_1 = Person_table_18a2d083.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_18a2d083(i_4);
      val x_6 = name_4b9bd792(x_5);
      val x_7 = charAt_1cee59b3(scala.Tuple2(x_6, 2L));
      val x_8 = init_Str_1e58a134(x_7.toString());
      val x_9 = age_16514789(x_5);
      val x_10 = init_Person_1524c52d(scala.Tuple2(x_8.asInstanceOf[CString], x_9));
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
def allMinors = {
  var res_0: scala.Int = 0;
  val size_1 = Person_table_18a2d083.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_18a2d083(i_4);
      val x_6 = isMinor_bcb503c(x_5);
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
def allOld = {
  var res_0: scala.Int = 0;
  val size_1 = Person_table_18a2d083.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_18a2d083(i_4);
      val x_6 = init_Str_1e58a134("Test");
      val x_7 = age_16514789(x_5);
      val x_8 = init_Person_1524c52d(scala.Tuple2(x_6.asInstanceOf[CString], x_7.+(100)));
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
def allOld2 = {
  var res_0: scala.Int = 0;
  val size_1 = Person_table_18a2d083.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_table_18a2d083(i_4);
      val x_6 = age_16514789(x_5);
      `age_=_79572e92`(x_5, x_6.+(10));
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
    
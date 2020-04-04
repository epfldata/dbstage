
    import scala.collection.mutable
    import scala.scalanative.unsafe._

    object MyDatabase {
      type Person = CStruct2[CString, CInt]
      def init_Person_37cda7ee(params: Tuple2[String, Int]): Person = {
val this_744ab078: Person = alloc[Person]
this_744ab078._1 = params._1
this_744ab078._2 = params._2
this_744ab078
}
      def isMinor_3540840e(this_744ab078: Person): Boolean = {
  val x_0 = age_cc56b2d(this_744ab078);
  x_0.<(18)
}
      def name_424f9caf(this_744ab078: Person): String = this_744ab078._1
def age_cc56b2d(this_744ab078: Person): Int = this_744ab078._2
      def `name_=_28b8680d`(this_744ab078: Person, name : String): Unit = this_744ab078._1 = name 
def `age_=_231ba1dc`(this_744ab078: Person, age : Int): Unit = this_744ab078._2 = age 
      val personsTable_2ed8d3e1 = mutable.ArrayBuffer.empty[Person]
      def allOld = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_2ed8d3e1.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_2ed8d3e1(i_4);
      val x_6 = name_424f9caf(x_5);
      val x_7 = age_cc56b2d(x_5);
      val x_8 = init_Person_37cda7ee(scala.Tuple2(x_6, x_7.+(100)));
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
  val size_1 = personsTable_2ed8d3e1.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_2ed8d3e1(i_4);
      val x_6 = age_cc56b2d(x_5);
      `age_=_231ba1dc`(x_5, x_6.+(10));
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
def allMinors = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_2ed8d3e1.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_2ed8d3e1(i_4);
      val x_6 = isMinor_3540840e(x_5);
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
    
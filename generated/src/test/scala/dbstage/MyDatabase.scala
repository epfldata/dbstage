
    import scala.collection.mutable
    import scala.scalanative.unsafe._

    object MyDatabase {
      type Person = CStruct2[CString, CInt]
      def init_Person_2d6555d(params: Tuple2[String, Int]): Person = {
val this_2573fd6d: Person = alloc[Person]
this_2573fd6d._1 = params._1
this_2573fd6d._2 = params._2
this_2573fd6d
}
      def isMinor_637321ac(this_2573fd6d: Person): Boolean = {
  val x_0 = age_1f756623(this_2573fd6d);
  x_0.<(18)
}
      def name_4a949cc(this_2573fd6d: Person): String = this_2573fd6d._1
def age_1f756623(this_2573fd6d: Person): Int = this_2573fd6d._2
      def `name_=_767a5a11`(this_2573fd6d: Person, name : String): Unit = this_2573fd6d._1 = name 
def `age_=_4ef7d319`(this_2573fd6d: Person, age : Int): Unit = this_2573fd6d._2 = age 
      val Person_data_6a328dfa = mutable.ArrayBuffer.empty[Person]
      def allOld = {
  var res_0: scala.Int = 0;
  val size_1 = Person_data_6a328dfa.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_data_6a328dfa(i_4);
      val x_6 = name_4a949cc(x_5);
      val x_7 = age_1f756623(x_5);
      val x_8 = init_Person_2d6555d(scala.Tuple2(x_6, x_7.+(100)));
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
  val size_1 = Person_data_6a328dfa.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_data_6a328dfa(i_4);
      val x_6 = age_1f756623(x_5);
      `age_=_4ef7d319`(x_5, x_6.+(10));
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
  val size_1 = Person_data_6a328dfa.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = Person_data_6a328dfa(i_4);
      val x_6 = isMinor_637321ac(x_5);
      if (x_6.asInstanceOf[scala.Boolean]) // Another asInstanceOf
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
    
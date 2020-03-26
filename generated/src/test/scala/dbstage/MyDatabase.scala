
    import scala.collection.mutable
    object MyDatabase {
      case class Person (  name : String,   age : Int)
      def isMinor_4cb0f030(this_53f81b46: Person): Boolean = {
  val x_0 = this_53f81b46.age;
  x_0.<(18)
}
      def age_2d71e45e(this_53f81b46: Person): Int = this_53f81b46._2
def name_3e9d09d7(this_53f81b46: Person): String = this_53f81b46._1
      val personsTable_500db6 = mutable.ArrayBuffer.empty[Person]
      def insert = personsTable_500db6.append(p_1580e2c4)
def allMinors = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_500db6.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_500db6(i_4);
      val x_6 = isMinor_4cb0f030(x_5);
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
  val size_1 = personsTable_500db6.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_500db6(i_4);
      val x_6 = name_3e9d09d7(x_5);
      val x_7 = age_2d71e45e(x_5);
      val x_8 = x_7.asInstanceOf[scala.Int].+(100);
      val x_9 = new Person(x_6.asInstanceOf[java.lang.String], x_8);
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
    
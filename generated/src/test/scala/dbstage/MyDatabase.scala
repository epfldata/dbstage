
    import scala.collection.mutable
    object MyDatabase {
      case class Person (  name : String,   age : Int)
      def isMinor_35b07492(this_1f480e85: Person): Boolean = {
  val x_0 = this_1f480e85.age;
  x_0.<(18)
}
      def name_4e3ae1b8(this_1f480e85: Person): String = this_1f480e85._1
def age_6e7f6fa2(this_1f480e85: Person): Int = this_1f480e85._2
      def `name_=_30404468`(this_1f480e85: Person, name : String): Unit = this_1f480e85._1 = name 
def `age_=_29ee54f9`(this_1f480e85: Person, age : Int): Unit = this_1f480e85._2 = age 
      val personsTable_c905c2b = mutable.ArrayBuffer.empty[Person]
      def allOld2 = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_c905c2b.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_c905c2b(i_4);
      val x_6 = age_6e7f6fa2(x_5);
      val x_7 = x_6.asInstanceOf[scala.Int].+(10); // Why?
      `age_=_29ee54f9`.apply(x_5, x_7); // How to remove apply? make a run of Optimizer
      val x_8 = res_0;
      res_0 = x_8.+(1);
      true
    })
  }) 
    {
      val x_9 = i_2;
      i_2 = x_9.+(1)
    }
  ;
  res_0
}
def allMinors = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_c905c2b.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_c905c2b(i_4);
      val x_6 = isMinor_35b07492(x_5);
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
def insert = personsTable_c905c2b.append(p_50c040b6)
def allOld = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_c905c2b.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_c905c2b(i_4);
      val x_6 = name_4e3ae1b8(x_5);
      val x_7 = age_6e7f6fa2(x_5);
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
    
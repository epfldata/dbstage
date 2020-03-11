
    import scala.collection.mutable
    object MyDatabase {
      class Person {
  val name : String = null;
  val age : Int = 0;

}
      def isMinor_57c263e7(this_792dd012: Person): Boolean = {
  val x_0 = this_792dd012.age;
  x_0.<(18)
}
      val personsTable_7da930fd = mutable.ArrayBuffer.empty[Person]
      def allOld = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_7da930fd.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_7da930fd(i_4);
      val x_6 = x_5.name;
      val x_7 = x_5.age;
      val x_8 = new Person(x_6, x_7.+(100));
      val x_9 = res_0;
      val x_12 = ((x$1_10: Person, acc_11: scala.Int) => acc_11.+(1)).apply(x_8, x_9);
      res_0 = x_12;
      true
    })
  }) 
    {
      val x_13 = i_2;
      i_2 = x_13.+(1)
    }
  ;
  res_0
}
def allMinors = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_7da930fd.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_7da930fd(i_4);
      val x_6 = isMinor_57c263e7(x_5);
      if (x_6.asInstanceOf[scala.Boolean])
        {
          val x_7 = res_0;
          val x_10 = ((x$1_8: Person, acc_9: scala.Int) => acc_9.+(1)).apply(x_5, x_7);
          res_0 = x_10;
          true
        }
      else
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
    
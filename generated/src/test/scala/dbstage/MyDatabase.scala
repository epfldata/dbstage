
    import scala.collection.mutable
    object MyDatabase {
      class Person {
  val name : String = null;
  val age : Int = 0;

}
      def isMinor_47251f6d(this_505e0477: Person): Boolean = {
  val x_0 = this_505e0477.age;
  x_0.<(18)
}
      val personsTable_3cef0eba = mutable.ArrayBuffer.empty[Person]
      def allMinors = {
  var res_0: scala.Int = 0;
  val size_1 = personsTable_3cef0eba.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = personsTable_3cef0eba(i_4);
      val x_6 = isMinor_47251f6d(x_5);
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
    
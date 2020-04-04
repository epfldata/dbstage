
    import scala.collection.mutable
    import scala.scalanative.unsafe._

    object MyDatabase {
      type Person = CStruct2[CString, CInt]
      def init_Person_146237e7(params: Tuple2[String, Int]): Person = {
val this_b5931c0: Person = alloc[Person]
this_b5931c0._1 = params._1
this_b5931c0._2 = params._2
this_b5931c0
}
      def isMinor_3386f600(this_b5931c0: Person): Boolean = {
  val x_0 = age_3dafc4ad(this_b5931c0);
  x_0.<(18)
}
      def name_7811b49e(this_b5931c0: Person): String = this_b5931c0._1
def age_3dafc4ad(this_b5931c0: Person): Int = this_b5931c0._2
      def `name_=_5ee28fb3`(this_b5931c0: Person, name : String): Unit = this_b5931c0._1 = name 
def `age_=_323a1447`(this_b5931c0: Person, age : Int): Unit = this_b5931c0._2 = age 
      val MyDatabase_3e34e1d2 = mutable.ArrayBuffer.empty[Person]
      def allOld2 = {
  var res_0: scala.Int = 0;
  val size_1 = MyDatabase_3e34e1d2.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = MyDatabase_3e34e1d2(i_4);
      val x_6 = age_3dafc4ad(x_5);
      `age_=_323a1447`(x_5, x_6.+(10));
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
  val size_1 = MyDatabase_3e34e1d2.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = MyDatabase_3e34e1d2(i_4);
      val x_6 = isMinor_3386f600(x_5);
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
  val size_1 = MyDatabase_3e34e1d2.size;
  var i_2: scala.Int = 0;
  while ({
    val x_3 = i_2;
    x_3.<(size_1).&&({
      val i_4 = i_2;
      val x_5 = MyDatabase_3e34e1d2(i_4);
      val x_6 = name_7811b49e(x_5);
      val x_7 = age_3dafc4ad(x_5);
      val x_8 = init_Person_146237e7(scala.Tuple2(x_6, x_7.+(100)));
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
    
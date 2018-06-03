package gen.example

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

object MyApp extends Embedding.ProgramGen with Vectors with App {
  val root = Root.thisEnclosingInstance
  
  val V3 = Vector[Int](3)
  import V3.{Self => V3T}
  //println(V3.prod)
  println(V3.showCode)
  
  object Main extends Class {
    val curried = method(V3.prod.inlined)
    val curried2 = method(c"(a:V3T,b:V3T) => ${V3.prod.inlined}(a)(b) + ${V3.prod.inlined}(a)(b)")
  }
  //import Main.{Self=>MainT}
  //println(Main.curried.effect)
  println(Main.showCode)
  // ^ FIXME scheduling fail in curried2: some thinds are shceduled and never used!
  
  //println(dbg_code"${Main.curried}(${toRef(Main())})")
  println(c"${Main.curried}(${Main()})")
  //println(dbg_code"${Main.curried}(${V3(c"0",c"1",c"2")})")  // note: nice error
  println(c"${Main.curried.inlined}(${Main()})(${V3(c"0",c"1",c"2")})") // nice, vector is partially evaluated
  
  //println(c{(v: V3T) => $(V3.xs(1))(v)+1})
  //println(c"(v: V3T) => ${V3.xs(0)}(v)+1")
  //println(V3.prod.inlined)
  
  //val VD4 = Vector[Double](4)
  //println(VD4.showCode)
  
  println(code"println(1337)")
  println(code"(x:Int) => x*x".rep.effect)
  
  println("Done.")
  
}

/*


FIXedME: widening of type paths is actually DANGEROUS!!
I could do this:
  c"${Main.curried}(${V3(c"0",c"1",c"2")})"
because
  Main.Self was widened to gen.example.MyApp.Class[Any]#Self
EDIT: was caused by old toRef implicit method being defined on type projections...


FIXME: SchedulingANF produces duplicated values
AS in:
  def curried2(a_$22: gen.example.MyApp.Vector_3_Int, b_$22: gen.example.MyApp.Vector_3_Int): Int = {
    val sch_0 = a_$22.xs;
    val sch_1 = b_$22.xs_2;
    val sch_2 = b_$22.xs;
    val sch_3 = a_$22.xs_3;
    val sch_4 = b_$22.xs_3;
    val sch_5 = a_$22.xs_2;
    val x_6 = sch_0.*(sch_2);
    val x_7 = sch_5.*(sch_1);
    val x_8 = sch_3.*(sch_4);
    val x_9 = sch_0.*(sch_2);
    val sch_10 = (0).+(x_9);
    val x_11 = sch_5.*(sch_1);
    val sch_12 = sch_10.+(x_11);
    val x_13 = sch_3.*(sch_4);
    val sch_14 = sch_12.+(x_13);
    sch_14.+(sch_14)
  }
EDIT: in fact, the problem is that multiplication somehows seems to not be pure...

*/
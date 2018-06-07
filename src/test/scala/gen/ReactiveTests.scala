package gen

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

import gen.example.Reactive

object ReactGen extends Embedding.ProgramGen with Reactive {
  val root = Root.thisEnclosingInstance
  
  object Counter extends StateMachine {
    val count = new Signal(c"0")
    val inc = method(c"${count.curVal} := ${count.curVal}.! + 1")
  }
  
}
import ReactGen._

object ReactiveTests extends App {
  
  //object A extends ReactGen.StateMachine
  //println(A.defName)
  //println((new ReactGen.StateMachine).defName)
  
  println(Counter.showCode)
  println(c"${Counter.count.curVal}(${Counter()})"/*.rep.dfn*/)
  //println(c"if(readInt>0)??? else println(???)")
  //println(c"while(readInt>0)???")
  
  //c"(squid.lib.MutVarProxy[Int](readInt,a=> ???):squid.lib.MutVar[Int]).!" alsoApply println match {
  //  case c"squid.lib.MutVarProxy[$at]($a,$f).!" => println(a,f)
  //}
  //val ri = c"readInt"
  //val f = c"(a:Int)=> ???"
  //val mv=c"squid.lib.MutVarProxy[Int]($ri,$f)"
  //base.debugFor(c"$mv.!")
  
  
}

package gen

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

object MSSGen extends Embedding.ProgramGen {
  val root = Root.thisEnclosingInstance
  
  //import scala.language.dynamics
  //trait MyDynamic extends Dynamic { def selectDynamic(name: String): Int }
  object Weird extends Class {
    //type Self <: Dynamic { def selectDynamic(name: String): Int }
    //type Self <: MyDynamic
    //type Self <: ClassSelfType
    
    val lol = method(c"println('LOL.name)")
    
  }
  
}
import MSSGen._

object MethodsSyntaxSugar extends App {
  
  import Weird.{Self=>W}
  println(c"(w:W) => w.lol") // code"""((w_0: gen.MSSGen.Weird) => w_0.selectDynamic("lol"))"""
  
  
}

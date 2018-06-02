package gen

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

import Helper.Dummy

// this needs to work because it's used in `exprToScalaTree`; it requires Scala not matching invariant type-args as covariant
// note that a workaround can be used otherwise (just match the type and the term separately...)
object Debug extends App {
  
  //val p = code"Dummy[Int](0)"
  //val p = code"Dummy[Int](???)"
  val p = code"Dummy[Int=>Int](???)"
  def f = p alsoApply println match {
    //case code"Dummy[$t=>$s]($x):Dummy[t=>s]" =>
    case code"Dummy($x):Dummy[$t=>$s]" =>
  }
  f
  //base.debugFor(f)
  
  
}

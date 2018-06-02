package gen
package example

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

trait Vectors extends Embedding.ProgramGen {
  
  class Vector(n: Int) extends Class()("Vector"+n) {
    val xs = Array.fill(n)(param[Int])
    //val xs = Array.fill(n)(param[Int]).lift.andThen(_.getOrElse(lastWords("Out of bounds!")))  // not a Seq; can't iterate!
    //val prod = method(c"(that: Self) => ${xs.foldLeft(c"0" withContextOf that)((r,x) => c"$r + $x * ${x.toLambda}(?that:Self)")}")
    val prod = method {
      val that = Variable[Self]
      c"{ ($that) => ${xs.foldLeft[C[Int, Ctx & that.Ctx]](c"0")((r,x) => c"$r + $x * ${x.asLambda}($that)")} }"
    }
  }
  def Vector(n: Int) = new Vector(n: Int)
  
}

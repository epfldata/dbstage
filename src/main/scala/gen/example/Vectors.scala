package gen
package example

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

trait Num[T] {
  def zero: ClosedCode[T]
  def plus: ClosedCode[(T,T) => T]
  def prod: ClosedCode[(T,T) => T]
}
object Num {
  def apply[N: Num]: Num[N] = implicitly
  
  implicit object NumInt extends Num[Int] {
    def zero = c"0"
    def plus = c"(_:Int) + (_:Int)"
    def prod = c"(_:Int) * (_:Int)"
  }
  implicit object NumDouble extends Num[Double] {
    def zero = c"0.0"
    def plus = c"(_:Double) + (_:Double)"
    def prod = c"(_:Double) * (_:Double)"
  }
}

trait Vectors extends Embedding.ProgramGen {
  
  class Vector[N: CodeType: Num](n: Int) extends Class()(s"Vector_${n}_${typeRepOf[N]}") {
    val N = Num[N]
    val xs = Array.fill(n)(param[N])
    //val xs = Array.fill(n)(param[Int]).lift.andThen(_.getOrElse(lastWords("Out of bounds!")))  // not a Seq; can't iterate!
    //val prod = method(c"(that: Self) => ${xs.foldLeft(c"0" withContextOf that)((r,x) => c"$r + $x * ${x.toLambda}(?that:Self)")}")
    val prod = method {
      val that = Variable[Self]
      //c"{ ($that) => ${xs.foldLeft[C[Int, Ctx & that.Ctx]](c"0")((r,x) => c"$r + $x * ${x.asLambda}($that)")} }"
      c"{ ($that) => ${xs.foldLeft[C[N, Ctx & that.Ctx]](N.zero)((r,x) => c"${N.plus}($r, ${N.prod}($x, ${x.asLambda}($that)))")} }"
      // ^ would be nice to somehow allow syntax sugar so c"a + b" means c"${N.plus}(a, b)"
    }
    val lol = method[Int => Int](code"???") // also works as: method(code"??? : (Int=>Int)")
  }
  def Vector[N: CodeType: Num](n: Int) = new Vector[N](n: Int)
  
  // just to test the syntax; not properly wired yet:
  override def rewrite[T:CodeType,C] = {
    case code"1337" => code"-7331:T"
    case t => super.rewrite[T,C] apply t
  }
  // compare to:
  online.dbg_rewrite {
    case code"1337" => code"-7331"
  }
  
}

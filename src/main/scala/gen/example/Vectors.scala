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
    // ie:   = c"(a: Int, b: Int) => a + b"
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
  online.rewrite {
    case code"1337" => code"-7331"
  }
  
}

// example in slides:
object Vectors_0 extends App {
  class Vector(val x: Int, val y: Int, val z: Int) {
    def prod(that: Vector) = {
      x * that.x + y * that.y + z * that.z
    }
  }
  val v = new Vector(0,1,2)
  println(v prod v)
  //object Gen extends Embedding.ProgramGen with Vectors_0 {
  object Gen extends Embedding.ProgramGen { val root = Root.thisEnclosingInstance
    object Vector extends Class {
      //type Self <: ClassSelfType
      val x, y, z = param[Int]
      //val prod = method(c"""(that: Self) =>
      //  $x * ${x.asLambda}(that) + $y * ${y.asLambda}(that) + $z * ${z.asLambda}(that)
      //""")
      val prod = method(c"""(that: Self) => $x * that.x + $y * that.y + $z * that.z """)
    }
    import Vector.Self
    println(Vector.showCode)
    val v = Vector(c"0",c"1",c"2")
    //val f = c"(v: Vector.Self) => ${Vector.x}(v)"
    val f = c"(v: Vector.Self) => v.x"
    println(c"$f($v)") // code"0"
    //println(c"$v prod $v")
    //println(c"${Vector.prod}($v)($v)")
    println(c"$v.prod.apply($v)") // Note: apply needed here because .f(...) desulars to applyDynamic...
    println(c"${Vector.prod.inlined}($v)($v)") // code"(0).*(0).+((1).*(1)).+((2).*(2))"
  }
  Gen
}
//trait Vectors_0 extends Embedding.ProgramGen {
//  object Vector extends Class {
//    val x, y, z = param[Int]
//    val prod = method(c"""(that: Self) =>
//      $x * ${x.asLambda}(that)
//    """)
//  }
//}

object Vectors_1 extends App {
  class Vector(val xs: Int*) {
    def prod(that: Vector) = {
      assert(xs.size == that.xs.size)
      (xs,that.xs).zipped.map(_ * _).sum
    }
  }
  object Gen extends Embedding.ProgramGen { val root = Root.thisEnclosingInstance
    class Vector(n: Int) extends Class {
      val xs = Array.fill(n)(param[Int])
      val prod = method {
        val that = Variable[Self]
        c"{ ($that) => ${xs.foldLeft[C[Int, Ctx & that.Ctx]](c"0")((r,x) => 
          c"$r + $x * ${x.asLambda}($that)")} }"
          //dbg_code"$r + $x * $that.x")} }" // lol no, ofc not, there is no 'x' method in Vector!!
      }
    }
    object Vec2 extends Vector(2)
    import Vec2.Self
    println(Vec2.showCode)
  }
  Gen
  object Generated {
    class Vector(val xs: Int, val xs_2: Int) {
      def prod(that_$23: gen.example.Vectors_1.Generated.Vector): Int = {
        val x_0 = Vector.this.xs.*(that_$23.xs);
        val x_1 = Vector.this.xs_2.*(that_$23.xs_2);
        (0).+(x_0).+(x_1)
      }
    }
  }
}

object Vectors_2 extends App {
  
  object Gen extends Embedding.ProgramGen { val root = Root.thisEnclosingInstance
    object V3 extends Class
    class ColumnStore[C](cls: Class[C])
    object V3Store extends ColumnStore(V3)
    //val instance = field(V3Store())
    //c"${V3Store.insert}($instance)(${V3(c"0",c"1",c"2")})"
    //instance.insert(0,1,2) // stored in columns; never allocates vector
  }
  
}

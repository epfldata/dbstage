import cats.Monoid
import cats.Semigroup
import squid.utils._
import squid.ir.SimpleEffect
import squid.lib.transparencyPropagating

import scala.language.implicitConversions

package object dbstage extends EmbeddedDefs {
  
  def the[A <: AnyRef](implicit ev: A): ev.type = ev
  
  implicit class MonoidHelper(m: Monoid.type) {
    def instance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = new Monoid[A] {
      def empty = _empty
      def combine(x: A, y: A): A = _combine(x,y)
    }
  }
  
  // for use in embedded code:
  @transparencyPropagating
  def monoidInstance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = Monoid.instance(_empty)(_combine)
  @transparencyPropagating
  def semigroupInstance[A](_combine: (A,A) => A): Semigroup[A] = Semigroup.instance(_combine)
  
  @transparencyPropagating
  implicit def monoidSyntax[A:Monoid](self: A): MonoidSyntax[A] = new MonoidSyntax(self)
  @transparencyPropagating
  implicit def semigroupSyntax[A:Semigroup](self: A): SemigroupSyntax[A] = new SemigroupSyntax(self)
  
  
  @transparencyPropagating
  implicit def recordSyntax[A](self: A): RecordSyntax[A] = new RecordSyntax(self)
  
  
  import Embedding.Predef._
  
  def isPure(cde: OpenCode[Any]) = cde.rep.effect === SimpleEffect.Pure
  
  def show(cde: OpenCode[Any]) = cde.rep|>base.showRep
  
  
  def indentString(str: String) = {
    val lines = str.splitSane('\n')
    val pre = "| "
    lines map (Debug.GREY + pre + Console.RESET + _) mkString "\n"
  }
  
  //def consumeString(str: String, untilSep: Char): String = str.takeWhile(_ != untilSep)
  
  //def parseInt(str: String): Int = str.readInt
  
  def splitString(str: String, sep: Char): Iterator[String] = str.splitSane(sep).iterator
  
  
  
  
  def time[R](n: Int)(ktime: Long => Unit)(mkResult: => R): R = {  
    val t0 = System.nanoTime()
    val result = mkResult
    var i = 1
    while(i < n) {
      i += 1
      mkResult
    }
    val t1 = System.nanoTime()
    ktime((t1 - t0)/1000/1000)
    result
  }
  
  
  
  import scala.language.higherKinds
  
  sealed trait BoolT { self => type Not <: BoolT { type Not <: self.type } }
  object True extends BoolT  { type Not = False.type }
  object False extends BoolT { type Not = True.type }
  type True = True.type
  type False = False.type
  type ![+T<:BoolT] = T#Not
  
  def choice[B<:BoolT:Choice] = implicitly[Choice[B]] 
  def choose[B<:BoolT:Choice,F[_<:BoolT],R](x:F[B])(ifTrue: F[True] => R, ifFalse: F[False] => R) = 
    choice.apply[F](x)(ifTrue,ifFalse)
  //def ite[B<:BoolT:Choice,R](thn: => R)(els: => R) = choice[B] match { case True => thn case Flase => els }
  //def ite[B<:BoolT,R](thn: => R)(els: => R)(implicit b: B) = b match { case True => thn case False => els }
  
  
  /*
  // Note: AnyVal causes method resolution problem
  //implicit class BagOrderingSyntax[B,T,O](private val self: BagOrdering[B,T,O,True]) { //extends AnyVal {
  @transparencyPropagating
  implicit def bagOrderingSyntax[B,T,O:Ordering](self: BagOrdering[B,T,O,True])(implicit p: T ProjectsOn O) = new BagOrderingSyntax[B,T,O](self)
  class BagOrderingSyntax[B,T,O:Ordering](private val self: BagOrdering[B,T,O,True])(implicit p: T ProjectsOn O) { //extends AnyVal {
    @transparencyPropagating
    //def desc() = new BagOrdering[B,T,O,False](self.b, self.iterator.toBuffer.reverseIterator)
    // ^ note that this impl of desc()'s _ordered is not the one that will be used in the result of usual queries (obtained from the Monoid instance)
    def desc() = new BagOrdering[B,T,O,False](self.src, BagOrdering.orderIterator(self.src,false)(Ordering[O].on(p)))
    // ^ note that this impl of desc()'s _ordered is not the one that will be used in the result of usual queries (obtained from the Monoid instance)
  }
  */
  
  
}

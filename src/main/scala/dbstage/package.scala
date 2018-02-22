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
  
  @transparencyPropagating implicit def generalHelper[A](self: A) = new GeneralHelper(self)
  class GeneralHelper[A](private val self: A) extends AnyVal {
    @transparencyPropagating
    def normalize[R](implicit norm: R Normalizes A): R = norm(self)
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
  
  def showC(cde: OpenCode[Any]) = cde.rep|>base.showRep
  def showCT(cdeTyp: CodeType[_]) = cdeTyp.rep.tpe.toString|>trimPrefixes
  def trimPrefixes(str: String) = str
    .replaceAll("dbstage.example.","")
    .replaceAll("dbstage.","")
  
  
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
  
  
}

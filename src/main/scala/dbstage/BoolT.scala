package dbstage

import scala.language.higherKinds
import squid.utils.Bool

object BoolT {
  implicit def TrueEv = True
  implicit def FalseEv = False
}

//sealed trait BoolT { self => type Not <: BoolT { type Not = self.type } }
//object True extends BoolT  { type Not = False }
//object False extends BoolT { type Not = True }
//object True extends BoolT  { type Not = False.type }
//object False extends BoolT { type Not = True.type }
// ^ Note: works in 2.12
//object True extends BoolT  { self => type Not = False{type Not = self.type} }
//object False extends BoolT { self => type Not = True{type Not = self.type} }
// ^ creates illegal cyclic reference

//sealed trait BoolT { self => type Not <: BoolT { type Not <: self.type } }

sealed abstract class Choice[B<:BoolT] {
  def apply[F[_<:BoolT]](x:F[B]): Branch[F] //= new Branch[F](x:F[B])
  abstract class Branch[F[_<:BoolT]](x:F[B]) {
    def apply[R](ifTrue: F[True] => R, ifFalse: F[False] => R): R //= ???
  }
  def ite[R](thn: => R)(els: => R): R
  def reify: Bool
}
object Choice {
  implicit object TrueChoice extends Choice[True] {
    def apply[F[_<:BoolT]](x:F[True]) = new Branch[F](x) {
      def apply[R](ifTrue: F[True] => R, ifFalse: F[False] => R): R = ifTrue(x)
    }
    def ite[R](thn: => R)(els: => R) = thn
    def reify: Bool = true
  }
  implicit object FalseChoice extends Choice[False] {
    def apply[F[_<:BoolT]](x:F[False]) = new Branch[F](x) {
      def apply[R](ifTrue: F[True] => R, ifFalse: F[False] => R): R = ifFalse(x)
    }
    def ite[R](thn: => R)(els: => R) = els
    def reify: Bool = false
  }
}

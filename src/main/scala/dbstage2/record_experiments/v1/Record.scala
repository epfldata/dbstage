package dbstage2
package records.v1

import squid.lib.transparencyPropagating

import scala.language.higherKinds

sealed trait Record {
  type Rep
  def getRep = null.asInstanceOf[Rep]
  type Mk[T]
  //def mk(m: Mk) = ???
  
  //def mk: Mk[Rep] //= ???
  @transparencyPropagating
  def mk: Mk[Rep] = mkCPS(identity)
  def mkCPS[R](k: Rep => R): Mk[R]
  
  case class Access[F<:FieldBase](idx: Int, fun: Rep => F#Typ)
  //final implicit class FieldBaseOps[F<:FieldBase](val f:F) {
  //  def apply(r: Rep)(implicit acc: Access[f.type]): f.Typ = ???
  final implicit class FieldBaseOps[F<:FieldBase](f:F) {
    def apply(r: Rep)(implicit acc: Access[F]): F#Typ = acc.fun(r)
  }
}
object Record {
  implicit class RecordOps[FS<:Record](fs:FS) {
    def :: [F<:FieldBase with Singleton](f: F): F :: FS = new ::(f,fs)
    { 
      type Rep = (h.Typ,t.Rep); 
      def mkCPS[R](k: Rep => R): Mk[R] = h => t.mkCPS(tr => k((h,tr)))
      def head: Rep => h.Typ = _._1
      def tail: Rep => t.Rep = _._2
      def accessFirst: Access[h.type] = Access(0, head)
    }
  }
  
  /*
  //case class Access[F,T](idx: Int)
  case class Access[F,+T](idx: Int)
  implicit def accessFirst[H<:FieldBase with Singleton,T<:Record]: Access[H,H::T] = Access(0)
  //implicit def accessNext[H<:FieldBase with Singleton,T<:Record](implicit next: Access[H,T]): Access[H,H::T] = Access(next.idx+1)
  implicit def accessNext[N,H<:FieldBase with Singleton,T<:Record](implicit next: Access[N,T]): Access[N,H::T] = Access(next.idx+1)
  */
  //case class Access[F,+T](f: F)(fun: T => f.Typ)
  //case class Access[F<:FieldBase,+T](idx: Int, fun: T => F#Typ)
  //case class Access[F<:FieldBase,+T<:Record](idx: Int, fun: T => T#Rep => F#Typ)
  //implicit def accessFirst[H<:FieldBase with Singleton,T<:Record]: Access[H,H::T] = Access[H,H::T](0, ht => r => ht.head(r))
  //implicit def accessNext[N,H<:FieldBase with Singleton,T<:Record](implicit next: Access[N,T]): Access[N,H::T] = Access(next.idx+1, ht => next.fun(ht.tail))
  
}
import Record._

case object NoFields extends Record {
  type Rep = Unit
  type Mk[T] = T
  //def mk: Mk[Rep] = ()
  def mkCPS[R](k: Rep => R): Mk[R] = k(())
}
abstract case class ::[H<:FieldBase,T<:Record](h:H, t:T) extends Record {
  type Mk[T] = h.Typ => t.Mk[T]
  def head: Rep => h.Typ
  def tail: Rep => t.Rep
  
  implicit def accessFirst: Access[h.type]
  implicit def accessNext[H<:FieldBase with Singleton](implicit next: t.Access[H]): Access[H] = Access(next.idx+1, r => next.fun(tail(r)))
}
class FieldBase {
  type Typ
  ////def apply[T<:Record](s:T)(implicit acc: this.type Access T) = acc
  //def apply[T<:Record /*with Singleton*/](r: T#Rep)(implicit acc: this.type Access T) = acc
  //def apply[T<:Record /*with Singleton*/](r: T#Rep)(implicit acc: this.type Access T) = acc.fun
  //def apply[T<:Record /*with Singleton*/](r: T#Rep)(implicit acc: T#Access[this.type]) = acc.fun(r)
  
  // TODO:
  ////def apply[L<:Record,R<:Record](r: (L#Rep,R#Rep))(implicit acc: this.type Access L) = acc
  //def apply[L<:Record,R<:Record](r: (L#Rep,R#Rep))(implicit acc: this.type Access (L,R)) = acc
}
class Field[T] extends FieldBase { type Typ = T }

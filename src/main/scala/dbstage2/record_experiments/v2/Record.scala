package dbstage2
package records.v2

import squid.lib.transparencyPropagating
import scala.language.higherKinds

/*
  
  Main problem:
    Access needs an actual instance of the Record in order to perform the access (at least in the original model, where the Rep type of :: is hidden/abstract)
    Making Access a class member of Record and having implicits in each :: doesn't really work, as record values are normally not modules (it's hard to extend a record explicitly)
   
*/

abstract class Record {
  type Rep
  type Mk[T]
  @transparencyPropagating
  def mk: Mk[Rep] = mkCPS(identity)
  def mkCPS[R](k: Rep => R): Mk[R]
  
  ////case class Access[F<:FieldModule](idx: Int, fun: Rep => F#Typ)
  //abstract class AccessModule(idx: Int) {
  //  val f: FieldModule
  //  def fun: Rep => f.Typ
  //}
  //type Access[F] = AccessModule { val f: F }
  
}
object Record {
  implicit class RecordOps[FS<:Record](fs:FS) {
    def :: [F<:FieldModule with Singleton](f: F): F :: FS = new ::(f,fs)
  }
}
case object NoFields extends Record {
  type Rep = Unit
  type Mk[T] = T
  //def mk: Mk[Rep] = ()
  def mkCPS[R](k: Rep => R): Mk[R] = k(())
}
case class ::[H<:FieldModule,T<:Record](h:H, t:T) extends Record {
  type Rep = (h.Typ,t.Rep)
  type Mk[T] = h.Typ => t.Mk[T]
  def mkCPS[R](k: Rep => R): Mk[R] = h => t.mkCPS(tr => k((h,tr)))
  def head: Rep => h.Typ = _._1
  def tail: Rep => t.Rep = _._2
  
  //def accessFirst: Access[h.type] = Access(0, head)
  
  //implicit def accessFirst: Access[h.type] = ???
  //implicit def accessNext[H<:FieldModule with Singleton](implicit next: t.Access[H]): Access[H] = Access(next.idx+1, r => next.fun(tail(r)))
}
class FieldModule {
  type Typ
  implicit def self: this.type = this
  def access[T<:Record](implicit acc: this.type Access T) = acc
  def apply[T<:Record](r: T#Rep)(implicit acc: this.type Access T) = acc.fun(r)
}
class Field[T] extends FieldModule { type Typ = T }

//case class Access[F<:FieldModule,+T<:Record](idx: Int, fun: T => T#Rep => F#Typ)
abstract case class Access[H<:FieldModule,-T<:Record](idx: Int) {
//abstract case class Access[H<:FieldModule,T<:Record](idx: Int) {
  //val h: H
  //val t: T
  //def fun: T => T#Rep => h.Typ
  //def fun: T#Rep => h.Typ
  def fun: T#Rep => H#Typ
}
object Access {
  //implicit def accessFirst[H<:FieldModule with Singleton,T<:Record]: Access[H,H::T] = Access[H,H::T](0, ht => r => ht.head(r))
  //implicit def accessNext[N,H<:FieldBase with Singleton,T<:Record](implicit next: Access[N,T]): Access[N,H::T] = Access(next.idx+1, ht => next.fun(ht.tail))
  
  implicit def accessFirst[H<:FieldModule,T<:Record]: Access[H,H::T] = new Access[H,H::T](0) {
  //implicit def accessFirst[H<:FieldModule,T<:Record](implicit h0: H): Access[H,H::T] = new Access[H,H::T](0) {
  //implicit def accessFirst[H<:FieldModule,T<:Record](implicit h0: H): Access[h0.type,h0.type::T] = new Access(0)[h0.type,h0.type::T] { // compiler crash! assertion failed
  //  val h: H = h0
    //val h: h0.type = h0
    def fun = r => r._1
  }
  implicit def accessNext[N<:FieldModule,H<:FieldModule,T<:Record](implicit next: Access[N,T]): Access[N,H::T] = new Access[N,H::T](next.idx+1) {
  //implicit def accessNext[N<:FieldModule,H<:FieldModule,T<:Record](implicit h: H, next: Access[N,T]): Access[N,H::T] = new Access[N,H::T](next.idx+1) {
    def fun = r => next.fun(r._2)
  }
  
}


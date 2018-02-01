/*

Conclusions:
  using an external type-class approach to define the properties of records (such as their Rep and Mk) is totally 
  possible, as long as we're careful to thread precise modules singleton types properly – eg using shapeless.the instead
  of scala.Predef.implicitly.
  But this approach requires having the types be public (though we could maybe hide them using private[package] – not sure),
  so because of Scala's eager dealiasing it disconnects a record's Rep from the record's type/type-class and so we can't
  have operations like r(Age) or Age(r).
  Perhaps using only an upper bound for Rep would be possible? (preventing eager dealiasing)
  
  Maybe a cleaner alternative would be to use only AnyVal classes as wrappers for each field...
    -> see v4, it's much much easier and super clean!!

*/

package dbstage2
package v3

import squid.lib.transparencyPropagating
import scala.language.higherKinds
import shapeless.the

//import FieldModule.Field

//class Relation[R<:Record](r:R)(implicit R: RecordRep[R]) {
class Relation[R<:Record:RecordRep](r: R) {
  type Rec = R
  type RecTrans <: R
  val RecRep = implicitly[RecordRep[R]]
  
  ////case class Access[F<:Field[_]](idx: Int, fun: Rep => F#Typ)
  //case class Access[A,F<:Field[A],T<:Record](idx: Int, fun: T#Rep => A)
  //implicit def accessFirst[A,F<:Field[A],T]: Access[A,F,F::T] = Access(0, r => r._1)
  
  //case class Access[A,F<:Field[A]](idx: Int, fun: RecRep.Rep => A)
  //implicit def accessFirst[A,F<:Field[A],T]: Access[A,F::T] = Access(0, r => r._1)
  
  //implicit def accessNext[H<:FieldBase with Singleton](implicit next: t.Access[H]): Access[H] = Access(next.idx+1, r => next.fun(tail(r)))
  
  //implicit class FieldOps[A](f: Field[A]) {
  //  //def apply(rep: RecRep.Rep): A = ???
  //  def apply(rep: RecTrans): A = ???
  //  def test = 0
  //}
  
  def getRep: RecRep.Rep = null.asInstanceOf[RecRep.Rep]
}

abstract class Record {
  //def rep[T,R](t: T)(implicit TR: RecordRep[T,R]): Option[R] = Option.empty[R]
  //def rep[R](implicit TR: RecordRep[this.type,R]): Option[R] = Option.empty[R]
  def mkRep(implicit r: RecordRep[this.type]): r.Rep => r.Rep = identity(_)
}
object Record {
  implicit class RecordOps[FS<:Record](fs:FS) {
    def :: [F](f: F): F :: FS = new ::(f,fs)
  }
  ///*
  implicit class RecordRepOps[R<:Record](r:R)(implicit val R: RecordRep[R]) {
  //implicit class RecordRepOps(r:Record)(implicit val R: RecordRep[r.type]) { // no dept classes
  //import scala.language.implicitConversions
  //implicit def RecordRepOps(r:Record)(implicit val R: RecordRep[r.type]) {
    def apply: R.Mk[R.Rep] = R.mkCPS[R.Rep](identity)
  }
  //*/
  
  //implicit class RecordRepOps[R<:Record,Re,M[_]](r:R)(implicit R: RecordRep[R]{type Rep = Re;type Mk[T]=M[T]}) {
  //  def apply: M[Re] = R.mkCPS[R.Rep](identity)
  //}
  
  //implicit class ConsOps[H,T, R <: Field[H] :: T](r: R) {
  implicit class ConsOps[H,T](r: Field[H] :: T)(implicit TR: RecordRep[T]) {
    //def apply(h: H) = ???
    //def apply: Mk[T] = ???
    //def mk: TR
  }
}

//class RecordRep[R] {
abstract class RecordRep[-R] {
//class RecordRep[+R] {
  type Rep
  type Mk[T]
  def mkCPS[R](k: Rep => R): Mk[R]
  def mk: Mk[Rep] = mkCPS[Rep](identity)
//class RecordRep[-R,Rep] {
//class RecordRep[R,Rep] {
//class RecordRep[+R,Rep] {
}
object RecordRep {
  implicit object Rep extends RecordRep[NoFields.type] {
    type Rep = Unit
    type Mk[T] = T
    def mkCPS[R](k: Rep => R): Mk[R] = k(())
  //implicit object Rep extends RecordRep[NoFields.type,Unit] {
  }
  //implicit def Rep[H:FieldRep,T:RecordRep] extends RecordRep[H::T] {
  //implicit def Rep[H,T,R](implicit TR: RecordRep[T,R]) = new RecordRep[Field[H]::T,(H,R)] {
  //implicit def Rep[H<:FieldModule,T,R](implicit h: H, TR: RecordRep[T,R]) = new RecordRep[h.type::T,(h.Typ,R)] {
  //implicit def Rep[H<:FieldModule,T:RecordRep](implicit h: H) = new RecordRep[h.type::T] { type Rep = (h.Typ,R)
  //implicit def Rep[H<:FieldModule,T](implicit h: H, r: RecordRep[T]) = new RecordRep[h.type::T] { type Rep = (h.Typ,r.Rep)
  //implicit def Rep2[H<:FieldModule,T](implicit h: H, r: RecordRep[T]): RecordRep[h.type::T] = new RecordRep[h.type::T] { type Rep = (h.Typ,r.Rep)
  //implicit def Rep2[T](implicit h: FieldModule, r: RecordRep[T]): RecordRep[h.type::T] = new RecordRep[h.type::T] { type Rep = (h.Typ,r.Rep)
  //}
  
  //implicit def Rep2[FT,F<:Field[FT],T](implicit r: RecordRep[T]): RecordRep[F::T] = new RecordRep[F::T] { type Rep = (F,r.Rep) }
  
  //implicit def Rep2[FT,T](implicit r: RecordRep[T])/*: RecordRep[Field[FT]::T] { type Rep = (FT,r.Rep) }*/ = new RecordRep[Field[FT]::T] { // works
  
  //implicit def Rep2[FT,T](implicit r: RecordRep[T]) = new RecordRep[Field[FT]::T] { // works 
  implicit def Rep2[F<:FieldModule,T](implicit r: RecordRep[T]) 
  : RecordRep[F::T] { type FT = F#Typ; type Rep = (FT,r.Rep); type Mk[T] = FT => r.Mk[T] }
  = new RecordRep[F::T] { // requires type projection, but makes impl of accessFirst easier (because it's defined in terms of FieldModule)
    type FT = F#Typ
    type Rep = (FT,r.Rep)
    type Mk[T] = FT => r.Mk[T]
    def mkCPS[R](k: Rep => R): Mk[R] = h => r.mkCPS(tr => k((h,tr)))
  }
  
}
//class FieldRep[F] {
//  type Typ
//}
//object FieldRep {
//  implicit def frep[T] = new FieldRep[Field[T]] { type Typ = T }
//}

case object NoFields extends Record {
}
//case class ::[H:FieldRep,T:RecordRep](h:H, t:T) extends Record {
//case class ::[H,T](h:H, t:T) extends Record {
case class ::[+H,+T](h:H, t:T) extends Record {
}
//class Field[T]
class FieldModule {
  type Typ
  //implicit def self: this.type = this
}
object FieldModule {
  
  implicit //def FieldOps(f: FieldModule)
  //class FieldOps(val __field: FieldModule) {
  class FieldOps[F <: FieldModule](f: F) {
    //def apply[R <: Record](r: R)(implicit recr: RecordRep[R]) = 
    def apply[R <: Record](r: recr.r.Rep)(implicit recr: F Access R): F#Typ = recr.fun(r)
  }
  
  //type Field[T] = FieldModule { type Typ = T }
}
class Field[T] extends FieldModule { type Typ = T }

//case class Access[F<:FieldModule,R:RecordRep](idx: Int, fun: RecRep.Rep => A)
abstract class Access[F<:FieldModule,R](idx: Int) {
  //val f: FieldModule
  val r: RecordRep[R]
  def fun: r.Rep => F#Typ
}
object Access {
  //implicit def accessFirst[F<:FieldModule,R]: Access[F,F::R] = ???
  implicit def accessFirst[F<:FieldModule,R](implicit rr: RecordRep[R]): Access[F,F::R] = new Access[F,F::R](0) {
    //val r: RecordRep[F::R] = the[RecordRep[F::R]]
    val r = the[RecordRep[F::R]]
    def fun: r.Rep => F#Typ = rep => rep._1
  }
  //implicit def accessNext[F<:FieldModule,H<:FieldModule,R:RecordRep](implicit next: Access[F,R]): Access[F,H::R] = new Access[F,H::R](0) {
  //implicit def accessNext[F<:FieldModule,H<:FieldModule,R](implicit next: Access[F,R], rr: RecordRep[R]): Access[F,H::R] = new Access[F,H::R](0) {
  implicit def accessNext[F<:FieldModule,H<:FieldModule,R](implicit next: Access[F,R]): Access[F,H::R] = new Access[F,H::R](0) {
    //import next.r // requires making r implicit in Access, which produces a divergent resolution
    //implicit def nextr: next.r.type = next.r // for some reason, doesn't work (implicit resolution doesn't find it)
    //val r: RecordRep[H::R] = the[RecordRep[H::R]](RecordRep.Rep2)
    //val r = the[RecordRep[H::R]]
    //val r = the[RecordRep[H::R]](RecordRep.Rep2(nextr))
    val r = the[RecordRep[H::R]](RecordRep.Rep2(next.r))
    //42:r.Rep
    //def fun: r.Rep => F#Typ = ???
    def fun: r.Rep => F#Typ = rep => next.fun(rep._2)
  }
}

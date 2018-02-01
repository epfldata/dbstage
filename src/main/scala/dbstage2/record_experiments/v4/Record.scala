/*

  TODO: change :: to ~: ? (less confusion with lists)
  
  Note: we could use exclusively tuples, with ~ type synony, and extension method... but it's nicer to have this class' toString
   

*/

package dbstagetests
package v4

import dbstage2.Embedding

import squid.lib.transparencyPropagating
import squid.utils._

import scala.annotation.implicitNotFound
import scala.annotation.unchecked.uncheckedVariance

object Ops {
  type NoFields = NoFields.type
  //implicit class TupleOps[L,R](tup: (L,R)) {
  //  def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,L,A]): A = access.fun(tup._1)
  //  def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,R,A]): A = access.fun(tup._2)
  //}
  implicit class AccessOps[T](self: T) {
    def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,T,A]): A = access.fun(self)
    def project[R](implicit project: Project[T,R]): R = project.apply(self)
    //def proj[A,F<:Field[A]](implicit access: Project[T,R]): R = project.apply(self)
  }
}
import Ops.NoFields

//trait FieldModule extends Any { type Typ; def value: Typ }
//trait Field[T] extends Any with FieldModule { type Typ = T }

//trait Field[T] extends Any { type Typ = T; def value: T }
//trait Field[+T] extends Any { type Typ = T @uncheckedVariance; def value: T }
trait Field[+T] extends Any { def value: T }

//class FieldModule[F <: Field[_]] {
//}

abstract class Record
object Record {
  //implicit class RecordOps[FS<:Record](fs:FS) {
  implicit class RecordOps[FS](fs:FS) {
    @transparencyPropagating
    def :: [F](f: F): F :: FS = new ::(f,fs)
  }
}

case object NoFields extends Record {
  //type NoFields = NoFields.type
}
case class ::[+H,+T](hd:H, tl:T) extends Record {
//case class ::[H,T](hd:H, tl:T) extends Record { // not covariant to avoid potentially confusing widening of fields to Field[T]
  // TODO try to use the fact a Field case class extends Function1!!
  //def apply[F](fm: FieldModule[F])(implicit access)
  //def apply[A,F<:Field[A]](fm: A => F)(implicit access: F Access this.type): A = ???
  @transparencyPropagating
  def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,this.type,A]): A = access.fun(this)
  override def toString = s"$hd :: $tl"
}

//class Access[F<:Field[_],-R](val idx: Int, fun: R => F#Typ) // SCALA BUG?! making it a case class raises a compilation error!
//case class Access[+F<:Field[A],-R,+A](val idx: Int, fun: R => A)
@implicitNotFound("${F} is not known to be a field of ${R}")
//case class Access[F<:Field[A],-R,A](val idx: Int, fun: R => A) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
case class Access[F<:Field[A],-R,A](val idx: Int, field: R => F) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
{ def fun: R => A = field(_).value }
object Access {
  //implicit def accessFirst[F<:Field[_],R]: Access[F,F::R] = new Access[F,F::R](0, _.hd.value)
  //implicit def accessFirst[F<:Field[_],R]: Access[F,Field[_]::R] = new Access[F,Field[_]::R](0, _.hd.value)
  
  //implicit def accessFirst[F<:Field[A],R,A]: Access[F,Field[A]::R,A] = Access(0, _.hd.value)
  @transparencyPropagating
  //implicit def accessFirst[F<:Field[A],R,A]: Access[F,F::R,A] = Access(0, _.hd.value)
  implicit def accessFirst[F<:Field[A],R,A]: Access[F,F::R,A] = Access(0, _.hd)
  //implicit def accessFirst[F<:FieldModule,R]: Access[F,F::R,F#Typ] = Access(0, _.hd)
  
  @transparencyPropagating
  //implicit def accessNext[F<:Field[A],H,R,A](implicit next: Access[F,R,A]): Access[F,H::R,A] = Access(next.idx+1, r => next.fun(r.tl))
  implicit def accessNext[F<:Field[A],H,R,A](implicit next: Access[F,R,A]): Access[F,H::R,A] = Access(next.idx+1, r => next.field(r.tl))
  
  @transparencyPropagating
  //implicit def accessLeft[F<:Field[A],L,R,A](implicit left: Access[F,L,A]): Access[F,(L,R),A] = Access(left.idx+1, r => left.fun(r._1))
  implicit def accessLeft[F<:Field[A],L,R,A](implicit left: Access[F,L,A]): Access[F,(L,R),A] = Access(left.idx+1, r => left.field(r._1))
  @transparencyPropagating
  //implicit def accessRight[F<:Field[A],L,R,A](implicit left: Access[F,R,A]): Access[F,(L,R),A] = Access(left.idx+1, r => left.fun(r._2))
  implicit def accessRight[F<:Field[A],L,R,A](implicit right: Access[F,R,A]): Access[F,(L,R),A] = Access(right.idx+1, r => right.field(r._2))
}

//abstract class Project[A,B] { def apply: A => B }
//case class Project[-A,+B](apply: A => B)
case class Project[A,B](apply: A => B)
object Project {
  //implicit object projectNone extends Project[Any,NoFields](_ => NoFields) // TODO make Any Record so we can' project _absolutely anything_?
  implicit def projectNone[T] = Project[T,NoFields](_ => NoFields) // TODO make Any Record so we can' project _absolutely anything_?
  @transparencyPropagating
  implicit def projectNext[F<:Field[A],T,R,A](implicit acc: Access[F,T,A], proj: Project[T,R]): Project[T,F::R] = {
    import Record.RecordOps
    Project(t => acc.field(t) :: proj.apply(t))
  }
  //implicit def projectLeft[F<:Field[A],T,R,A](implicit acc: Access[F,T,A], proj: Project[T,R]): Project[T,F::R] = {
}

import Embedding.Predef._

//class Relation[R: RecordRep] {
class Relation[R](implicit Rec: RecordRep[R]) {
  type Rec = R
  //val Rec = shapeless.the[RecordRep[R]]
  import Rec.R
  
  //def load(f: ClosedCode[R] => ClosedCode[Any]) = {
  //def load[C](f: AnyCode[R] => Code[Any,C]) = {
  //  val v = new Variable[R]()
  //  f(v.toCode)
  //}
  def load[T:CodeType,C](f: Code[R => T,C]) = {
    val v = new Variable[R]()
    val opcde = (f:Code[R=>T,C&v.Ctx])(v.toCode)
    val cde = opcde rewrite {
      //case code"${v.ref()}:$t" =>
      case code"$$v" =>
        code"???"
    }
    cde.asInstanceOf[Code[T,C]] // TODO make safe
  }
  
  
}
class RecordRep[R](implicit val R: CodeType[R])
object RecordRep {
  implicit object NoFieldsRep extends RecordRep[NoFields] //(the)
  implicit def consRep[F:CodeType,R:RecordRep:CodeType] = new ConsRep[F,R]
  //implicit class consRep[F:CodeType,R:RecordRep] extends RecordRep[F::R] // nope
  class ConsRep[F:CodeType,R:RecordRep:CodeType] extends RecordRep[F::R] //(the)
  class PairRep[L:RecordRep:CodeType,R:RecordRep:CodeType] extends RecordRep[(L,R)] // Q: useful?
}



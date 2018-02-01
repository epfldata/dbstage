/*

  TODO: change :: to ~: ? (less confusion with lists)
  
  Note: we could use exclusively tuples, with ~ type synony, and extension method... but it's nicer to have this class' toString
  
  TODO: more appropriate for PairUp would be to return a proper typed record – of tuples;
       (p pairUpWith j) == ((Address("A"),Address("B")) :: NoFields)
     or, more appropriately:
       (p pairUpWith j) == FieldPair[Address](Address("A"),Address("B")) :: NoFields

*/

package dbstagetests
package v5

import dbstage2.Embedding

import squid.lib.transparencyPropagating
import squid.utils._

import scala.annotation.implicitNotFound
import scala.annotation.unchecked.uncheckedVariance

object Ops {
  type NoFields = NoFields.type
  implicit class AccessOps[T](self: T) {
    def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,T]): A = access.fun(self)
    def get[F<:FieldModule](implicit access: Access[F,T]): F#Typ = access.fun(self) // TODO replace apply with this
    def project[R](implicit project: Project[T,R]): R = project.apply(self)
    def pairUpWith[R](r:R)(implicit pairs: PairUp[T,R]): List[FieldPair[_]] = pairs.ls(self,r)
  }
}
import Ops.NoFields

trait FieldModule extends Any { type Typ; def value: Typ }
trait Field[T] extends Any with FieldModule { type Typ = T }
//trait Field[+T] extends Any with FieldModule { type Typ = T @uncheckedVariance }

abstract class Record
object Record {
  //implicit class RecordOps[FS<:Record](fs:FS) {
  implicit class RecordOps[FS](fs:FS) {
    @transparencyPropagating
    def :: [F](f: F): F :: FS = new ::(f,fs)
  }
}

case object NoFields extends Record
//case class ::[+H,+T](hd:H, tl:T) extends Record {
case class ::[H,T](hd:H, tl:T) extends Record { // covariance does not seem necessary
  @transparencyPropagating
  def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,this.type]): A = access.fun(this)
  override def toString = s"$hd :: $tl"
}

@implicitNotFound("${F} is not known to be a field of ${R}")
//case class Access[F<:Field[A],-R,A](val idx: Int, field: R => F) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
case class Access[F<:FieldModule,-R](val idx: Int, field: R => F) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
{ def fun: R => F#Typ = field(_).value }
object Access {
  @transparencyPropagating
  implicit def accessFirst[F<:FieldModule,R]: Access[F,F::R] = Access(0, _.hd)
  
  @transparencyPropagating
  implicit def accessNext[F<:FieldModule,H,R](implicit next: Access[F,R]): Access[F,H::R] = Access(next.idx+1, r => next.field(r.tl))
  
  @transparencyPropagating
  implicit def accessLeft[F<:FieldModule,L,R](implicit left: Access[F,L]): Access[F,(L,R)] = Access(left.idx+1, r => left.field(r._1))
  @transparencyPropagating
  implicit def accessRight[F<:FieldModule,L,R](implicit right: Access[F,R]): Access[F,(L,R)] = Access(right.idx+1, r => right.field(r._2))
}

@implicitNotFound("Record type ${A} cannot be projected onto record type ${B}")
case class Project[A,B](apply: A => B)
object Project {
  //implicit object projectNone extends Project[Any,NoFields](_ => NoFields) // TODO make Any Record so we can' project _absolutely anything_?
  implicit def projectNone[T] = Project[T,NoFields](_ => NoFields) // TODO make Any Record so we can' project _absolutely anything_?
  @transparencyPropagating
  implicit def projectNext[F<:FieldModule,T,R](implicit acc: Access[F,T], proj: Project[T,R]): Project[T,F::R] = {
    import Record.RecordOps
    Project(t => acc.field(t) :: proj.apply(t))
  }
}

//case class FieldPair[T](l: Field[T], r: Field[T])
//case class FieldPair[T](l: FieldModule{type Typ = T}, r: FieldModule{type Typ = T})
case class FieldPair[F<:FieldModule](l: F, r: F)
case class PairUp[L,R](ls: (L,R) => List[FieldPair[_]])
object PairUp {
  
  implicit def noMorePairs[L]: PairUp[L,NoFields] = PairUp((_,_) => Nil)
  
  implicit def hasPair[F<:FieldModule,L,R](implicit pairs: PairUp[L,R], acc: Access[F,L]=null): PairUp[L,F::R] =
    if (acc == null) 
      println((acc,pairs)) thenReturn 
      PairUp((l,r) => 
        println(l,r) thenReturn
        pairs.ls(l,r.tl)) else
    PairUp((l,r) => FieldPair[F](acc.field(l),r.hd) :: pairs.ls(l,r.tl))
  
  //implicit def hasPair[F<:Field[A],L,R,A](implicit pairs: PairUp[L,R], acc: Access[F,L]=null): PairUp[L,F::R] =
  ////implicit def hasPair[F<:Field[F#Typ],L,R,A](implicit pairs: PairUp[L,R], acc: Access[F,L]=null): PairUp[L,F::R] = // Error:(87, 35) illegal cyclic reference involving type F
  //  if (acc == null) PairUp((l,r) => pairs.ls(l,r.tl)) else
  //  PairUp((l,r) => FieldPair(acc.field(l),r.hd) :: pairs.ls(l,r.tl))
  
  ////implicit def noPair[F<:Field[A],L,R,A](implicit acc: Access[F,L], pairs: PairUp[L,R]): PairUp[L,F::R] =
  
  
}

import Embedding.Predef._

// class Relation not ported from v4 yet



package dbstage2

import cats.Monoid
import squid.lib.transparencyPropagating
import squid.quasi.embed
import squid.quasi.phase
import squid.utils._

import scala.annotation.implicitNotFound

object RecordsPredef {
  //case object NoFields extends Record
  //case object NoFields extends Record with Field[NoFields.type] // illegal cyclic reference involving object NoFields
  case object NoFields extends Record with FieldModule {
    type Typ = NoFields
    def value = this
    //override def toString: String = s"\u23DA" // (ground symbol)
  }
  type NoFields = NoFields.type
}
trait RecordsPredef {
  //case object NoFields extends Record
  //type NoFields = NoFields.type
  val NoFields = RecordsPredef.NoFields
  type NoFields = RecordsPredef.NoFields
  
  //implicit class AccessOps[T](self: T) {
  //  def get[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,T]): A = access.fun(self)
  //  def apply[F<:FieldModule](implicit access: Access[F,T]): F#Typ = access.fun(self)
  //  def project[R](implicit project: Project[T,R]): R = project.apply(self)
  //  def pairUpWith[R](r:R)(implicit pairs: PairUp[T,R]): List[FieldPair[_]] = pairs.ls(self,r)
  //}
}
//@embed // FIXME: Embedding Error: Unknown type `F#Typ` does not have a TypeTag to embed it as uninterpreted.
class AccessOps[T] private[dbstage2](val self: T) {
  def get[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,T]): A = access.fun(self)
  def apply[F<:FieldModule](implicit access: Access[F,T]): F#Typ = access.fun(self)
  def project[R](implicit project: Project[T,R]): R = project.apply(self)
  def select[F<:FieldModule](implicit access: Access[F,T]): F = access.field(self)
  //def pairUpWith[R](r:R)(implicit pairs: PairUp[T,R]): List[FieldPair[_]] = pairs.ls(self,r)
  def pairUpWith[R](r:R)(implicit pairs: PairUp[T,R]): List[FieldPair] = pairs.ls(self,r)
}
//object RecordOps extends RecordOps
//import RecordOps.NoFields

trait FieldModule extends Any { type Typ; @transparencyPropagating def value: Typ }
object FieldModule {
  import dbstage.BuildField
  //implicit def monoid[T:Monoid]: Monoid[FieldModule { type Typ = T }] = ???
  //implicit def monoid[T:Monoid,F<:FieldModule { type Typ = T }]: Monoid[F] = ???
  //implicit def monoid[T:Monoid,F<:FieldModule](implicit ev: F#Typ =:= T): Monoid[F] = ???
  implicit def monoid[F<:FieldModule:BuildField](implicit ev: Monoid[F#Typ]): Monoid[F] = {
    val bf = implicitly[BuildField[F]]
    val mon = Monoid[F#Typ]
    new Monoid[F] { // TODO require evidence to BUILD objects of type F...
      def empty = bf(mon.empty)
      def combine(lhs: F, rhs: F) = bf(mon.combine(lhs.value, rhs.value))
    }
  }
}
trait Field[T] extends Any with FieldModule { type Typ = T }
//trait Field[+T] extends Any with FieldModule { type Typ = T @uncheckedVariance }
object Field {
  //implicit def monoid[T:Monoid]: Monoid[Field[T]] = ???
  //implicit def monoid[T:Monoid,F<:Field[T]]: Monoid[F] = ???
}

abstract class Record {
  override def toString = {
    def go(r: Any): String = r match {
      case ::(hd,NoFields) => s"$hd"
      case ::(hd,tl) => s"$hd, ${go(tl)}"
      case NoFields => ""
      case _ => r.toString
    }
    s"<${go(this)}>"
  }
}
object Record {
  //implicit class RecordOps[FS<:Record](fs:FS) {
  implicit class RecordOps[FS](fs:FS) {
    @transparencyPropagating @phase('Sugar)
    def :: [F](f: F): F :: FS = new ::(f,fs)
  }
  implicit object noFieldMonoid extends Monoid[NoFields] {
    def empty = NoFields
    def combine(lhs: NoFields, rhs: NoFields) = NoFields
  }
  implicit def recordMonoid[A:Monoid,B:Monoid]: Monoid[A::B] = new Monoid[A::B] {
    def empty = Monoid[A].empty :: Monoid[B].empty
    def combine(lhs: A :: B, rhs: A :: B) = Monoid[A].combine(lhs.hd,rhs.hd) :: Monoid[B].combine(lhs.tl,rhs.tl)
  }
}

//case object NoFields extends Record
//case class ::[+H,+T](hd:H, tl:T) extends Record {
//case class ::[+H,+T](hd:H, tl:T) extends Record with Field[H::T] { // variance problem
case class ::[H,T](hd:H, tl:T) extends Record with Field[H::T] { // variance problem
//case class ::[+H,+T](hd:H, tl:T) extends Record with FieldModule { // found: ::.this.type (with underlying type dbstage.::[H,T]); required: ::.this.Typ
//  type Typ <: H::T
  @transparencyPropagating
  def value = this
  //@transparencyPropagating
  //def apply[A,F<:Field[A]](fm: A => F)(implicit access: Access[F,this.type]): A = access.fun(this)
  
  //@transparencyPropagating
  //override def toString = s"$hd :: $tl"
}

@implicitNotFound("${F} is not known to be a field of ${R}")
//case class Access[F<:Field[A],-R,A](val idx: Int, field: R => F) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
//case class Access[F<:FieldModule,-R] private[dbstage](val idx: Int, field: R => F) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
class Access[F<:FieldModule,-R] private[dbstage2](_idx: Int, _field: R => F) // Note: `A` acts like an output type; for some reason making it covariant breaks hell lose
//{ def fun: R => F#Typ = field(_).value }
{ 
  @transparencyPropagating def idx = _idx
  @transparencyPropagating def field = _field
  def fun: R => F#Typ = field(_).value }


@embed
object Access {
  //@phase('Sugar)
  @transparencyPropagating
  def apply[F<:FieldModule,R](_idx: Int, _field: R => F) = new Access(_idx,_field)
  
  @transparencyPropagating @phase('Sugar)
  implicit def accessFirst[F<:FieldModule,R]: Access[F,F::R] = Access(0, _.hd)
  
  @transparencyPropagating @phase('Sugar)
  implicit def accessNext[F<:FieldModule,H,R](implicit next: Access[F,R]): Access[F,H::R] = Access(next.idx+1, r => next.field(r.tl))
  
  @transparencyPropagating @phase('Sugar)
  implicit def accessLeft[F<:FieldModule,L,R](implicit left: Access[F,L]): Access[F,(L,R)] = Access(left.idx+1, r => left.field(r._1))
  @transparencyPropagating @phase('Sugar)
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

//case class FieldPair[F<:FieldModule](l: F, r: F)
abstract class FieldPair { type F<:FieldModule; val l: F; val r: F }
object FieldPair {
  @transparencyPropagating
  def apply[F0<:FieldModule](l0: F0, r0: F0): FieldPair = new FieldPair { type F = F0; val l = l0; val r = r0 }
  //def unapply[F0<:FieldModule](fp: FieldPair{type F = F0}) = Some(fp.l,fp.r)
  def unapply(fp: FieldPair): Some[(fp.F,fp.F)] = Some(fp.l,fp.r)
}
//case class PairUp[L,R](ls: (L,R) => List[FieldPair[_]]) {
@transparencyPropagating
//case class PairUp[L,R](ls: (L,R) => List[FieldPair]) { // used to be List[FieldPair[_]] but Squid does not support existential types...
//  /*
//  @transparencyPropagating
//  def mapR[S](f: S => R): PairUp[L,S] = PairUp((l, r) => ls(l,f(r)))
//  */
//}
class PairUp[L,R](val ls: (L,R) => List[FieldPair])
//object PairUp {
//  def apply[L,R](ls: (L,R) => List[FieldPair]) = new PairUp(ls)
//}
@embed
object PairUp extends PairUpLowPriority {
  
  @transparencyPropagating
  def apply[L,R](ls: (L,R) => List[FieldPair]) = new PairUp(ls)
  
  //@deprecated("LOL")
  //@transparencyPropagating
  @phase('Sugar)
  implicit def noMorePairs[L]: PairUp[L,NoFields] = PairUp((_,_) => Nil)
  
  //implicit def hasPair[F<:FieldModule,L,R](implicit pairs: PairUp[L,R], acc: Access[F,L]=null): PairUp[L,F::R] =
  //  if (acc == null) 
  //    //println((acc,pairs)) thenReturn 
  //    PairUp((l,r) => 
  //      //println(l,r) thenReturn
  //      pairs.ls(l,r.tl)) else
  //  PairUp((l,r) => FieldPair[F](acc.field(l),r.hd) :: pairs.ls(l,r.tl))
  
  //@transparencyPropagating
  @phase('Sugar)
  implicit def hasPair[F<:FieldModule,L,R](implicit pairs: PairUp[L,R], acc: Access[F,L]): PairUp[L,F::R] =
    PairUp((l,r) => FieldPair[F](acc.field(l),r.hd) :: pairs.ls(l,r.tl))
  
  //def stack[f,l,r](pairs: PairUp[L,R]): PairUp[L,F::R] = 
  
}
@embed
class PairUpLowPriority {
  
  //@transparencyPropagating
  @phase('Sugar)
  implicit def doesNotHavePair[F<:FieldModule,L,R](implicit pairs: PairUp[L,R]): PairUp[L,F::R] =
    PairUp((l,r) => pairs.ls(l,r.tl))
  
}

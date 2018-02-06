package dbstagetests
package record_experiments.v6

import scala.annotation.implicitNotFound
import scala.annotation.unchecked.uncheckedVariance
import cats.Monoid
import cats.syntax.all._
import dbstage.MonoidHelper

import squid.utils._

import dbstage2.Embedding

import Ops._

case class ~[A,B](lhs: A, rhs: B) {
  //override def toString: String = s"$lhs ~ $rhs"
  override def toString: String = rhs match {
    case ~(a,b) => s"$lhs ~ ($rhs)"
    case _ => s"$lhs ~ $rhs"
  }
}
object ~ {
  //implicit def monoid[A,B](implicit ev: A RecordAccess)
  //implicit def monoid[A](implicit ev: A RecordOf Monoid): Monoid[A] = ???
  implicit def monoid[A:Monoid,B:Monoid]: Monoid[A ~ B] =
    Monoid.instance(Monoid[A].empty ~ Monoid[B].empty)((x,y) => (x.lhs |+| y.lhs) ~ (x.rhs |+| y.rhs))
}

object Ops {
  implicit class RecordSyntax[A](self: A) {
    def ~ [B] (that: B) = new ~(self,that)
    def apply[B](implicit ev: A RecordAccess B): B = ev(self)
    def project[B](implicit ev: A Project B): B = ev(self)
  }
}

@implicitNotFound("${A} is not known to be a field of ${R}")
case class RecordAccess[A,R](fun: A => R) extends (A => R) { def apply(a: A) = fun(a) }
object RecordAccess {
  implicit def fromLHS[A,B,T](implicit ev: A RecordAccess T): (A ~ B) RecordAccess T = RecordAccess(ev compose (_.lhs))
  implicit def fromRHS[A,B,T](implicit ev: B RecordAccess T): (A ~ B) RecordAccess T = RecordAccess(ev compose (_.rhs))
  implicit def fromT[T]: T RecordAccess T = RecordAccess(identity)
}

//@implicitNotFound("Record type ${A} cannot be projected onto record type ${B}")
@implicitNotFound("Type ${A} cannot be projected onto type ${B}")
case class Project[A,B](fun: A => B) extends (A => B) { def apply(a: A) = fun(a) }
object Project extends ProjectLowPrio {
  //implicit object projectUnit extends Project[Any,Unit](_ => ())  // TODO make Project contravariant?
  implicit def projectUnit[T] = Project[T,Unit](_ => ())
  implicit def projectLHS[A,B,T](implicit ev: A RecordAccess T): (A ~ B) Project T = Project(ev compose (_.lhs))
  implicit def projectRHS[A,B,T](implicit ev: B RecordAccess T): (A ~ B) Project T = Project(ev compose (_.rhs))
  //implicit def projectBoth[A,B,T](implicit evLHS: T Project A, evRHS: T Project B): T Project (A ~ B) = Project(t => evLHS(t) ~ evRHS(t))
  //implicit def projectT[T]: T Project T = Project(identity)
  
  //implicit def projectLHS[F<:FieldModule,T,R](implicit acc: Access[F,T], proj: Project[T,R]): Project[T,F::R] = {
  //  import Record.RecordOps
  //  Project(t => acc.field(t) :: proj.apply(t))
  //}
}
class ProjectLowPrio extends ProjectLowPrio2 {
  implicit def projectBoth[A,B,T](implicit evLHS: T Project A, evRHS: T Project B): T Project (A ~ B) = Project(t => evLHS(t) ~ evRHS(t))
}
class ProjectLowPrio2 {
  implicit def projectT[T]: T Project T = Project(identity)
}


trait RecordOfConsumer[F[_],R[_]] {
  def apply[A:F](a: A): R[A]
}
trait RecordOf[A,F[_]] //{ def apply[X:F] }
{def apply[R[_]](k: RecordOfConsumer[F,R]): A => R[A]}

// doesn't seem to be actually very useful; for example, it's easier to impl Monoid directly than use sthg like this
object RecordOf extends RecordOfLP {
  //implicit def fromLHS[A,B,F[_]](implicit ev: A RecordOf F): (A ~ B) RecordOf F = ???
  //implicit def fromRHS[A,B,F[_]](implicit ev: B RecordOf F): (A ~ B) RecordOf F = ???
  implicit def fromBoth[A,B,F[_]](implicit evLHS: A RecordOf F, evRHS: B RecordOf F): (A ~ B) RecordOf F =
    ???
    //new ((A ~ B) RecordOf F) {
    //  def apply[R[_]](k: RecordOfConsumer[F,R]): (A ~ B) => R[A ~ B] = a => k(a.lhs) ~ k(a.rhs)
    //}
}
class RecordOfLP {
  implicit def fromF[T,F[_]](implicit ev: F[T]): T RecordOf F = new (T RecordOf F) {
    def apply[R[_]](k: RecordOfConsumer[F,R]): T => R[T] = a => k(a)
    //def apply[R[_]](k: RecordOfConsumer[F,R]): R[A] = k(new RecordOfConsumer[F,R] {
    //  def apply[A:F](a: A): R[A] = 
    //})
  }
}


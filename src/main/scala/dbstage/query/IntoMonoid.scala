package dbstage
package query

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import squid.lib.transparencyPropagating
import squid.quasi.doNotEmbed
import squid.quasi.embed

import scala.collection.immutable

trait IntoMonoid[A,M] {
  @transparencyPropagating
  def apply(a: A): M
}
@embed
object IntoMonoid extends IntoMonoidLowPrio {
  @doNotEmbed @transparencyPropagating
  def instance[A,M](f: A => M): (A IntoMonoid M) = new (A IntoMonoid M) { def apply(a: A): M = f(a) }
  //implicit def ofMonoid[M:Monoid]: (M Into M) = instance(identity)
  implicit object ofNonZeroNat extends (NonZero[Nat] IntoMonoid Nat) { def apply(a: NonZero[Nat]): Nat = a }
  @desugar implicit def ofNonEmpty[As]: NonEmpty[As] IntoMonoid As = 
    //new (NonEmpty[As] IntoMonoid As) { def apply(a: NonEmpty[As]): As = a }
    instance(identity)
  @desugar implicit def ofMin[O:Ordering]: Min[O] IntoMonoid Option[Min[O]] = instance(Some.apply)
  @desugar implicit def ofMax[O:Ordering]: Max[O] IntoMonoid Option[Max[O]] = instance(Some.apply)
  //{ def apply(a: Min[O]): Option[Min[O]] = Some(a) }
  @desugar implicit def intoOptRec[A,B](implicit a: A IntoMonoid Option[A], b: B IntoMonoid Option[B]): (A ~ B) IntoMonoid Option[A ~ B] = ???
  
  @desugar
  implicit def ofSortedBy[A,B,O](implicit ev: A IntoMonoid B): (A SortedBy O) IntoMonoid (B SortedBy O) =
    //IntoMonoid.instance(_.as |> ev.apply |> SortedBy.apply)
    IntoMonoid.instance(sb => SortedBy(ev(sb.as)))
  
  protected[dbstage] def infer[A,M](a: => A)(implicit ev: A IntoMonoid M) = ev
  //case class apply[A](implicit A IntoMonoid M) =
}
@embed
class IntoMonoidLowPrio {
  @desugar
  implicit def id[M:Monoid]: (M IntoMonoid M) = IntoMonoid.instance(identity)
  implicit def intoRec[A0,A,B0,B](implicit a: A0 IntoMonoid A, b: B0 IntoMonoid B): (A0 ~ B0) IntoMonoid (A ~ B) = ???
}

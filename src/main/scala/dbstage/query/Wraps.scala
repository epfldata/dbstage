package dbstage

import squid.lib.transparencyPropagating
import squid.quasi.{phase, embed}
import squid.utils._
import cats.Monoid
import cats.syntax.all._

import scala.annotation.unchecked.uncheckedVariance

/* Note: the purpose of having a `final def apply` defer to a protected implementation is so that all user fields
 * share the same apply/deapply method symbols, so that rewrite rules can be defined in Squid that apply to all of
 * them at the same time. */
abstract class Wraps[A,B] extends WrapsBase[A] {
  type Typ = B
  @transparencyPropagating
  final def apply(b: B): A = applyImpl(b)
  protected def applyImpl(b: B): A
  /** Similar to unapply, but always succeeds. */
  @transparencyPropagating
  final def deapply(a: A): B = deapplyImpl(a)
  protected def deapplyImpl(a: A): B
  //def unapply(a: A): Some[B] = Some(deapply(a)) // can conflict with inherited case class defs
  /** when this Wrapper is a companion object of T, this implicit is found when looking for (T Wraps X) evidence */
  final implicit def instance: this.type = this
}
object Wraps {
  def apply[A,B](app: B => A, deapp: A => B) = new Wraps[A,B] {
    def applyImpl(b: B): A = app(b)
    def deapplyImpl(a: A): B = deapp(a)
  }
  implicit class WrapsOps[A,B](private val self: Wraps[A,B]) extends AnyVal {
    def unapply(a: A): Some[B] = Some(self.deapply(a))
  }
}

/** The only instances of this are also instances of Wraps[A,_] */
sealed abstract class WrapsBase[A] { type Typ; def instance: A Wraps Typ }


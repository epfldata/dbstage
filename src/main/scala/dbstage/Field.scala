package dbstage

import squid.lib.transparencyPropagating
import squid.quasi.{phase, embed}
import squid.utils._
import cats.Monoid
import cats.syntax.all._

import scala.annotation.unchecked.uncheckedVariance

trait FieldBase extends Any { type Typ; @transparencyPropagating def value: Typ }
object FieldBase {
  /*
  //import dbstage.BuildField
  //implicit def monoid[T:Monoid]: Monoid[FieldBase { type Typ = T }] = ???
  //implicit def monoid[T:Monoid,F<:FieldBase { type Typ = T }]: Monoid[F] = ???
  //implicit def monoid[T:Monoid,F<:FieldBase](implicit ev: F#Typ =:= T): Monoid[F] = ???
  @transparencyPropagating
  //implicit def monoid[F<:Field[T]:BuildField,T]: Monoid[F] = ??? // nope (not found)
  //implicit def monoid[F<:FieldBase:BuildField,T](implicit ev: F <:< Field[T]): Monoid[F] = ??? // WORKS !!!
  //
  //implicit def monoid[T:Monoid,F<:Field[T]:BuildField]: Monoid[F] = ??? // diverging implicit expansion
  //implicit def monoid[T,F<:Field[T]:BuildField]: Monoid[F] = ??? // does not seem to find it
  //implicit def monoid[F<:Field[_]:BuildField]: Monoid[F] = ??? // resolves, but this uses a type wildcard (not supported by Squid)
  implicit def monoid[F<:FieldBase:BuildField](implicit ev: Monoid[F#Typ]): Monoid[F] = {
    val bf = implicitly[BuildField[F]]
    val mon = Monoid[F#Typ]
    new Monoid[F] { // TODO require evidence to BUILD objects of type F...
      def empty = bf(mon.empty)
      def combine(lhs: F, rhs: F) = bf(mon.combine(lhs.value, rhs.value))
    }
  }
  */
  
  //implicit def wraps[F<:Field[T],T]: (F Wraps T) = ??? // doesn't infer
  //implicit def wraps[F/*<:FieldBase*/,T](implicit ev: F <:< Field[T]): (F Wraps T) = ??? // infers
  //implicit def wraps[F<:FieldBase:BuildField,T](implicit ev: F <:< Field[T]): (F Wraps T) = ??? // infers
  implicit def wraps[F<:FieldBase:BuildField]: (F Wraps (F#Typ)) = //??? // infers
    Wraps(t => implicitly[BuildField[F]].apply(t), f => f.value)
  
}
trait Field[T] extends Any with FieldBase { type Typ = T }
//trait Field[+T] extends Any with FieldBase { type Typ = T @uncheckedVariance }
object Field {
  //implicit def monoid[T:Monoid]: Monoid[Field[T]] = ???
  //implicit def monoid[T:Monoid,F<:Field[T]]: Monoid[F] = ???
}

class FieldModule[F] {
  
}

//class CompanionOf[M,T] { def m: M }

/* Note: the purpose of having a `final def apply` defer to a protected implementation is so that all user fields
 * share the same apply/deapply method symbols, so that rewrite rules can be defined in Squid that apply to all of
 * them at the same time. */
abstract class Wraps[A,B] {
  @transparencyPropagating
  final def apply(b: B): A = applyImpl(b)
  protected def applyImpl(b: B): A
  /** Similar to unapply, but always succeeds. */
  @transparencyPropagating
  final def deapply(a: A): B = deapplyImpl(a)
  protected def deapplyImpl(a: A): B
  //def unapply(a: A): Some[B] = Some(deapply(a)) // can conflict with inherited case class defs
  /** when this Wrapper is a companion object of T, this implicit is found when looking for (T Wraps X) evidence */
  implicit def instance: this.type = this
}
//class WrapsWith[A,B](app: B => A, deapp: A => B) extends Wraps[A,B] { def apply(b: B): A = app(b); def deapply(a: A): B = deapp(a) }
//case class Wraps[A,B](apply: B => A, getValue: A => B)
//case class Wraps[A,B](apply: B => A, getValue: A => B) extends WrapsBase[A] { type Res = B }
//abstract class WrapsBase[A] { type Res; def apply: Res => A; def getValue: A => Res }
object Wraps {
  def apply[A,B](app: B => A, deapp: A => B) = new Wraps[A,B] {
    def applyImpl(b: B): A = app(b)
    def deapplyImpl(a: A): B = deapp(a)
  }
  /*
  //implicit def monoid[A,B:Monoid](implicit ev: Wraps[A,B]): Monoid[A] = {
  implicit def monoid[A,B](implicit ev: Wraps[A,B], m:Monoid[B]): Monoid[A] = {
    Monoid.instance(ev.apply(Monoid[B].empty))((x,y) => ev.apply(ev.getValue(x) |+| ev.getValue(y)))
  }
  //implicit def monoid[A,B:Monoid](implicit w: WrapsBase[A]{ type Res = B }): Monoid[A] = ???
  //implicit def monoid[A,B](implicit w: WrapsBase[A]{ type Res = B }, m:Monoid[B]): Monoid[A] = ???
  */
  implicit class WrapsOps[A,B](private val self: Wraps[A,B]) extends AnyVal {
    def unapply(a: A): Some[B] = Some(self.deapply(a))
  }
}

import Embedding.Predef._

abstract class BuildField[F<:FieldBase] extends (F#Typ => F) {
//abstract class BuildField[F<:FieldBase:CodeType](implicit Typ: CodeType[Typ]) extends (F#Typ => F) {
//  def staged: ClosedCode[F#Typ => F]
}
object BuildField {
  import scala.language.experimental.macros
  implicit def build[T]: BuildField[T] = macro BuildImplicitGen.buildImplicitGen[T]
  def fromFunction[A,F<:FieldBase{type Typ=A}](f: A => F) = new BuildField[F] {
    def apply(a: A): F = f(a)
    //def staged = ???
  }
}

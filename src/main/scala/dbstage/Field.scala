package dbstage

import squid.lib.transparencyPropagating
import squid.quasi.{phase, embed}
import squid.utils._
import cats.Monoid
import cats.syntax.all._

import scala.annotation.unchecked.uncheckedVariance

trait FieldBase extends Any { type Typ; @transparencyPropagating def value: Typ }
object FieldBase {
  //import dbstage.BuildField
  //implicit def monoid[T:Monoid]: Monoid[FieldBase { type Typ = T }] = ???
  //implicit def monoid[T:Monoid,F<:FieldBase { type Typ = T }]: Monoid[F] = ???
  //implicit def monoid[T:Monoid,F<:FieldBase](implicit ev: F#Typ =:= T): Monoid[F] = ???
  @transparencyPropagating
  //implicit def monoid[T:Monoid,F<:Field[T]:BuildField]: Monoid[F] = ??? // diverging implicit expansion
  implicit def monoid[F<:FieldBase:BuildField](implicit ev: Monoid[F#Typ]): Monoid[F] = {
    val bf = implicitly[BuildField[F]]
    val mon = Monoid[F#Typ]
    new Monoid[F] { // TODO require evidence to BUILD objects of type F...
      def empty = bf(mon.empty)
      def combine(lhs: F, rhs: F) = bf(mon.combine(lhs.value, rhs.value))
    }
  }
}
//trait Field[T] extends Any with FieldBase { type Typ = T }
trait Field[+T] extends Any with FieldBase { type Typ = T @uncheckedVariance }
object Field {
  //implicit def monoid[T:Monoid]: Monoid[Field[T]] = ???
  //implicit def monoid[T:Monoid,F<:Field[T]]: Monoid[F] = ???
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

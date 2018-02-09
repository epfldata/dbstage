import cats.Monoid
import squid.utils._
import squid.ir.SimpleEffect
import squid.lib.transparencyPropagating
import scala.language.implicitConversions

package object dbstage extends EmbeddedDefs {
  
  //implicit def the[A <: AnyRef](implicit ev: A): ev.type = ev
  // ^ this seems to let all hell break loose (but for some reason the one from shapeless worked)
  
  implicit class MonoidHelper(m: Monoid.type) {
    def instance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = new Monoid[A] {
      def empty = _empty
      def combine(x: A, y: A): A = _combine(x,y)
    }
  }
  
  // for use in embedded code:
  @transparencyPropagating
  def monoidInstance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = Monoid.instance(_empty)(_combine)
  
  @transparencyPropagating
  implicit def monoidSyntax[A:Monoid](self: A): MonoidSyntax[A] = new MonoidSyntax(self)
  
  @transparencyPropagating
  implicit def mapSyntax[A,B](self: Map[A,B]): MapSyntax[A,B] = new MapSyntax(self)
  
  
  @transparencyPropagating
  implicit def recordSyntax[A](self: A): RecordSyntax[A] = new RecordSyntax(self)
  
  
  import Embedding.Predef._
  
  def isPure(cde: OpenCode[Any]) = cde.rep.effect === SimpleEffect.Pure
  
  
}

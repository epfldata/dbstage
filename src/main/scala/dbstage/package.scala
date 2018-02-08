import cats.Monoid
import squid.utils._
import squid.ir.SimpleEffect
import squid.lib.transparencyPropagating

package object dbstage extends EmbeddedDefs {
  
  implicit class MonoidHelper(m: Monoid.type) {
    def instance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = new Monoid[A] {
      def empty = _empty
      def combine(x: A, y: A): A = _combine(x,y)
    }
  }
  
  // for use in embedded code:
  @transparencyPropagating
  def monoidInstance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = Monoid.instance(_empty)(_combine)
  
  
  import scala.language.implicitConversions
  
  @transparencyPropagating
  implicit def recordSyntax[A](self: A): RecordSyntax[A] = new RecordSyntax(self)
  
  
  //import cats.syntax.all._
  
  //@transparencyPropagating
  //implicit def monoidWrap[A,B](implicit ev: A Wraps B, m: Monoid[B]): Monoid[A] = {
  //  Monoid.instance(ev.apply(Monoid[B].empty))((x,y) => ev.apply(ev.getValue(x) |+| ev.getValue(y)))
  //}
  
  import Embedding.Predef._
  
  def isPure(cde: OpenCode[Any]) = cde.rep.effect === SimpleEffect.Pure
  
  
}

import cats.Monoid
import squid.lib.transparencyPropagating

package object dbstage {
  
  implicit class MonoidHelper(m: Monoid.type) {
    def instance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = new Monoid[A] {
      def empty = _empty
      def combine(x: A, y: A): A = _combine(x,y)
    }
  }
  
  import scala.language.implicitConversions
  
  @transparencyPropagating
  implicit def recordSyntax[A](self: A): RecordSyntax[A] = new RecordSyntax(self)
  
  
}

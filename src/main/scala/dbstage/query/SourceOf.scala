package dbstage
package query

import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import squid.utils._
import squid.lib.transparencyPropagating

trait SourceOf[As,A] {
  def iterator(as: As): Iterator[A]
}
// Q: remove the `A` parameter in these?
trait FiniteSourceOf[As,A] extends SourceOf[As,A]
trait OrderedSourceOf[As,A] extends SourceOf[As,A]
trait NonEmptySourceOf[As,A] extends SourceOf[As,A]


// these traits are to facilitate implementation (and Squid embedding, which does not support intersection types)
// but should not be used as evidence types:
trait OrderedFiniteSourceOf[As,A] extends OrderedSourceOf[As,A] with FiniteSourceOf[As,A]

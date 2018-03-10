package dbstage
package moncomp

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import squid.lib.transparencyPropagating
import squid.quasi.embed

//abstract class Abstracts[To,From] {
//  @transparencyPropagating
//  def apply(from: From): To
//}
case class Abstracts[To,From](f: From => To) {
  @transparencyPropagating
  def apply(from: From): To = f(from)
}
@embed
object Abstracts extends AbstractsLoPri {
  
  //@desugar // FIXME conflicts with class method?
  //def apply[A,B](queryCode: A)(implicit ev: B Abstracts A): B = ev(queryCode)
  
  @transparencyPropagating
  //@desugar
  implicit def absSortedBy[A,As,O:Ordering](implicit src: As SourceOf A, proj: A ProjectsOn O): List[A] Abstracts (As SortedBy O) =
    Abstracts{ as: As SortedBy O =>
      List(src.inAnyOrder(as.as).iterator.toBuffer.sortBy(proj))
    }
  
  @transparencyPropagating
  //@desugar
  implicit def absNonEmpty[A,As,AAs](implicit abs: AAs Abstracts As): NonEmpty[AAs] Abstracts NonEmpty[As] = ???
  
}
@embed
class AbstractsLoPri {
  @desugar
  implicit def absNot[A]: A Abstracts A = Abstracts(identity)
}

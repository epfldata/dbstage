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

class ConcatIterable[A](lhs: Iterable[A], rhs: Iterable[A]) extends Iterable[A] { def iterator = lhs.iterator ++ rhs.iterator }
class PrependIterable[A](lhs: A, rhs: Iterable[A]) extends Iterable[A] { def iterator = Iterator(lhs) ++ rhs.iterator }
class FromThunkIterable[A](thunk: => Iterator[A]) extends Iterable[A] { def iterator = thunk }

class ListOf[A](val as: Iterable[A]) {
  override def equals(that: Any): Bool = that match { case that: ListOf[_] => as.toList == that.as.toList  case _ => false }
  override def toString: String = s"[${as.mkString(",")}]"
}
object ListOf {
  def apply[A](): ListOf[A] = empty
  def apply[A](a: A, as: A*): NonEmpty[ListOf[A]] = mkNonEmpty(new ListOf(new PrependIterable(a,as)))
  def of[A](xs:A*) = new ListOf(xs)
  
  @transparencyPropagating
  def empty[A]: ListOf[A] = new ListOf(Nil)
  
  @transparencyPropagating
  implicit def asSource[A]: (ListOf[A] OrderedFiniteSourceOf A) = new (ListOf[A] OrderedFiniteSourceOf A) {
    def iterator(c: ListOf[A]) = c.as.iterator
  }
  /* Implicit resolution chokes on that for more advanced (indirect) uses: */
  //implicit def asSource[A,As<:ListOf[A]]: (As OrderedFiniteSourceOf A) = new (As OrderedFiniteSourceOf A) {
  //  def iterator(c: As) = c.as.iterator
  //}
  @transparencyPropagating
  implicit def asMonoid[A]: Monoid[ListOf[A]] =
    monoidInstance[ListOf[A]](ListOf.empty)((a,b) => new ListOf(new ConcatIterable(a.as,b.as)))
  
}

class SetOf[A](val as: Set[A]) {
  override def equals(that: Any): Bool = that match { case that: SetOf[_] => as == that.as  case _ => false }
  override def toString: String = s"{${as.mkString(";")}}"
}
object SetOf {
  def apply[A](): SetOf[A] = empty
  def apply[A](a: A, as: A*): NonEmpty[SetOf[A]] = mkNonEmpty(new SetOf(as.toSet + a))
  def of[A](xs:A*) = new SetOf(xs.toSet)
  
  @transparencyPropagating
  def empty[A]: SetOf[A] = new SetOf(Set.empty)
  
  @transparencyPropagating
  implicit def asSource[A]: (SetOf[A] FiniteSourceOf A) = new (SetOf[A] FiniteSourceOf A) {
    def iterator(c: SetOf[A]) = c.as.iterator
  }
  @transparencyPropagating
  implicit def asMonoid[A]: CommutativeMonoid[SetOf[A]] =
    commutativeMonoidInstance[SetOf[A]](SetOf.empty)((a,b) => new SetOf(a.as ++ b.as))
  
}

class MultiSetOf[A](val as: Iterable[A]) {
  override def equals(that: Any): Bool = that match { case that: MultiSetOf[_] => as.toList == that.as.toList  case _ => false }
  override def toString: String = s"{{${as.mkString(";")}}}"
}
object MultiSetOf {
  def apply[A](): MultiSetOf[A] = empty
  def apply[A](a: A, as: A*): NonEmpty[MultiSetOf[A]] = mkNonEmpty(new MultiSetOf(new PrependIterable(a,as)))
  def of[A](xs:A*) = new MultiSetOf(xs)
  
  @transparencyPropagating
  def empty[A]: MultiSetOf[A] = new MultiSetOf(Nil)
  
  @transparencyPropagating
  implicit def asSource[A]: (MultiSetOf[A] FiniteSourceOf A) = new (MultiSetOf[A] FiniteSourceOf A) {
    def iterator(c: MultiSetOf[A]) = c.as.iterator
  }
  @transparencyPropagating
  implicit def asMonoid[A]: CommutativeMonoid[MultiSetOf[A]] =
    commutativeMonoidInstance[MultiSetOf[A]](MultiSetOf.empty)((a,b) => new MultiSetOf(a.as ++ b.as))
  
}

case class SortedBy[As,O](as: As) {
  override def toString: String = s"$as.sorted"
}

@embed
object SortedBy {
  /*
  @doNotEmbed
  @transparencyPropagating
  implicit def wraps[As,O]: (As SortedBy O) Wraps As = new ((As SortedBy O) Wraps As) { // TODO code-generate
    protected def applyImpl(b: As): dbstage.moncomp.SortedBy[As,O] = SortedBy(b)
    protected def deapplyImpl(a: dbstage.moncomp.SortedBy[As,O]): As = a.as
  }
  */
  // ^ annoying because it desugars the monoid instance, which we don't want!
  @transparencyPropagating
  implicit def monoid[As:Monoid,O]: Monoid[As SortedBy O] = ??? 
  
  @doNotEmbed
  implicit def orderedSrc[A,As,O:Ordering](implicit src: As SourceOf A, proj: A ProjectsOn O): (As SortedBy O) OrderedSourceOf A = new ((As SortedBy O) OrderedSourceOf A) {
    def iterator(c: As SortedBy O): Iterator[A] = src.iterator(c.as).toBuffer.sortBy(proj).iterator
  }
  @doNotEmbed
  implicit def finiteSrc[A,As,O](implicit src: As FiniteSourceOf A): (As SortedBy O) FiniteSourceOf A = new ((As SortedBy O) FiniteSourceOf A) {
    def iterator(c: As SortedBy O): Iterator[A] = src.iterator(c.as)
  }
  implicit def nonEmptySrc[A,As,O](implicit src: As NonEmptySourceOf A): (As SortedBy O) NonEmptySourceOf A = ???
  
}

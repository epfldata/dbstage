package dbstage

import squid.utils._
import cats.{Semigroup, Monoid}
import cats.syntax.semigroup._
import cats.instances.unit._
import cats.instances.map._
import squid.lib.transparencyPropagating
import squid.quasi.overloadEncoding
import squid.quasi.{phase, embed}

import scala.collection.immutable.SortedMap


@embed
class MonoidSyntax[A](private val self: A)(implicit mon: A |> Monoid) {
  //@phase('Sugar)
  def pairWith[B](f: A => B) = self ~ f(self)
  //@phase('Sugar)
  def whereMin[T:Ordering](t:T): ArgMin[T,A] = ???
}
@embed
class SemigroupSyntax[A](private val self: A)(implicit sem: A |> Semigroup) {
  //@phase('Sugar)  // exposes private value `self`
  @transparencyPropagating
  def groupBy[B](that: B) = Groups(Map(that -> self))
}

// TODO have a reified Bool type parameter saying whether it should be displayed as a Min or a Max
sealed abstract class ArgMin[+T,+A]  // TODO change to Option[ArgMin] with only one ArgMin ctor?
case class SomeMin[T,A](res: T, a: A) extends ArgMin[T,A] {
  override def toString = a match {
    case () => s"Min($res)"
    case _  => s"ArgMin($res ~> $a)"
  }
}
case object NoMin extends ArgMin[Nothing,Nothing]
object ArgMin {
  @transparencyPropagating
  implicit def monoid[T:Ordering,A:Semigroup]: Monoid[ArgMin[T,A]] =
    Monoid.instance(NoMin:ArgMin[T,A]){ (x, y) =>
      x -> y match {
        case NoMin -> NoMin => NoMin
        case NoMin -> a => a
        case a -> NoMin => a
        case SomeMin(r0,a0) -> SomeMin(r1,a1) => Ordering[T].compare(r0,r1) match {
          case 0 => SomeMin(r0, a0 |+| a1)
          case n if n > 0 => y
          case n if n < 0 => x
        }
      }
    }
}
object Min {
  def apply[T,A](t:T,a:A): ArgMin[T,A] = SomeMin(t,a)
  def apply[T](t:T): ArgMin[T,Unit] = SomeMin(t,())
}

class Count(val n: Int) {
  override def toString: String = s"Count(${n})"
}
//@embed // overloading doesn't really work with @embed
object Count extends (Count Wraps Int) {
  //@phase('Sugar)
  //def dflt = true
  @transparencyPropagating
  //@phase('Sugar)
  @overloadEncoding // to avoid references to Count.apply$default$1
  def apply(where: Bool = true): Count = Count(if (where) 1 else 0)
  protected def applyImpl(v: Int) = new Count(v)
  protected def deapplyImpl(x: Count) = x.n
}

class ConcatIterable[A](lhs: Iterable[A], rhs: Iterable[A]) extends Iterable[A] { def iterator = lhs.iterator ++ rhs.iterator }


// TODO optimize: no need to wrap/box Iterable objects...
class Bag[A](val it: Iterable[A]) extends DataSource[A] {
  def iterator = it.iterator
}
object Bag {
  def empty[E] = new Bag[E](Nil)
  def apply[A](xs: A*) = new Bag(xs)
  implicit def monoid[E]: Monoid[Bag[E]] = Monoid.instance(empty[E])((a, b) => new Bag[E](new ConcatIterable(a.it,b.it)))
}

//case class OrderedBag2[K,A](toMap: SortedMap[K,Bag[A]]) extends DataSource[K~A] {
case class OrderedBag[K,V](toMap: Map[K,Bag[V]]) extends DataSource[K~V] {
  def iterator = toMap.iterator.flatMap{case(k,v)=>v.iterator.map(k~_)}
}
object OrderedBag {
  implicit def monoidOrderedBag[K:Ordering,A]: Monoid[OrderedBag[K,A]] =
    Monoid.instance(OrderedBag[K,A](SortedMap.empty)){(a,b) => OrderedBag(a.toMap |+| b.toMap)}
}

case class Groups[K,V](toMap: Map[K,V]) extends DataSource[K~V] {
  def iterator = toMap.iterator.map{case(k,v)=>(k~v)} 
}
object Groups {
  @transparencyPropagating
  implicit def monoidGroups[K,A:Semigroup]: Monoid[Groups[K,A]] =
    Monoid.instance(Groups[K,A](Map.empty)){(a,b) => Groups(a.toMap |+| b.toMap)}
}





//@field class Key[T](value: T)
case class Key[T](value: T)
object Key { // it's actually not wanted to have `Key[T] Wraps T`, as it would expose unwanted semantics based on T
  //implicit def wraps[T]: (Key[T] Wraps T) = new (Key[T] Wraps T) {
  //  def applyImpl(v: T) = Key(v)
  //  def deapplyImpl(x: Key[T]) = x.value
  //}
  @transparencyPropagating
  implicit def semigroupKey[T]: Semigroup[Key[T]] = Semigroup.instance((a,b) => require(a == b) thenReturn a)
  //implicit def monoidKey[T]: Monoid[Key[T]] = forget it, not wanted!
  @transparencyPropagating
  implicit def ordKey[T:Ordering]: Ordering[Key[T]] = Ordering.by(_.value)
}





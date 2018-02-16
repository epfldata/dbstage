package dbstage

import squid.utils._
import cats.{Semigroup, Monoid}
import cats.syntax.semigroup._
import cats.instances.unit._
import cats.instances.map._
import squid.lib.transparencyPropagating
import squid.quasi.{phase, embed}

import scala.collection.immutable.SortedMap


@embed
class MonoidSyntax[A](private val self: A)(implicit mon: A |> Monoid) {
  //@phase('Sugar)
  def pairWith[B](f: A => B) = self ~ f(self)
  //@phase('Sugar)
  def whereMin[T:Ordering](t:T): ArgMin[T,A] = ???
  //@phase('Sugar)
  //def groupBy[B](that: B) = GroupedBag(Map(that -> self),None,None,None,None)
  //@phase('Sugar)
  //def groupByAndSelect[B](that: B) = GroupedBag(Map(that -> that ~ self),None,None,None,None)
}
@embed
class SemigroupSyntax[A](private val self: A)(implicit sem: A |> Semigroup) {
  //@phase('Sugar)  // exposes private value `self`
  @transparencyPropagating
  def groupBy[B](that: B) = GroupedBag(Map(that -> self),None,None,None,None)
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
object Count extends (Count Wraps Int) {
  def apply(where: Bool = true): Count = Count(if (where) 1 else 0)
  protected def applyImpl(v: Int) = new Count(v)
  protected def deapplyImpl(x: Count) = x.n
}

class ConcatIterable[A](lhs: Iterable[A], rhs: Iterable[A]) extends Iterable[A] { def iterator = lhs.iterator ++ rhs.iterator }


// TODO a common interface between Bag, IndexedBag, and GroupedBag?

// TODO optimize: no need to wrap/box Iterable objects...
case class Bag[A](it: Iterable[A], pred: Option[A => Bool], lim: Option[Int]) extends DataSource[A] {
  def iterator = {
    val it0 = pred.fold(it.iterator)(it.iterator.filter)
    lim.fold(it0)(it0.take)
  }
  def orderBy[B:Ordering](b: B): IndexedBag[B,A] = 
    //GeneralIndexedBag(it.map(b -> _),None,None)
    IndexedBag(groupAndOrderBy(b),None)
  
  def groupAndOrderBy[B:Ordering](b: B): GroupedBag[B,Bag[A]] = GroupedBag(SortedMap(b -> this),None,None,None,None)
  
  //def groupBy[B](b: B): IndexedBag[B,A] = IndexedBag(GroupedBag(Map(b -> this),None,None,None),None)
  // ^ TODO?
  
  def distinct = ??? // TODOdef distinct = Set(xs: _*)
  
  def limit(n: Int) = copy(lim = Some(lim.fold(n)(_ min n)))
  def head = iterator.next
  
  override def filter(p: A => Bool): Bag[A] = copy(pred = Some(pred.fold(p)(pred => x => pred(x) && p(x))))
  override def where(p: A => Bool): Bag[A] = filter(p)
  
  def isEmpty = iterator.isEmpty
  def nonEmpty = !isEmpty
  
  override def toString = s"{ ${iterator mkString "; "} }"
}
object Bag {
  @transparencyPropagating
  def apply[A](a: A): Bag[A] = Bag(a::Nil,None,None)
  implicit def monoidBag[A]: Monoid[Bag[A]] =
    Monoid.instance(Bag[A](Nil,None,None)) {
      case Bag(it0,p0,l0) -> Bag(it1,p1,l1) => Bag(new ConcatIterable(it0,it1),p0 orElse p1, l0 orElse l1)
    }
}

// Thin wrapper to hide the inner bag nesting
case class IndexedBag[O,A](underlying: GroupedBag[O,Bag[A]], lim: Option[Int]) extends DataSource[A] {
  def iterator: Iterator[A] = {
    val it0 = underlying.iterator.flatMap(_.iterator)
    lim.fold(it0)(it0.take)
  }
  def limit(n: Int) = copy(lim = Some(lim.fold(n)(_ min n)))
  
  //def first = xs.headOption.fold(NoMin:ArgMin[Key,A]){case (k,v) => SomeMin(k,v)}
  
  override def filter(pred: A => Bool): IndexedBag[O,A] = copy(underlying applying (_ filter pred))
  // TODO GroupedBag should have special map construct... (named `applying`?)
  override def where(p: A => Bool): IndexedBag[O,A] = filter(p)
  
  override def toString = s"[ ${iterator mkString ", "} ]"
}
object IndexedBag {
  implicit def monoidIndexedBag[O,A]: Monoid[IndexedBag[O,A]] =
    Monoid.instance(IndexedBag(GroupedBag.empty[O,Bag[A]],None))((x, y) => 
      IndexedBag(x.underlying |+| y.underlying, x.lim orElse y.lim))
}

case class GroupedBag[K,A](toMap: Map[K,A], pred: Option[A => Bool], app: Option[A => A], postOrd: Option[Ordering[A]], lim: Option[Int]) extends DataSource[A] {
  @transparencyPropagating
  def iterator = {
    val it0 = toMap.valuesIterator
    val it1 = pred.fold(it0)(it0.filter)
    postOrd.fold(lim.fold(it1)(it1.take)) { implicit o => 
      val it2 = it1.toSeq.sorted.iterator
      lim.fold(it2)(it2.take)
    }
  }
  @transparencyPropagating
  def withKey () = projectingKey[K]
  def projectingKey[K0]()(implicit ev: K ProjectsOn K0): GroupedBag[K,Key[K]~A] =
    GroupedBag[K,Key[K]~A](
      toMap.map{case k->v=>k->(Key(k)~v)}, 
      pred.map(_ compose (_.rhs)), 
      app.map(f => {case k ~ v => k ~ f(v)}), 
      postOrd.map(implicit oa => Ordering.by(_.rhs)), 
      lim)
  override def filter(p: A => Bool): GroupedBag[K,A] = copy(pred = Some(pred.fold(p)(pred => x => pred(x) && p(x))))
  override def where(p: A => Bool): GroupedBag[K,A] = filter(p)
  def applying(f: A => A) = copy(app = Some(app.fold(f)(_ andThen f)))
  def orderByGroupKey(implicit kord: Ordering[K]): GroupedBag[K,A] = copy(toMap = SortedMap(toMap.toSeq:_*), postOrd = None)
  def orderBy[B:Ordering](f: A => B): GroupedBag[K,A] = copy(postOrd = Some(Ordering.by(f)))
  def limit(n: Int) = copy(lim = Some(lim.fold(n)(_ min n)))
  def descending() = copy(postOrd = postOrd.map(_.reverse))
  // ^ TODO restrict: only available if some Sorted boolean type parameter is True
  override def toString = if (toMap.isInstanceOf[SortedMap[_,_]] || postOrd.isDefined) s"[ ${iterator mkString ", "} ]" else s"{ ${iterator mkString "; "} }"
}
object GroupedBag {
  @transparencyPropagating
  def empty[K,A] = GroupedBag[K,A](Map.empty,None,None,None,None)
  @transparencyPropagating
  implicit def monoidGroupedBag[K,A:Semigroup]: Monoid[GroupedBag[K,A]] =
    Monoid.instance(empty[K,A]) {
      case GroupedBag(it0,p0,a0,o0,l0) -> GroupedBag(it1,p1,a1,o1,l1) =>
        GroupedBag(if (it1.isInstanceOf[SortedMap[K,A]]) it1 |+| it0 else it0 |+| it1, // tries to preserve a possible existing 'sortedness' of the merged maps
          p0 orElse p1,a0 orElse a1,o0 orElse o1, l0 orElse l1)
    }
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

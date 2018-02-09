package dbstage

import squid.utils._
import cats.{Semigroup, Monoid}
import cats.syntax.semigroup._
import cats.instances.unit._
import squid.quasi.{embed,phase}
import scala.collection.immutable.TreeMap


@embed
class MonoidSyntax[A](private val self: A)(implicit mon: A |> Monoid) {
  @phase('Sugar)
  def by[B](that: B) = Map(that -> self)
  @phase('Sugar)
  def groupBy[B](that: B) = by(that)
  @phase('Sugar)
  def pairWith[B](f: A => B) = self ~ f(self)
  @phase('Sugar)
  def whereMin[T:Ordering](t:T): ArgMin[T,A] = ???
}

@embed
class MapSyntax[A,B](private val self: Map[A,B]) {
  // TODO:
  //@phase('Sugar)
  //def orderBy[A0]
  @phase('Sugar)
  def orderByKey(implicit ord: A|>Ordering) = TreeMap[A,B](self.toSeq:_*)
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


// TODO make SortedBag<:Bag and add method `.having(condition)`
// TODO actually, only have GroupedBag and possibly unit keys, and an optional ordering... encoded in the fact it wraps a TreeMap...

case class Bag[A](xs: A*) extends DataSource[A] {
  
  def iterator = xs.iterator
  
  //def orderBy[T:Ordering](implicit proj: A ProjectsOn T) = TreeMap(xs map (x => proj(x) -> x): _*)  // ugly: exposes a map where a sequence is expected
  def orderByProject[T:Ordering](implicit proj: A ProjectsOn T) = new SortedBag(TreeMap(xs map (x => proj(x) -> x): _*))
  def orderBy[T:Ordering](t: T) = new SortedBag(TreeMap(xs map (t -> _): _*))
  def unique = Set(xs: _*)
  
  override def toString = s"{${xs mkString ", "}}"
}
object Bag {
  implicit def semigroupBag[A]: Monoid[Bag[A]] = Monoid.instance(Bag[A]())((a,b) => Bag(a.xs ++ b.xs : _*))
}

class SortedBag[A,Key](val xs: TreeMap[Key,A]) {
  // TODO def orderBy to refine the ordering
  
  def first = xs.headOption.fold(NoMin:ArgMin[Key,A]){case (k,v) => SomeMin(k,v)}
  
  override def toString = s"[${xs.valuesIterator mkString ", "}]"
}
object SortedBag {
  // TODO def apply...
  
  implicit def monoidSortedBag[Key:Ordering,A:Monoid]: Monoid[SortedBag[A,Key]] =
    Monoid.instance(new SortedBag(TreeMap.empty[Key,A]))((a,b) => new SortedBag(a.xs |+| b.xs))
  
}


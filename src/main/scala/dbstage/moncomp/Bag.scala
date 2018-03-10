package dbstage
package moncomp

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import squid.lib.transparencyPropagating

import scala.collection.immutable

trait Container[A]

//trait SourceOf[C,A] { def inAnyOrder(c: C): OrderedSourceOf[C,A] }
trait SourceOf[C,A] { def inAnyOrder(c: C): Iterable[A] }
object SourceOf {
  implicit def fromIterable[A]: (Iterable[A] SourceOf A) = new (Iterable[A] SourceOf A) {
    def inAnyOrder(c: Iterable[A]) = c
  }
}
//trait OrderedSourceOf[C,A] extends SourceOf[C,A] { def iterator(c: C): Iterator[A]; def inAnyOrder = this }
trait OrderedSourceOf[C,A] extends SourceOf[C,A] { self =>
  def iterator(c: C): Iterator[A]
  def inAnyOrder(c: C) = new Iterable[A] { def iterator = self.iterator(c) } }
object OrderedSourceOf {
  //implicit def fromIterable[A]: (Iterable[A] OrderedSourceOf A) = new (Iterable[A] OrderedSourceOf A) {
  //  def iterator(c: Iterable[A]) = c.iterator
  //}
  implicit def fromSeq[A]: (Seq[A] OrderedSourceOf A) = new (Seq[A] OrderedSourceOf A) {
    def iterator(c: Seq[A]) = c.iterator
    override def inAnyOrder(c: Seq[A]) = c
  }
}
trait OrderedFiniteSourceOf[C,A] extends OrderedSourceOf[C,A] with FiniteSourceOf[C,A]
object OrderedFiniteSourceOf {
  /*
  implicit object unit extends OrderedFiniteSourceOf[Unit,Unit] {
    def iterator(c: Unit) = Iterator(())
  }
  */
}

trait SourceOfDistinct[C,A] extends SourceOf[C,A]
object SourceOfDistinct {
  
}
trait IndexedSourceOfDistinct[C,K,A] extends IndexedSourceOf[C,K,A]
trait OrderedSourceOfDistinct[C,A] extends OrderedSourceOf[C,A]
trait OrderedFiniteSourceOfDistinct[C,A] extends OrderedFiniteSourceOf[C,A]
object OrderedFiniteSourceOfDistinct {
  //object UnitIterator extends Iterator[Unit]
  //implicit object unit extends OrderedFiniteSourceOfDistinct[Unit,Unit] {
  //  //def iterator(c: Unit) = UnitIterator
  //  def iterator(c: Unit) = Iterator(())
  //}
}

trait NonEmptySourceOf[As,A] extends SourceOf[As,A]
//{ def any: A; def rest: As }
{ def any: A; def all: As } // more efficient: no need to have As efficiently decomposable into (A,As)


trait IndexedSourceOf[C,K,A] extends SourceOf[C,K~A]

trait FiniteSourceOf[C,A] extends SourceOf[C,A] { def toList(c: C): List[A] = List(inAnyOrder(c).iterator.toList) }
//trait FiniteSourceOfDistinct[C,A] extends SourceOfDistinct[C,A] { def toList: List[A] }
trait FiniteSourceOfDistinct[C,A] extends SourceOfDistinct[C,A] with FiniteSourceOf[C,A] //{ def toList: List[A] }

trait BoundedSourceOf[C,A] extends SourceOf[C,A] { def sizeBound: Int }
//trait BoundedSourceOfDistinct[C,A] extends SourceOfDistinct[C,A] { def sizeBound: Int }



//case class List[A](xs: immutable.List[A]) extends Container[A] {
case class List[A](xs: Iterable[A]) extends Container[A] {
  def :: (a: A) : List[A] = List(new PrependIterable(a,xs))
}
object List {
  @transparencyPropagating
  def empty[A]: List[A] = List(Nil)
  @transparencyPropagating
  implicit def asSource[A]: (List[A] OrderedFiniteSourceOf A) = new (List[A] OrderedFiniteSourceOf A) {
    def iterator(c: List[A]) = c.xs.iterator
    //override def inAnyOrder(c: List[A]) = c
  }
  @transparencyPropagating
  implicit def asMonoid[A]: Monoid[List[A]] =
    Monoid.instance[List[A]](List.empty)((a,b) => List(new ConcatIterable(a.xs,b.xs)))
}

case class Bag[A](xs: Iterable[A]) extends Container[A] {
  
}
object Bag {
  def empty[A]: Bag[A] = Bag(Nil)
  @transparencyPropagating
  implicit def asSource[A]: (Bag[A] FiniteSourceOf A) = ???
  @transparencyPropagating
  implicit def asMonoid[A]: CommutativeMonoid[Bag[A]] = ???
}

case class Indexing[K,A](xs: Map[K,A]) extends Container[A] {
  
}
object Indexing {
  def empty[K,A]: Indexing[K,A] = Indexing(Map.empty)
  implicit def asSource[K,A]: (Indexing[K,A] FiniteSourceOf (K~A)) = ???
  implicit def asMonoid[K,A:Semigroup]: Monoid[Indexing[K,A]] = ???
  implicit def asCommutativeMonoid[K,A:CommutativeSemigroup]: CommutativeMonoid[Indexing[K,A]] = ???
  implicit def asIdempotentMonoid[K,A:IdempotentSemigroup]: IdempotentMonoid[Indexing[K,A]] = ???
  implicit def asCommutativeIdempotentMonoid[K,A:CommutativeIdempotentSemigroup]: CommutativeIdempotentMonoid[Indexing[K,A]] = ???
}

case class Set[A](xs: immutable.Set[A]) extends Container[A] {
  override def toString: String = s"{${xs.mkString(",")}}"
}
object Set {
  def empty[A]: Set[A] = Set(immutable.Set.empty)
  @transparencyPropagating
  implicit def asSource[A]: (Set[A] FiniteSourceOfDistinct A) = new (Set[A] FiniteSourceOfDistinct A) {
    //def toList: List[A] =
    def inAnyOrder(c: Set[A]): Iterable[A] = c.xs
  }
  @transparencyPropagating
  //implicit def asMonoid[A]: CommutativeIdempotentMonoid[Set[A]] = ???
  implicit def asMonoid[A]: CommutativeMonoid[Set[A]] =
    commutativeMonoidInstance[Set[A]](Set.empty)((a,b) => Set(a.xs ++ b.xs))
}

case class OrderedSet[A](xs: immutable.TreeSet[A]) extends Container[A] {
  
}
object OrderedSet {
  def empty[A]: OrderedSet[A] = ??? //OrderedSet(immutable.OrderedSet.empty)
  implicit def asSource[A]: (OrderedSet[A] OrderedFiniteSourceOfDistinct A) = ???
  implicit def asMonoid[A]: CommutativeIdempotentMonoid[OrderedSet[A]] = ???
}

sealed abstract class NonEmpty[C] // GADT so we can have a single type parameter in the interface
object NonEmpty {
  //implicit class Ops[A,C<:Container[A]](self: NonEmpty[C]) {
  implicit class Ops[A,C<:Container[A]](self: NonEmpty[C]) {
    def any: A = self match {
      //case MkNonEmpty(a,as,m) => a  // should probably work, but scalac doesn't keep track – try Dotty
      case ne: MkNonEmpty[A,C] => ne.a
    }
    def rest: C = self match {
      case ne: MkNonEmpty[A,C] => ne.as
    }
    def weaken: C = self match {
      case ne: MkNonEmpty[A,C] => ne.weaken
    }
  }
  def apply[A,C<:Container[A]](a: A, as: C)(merge: (A,C)=>C): NonEmpty[C] = new MkNonEmpty[A,C](a,as) {
    def weaken: C = merge(a,as)
  }
  def tryWeaken[A,C](self: NonEmpty[C]) = self match {  // FIXME hacky
    case ne: MkNonEmpty[A,C] => ne.weaken
  }
  
  //implicit def bagSource[A]: (NonEmpty[Bag[A]] FiniteSourceOf A) = ???
  //implicit def setSource[A]: (NonEmpty[Set[A]] FiniteSourceOfDistinct A) = ???
  //implicit def streamedSource[A]: (NonEmpty[Streamed[A]] FiniteSourceOfDistinct A) = ???
  @transparencyPropagating
  implicit def finiteSource[A,As](implicit ev: As FiniteSourceOf A): NonEmpty[As] FiniteSourceOf A = new (NonEmpty[As] FiniteSourceOf A) {
    def inAnyOrder(c: NonEmpty[As]): Iterable[A] = ev.inAnyOrder(tryWeaken(c))
  } 
  implicit def orderedSource[A,As](implicit ev: As OrderedSourceOf A): NonEmpty[As] OrderedSourceOf A = new (NonEmpty[As] OrderedSourceOf A) {
    def iterator(c: NonEmpty[As]): Iterator[A] = ev.iterator(tryWeaken(c))
  }
  implicit def source[A,As](implicit ev: As SourceOf A): NonEmpty[As] SourceOf A = ???
  
  @transparencyPropagating
  implicit def intoSet[A]: NonEmpty[Set[A]] IntoMonoid Set[A] =
    //IntoMonoid.instance((_:NonEmpty[Set[A]]).weaken)
    IntoMonoid.instance(nes => Ops[A,Set[A]](nes).weaken)
  implicit def intoBag[A]: NonEmpty[Bag[A]] IntoMonoid Bag[A] = ???
  implicit def intoList[A]: NonEmpty[List[A]] IntoMonoid List[A] = IntoMonoid.instance(tryWeaken)
  
  //implicit def intoSortedBy[A,M](implicit ev: A IntoMonoid M): NonEmpty[A] IntoMonoid List[A] = ???
  //implicit def intoMonoid[M:Monoid]: NonEmpty[M] IntoMonoid NonEmpty[M] = ???
  
  implicit def semiBag[A]: CommutativeSemigroup[NonEmpty[Bag[A]]] = ???
  implicit def semiSet[A]: CommutativeIdempotentSemigroup[NonEmpty[Set[A]]] = ???
  implicit def semiList[A]: Semigroup[NonEmpty[List[A]]] = ???
  
}
//case class MkNonEmpty[A,C<:Container[A]](a: A, as: C, weaken: () => C) extends NonEmpty[C] with Container[A]
abstract class MkNonEmpty[A,C<:Container[A]](val a: A, val as: C) extends NonEmpty[C] with Container[A] {
  def weaken: C
}
case class MkNonEmptyPaper[A,As<:Container[A]](any: A, rest: As, _weaken: (A,As) => As)
  extends MkNonEmpty[A,As](any,rest) { def weaken: As = _weaken(a,as) }


//sealed abstract class Sorted[As]
//case class MkSorted[A:Ordering,As<:Container[A]](val unsorted: As) extends Sorted[As] with Container[A] {
//  //def unsorted: As
//}
//@field class Sorted[As](as: As) // not supported
case class SortedBy[As,O](as: As) {
  override def toString: String = s"$as.sorted"
}
object SortedBy {
  @transparencyPropagating
  implicit def wraps[As,O]: (As SortedBy O) Wraps As = new ((As SortedBy O) Wraps As) { // TODO code-generate
    protected def applyImpl(b: As): dbstage.moncomp.SortedBy[As,O] = SortedBy(b)
    protected def deapplyImpl(a: dbstage.moncomp.SortedBy[As,O]): As = a.as
  }
  implicit def orderedSrc[A,As,O:Ordering](implicit src: As SourceOf A, proj: A ProjectsOn O): (As SortedBy O) OrderedSourceOf A = new ((As SortedBy O) OrderedSourceOf A) {
    //val sorted = src.inAnyOrder()
    def iterator(c: As SortedBy O): Iterator[A] = src.inAnyOrder(c.as).toBuffer.sortBy(proj).iterator
  }
  implicit def finiteSrc[A,As,O](implicit src: As FiniteSourceOf A): (As SortedBy O) FiniteSourceOf A = new ((As SortedBy O) FiniteSourceOf A) {
    def inAnyOrder(c: As SortedBy O) = src.inAnyOrder(c.as)
  }
  implicit def nonEmptySrc[A,As,O](implicit src: As NonEmptySourceOf A): (As SortedBy O) NonEmptySourceOf A = ???
  //implicit def monoid[As:Monoid,O]: Monoid[As SortedBy O] = ???
  @transparencyPropagating
  implicit def intoMonoid[A,B,O](implicit ev: A IntoMonoid B): (A SortedBy O) IntoMonoid (B SortedBy O) =
    //IntoMonoid.instance(_.as |> ev.apply |> SortedBy.apply)
    IntoMonoid.instance(sb => SortedBy(ev(sb.as)))
}



case class Streamed[A](as: Iterable[A]) extends Container[A]
object Streamed {
  def empty[A]: Streamed[A] = Streamed(Nil)
  def from(n: Int) = Streamed(new FromThunkIterable(Iterator.from(n)))
  //implicit def monoid[A]: Monoid[Streamed[A]] = Monoid.instance(Streamed(Nil)) { (xs,ys) => }
  //implicit def monoid[A]: Monoid[Streamed[A]] = new IncrementalMonoid[Streamed[A]] {
  //implicit def monoid[A]: Monoid[Streamed[A]] = new IncrementalMonoid[Streamed[A],A] {
  implicit def monoid[A]: IncrementalMonoid[Streamed[A]] = new IncrementalMonoid[Streamed[A]] {
    def empty = Streamed.empty
    def combine(x: Streamed[A], y: Streamed[A]): Streamed[A] = Streamed(new ConcatIterable(x.as, y.as))
    
    // TODO override?
    //override def combineAll(as: TraversableOnce[Streamed[A]]): Streamed[A] = ??? //super.combineAll(as)
    //override def combineAllOption(as: TraversableOnce[Streamed[A]]): Option[Streamed[A]] = ??? //super.combineAllOption(as)
    
    //def combineAllIncrementally(as: TraversableOnce[Streamed[A]]): Streamed[A] = combineAll(as)
    def combineAllIncrementally(as: Iterable[Streamed[A]]): Streamed[A] =
      Streamed(new FromThunkIterable(as.iterator.flatMap(_.as.iterator)))
    //def combineAllIncrementally(as: TraversableOnce[Streamed[A]]): Streamed[Streamed[A]] = ???
    //def combineAllIncrementally(as: TraversableOnce[Streamed[A]]): Iterator[A] = ???
  }
  implicit def source[A]: Streamed[A] SourceOf A = new (Streamed[A] SourceOf A) {
    def inAnyOrder(strm: Streamed[A]) = strm.as
  }
  //implicit def source[A]: Streamed[A] OrderedSourceOf A = new (Streamed[A] OrderedSourceOf A) {
  //  override def inAnyOrder(strm: Streamed[A]) = strm.as
  //  def iterator(c: Streamed[A]): Iterator[A] = c.as.iterator
  //}
}

//sealed abstract class NonNeg[N]
sealed abstract case class NonNeg[N](weaken: N)
object NonNeg {
  implicit class NonNegNatOps(private val self: NonNeg[Nat]) extends AnyVal {
    def + (that: Nat): NonNeg[Nat] = new NonNeg(self.weaken + that){}
  }
}
//sealed abstract class NonZero[N](val weaken: N)
sealed abstract case class NonZero[N](weaken: N)
object NonZero {
  implicit class NonZeroNatOps(private val self: NonZero[Nat]) extends AnyVal {
    def + (that: Nat): NonZero[Nat] = new NonZero(self.weaken + that){}
  }
}
//case class Nat(val value: Int) extends AnyVal {
sealed abstract case class Nat(val value: Int) {
  def + (that: Nat): Nat = new Nat(value + that.value){}
}
object Nat {
  val zero: Nat = new Nat(0){}
  def succ(n: Nat): NonZero[Nat] = new NonZero(new Nat(n.value+1){}){}
  val unit = succ(zero)
  implicit def ord: Ordering[Nat] = ???
  implicit def num: Numeric[Nat] = ???
}

case class Min[O](value: O)
object Min {
  //implicit def semigroup[O:Ordering]: CommutativeIdempotentSemigroup[Min[O]] = ???
  implicit def semigroup[O:Ordering]: CommutativeSemigroup[Min[O]] = ???
}
case class Max[O](value: O)
object Max {
  //implicit def semigroup[O:Ordering]: CommutativeIdempotentSemigroup[Max[O]] = ???
  //implicit def monoid: CommutativeIdempotentMonoid[Max[Nat]] = ???
  implicit def semigroup[O:Ordering]: CommutativeSemigroup[Max[O]] = ???
  implicit def monoid: CommutativeMonoid[Max[Nat]] = ???
}
case class Avg[N](value: N, count: NonZero[Nat])
object Avg {
  implicit def semigroup[N:Fractional]: CommutativeIdempotentSemigroup[Avg[N]] =
    CommutativeIdempotentSemigroup.instance { (a,b) =>
      import Fractional.Implicits._
      import NonZero.NonZeroNatOps
      val N = the[Fractional[N]]
      //val c = a.count + b.count
      //Avg((a.value * N.fromInt(a.count) + b.value * N.fromInt(b.count)) / N.fromInt(c), c)
      val ac = N.fromInt(a.count.weaken.value)
      val bc = N.fromInt(b.count.weaken.value)
      Avg((a.value * ac + b.value * bc) / (ac + bc), a.count + b.count.weaken)
    }
}

// goal: define Avg in terms of Sum and Count

case class PostProcessed[R,F](current: R)
object PostProcessed {
  // TODO instances for:
  // PostProcessed[(N,Count),Avg] <: { def value: N }
  // PostProcessed[Bag[A],SortingBy[O]] OrderedSourceOf A
  
  // or overload sources' map/flatMap to automatically return the post-processed result
  // this way we can have:
  //   @field class Avg[N](current: N ~ Count)  // has auto-derived Monoid
  //     { def result = ... }
  //   implicitly[Avg[N] PostProcessedTypeIs N] -- uses _.result
  
}



/** An injection type class */
//trait Into[A,M] {
//  def apply(a: A): M
//}
//object Into {
//  def instance[A,M](f: A => M): (A Into M) = new (A Into M) { def apply(a: A): M = f(a) }
//  //implicit def ofMonoid[M:Monoid]: (M Into M) = instance(identity)
//  implicit def id[M]: (M Into M) = instance(identity)
//  implicit object ofNonZeroNat extends (NonZero[Nat] Into Nat) { def apply(a: NonZero[Nat]): Nat = a.weaken }
//}
trait IntoMonoid[A,M] {
  def apply(a: A): M
}
object IntoMonoid extends IntoMonoidLowPrio {
  def instance[A,M](f: A => M): (A IntoMonoid M) = new (A IntoMonoid M) { def apply(a: A): M = f(a) }
  //implicit def ofMonoid[M:Monoid]: (M Into M) = instance(identity)
  implicit def id[M:Monoid]: (M IntoMonoid M) = instance(identity)
  implicit object ofNonZeroNat extends (NonZero[Nat] IntoMonoid Nat) { def apply(a: NonZero[Nat]): Nat = a.weaken }
  implicit def ofMin[O:Ordering]: Min[O] IntoMonoid Option[Min[O]] = instance(Some.apply)
  implicit def ofMax[O:Ordering]: Max[O] IntoMonoid Option[Max[O]] = instance(Some.apply)
  //{ def apply(a: Min[O]): Option[Min[O]] = Some(a) }
  implicit def intoOptRec[A,B](implicit a: A IntoMonoid Option[A], b: B IntoMonoid Option[B]): (A ~ B) IntoMonoid Option[A ~ B] = ???
  
  protected[dbstage] def infer[A,M](a: => A)(implicit ev: A IntoMonoid M) = ev
  //case class apply[A](implicit A IntoMonoid M) =
}
class IntoMonoidLowPrio {
  implicit def intoRec[A0,A,B0,B](implicit a: A0 IntoMonoid A, b: B0 IntoMonoid B): (A0 ~ B0) IntoMonoid (A ~ B) = ???
}



package dbstage

import cats.{Monoid,Semigroup}
import cats.kernel.CommutativeMonoid
import squid.utils._

/*
    TODO:
      `comprehend` should be overloaded to accept different combinations of CollectionMonoid[F,Comm,Idem]
      where Comm Idem are either True or False; each overload would then require the necessary properties
      from the result monoid (cf: monoid comprehension calculus)
      
    
    normalize comprehensions by going through the predicates one by one, generating normalizedComprehension calls?
    
*/
//package object moncomp extends moncomp.`package`.LowPrioImplicits {
package object moncomp extends LowPrioImplicits {
  //import moncomp.Bag
  
  //implicit class 
  
  /*
  def comprehend[F[_]:CollectionMonoid,A,R:Monoid](prod: F[A], pred: A => Bool = (_:A) => true)(mkRes: A => R): R = {
    //val F = implicitly[CollectionMonoid[F]]
    //val R = implicitly[Monoid[R]]
    //val it = F.iterator(prod)
    //var r = R.combineAll()
    //while (it.hasNext)
    implicitly[Monoid[R]].combineAll(implicitly[CollectionMonoid[F]].iterator[A](prod).filter(pred).map(mkRes))
  }
  */
  
  
  def bag[A]() = Bag.empty
  def bag[A](a: A, as: A*) = NonEmpty(a,Bag(as))((a,b) => 
    //Bag(new ConcatIterable(a::Nil,b.xs)))
    Bag(new PrependIterable(a,b.xs)))
  
  def set[A]() = Set.empty
  def set[A](a: A, as: A*) = NonEmpty(a,Set(as.toSet))((a,b) => Set(b.xs + a))
  
  def list[A]() = List.empty
  def list[A](a: A, as: A*) = NonEmpty(a,List(as))((a,b) => List(new PrependIterable(a,b.xs)))
  
  
  def count(): NonZero[Nat] = ???
  def countIf(cond: Bool): Nat = ???
  
  //def min[O: Ordering](o: O) = Min(o)
  def min[O](o: O) = Min(o)
  def max[O](o: O) = Max(o)
  
  def avg[N:Numeric](n: N) = Avg(n,Nat.unit)
  
  def stream[A](a: A): Streamed[A] = Streamed(a::Nil)
  
  def streamAggregate[A:Monoid](a: A): NonEmpty[Streamed[A]] = ???
  
  
  abstract class FlatMapMirrorsMap[A,Mon[_]] {
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: Mon[M]): M
    def flatMap[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: Mon[M]): M = map(f)
  }
  
  //implicit class FiniteOps[C,A](implicit ev: C FiniteSourceOf A) {
  //  def map[R:CommutativeIdempotentMonoid](f: A => R): R = ???
  //}
  implicit class OrderedOps[C,A](private val self: C)(implicit ev: C OrderedFiniteSourceOf A) extends FlatMapMirrorsMap[A,Monoid] {
    //def map[R:IdempotentMonoid](f: A => R): R = ???
    //def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: IdempotentMonoid[M]): M = ???
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: Monoid[M]): M =
      M.combineAll(ev.iterator(self).map(f andThen into.apply))
  }
  implicit class OrderedDistinctOps[C,A](private val self: C)(implicit ev: C OrderedFiniteSourceOfDistinct A) extends FlatMapMirrorsMap[A,IdempotentMonoid] {
    //def map[R:IdempotentMonoid](f: A => R): R = ???
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: IdempotentMonoid[M]): M =
      M.combineAll(ev.iterator(self).map(f andThen into.apply))
  }
  implicit class DistinctOps[C,A](private val self: C)(implicit ev: C FiniteSourceOfDistinct A) extends FlatMapMirrorsMap[A,CommutativeIdempotentMonoid] {
    //def map[R:CommutativeIdempotentMonoid](f: A => R): R = ???
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: CommutativeIdempotentMonoid[M]): M =
      M.combineAll(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
  }
  
  implicit class UnbounedOrderedOps[C,A](private val self: C)(implicit ev: C OrderedSourceOf A) {
    def take(n: NonNeg[Nat]): NonEmpty[List[A]] = ???
    def take(n: Nat): List[A] = ???
  }
  
  implicit class SemigroupSyntax[A](private val self: A)(implicit sem: A |> Semigroup) {
    def groupingBy[B](that: B) = Indexing(Map(that -> self))
  }
  
  
}

sealed class LowPrioImplicits extends LowPrioImplicits2 {
  import moncomp._
  
  implicit class FiniteOps[C,A](private val self: C)(implicit ev: C FiniteSourceOf A) extends FlatMapMirrorsMap[A,CommutativeMonoid] {
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: CommutativeMonoid[M]): M =
      M.combineAll(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
    def streamed = {
      val iao = ev.inAnyOrder(self)
      Streamed(new FromThunkIterable(iao.iterator))
    }
  }
  
  
}
sealed class LowPrioImplicits2 {
  import moncomp._
  
  implicit class UnboundedOps[C,A](private val self: C)(implicit ev: C SourceOf A) extends FlatMapMirrorsMap[A,IncrementalMonoid] {
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: IncrementalMonoid[M]): M =
      //M.combineAllIncrementally(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
      M.combineAllIncrementally(new FromThunkIterable(ev.inAnyOrder(self).iterator.map(f andThen into.apply)))
    
    //def take(n: Int): ??? = ???
    
  }
  //implicit class UnboundedOps[C,A](private val self: C)(implicit ev: C FiniteSourceOf A) { //extends FlatMapMirrorsMap[A,IncrementalMonoid] {
  //  def map[R,M,B](f: A => R)(implicit into: (R IntoMonoid M), M: IncrementalMonoid[M,B]): Iterator[B] =
  //    M.combineAllIncrementally(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
  //}
  
}

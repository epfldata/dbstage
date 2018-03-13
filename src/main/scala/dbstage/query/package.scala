package dbstage

import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import squid.utils._
import squid.ir.SimpleEffect
import squid.lib.transparencyPropagating

import scala.language.higherKinds
import scala.language.implicitConversions

package object query extends EmbeddedDefs with LowPrioQueryImplicits {
  
  // for use in embedded code:
  @transparencyPropagating
  def monoidInstance[A](_empty: A)(_combine: (A,A) => A): Monoid[A] = new Monoid[A] {
    def empty = _empty
    def combine(x: A, y: A): A = _combine(x,y)
  }
  @transparencyPropagating
  def commutativeMonoidInstance[A](_empty: A)(_combine: (A,A) => A): CommutativeMonoid[A] = new CommutativeMonoid[A] {
    def empty = _empty
    def combine(x: A, y: A): A = _combine(x,y)
  }
  @transparencyPropagating
  def semigroupInstance[A](_combine: (A,A) => A): Semigroup[A] = Semigroup.instance(_combine)
  @transparencyPropagating
  def commutativeSemigroupInstance[A](_combine: (A,A) => A): CommutativeSemigroup[A] = new CommutativeSemigroup[A] {
    def combine(x: A, y: A): A = _combine(x,y)
  }
  
  
  type Bag[A] = MultiSetOf[A]
  
  
  /* // in the paper:
  type NonEmpty[As] = NonEmptyModule.NonEmpty[As]
  abstract class NonEmptyInterface {
    type NonEmpty[As] <: As
    def apply[As,A](as: As)(implicit nonEmptyEv: As NonEmptyOf A): NonEmpty[As] }
  val NonEmptyModule: NonEmptyInterface = new NonEmptyInterface {
    type NonEmpty[As] = As
    def apply[As,A](as: As)(implicit nonEmptyEv: As NonEmptyOf A): NonEmpty[As] = as }
  */
  @field @privateWraps type NonEmpty[N] <: N
  
  def mkNonEmpty[N:Monoid](n: N) = require(implicitly[Monoid[N]].empty =/= n) thenReturn NonEmpty(n)
  
  @transparencyPropagating
  implicit def semigroupNonEmpty[N:Semigroup]: Semigroup[NonEmpty[N]] =
    semigroupInstance((a,b) => NonEmpty(implicitly[Semigroup[N]].combine(a,b)))
  
  @transparencyPropagating
  implicit def commutativeSemigroupNonEmpty[N:CommutativeSemigroup]: CommutativeSemigroup[NonEmpty[N]] =
    commutativeSemigroupInstance((a,b) => NonEmpty(implicitly[CommutativeSemigroup[N]].combine(a,b)))
  
  @transparencyPropagating
  implicit def nonEmptySourceOf[A,As](implicit srcEv: As SourceOf A): NonEmpty[As] NonEmptySourceOf A =
    new (NonEmpty[As] NonEmptySourceOf A) { def iterator(as: NonEmpty[As]) = srcEv.iterator(as) }
  
  @transparencyPropagating
  implicit def nonEmptyFiniteSourceOf[A,As](implicit srcEv: As SourceOf A): NonEmpty[As] FiniteSourceOf A =
    new (NonEmpty[As] FiniteSourceOf A) { def iterator(as: NonEmpty[As]) = srcEv.iterator(as) }
  
  @transparencyPropagating
  implicit def nonEmptyOrderedSourceOf[A,As](implicit srcEv: As SourceOf A): NonEmpty[As] OrderedSourceOf A =
    new (NonEmpty[As] OrderedSourceOf A) { def iterator(as: NonEmpty[As]) = srcEv.iterator(as) }
  
  
  @field type ForAll <: Bool
  @field type ExistsAny <: Bool
  
  @transparencyPropagating
  implicit def monoidExistsAny: CommutativeMonoid[ForAll] = commutativeMonoidInstance(ForAll(true))(_ && _ |> ForAll.apply)
  
  @transparencyPropagating
  implicit def monoidForAll: CommutativeMonoid[ExistsAny] = commutativeMonoidInstance(ExistsAny(false))(_ || _ |> ExistsAny.apply)
  
  @field type Min[N] = N
  @field type Max[N] = N
  
  @transparencyPropagating
  implicit def semigroupMax[N:Ordering]: CommutativeSemigroup[Max[N]] =
    //commutativeSemigroupInstance(_ max _ |> Max[N].apply)
    commutativeSemigroupInstance((a,b) => Max(Ordering[N].max(Max deapply a, Max deapply b)))
  @transparencyPropagating
  implicit def semigroupMin[N:Ordering]: CommutativeSemigroup[Min[N]] =
    commutativeSemigroupInstance((a,b) => Min(Ordering[N].min(Min deapply a, Min deapply b)))
  
  
  // FIXME prevent this from being a monoid from its Wraps instance...!
  //@field @privateWraps type Filtered[As] <: As
  case class Filtered[A,As](as: As, pred: A => Bool)
  
  @transparencyPropagating
  implicit def filteredOrderedSourceOf[A,As](implicit srcEv: As OrderedSourceOf A): Filtered[A,As] OrderedSourceOf A =
    new (Filtered[A,As] OrderedSourceOf A) { def iterator(as: Filtered[A,As]) = srcEv.iterator(as.as).filter(as.pred) }
  
  @transparencyPropagating
  implicit def filteredSourceOf[A,As](implicit srcEv: As SourceOf A): Filtered[A,As] SourceOf A =
    new (Filtered[A,As] SourceOf A) { def iterator(as: Filtered[A,As]) = srcEv.iterator(as.as).filter(as.pred) }
  
  @transparencyPropagating
  implicit def filteredFiniteSourceOf[A,As](implicit srcEv: As FiniteSourceOf A): Filtered[A,As] FiniteSourceOf A =
    new (Filtered[A,As] FiniteSourceOf A) { def iterator(as: Filtered[A,As]) = srcEv.iterator(as.as).filter(as.pred) }
  
  
  
  @field @privateWraps type NonZero[N] <: N
  // note: this could be worked around by passing a dummy numeric... >.<
  def mkNonZero[N:Numeric](n: N) = require(the[Numeric[N]].zero =/= n) thenReturn NonZero(n)
  implicit def semigroupNonZero[N:Numeric]: CommutativeSemigroup[NonZero[N]] =
    commutativeSemigroupInstance((a,b) => mkNonZero(the[Numeric[N]].plus(a,b)))
  //def mkNonZero[N:Monoid](n: N) = require(implicitly[Monoid[N]].empty =/= n) thenReturn NonZero(n)
  //implicit def semigroupNonZero[N:Monoid]: CommutativeSemigroup[NonZero[N]] =
  //  commutativeSemigroupInstance((a,b) => mkNonZero(implicitly[Monoid[N]].combine(a,b)))
  // ^ TODO unify NonZero and NonEmpty?
  
  @field @privateWraps type Nat <: Int
  
  def mkNat(n: Int) = require(n >= 0) thenReturn Nat(n)
  
  //implicit object NumericNat extends Numeric[Nat] {}
  implicit def numericNat = the[Numeric[Int]].asInstanceOf[Numeric[Nat]]
  // ^ FIXME check for no 0; ideally we'd use a more appropriate type class than the Numeric kitchen-sink
  
  implicit object monoidNat extends CommutativeMonoid[Nat] {
    def empty = mkNat(0)
    def combine(x: Nat, y: Nat): Nat = mkNat(x + y)
  }
  
  
  // --- Syntax Helpers ---
  
  
  //@transparencyPropagating
  //implicit def monoidSyntax[A:Monoid](self: A): MonoidSyntax[A] = new MonoidSyntax(self)
  //@transparencyPropagating
  //implicit def semigroupSyntax[A:Semigroup](self: A): SemigroupSyntax[A] = new SemigroupSyntax(self)
  
  @transparencyPropagating
  implicit def recordSyntax[A](self: A): RecordSyntax[A] = new RecordSyntax(self)
  
  
  abstract class FlatMapMirrorsMap[A,Mon[_]] {
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: Mon[M]): M
    def flatMap[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: Mon[M]): M = map(f)
  }
  
  implicit class SourceOps[C,A](private val self: C)(implicit evSrc: C SourceOf A) {
    
    @transparencyPropagating
    def filter(pred: A => Bool) = Filtered(self, pred)
    @transparencyPropagating
    def withFilter(pred: A => Bool) = filter(pred)
    
  }
  
  implicit class NonEmptyOrderedOps[As,A](private val self: As)(implicit evOrd: As OrderedSourceOf A, evNE: As NonEmptySourceOf A, evFin: As FiniteSourceOf A) {
    //@transparent  // FIXME should be this...
    @transparencyPropagating
    def map[R:Semigroup](f: A => R): R = implicitly[Semigroup[R]].combineAllOption(evOrd.iterator(self).map(f)).get
    def flatMap[R:Semigroup](f: A => R): R = map(f)
  }
  
  
  
  
  
  
}

import query._

sealed trait LowPrioQueryImplicits extends LowPrioQueryImplicits2 {
  
  implicit class OrderedOps[As,A](private val self: As)(implicit evOrd: As OrderedSourceOf A, evFin: As FiniteSourceOf A) extends FlatMapMirrorsMap[A,Monoid] {
    //@transparent  // FIXME should be this...
    @transparencyPropagating
    //def map[R:IdempotentMonoid](f: A => R): R = ???
    //def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: IdempotentMonoid[M]): M = ???
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: Monoid[M]): M =
      M.combineAll(evOrd.iterator(self).map(f andThen into.apply))
    
    //@transparencyPropagating
    //def filter(pred: A => Bool) = Filtered(self, pred)
    //@transparencyPropagating
    //def withFilter(pred: A => Bool) = filter(pred)
    
  }
  
  implicit class NonEmptyOps[As,A](private val self: As)(implicit evOrd: As OrderedSourceOf A, evNE: As NonEmptySourceOf A, evFin: As FiniteSourceOf A) {
    //@transparent  // FIXME should be this...
    @transparencyPropagating
    def map[R:CommutativeSemigroup](f: A => R): R = implicitly[Semigroup[R]].combineAllOption(evOrd.iterator(self).map(f)).get
    def flatMap[R:CommutativeSemigroup](f: A => R): R = map(f)
  }
  
  //implicit class UnboundedOrderedOps[C,A](private val self: C)(implicit ev: C OrderedSourceOf A) extends FlatMapMirrorsMap[A,IncrementalMonoid] {
  //implicit class UnboundedOrderedOps[C,A](private val self: C)(implicit ev: C OrderedSourceOf A) extends UnboundedOps[C,A](self) {
  //  def take(n: NonNeg[Nat]): NonEmpty[List[A]] = ???
  //  def take(n: Nat): List[A] = ???
  //}
  
  
}

sealed trait LowPrioQueryImplicits2 {
  
  implicit class FiniteOps[C,A](private val self: C)(implicit ev: C FiniteSourceOf A) extends FlatMapMirrorsMap[A,CommutativeMonoid] {
    def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: CommutativeMonoid[M]): M =
      //M.combineAll(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
      M.combineAll(ev.iterator(self).map(f andThen into.apply))
    
    // TODO
    //def streamed = {
    //  val iao = ev.inAnyOrder(self)
    //  Streamed(new FromThunkIterable(iao.iterator))
    //}
    
    // the implicits here are not used; they're just for better type error, informing users what is required for this op to make sense
    @transparencyPropagating
    //@desugar
    def orderingBy[O:Ordering](implicit proj: A ProjectsOn O): SortedBy[C,O] = SortedBy(self)
    
    @transparencyPropagating
    def iterator: Iterator[A] = ev.iterator(self)
    
    @transparencyPropagating
    def toList: List[A] = {
      val builder = List.newBuilder[A]
      ev.iterator(self).foreach(builder += _)
      builder.result()
    }
    
  }
  
  //implicit 
  //class UnboundedOps[C,A](private val self: C)(implicit ev: C SourceOf A) extends FlatMapMirrorsMap[A,IncrementalMonoid] {
  //  def map[R,M](f: A => R)(implicit into: (R IntoMonoid M), M: IncrementalMonoid[M]): M =
  //    //M.combineAllIncrementally(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
  //    M.combineAllIncrementally(new FromThunkIterable(ev.inAnyOrder(self).iterator.map(f andThen into.apply)))
  //  
  //  //def take(n: Int): ??? = ???
  //  
  //}
  
  //implicit class UnboundedOps[C,A](private val self: C)(implicit ev: C FiniteSourceOf A) { //extends FlatMapMirrorsMap[A,IncrementalMonoid] {
  //  def map[R,M,B](f: A => R)(implicit into: (R IntoMonoid M), M: IncrementalMonoid[M,B]): Iterator[B] =
  //    M.combineAllIncrementally(ev.inAnyOrder(self).iterator.map(f andThen into.apply))
  //}
  
}

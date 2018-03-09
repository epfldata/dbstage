package dbstage
package moncomp

import cats.{Monoid,Semigroup}
import cats.kernel.{CommutativeMonoid,CommutativeSemigroup}
import cats.instances.all._

// TODO remove the idempotent stuff; not actually needed by interfaces anymore...

//trait CommutativeMonoid[A] extends Monoid[A] {
//  
//}
//object CommutativeMonoid {
//  implicit def stdInt: CommutativeMonoid[Int] = ???
//}
trait IdempotentMonoid[A] extends Monoid[A] with IdempotentSemigroup[A] {
  
}
object IdempotentMonoid {
}
trait CommutativeIdempotentMonoid[A] extends CommutativeMonoid[A] with IdempotentMonoid[A] with CommutativeIdempotentSemigroup[A] {
  
}
object CommutativeIdempotentMonoid {
  // problem with putting things here is that they won't be seen when looking for, eg, `CommutativeMonoid`
  /*
  implicit def stdSet[A]: CommutativeMonoid[Set[A]] = ???
  implicit def stdOpt[A:CommutativeIdempotentSemigroup]: CommutativeIdempotentMonoid[Option[A]] = ???
  implicit def stdRec[A:CommutativeIdempotentMonoid,B:CommutativeIdempotentMonoid]: CommutativeIdempotentMonoid[A ~ B] = ???
  implicit object stdUnit extends CommutativeIdempotentMonoid[Unit] {
    def empty: Unit = ()
    def combine(x: Unit, y: Unit): Unit = ()
  }
  */
}

trait IdempotentSemigroup[A] extends Semigroup[A] {
  
}
trait CommutativeIdempotentSemigroup[A] extends CommutativeSemigroup[A] with IdempotentSemigroup[A] {
  
}
object CommutativeIdempotentSemigroup {
  def instance[A](_combine: (A,A) => A): CommutativeIdempotentSemigroup[A] = new CommutativeIdempotentSemigroup[A] {
    def combine(x: A, y: A): A = _combine(x,y)
  }
  /*
  implicit def stdRec[A:CommutativeIdempotentSemigroup,B:CommutativeIdempotentSemigroup]: CommutativeIdempotentSemigroup[A ~ B] = ???
  */
}

trait IncrementalMonoid[A] extends Monoid[A] {
//trait IncrementalMonoid[A,R] extends Monoid[A] {
  //abstract override def combineAll(as: TraversableOnce[A]): A
  //abstract override def combineAllOption(as: TraversableOnce[A]): Option[A]
  /** Only difference with `combineAll` is that this is guaramteed to terminate even if `as` is infinite... */
  //def combineAllIncrementally(as: TraversableOnce[A]): A 
  def combineAllIncrementally(as: Iterable[A]): A 
  //def combineAllIncrementally(as: TraversableOnce[A]): Streamed[A]
  //def combineAllIncrementally(as: TraversableOnce[A]): R
  //def combineAllIncrementally(as: TraversableOnce[A]): Iterator[R]
}

/*
trait CollectionMonoid[F0[_]] {
  type F[A] = F0[A]
  def lift[A](a: A): F[A] //= ???
  def mon[A]: Monoid[F[A]]
  def iterator[A](as: F[A]): Iterator[A]
}
object CollectionMonoid {
  implicit object List extends CollectionMonoid[List] {
    def lift[A](a: A): F[A] = a :: Nil
    def mon[A]: Monoid[F[A]] = implicitly
    def iterator[A](as: F[A]): Iterator[A] = as.iterator
  } 
}
*/


package dbstage.example.paper

import dbstage.query.{NonEmpty,ListOf,SetOf,MapOf}
import squid.utils._

object SimpleLiftedQuery extends App {
  /*
  sealed abstract class LiftedQuery[A]
  
  case class IfThenElse[A](cond: LiftedQuery[Bool], thn: LiftedQuery[A], els: LiftedQuery[A]) extends LiftedQuery[A]
  
  /*
  case class Comprehension[R](productions: IterationBase[R], mon: StagedMonoid[R]) extends LiftedQuery[R]
  sealed abstract class Productions[R]
  sealed abstract class IterationBase[R] extends Productions[R]
  case class Iteration[A,R](src: StagedSource[A]) extends IterationBase[R]
  case class Yield[R](pred: LiftedQuery[Bool], cde: LiftedQuery[R]) extends Productions[R]
  */
  case class Comprehension[R](mon: StagedMonoid[R], productions: NonEmpty[ListOf[Production[_]]], pred: LiftedQuery[Bool], yielded: LiftedQuery[R]) extends LiftedQuery[R]
  case class Production[A](src: StagedSource[A])
  
  sealed abstract class StagedSemigroup[R](val commutes: Bool, val idempotent: Bool)
  sealed abstract class StagedMonoid[R](commutes: Bool, idempotent: Bool) extends StagedSemigroup[R](commutes,idempotent)
  case object IntMonoid extends StagedMonoid[Int](true,false)
  case class SetMonoid[A]() extends StagedMonoid[SetOf[A]](true,true)
  case class ListMonoid[A]() extends StagedMonoid[ListOf[A]](false,false)
  case class MapMonoid[K,V](vmon: StagedSemigroup[V]) extends StagedMonoid[MapOf[K,V]](vmon.commutes,vmon.idempotent)
  
  sealed abstract class StagedSource[A]
  case class BasicSource[A](name: String) extends StagedSource[A]
  case class NestedQuerySource[As,A](subQuery: LiftedQuery[As]) extends StagedSource[A]
  */
  
  //class SetOf[A]
  //class ListOf[A]
  //class MapOf[K,V]
  
}
object SimpleLiftedQuery2 extends App {
  /*
  sealed abstract class LiftedQuery
  case class Comprehension(mon: StagedMonoid, productions: NonEmpty[ListOf[Production]], pred: LiftedQuery, res: LiftedQuery) extends LiftedQuery
  case class Production(src: StagedSource, bound: Variable)
  case class Variable(name: String) extends LiftedQuery
  case class MonoidZero(mon: StagedMonoid) extends LiftedQuery
  case class MonoidMerge(mon: StagedMonoid) extends LiftedQuery
  
  sealed abstract class StagedSource
  case class BasicSource(name: String) extends StagedSource
  case class NestedQuerySource(subQuery: LiftedQuery) extends StagedSource
  
  sealed abstract class StagedSemigroup(val commutes: Bool, val idempotent: Bool)
  sealed abstract class StagedMonoid(commutes: Bool, idempotent: Bool) extends StagedSemigroup(commutes,idempotent)
  case object IntMonoid extends StagedMonoid(true,false)
  case object SetMonoid extends StagedMonoid(true,true)
  case object ListMonoid extends StagedMonoid(false,false)
  case class MapMonoid(valuesSG: StagedSemigroup) extends StagedMonoid(valuesSG.commutes, valuesSG.idempotent)
  */
}

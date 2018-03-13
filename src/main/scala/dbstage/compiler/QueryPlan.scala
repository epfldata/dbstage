package dbstage
package compiler

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

abstract class QueryPlan[A:CodeType,-C] {
  
}
case class Reduction[A:CodeType,R:CodeType,C](src: QueryPlan[A,C], pred: Code[A=>Bool,C], mon: StagedMonoid[R,C]) extends QueryPlan[R,C]
case class Join[A:CodeType,B:CodeType,C](lhs: QueryPlan[A,C], rhs: QueryPlan[B,C], pred: Code[A=>Bool,C]) extends QueryPlan[(A,B),C]
case class Select[A:CodeType,C](src: QueryPlan[A,C], pred: Code[A=>Bool,C]) extends QueryPlan[A,C] // Q: can encode as a Reduction?
case class Unnest[A:CodeType,C](src: QueryPlan[A,C], pred: Code[A=>Bool,C]) extends QueryPlan[A,C]

//case class Scan[A:CodeType,C](src: StagedDataSource[A,C]/*, pred: Code[A=>Bool,C]*/) extends QueryPlan[A,C] // Q: can encode as a Reduction?
case class Scan[A:CodeType,C](src: Path[A,C]/*, pred: Code[A=>Bool,C]*/) extends QueryPlan[A,C]

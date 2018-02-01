package dbstage

import dbstage2.Embedding
import squid.utils._
import dbstage2.Embedding.Predef._
import dbstage2.Embedding.Quasicodes._
import dbstage2.Embedding.ClosedCode

sealed abstract class QueryPlan[+T,-C] {
  
}
sealed abstract class SimpleQueryPlan[+T,-C]

case class Scan[T,C](r: QuerySource[T], src: Option[ClosedCode[QuerySource[T]]]) extends SimpleQueryPlan[T,C] {
  
}
case class HashJoin[A,B,C](lhs: SimpleQueryPlan[A,C], rhs: QueryPlan[B,C]) extends QueryPlan[(A,B),C]
case class Order[T,C](q: Query[T,C], by: Code[Ordering[T],C]) extends QueryPlan[T,C]

case class SimpleSelection[T,C](q: SimpleQueryPlan[T,C], pred: Code[T => Bool,C]) extends SimpleQueryPlan[T,C]




package dbstage2
package queryplan

import squid.utils._
import Embedding.Predef._

//sealed abstract class QueryPlan[T<:Record] {
sealed abstract class QueryPlan[T] {
  
}
//case class Scan[T<:Record](rel: Relation[T]) extends QueryPlan[T]
//case class Filter[T<:Record](self: QueryPlan[T], pred: Code[Bool,_]) extends QueryPlan[T]
case class Scan[T](rel: Relation[T]) extends QueryPlan[T]
case class Filter[T](self: QueryPlan[T], pred: OpenCode[T => Bool]) extends QueryPlan[T]
case class Count[T](self: QueryPlan[T]) extends QueryPlan[dbstage2.Count::NoFields]


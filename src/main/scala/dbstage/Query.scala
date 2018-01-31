package dbstage

import dbstage2.Embedding
import squid.utils._
import dbstage2.Embedding.Predef._
import dbstage2.Embedding.Quasicodes._
import dbstage2.Embedding.ClosedCode

sealed abstract class StagedSource[T,C] {
  def plans: List[SimpleQueryPlan[T,C]] = this match {
    case From(r,src) => Scan[T,C](r,src)::Nil
  }
}
sealed abstract class Query[T,C] {
  def run(implicit ev: C <:< {}): Iterable[T] = ???
  def plans = Query.plansOf(this)
}
object Query {
  def plansOf[T,C](q: Query[T,C]): List[QueryPlan[T,C]] = q match {
    //case From(r,src) => Scan(r,src)::Nil
    //case fmw @ FlatMapWith(src,v,q) => 
    case fmw: FlatMapWith[a,T,C] => val (src,v,q) = (fmw.q, fmw.v, fmw.query)
      for {
        vp <- src.plans
        qp <- q.plans
      //} yield Join[T,C](vp,qp)
      } yield ???
      //???
  }
}
//case class From[T,C](r: Relation[T]) extends Query[T,C] {
case class From[T,C](r: QuerySource[T], src: Option[ClosedCode[QuerySource[T]]]) extends StagedSource[T,C] {
  //def run = ???
  override def toString = s"From(${src.fold(r.toString)(cde => cde.rep|>base.showRep)})"
}
////case class CountQuery[T<:Record](r: Query[T,C]) extends Query[Count::NoFields] {
//case class Fold[T,C](r: Query[T,C]) extends Query[T,C] {
//  //def run = ???
//}
//case class Filter[T<:Record](q: Query[T,C], pred: T => Bool) extends Query[T,C] {
case class Filter[T,C](q: StagedSource[T,C], pred: Code[T => Bool, C]) extends StagedSource[T,C] {
  //def run = ???
}
//case class Join[A,B,C](lhs: Query[A,C], rhs: Query[B,C], pred: (A,B) => Bool) extends Query[(A,B),C] {
//  //def run = ???
//}
//case class OrderBy[A,C](q: Query[A,C], cmp: Code[Ordering[A],C]) extends Query[A,C] {
//  //def run = ???
//}
case class FlatMap[A,B,C](q: StagedSource[A,C], f: Code[A => B,C]) extends Query[B,C] {
  //def run = ???
}
abstract class WithComputation[T,C] extends Query[T,C] {
  type V
  val v: Variable[V]
  val computation: Code[V,C]
  val query: Query[T, C & v.Ctx]
  override def toString = s"<$v=$computation; $query>"
}
abstract class FlatMapWith[A,B,C](val q: StagedSource[A,C]) extends Query[B,C] {
  val v: Variable[A]
  val query: Query[B, C & v.Ctx]
  //override def toString = s"ProjectionW($q, $v => $query)"
  //override def toString = s"FlatMap(\n\t$q,\n\t$v => $query\n)"
  override def toString = s"FlatMap(\n${indentString(q.toString)},\n${
    indentString(s"$v =>\n${indentString(query.toString)}")
  }\n)"
}
object FlatMapWith {
  def unapply[A,B,C](fmw: FlatMapWith[A,B,C]): Some[(StagedSource[A,C], fmw.v.type,Query[B, C & fmw.v.Ctx])] = Some(fmw.q,fmw.v,fmw.query)
}
case class NaturalJoin[A,B,C](q: StagedSource[A,C], r: Code[B,C]) extends StagedSource[A,C] {
  
}
case class Produce[T,C](r: Code[T,C]) extends Query[T,C]

// For compiling parametrized queries only once
abstract class Parametrized[A] extends Query[A,Any] {
  type Ctx
  val body: Query[A,Ctx]
  def close(cde: Code[A,Ctx]): ClosedCode[A]
}

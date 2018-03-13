package dbstage
package compiler

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

sealed abstract class QueryRepr[A:CodeType,-C] {
  //val cde: Code[A,C]
}
case class UnliftedQuery[A:CodeType,C](cde: Code[A,C]) extends QueryRepr[A,C] {
  override def toString: String = cde.toString
}

abstract class LiftedQuery[A:CodeType,-C] extends QueryRepr[A,C]

abstract class NestedQuery[A:CodeType,B:CodeType,C] extends LiftedQuery[A,C] {
  val v: Variable[B]
  val nestedQuery: LiftedQuery[B,C]
  val body: QueryRepr[A,C&v.Ctx]
  override def toString: String = s"let ${v.rep|>base.showRep} = ${nestedQuery.toString|>blockIndentString}\nin ${body}"
}
object NestedQuery {
  // TODO normalization of unnecessary nesting; requires better Inspector that allow inspection of body...
  def apply[A:CodeType,B:CodeType,C](_v: Variable[B], _nestedQuery: LiftedQuery[B,C])(_body: QueryRepr[A,C&_v.Ctx]): LiftedQuery[A,C] = {
    val stable_v = _v.`internal bound`
    (_nestedQuery,_body) match {
      case (_, UnliftedQuery(base.Code(base.RepDef(`stable_v`)))) =>
        _nestedQuery
          .asInstanceOf[LiftedQuery[A,C]] // FIXME GADT
      /* // TODO let commuting
      case (nq: NestedQuery[B,d,C],_) => // let commuting
        new NestedQuery[A,B,C] {
          val v: nq.v.type = nq.v
          val nestedQuery: LiftedQuery[B,C] = _nestedQuery
        }
      */
      case _ =>
        //println(_body,_body.getClass)
        new NestedQuery[A,B,C] {
          val v: _v.type = _v
          val nestedQuery: LiftedQuery[B,C] = _nestedQuery
          val body: QueryRepr[A,C&v.Ctx] = _body
        }
    }
  }
}

case class MonoidEmpty[A:CodeType,C](empty: Code[A,C]) extends LiftedQuery[A,C]

case class MonoidMerge[A:CodeType,C](lhs: QueryRepr[A,C], rhs: QueryRepr[A,C]) extends LiftedQuery[A,C] {
  override def toString = s"merge ${blockIndentString(s"$lhs","|+|")} ${blockIndentString(s"$rhs")}"
}


abstract 
case class Comprehension[R:CodeType,C](productions: Productions[R,C], mon: StagedMonoid[R,C]) extends LiftedQuery[R,C] {
  override def toString = s"Comp ($mon)\n${indentString(s"$productions")}"
}
object Comprehension {
  // TODOne if `mon` is SortedBy.monoid, simplify the comprehension and apply Sorting
  //def apply[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]): QueryRepr[R,C] = QueryCompiler.liftMonoid(mon) match {
  def apply[R:CodeType,C](productions: Productions[R,C], mon: StagedMonoid[R,C]): LiftedQuery[R,C] = mon match {
      // TODO:
      /*
    case sbsm: SortedByStagedMonoid[a,o,C] =>
      //import sbsm.{As,O} // doesn't work... why?!
      implicit val as = sbsm.As
      implicit val o = sbsm.O
      Sorting2(Comprehension(productions.mapYield(code"(sb:a SortedBy o) => sb.as"),sbsm.underlying))
      */
    case lmon => 
      //lastWords(mon.toString)
      new Comprehension[R,C](productions,lmon){}
  }
}
sealed abstract class Productions[R,-C]
//abstract class Iteration[A:CodeType,As:CodeType,R,C](src: StagedDataSource[A,As,C]) extends Productions[R,C] {
//abstract class Iteration[A:CodeType,As:CodeType,R,C](val src: Path[A,As,C]) extends Productions[R,C] {
abstract class Iteration[A:CodeType,R,C](val src: Path[A,C]) extends Productions[R,C] {
  def A = codeTypeOf[A]
  //def As = codeTypeOf[As]
  val v: Variable[A]
  val body: Productions[R,C & v.Ctx]
  
  //override def toString: String = s"Iteration($src,$v,$body)"
  override def toString: String = s"${v.rep|>base.showRep} <- $src\n${/*indentString*/(body.toString)})"
}
object Iteration {
  //def apply[A:CodeType,As:CodeType,R,C](src: Path[A,As,C], v0: Variable[A])(body0: Productions[R,C & v0.Ctx]) = new Iteration[A,As,R,C](src) {
  def apply[A:CodeType,R,C](src: Path[A,C], v0: Variable[A])(body0: Productions[R,C & v0.Ctx]) = new Iteration[A,R,C](src) {
    val v: v0.type = v0
    val body = body0
  }
}
// TODO Binding? -> defined SingleElement instead

//case class Predicate[C]() extends Productions[C]
//abstract 
//case class Yield[R,C](pred: Code[Bool,C], cde: Code[R,C]) extends Productions[R,C] {
//  override def toString: String = s"if ${pred|>showCbound}\nyield ${cde|>showCbound}"
case class Yield[R,C](pred: QueryRepr[Bool,C], cde: QueryRepr[R,C]) extends Productions[R,C] {
  override def toString: String = s"if ${pred}\nyield ${cde.toString|>blockIndentString}"
}
object Yield {
  //def apply[R,C](pred: Code[Bool,C], cde: Code[R,C])(enclosingMonoid: StagedMonoid[R,C]) = cde match {
  //  case 
  //}
}

sealed abstract class Path[A:CodeType,-C]

import query.SourceOf

//abstract
//case class StagedDataSource[A:CodeType,As:CodeType,C](cde: Code[As,C], srcEv: Code[As SourceOf A,C]) extends Path[A,As,C]
sealed abstract class StagedDataSource[A:CodeType,C] extends Path[A,C]
//case class StagedDataSourceOf[A:CodeType,As:CodeType,C](cde: Code[As,C], srcEv: Code[As SourceOf A,C]) extends StagedDataSource[A,C]
case class StagedDataSourceOf[A:CodeType,As:CodeType,C](query: QueryRepr[As,C], srcEv: Code[As SourceOf A,C]) extends StagedDataSource[A,C] {
  override def toString: String = s"Source ${query.toString|>blockIndentString}"
}

//case class SingleElement[A:CodeType,C](cde: Code[A,C]) extends Path[A,A,C]
//case class SingleElement[A:CodeType,C](cde: Code[A,C]) extends Path[A,C]
case class SingleElement[A:CodeType,C](query: LiftedQuery[A,C]) extends Path[A,C]









abstract class StagedMonoid[T:CodeType,-C](val commutes: Bool, val idempotent: Bool) {
  //def mkContext: MonoidContext[T,C,_]
}
case class RawStagedMonoid[T:CodeType,C](cde: Code[Monoid[T],C], com: Bool, ide: Bool) extends StagedMonoid[T,C](com,ide)
case class RawStagedSemigroup[T:CodeType,C](cde: Code[Semigroup[T],C], com: Bool, ide: Bool) extends StagedMonoid[Option[T],C](com,ide)

import query.SortedBy

case class SortedByStagedMonoid[As:CodeType,O:CodeType,C](underlying: StagedMonoid[As,C])
  extends StagedMonoid[SortedBy[As,O],C](underlying.commutes, underlying.idempotent)
{
  implicit def As = codeTypeOf[As]
  implicit def O = codeTypeOf[O]
  //def mkContext: MonoidContext[SortedBy[As,O],C,_] = ???
}



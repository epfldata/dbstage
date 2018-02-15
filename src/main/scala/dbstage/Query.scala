package dbstage

import squid.utils._
import Embedding.Predef._
import Embedding.Quasicodes._
//import Embedding.ClosedCode

import scala.annotation.unchecked.uncheckedVariance

sealed abstract class Query[T:CodeType,-C] {
  //def naivePlan: Code[Any,C] = this match {
  //def naivePlan: Code[(T=>Unit)=>Unit,C] = this match {
  def naivePlan: Code[() => T,C] = this match {
    case fm: FlatMap[a,T,C] => 
      //fm.nestedLoop
      code"() => ${fm.nestedLoop}"
    //case Produce(cde) => code"(k:T=>Unit) => k($cde)"
    case Produce(cde) => 
      //cde
      code"() => $cde"
    case wq: WrappedQuery[T,C] =>
      die
    case wc: WithComputation[T,C] =>
      val v: wc.v.type = wc.v
      code"() => { val ${v} = ${wc.computation}; ${wc.query.naivePlan}() }"
  }
}

abstract class WrappedQuery[T:CodeType,-C] extends Query[T,C] {
  type Under
  implicit val Under: CodeType[Under]
  //def map(f: )
}

case class StagedSource[T:CodeType,C](ds: DataSource[T], cde: ClosedCode[DataSource[T]] |> Option, pred: Code[T => Bool,C] |> Option) {
  def filter(pred: Code[T => Bool,C]) = copy[T,C](pred = Some(pred))
  
  def iterateCode = ds match {
    case ds: StagedDataSource[T] => ds.stagedIterator
    case _ => 
      //val it = ds.iterator
      //code"it"
      val d = ds
      code"d.iterator"
  }
  
  override def toString: String = s"${cde map show getOrElse ds}" + pred.fold("")(" % " concat _ |> show)
}

abstract class FlatMap[A:CodeType,B:CodeType,C](val src: StagedSource[A,C], val mon: StagedMonoid[B,C]) extends Query[B,C] {
  val v: Variable[A]
  val query: Query[B, C & v.Ctx]
  
  def nestedLoop = {
    val v0: v.type = v
    //code"(k: B=>Unit) => { val it = ${src.iterateCode}; while(it.hasNext) { val $v0 = it.next; ${query.naivePlan}(k) } }"
    //code"{ val it = ${src.iterateCode}; while(it.hasNext) { val $v0 = it.next; ${query.naivePlan} } }; ???"
    //code"{ ${src.iterateCode};val $v0 = ??? ; ${query.naivePlan}; }; ???"
    mon accumulateCode
    code"(k: B=>Unit) => { val it = ${src.iterateCode}; while(it.hasNext) { val $v0 = it.next; if (${
      //src.pred.getOrElse(code"(a:A)=>true")}($v0))
      src.pred.map(p => code"$p($v0)")getOrElse(code"true")
    }) k(${query.naivePlan}()) } }"
  }
  
  override def toString = s"FlatMap ($mon)\n${indentString(s"${v.rep|>base.showRep} <- $src")}\n${indentString(query.toString)}"
}
object FlatMap {
  def build[A:CodeType,B:CodeType,C](x: Variable[A])(src: StagedSource[A,C], qu: Query[B,C & x.Ctx], mon: StagedMonoid[B,C]): FlatMap[A,B,C] { val v: x.type } =
    new FlatMap[A,B,C](src,mon) {
      val v: x.type = x
      val query: Query[B,C & v.Ctx] = qu
    }
  //def apply[A,B,C](x: Variable[A])(src: StagedSource[A,C], qu: Query[B,C & x.Ctx]): Query[B,C] = qu match {
  //  case _ => build(x)(src,qu)
  //}
  def unapply[A,B,C](fmw: FlatMap[A,B,C]): Some[(StagedSource[A,C], fmw.v.type,Query[B, C & fmw.v.Ctx])] =
    Some(fmw.src,fmw.v,fmw.query)
}

abstract class WithComputation[T:CodeType,C] extends Query[T,C] {
  type V
  val v: Variable[V]
  val computation: Code[V,C]
  val query: Query[T, C & v.Ctx]
  override def toString = s"${v.rep|>base.showRep} = $computation\n$query"
}

case class Produce[T:CodeType,C](r: Code[T,C]) extends Query[T,C]

import cats.Monoid

sealed abstract class StagedMonoid[T:CodeType,C] {
//sealed abstract class StagedMonoid[T,C](init: Code[T,C], update: Code[T]) {
  
  type Rep
  implicit val Rep: CodeType[Rep]
  val init: Code[Rep,C]
  val update: Code[(Rep,T)=>Unit,C]
  val get: Code[Rep=>T,C]
  
  //def accumulateCode[R:CodeType](f: Code[(T=>Unit)=>R,C]) = code"""
  def accumulateCode(f: Code[(T=>Unit)=>Unit,C]) = code"""
    val cur = $init
    $f(t => $update(cur,t))
    $get(cur)
  """
    //$f(t => $update($get(cur),t))
  
  //override def toString = this match {
  //  case RawStagedMonoid(cde) => show(cde)
  //}
}

import squid.lib.MutVar

case class RawStagedMonoid[T:CodeType,C](cde: Code[Monoid[T],C]) 
  extends VariableBasedStagedMonoid[T,C](code"$cde.empty", code"$cde.combine _") 
{
  override def toString = show(cde)
}
class VariableBasedStagedMonoid[T:CodeType,C](zero: Code[T,C], combine: Code[(T,T)=>T,C]) extends StagedMonoid[T,C] {
  
  type Rep = MutVar[T]
  val Rep = codeTypeOf[MutVar[T]]
  val init = code"MutVar($zero)"
  val update = code"(r:Rep,n:T) => r := $combine(r.!, n)"
  val get = code"(r:Rep) => r.!"
  
  override def toString = s"Monoid(${show(zero)}, ${show(combine)})"
}
case object IntMonoid extends VariableBasedStagedMonoid(code"0", code"(_:Int)+(_:Int)")

case class ArgMinMonoid[T:CodeType,S:CodeType,C](ord: Code[Ordering[T],C], sg: Code[cats.Semigroup[S],C])
  extends VariableBasedStagedMonoid[ArgMin[T,S],C](code"NoMin", 
    //code"???"
    code"ArgMin.monoid[T,S]($ord,$sg).combine _"  // (stupid)
  )



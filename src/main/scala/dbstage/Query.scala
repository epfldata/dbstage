package dbstage

import squid.utils._
import Embedding.Predef._
import Embedding.Quasicodes._
import cats.Semigroup
import squid.lib.transparencyPropagating
import squid.lib.transparent

import scala.annotation.compileTimeOnly
import scala.collection.mutable
import scala.annotation.unchecked.uncheckedVariance

//sealed abstract class Query[T:CodeType,-C] {
sealed abstract class Query[+T:CodeType,-C] {
  def directPlan: Code[T,C] = this match {
    case fm: FlatMap[a,T,C] => fm.mon.mkContext match { case mc: MonoidContext[T,C,d] =>
      implicit val a = fm.A
      val v0: fm.v.type = fm.v
      mc wrap code"""
        val it = ${fm.src.iterateCode}
        while(it.hasNext) {
          val $v0 = it.next
          if (${fm.mkPred}) ${fm.query.returnOrConsume(Some(mc))}foreach${mc.consume}
        }
        ${mc.get}
      """
    }
    case Produce(cde) => cde
    case wc: WithComputation[T,C] =>
      val v: wc.v.type = wc.v
      code"val ${v} = ${wc.computation}; ${wc.query.directPlan}"
    case wq: WrappedQuery[T,C] => die
    case ob: OrderBy[a,o,C] =>
      import ob.{A,O}
      //code"new BufferedOrderedDataSource(${ob.src.directPlan}.iterator)(${ob.ord} on ${ob.proj})"
      code"new BufferedOrderedDataSource(${ob.src.directPlan}.iterator)(${ob.getOrd})"
          .asInstanceOf[Code[T,C]]
  }
  //def returnOrConsume[E,D](ctx: MonoidContext[T,E,D]|>Option): Code[Option[T],C & D] = this match {
  def returnOrConsume[E,D](ctx: MonoidContext[T@uncheckedVariance,E,D]|>Option): Code[Option[T],C & D] = this match {
    case fm: FlatMap[a,T,C] => 
      implicit val a = fm.A
      val v0: fm.v.type = fm.v
      ctx.filter(_.curMon == fm.mon).fold[Code[Option[T],C & D]] { code"Some($directPlan)" }{ sctx =>
        code"""
          val it = ${fm.src.iterateCode}
          while(it.hasNext) {
            val $v0 = it.next
            if (${fm.mkPred}) ${fm.query.returnOrConsume(ctx)}foreach${sctx.consume}
          }
          None
        """
      }
    case Produce(cde) => code"Some($cde)"
    case wc: WithComputation[T,C] =>
      val v: wc.v.type = wc.v
      code"val ${v} = ${wc.computation}; ${wc.query.returnOrConsume(ctx)}"
    case wq: WrappedQuery[T,C] => die
  }
}

abstract class WrappedQuery[T:CodeType,-C] extends Query[T,C] {
  type Under
  implicit val Under: CodeType[Under]
  //def map(f: )
}

case class StagedSource[T:CodeType,C](cde: Code[DataSource[T],C], currentStageValue: DataSource[T] |> Option, pred: Code[T => Bool,C] |> Option) {
  def filter(p: Code[T => Bool,C]) = copy[T,C](pred = Some(pred.fold(p)(pred => code"(x:T) => $pred(x) && $p(x)")))
  def iterateCode = currentStageValue match {
    case Some(ds: StagedDataSource[T]) => ds.stagedIterator
    case Some(ds) =>
      val d = ds
      code"d.iterator"
    case None => code"$cde.iterator"
  }
  val primaryKeys = Lazy(cde.erase |>? {
    //case code"$_:IndexedDataSource[$k,$v]" => StagedSource.keysOf(k)
    case code"$_:WithPrimaryKey[$k]" => StagedSource.keysOf(k)
  })
  val columns = Lazy(StagedSource.keysOf[T])
  override def toString: String = s"${cde |> showC}${
    primaryKeys.value.fold("")(" <"+_.map(showCT).mkString(",")+">")}" + pred.fold("")(" % " concat _ |> showC)
}
object StagedSource {
  import scala.language.higherKinds
  protected type R[T]
  @compileTimeOnly("") private def R[T]: R[T] = ???
  def keysOf[T:CodeType]: List[CodeType[_]] = code"R[T]".erase match {
    case code"R[$ta ~ $tb]" => keysOf(ta) ++ keysOf(tb)
    case _ => codeTypeOf[T] :: Nil
  }
}

abstract class FlatMap[A:CodeType,B:CodeType,C](val src: StagedSource[A,C], val mon: StagedMonoid[B,C]) extends Query[B,C] {
  def A = codeTypeOf[A]
  val v: Variable[A]
  val query: Query[B, C & v.Ctx]
  
  def mkPred = {
    val v0: v.type = v
    src.pred.map(p => code"$p($v0)")getOrElse(code"true")
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

//case class OrderBy[A:CodeType,O:CodeType,C](src: Query[DataSource[A],C], ord: Code[Ordering[O],C], proj: Code[A=>O,C], desc: Code[Bool,C])
case class OrderBy[A,O,C](src: Query[DataSource[A],C], ord: Code[Ordering[O],C], proj: Code[A=>O,C], desc: Code[Bool,C])
                         (implicit val A: CodeType[A], val O: CodeType[O])
extends Query[OrderedDataSource[A],C]
{
  def getOrd = {
    val o = code"$ord on $proj"
    code"if ($desc) $o.reverse else $o"
  }
  //implicit val A = codeTypeOf[A]
  override def toString = s"OrderBy ${desc|>showC} (${ord|>showC})\n${indentString(s"$src")}"
}

import cats.Monoid

//sealed 
//abstract class StagedMonoid[T:CodeType,C](val commutes: Bool) {
abstract class StagedMonoid[T:CodeType,-C](val commutes: Bool) {
  def mkContext: MonoidContext[T,C,_]
}

//abstract class MonoidContext[T,C,D](val curMon: StagedMonoid[T,C], val consume: Code[T=>Unit,D], val get: Code[T,D]) {
abstract class MonoidContext[T,-C,D](val curMon: StagedMonoid[T,C], val consume: Code[T=>Unit,D], val get: Code[T,D]) {
  def wrap[R:CodeType](cde: Code[R,(C@uncheckedVariance) & D]): Code[R,C]  // FIXME: remove uncheckedVariance by adding a TParam
}

sealed abstract class SingleValueStagedMonoid[T:CodeType,C](commutes: Bool) extends StagedMonoid[T,C](commutes) {
  
  type Rep
  implicit val Rep: CodeType[Rep]
  val init: Code[Rep,C]
  val update: Code[(Rep,T)=>Unit,C]
  val get: Code[Rep=>T,C]
  
  def mkContext: MonoidContext[T,C,_] = {
    val cur = new Variable[Rep]
    type D = C & cur.Ctx
    new MonoidContext[T,C,D](this,code"(t:T) => $update($cur,t)",code"$get($cur)") {
      def wrap[R:CodeType](cde: Code[R,C & D]): Code[R,C] = code"val $cur = $init; $cde"
    }
  }
  
  //override def toString = this match {
  //  case RawStagedMonoid(cde) => show(cde)
  //}
}

import squid.lib.MutVar

case class RawStagedMonoid[T:CodeType,C](cde: Code[Monoid[T],C]) 
  extends VariableBasedStagedMonoid[T,C](code"$cde.empty", code"$cde.combine _", false) 
{
  override def toString = showC(cde)
}
class VariableBasedStagedMonoid[T:CodeType,C](zero: Code[T,C], combine: Code[(T,T)=>T,C], commutes: Bool) extends SingleValueStagedMonoid[T,C](commutes) {
  
  type Rep = MutVar[T]
  val Rep = codeTypeOf[MutVar[T]]
  val init = code"MutVar($zero)"
  val update = code"(r:Rep,n:T) => r := $combine(r.!, n)"
  val get = code"(r:Rep) => r.!"
  
  override def toString = s"Monoid(${showC(zero)}, ${showC(combine)})"
}
case object IntMonoid extends VariableBasedStagedMonoid(code"0", code"(_:Int)+(_:Int)", true)

case object StringMonoid extends SingleValueStagedMonoid[String,Any](false) {
  type Rep = mutable.StringBuilder
  val Rep = codeTypeOf[Rep]
  val init = code"new mutable.StringBuilder"
  val update = code"(r:Rep,t:String) => {r ++= t; ()}"
  val get = code"(r:Rep) => r.toString"
}

case class ArgMinMonoid[T:CodeType,S:CodeType,C](ord: Code[Ordering[T],C], sg: Code[cats.Semigroup[S],C])
  extends VariableBasedStagedMonoid[ArgMin[T,S],C](code"NoMin", 
    //code"???"
    code"ArgMin.monoid[T,S]($ord,$sg).combine _",  // (stupid)
    true
  )

// TODO remove the wrapper in case K is a wrapper type...

case class GroupsMonoid[K:CodeType,V:CodeType,C](asem: Code[Semigroup[V],C]) extends SingleValueStagedMonoid[Groups[K,V],C](true) {
  type Rep = mutable.ArrayBuffer[(K,V)]
  val Rep = codeTypeOf[Rep]
  val init = code"new mutable.ArrayBuffer[(K,V)]"
  val update = code"(r:Rep,t:Groups[K,V]) => GroupsMonoid.updateFrom(r,t)"
  val get = code"(r:Rep) => GroupsMonoid.reconstruct(r)($asem)"
}
object GroupsMonoid {
  def updateFrom[K,A](self: mutable.ArrayBuffer[(K,A)], that: Groups[K,A]): Unit = {
    self ++= that.toMap.iterator
  }
  def reconstruct[K,A:Semigroup](self: mutable.ArrayBuffer[(K,A)]): Groups[K,A] = {
    import cats.syntax.all._
    var i = 0
    var res = Map.empty[K,A]
    while (i < self.size) {
      val (k,a) = self(i)
      res = res.updated(k, res get k match { case Some(a0) => a |+| a0  case None => a })
      i += 1
    }
    Groups(res)
  }
}



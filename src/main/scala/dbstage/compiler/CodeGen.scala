package dbstage
package compiler

import query.{RecordSyntax=>_,_}
import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

import scala.language.higherKinds

class CodeGen {
  
  def apply[R:CodeType,C](p: QueryPlan[R,C]): Code[R,C] = rec(p)(code"()")
  
  //def rec[A:CodeType,R:CodeType,C](p: QueryPlan[A,C])(k: Code[A,p.Ctx]=>Code[S,p.Ctx]): Code[R,C] = p match {
  //def rec[A:CodeType,R:CodeType,C](p: QueryPlan[A,C])(k: Code[Unit,C&p.Ctx]): Code[R,C] = p match {
  def rec[A:CodeType,C](p: QueryPlan[A,C])(k: Code[Unit,C&p.Ctx]): Code[A,C] = p match {
      /*
    case r: Reduction[a,A,C] with p.type =>
      implicit val a = r.A
      import squid.lib.MutVar
      val cur = new Variable[MutVar[A]]
      // TOOD use pred
      code"""
        val $cur = MutVar(${r.mon.zero})
        ${rec[a,C&cur.Ctx](r.src)(code"$cur := ${r.mon.combine}($cur.!, ${apply(r.expr)}); ${k:Code[Unit,C&r.Ctx]}")}
        $cur.!
      """
      //${rec[a,A,C&cur.Ctx](r.src)(code"$cur := ${r.mon.combine}($cur.!, ${apply(r.expr)})")}
      */
    case r: Reduction[a,A,C] with p.type =>
      implicit val a = r.A
      import squid.lib.MutVar
      val cur = new Variable[MutVar[A]]
      val v: r.v.type = r.v
      // TOOD use pred
      code"""
        val $cur = MutVar(${r.mon.zero})
        ${rec[a,C&cur.Ctx](r.src)(code"$cur := ${r.mon.combine}($cur.!, ${apply(r.expr)})")}
        val $v = $cur.!
        ${k:Code[Unit,C&r.Ctx]}
        $v
      """
      
    case s: Scan[a,C] with p.type =>
      s.src match {
        case pp: PathPlan[a,as,C] =>
          implicit val A = pp.A // lol: naming it `a` instead of `A` crashes the compiler while looking for its companion!
          //code"${pp.srcEv}.iterator(${pp.src})"
          //k(code"${pp.srcEv}.iterator(${pp.src})")
          val v: s.v.type = s.v
          code"${pp.srcEv}.iterator(${pp.src}).foreach($v => ${k:Code[Unit,C&s.Ctx]}); ???"
      }
    
    //case pp @ PostProcessed(src,f) with p.type =>
    case pp: PostProcessed[a,A,C] with p.type =>
      implicit val r = pp.A
      val v: pp.v.type = pp.v
      code"val $v = ${pp.f}(${apply(pp.src)}); ${k:Code[Unit,C&pp.Ctx]}; $v"
      
    case PlainCode(cde) => code"$cde; $k".asInstanceOf[Code[A,C]]
      
    case _ => 
      println(p.toString)
      ??? // TODO
  }
  
  /*
  def apply[R:CodeType,C](p: QueryPlan[R,C]): Code[R,C] = rec(p)(code"identity[R] _")
  
  def rec[R:CodeType,S:CodeType,C](p: QueryPlan[R,C])(k: Code[R=>S,C]): Code[S,C] = p match {
      
    case r: Reduction[a,R,C] =>
      implicit val a = r.A
      //apply(r.expr):Code[R,r.src.Ctx]
      //code"""
      //  val src = ${apply(r.src)}
      //  ???
      //"""
      import squid.lib.MutVar
      val cur = new Variable[MutVar[R]]
      // TOOD use pred
      code"""
        val $cur = MutVar(${r.mon.zero})
        ${rec[a,R,C](r.src)(code"{(a:a) => val r = ${apply(r.expr)}; $cur := ${r.mon.combine}($cur.!,r); r}".asInstanceOf[Code[a=>R,C]])}
        $k($cur.!)
      """
        //${rec[a,Nothing,C](r.src)(???)}
      //???
      
    case pp @ PostProcessed(src,f) =>
      //implicit val r = pp.R
      k(code"${f}(${apply(src)})")
      
    //case s: Scan[Iterator[R],C] =>
    case s: Scan[a,C] =>
      s.src match {
        case pp: PathPlan[a,as,C] =>
          implicit val A = pp.A // lol: naming it `a` instead of `A` crashes the compiler while looking for its companion!
          //code"${pp.srcEv}.iterator(${pp.src})"
          k(code"${pp.srcEv}.iterator(${pp.src})")
      }
      //???
      
    case PlainCode(cde) => k(cde)
      
    case _ => 
      lastWords(p.toString)
      ??? // TODO
  }
  */
  
}
object CodeGen extends CodeGen

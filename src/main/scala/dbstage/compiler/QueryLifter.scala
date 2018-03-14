package dbstage
package compiler

import query._
import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
import dbstage.Embedding.HollowedCode

import scala.language.higherKinds

/*

depending on purity of nested query, accept or not extraction under uncertain eval context

*/

class QueryLifter {
  
  def apply[A:CodeType,C](q: Code[A,C]): Option[LiftedQuery[A,C]] = liftQuery(q) match {
    case lq: LiftedQuery[A,C] => Some(lq)
    case uq: UnliftedQuery[A,C] => None
  }
  
  def liftQuery[T:CodeType,C](q: Code[T,C]): QueryRepr[T,C] = {
    //println(s"\n<<-- Rec ${q|>showC}\n")
    
    object Ins extends Embedding.Inspector[T,C,LiftedQuery[T,C]] {
      def traverse[S:CodeType]: PartialFunction[Code[S,C], HollowedCode[T,S,C] => LiftedQuery[T,C]] = {
        
        //case cde @ code"readInt" => // Just a dummy case to test the nested-lifting 
        //  h => NestedQuery(h.v,MonoidEmpty(code"$cde:S"))(liftQuery(h.body))
          
        //case cde @ code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).map[S]($v => $body)($tsem)" =>
        case cde @ code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).map[S]($f)($tsem)" =>
          val res = liftSemigroup(tsem) match {
            case Right(lmon) =>
              val lifted = liftProductions[S,Option[S],C,C](cde,lmon,code"(x:S)=>Some(x)", code"true")(prods => Comprehension(prods,lmon))
              val w = new Variable[Option[S]]()
              val res = NestedQuery(w,lifted)(UnliftedQuery(code"$w.get"))
              //println(res)
              res
            case Left(lmon) =>
              die // TODO
          }
          h => NestedQuery[T,S,C](h.v,res)(liftQuery(h.body))
          
        case cde @ code"OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,S]($v => $body)($into,$mmon)" =>
          
          val lmon = liftMonoid(mmon)
          val lifted = liftProductions[S,S,C,C](cde,lmon,code"identity[S] _", code"true")(prods => Comprehension(prods,lmon))
          h => NestedQuery[T,S,C](h.v,lifted)(liftQuery(h.body))
          
      }
    }
    
    Ins(q) match {
      case Left(cde) => UnliftedQuery(cde)
      case Right(lq) =>
        //println(lq)
        //die
        lq
    }
  }
  /*
  //abstract class LiftedQueryK[A,R,C,F[_]] {
  abstract class LiftedQueryK[R,E,F[_]] {
    def apply[D<:E](x: F[D]): LiftedQuery[R,D] = impl(x)
    def impl[D<:E]: F[D] => LiftedQuery[R,D
  }
  abstract class ProductionsK[R,E] extends LiftedQueryK[R,E,Productions[R,?]]
  abstract class StagedDataSourceK[A,R,C] extends LiftedQueryK[R,C,StagedDataSource[A,?]]
  */
  
  def liftProductions[A:CodeType,R:CodeType,C<:E,E]
  (q: Code[A,C], lmon: StagedMonoid[R,E], f: Code[A=>R,C], pred: Code[Bool,C])(k: Productions[R,C] => LiftedQuery[R,E]): LiftedQuery[R,E]
  = q match {
    case code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).map[A]($v => $body)($tsem)" =>
      liftSemigroup(tsem) match {
        case Right(lmon2) =>
          if (lmon == lmon2)
            liftDataSource[at.Typ,ast.Typ,C,E,R](lmon,as,afin)((ds,p) =>
              liftProductions[A,R,C&v.Ctx,E](body, lmon, f, code"$pred&&$p(${v.toCode})")({prods =>
                k(Iteration(ds,v)(prods))
                //k(Iteration[at.Typ,R,C](ds,v)(prods))
              })
            )
          else lastWords(s"TODO: different monoid $lmon and $lmon2")
        case Left(lmon) =>
          die // TODO
      }
    case code"OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,A]($v => $body)($into,$mmon)" =>
      val lmon2 = liftMonoid(mmon)
      //println(s">>> INTO $into")
      if (lmon == lmon2)
        liftDataSource[at.Typ,ast.Typ,C,E,R](lmon,as,afin)((ds,p) =>
          liftProductions[rt.Typ,R,C&v.Ctx,E](body, lmon, code"(x:$rt)=>$f($into(x))",code"$pred&&$p(${v.toCode})")({prods =>
            k(Iteration(ds,v)(prods))
            //k(Iteration[at.Typ,R,C](ds,v)(prods))
          })
        )
      else lastWords(s"TODO: different monoid $lmon and $lmon2")
    case r =>
      println(s"YIELD:\n${indentString(r|>showC)}")
      k(Yield(liftQuery(pred), liftQuery(
        f(q)
      )))
  }
  
  def liftDataSource[A:CodeType,As:CodeType,C<:E,E,R:CodeType]
  (lmon:StagedMonoid[R,E],cde: Code[As,C], srcEv: Code[As SourceOf A,C])(k: (StagedDataSource[A,C],Code[A=>Bool,C]) => LiftedQuery[R,E]): LiftedQuery[R,E]
  = cde match {
    //case code"SourceOps[As,A]($as)($asrc).withFilter($pred)" => // Note: will NOT match because `withFilter` wraps into `Filtered`
    case code"SourceOps[$ast,A]($as)($asrc).withFilter($pred)" => // here type As = Filtered[A,ast.Typ]
      liftDataSource[A,ast.Typ,C,E,R](lmon,as,asrc)((ds,p) => k(ds,code"(a:A)=>$p(a)&&$pred(a)"))
    case code"if ($cond) $thn else $els : As" =>
      liftDataSource[A,As,C,E,R](lmon,thn,srcEv)((thn,p0) => liftDataSource[A,As,C,E,R](lmon,els,srcEv)((els,p1) => {
        val pred = code"(a:A)=>$p0(a)&&$p1(a)"
        MonoidMerge(lmon,k(thn,pred),k(els,pred))
      }))
    case cde =>
      println(s"SOURCE:\n${indentString(cde|>showC)}")
      k(StagedDataSourceOf(liftQuery(cde),srcEv),code"(a:A)=>true")
  }
  
  def liftMonoid[S:CodeType,C](cde: Code[Monoid[S],C]): StagedMonoid[S,C] = cde match {
    //case code"" => // TODO cases known to the query compiler
    case code"$_:CommutativeMonoid[S]" =>
      RawStagedMonoid(cde,true,false)
    case _ =>
      RawStagedMonoid(cde,false,false)
  }
  //def liftSemigroup[S:CodeType,C](cde: Code[Semigroup[S],C]): StagedMonoid[S,C] =
  //  //new StagedMonoid[S,C](false,false){} // TODO
  //  RawStagedSemigroup[S,C](cde,false,false).asInstanceOf[StagedMonoid[S,C]] // FIXME
  def liftSemigroup[S:CodeType,C](cde: Code[Semigroup[S],C]): Either[StagedMonoid[S,C],StagedMonoid[Option[S],C]] = cde match {
    //case code"" => // TODO cases known to the query compiler
    case code"$_:CommutativeSemigroup[S]" =>
      Right(RawStagedSemigroup[S,C](cde,true,false))
    case cde =>
      Right(RawStagedSemigroup[S,C](cde,false,false))
  }
  
}
object QueryLifter extends QueryLifter

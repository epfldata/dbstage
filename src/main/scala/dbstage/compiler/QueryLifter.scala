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

/*

depending on purity of nested query, accept or not extraction under uncertain eval context

*/

class QueryLifter {
  
  def apply[A:CodeType,C](q: Code[A,C]): Option[LiftedQuery[A,C]] = liftQuery(q) match {
    case lq: LiftedQuery[A,C] => Some(lq)
    case uq: UnliftedQuery[A,C] => None
  }
  
  def liftQuery[T:CodeType,C](q: Code[T,C]): QueryRepr[T,C] = {
    println(s"\n<<-- Rec ${q|>showC}\n")
    object Ins extends Embedding.Inspector[T,C,LiftedQuery[T,C]] {
      def traverse[S:CodeType]: PartialFunction[Code[S,C], HollowedCode[T,S,C] => LiftedQuery[T,C]] = {
        case cde @ code"readInt" => // Just a dummy case to test the nested-lifting 
          h => NestedQuery(h.v,MonoidEmpty(code"$cde:S"))(liftQuery(h.body))
        case code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).map[S]($v => $body)($tsem)" =>
          /*
          val lmon = liftSemigroup(tsem)
          //liftProductions[as.Typ,S,C&v.Ctx,C](body, lmon)(prods => Comprehension(prods, lmon)) // FIXME iter
          liftDataSource[at.Typ,ast.Typ,C,S](as,afin)(ds =>
            liftProductions(body.asInstanceOf[Code[S,C]], // FIXME actually need HKP
              lmon)(prods => 
              Comprehension(Iteration[at.Typ,S,C](ds,v)(prods), lmon)
            )
          )
          */
          
          // TODO factor with case in leftProductions
          val res =
          liftSemigroup(tsem) match {
            case Right(lmon) =>
              val lifted =
              liftDataSource[at.Typ,ast.Typ,C,Option[S]](as,afin)(ds =>
                liftProductions(body.asInstanceOf[Code[S,C]], // FIXME actually need HKP
                  lmon, code"(x:S)=>Some(x)")(prods => 
                  Comprehension(Iteration[at.Typ,Option[S],C](ds,v)(prods), lmon)
                )
              )
              val w = new Variable[Option[S]]()
              val res = NestedQuery(w,lifted)(UnliftedQuery(code"$w.get"))
              //println(res)
              res
            case Left(lmon) =>
              die // TODO
          }
          h => NestedQuery[T,S,C](h.v,res)(liftQuery(h.body))
          
        case code"OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,T]($v => $body)($into,$mmon)" =>
          ???
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
  
  //def liftProductionsAndApply[T:CodeType,R:CodeType,C](q: Code[T,C], lmon: StagedMonoid[R,C], f: Code[T=>R,C]): Productions[R,C] = q match {
  //def liftProductions[A:CodeType,R:CodeType,C,D](q: Code[A,C], lmon: StagedMonoid[R,C])
  //                                            (k: Productions[R,C] => LiftedQuery[R,D]): LiftedQuery[R,D] = q match
  def liftProductions[A:CodeType,R:CodeType,C](q: Code[A,C], lmon: StagedMonoid[R,C], f: Code[A=>R,C])
                                              (k: Productions[R,C] => LiftedQuery[R,C]): LiftedQuery[R,C] = q match 
  {
    case code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).map[A]($v => $body)($tsem)" =>
      /*
      val lmon2 = liftSemigroup(tsem)
      if (lmon == lmon2) 
        liftDataSource(as,afin)(ds =>
          liftProductions(body, lmon, f)(prods =>
            k(Iteration[at.Typ,R,C](ds,v)(prods))
          ).asInstanceOf[LiftedQuery[R,C]] // FIXME actually need HKP
        )
        //???
      else lastWords(s"TODO: different monoid $lmon and $lmon2")
      */
      liftSemigroup(tsem) match {
        case Right(lmon2) =>
          if (lmon == lmon2) 
            liftDataSource(as,afin)(ds =>
              liftProductions(body, lmon, f)(prods =>
                k(Iteration[at.Typ,R,C](ds,v)(prods))
              ).asInstanceOf[LiftedQuery[R,C]] // FIXME actually need HKP
            )
            //???
          else lastWords(s"TODO: different monoid $lmon and $lmon2")
        case Left(lmon) =>
          die // TODO
      }
      //???
    case code"OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,A]($v => $body)($into,$mmon)" =>
      ???
    case r =>
      println(s"YIELD:\n${indentString(r|>showC)}")
      k(Yield(liftQuery(code"true"), liftQuery(f(q))))
  }
  def liftDataSource[A:CodeType,As:CodeType,C,R:CodeType](cde: Code[As,C], srcEv: Code[As SourceOf A,C])
                                                //(k: StagedDataSource[A,C] => Comprehension[R,C]): Comprehension[R,C] = cde match 
                                                (k: StagedDataSource[A,C] => LiftedQuery[R,C]): LiftedQuery[R,C] = cde match 
  {
    case code"if ($cond) $thn else $els : As" =>
      liftDataSource(thn,srcEv)(thn => liftDataSource(els,srcEv)(els => MonoidMerge(k(thn),k(els))))
    case cde =>
      println(s"SOURCE:\n${indentString(cde|>showC)}")
      k(StagedDataSourceOf(liftQuery(cde),srcEv))
  }
  
  def liftMonoid[S:CodeType,C](cde: Code[Monoid[S],C]): StagedMonoid[S,C] = ???
  //def liftSemigroup[S:CodeType,C](cde: Code[Semigroup[S],C]): StagedMonoid[S,C] =
  //  //new StagedMonoid[S,C](false,false){} // TODO
  //  RawStagedSemigroup[S,C](cde,false,false).asInstanceOf[StagedMonoid[S,C]] // FIXME
  def liftSemigroup[S:CodeType,C](cde: Code[Semigroup[S],C]): Either[StagedMonoid[S,C],StagedMonoid[Option[S],C]] = cde match {
    case cde =>
      Right(RawStagedSemigroup[S,C](cde,false,false))
  }
  
}
object QueryLifter extends QueryLifter

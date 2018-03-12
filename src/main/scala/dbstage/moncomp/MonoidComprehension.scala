package dbstage
package moncomp

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._
  
/*

TODO
  
  front end: a `where` extension method taking an IntoMonoid value and making it Empty if the condition is false...
  
  reliably lift comprehensions nested inside some uninterpreted computation; useful at the top-level and in predicates and yield exprs
    has to lift _all_ such sibling nested comprehensions
    use MethodApplication xtor...
  
  handle and normalize data source projection/filtering (actually desugar to a comprehension) and record construction
  
  (?) simplify lifting by normalizing all map variations to the simplest aggregation on Monoid? (StagedMonoid does retain the Monoid properties)
        including aggrs on non-empty-sources w/ semigroups: aggr on option and apply unsafe get on result? -> we should represent this as a subclass of Comprehension, in any case
  
  need to put liftProductions in CPS because liftDataSource may generate not a production but a merge of two distinct comprehensions!!
  
  define Unit and Merge (or Empty and Combine) extractors that can decompose the canonical expressions of the usual monoids
  
  in the normalizer, do graph-reduction?! just store a CSE table of queries...
  also automatically lift all pure intermediate expressions out of the inner queries
  
  handle zipping... how?
  
  transform groupBy on indices to a monoidal vector comprehension? eg:
    for { (x,i) <- ls.zipWithIndex } yield x.groupBy(ls.size - i - 1)  // vector reversal
  histogram computation:
    for { x <- set } yield x.groupBy((set.max - x) * 100)  // set.max should be lifted out of the query!

*/

//sealed class MonoidComprehension {
//}
//case class Path() extends MonoidComprehension

abstract 
//case class Comprehension[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]) extends QueryRepr[R,C]
case class Comprehension[R:CodeType,C](productions: Productions[R,C], mon: StagedMonoid[R,C]) extends LiftedQuery[R,C] {
  override def toString = s"Comp ($mon)\n${indentString(s"$productions")}"
}
object Comprehension {
  // TODOne if `mon` is SortedBy.monoid, simplify the comprehension and apply Sorting
  //def apply[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]): QueryRepr[R,C] = QueryCompiler.liftMonoid(mon) match {
  def apply[R:CodeType,C](productions: Productions[R,C], mon: StagedMonoid[R,C]): LiftedQuery[R,C] = mon match {
    case sbsm: SortedByStagedMonoid[a,o,C] =>
      //import sbsm.{As,O} // doesn't work... why?!
      implicit val as = sbsm.As
      implicit val o = sbsm.O
      Sorting2(Comprehension(productions.mapYield(code"(sb:a SortedBy o) => sb.as"),sbsm.underlying))
    case lmon => 
      //lastWords(mon.toString)
      new Comprehension[R,C](productions,lmon){}
  }
}

//sealed abstract class Productions[R:CodeType,C]
sealed abstract class Productions[R,C] {
  def mapYield[S:CodeType](f: Code[R=>S,C]): Productions[S,C] = this match {
    //case ite:Iteration[a,as,R,C] =>
    //  implicit val A = ite.A
    //  implicit val As = ite.As
    //  Iteration(ite.src,ite.v)(ite.body.mapYield(f))
    case ite:Iteration[a,R,C] =>
      implicit val A = ite.A
      Iteration(ite.src,ite.v)(ite.body.mapYield(f))
    //case Yield(pred,cde) => Yield(pred,f(cde))
    case Yield(pred,cde) => Yield(pred,cde.mapCode(f))
  }
  def withFilter(pred: Code[Bool,C]): Productions[R,C] = this match {
    case ite:Iteration[a,R,C] =>
      implicit val A = ite.A
      Iteration(ite.src,ite.v)(ite.body.withFilter(pred))
    case Yield(pred0,cde) =>
      //Yield(code"$pred0 && $pred",cde)
      Yield(pred0.mapCode[Bool](code"(p0:Bool) => p0 && $pred"),cde)
  }
}
//abstract class Iteration[A:CodeType,As:CodeType,R,C](src: StagedDataSource[A,As,C]) extends Productions[R,C] {
//abstract class Iteration[A:CodeType,As:CodeType,R,C](val src: Path[A,As,C]) extends Productions[R,C] {
abstract class Iteration[A:CodeType,R,C](val src: Path[A,C]) extends Productions[R,C] {
  def A = codeTypeOf[A]
  //def As = codeTypeOf[As]
  val v: Variable[A]
  val body: Productions[R,C & v.Ctx]
  
  def withFilter(pred: Code[A=>Bool,C]): Iteration[A,R,C] = Iteration(src,v)(body.withFilter(
    //pred(v.toCode) // found: dbstage.Embedding.Code[Iteration.this.v.Typ,Iteration.this.v.Ctx]; required: dbstage.Embedding.Code[A,C]
    code"$pred(${v.toCode})"
  ))
  
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
case class Yield[R,C](pred: LiftedQuery[Bool,C], cde: LiftedQuery[R,C]) extends Productions[R,C] {
  override def toString: String = s"if ${pred}\nyield ${cde}"
}
object Yield {
  //def apply[R,C](pred: Code[Bool,C], cde: Code[R,C])(enclosingMonoid: StagedMonoid[R,C]) = cde match {
  //  case 
  //}
}

//sealed abstract class Path[A:CodeType,As:CodeType,C]
sealed abstract class Path[A:CodeType,C]

//abstract
//case class StagedDataSource[A:CodeType,As:CodeType,C](cde: Code[As,C], srcEv: Code[As SourceOf A,C]) extends Path[A,As,C]
sealed abstract class StagedDataSource[A:CodeType,C] extends Path[A,C]
//case class StagedDataSourceOf[A:CodeType,As:CodeType,C](cde: Code[As,C], srcEv: Code[As SourceOf A,C]) extends StagedDataSource[A,C]
case class StagedDataSourceOf[A:CodeType,As:CodeType,C](query: LiftedQuery[As,C], srcEv: Code[As SourceOf A,C]) extends StagedDataSource[A,C]

//case class SingleElement[A:CodeType,C](cde: Code[A,C]) extends Path[A,A,C]
//case class SingleElement[A:CodeType,C](cde: Code[A,C]) extends Path[A,C]
case class SingleElement[A:CodeType,C](query: LiftedQuery[A,C]) extends Path[A,C]

//case class Query[A:CodeType,B:CodeType,C](body: Comprehension[A,C], postProcess: Code[A=>B,C])
abstract class LiftedQuery[A:CodeType,C] //extends Path[A,C]
{
  def mapCode[B:CodeType](f: Code[A=>B,C]): LiftedQuery[B,C] = ???
}
case class Sorting[A:CodeType,As:CodeType,C](underlying: LiftedQuery[As,C], asSrc: StagedDataSourceOf[A,As,C], ord: StagedOrdering[A,C])
  extends LiftedQuery[SortedBy[A,A],C]
//case class Sorting2[A:CodeType,O:CodeType,As:CodeType,C](underlying: QueryRepr[As,C])
case class Sorting2[As:CodeType,O:CodeType,C](underlying: LiftedQuery[As,C])
  extends LiftedQuery[SortedBy[As,O],C]
{
  override def toString = s"$underlying\nsort by ${codeTypeOf[O]|>showCT}"
}

case class Uninterpreted[A:CodeType,C](result: Code[A,C]) extends LiftedQuery[A,C] {
  override def mapCode[B:CodeType](f: Code[A=>B,C]): LiftedQuery[B,C] = Uninterpreted(f(result))
}

//case class MonoidUnit[M:CodeType,C](value: LiftedQuery[M,C]) extends LiftedQuery[M,C]
case class MonoidUnit[A:CodeType,M:CodeType,C](value: LiftedQuery[A,C], unit: Code[A=>M,C]) extends LiftedQuery[M,C] {
  //override def mapCode[B:CodeType](f: Code[A=>B,C]): LiftedQuery[B,C] = MonoidUnit[A,M,C](value, code"$unit andThen $f") // nope
}


object QueryLifter {
  
  // TODO use CPS to wrap a whole result with .sort if a Sorted is encountered?
  
  def liftQuery[T:CodeType,C](q: Code[T,C]): LiftedQuery[T,C] = println(s"\n<<-- Rec ${q|>showC}\n") thenReturn (q match {
    case code"val $v: $vt = $init; $body: T" =>
      //letin[vt.Typ,T,C](v,init)(body)
      letin(v,init)(liftQuery(body))
    case code"Abstracts.absSortedBy[$ta,$tas,$to]($oord,$src,$proj).apply($as)" =>
      println(s"A ${as|>showC}")
      val las = liftQuery[SortedBy[tas.Typ,to.Typ],C](as)
      //lastWords(s"${las|>showC}")
      //lastWords(s"${las}")
      //println(las)
      //die
      las.asInstanceOf[LiftedQuery[T,C]] // FIXME
    //case code"moncomp.`package`.OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,$mt]($v => $body)($into,$mmon)" =>
    case code"moncomp.`package`.OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,T]($v => $body)($into,$mmon)" =>
      //type mt = T
      //println(as)
      //println(body)
      //println(liftQuery(body))
      //val lmon = QueryCompiler.liftMonoid[T,C & v.Ctx](mmon)
      val lmon = QueryCompiler.liftMonoid(mmon)
      //Comprehension[rt.Typ,C](
      Comprehension[T,C](
        //Iteration[at.Typ,ast.Typ,rt.Typ,C](StagedDataSource(as,afin),v)(liftProductions(body)),
        liftDataSource(as,afin)(ds =>
        Iteration[at.Typ,T,C](
          //StagedDataSource(as,afin),
          ds,
          v
        )(
          //liftProductions(code"$into.apply($body)", lmon)
          liftProductionsAndApply(body, lmon, code"$into.apply _")
          //liftProductions(body, lmon).mapYield ...
        )),
        //mmon.asInstanceOf[Code[Monoid[rt.Typ],C]] // FIXME wrong!
        //mmon
        lmon
      )
      //???
      
    case code"moncomp.`package`.FiniteOps[$ast,$at]($as)($afin).orderingBy[$ot]($oord,$aoproj)" =>
      /*
      // weird: Embedding Error: Could not find type evidence associated with extracted type `dbstage.moncomp.QueryLifter.at.Typ`.
      Sorting[at.Typ,ast.Typ,C](liftQuery(as), StagedDataSourceOf(afin), new StagedOrdering[at.Typ,C]()(at){})
        // TODO the ordering should be on at (adaptation needed from ot)
        .asInstanceOf[QueryRepr[T,C]]
      */
      // Note from dbg_code: "Subtyping knowledge: dbstage.moncomp.SortedBy[ast,ot] <:< T"
      
      ??? // TODO
      
    case code"any($b)" =>
      //MonoidUnit[ExistsAny,C](liftQuery(b),)
      MonoidUnit(liftQuery(b),code"any(_)")
        .asInstanceOf[LiftedQuery[T,C]] // FIXME gadt
    case code"all($b:($bt where (bt <:< Bool)))" =>  // note: important subtype match
      MonoidUnit(liftQuery(b),code"all(_)")
        .asInstanceOf[LiftedQuery[T,C]] // FIXME gadt
      
    case r => 
      //println(codeTypeOf[T])
      //die //Query(r, code"idenityt[T] _")
      //lastWords(s"Unhandled: ${showC(q)}")
      //Comprehension(Yield(code"true", r),)
      Uninterpreted(r)
  }) alsoApply (r => println(s"\n-->> $r"))
  def liftComprehension[T:CodeType,C](q: Code[T,C]) = q match {
    case r => die
  }
  //def liftProductions[T:CodeType,C](q: Code[T,C], lmon: StagedMonoid[T,C]): Productions[T,C] = q match {
  def liftProductions[T:CodeType,C](q: Code[T,C], lmon: StagedMonoid[T,C]): Productions[T,C] = liftProductionsAndApply(q,lmon,code"identity[T] _")
  def liftProductionsAndApply[T:CodeType,R:CodeType,C](q: Code[T,C], lmon: StagedMonoid[R,C], f: Code[T=>R,C]): Productions[R,C] = q match {
    // TODO handle effects here...
    case code"moncomp.`package`.OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,T]($v => $body)($into,$mmon)" =>
      val lmon2 = QueryCompiler.liftMonoid(mmon)
      //if (lmon == lmon2) Iteration[at.Typ,ast.Typ,T,C](StagedDataSource(as,afin),v)(liftProductions(code"$into.apply($body)", lmon))
      //if (lmon == lmon2) Iteration[at.Typ,ast.Typ,R,C](StagedDataSource(as,afin),v)(liftProductionsAndApply(body, lmon, code"(x:$rt)=>$f($into(x))"))
      if (lmon == lmon2) 
        liftDataSource(as,afin)(ds =>
          Iteration[at.Typ,R,C](ds,v)(liftProductionsAndApply(body, lmon, code"(x:$rt)=>$f($into(x))"))
        )
      else lastWords("TODO: different monoid")
    //case code"moncomp.`package`.OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,$tt]($v => $body)($into,$mmon)" =>
    //  ???
    case r =>
      println(s"YIELD:\n${indentString(r|>showC)}")
      //Yield(code"true", q)
      //Yield(code"true", f(q))
      Yield(liftQuery(code"true"), liftQuery(q).mapCode(f))
      //die
  }
  //def letin[T:CodeType,R:CodeType,C](v: Variable[T], init: Code[T,C])(body: Code[R,C&v.Ctx]) = {
  def letin[T:CodeType,R:CodeType,C](v: Variable[T], init: Code[T,C])(body: LiftedQuery[R,C&v.Ctx]) = {
    println(v,init,body)
    ???
  }
  // in CPS because we may discover nested aggregates as well as filter predicates in a source
  //def liftDataSource[A:CodeType,As:CodeType,C,R](cde: Code[As,C], srcEv: Code[As SourceOf A,C])(k: StagedDataSource[A,As,C] => R): R = cde match {
  //def liftDataSource[A:CodeType,As:CodeType,C,R](cde: Code[As,C], srcEv: Code[As SourceOf A,C])(k: StagedDataSource[A,C] => R): R = cde match {
  def liftDataSource[A:CodeType,As:CodeType,C,R](cde: Code[As,C], srcEv: Code[As SourceOf A,C])(k: StagedDataSource[A,C] => Iteration[A,R,C]): Iteration[A,R,C] = cde match {
  //def liftDataSource[A:CodeType,As:CodeType,C,R](cde: Code[As,C], srcEv: Code[As SourceOf A,C], curPred: Code[A=>Bool,C])(k: StagedDataSource[A,As,C] => Productions[R,C]): Productions[R,C] = cde match {
  //  case code"OrderedOps[$ast,$at]($as)($aord,$afin).withFilter($pred)" =>
    case code"OrderedOps[$ast,A]($as)($aord,$afin).withFilter($pred)" =>
    //case code"OrderedOps[As,A]($as)($aord,$afin).withFilter($pred)" =>
      // type As = Filtered[ast,at]
      liftDataSource(as,afin)(k).withFilter(pred)
      //liftDataSource(as,afin,pred)(k)
    //case code"OrderedOps[$ast,$at]($as)($aord,$afin).withFilter($pred)" =>
    //  println(codeTypeOf[As],codeTypeOf[A])
    //  println(ast,at)
    //  ???
    case code"if ($cond) $thn else $els : As" =>
      // TODO require commutative monoid; + merge with else branch
      liftDataSource(thn,srcEv)(k)  // FIXME <-------------------------------------------------------------------------
    case cde => 
      println(s"SOURCE:\n${indentString(cde|>showC)}")
      //k(StagedDataSourceOf(cde,srcEv)) // TODO
      k(StagedDataSourceOf(liftQuery(cde),srcEv)) // TODO
  }
  
  
}


// TODO
abstract class StagedOrdering[A:CodeType,C]

// TODO
//case class StagedDataSourceOf[A:CodeType,As:CodeType,C](cde: Code[As SourceOf A, C])


case class SortedByStagedMonoid[As:CodeType,O:CodeType,C](underlying: StagedMonoid[As,C]) extends StagedMonoid[SortedBy[As,O],C](underlying.commutes) {
  implicit def As = codeTypeOf[As]
  implicit def O = codeTypeOf[O]
  def mkContext: MonoidContext[SortedBy[As,O],C,_] = ???
}




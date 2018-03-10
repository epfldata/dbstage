package dbstage
package moncomp

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._

//sealed class MonoidComprehension {
//}
//case class Path() extends MonoidComprehension

abstract 
//case class Comprehension[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]) extends QueryRepr[R,C]
case class Comprehension[R:CodeType,C](productions: Productions[R,C], mon: StagedMonoid[R,C]) extends QueryRepr[R,C] {
  override def toString = s"Comp ($mon)\n${indentString(s"$productions")}"
}
object Comprehension {
  // TODOne if `mon` is SortedBy.monoid, simplify the comprehension and apply Sorting
  //def apply[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]): QueryRepr[R,C] = QueryCompiler.liftMonoid(mon) match {
  def apply[R:CodeType,C](productions: Productions[R,C], mon: StagedMonoid[R,C]): QueryRepr[R,C] = mon match {
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
    case ite:Iteration[a,as,R,C] =>
      implicit val A = ite.A
      implicit val As = ite.As
      Iteration(ite.src,ite.v)(ite.body.mapYield(f))
    case Yield(pred,cde) => Yield(pred,f(cde))
  }
}
//abstract class Iteration[A:CodeType,As:CodeType,R,C](src: StagedDataSource[A,As,C]) extends Productions[R,C] {
abstract class Iteration[A:CodeType,As:CodeType,R,C](val src: Path[A,As,C]) extends Productions[R,C] {
  def A = codeTypeOf[A]
  def As = codeTypeOf[As]
  val v: Variable[A]
  val body: Productions[R,C & v.Ctx]
  
  //override def toString: String = s"Iteration($src,$v,$body)"
  override def toString: String = s"${v.rep|>base.showRep} <- $src\n${/*indentString*/(body.toString)})"
}
object Iteration {
  def apply[A:CodeType,As:CodeType,R,C](src: Path[A,As,C], v0: Variable[A])(body0: Productions[R,C & v0.Ctx]) = new Iteration[A,As,R,C](src) {
    val v: v0.type = v0
    val body = body0
  } 
}
// TODO Binding? -> defined SingleElement instead

//case class Predicate[C]() extends Productions[C]
//abstract 
case class Yield[R,C](pred: Code[Bool,C], cde: Code[R,C]) extends Productions[R,C] {
  override def toString: String = s"if ${pred|>showCbound}\nyield ${cde|>showCbound}"
}
object Yield {
  //def apply[R,C](pred: Code[Bool,C], cde: Code[R,C])(enclosingMonoid: StagedMonoid[R,C]) = cde match {
  //  case 
  //}
}

sealed abstract class Path[A:CodeType,As:CodeType,C]
case class StagedDataSource[A:CodeType,As:CodeType,C](cde: Code[As,C], srcEv: Code[As SourceOf A,C]) extends Path[A,As,C] {
  
}
case class SingleElement[A:CodeType,C](cde: Code[A,C]) extends Path[A,A,C]

//case class Query[A:CodeType,B:CodeType,C](body: Comprehension[A,C], postProcess: Code[A=>B,C])
abstract class QueryRepr[A:CodeType,C]
case class Sorting[A:CodeType,As:CodeType,C](underlying: QueryRepr[As,C], asSrc: StagedDataSourceOf[A,As,C], ord: StagedOrdering[A,C])
  extends QueryRepr[SortedBy[A,A],C]
//case class Sorting2[A:CodeType,O:CodeType,As:CodeType,C](underlying: QueryRepr[As,C])
case class Sorting2[As:CodeType,O:CodeType,C](underlying: QueryRepr[As,C])
  extends QueryRepr[SortedBy[As,O],C]
{
  override def toString = s"$underlying\nsort by ${codeTypeOf[O]|>showCT}"
}

case class Return[A:CodeType,C](result: Code[A,C]) extends QueryRepr[A,C]

object QueryLifter {
  
  // TODO use CPS to wrap a whole result with .sort if a Sorted is encountered?
  
  def liftQuery[T:CodeType,C](q: Code[T,C]): QueryRepr[T,C] = println(s"\n<<-- Rec ${q|>showC}\n") thenReturn (q match {
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
      las.asInstanceOf[QueryRepr[T,C]] // FIXME
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
        Iteration[at.Typ,ast.Typ,T,C](StagedDataSource(as,afin),v)(
          //liftProductions(code"$into.apply($body)", lmon)
          liftProductionsAndApply(body, lmon, code"$into.apply _")
          //liftProductions(body, lmon).mapYield ...
        ),
        //mmon.asInstanceOf[Code[Monoid[rt.Typ],C]] // FIXME wrong!
        //mmon
        lmon
      )
      //???
    case code"moncomp.`package`.FiniteOps[$ast,$at]($as)($afin).orderingBy[$ot]($oord,$aoproj)" =>
      // weird: Embedding Error: Could not find type evidence associated with extracted type `dbstage.moncomp.QueryLifter.at.Typ`.
      Sorting[at.Typ,ast.Typ,C](liftQuery(as), StagedDataSourceOf(afin), new StagedOrdering[at.Typ,C]()(at){})
        // TODO the ordering should be on at (adaptation needed from ot)
        .asInstanceOf[QueryRepr[T,C]]
      // Note from dbg_code: "Subtyping knowledge: dbstage.moncomp.SortedBy[ast,ot] <:< T"
    case r => 
      //die //Query(r, code"idenityt[T] _")
      //lastWords(s"Unhandled: ${showC(q)}")
      //Comprehension(Yield(code"true", r),)
      Return(r)
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
      if (lmon == lmon2) Iteration[at.Typ,ast.Typ,R,C](StagedDataSource(as,afin),v)(liftProductionsAndApply(body, lmon, code"(x:$rt)=>$f($into(x))"))
      else lastWords("TODO: different monoid")
    case r =>
      println(s"YIELD:\n${indentString(r|>showC)}")
      //Yield(code"true", q)
      Yield(code"true", f(q))
      //die
  }
  //def letin[T:CodeType,R:CodeType,C](v: Variable[T], init: Code[T,C])(body: Code[R,C&v.Ctx]) = {
  def letin[T:CodeType,R:CodeType,C](v: Variable[T], init: Code[T,C])(body: QueryRepr[R,C&v.Ctx]) = {
    println(v,init,body)
    ???
  }
  
  
  
}


// TODO
abstract class StagedOrdering[A:CodeType,C]

// TODO
case class StagedDataSourceOf[A:CodeType,As:CodeType,C](cde: Code[As SourceOf A, C])


case class SortedByStagedMonoid[As:CodeType,O:CodeType,C](underlying: StagedMonoid[As,C]) extends StagedMonoid[SortedBy[As,O],C](underlying.commutes) {
  implicit def As = codeTypeOf[As]
  implicit def O = codeTypeOf[O]
  def mkContext: MonoidContext[SortedBy[As,O],C,_] = ???
}




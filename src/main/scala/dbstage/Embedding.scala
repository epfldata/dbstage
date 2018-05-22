package dbstage

import cats.{Monoid,Semigroup}
import squid.utils._
import squid.anf.analysis.BlockHelpers
import squid.anf.transfo.{
  EqualityNormalizer
, LogicFlowNormalizer
, LogicNormalizer
, OptionNormalizer
, FunctionNormalizer
//, EffectsNormalizer
, StandardNormalizer
, VarFlattening
}
import squid.ir.{
  CrossStageAST
, CurryEncoding
, FixPointRuleBasedTransformer
, FixPointTransformer
, OnlineOptimizer
, SchedulingANF
, SimpleANF
, SimpleRuleBasedTransformer
, StandardEffects
, TopDownTransformer
}
import squid.lang.ScalaCore
import query._

object Embedding 
  extends SimpleANF 
  //extends SchedulingANF 
    with StandardEffects 
    with OnlineOptimizer
    ////with StandardNormalizer 
    //with LogicNormalizer
    //with OptionNormalizer // note: needed by VarFlattening
    with BlockHelpers
    with ScalaCore
    //with EqualityNormalizer
    //with CurryEncoding.ApplicationNormalizer
    with CrossStageAST
{
  embed(EmbeddedDefs)
  embed(AbstractsLoPri)
  embed(IntoMonoid)
  embed(IntoMonoidLowPrio)
  embed(IntoMonoidLowPrio2)
  //embed(CanAccess)
  //embed(ProjectsOn)
  //embed(ProjectLowPrio)
  //embed(ProjectLowPrio2)
  //embed(MonoidSyntax)
  //embed(Read)
  //embed(RecordRead)
  //embed(RecordReadLowPrio)
  //embed(SemigroupSyntax)
  ////embed(Count)
  //embed(PairUp)
  //embed(PairUpNorm)
  //embed(PairUpLowPriority0)
  //embed(PairUpLowPriority1)
  //embed(PairUpLowPriority2)
  //embed(Normalizes)
  //embed(NormalizesLowPriority0)
  //embed(NormalizesLowPriority1)
  
  // --- //
  
  //embed(
  //  moncomp.Abstracts,
  //  moncomp.AbstractsLoPri,
  //  moncomp.SortedBy,
  //  moncomp.IntoMonoid,
  //  moncomp.IntoMonoidLowPrio,
  //  //LowPrioImplicits,
  //  MonCompEmbeddedDefs
  //)
  
  //override val bindEffects = true  // FIXME causes SOF
  
  //import Predef._
  
  //def pipeline = OnlineRewritings.pipeline
  def pipeline = OnlineTransformations.pipeline
  
  //transparencyPropagatingMtds += methodSymbol[Iterator[Any]]("map") // nah good without scheduling! and wrong anyway, as Iterator is stateful...
  
  //transparencyPropagatingMtds += methodSymbol[~.type]("apply")
  //transparencyPropagatingMtds += methodSymbol[Any~Any]("lhs")
  //transparencyPropagatingMtds += methodSymbol[Any~Any]("rhs")
  //transparencyPropagatingMtds += methodSymbol[CanAccess.type]("apply")
  //transparencyPropagatingMtds += methodSymbol[ProjectsOn.type]("apply")
  //transparencyPropagatingMtds += methodSymbol[Bag.type]("empty")
  //transparencyPropagatingMtds += methodSymbol[Bag.type]("apply")
  //transparencyPropagatingMtds += methodSymbol[TraversableOnce[Any]]("toMap")
  //transparencyPropagatingMtds += methodSymbol[scala.Ordering.type]("by")
  //transparencyPropagatingMtds += methodSymbol[scala.Ordering[Any]]("on")
  //transparencyPropagatingMtds += methodSymbol[scala.Ordering[Any]]("reverse")
  //transparencyPropagatingMtds += methodSymbol[Normalizes[_,_]]("<init>")
  //transparencyPropagatingMtds += methodSymbol[PairUpNorm[_,_]]("<init>")
  //transparencyPropagatingMtds += methodSymbol[PairUp[_,_]]("<init>")
  //transparencyPropagatingMtds += methodSymbol[List[_]]("$colon$colon")
  //
  //transparentMtds += methodSymbol[scala.Predef.type]("augmentString")
  //// does not seem to work:
  //transparentMtds += loadMtdSymbol(loadTypSymbol("java.lang.Integer"), "parseInt", None, true) //alsoApply println
  //
  ////transparentTyps += typeSymbol[RecordRead.type]
  ////transparentTyps += typeSymbol[RecordRead[Any]]
  //
  //
  //
  //transparencyPropagatingMtds += methodSymbol[Monoid[_]]("empty")
  //transparencyPropagatingMtds += methodSymbol[Semigroup[_]]("combine")
  //
  //
  //// --- //
  //
  ////transparentMtds +=
  transparencyPropagatingMtds += methodSymbol[query.`package`.type]("NonEmptyOrderedOps")
  transparencyPropagatingMtds += methodSymbol[query.`package`.type]("OrderedOps")
  transparencyPropagatingMtds += methodSymbol[query.`package`.type]("FiniteOps")
  transparencyPropagatingMtds += methodSymbol[query.`package`.type]("NonEmptyOps")
  transparencyPropagatingMtds += methodSymbol[query.`package`.type]("SourceOps")
  transparencyPropagatingMtds += methodSymbol[Abstracts.type]("apply")
  //transparencyPropagatingMtds += methodSymbol[dbstage.moncomp.SortedBy.type]("apply")
  
  
  
  // TODO move to Squid
  object ClosedCode {
    def unapply[T,C](c:Code[T,C]): Option[ClosedCode[T]] = {
      Code(c.rep) optionIf c.rep.dfn.unboundVals.isEmpty
    }
  }
  
  // TODO move to Squid
  /** A piece of code of type T with a hole of type S in it */
  abstract class HollowedCode[T,S,C] {
    val v: Variable[S]
    def body: Code[T,C&v.Ctx]
    override def toString: String = s"Hollow[${v.rep|>showScala}]{${body|>showC}}"
  }
  abstract class Inspector[T,C,R] {
    
    // TODO there should be a more lenient way to lift queries out even when they refer to locally-bound executed-once values...
    // FIXME have a way to account for when a hole appears in an uncertain evaluation context...
    // FIXedME account for current FVs with `inScope` and make sure no extrusion can happen
    def apply(cde: Code[T,C]): Either[Code[T,C],R] = {  // FIXME returning an option is sufficient
      var result: Option[(HollowedCode[T,Any,C] => R,Variable[Any])] = None
      val boundInCde = cde.rep.boundVals
      val res = topDown(cde.rep) {
        case r if result.isEmpty && (r.dfn.unboundVals & boundInCde isEmpty) =>  // Q: what if same val used in unrelated blocks?
        //case r if result.isEmpty && (r.dfn.unboundVals & boundInCde isEmpty)||{println(s"unboundVals=${r.dfn.unboundVals}");false} =>
          val rtp = r.typ|>CodeType.apply[Any]
          val t = traverse[Any](rtp)
          val c = Code[Any,Any](r)
          t.lift(c).fold(r){f => assert(result.isEmpty); result = Some((f,Variable[Any]()(rtp))); result.get._2.rep}
        case r => r
      }
      result.fold[Either[Code[T,C],R]](Left(cde)){
        case f -> v0 => Right(f(new HollowedCode[T,Any,C] {
          val v: v0.type = v0
          val body = Code(res)
        }))
      }
    }
    
    def traverse[S:CodeType]: PartialFunction[Code[S,C], HollowedCode[T,S,C] => R]
    
  }
  
  
  
  override def effect(r: Rep): squid.ir.SimpleEffect = dfn(r) match {
    case MethodApp(s,m,Nil,Nil,rt) if m.isStable =>
      //println(m,m.isAccessor)
      //println(m,m.isStable)
      //super.effect(r)
      s.effect
    case _ => super.effect(r)
  }
  
  override def showScala(r: Embedding.Rep): String = 
    super.showScala(r) |> trimPrefixes
  
  
}

object OnlineRewritings extends Embedding.SelfTransformer with SimpleRuleBasedTransformer
  //with StandardNormalizer 
  with LogicNormalizer
  with OptionNormalizer
  with FunctionNormalizer
  with EqualityNormalizer
  //with EffectsNormalizer
  with CurryEncoding.ApplicationNormalizer
{
  import base.Predef._
  
  // We make heavy use of tuples as an abstraction mechanism; they need to be removed automatically:
  rewrite {
    case code"($f:$t0=>$t1) andThen ($g:t1 => $t2)" => code"(x:$t0) => $g($f(x))"
    case code"($lhs:$t0) -> ($rhs:$t1)" => code"($lhs,$rhs)"
    case code"($x0:$t0,$x1:$t1)._1" => x0
    case code"($x0:$t0,$x1:$t1)._2" => x1
    case code"($x0:$t0,$x1:$t1,$x2:$t2)._1" => x0
    case code"($x0:$t0,$x1:$t1,$x2:$t2)._2" => x1
    case code"($x0:$t0,$x1:$t1,$x2:$t2)._3" => x2
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._1" => x0
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._2" => x1
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._3" => x2
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._4" => x3
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._1" => x0
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._2" => x1
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._3" => x2
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._4" => x3
    case code"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._5" => x4
  }
  
  rewrite {
    case code"the[$t where (t <:< AnyRef)]($x:t)" => x
    case code"squid.utils.GenHelper[$t]($x).|>[$s]($f)" => code"$f($x)"
  }
  
  rewrite {
    case code"val $ite = ($it:Iterator[$ta]).map[$tb]($f); $body: $bt" =>
      val body0 = body rewrite {
        case code"$$ite.hasNext" => code"$it.hasNext"
        case code"$$ite.next" => code"$f($it.next)"
      }
      ite.substitute[bt.Typ, ite.OuterCtx](body0, Abort())
  }
  
  
  // For Record stuff and type classes:
  
  rewrite {
      
    case code"query.~[$lt,$rt]($l,$r).lhs" => l
    case code"query.~[$lt,$rt]($l,$r).rhs" => r
    case code"recordSyntax[$at]($a).~[$bt]($b)" => code"query.~($a,$b)"
    case code"query.~.monoid[$at,$bt]($aev,$bev).combine($x,$y)" =>
      code"query.~($aev.combine($x.lhs,$y.lhs), $bev.combine($x.rhs,$y.rhs))"
      
    // Note: no need for `if isPure(w)` since we're in ANF
    case code"($w:Wraps[$a,$b]).deapply((w:Wraps[a,b]).apply($x))" => x
    case code"($w:Wraps[$a,$b]).apply((w:Wraps[a,b]).deapply($x))" => x
    case code"($w:Wraps[$a,$b]).instance" => w
      /*
    case code"generalHelper[$at]($a).normalize[$tb]($norm)" => code"$norm($a)"
      */
    case code"(new Normalizes[$ta,$tna]($fun)).apply($a)" => code"$fun($a)"
    case code"(new PairUpNorm[$ta,$tb]($lsfun)).ls($l,$r)" => code"$lsfun($l,$r)"
    case code"(new PairUp[$ta,$tb]($lsfun)).ls($l,$r)" => code"$lsfun($l,$r)"
      /*
    case code"($ds: DataSource[$ta]).naturallyJoining[$tr where (ta <:< tr),$tb]($b)($pairs)" =>
      //code"$ds.filter(a => $pairs.ls(a,$b).forall(fp => fp.l == fp.r))" // no support for path-dependent types
      code"$ds.filter(a => $pairs.ls(a,$b).forall(_.same))"
      */
    case code"($ls: List[$ta]).::[$tb where (ta <:< tb)]($b).forall($p)" => code"$p($b) && ($ls forall $p)"
    case code"Nil.forall($p)" => code"true"
    case code"FieldPair[$ta]($a0,$a1).same" => code"$a0 == $a1"
      
      // FIXME renamed/removed:
    /*
    case code"recordSyntax[$at]($a).apply[$f]($acc, $w: f Wraps $v)" => code"$w.instance.deapply($acc($a))"
    case code"recordSyntax[$at]($a).apply[$f,$v]($w)($acc)" => code"$w.deapply($acc($a))"
    case code"recordSyntax[$at]($a).project[$rt]($proj)" => code"$proj($a)"
    case code"recordSyntax[$at]($a).p[$rt]($proj)" => code"$proj($a)"
    case code"recordSyntax[$at]($a).select[$rt]($sel)" => code"$sel($a)"
    */
    case code"recordSyntax[$at]($a).apply[$rt]($proj)" => code"$proj($a)"
      
    case code"CanAccess[$at,$rt]($f).fun" => f
    case code"CanAccess[$at,$rt]($f).apply($x)" => code"$f($x)"
    case code"(CanAccess[$at,$rt]($f):at=>rt)($x)" => code"$f($x)" // for when the apply symbol of Function1 is used
      
    case code"ProjectsOn[$at,$rt]($f).fun" => f
    case code"ProjectsOn[$at,$rt]($f).apply($x)" => code"$f($x)"
    case code"(ProjectsOn[$at,$rt]($f):at=>rt)($x)" => code"$f($x)" // for when the apply symbol of Function1 is used
      
      /*
    //case code"RecordRead.read(Read.readWrapped[$ft,$vt]($w,$rdv)).read($sep)" =>
    //  code"(str:String) => $w($rdv(str))"
    //case code"RecordRead.readLHS[$ta,$tb]($ra,$rrb).read($sep)" =>
    case code"Read.instance[$ta]($f).apply($str)" => code"$f($str)"
    //case code"($rr:RecordRead[$rrt]).read($sep)" => code"(str: String) => splitString(str)"
    case code"RecordRead.instance[$ta]($f).apply($ite)" => code"$f($ite)"
      */
      
    case code"augmentString($str).toInt" => code"java.lang.Integer.parseInt($str)"
      
    case code"query.monoidInstance[$t]($e)($c).empty" => code"$e"
    case code"query.monoidInstance[$t]($e)($c).combine($x,$y)" => code"$c($x,$y)"
      
    //case code"dbstage.monoidWrap[$at,$bt]($wev,$mev).combine($x,$y)" =>
    //  code"$wev..monoidWrap[$at,$bt]($wev,$mev).combine($x,$y)"
    
    case code"($m:cats.kernel.instances.StringInstances).catsKernelStdMonoidForString.empty" => Const("")
    case code"($m:cats.kernel.instances.StringInstances).catsKernelStdMonoidForString.combine($x,$y)" => code"$x + $y"
    case code"($m:cats.kernel.instances.IntInstances).catsKernelStdGroupForInt.empty" => Const(0)
    case code"($m:cats.kernel.instances.IntInstances).catsKernelStdGroupForInt.combine($x,$y)" => code"$x + $y"
      
    //case code"GroupedBagMonoid.updateFrom[$tk,$ta]($self,GroupedBagMonoid.reconstruct[tk,ta]($that)($asem))" =>
    //  code"GroupedBagMonoid.updateFromReconstructed[$tk,$ta]($self,$that)($asem)" // FIXME check asem?
      
      /*
    case code"semigroupSyntax[$ta]($self)($asem).groupBy[$tb]($b)" => code"Groups.single($b,$self)"
    case code"GroupsMonoid.updateFrom[$tk,$tv]($self,Groups.single($k,$v))" => code"$self += (($k,$v)); ()"
    //case code"GroupsMonoid.updateFrom[$tk,$tv]($self,Groups.reconstruct[tk,ta]($that)($asem))" =>
      */
    
  }
  
  // --- //
  
  //import moncomp._
  
  rewrite {
      
    case code"Abstracts[$at,$bt]($f).apply($x)" => code"$f($x)"
    case code"IntoMonoid.instance[$at,$mt]($f).apply($x)" => code"$f($x)"
    case code"FiniteOps[$ct,$ta]($self)($ev).orderingBy[$ot]($oord,$aoproj)" => code"SortedBy[$ct,$ot]($self)"
    case code"SortedBy[$ast,$ot]($as).as" => as
      
      /*
    case code"NonEmpty.intoList[$at].apply(list($a,$as*))" =>
      //code"List.of(${a +: as}*)" // FIXME
      val aas = a +: as
      code"List.of(${aas}*)"
    //case code"NonEmpty.intoList[$at].apply($a,list($as:_*))" => code"List.of($as:_*)" // TODO
    //case code"list2[$at]($as*)" => code"???"
      */
      
    case code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).flatMap[$rt]($v => $body)($mmon)" =>
      code"NonEmptyOrderedOps[$ast,$at]($as)($aord,$ane,$afin).map[$rt]($v => $body)($mmon)"
    case code"OrderedOps[$ast,$at]($as)($aord,$afin).flatMap[$rt,$rm]($v => $body)($into,$mmon)" =>
      code"OrderedOps[$ast,$at]($as)($aord,$afin).map[$rt,$rm]($v => $body)($into,$mmon)"
    case code"FiniteOps[$ast,$at]($as)($afin).flatMap[$rt,$rm]($v => $body)($into,$mmon)" =>
      code"FiniteOps[$ast,$at]($as)($afin).map[$rt,$rm]($v => $body)($into,$mmon)"
    
  }
  
  
}

object OnlineTransformations extends Embedding.TransformerWrapper(Embedding.Desugaring,OnlineRewritings) with FixPointTransformer


object LogicFlow extends Embedding.SelfTransformer with LogicFlowNormalizer

object LowLevelOpt extends Embedding.SelfTransformer with VarFlattening with FixPointRuleBasedTransformer with TopDownTransformer

object FinalizeCode extends Embedding.TransformerWrapper(LogicFlow,LowLevelOpt)

//object FinalizeCode extends Embedding.TransformerWrapper(LogicFlow,LowLevelOpt) with FixPointTransformer
// ^ does not seem to converge, probably because LogicFlowNormalizer is an (atypical) IRTransformer 

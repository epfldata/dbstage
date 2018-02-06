package dbstage

import squid.utils._
import squid.anf.analysis.BlockHelpers
import squid.anf.transfo.EqualityNormalizer
import squid.anf.transfo.LogicFlowNormalizer
import squid.anf.transfo.LogicNormalizer
import squid.anf.transfo.OptionNormalizer
import squid.anf.transfo.StandardNormalizer
import squid.anf.transfo.VarFlattening
import squid.ir.CrossStageAST
import squid.ir.CurryEncoding
import squid.ir.FixPointRuleBasedTransformer
import squid.ir.FixPointTransformer
import squid.ir.OnlineOptimizer
import squid.ir.SchedulingANF
import squid.ir.SimpleANF
import squid.ir.SimpleRuleBasedTransformer
import squid.ir.StandardEffects
import squid.ir.TopDownTransformer
import squid.lang.ScalaCore

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
  //embed(Access)
  //embed(PairUp)
  //embed(PairUpLowPriority)
  
  //import Predef._
  
  //def pipeline = OnlineRewritings.pipeline
  def pipeline = OnlineTransformations.pipeline
  
  transparencyPropagatingMtds += methodSymbol[~.type]("apply")
  transparencyPropagatingMtds += methodSymbol[Any~Any]("lhs")
  transparencyPropagatingMtds += methodSymbol[Any~Any]("rhs")
  
  object ClosedCode {
    def unapply[T,C](c:Code[T,C]): Option[ClosedCode[T]] = {
      Code(c.rep) optionIf c.rep.dfn.unboundVals.isEmpty
    }
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
    super.showScala(r)
      .replaceAll("dbstage.example.","")
      .replaceAll("dbstage.","")
  
  
}

object OnlineRewritings extends Embedding.SelfTransformer with SimpleRuleBasedTransformer
  //with StandardNormalizer 
  with LogicNormalizer
  with OptionNormalizer
  with EqualityNormalizer
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
  
  
  // For Record stuff:
  
  val FieldBase_monoid = base.loadMtdSymbol(base.loadTypSymbol("dbstage.FieldBase$"), "monoid")
  //val fieldValue = base.loadMtdSymbol(base.loadTypSymbol("dbstage2.Field"), "value")
  //val FieldBase_apply = base.loadMtdSymbol(base.loadTypSymbol("dbstage.FieldBase"), "apply")
  
  import cats.Monoid
  import base.MethodApplication
  //object UH extends squid.utils.meta.UniverseHelpersClass[scala.reflect.runtime.universe.type](scala.reflect.runtime.universe)
  
  //def handleFieldBase_monoid[FB:CodeType,C](ma: MethodApplication[Monoid[FB],C],x:Code[FB,C],y:Code[FB,C]): ClosedCode[FB] = {
  def handleFieldBase_monoid[FB<:FieldBase:CodeType,C](ma: MethodApplication[Monoid[FB],C], _x:Code[FB,C], _y:Code[FB,C]): ClosedCode[FB] = {
    //val t = ma.targs.head // : CodeType[_]
    //val tmonoid = ma.args.tail.head.tail.head.asInstanceOf[ClosedCode[Monoid[t.Typ]]]
    //println(t,tmonoid)
    //base.debugFor{
    (code"Seq.empty[FB]":Code[_,C]) match {
      case code"Seq.empty[Field[$t]]" =>
        type F = Field[t.Typ]
        //println(t)
        val x = _x.asInstanceOf[Code[F,C]]
        val y = _y.asInstanceOf[Code[F,C]]
        val tmonoid = ma.args.tail.head.tail.head.asInstanceOf[ClosedCode[Monoid[t.Typ]]]
        val comb = code"$tmonoid.combine($x.value, $y.value)"
        //println(comb)
        //println(codeTypeOf[FB].rep.tpe.typeSymbol.companion.typeSignature.member("apply"))
        //val objName = codeTypeOf[FB].rep.tpe.typeSymbol.companion.fullName
        val obj = codeTypeOf[FB].rep.tpe.typeSymbol.companion
        //println(obj.typeSignature)
        //UH.encodedTypeSymbol(obj.typeSignature.typeSymbol.asType alsoApply println) alsoApply println
        //println()
        //val objRep = base.staticModule(UH.encodedTypeSymbol(obj.typeSignature.typeSymbol.asType))
        val objRep = base.staticModule(obj.fullName)
        base.Code(base.methodApp(objRep, base.Apply.Symbol, Nil, base.Args(comb.rep) :: Nil, typeRepOf[FB]))
    }
    //}
    //???
  }
  
  rewrite {
    case code"dbstage.~[$lt,$rt]($l,$r).lhs" => l
    case code"dbstage.~[$lt,$rt]($l,$r).rhs" => r
    case code"recordSyntax[$at]($a).~[$bt]($b)" => code"dbstage.~($a,$b)"
    case code"dbstage.~.monoid[$at,$bt]($aev,$bev).combine($x,$y)" =>
      code"dbstage.~($aev.combine($x.lhs,$y.lhs), $bev.combine($x.rhs,$y.rhs))"
    //case code"${base.MethodApplication(ma)}:($t where (t <:< FieldBase))" if ma.symbol === FieldBase_monoid =>
    //case code"(${MethodApplication(ma)}:Monoid[$t]).combine($x,$y)" if ma.symbol === FieldBase_monoid =>
    case e@code"(${MethodApplication(ma)}:Monoid[$t where (t <:< FieldBase)]).combine($x,$y)" if ma.symbol === FieldBase_monoid =>
      println(e)
      //println(ma.args.tail.head.tail.head)
      //val s = ma.targs.head:CodeType[_]
      //val smonoid = ma.args.tail.head.tail.head.asInstanceOf[ClosedCode[Monoid[s.Typ]]]
      //println(s,smonoid)
      //???
      handleFieldBase_monoid(ma,x,y)
    
  }
  
  /*
  val recordAccessApply = base.loadMtdSymbol(base.loadTypSymbol("dbstage2.AccessOps"), "apply")
  val fieldValue = base.loadMtdSymbol(base.loadTypSymbol("dbstage2.Field"), "value")
  
  def handleApply[R<:FieldModule:CodeType](args: Seq[Seq[OpenCode[Any]]]): ClosedCode[R] = args match {
    case Seq(Seq(ops),Seq(acc)) =>
      ops match {
        case code"toAccessOps[$s]($self)" =>
          //self.asInstanceOf[ClosedCode[Nothing]]
          val fld = code"${acc.asInstanceOf[OpenCode[Access[R,s.Typ]]]}.field($self)"
          //.asInstanceOf[ClosedCode[R]]
          base.Code(base.methodApp(fld.rep,fieldValue,Nil,Nil,codeTypeOf[R].rep))
      }
  }
  rewrite {
    case code"::[$hdt,$tlt]($hd,$tl).hd" => hd 
    case code"::[$hdt,$tlt]($hd,$tl).tl" => tl 
    case code"Access[$f where (f <:< FieldModule),$r]($idx,$field).idx" => idx 
    case code"Access[$f where (f <:< FieldModule),$r]($idx,$field).field" => field 
    case code"${base.MethodApplication(ma)}:($t where (t <:< FieldModule))" if ma.symbol === recordAccessApply =>
      //println(ma)
      handleApply[t.Typ](ma.args)
    case code"false" => code"false"

      
    //case code"PairUp.hasPair[$f where (f <:< FieldModule),$l,$r]($pairs,$acc)" =>
    //  //code"$pairs.mapR[$f::$r](_.tl)"
    //  code"PairUp[$l,$f::$r]((l,r) => $pairs.ls(l,r.tl))"
      
    //case code"PairUp.doesNotHavePair[$f where (f <:< FieldModule),$l,$r]($pairs)" =>
    //  code"PairUp[$l,$f::$r]((l,r) => $pairs.ls(l,r.tl))"
    //  //code"$pairs.mapR[$f,$l,$r](_.tl)"
    //  //code"$pairs.mapR[$f::$r](_.tl)"
      
    //case code"($pu:PairUp[$l,$r]).mapR[$t]($f).mapR[$s]($g)" => code"$pu.mapR($g andThen $f)"
    
    case code"PairUp[$l,$r]($ls).ls" => ls
      
      
    //  FIXME: does not seem to match:
    //case code"$x:Null" => code"null"
    //case code"$x:Null" if !x.rep.effect.immediate => code"null"
    //case code"$x:($t where (t <:< Null))" if !(x =~= code"null") && (t <:< codeTypeOf[Null]) => // FIXME StackOverflowError
    //  //code"null:$t" // FIXME does not type-check
    //  t.nullValue
    
  }
  
  */
  
}

object OnlineTransformations extends Embedding.TransformerWrapper(Embedding.Desugaring,OnlineRewritings) with FixPointTransformer


object LogicFlow extends Embedding.SelfTransformer with LogicFlowNormalizer

object LowLevelOpt extends Embedding.SelfTransformer with VarFlattening with FixPointRuleBasedTransformer with TopDownTransformer

object FinalizeCode extends Embedding.TransformerWrapper(LogicFlow,LowLevelOpt)

//object FinalizeCode extends Embedding.TransformerWrapper(LogicFlow,LowLevelOpt) with FixPointTransformer
// ^ does not seem to converge, probably because LogicFlowNormalizer is an (atypical) IRTransformer 

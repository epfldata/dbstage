package dbstage

import squid.anf.analysis.BlockHelpers
import squid.anf.transfo.EqualityNormalizer
import squid.anf.transfo.LogicFlowNormalizer
import squid.anf.transfo.LogicNormalizer
import squid.anf.transfo.OptionNormalizer
import squid.anf.transfo.StandardNormalizer
import squid.anf.transfo.VarFlattening
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
import squid.quasi.SimpleReps

object Embedding 
  extends SimpleANF 
  //extends SchedulingANF 
    with StandardEffects 
    with OnlineOptimizer
    //with StandardNormalizer 
    with LogicNormalizer
    with OptionNormalizer // note: needed by VarFlattening
    with BlockHelpers
    with ScalaCore
    with EqualityNormalizer
    with CurryEncoding.ApplicationNormalizer
{
  
  import Predef._
  
  rewrite {
    case ir"($f:$t0=>$t1) andThen ($g:t1 => $t2)" => ir"(x:$t0) => $g($f(x))"
    case ir"($lhs:$t0) -> ($rhs:$t1)" => ir"($lhs,$rhs)"
    case ir"($x0:$t0,$x1:$t1)._1" => x0
    case ir"($x0:$t0,$x1:$t1)._2" => x1
    case ir"($x0:$t0,$x1:$t1,$x2:$t2)._1" => x0
    case ir"($x0:$t0,$x1:$t1,$x2:$t2)._2" => x1
    case ir"($x0:$t0,$x1:$t1,$x2:$t2)._3" => x2
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._1" => x0
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._2" => x1
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._3" => x2
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3)._4" => x3
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._1" => x0
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._2" => x1
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._3" => x2
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._4" => x3
    case ir"($x0:$t0,$x1:$t1,$x2:$t2,$x3:$t3,$x4:$t4)._5" => x4
  }
  
}

object LogicFlow extends Embedding.SelfTransformer with LogicFlowNormalizer

object LowLevelOpt extends Embedding.SelfTransformer with VarFlattening with FixPointRuleBasedTransformer with TopDownTransformer

object FinalizeCode extends Embedding.TransformerWrapper(LogicFlow,LowLevelOpt)

//object FinalizeCode extends Embedding.TransformerWrapper(LogicFlow,LowLevelOpt) with FixPointTransformer
// ^ does not seem to converge, probably because LogicFlowNormalizer is an (atypical) IRTransformer 

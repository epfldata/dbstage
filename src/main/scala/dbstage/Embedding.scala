package dbstage

import squid.anf.analysis.BlockHelpers
import squid.anf.transfo.EqualityNormalizer
import squid.anf.transfo.LogicFlowNormalizer
import squid.anf.transfo.LogicNormalizer
import squid.anf.transfo.StandardNormalizer
import squid.ir.OnlineOptimizer
import squid.ir.SchedulingANF
import squid.ir.SimpleANF
import squid.ir.SimpleRuleBasedTransformer
import squid.ir.StandardEffects
import squid.lang.ScalaCore
import squid.quasi.SimpleReps

object Embedding 
  extends SimpleANF 
  //extends SchedulingANF 
    with SimpleReps 
    with StandardEffects 
    with OnlineOptimizer
    //with StandardNormalizer 
    with LogicNormalizer
    with BlockHelpers
    with ScalaCore
    with EqualityNormalizer
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
  }
  
}

object LogicFlow extends Embedding.SelfTransformer with LogicFlowNormalizer

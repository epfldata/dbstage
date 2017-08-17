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
  
}

object LogicFlow extends Embedding.SelfTransformer with LogicFlowNormalizer

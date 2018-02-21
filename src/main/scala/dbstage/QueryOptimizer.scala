package dbstage

import squid._
import squid.ir.{BindingNormalizer, TopDownTransformer}
import utils._
import squid.quasi.{phase, embed, dbg_embed}
import squid.lib.{transparent,transparencyPropagating}

//class QueryOptimizer extends squid.lang.Optimizer {
class QueryOptimizer extends Embedding.SelfCodeTransformer {
  Embedding.embed(example.tpch.Queries)
  
  //println("E "+Embedding.methods)
  //Embedding.embed(Definitions)
  
  import Embedding.Predef._
  
  //OnlineRewritings.rewrite {
  //  case code"42" => code"43"
  //}
  
  def transform[T,C](cde: Code[T,C]): Code[T,C] = {
    import cde.Typ
    implicit val Typ = cde.Typ
    implicit val cfg = QueryCompiler.Config(false) // don't do cross-stage persistence when optimizing at compile-time!
    val q = QueryCompiler.lift(cde.withUnderlyingTyp)
    println(q)
    q.directPlan
  }
  
}
object QueryOptimizer extends StaticOptimizer[QueryOptimizer]

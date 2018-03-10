package dbstage

import cats.{Monoid,Semigroup}
import squid.utils._
import squid.ir.SimpleANF
import squid.ir.StandardEffects
import squid.ir.CrossStageAST

/** Main purpose of this embedding is to let-bind most things so that we can print-debug code more easily;
  * (The main Embedding knows many things are pure and won't let-bind them, resulting in enormous expressions that are
  * very hard to parse...) */
object SimplerEmbedding extends SimpleANF with StandardEffects with CrossStageAST {
  
}

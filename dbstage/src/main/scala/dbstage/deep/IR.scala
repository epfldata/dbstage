package dbstage.deep

import dbstage.lang.TableView

import squid.ir._
import squid.lang._
import squid.anf.analysis.BlockHelpers

object IR extends SimpleANF
  with ClassEmbedder
  with ScalaCore
  with Definitions
  with BlockHelpers
  with StandardEffects
  with OnlineOptimizer
  with CurryEncoding.ApplicationNormalizer
{
  
  // These are used to allow the ANF representation not to let-bind them,
  // as they can be considered pure (if their arguments are):
  transparencyPropagatingMtds += methodSymbol[TableView.type]("all")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("filter")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("size")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("map")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("join")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("aggregate")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("forEach")

  import Predef._

  rewrite {
    case code"($x: $tx, $y: $ty)._1" => x
    case code"($x: $tx, $y: $ty)._2" => y
  }
}

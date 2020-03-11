package dbstage.deep

import dbstage.lang.{Table, TableView}

import squid.ir._
import squid.lang._
import squid.anf.analysis.BlockHelpers

object IR extends SimpleANF
  with ClassEmbedder
  with ScalaCore
  with Definitions
  with BlockHelpers
  with StandardEffects
{
  
  // These are used to allow the ANF representation not to let-bind them,
  // as they can be considered pure (if their arguments are):
  transparencyPropagatingMtds += methodSymbol[Table[_]]("view")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("filter")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("size")
  transparencyPropagatingMtds += methodSymbol[TableView[_]]("map")
  
}

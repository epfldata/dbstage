package dbstage

import squid.utils._

import scala.language.dynamics
import scala.language.experimental.macros

object BuildImplicitGen {
  import scala.reflect.macros.blackbox.Context
  
  // TODO better error detection and reporting!
  def buildImplicitGen[T:c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    val T = weakTypeOf[T]
    //println(T)
    //println(T.typeSymbol)
    //println(T.typeSymbol.companion)
    //println(T.typeSymbol.companion.typeSignature)
    //println(T.typeSymbol.companion.typeSignature.baseType(typeOf[Any=>Any].typeSymbol))
    //???
    require(T.typeSymbol.companion =/= NoSymbol, s"No companion found for type $T")
    val res = q"_root_.dbstage.BuildField.fromFunction(${T.typeSymbol.companion})"
    //println(s"Generated: $res")
    res
  }
  
}

package gen

import squid.utils._
//import dbstage.Embedding
//import Embedding.Predef._
//import Embedding.Quasicodes.{code=>c,_}
import scala.language.dynamics
import squid.utils.MacroUtils.MacroSetting

import scala.language.experimental.macros
class ClassSelfType extends Dynamic {
  def selectDynamic(name: String): Any = macro ClassSelfType.selectDynamicImpl
}
class dbg_ClassSelfType extends Dynamic { // TODO actual debugging infrastructure
  //@MacroSetting(debug = true) not found: type MacroSetting 
  def selectDynamic(name: String): Any = macro ClassSelfType.selectDynamicImpl
}
object ClassSelfType {
  import scala.reflect.macros.whitebox
  def selectDynamicImpl(c: whitebox.Context)(name: c.Tree): c.Tree = {
    import c.universe._
    val Literal(Constant(nameVal:String)) = name
    //println(c.macroApplication)
    //println(c.prefix)
    val sym = c.prefix.tree.tpe.widen match {
      case TypeRef(base,sym,targs) =>
        //println(base,sym,targs)
        //println(base.members)
        //println(base.member(TermName(nameVal)))
        base.member(TermName(nameVal)) match {
          case NoSymbol => lastWords(s"no member $nameVal in $base")
          case sym => sym
        }
    }
    assert(sym.isStatic)
    val tree = c.parse(sym.fullName)
    //println(tree)
    //q"$$(Const(42))"
    val gen = q"$$($tree.asLambda)(${c.prefix.tree})"
    //println(s"Generated ${showCode(gen)}")
    //???
    gen
  }
}

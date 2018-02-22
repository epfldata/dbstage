package dbstage

import squid.utils._
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox

// TODO move this to Squid:
@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class desugar extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro desugarMacros.impl
}
object desugarMacros {
  
  def impl(c: whitebox.Context)(annottees: c.Tree*): c.Tree = {
    import c.universe._
    def addSugarPhase = (mods:Modifiers) =>
      Modifiers(mods.flags, mods.privateWithin, q"new _root_.squid.quasi.phase('Sugar)" :: mods.annotations)
    annottees.toList match {
      case ValDef(mods,name,tpt,rhs) :: Nil =>
        ValDef(mods|>addSugarPhase,name,tpt,rhs)
      case DefDef(mods,name,tps,ps,tpt,rhs) :: Nil =>
        DefDef(mods|>addSugarPhase,name,tps,ps,tpt,rhs)
    }
  }
  
}


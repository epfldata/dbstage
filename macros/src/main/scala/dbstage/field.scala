package dbstage

import squid.utils._
import scala.language.experimental.macros
import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.reflect.macros.whitebox

// TODO a `thinField` annot that expands into a type and an implicit companion object (upcasted to Wraps[A,B] so we get rid of Squid's explicit upcasting ascriptions in printed code)

// TODO generalize to handle classes with several parameters, associated with WrapsN classes

@compileTimeOnly("Enable macro paradise to expand macro annotations.")
class field extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro fieldMacros.impl
}
//object field {
object fieldMacros {
  
  def impl(c: whitebox.Context)(annottees: c.Tree*): c.Tree = {
    import c.universe._
    annottees.toList match {
      //case (cls @ ClassDef(mods, name, tparams, impl)) :: Nil =>
      //case (cls @ q"$mods class $name[..$tparams](...$params) extends ..$parents") :: Nil => // TODO match body
      case (cls @ q"$mods class $name[..$tparams]($pname: $ptyp) extends ..$parents") :: Nil => // TODO match body; param modifiers
        import Flag._
        //require(mods.hasFlag(CASE))
        val tname = name.toTermName
        
        //Flag()
        //tq"_root_.scala.Product"::tq"_root_.scala.Serializable"::
        //  ${ClassDef(Modifiers(mods.flags | CASE), name, tparams, impl)} 
        //val gen = q"""
        //  ${
        //  require(mods.hasFlag(CASE), "a @field class should be a case class.")
        //  cls
        //  // it's too hard to turn a random class into a case class... this crashes the compiler...:
        //  /*
        //  ClassDef(Modifiers(mods.flags | CASE), name, tparams, 
        //    //Template(
        //    //  //tq"_root_.scala.Product"::tq"_root_.scala.Serializable"::
        //    //  Nil, 
        //    //  ValDef(Modifiers(), termNames.WILDCARD,EmptyTree,EmptyTree), Nil)
        //    Template(
        //      //impl.parents,
        //      tq"_root_.scala.Product"::tq"_root_.scala.Serializable"::tq"_root_.dbstage.Field[Int]"::Nil,
        //      impl.self, 
        //      impl.body map {
        //        case ValDef(mods, nme, tpt, rhs) if mods.hasFlag(PARAMACCESSOR) =>
        //          ValDef(Modifiers(CASEACCESSOR), nme, tpt, rhs) // TODO salvage mods.flags...
        //        case x => x
        //      })
        //    )
        //    */
        //    ClassDef(mods, name, tparams, Template(impl.parents:+tq"_root_.dbstage.Field[Int]",impl.self,impl.body))
        //  } 
        //  ${ModuleDef(Modifiers(), name.toTermName, 
        //    Template(Nil, ValDef(Modifiers(), termNames.WILDCARD,EmptyTree,EmptyTree), 
        //      q"implicit object builder extends _root_.dbstage.BuildField[$name] { def apply(a: Int): $name = ${name.toTermName}(a) }" ::
        //      Nil)
        //  )}
        //"""
        //  ${mods} case class $name extends 
        //  $mods class $name[..$tparams](...$params) extends ..$parents with _root_.dbstage.Field[Int]
        //  ${mods.flags | CASE} class $name[..$tparams]($pname: $ptyp) extends ..$parents with _root_.dbstage.Field[$ptyp]
        //    implicit object builder extends _root_.dbstage.BuildField[$name] { def apply(a: $ptyp): $name = $tname(a) }
        //    implicit object wraps extends _root_.dbstage.Wraps[$name,$ptyp]($tname.apply,_.$pname)
        //val gen = q"""
        //  case class $name[..$tparams]($pname: $ptyp) extends ..$parents with _root_.dbstage.Field[$ptyp]  // TODO other mods...
        //  object $tname {
        //    implicit val wraps = _root_.dbstage.Wraps[$name,$ptyp]($tname.apply,_.$pname)
        //  }
        //"""
        
        // TODO check ptyp does not refer to tparams... or rm tparams (but they could be used as phantom types, or in `parents`)
        // In fact, a more elaborate scheme could be used allowing for a parametrized parameter,
        // whereby the companion object does not directly extend Wraps but some other type (with standard apply/deapply symbols)
        // and that contains a (possibly polymorphic) def for the implicit Wraps instance
        val gen = q"""
          abstract case class $name[..$tparams]($pname: $ptyp) extends ..$parents  // TODO propagate other mods...?
          object $tname extends Wraps[$name,$ptyp] {
            protected def applyImpl(v: $ptyp) = new $name(v){}
            protected def deapplyImpl(x: $name) = x.$pname
          }
        """
        //println(s"Gen: ${showCode(gen)}")
        gen
        //c.parse(showCode(gen))
    }
    //???
  }
  
}


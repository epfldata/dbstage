package dbstage

import squid.lang.{Base, CrossStageEnabled}

abstract class Staged[T] {
  def embedded(base: Base with CrossStageEnabled): base.ClosedCode[T]
}
object Staged {
  //import scala.language.implicitConversions
  import scala.language.experimental.macros
  implicit def apply[T](implicit cde: T): Staged[T] = macro applyImpl[T]
  import scala.reflect.macros.blackbox
  def applyImpl[T: c.WeakTypeTag](c: blackbox.Context)(cde: c.Tree): c.Tree = {
    import c.universe._
    val T = weakTypeOf[T]
    val dbs = q"_root_.dbstage"
    val sqd = q"_root_.squid"
    //q"new $dbs.Staged[$T] { def embedded(base: $sqd.lang.Base with $sqd.lang.CrossStageEnabled) = ??? }"
    val paramName = TermName(c.freshName("base"))
    q"""new $dbs.Staged[$T] {
      def embedded($paramName: $sqd.lang.Base with $sqd.lang.CrossStageEnabled) = {
        import $paramName.Predef._
        import $paramName.Quasicodes._
        code{$cde}
      }
    }"""
  }
}

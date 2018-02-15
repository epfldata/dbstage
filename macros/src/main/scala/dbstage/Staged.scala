package dbstage

import squid.lang.{Base, CrossStageEnabled}

// TODO move this into Squid's QuasiBase! have a T:CodeType[T] member
// TODO a Lifted macro impl class that does not have the `plain` version (so we can actually use code insertion!)

abstract class Lifted[T] {
  def embedded(base: Base with CrossStageEnabled): base.ClosedCode[T]
}
abstract class Staged[T] extends Lifted[T] {
  def plain: T
}
//abstract class Staged[T] {
//  def embedded(base: Base with CrossStageEnabled): base.ClosedCode[T]
//  def plain: T
//}
object Staged {
  import scala.language.experimental.macros
  implicit def stage[T](implicit cde: T): Staged[T] = macro stageImpl[T]
  import scala.reflect.macros.blackbox
  def stageImpl[T: c.WeakTypeTag](c: blackbox.Context)(cde: c.Tree): c.Tree = {
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
      def plain = $cde
    }"""
      //type T = $T
      //val T = _root_.scala.Predef.implicitly[]
  }
  
  import scala.language.implicitConversions
  implicit def apply[T](cde: T): Staged[T] = macro stageImpl[T]
  
}

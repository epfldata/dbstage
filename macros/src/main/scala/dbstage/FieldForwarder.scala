package dbstage

import scala.language.dynamics
import scala.language.experimental.macros

class FieldForwarder[T](val underlying: T) extends Dynamic {
  def wrapSelect[R](x:R):R = x
  final def selectDynamic(fieldName: String): Any = macro FieldForwarder.selectDynamicImpl
}
object FieldForwarder {
  import scala.reflect.macros.whitebox.Context
  def selectDynamicImpl(c: Context)(fieldName: c.Tree): c.Tree = {
    import c.universe._
    val Literal(Constant(fieldNameStr:String)) = fieldName
    //println(c.prefix.tree.tpe.baseType(symbolOf[FieldForwarder[_]]))
    val pre = c.prefix.tree
    val wrappedType = pre.tpe.baseType(symbolOf[FieldForwarder[_]])
    q"$pre.wrapSelect($pre.underlying.${TermName(fieldNameStr)})"
  }
}

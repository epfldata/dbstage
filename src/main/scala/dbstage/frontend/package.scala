package dbstage

import squid.utils._
import Embedding.Predef._
import squid.lib.transparencyPropagating
import squid.lib.transparent

package object frontend {
  
  def log(msg: String) = System.err.println(s"[${new java.util.Date}] $msg")
  
  // Used in temporary code representations to encode SQL field references
  @transparent def field[T](name: String): T = ???
  @transparent def fieldIn[T](name: String, uid: Int): T = ???
  
  import scala.language.implicitConversions
  implicit def field2Code(f: Field): IR[f.T,Any] = f.toCode.asClosedIR // there is currently no unquote $ overload for Code in Squid...
  //def field2CodeOf[S:IRType](f: Field{type T <: S}): Code[S] = f.toCode
  
}

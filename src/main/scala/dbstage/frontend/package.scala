package dbstage

import squid.utils._
import Embedding.Predef._
import squid.lib.transparencyPropagating
import squid.lib.transparent

package object frontend {
  
  def log(msg: String) = System.err.println(s"[${new java.util.Date}] $msg")
  
  //@transparencyPropagating def field[T](name: String, uid: Option[Int] = None): T = ???
  //@transparencyPropagating def field[T](name: String, uid: Option[Int]): T = ???
  @transparent def field[T](name: String): T = ???
  @transparent def fieldIn[T](name: String, uid: Int): T = ???
  
  import scala.language.implicitConversions
  //implicit def field2Code(f: Field): Code[f.T] = ir"field[${f.IRTypeT}](${Const(f.name)})"
  implicit def field2Code(f: Field): IR[f.T,Any] = f.toCode.asClosedIR // there is currently no unquote $ overload for Code in Squid...
  //def field2CodeOf[S:IRType](f: Field{type T <: S}): Code[S] = ir"field[S](${Const(f.name)})" // FIXME rm
  def field2CodeOf[S:IRType](f: Field{type T <: S}): Code[S] = f.toCode
  
  //def select(): Query[Int] = ???
  //def select(c0: Field): TableQuery[c0.T] = ???
  //def select(c0: Field, c1: Field): TableQuery[(c0.T,c1.T)] = ???
  //def select(c0: Field, c1: Field, c2: Field): TableQuery[(c0.T,c1.T,c2.T)] = ???
  
}

import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import squid.utils._
import squid.ir.SimpleEffect
import squid.lib.transparencyPropagating

import scala.language.implicitConversions

package object dbstage {
  
  def the[A <: AnyRef](implicit ev: A): ev.type = ev
  
  import Embedding.Predef._
  
  def showC(cde: OpenCode[Any]) = cde.rep|>base.showRep // not: trimPrefixes called by overridden Embedding.showScala
  def showCT(cdeTyp: CodeType[_]) = cdeTyp.rep.tpe.toString|>trimPrefixes
  def trimPrefixes(str: String) = str
    .replaceAll("dbstage.example.","")
    .replaceAll("dbstage.moncomp.`package`.","")
    .replaceAll("dbstage.moncomp.","")
    .replaceAll("dbstage.","")
    .replaceAll("scala.","")
  
  def indentString(str: String) = {
    val lines = str.splitSane('\n')
    val pre = "| "
    lines map (Debug.GREY + pre + Console.RESET + _) mkString "\n"
  }
  def blockIndentString(str: String): String = blockIndentString(str, "")
  def blockIndentString(str: String, post: String) =
    if (str.contains('\n')) "\n"+indentString(str)+"\n"+post else str+" "+post
  
  
}

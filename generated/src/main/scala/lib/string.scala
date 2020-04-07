package lib

import scala.scalanative.unsafe._

@extern
object string {
  def strlen(str: CString): CSize = extern
}

object str {
  def charAt(str: CString, index: Word): CChar = str(index)
}
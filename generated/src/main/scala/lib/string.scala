package lib

import scala.scalanative.unsafe._

@extern
object string {
  def strlen(str: CString): CSize = extern
  def strcpy(dest: CString, src: CString): CString = extern
  def strcmp(str1: CString, str2: CString): Int = extern
}

object str {
  def charAt(str: CString, index: Word): CChar = str(index)
}
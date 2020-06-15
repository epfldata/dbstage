package dbstage

import scala.language.implicitConversions

package object lang {
  def schedule[A](x: A): A = x
  implicit def toCString(x: String): CString = ???
}

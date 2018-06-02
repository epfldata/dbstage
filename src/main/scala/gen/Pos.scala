package gen

import sourcecode.{File,Line}

case class Pos(f: File, l: Line) {
  def file = f.value
  def line = l.value
}
object Pos {
  implicit def instance(implicit f: File, l: Line) = Pos(f,l)
}

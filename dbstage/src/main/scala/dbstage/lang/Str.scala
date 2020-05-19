package dbstage.lang

import squid.quasi.lift

@lift
class Str private(val string: String) {
  private type Word = Long
  private type CSize = Word
  private type CChar = Byte

  def strlen: CSize = ???.asInstanceOf[CSize]
  def charAt(index: Word): CChar = ???.asInstanceOf[CChar]
}
object Str {
  implicit def apply(str: String): Str = new Str(str)
}

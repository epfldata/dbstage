package dbstage.lang

import squid.quasi.lift

@lift
class Str(val string: String) {
  private type Word = Long
  private type CSize = Word
  private type CChar = Byte

  def strlen: CSize = ???.asInstanceOf[CSize]
  def charAt(index: Word): CChar = ???.asInstanceOf[CChar]
}
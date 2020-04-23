package lib

import scala.scalanative.unsafe._

@extern
object libc {
  def malloc(size: CSize): Ptr[Byte] = extern
}

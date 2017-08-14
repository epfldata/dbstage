package dbstage

import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}

package object frontend {
  
  // Can make freestanding fields not associated with a table
  abstract class Field(val name: String) {
    type T
    implicit val IRTypeT: IRType[T]
    implicit val SerialT: Serial[T]
  }
  
  def log(msg: String) = System.err.println(s"[${new java.util.Date}] $msg")
  
}

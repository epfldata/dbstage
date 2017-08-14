package dbstage

import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}

package object frontend {
  
  def log(msg: String) = System.err.println(s"[${new java.util.Date}] $msg")
  
}

package dbstage
package example

import squid.utils._
import Embedding.Predef._
import Embedding.Quasicodes._

sealed abstract class Sex extends Product with Serializable {
  def serialize: String
}
object Sex {
  implicit val serial = new Serial[Sex](
    str => ir{ if (${str} == "M") Male else if (${str} == "F") Female else lastWords(s"Unknown sex: ${${str}}") }, 
    s => ir{ ${s}.serialize })
}
case object Male   extends Sex { def serialize = "M" }
case object Female extends Sex { def serialize = "F" }

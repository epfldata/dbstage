package dbstage

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._

package object query {
  
  def from(rel: Relation): From[rel.type] = new From(rel)
  
}

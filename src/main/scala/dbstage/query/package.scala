package dbstage

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep => Code, _}

package object query {
  
  //type Producer[T] = (T => Unit) => Bool
  //type Result[T] = CrossStage[Producer[T]]
  
  def from(rel: Relation): From[rel.type] = new From(rel)
  
  //implicit def from2rel[R <: Relation](f: From[R]): R = 
    //f.rel in f
    //new RelationFrom(f)
    //f.rel.withQuery(f)
  //class RelationFrom[R <: Relation](f: query.From[R]) {
  //}
  
}

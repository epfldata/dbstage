package dbstage
package query

import squid.quasi.{phase, embed}
import cats.Monoid
import cats.Semigroup
import cats.syntax.all._
import squid.lib.transparencyPropagating

@embed
class EmbeddedDefs {
  
  // Note: this is here and not in Wraps so that it can be found for type T even when there is no proper Wraps companion
  // object for T (which happens if T is not a class type but, say, a type member/synonym)
  // NOTE: the order of implicit parameters matters! having Monoid[B] in front does not seem to work!
  @desugar
  implicit def monoidWrap[A,B](implicit ev: A Wraps B, m: Monoid[B]): Monoid[A] = {
    monoidInstance(ev(m.empty))((x,y) => ev(ev.deapply(x) |+| ev.deapply(y)))
  }
  @desugar
  implicit def semigroupWrap[A,B](implicit ev: A Wraps B, m: Semigroup[B]): Semigroup[A] = {
    semigroupInstance((x,y) => ev(ev.deapply(x) |+| ev.deapply(y)))
  }
  
  @desugar
  implicit def orderingWrap[A,B](implicit ev: A Wraps B, ord: Ordering[B]): Ordering[A] = {
    Ordering.by(ev.deapply)
  }
  
  //import scala.collection.immutable.TreeMap  // FIXME handling of those in @embed'ed code
  
  //@transparencyPropagating
  //implicit def monoidTreeMap[A:Ordering,B:Monoid]: Monoid[TreeMap[A,B]] = monoidInstance[TreeMap[A,B]](TreeMap.empty)((xs,ys) => xs ++ ys.map {
  //  //case (k,v) =>  // Embedding Error: Unsupported feature
  //  kv =>
  //    val k = kv._1
  //    val v = kv._2
  //    k -> xs.get(k).fold(v)(Monoid[B].combine(v,_))
  //})
  
}

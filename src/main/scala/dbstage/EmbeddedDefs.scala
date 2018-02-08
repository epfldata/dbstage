package dbstage

import squid.quasi.{embed,phase}
import cats.Monoid
import cats.syntax.all._

@embed
class EmbeddedDefs {
  
  // Note: this is here and not in Wraps so that it can be found for type T even when there is no proper Wraps companion
  // object for T (which happens if T is not a class type but, say, a type member/synonym)
  // NOTE: the order of implicit parameters matters! having Monoid[B] in front does not seem to work!
  @phase('Sugar)
  implicit def monoidWrap[A,B](implicit ev: A Wraps B, m: Monoid[B]): Monoid[A] = {
    monoidInstance(ev(m.empty))((x,y) => ev(ev.deapply(x) |+| ev.deapply(y)))
  }
  
}

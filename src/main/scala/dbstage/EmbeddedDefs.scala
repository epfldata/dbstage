package dbstage

import squid.quasi.{embed,phase}
import cats.Monoid
import cats.syntax.all._

@embed
class EmbeddedDefs {
  @phase('Sugar)
  implicit def monoidWrap[A,B](implicit ev: A Wraps B, m: Monoid[B]): Monoid[A] = {
    //monoidInstance(ev.apply(m.empty))((x,y) => ev.apply(ev.getValue(x) |+| ev.getValue(y)))
    monoidInstance(ev(m.empty))((x,y) => ev(ev.deapply(x) |+| ev.deapply(y)))
  }
}

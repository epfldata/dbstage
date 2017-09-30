import squid.utils._
import squid.lib.transparent

package object dbstage {
  
  val MAX_SCALA_TUPLE_ARITY = 22
  
  @inline @transparent
  def loopWhile(cnd: => Bool) = {
    while(cnd)()
  }
  
  type IteratorRep[T] = () => (() => Bool, () => T)
  
}

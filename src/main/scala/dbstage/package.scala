import squid.utils._
import squid.lib.transparent

package object dbstage {
  
  @transparent
  def loopWhile(cnd: => Bool) = {
    while(cnd)()
  }
  
  type IteratorRep[T] = () => (() => Bool, () => T)
  
}

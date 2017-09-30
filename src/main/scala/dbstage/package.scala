import squid.utils._
import squid.lib.transparent

package object dbstage {
  
  val MAX_SCALA_TUPLE_ARITY = 22
  
  @inline @transparent
  def loopWhile(cnd: => Bool) = {
    while(cnd)()
  }
  
  type IteratorRep[T] = () => (() => Bool, () => T)
  
  import scala.language.implicitConversions
  import Embedding.Predef._
  implicit def interop[T](q: Code[T]): IR[T,Any] = q.asClosedIR // because currently Squid requires an IR (eg: for calling .compile and .run)
  
}

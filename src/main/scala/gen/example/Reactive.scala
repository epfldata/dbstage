package gen
package example

import squid.utils._
import squid.lib.MutVar
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code => c, _}
import sourcecode.{Name => SrcName}

import scala.collection.mutable

trait Reactive extends Embedding.ProgramGen {
  
  class StateMachine[-C](implicit srcName: SrcName) extends Class[C] {
    val signals = mutable.Buffer[Signal[_]]()
    
    class Signal[T:CodeType] /*private[ProgramGen]*/(bodyFun: => Code[T, Ctx])(implicit srcName: SrcName) { //extends Method[T](name) {
      signals += this
      val curVal = field(c"MutVar($bodyFun)")(implicitly,srcName.value+"_val")
    }
    
  }
  
  
}

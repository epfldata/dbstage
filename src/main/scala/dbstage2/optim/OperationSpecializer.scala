//package dbstage
//package optim
//
//import squid.utils._
//import squid.ir.BottomUpTransformer
//import squid.ir.FixPointRuleBasedTransformer
//import Embedding.Predef._
//
//class OperationSpecializer extends Embedding.SelfTransformer with FixPointRuleBasedTransformer with BottomUpTransformer {
//  import runtime.GenericOps._
//  
//  def report(msg: String) = throw new RuntimeException(msg)
//  
//  object Irrefutable {
//    def unapply[T:IRType,C](x: IR[T,C]): Some[IR[T,C]] = x match {
//      case ir"$v:T" => Some(v)
//      // ^ Q: can match sthg like (42:Any)  ?
//      case _ => report(s"Unexpected operand of type ${typeRepOf[T]}: $x")
//    }
//  }
//  
//  rewrite {
//  
//    case ir"add($lhs:Int,${Irrefutable(rhs)}:Int)" => ir"$lhs + $rhs"
//
//    case ir"not(${Irrefutable(self)}:Bool)" => ir"!$self"
//    case ir"and(${Irrefutable(lhs)}:Bool,${Irrefutable(rhs)}:Bool)" => ir"$lhs && $rhs"
//      
//  }
//  
//}

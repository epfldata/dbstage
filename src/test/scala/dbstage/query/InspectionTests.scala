package dbstage
package query

import dbstage.compiler._
import org.scalatest.FunSuite
import Embedding.Predef._
import Embedding.Quasicodes._
import dbstage.Embedding.HollowedCode

class InspectionTests extends FunSuite {
  
  test("Basics") {
    
    case class InsRes[T:CodeType,S:CodeType,C](h: HollowedCode[T,S,C])
    
    def process[T:CodeType,C](p: Code[T,C]) = {
      object Ins extends Embedding.Inspector[T,C,InsRes[T,_,C]] {
        override def traverse[S:CodeType]: PartialFunction[Code[S,C], HollowedCode[T,S,C] => InsRes[T,S,C]] = {
          case code"readInt" => h => InsRes(h)
        }
      }
      Ins(p)
    }
    
    val pgrm = code"println(readInt + (?n:Int) + readDouble + readInt)"
    
    println(process(pgrm))
    
    
    
  }
  
  
  
}

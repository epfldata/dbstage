package dbstage
package moncomp

import cats.kernel.CommutativeMonoid
import squid.utils._

object MyTests {
  
  import Embedding.Predef._
  
  def init() = {
    //OnlineRewritings.rewrite {
      
      //case code"comprehend[$tf,$ta,$tr]($prod,$pred)($mkRes)($fcol,$rmon)" =>
      //  code"???"
        
    //}
  }
  init()
  
  def main(args: Array[String]): Unit = {
    import cats.Monoid
    import cats.data.NonEmptyList
    import cats.instances.all._
    
    //println(MkNonEmptyPaper[Int,scala.collection.immutable.List[Int]](0,Nil,_ :: _)) // type arguments [Int,List[Int]] do not conform to method apply's type parameter bounds [A,As <: dbstage.moncomp.Container[A]]
    //println(MkNonEmptyPaper(0,List.empty,_ :: _))
    //println(MkNonEmptyPaper[Int,List[Int]](0,List.empty,_ :: _))
    
    //implicitly[Monoid[List[Int]]]
    //Debug show 
    //implicitly[Monoid[Map[Int,NonEmptyList[Int]]]]
    
    //implicitly[CommutativeMonoid[Int]]
    
    
    
  }
  
}

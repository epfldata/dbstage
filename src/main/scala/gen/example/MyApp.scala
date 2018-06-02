package gen.example

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

object MyApp extends Embedding.ProgramGen with Vectors with App {
  val root = Root.apply
  
  val V3 = Vector[Int](3)
  //println(V3.prod)
  println(V3.showCode)
  
  import V3.{Self => V3T}
  //println(c{(v: V3T) => $(V3.xs(1))(v)+1})
  //println(c"(v: V3T) => ${V3.xs(0)}(v)+1")
  //println(V3.prod.inlined)
  
  val VD4 = Vector[Double](4)
  //println(VD4.showCode)
  
  println("Done.")
  
}

package gen.example

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import Embedding.Quasicodes.{code=>c,_}

object MyApp extends Embedding.ProgramGen with Vectors with App {
  val root = Root.apply
  
  val V3 = Vector(3)
  import V3.{Self => V3T}
  println(V3.showCode)
  //println(c{(v: V3T) => $(V3.xs(1))(v)+1})
  //println(c"(v: V3T) => ${V3.xs(0)}(v)+1")
  //println(V3.prod.inlined)
  
  println("Done.")
  
}

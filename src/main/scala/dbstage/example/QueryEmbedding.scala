package dbstage
package example

import squid.utils._

import scala.io.Codec
import scala.util.Try
import scala.util.control.NonFatal
//import RecordDefs._
import Embedding.Predef._
//import cats.instances.all._
import cats.instances.all.{
  catsKernelOrderingForOrder=>_,
  catsKernelStdAlgebraForUnit => _,
  // ^ exposes: Unsupported feature: Refinement type 'cats.kernel.BoundedSemilattice[Unit] with cats.kernel.CommutativeGroup[Unit]'
  _}
//import scala.math.Ordering.Int
import cats.Monoid
import Gender.Read
//import Staged.apply

object QueryEmbedding extends App {
  /*
  //def foldr[A,B](xs: Iterable[A])(z: B)(f: (A, => B) => B): B = {
  //  val it = xs.iterator
  //  //@tailrec def go() = 
  //  //go()
  //  var acc = z
  //  var continue = true
  //  while (continue && it.hasNext) {
  //    continue = false
  //    acc = f(it.next, {continue = true; acc})
  //  }
  //  acc
  //}
  def foldr[A,B](xs: Iterable[A])(z: B)(f: (A, => B) => B): B = {
    val it = xs.iterator
    //def go: B = if (it.hasNext) f(it.next,go) else z  // unsafe w.r.t. re-eval
    def go: B = {
      lazy val rest = go
      if (it.hasNext) f(it.next,rest) else z
    }
    go
  }
  
  val ls = List(1,2,3,4,5,6)
  println(foldr(ls)(0)(_ + _), ls.sum)
  //println(foldr(ls)(0){(a,mkB) => if (a < 4) mkB + a else 0})
  println(foldr(ls)(List.empty[Int]){(a,mkB) => 
  //println(foldr(ls)(List(42)){(a,mkB) => 
    print(s"Eval $a; ")
    //print(s"Eval $a $mkB; ")
    //if (a < 4) a::mkB else Nil})
    if (a < 4) {print(s"Mk $mkB; ");a::mkB} else Nil})
  */
  
  //println(implicitly[Codec] == implicitly[Codec])
  
  //System exit 0
  
  
  
  //val q0 = code{
  //  
  //}
  
  //def compileQuery[R:CodeType](q: Staged[R]) = {
  def compileQuery[R:CodeType](q: Lifted[R]) = {
    val cde = q.embedded(Embedding)
    println(cde)
    //println(q.plain)
    //QueryCompiler.compile(cde).fold { err =>
    //  System.err.println("Could not lift query: $")
    //  q.plain
    //} { res =>
    //  res
    //}
    //Try(QueryCompiler.compile(cde)).recover { err =>
    try QueryCompiler.compile(cde) catch { case NonFatal(err) 
      if false 
    =>
      System.err.println(s"Could not lift query: ${err.getMessage} (${
        err.getStackTrace.dropWhile(_.toString startsWith "scala.").headOption.getOrElse(err.getStackTrace.head)})")
      //q.plain
      cde.compile  // TODO
      //cde.run
      die
    }
  }
  
  val sq = Staged { //() =>
    
    for {
      male   <- persons where (_[Gender] == Male)
      female <- persons where (_[Gender] == Female)
    //} yield Min(Age(math.abs(male[Age] - female[Age])))
    //} yield male[Age]
    } yield math.abs(male[Age] - female[Age])
    
  }
  val q0 = compileQuery(sq)
  
  //System exit 0
  
  //OnlineRewritings.TranformerDebug.debugFor{
  val exec = q0.compile
  //println(exec,sq.plain,sq.embedded(Embedding).compile)
  //println(exec(println))
  //}
  val raw = code"() => ${sq.embedded(Embedding)}".compile
  
  println(exec())
  
  val N = 100
  
  for (_ <- 1 to 5) {
    time(N)(t => println(s"Plain : $t ms"))(sq.plain)  alsoApply println
    time(N)(t => println(s"Staged: $t ms"))(raw())  alsoApply println
    time(N)(t => println(s"Opt   : $t ms"))(exec())  alsoApply println
    println
  }
  
  
  
  
}

object QueryEmbedding2 extends App {
  
  val sq = Staged/*.dbg*/ { //() =>
    
    for {
      male   <- persons where (_[Gender] == Male)
      female <- persons where (_[Gender] == Female)
    } yield Min(math.abs(male[Age] - female[Age]),0)
    
  }
  
  val cq = QueryEmbedding.compileQuery(sq)
  
  println(cq.compile.apply())
  println(sq.plain)
  
  
  
  
}



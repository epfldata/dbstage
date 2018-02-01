import cats.kernel.Monoid

package object dbstage {
  
  import squid.utils._
  
  import dbstage2.{Record,::,NoFields,Project,Access,Field}
  
  //implicit class FieldFunOps[A,F<:Field[A]](private val f: A => Field[A]) extends AnyVal {
  //  def apply[R](r:R)(implicit ev: R Access F): F = ???
  //  //def get[R](r:R)(implicit ev: F Access R): F = ???
  //}
  
  implicit object unitMonoid extends Monoid[Unit] {
    def empty = ()
    def combine(a: Unit, b: Unit) = ()
  }
  
  //val indent = 2
  def indentString(str: String) = {
    //val lines = str.splitSane('\n')
    //lines map (Console.RED + "| " * indent + ">> " + Console.RESET + Console.BOLD + _) mkString s"${Console.RESET}\n"
    val lines = str.splitSane('\n')
    val pre = "| "
    lines map (Debug.GREY + pre + Console.RESET + _) mkString "\n"
  }
  
  //import dbstage2.Embedding.Predef._
  //type PreciseCode[T,-C] = Code[T,C]{type Typ = T}
  //def asPreciseCode[T,C](cde: Code[T,C]): PreciseCode[cde.Typ,C] = cde.withUnderlyingTyp.asInstanceOf[PreciseCode[cde.Typ,C]]
  
  
}

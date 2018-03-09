package dbstage
package moncomp

import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.kernel.CommutativeMonoid
import cats.kernel.CommutativeSemigroup
import Embedding.Predef._

//sealed class MonoidComprehension {
//}
//case class Path() extends MonoidComprehension

//abstract 
case class Comprehension[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]) extends QueryRepr[R,C]
object Comprehension {
  //def apply[R:CodeType,C](productions: Productions[R,C], mon: Code[Monoid[R],C]) = ???
}

//sealed abstract class Productions[R:CodeType,C]
sealed abstract class Productions[R,C]
//abstract class Iteration[A:CodeType,As:CodeType,R,C](src: StagedDataSource[A,As,C]) extends Productions[R,C] {
abstract class Iteration[A:CodeType,As:CodeType,R,C](src: Path[A,As,C]) extends Productions[R,C] {
  def A = codeTypeOf[A]
  val v: Variable[A]
  val body: Productions[R,C & v.Ctx]
}
// TODO Binding? -> defined SingleElement instead

//case class Predicate[C]() extends Productions[C]
//abstract 
case class Yield[R,C](pred: Code[Bool,C], cde: Code[R,C]) extends Productions[R,C]
object Yield {
  //def apply[R,C](pred: Code[Bool,C], cde: Code[R,C])(enclosingMonoid: StagedMonoid[R,C]) = cde match {
  //  case 
  //}
}

sealed abstract class Path[A:CodeType,As:CodeType,C]
case class StagedDataSource[A:CodeType,As:CodeType,C](cde: Code[As,C], srcEv: Code[As SourceOf A,C]) extends Path[A,As,C] {
  
}
case class SingleElement[A:CodeType,C](cde: Code[A,C]) extends Path[A,A,C]

//case class Query[A:CodeType,B:CodeType,C](body: Comprehension[A,C], postProcess: Code[A=>B,C])
abstract class QueryRepr[A:CodeType,C]
case class Sorting[A:CodeType,As:CodeType,C](underlying: QueryRepr[A,C], asSrc: StagedDataSource[A,As,C], ord: StagedOrdering[A,C])
  extends QueryRepr[SortedBy[A,A],C]

object QueryLifter {
  def liftQuery[T:CodeType,C](q: Code[T,C]): Query[T,C] = q match {
    //case 
    case r => die //Query(r, code"idenityt[T] _")
  }
  def liftComprehension[T:CodeType,C](q: Code[T,C]) = q match {
    case r => die
  }
}


abstract class StagedOrdering[A:CodeType,C]




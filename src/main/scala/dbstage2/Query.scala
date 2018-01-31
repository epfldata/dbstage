package dbstage2

//import cats.Applicative
//import cats.Monad
import squid.ir.BaseInterpreter
import squid.utils._
import squid.lib.transparencyPropagating

//class Count[F<:FieldModule]
case class Count(value: Int) extends Field[Int]

// TODO better representation of aggregates
//abstract class Query[T<:Record] {
abstract class Query[T] {
  def run: Iterable[T]
  @transparencyPropagating
  def filter(pred: T => Bool): Query[T] = Filter(this, pred)
  def count: Query[Count::NoFields] = CountQuery(this)
}
//case class Scan[T<:Record](r: Relation[T]) extends Query[T] {
case class Scan[T](r: Relation[T]) extends Query[T] {
  def run = ???
}
//case class CountQuery[T<:Record](r: Query[T]) extends Query[Count::NoFields] {
case class CountQuery[T](r: Query[T]) extends Query[Count::NoFields] {
  def run = ???
}
//case class Filter[T<:Record](q: Query[T], pred: T => Bool) extends Query[T] {
case class Filter[T](q: Query[T], pred: T => Bool) extends Query[T] {
  def run = ???
}
case class Join[A,B](lhs: Query[A], rhs: Query[B])(pred: (A,B) => Bool) extends Query[(A,B)] {
  def run = ???
}

abstract class QueryHK {
  val base: squid.lang.CrossStageEnabled with squid.lang.ScalaCore
  import base.Predef._
  type F[T,C] = base.Code[T,C]
  
  sealed abstract class Query[T:CodeType,C] {
    @transparencyPropagating
    //def filter(pred: F[T => Bool,C]): Query[T,C] = Filter(this, pred)
    def filter(pred: T => Bool): Query[T,C] = Filter(this, CrossStage(pred))
    def count: Query[Count::NoFields,C] = CountQuery(this)
  }
  case class Scan[T:CodeType,C](r: Relation[T]) extends Query[T,C]
  abstract case class Filter[T:CodeType,C](q: Query[T,C], pred: F[T => Bool,C]) extends Query[T,C]
  object Filter {
    def apply[T:CodeType,C](q: Query[T,C], pred: F[T => Bool,C]): Filter[T,C] = q match {
      case Filter(q0, f) => Filter(q0, code"(x:T) => $pred(x) && $f(x)")
      case q => new Filter(q, pred){}
    }
  }
  case class CountQuery[T,C](r: Query[T,C]) extends Query[Count::NoFields,C]
}
object PlainQueryInterp extends BaseInterpreter with squid.lang.ScalaCore
object PlainQuery extends QueryHK {
  //val base = new BaseInterpreter with squid.lang.ScalaCore
  //object base extends BaseInterpreter with squid.lang.ScalaCore // crashes compiler
  val base = PlainQueryInterp
}
object StagedQuery extends QueryHK {
  val base: Embedding.type = Embedding
}

import Embedding.Predef._

/*
sealed abstract class QueryHK[T,F[_]] {
  @transparencyPropagating
  def filter(pred: F[T => Bool]): QueryHK[T,F] = FilterHK(this, pred)
}
case class ScanHK[T,F[_]](r: Relation[T]) extends QueryHK[T,F]
case class FilterHK[T,F[_]](q: QueryHK[T,F], pred: F[T => Bool]) extends QueryHK[T,F]
object QueryHK {
  type Identity[T] = T
  type PlainQuery[T] = QueryHK[T,Identity]
  type StagedQuery[T,C] = QueryHK[T,({type F[T]=Code[T,C]})#F]
}
*/

/*
import scala.language.higherKinds

abstract class QueryHK {
  type F[_,_]
  //implicit def FMonad[C]: Monad[F[?,C]]
  implicit def FApp[C]: Applicative[F[?,C]] // useless
  
  def flat[A,B,C](fa: F[A,C])(f: A => F[B,C]): F[B,C]
  //def flat[A,B,C](fa: F[A,C])(f: F[A => B,C]): F[B,C]
  
  def compose[A,B,C,Ctx](fa: F[A => B,Ctx], fb: F[B => C,Ctx]): F[A => C,Ctx]
  
  sealed abstract class Query[T,C] {
    @transparencyPropagating
    def filter(pred: F[T => Bool,C]): Query[T,C] = Filter(this, pred)
    def count: Query[Count::NoFields,C] = CountQuery(this)
  }
  case class Scan[T,C](r: Relation[T]) extends Query[T,C]
  abstract case class Filter[T,C](q: Query[T,C], pred: F[T => Bool,C]) extends Query[T,C]
  object Filter {
    def apply[T,C](q: Query[T,C], pred: F[T => Bool,C]): Filter[T,C] = q match {
      case Filter(q0, f) => 
        //import cats._
        //val v = new Variable[]
        //Filter(q0, code"${FApp[C].ap()}")
        //FApp[C].pure((x:T) => )
        //FApp[C].ap(pred)(ap)
        //flat(pred)
        ???
      case q => new Filter(q, pred){}
    }
  }
  case class CountQuery[T,C](r: Query[T,C]) extends Query[Count::NoFields,C]
}
object PlainQuery extends QueryHK {
  type F[T,C] = T
  // ^ note: we could consider the problem of parametrizing shallow queries so they can be optimized only once, in which case
  //   we'd have:  type F[T,C] = C => T ;  but the interface would be more awkward (basically requiring users to build Query objects manually)
  
  def flat[A,B,C](fa: F[A,C], f: A => F[B,C]): F[B,C] = f(fa)
  def compose[A,B,C,Ctx](fa: F[A => B,Ctx], fb: F[B => C,Ctx]): F[A => C,Ctx] = fa andThen fb
  
  /*
  def FMonad[C]: Monad[F[?,C]] = new Monad[F[?,C]] {
    // Members declared in cats.Applicative
    def pure[A](x: A): A = x
    // Members declared in cats.FlatMap
    def flatMap[A, B](fa: A)(f: A => B): B = f(fa)
    def tailRecM[A, B](a: A)(f: A => Either[A,B]): B = ???
  }
  */
  def FApp[C]: Applicative[F[?,C]] = new Applicative[F[?,C]] {
    // Members declared in cats.Applicative
    def pure[A](x: A): A = x
    // Members declared in cats.Apply
    def ap[A, B](ff: A => B)(fa: A): B = ff(fa)
  }
}
object StagedQuery extends QueryHK {
  type F[T,C] = Code[T,C]
  
  def flat[A,B,C](fa: F[A,C], f: A => F[B,C]): F[B,C] = ???
  def compose[A,B,C,Ctx](fa: F[A => B,Ctx], fb: F[B => C,Ctx]): F[A => C,Ctx] = ???
  
  /*
  def FMonad[C]: Monad[F[?,C]] = new Monad[F[?,C]] {
    // Members declared in cats.Applicative
    def pure[A](x: A): Code[A,C] = {
      //code"x" // Embedding Error: Unknown type `A` does not have a TypeTag to embed it as uninterpreted.
      val a:Any = x
      code"a".asInstanceOf[Code[A,C]]
    }
    // Members declared in cats.FlatMap
    def flatMap[A, B](fa: Code[A,C])(f: A => Code[B,C]): Code[B,C] = {
      code"f($fa)".compile // Embedding Error: Unknown type `B` does not have a TypeTag to embed it as uninterpreted.
      //val a: Any = fa
      ////val g: Any => Any = f.asInstanceOf[Any => Any]
      //val g: Any => Any = f.asInstanceOf[Any => Any]
      ////code"g()".asInstanceOf[Code[A,C]]
    }
    def tailRecM[A, B](a: A)(f: A => Code[Either[A,B],C]): Code[B,C] = ???
  }
  */
  def FApp[C]: Applicative[F[?,C]] = new Applicative[F[?,C]] {
    // Members declared in cats.Applicative
    def pure[A](x: A): Code[A,C] = {
      //code"x" // Embedding Error: Unknown type `A` does not have a TypeTag to embed it as uninterpreted.
      val a:Any = x
      code"a".asInstanceOf[Code[A,C]]
    }
    // Members declared in cats.Apply
    def ap[A, B](ff: Code[A => B,C])(fa: Code[A,C]): Code[B,C] = ff match {
      //case dbg_code"$f: ($a => $b)" => code"$f($fa.asInstanceOf[$a]):B" // Embedding Error: Unknown type `B` does not have a TypeTag to embed it as uninterpreted.
      case code"$f: ($a => $b)" => code"$f(${fa.asInstanceOf[Code[a.Typ,C]]})".asInstanceOf[Code[B,C]]
    }
  }
}
*/

/*
abstract class QueryHK {
  type F[_]
  sealed abstract class Query[T] {
    @transparencyPropagating
    def filter(pred: F[T => Bool]): Query[T] = Filter(this, pred)
    def count: Query[Count::NoFields] = CountQuery(this)
  }
  case class Scan[T](r: Relation[T]) extends Query[T]
  case class Filter[T](q: Query[T], pred: F[T => Bool]) extends Query[T]
  case class CountQuery[T](r: Query[T]) extends Query[Count::NoFields]
}
object PlainQuery extends QueryHK {
  type F[T] = T
  
}
class StagedQuery[C] extends QueryHK {
  type F[T] = Code[T,C]
  
}
*/

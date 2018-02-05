package dbstage2
package record_experiments.v6

import scala.annotation.implicitNotFound
import scala.annotation.unchecked.uncheckedVariance

import squid.utils._

import dbstage2.Embedding

case class ~[A,B](lhs: A, rhs: B) {
  //override def toString: String = ???
}

object Ops {
  implicit class RecordSyntax[A](self: A) {
    def ~ [B] (that: B) = new ~(self,that)
  }
}
import Ops._

trait Record[A,F[_]]
object Record {
  implicit def fromLHS[A,B,F[_]](implicit ev: A Record F): (A ~ B) Record F = ???
  implicit def fromRHS[A,B,F[_]](implicit ev: B Record F): (A ~ B) Record F = ???
  implicit def fromF[T,F[_]](implicit ev: F[T]): T Record F = ???
}




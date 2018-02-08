package dbstage

import squid.lib.transparencyPropagating
import squid.lib.transparent
import squid.utils._

case class Separator(chr: Char)
object Separator {
  implicit def default = Separator('|')
}

abstract class Read[T] { def apply(str: String): T }
// TODO add implicitNotFound
object Read {
  
  def instance[T](app: String => T): Read[T] = new Read[T] {
    def apply(str: String) = app(str)
  }
  
  @transparencyPropagating
  implicit def readWrapped[F,V](implicit w: F Wraps V, readV: Read[V]): Read[F] = new Read[F] {
    def apply(str: String) = w(readV(str))
  }
  
  implicit object readBool extends Read[Bool] { def apply(str: String) = str.toBoolean }
  implicit object readDouble extends Read[Double] { def apply(str: String) = str.toDouble }
  implicit object readInt extends Read[Int] { def apply(str: String) = str.toInt }
  implicit object readLong extends Read[Long] { def apply(str: String) = str.toLong }
  implicit object readString extends Read[String] { def apply(str: String) = str }
  //implicit object readDate extends Read[Date] { def apply(str: String) = Date(str) }
  
}

trait RecordRead[T] { def read(separator: Char): String => T }
object RecordRead extends RecordReadLowPrio {
  
  @transparent 
  //implicit def read[A:Read]: RecordRead[A] = new RecordRead[A] { def read(separator: Char): String => A = str => the[Read[A]].apply(str) }
  // ^ used to work!, now: both method the in package dbstage and value evidence$1 match expected type dbstage.Read[A]
  implicit def read[A](implicit rd: Read[A]): RecordRead[A] = new RecordRead[A]
  { def read(separator: Char): String => A = str => rd.apply(str) }
  
  @transparencyPropagating
  implicit def readLHS[A:Read,B:RecordRead]: RecordRead[A ~ B] = new RecordRead[A ~ B] {
    def read(separator: Char): String => (A ~ B) = str => {
      // TODO better impl...
      val (hd,tl) = str.span(_ != separator)
      //println(s"ReadLHS $str = '${hd}' + '${tl.tail}'")
      the[Read[A]].apply(hd) ~ the[RecordRead[B]].read(separator)(if (tl.isEmpty) tl else tl.tail)
    }
  }
}
class RecordReadLowPrio {
  
  @transparencyPropagating
  implicit def readRHS[A:RecordRead,B:Read]: RecordRead[A ~ B] = new RecordRead[A ~ B] {
    def read(separator: Char): String => (A ~ B) = str => {
      // TODO better impl...
      val (hd,tl) = str.reverse.span(_ != separator)
      //println(s"ReadRHS $str = '${tl.tail.reverse}' + '${hd.reverse}'")
      the[RecordRead[A]].read(separator)(if (tl.isEmpty) tl else tl.tail.reverse) ~ the[Read[B]].apply(hd.reverse)
    }
  }
  
}

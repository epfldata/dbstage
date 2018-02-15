package dbstage

import squid.lib.transparencyPropagating
import squid.lib.transparent
import squid.quasi.{embed,phase,doNotEmbed}
import squid.utils._

case class Separator(chr: Char)
object Separator {
  implicit def default = Separator('|')
}

// TODO add implicitNotFound
abstract class Read[T] { @transparent def apply(str: String): T }
@embed
object Read {
  
  @doNotEmbed @transparencyPropagating
  def instance[T](app: String => T): Read[T] = new Read[T] {
    def apply(str: String) = app(str)
  }
  
  //@transparencyPropagating
  //implicit def readWrapped[F,V](implicit w: F Wraps V, readV: Read[V]): Read[F] = new Read[F] {
  //  def apply(str: String) = w(readV(str))
  //}
  @phase('Sugar)
  implicit def readWrapped[F,V](implicit w: F Wraps V, readV: Read[V]): Read[F] = Read.instance[F] {
    (str: String) => w(readV(str))
  }
  
  /*
  implicit object readBool extends Read[Bool] { def apply(str: String) = str.toBoolean }
  implicit object readDouble extends Read[Double] { def apply(str: String) = str.toDouble }
  implicit object readInt extends Read[Int] { def apply(str: String) = str.toInt }
  implicit object readLong extends Read[Long] { def apply(str: String) = str.toLong }
  implicit object readString extends Read[String] { def apply(str: String) = str }
  //implicit object readDate extends Read[Date] { def apply(str: String) = Date(str) }
  */
  
  // TODO make Squid inline them if they're `val`
  @phase('Sugar) implicit def readBool = Read.instance[Bool] { (str: String) => str.toBoolean }
  @phase('Sugar) implicit def readDouble = Read.instance[Double] { (str: String) => str.toDouble }
  @phase('Sugar) implicit def readInt = Read.instance[Int] { (str: String) => str.toInt }
  @phase('Sugar) implicit def readLong = Read.instance[Long] { (str: String) => str.toLong }
  @phase('Sugar) implicit def readString = Read.instance[String] { (str: String) => str }
  @phase('Sugar) implicit def readDate = Read.instance[example.tpch.Date] { (str: String) => example.tpch.Date(str) }
  
}

@embed
trait RecordRead[T] {
  def apply(parts: Iterator[String]): T
  @phase('Sugar)
  def read(separator: Char): String => T = 
    //_.splitSane(separator).iterator |> apply
    splitString(_, separator) |> apply
}
//@embed
object RecordRead extends RecordReadLowPrio {
  
  @doNotEmbed @transparencyPropagating
  def instance[T](app: Iterator[String] => T): RecordRead[T] = new RecordRead[T] {
    def apply(parts: Iterator[String]): T = app(parts)
  }
  
  @phase('Sugar)
  //implicit def read[A:Read]: RecordRead[A] = new RecordRead[A] { def read(separator: Char): String => A = str => the[Read[A]].apply(str) }
  // ^ used to work!, now: both method the in package dbstage and value evidence$1 match expected type dbstage.Read[A]
  implicit def read[A](implicit rd: Read[A]): RecordRead[A] = 
    instance[A] { parts => rd.apply(parts.next) }
  
  @phase('Sugar)
  implicit def readLHS[A:Read,B:RecordRead]: RecordRead[A ~ B] =
    instance { parts => the[Read[A]].apply(parts.next) ~ the[RecordRead[B]].apply(parts) }
  
}
@embed
class RecordReadLowPrio {
  
  @phase('Sugar)
  implicit def readRHS[A:RecordRead,B:Read]: RecordRead[A ~ B] =
    RecordRead.instance { parts => the[RecordRead[A]].apply(parts) ~ the[Read[B]].apply(parts.next) }
  
}

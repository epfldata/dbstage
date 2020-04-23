package lib

import scala.scalanative.unsafe._

object LMDBTable {
  def init_environment(path: String): Ptr[Byte] = ???

  def intcpy(value: Ptr[Byte], field: Long, siz: Long)(implicit zone: Zone): Unit = {
    val size = siz.toInt
    val bytes = BigInt(field).toByteArray.reverse.padTo(size, 0.toByte).reverse

    for ( i <- 0 until size ) {
      !(value + i) = bytes(i)
    }
  }
}

case class LMDBTable[T](env: Ptr[Byte]) {
  def size: Long = ???
  def get(key: Long): T = ???
  def put(key: Long, valueSize: Long, value: Ptr[Byte]): Unit = ???
}
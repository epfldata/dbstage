package dbstage.lang

class Ptr[T]

object LMDBTable {
  type Cursor = Ptr[Byte] // Change?
  type CString = Ptr[Byte]
}

class LMDBTable[T] {
  import LMDBTable._

  def size: Int = ???
  def cursor: Cursor = ???
  def first(cursor: Cursor): (Long, Ptr[Byte]) = ???
  def next(cursor: Cursor): (Long, Ptr[Byte]) = ???
}
package dbstage.lang

class Ptr[T]

object LMDBTable {
  type Cursor = Ptr[Byte] // Change?
  type CString = Ptr[Byte]
  type Txn = Ptr[Byte]
  type Dbi = Int // UInt

  def txnBegin(): Unit = ???
  def txnCommit(): Unit = ???
}

class LMDBTable[T] {
  import LMDBTable._

  def size(): Int = ???

  def dbiOpen(): Unit = ???
  def cursorOpen(): Cursor = ???

  def dbiClose(): Unit = ???
  def cursorClose(cursor: Cursor): Unit = ???
  
  def first(cursor: Cursor): T = ???
  def last(cursor: Cursor): T = ???
  def prev(cursor: Cursor): T = ???
  def next(cursor: Cursor): T = ???
}
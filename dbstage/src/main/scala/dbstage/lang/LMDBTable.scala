package dbstage.lang

class Ptr[T]

object LMDBTable {
  type Cursor = Ptr[Byte] // Change?
  type CString = Ptr[Byte]
  type Txn = Ptr[Byte]
  type Dbi = Int // UInt
}

class LMDBTable[T] {
  import LMDBTable._

  def size(): Int = ???

  def txn(): Txn = ???
  def dbi(txn: Txn): Dbi = ???
  def cursor(txn: Txn, dbi: Dbi): Cursor = ???

  def commitTxn(txn: Txn): Unit = ???
  def closeDbi(dbi: Dbi): Unit = ???
  def closeCursor(cursor: Cursor): Unit = ???
  
  def first(cursor: Cursor): (Long, Ptr[Byte]) = ???
  def next(cursor: Cursor): (Long, Ptr[Byte]) = ???
}
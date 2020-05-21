package lib

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
import lmdb4s.bindings._
import lmdb4s.flags._

object LMDBTable {
  val path: CString = toCString("./lmdb-database")(zone)
  lazy val zone: Zone = Zone.open()
  lazy val env: Ptr[Byte] = {
    // Create env
    val envPtr = alloc(1)(tagof[Ptr[Byte]], zone)
    println("env create " + mdb_env_create(envPtr))
    val env_ = !envPtr

    // Set maximum number of databases
    println("Max num dbs " + mdb_env_set_maxdbs(env_, 4))

    // Open env
    println("env open " + mdb_env_open(env_, path, MDB_WRITEMAP, 664))

    env_
  }
  var txn: Ptr[Byte] = null

  def txnBegin()(implicit z: Zone): Unit = if (txn == null) {
    val txPtr = alloc[Ptr[Byte]]
    println("Txn begin " + mdb_txn_begin(env, null, 0, txPtr))
    txn = !txPtr
  }

  def txnCommit()(implicit z: Zone): Unit = {
    println("Txn commit " + mdb_txn_commit(txn))
    txn = null
  }
  
  def free(): Unit = {
    mdb_env_close(env)
    zone.close()
  }

  def get_lmdb_key(key: Long)(implicit z: Zone): KVPtr = {
    val bytes = BigInt(key).toByteArray
    val size = bytes.length

    val lmdb_key = alloc[KVType]
    lmdb_key._1 = size
    lmdb_key._2 = alloc[Byte](size)
    for ( i <- 0 until size ) {
      !(lmdb_key._2 + i) = bytes(i)
    }
    lmdb_key
  }

  def intcpy(ptr: Ptr[Byte], value: Int, size: Int): Unit = {
    val bytes = BigInt(value).toByteArray.reverse.padTo(size, 0.toByte).reverse

    for ( i <- 0 until size ) {
      !(ptr + i) = bytes(i)
    }
  }

  def intget(ptr: Ptr[Byte], size: Int): Int = {
    val bytes = new Array[Byte](size)

    for ( i <- 0 until size ) {
      bytes(i) = !(ptr + i)
    }
    BigInt(bytes).toInt
  }

  def longcpy(ptr: Ptr[Byte], value: Long, size: Int): Unit = {
    val bytes = BigInt(value).toByteArray.reverse.padTo(size, 0.toByte).reverse

    for ( i <- 0 until size ) {
      !(ptr + i) = bytes(i)
    }
  }

  def longget(ptr: Ptr[Byte], size: Int): Long = {
    val bytes = new Array[Byte](size)

    for ( i <- 0 until size ) {
      bytes(i) = !(ptr + i)
    }
    return BigInt(bytes).toLong
  }
}

case class LMDBTable[T](_name: String) {
  import LMDBTable._

  val name: CString = toCString(_name)(zone)
  var dbi: UInt = null

  def dbiOpen()(implicit z: Zone): Unit = {
    val dbiPtr = alloc[UInt]
    println("Dbi open " + mdb_dbi_open(txn, name, MDB_CREATE, dbiPtr))
    dbi = !dbiPtr
  }

  def dbiClose(): Unit = {
    mdb_dbi_close(env, dbi)
    dbi = null
  }

  def cursorOpen()(implicit z: Zone): Ptr[Byte] = {
    val cursorPtr = alloc[Ptr[Byte]]
    mdb_cursor_open(txn, dbi, cursorPtr)
    !cursorPtr
  }

  def cursorClose(cursor: Ptr[Byte]): Unit = {
    mdb_cursor_close(cursor)
  }

  def first(cursor: Ptr[Byte])(implicit z: Zone): T = {
    cursor_get(cursor, MDB_FIRST)
  }

  def next(cursor: Ptr[Byte])(implicit z: Zone): T = {
    cursor_get(cursor, MDB_NEXT)
  }

  private def cursor_get(cursor: Ptr[Byte], op: Int)(implicit z: Zone): T = {
    val key = alloc[KVType]
    val value = alloc[KVType]
    val code = mdb_cursor_get(cursor, key, value, op)

    if (code == 0) {
      value._2.asInstanceOf[T]
    } else {
      null.asInstanceOf[T]
    }
  }

  def size(implicit z: Zone): Long = {
    // Get size
    val stat = alloc[struct_lmdb_stat]
    mdb_stat(txn, dbi, stat)

    // Return
    stat._6
  }

  def get(key: Long)(implicit z: Zone): T = {
    // Get value
    val lmdb_key = get_lmdb_key(key)
    val dataGet = alloc[KVType]
    mdb_get(txn, dbi, lmdb_key, dataGet)

    // Return
    dataGet._2.asInstanceOf[T]
  }

  def put(key: Long, valueSize: Long, value: Ptr[Byte])(implicit z: Zone): Unit = {
    // Put
    val lmdb_key = get_lmdb_key(key)
    val dataPut = alloc[KVType]
    dataPut._1 = valueSize
    dataPut._2 = value
    mdb_put(txn, dbi, lmdb_key, dataPut, 0)
  }

  def getNextKey(implicit z: Zone): Long = size
}
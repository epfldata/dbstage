package lib

import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
import lmdb4s.bindings._
import lmdb4s.flags._

object LMDBTable {
  val path: CString = toCString("./demo-lmdb-database")(zone)
  lazy val zone: Zone = Zone.open()
  lazy val env: Ptr[Byte] = {
    // Create env
    val envPtr = alloc(1)(tagof[Ptr[Byte]], zone)
    mdb_env_create(envPtr)
    val env_ = !envPtr

    // Set maximum number of databases
    mdb_env_set_maxdbs(env_, 5)
    mdb_env_set_mapsize(env_, 1048576L) // 1 MegaByte

    // Open env
    mdb_env_open(env_, path, MDB_WRITEMAP, 664)

    env_
  }
  var txn: Ptr[Byte] = null
  var dbiSize: UInt = null

  def txnBegin()(implicit z: Zone): Unit = if (txn == null) {
    val txPtr = alloc[Ptr[Byte]]
    mdb_txn_begin(env, null, 0, txPtr)
    txn = !txPtr

    val dbiSizePtr = alloc[UInt]
    mdb_dbi_open(txn, toCString("sizes"), MDB_CREATE, dbiSizePtr)
    dbiSize = !dbiSizePtr
  }

  def txnCommit(): Unit = {
    mdb_txn_commit(txn)
    txn = null

    mdb_dbi_close(env, dbiSize)
    dbiSize = null
  }

  def txnAbort(): Unit = {
    mdb_txn_abort(txn)
    txn = null

    mdb_dbi_close(env, dbiSize)
    dbiSize = null
  }
  
  def free(): Unit = {
    mdb_env_close(env)
    zone.close()
  }

  def fill_lmdb_key(key: KVPtr, bytes: Array[Byte], size: Int): Unit = {
    for ( i <- 0 until size ) {
      !(key._2 + i) = bytes(i)
    }
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
  import lib.string.strlen

  val name: CString = toCString(_name)(zone)
  var dbi: UInt = null

  private val key_last_id = {
    val key_last_id_ptr = alloc(1)(tagof[KVType], zone)
    key_last_id_ptr._1 = strlen(name) + 1l
    key_last_id_ptr._2 = name
    key_last_id_ptr
  }

  def dbiOpen(): Unit = {
    val dbiPtr = stackalloc[UInt]
    mdb_dbi_open(txn, name, MDB_CREATE, dbiPtr)
    dbi = !dbiPtr
  }

  def dbiClose(): Unit = {
    mdb_dbi_close(env, dbi)
    dbi = null
  }

  def cursorOpen(): Ptr[Byte] = {
    // stackalloc ptr[Byte] and both KVType
    val cursorPtr = stackalloc[Ptr[Byte]]
    mdb_cursor_open(txn, dbi, cursorPtr)
    !cursorPtr
  }

  def cursorClose(cursor: Ptr[Byte]): Unit = {
    mdb_cursor_close(cursor)
  }

  def first(cursor: Ptr[Byte]): T = cursor_get(cursor, MDB_FIRST)
  def last(cursor: Ptr[Byte]): T = cursor_get(cursor, MDB_LAST)
  def prev(cursor: Ptr[Byte]): T = cursor_get(cursor, MDB_PREV)
  def next(cursor: Ptr[Byte]): T = cursor_get(cursor, MDB_NEXT)

  private def cursor_get(cursor: Ptr[Byte], op: Int): T = {
    val key = stackalloc[KVType]
    val value = stackalloc[KVType]
    val code = mdb_cursor_get(cursor, key, value, op)

    if (code == 0) {
      value._2.asInstanceOf[T]
    } else {
      null.asInstanceOf[T]
    }
  }

  def size(): Long = {
    // Get size
    val stat = stackalloc[struct_lmdb_stat]
    mdb_stat(txn, dbi, stat)

    // Return
    stat._6
  }

  def get(key: Long): T = {
    // Get value
    val lmdb_key = stackalloc[KVType]
    val bytes = BigInt(key).toByteArray
    val size = bytes.length
    lmdb_key._1 = size.toLong
    lmdb_key._2 = stackalloc[Byte](size)
    fill_lmdb_key(lmdb_key, bytes, size)

    val dataGet = stackalloc[KVType]
    mdb_get(txn, dbi, lmdb_key, dataGet)

    // Return
    dataGet._2.asInstanceOf[T]
  }

  def put(key: Long, valueSize: Long, value: Ptr[Byte]): Unit = {
    // Put
    val lmdb_key = stackalloc[KVType]
    val bytes = BigInt(key).toByteArray
    val size = bytes.length
    lmdb_key._1 = size.toLong
    lmdb_key._2 = stackalloc[Byte](size)
    fill_lmdb_key(lmdb_key, bytes, size)

    val dataPut = stackalloc[KVType]
    dataPut._1 = valueSize
    dataPut._2 = value
    val return_code = mdb_put(txn, dbi, lmdb_key, dataPut, 0)
    if (return_code != 0) {
      println("Put: " + return_code)
      System.exit(1)
    }
  }

  def delete(key: Long): Unit = {
    val lmdb_key = stackalloc[KVType]
    val bytes = BigInt(key).toByteArray
    val size = bytes.length
    lmdb_key._1 = size.toLong
    lmdb_key._2 = stackalloc[Byte](size)
    fill_lmdb_key(lmdb_key, bytes, size)
    mdb_del(txn, dbi, lmdb_key, null)
  }

  def getNextKey(): Long = {
    val dataGet = stackalloc[KVType]
    val return_code = mdb_get(txn, dbiSize, key_last_id, dataGet)

    val new_last_id = if (return_code == 0) {
      // Found last_id
      val last_id = longget(dataGet._2, dataGet._1.toInt)
      last_id + 1
    } else {
      // No last_id stored
      0l
    }

    val lmdb_key = stackalloc[KVType]
    val bytes = BigInt(new_last_id).toByteArray
    val size = bytes.length
    lmdb_key._1 = size.toLong
    lmdb_key._2 = stackalloc[Byte](size)
    fill_lmdb_key(lmdb_key, bytes, size)
    
    mdb_put(txn, dbiSize, key_last_id, lmdb_key, 0)
    new_last_id
  }
}

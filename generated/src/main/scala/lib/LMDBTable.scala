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
    println("Max num dbs " + mdb_env_set_maxdbs(env_, 3))

    // Open env
    println("env open " + mdb_env_open(env_, path, 0, 664))

    env_
  }
  
  def free(): Unit = {
    mdb_env_close(env)
    zone.close()
  }

  def init_transaction(env: Ptr[Byte])(implicit z: Zone): Ptr[Byte] = {
    val txPtr = alloc[Ptr[Byte]]
    println("Txn begin " + mdb_txn_begin(env, null, 0, txPtr))
    return !txPtr
  }
  
  def init_dbi(txn: Ptr[Byte], name: CString)(implicit z: Zone): UInt = {
    val dbiPtr = alloc[UInt]
    // Only one 'table' per database -> will use multiple database, so name = null
    println("Dbi open " + mdb_dbi_open(txn, name, MDB_CREATE, dbiPtr))
    return !dbiPtr
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
    return BigInt(bytes).toInt
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

  val name = toCString(_name)(zone)

  var txnn: Ptr[Byte] = null // Work around for now, later the txn and dbi will be created and given by the query
  var dbii: UInt = null

  def cursor()(implicit z: Zone): Ptr[Byte] = {
    val cursorPtr = alloc[Ptr[Byte]]
    txnn = init_transaction(env)
    dbii = init_dbi(txnn, name)
    mdb_cursor_open(txnn, dbii, cursorPtr)
    !cursorPtr
  }

  private def cursor_get(cursor: Ptr[Byte], op: Int)(implicit z: Zone): (Long, Ptr[Byte]) = {
    val key = alloc[KVType]
    val value = alloc[KVType]
    val code = mdb_cursor_get(cursor, key, value, op)

    if (code == 0) {
      println(s"Mdb cursor ${op}: ${code}")
      (longget(key._2, key._1.toInt), value._2)
    } else {
      println(s"Mdb cursor ${op}: ${code}")
      // Free
      mdb_txn_commit(txnn)
      mdb_dbi_close(env, dbii)
      (0, null)
    }
  }

  def first(cursor: Ptr[Byte])(implicit z: Zone): (Long, Ptr[Byte]) = {
    println("First")
    cursor_get(cursor, MDB_FIRST)
  }

  def next(cursor: Ptr[Byte])(implicit z: Zone): (Long, Ptr[Byte]) = {
    cursor_get(cursor, MDB_NEXT)
  }

  def size(implicit z: Zone): Long = {
    val txn = init_transaction(env)
    val dbi = init_dbi(txn, name)

    // Get size
    val stat = alloc[struct_lmdb_stat]
    println("Stats " + mdb_stat(txn, dbi, stat))

    // Free
    mdb_txn_commit(txn)
    mdb_dbi_close(env, dbi)

    // Return
    stat._6
  }

  def get(key: Long)(implicit z: Zone): Ptr[Byte] = {
    val txn = init_transaction(env)
    val dbi = init_dbi(txn, name)

    // Get value
    val lmdb_key = get_lmdb_key(key)
    val dataGet = alloc[KVType]
    println("Get " + mdb_get(txn, dbi, lmdb_key, dataGet))

    // Free
    mdb_txn_commit(txn)
    mdb_dbi_close(env, dbi)

    // Return
    dataGet._2
  }

  def put(key: Long, valueSize: Long, value: Ptr[Byte])(implicit z: Zone): Unit = {
    val txn = init_transaction(env)
    val dbi = init_dbi(txn, name)

    // Put
    val lmdb_key = get_lmdb_key(key)
    val dataPut = alloc[KVType]
    dataPut._1 = valueSize
    dataPut._2 = value
    println("Put " + mdb_put(txn, dbi, lmdb_key, dataPut, 0))

    // Free
    mdb_txn_commit(txn)
    mdb_dbi_close(env, dbi)
  }
}
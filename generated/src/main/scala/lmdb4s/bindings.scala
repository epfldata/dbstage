package com.github.lmdb4s
 
import scala.scalanative.unsafe._
import scala.scalanative.unsigned._
 
@link("lmdb")
@extern
// TODO: generate bindings from lmdb.h (some functions are missing here)
//private[lmdb4s]
object bindings { // TODO: rename Library (to be similar to JNR)
 
  /* --- Some C types --- */
  type KVType = CStruct2[Long,Ptr[Byte]]
  type KVPtr = Ptr[KVType]
 
  type struct_lmdb_env_info = CStruct6[Ptr[Byte],CSize,CSize,CSize,UInt,UInt]
  type struct_lmdb_stat = CStruct6[UInt,UInt,CSize,CSize,CSize,CSize]
 
  /*type LmdbEnv = Ptr[Byte]
  type Transaction = Ptr[Byte]
  type DB = UInt
  type Key = KVType
  type Value = KVType*/
 
  /*def mdb_dbi_open(tx: Transaction, name: CString, flags: Int, db: Ptr[DB]): Int = extern
 
  def mdb_env_create(env: Ptr[LmdbEnv]): Int = extern
  def mdb_env_open(env: LmdbEnv, path: CString, flags: Int, mode: Int): Int = extern
 
  def mdb_get(tx: Transaction, db: DB, key: Ptr[Key], value: Ptr[Value]): Int = extern
  def mdb_put(tx: Transaction, db: DB, key: Ptr[Key], value: Ptr[Value], flags: Int): Int = extern
 
  def mdb_txn_abort(tx: Transaction): Int = extern
  def mdb_txn_begin(env: LmdbEnv, parent: Ptr[Byte], flags: Int, tx: Ptr[Transaction]): Int = extern
  def mdb_txn_commit(tx: Transaction): Int = extern
 
  def mdb_strerror(rc: CInt): CString = extern
 
  def mdb_version(major: Ptr[CInt], minor: Ptr[CInt], patch: Ptr[CInt]): CString = extern*/
 
  /* --- Bindings for Cursor functions --- */
  def mdb_cursor_close(cursor: Ptr[Byte]): Unit = extern
  def mdb_cursor_count(cursor: Ptr[Byte], countp: Ptr[Long]): Int = extern
  def mdb_cursor_del(cursor: Ptr[Byte], flags: Int): Int = extern
  def mdb_cursor_get(cursor: Ptr[Byte], k: Ptr[Byte], v: Ptr[Byte], cursorOp: Int): Int = extern
  def mdb_cursor_open(txn: Ptr[Byte], dbi: UInt, cursorPtr: Ptr[Ptr[Byte]]): Int = extern
  def mdb_cursor_put(cursor: Ptr[Byte], key: KVPtr, data: KVPtr, flags: Int): Int = extern
  def mdb_cursor_renew(txn: Ptr[Byte], cursor: Ptr[Byte]): Int = extern
 
  /* --- Bindings for Dbi functions --- */
  def mdb_dbi_close(env: Ptr[Byte], dbi: UInt): Unit = extern
  def mdb_dbi_flags(txn: Ptr[Byte], dbi: UInt, flags: Int): Int = extern
  def mdb_dbi_open(txn: Ptr[Byte], name: CString, flags: Int, dbiPtr: Ptr[UInt]): Int = extern
 
  /* --- Bindings for misc. functions --- */
  def mdb_del(txn: Ptr[Byte], dbi: UInt, key: KVPtr, data: KVPtr): Int = extern
  def mdb_drop(txn: Ptr[Byte], dbi: UInt, del: Int): Int = extern
 
  /* --- Bindings for Env functions --- */
  def mdb_env_close(env: Ptr[Byte]): Unit = extern
  def mdb_env_copy2(env: Ptr[Byte], path: CString, flags: Int): Int = extern
  def mdb_env_create(envPtr: Ptr[Ptr[Byte]]): Int = extern
  def mdb_env_get_fd(env: Ptr[Byte], fd: Ptr[Byte]): Int = extern
  def mdb_env_get_flags(env: Ptr[Byte], flags: Int): Int = extern
  def mdb_env_get_maxkeysize(env: Ptr[Byte]): Int = extern
  def mdb_env_get_maxreaders(env: Ptr[Byte], readers: Int): Int = extern
  def mdb_env_get_path(env: Ptr[Byte], path: CString): Int = extern
  def mdb_env_info(env: Ptr[Byte], info: Ptr[struct_lmdb_env_info]): Int = extern
  def mdb_env_open(env: Ptr[Byte], path: CString, flags: Int, mode: Int): Int = extern
  def mdb_env_set_flags(env: Ptr[Byte], flags: Int, onoff: Int): Int = extern
  def mdb_env_set_mapsize(env: Ptr[Byte], size: CSize): Int = extern
  def mdb_env_set_maxdbs(env: Ptr[Byte], dbs: Int): Int = extern
  def mdb_env_set_maxreaders(env: Ptr[Byte], readers: Int): Int = extern
  def mdb_env_stat(env: Ptr[Byte], stat: Ptr[struct_lmdb_stat]): Int = extern
  def mdb_env_sync(env: Ptr[Byte], f: Int): Int = extern
 
  /* --- Bindings for GET/PUT functions --- */
  def mdb_get(txn: Ptr[Byte], dbi: UInt, key: KVPtr, data: KVPtr): Int = extern
  def mdb_put(txn: Ptr[Byte], dbi: UInt, key: KVPtr, data: KVPtr, flags: Int): Int = extern
 
  /* --- Bindings for misc. functions --- */
  def mdb_reader_check(env: Ptr[Byte], dead: Int): Int = extern
  def mdb_set_compare(txn: Ptr[Byte], dbi: UInt, cb: CFuncPtr2[Ptr[Byte], Ptr[Byte], Int]): Int = extern
  def mdb_stat(txn: Ptr[Byte], dbi: UInt, stat: Ptr[struct_lmdb_stat]): Int = extern
  def mdb_strerror(rc: Int): CString = extern
 
  /* --- Bindings for Txn functions --- */
  def mdb_txn_abort(txn: Ptr[Byte]): Unit = extern
  def mdb_txn_begin(env: Ptr[Byte], parentTx: Ptr[Byte], flags: Int, txPtr: Ptr[Ptr[Byte]]): Int = extern
  def mdb_txn_commit(txn: Ptr[Byte]): Int = extern
  def mdb_txn_env(txn: Ptr[Byte]): Ptr[Byte] = extern
  def mdb_txn_id(txn: Ptr[Byte]): Long = extern
  def mdb_txn_renew(txn: Ptr[Byte]): Int = extern
  def mdb_txn_reset(txn: Ptr[Byte]): Unit = extern
 
  /* --- Binding for version function --- */
  def mdb_version(major: Ptr[Int], minor: Ptr[Int], patch: Ptr[Int]): Ptr[Byte] = extern
 
}
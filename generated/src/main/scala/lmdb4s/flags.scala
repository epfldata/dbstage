package lmdb4s

object flags {
  /* --- Flags for Dbi functions --- */
  val MDB_REVERSEKEY: Int = 0x02
  val MDB_DUPSORT: Int = 0x04
  val MDB_INTEGERKEY: Int = 0x08
  val MDB_DUPFIXED: Int = 0x10
  val MDB_INTEGERDUP: Int = 0x20
  val MDB_REVERSEDUP: Int = 0x40
  val MDB_CREATE: Int = 0x40000
}
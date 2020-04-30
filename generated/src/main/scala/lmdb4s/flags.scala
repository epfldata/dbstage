package lmdb4s

object flags {
  /* --- Environment flags --- */
  val MDB_FIXEDMAP: Int = 0x01
  val MDB_NOSUBDIR: Int = 0x4000
  val MDB_NOSYNC: Int = 0x10000
  val MDB_RDONLY: Int = 0x20000
  val MDB_NOMETASYNC: Int = 0x40000
  val MDB_WRITEMAP: Int = 0x80000
  val MDB_MAPASYNC: Int = 0x100000
  val MDB_NOTLS: Int = 0x200000
  val MDB_NOLOCK: Int = 0x400000
  val MDB_NORDAHEAD: Int = 0x800000
  val MDB_NOMEMINIT: Int = 0x1000000

  /* --- Database flags --- */
  val MDB_REVERSEKEY: Int = 0x02
  val MDB_DUPSORT: Int = 0x04
  val MDB_INTEGERKEY: Int = 0x08
  val MDB_DUPFIXED: Int = 0x10
  val MDB_INTEGERDUP: Int = 0x20
  val MDB_REVERSEDUP: Int = 0x40
  val MDB_CREATE: Int = 0x40000
}
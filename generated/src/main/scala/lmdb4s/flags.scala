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

  /* --- Cursor get operations --- */
  val MDB_FIRST: Int = 0 // Position at first key/data item
  val MDB_FIRST_DUP: Int = 1 // Position at first data item of current key. Only for MDB_DUPSORT
  val MDB_GET_BOTH: Int = 2 // Position at key/data pair. Only for MDB_DUPSORT
  val MDB_GET_BOTH_RANGE: Int = 3 // position at key, nearest data. Only for MDB_DUPSORT
  val MDB_GET_CURRENT: Int = 4 // Return key/data at current cursor position
  val MDB_GET_MULTIPLE: Int = 5 // Return key and up to a page of duplicate data items from current cursor position. Move cursor to prepare for MDB_NEXT_MULTIPLE. Only for MDB_DUPFIXED
  val MDB_LAST: Int = 6 // Position at last key/data item
  val MDB_LAST_DUP: Int = 7 // Position at last data item of current key. Only for MDB_DUPSORT
  val MDB_NEXT: Int = 8 // Position at next data item
  val MDB_NEXT_DUP: Int = 9 // Position at next data item of current key. Only for MDB_DUPSORT
  val MDB_NEXT_MULTIPLE: Int = 10 // Return key and up to a page of duplicate data items from next cursor position. Move cursor to prepare for MDB_NEXT_MULTIPLE. Only for MDB_DUPFIXED
  val MDB_NEXT_NODUP: Int = 11 // Position at first data item of next key
  val MDB_PREV: Int = 12 // Position at previous data item
  val MDB_PREV_DUP: Int = 13 // Position at previous data item of current key. Only for MDB_DUPSORT
  val MDB_PREV_NODUP: Int = 14 // Position at last data item of previous key
  val MDB_SET: Int = 15 // Position at specified key
  val MDB_SET_KEY: Int = 16 // Position at specified key, return key + data
  val MDB_SET_RANGE: Int = 17 // Position at first key greater than or equal to specified key
}

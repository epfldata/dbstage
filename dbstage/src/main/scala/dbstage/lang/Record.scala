package dbstage.lang

abstract class Record

abstract class KeyedRecord extends Record {
  val key: Long
}

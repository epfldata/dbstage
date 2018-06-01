package dbstage
package db

import squid.utils._
import Embedding.Predef._
import java.util.Scanner

abstract class TableBase {
  
  type Ctx // global table context; typically references to the data structures used to store the table's data
  type RowCtx
  type Key // TODOne as type param
  implicit val Key: CodeType[Key]
  
  //def readInto(k: Key): Code[String => Unit, RowCtx]
  def readInto: Code[(Scanner,Key) => Unit, Ctx]
  
  
}
abstract class Table[K:CodeType] extends TableBase {
  type Key = K
  val Key = codeTypeOf[K]
}
//abstract class ArrayTable[T: CodeType: Serial] extends Table[Int] {
class ArrayTable[T: CodeType: Serial] extends Table[Int] {
  val arr = Variable[Array[T]]
  type Ctx = arr.Ctx
  //type Key = Int
  //implicit val Key = codeTypeOf[Int]
  val SerialT = implicitly[Serial[T]]
  def readInto: Code[(Scanner,Key) => Unit, Ctx] =
    code"(scn: Scanner, idx: Key) => $arr(idx) = ${SerialT.reader}(scn)"
  
  override def toString = s"ArrayTable[${codeTypeOf[T].rep}]"
}
//abstract class IndexedTable[K](val underlying: TableBase) extends TableBase {
//  type Key = K
abstract class IndexedTable[K:CodeType](val underlying: TableBase) extends Table[K] {
  type Ctx = underlying.Ctx
  
}

//class ColumnStore(relations: List[Relation]) extends Table {
//}
abstract class ColumnStore[K:CodeType] extends Table[K] {
  override def toString = {
    def rec(cs: ColumnStore[K], first: Bool = false): String = cs match {
      case ColumnStoreCons(tbl, cs) => (if (first) "" else ", ") + s"$tbl${rec(cs)}"
      case ColumnStoreNil() => s""
    }
    s"ColumnStore(${rec(this, true)})"
  }
}
case class ColumnStoreCons[K:CodeType](headTable: Table[K], tailTables: ColumnStore[K]) extends ColumnStore[K] {
  type Ctx = headTable.Ctx & tailTables.Ctx
  def readInto: Code[(Scanner,Key) => Unit, Ctx] =
    code"{(scn: Scanner, key: Key) => ${headTable.readInto}(scn,key); ${tailTables.readInto}(scn,key)}"
}
//case object ColumnStoreNil extends TableBase {
case class ColumnStoreNil[K:CodeType]() extends ColumnStore {
  type Ctx = Any
  //type Key = Unit
  def readInto: Code[(Scanner,Key) => Unit, Ctx] = code"(_:Scanner,_:Key) => ()"
}



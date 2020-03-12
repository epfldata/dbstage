package dbstage.lang

import scala.collection.mutable

class Table[T] {
  val data = mutable.ArrayBuffer.empty[T]
  def iterator: Iterator[T] = data.iterator
  def size: Int = data.size
  def view: TableView[T] = new TableScan(this)
  def insert(t: T): TableView[T] = new Insert(this, t)
}
abstract class TableView[T] { view =>
  protected def iterator: Iterator[T]
  def size: Int
  
  def filter(pred: T => Boolean): TableView[T] =
    new Filter(pred)
  
  class Filter(pred: T => Boolean) extends TableView[T] {
    protected def iterator: Iterator[T] = view.iterator.filter(pred)
    def size: Int = iterator.size
  }
  def map(f: T => T): TableView[T] = new Map(f)

  class Map(f: T => T) extends TableView[T] {
    protected def iterator: Iterator[T] = view.iterator.map(f)
    def size: Int = view.size
  }
}
class Insert[T](tbl: Table[T], t: T) extends TableView[T] {
  protected def iterator: Iterator[T] = tbl.iterator ++ Iterator(t)
  def size: Int = iterator.size
}

class TableScan[T](tbl: Table[T]) extends TableView[T] {
  protected def iterator: Iterator[T] = tbl.iterator
  def size: Int = tbl.size
}

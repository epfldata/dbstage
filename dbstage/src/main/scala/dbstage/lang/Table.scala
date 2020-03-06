package dbstage.lang

import scala.collection.mutable

class Table[T] {
  val data = mutable.ArrayBuffer.empty[T]
  def iterator: Iterator[T] = data.iterator
  def size: Int = data.size
  def view: TableView[T] = new TableScan(this)
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
}
class TableScan[T](tbl: Table[T]) extends TableView[T] {
  protected def iterator: Iterator[T] = tbl.iterator
  def size: Int = tbl.size
}

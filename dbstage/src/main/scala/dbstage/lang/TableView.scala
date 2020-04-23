package dbstage.lang

import scala.collection.mutable

object TableView {
  def all[T]: TableView[T] = ???
}

abstract class TableView[T] { view =>
  def size: Int = ???

  def filter(pred: T => Boolean): TableView[T] =
    new Filter(pred)
  
  class Filter(pred: T => Boolean) extends TableView[T]
/*
  def insert(el: T): TableView[T] =
    new Insert(el)

  class Insert(el: T) extends TableView[T] {
    protected def iterator: Iterator[T] = view.iterator.ad
  }*/
  
  def map(f: T => T): TableView[T] = new Map(f)

  class Map(f: T => T) extends TableView[T]
}

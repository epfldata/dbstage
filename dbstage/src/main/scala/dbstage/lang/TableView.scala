package dbstage.lang

import scala.collection.mutable

object TableView {
  def all[T]: TableView[T] = ???
  def delete[T](el: T): Unit = ???
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
  
  def map[R](f: T => R): TableView[R] = new Map(f)

  class Map[R](f: T => R) extends TableView[R]

  def join[R](other: TableView[R]): TableView[(T, R)] = new Join(other)

  class Join[R](other: TableView[R]) extends TableView[(T, R)]
}

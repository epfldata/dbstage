package dbstage.lang

import scala.collection.mutable
import scala.annotation.compileTimeOnly

object Table {

  @compileTimeOnly("This DSL method cannot be called at runtime.")
  def delete[T](el: T): Unit = ???

}

object TableView {
  def all[T]: TableView[T] = ???
}

abstract class TableView[T] { view =>
  def size: Int = ???
  def aggregate[Res](init: Res, acc: (T, Res) => Res): Res = ???
  def forEach(f: T => Unit): Unit = ???

  def filter(pred: T => Boolean): TableView[T] =
    new Filter(pred)
  class Filter(pred: T => Boolean) extends TableView[T]

  def map[R](f: T => R): TableView[R] = new Map(f)
  class Map[R](f: T => R) extends TableView[R]
  
  def flatMap[R](f: T => TableView[R]): TableView[R] = new FlatMap(f)
  class FlatMap[R](f: T => TableView[R]) extends TableView[R]

  def join[R](other: TableView[R]): TableView[(T, R)] = new Join(other)
  class Join[R](other: TableView[R]) extends TableView[(T, R)]
}

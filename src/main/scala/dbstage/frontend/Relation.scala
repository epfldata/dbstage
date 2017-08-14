package dbstage
package frontend

import scala.collection.mutable
import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}
import runtime._

class Relation {
  protected val curColumns = mutable.ArrayBuffer[Column]()
  
  val table = Lazy {
    val (keys,values) = columns.partition(_.isPrimary)
    //if (keys.isEmpty) Table(values)
    ////else PrimaryIndexedTable(keys, values)
    //else UniqueIndexedTable(keys, values, columns.map(_.name))
    if (keys.nonEmpty) UniqueIndexedTable(keys, values, columns.map(_.name))
    else {
      val (foreignKeys,values) = columns.partition(_.isForeignKey)
      if (foreignKeys.nonEmpty) GeneralIndexedTable(foreignKeys, values, columns.map(_.name))
      else Table(values)
    }
  }
  
  def columns: Seq[Column] = curColumns
  
  /** Partitions the columns into groups of columns that frequently accessed together and are thus to be stored in the same array. */
  //def columnsPartitioning: Seq[Row] = {
  //  //???
  //  // TODO
  //  columns map (c => Row(c))
  //}
  
  //abstract class Column(name: String) extends Field(name) {
  abstract case class Column(override val name: String, val isPrimary: Bool, val foreignKey: Option[Relation # Column]) extends Field(name) {
    assert(!table.computed)
    curColumns += this
    def isForeignKey = foreignKey.nonEmpty
  }
  object Column {
    //def apply[T](name: String, foreignIn: Table = null) = {
    def apply[T0:IRType:Serial](name: String, primary: Bool = false, foreign: Relation # Column = null) = {
      new Column(name,primary,Option(foreign)) {
        //val name = 
        type T = T0
        val IRTypeT: IRType[T] = implicitly
        val SerialT: Serial[T] = implicitly
      }
    }
  }
  
  def loadDataFromFile(fileName: String) = {
    log(s"Loading data from file $fileName into table $this")
    //val file = scala.util.nio.File(file)
    val src = scala.io.Source.fromFile(fileName)
    //data = Some(sm.fromCSV(this, src))
    
    val tbl = table.value
    
    tbl.loadData(src.getLines())
    
  }
  
  //def select(cols: Column*): TableQuery = ???
  
  //override def toString: String = s""
}

trait TableQuery {
  type ValueQuery = Code[Bool]
  
  //val row: Row
  
  def project(fields: Field*): TableQuery = ???
  def filter(pred: ValueQuery): TableQuery = ???
  //def groupBy(sch: Schema): TableQuery = GroupBy(this, sch)
  
  
}

// Can make freestanding fields not associated with a table
abstract class Field(val name: String) {
  type T
  implicit val IRTypeT: IRType[T]
  implicit val SerialT: Serial[T]
}









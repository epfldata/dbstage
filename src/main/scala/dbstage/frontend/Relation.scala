package dbstage
package frontend

import scala.collection.mutable
import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}
import runtime._

class Relation {
  protected val curColumns = mutable.ArrayBuffer[Column]()
  var data: Option[DataRep] = None
  
  val table = Lazy {
    val (keys,values) = columns.partition(_.isPrimary)
    if (keys.isEmpty) Table(values)
    //else PrimaryIndexedTable(keys, values)
    else IndexedTable(keys, values, columns.map(_.name))
  }
  
  def columns: Seq[Column] = curColumns
  
  /** Partitions the columns into groups of columns that frequently accessed together and are thus to be stored in the same array. */
  //def columnsPartitioning: Seq[Row] = {
  //  //???
  //  // TODO
  //  columns map (c => Row(c))
  //}
  
  //abstract class Column(name: String) extends Field(name) {
  abstract case class Column(override val name: String, val isPrimary: Bool) extends Field(name) {
    //val name: String
    //type T
    //implicit val IRTypeT: IRType[T]
    //implicit val SerialT: Serial[T]
    assert(!table.computed)
    curColumns += this
  }
  object Column {
    //def apply[T](name: String, foreignIn: Table = null) = {
    def apply[T0:IRType:Serial](name: String, primary: Bool = false, foreign: Relation # Column = null) = {
      new Column(name,primary) {
        //val name = 
        type T = T0
        val IRTypeT: IRType[T] = implicitly
        val SerialT: Serial[T] = implicitly
      }
    }
  }
  
  def loadDataFromFile(fileName: String, sm: StorageManager = InMemoryStorageManager) = {
    log(s"Loading data from file $fileName into table $this")
    //val file = scala.util.nio.File(file)
    val src = scala.io.Source.fromFile(fileName)
    //data = Some(sm.fromCSV(this, src))
    
    
    //println(Table(columns))
    //val tbl = Table(columns)
    val tbl = table.value
    //println(tbl.parse)
    //println(tbl.load)
    
    tbl.loadData(src.getLines())
    
    //println(tbl.show)
    
    /*
    val sep = '|'
    val arr: Code[Array[String]] = ir"arr?:Array[String]"
    val parser = tbl.parse(columns.zipWithIndex.map(ci => ci._1.name -> ir"$arr(${Const(ci._2)})").toMap)
    val pgrm = for {
      loader <- tbl.load
    } yield ir"""(ite: Iterator[String]) =>
      while (ite.hasNext) {
        val str = ite.next
        val arr = str.split(${Const(sep)})
        //println(">"+arr.toList)
        ${loader}(${parser:IR[tbl.Row,{val arr:Array[String]}]})
      }
    """
    
    println(s"Generated Program: $pgrm")
    
    pgrm.compile()(src.getLines())
    
    //println(tbl.buffer)
    println(tbl.show)
    */
    
    
  }
  
  def select(cols: Column*): TableQuery = ???
  
  
  //override def toString: String = s""
}

trait TableQuery {
  type ValueQuery = Code[Bool]
  
  //val row: Row
  
  def project(fields: Field*): TableQuery = ???
  def filter(pred: ValueQuery): TableQuery = ???
  //def groupBy(sch: Schema): TableQuery = GroupBy(this, sch)
  
  
}

//abstract class Row {
//  type T <: Product
//  implicit val T: IRType[T]
//  //val typs: Seq[IRType[_]]
//  val fields: Seq[Field]
//  //def parse(raw: Iterator[String]): T
//  def parse: Code[Iterator[String]] => Code[T]
//}
//object Row {
//  def apply(fs: Field*): Row = ???
//}







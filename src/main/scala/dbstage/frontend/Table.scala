package dbstage
package frontend

import scala.collection.mutable
import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}
import runtime._

class Table {
  protected val curColumns = mutable.ArrayBuffer[Column]()
  var data: Option[DataRep] = None
  
  def columns: Seq[Column] = curColumns
  
  /** Partitions the columns into groups of columns that frequently accessed together and are thus to be stored in the same array. */
  def columnsPartitioning: Seq[Row] = {
    //???
    // TODO
    columns map (c => Row(c))
  }
  
  //abstract class Column(name: String) extends Field(name) {
  abstract case class Column(override val name: String) extends Field(name) {
    //val name: String
    //type T
    //implicit val IRTypeT: IRType[T]
    //implicit val SerialT: Serial[T]
    curColumns += this
  }
  object Column {
    //def apply[T](name: String, foreignIn: Table = null) = {
    def apply[T0:IRType:Serial](name: String, primary: Bool = false, foreign: Table # Column = null) = {
      new Column(name) {
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
    
    
    println(MetaTable(columns))
    
    
    //val mtbl = new MetaTable[Int]{}
    val mtbl = new MetaTable_OLD[Int](columns)
    
    //columns.zipWithIndex.map(ci => ci._2 -> ci._1)
    
    val sep = "|"
    /*
    val pgrm =
    ir"""(ite: Iterator[String]) =>
      while (ite.hasNext) {
        val str = ite.next
        ${ (arr:Code[Array[String]]) =>
          mtbl.parse(columns.zipWithIndex.map(ci => ci._1.name -> ir"$arr(${Const(ci._2)})").toMap)
        }(str.split(${Const(sep)}))
      }
    """
    */
    
    val arr: Code[Array[String]] = ir"arr?:Array[String]"
    val pgrm = for {
      parser <- mtbl.parse(columns.zipWithIndex.map(ci => ci._1.name -> ir"$arr(${Const(ci._2)})").toMap)
    } yield ir"""(ite: Iterator[String]) =>
      while (ite.hasNext) {
        val str = ite.next
        val arr = str.split(${Const(sep)})
        ${parser:IR[Unit,{val arr:Array[String]}]}
      }
    """
    println(s"Generated Program: $pgrm")
    
    pgrm.compile()(src.getLines())
    
    println(mtbl.buffer)
    
  }
  
  def select(cols: Column*): TableQuery = ???
  
  
  //override def toString: String = s""
}

trait TableQuery {
  type ValueQuery = Code[Bool]
  
  val row: Row
  
  def project(fields: Field*): TableQuery = ???
  def filter(pred: ValueQuery): TableQuery = ???
  //def groupBy(sch: Schema): TableQuery = GroupBy(this, sch)
  
  
}

abstract class Row {
  type T <: Product
  implicit val T: IRType[T]
  //val typs: Seq[IRType[_]]
  val fields: Seq[Field]
  //def parse(raw: Iterator[String]): T
  def parse: Code[Iterator[String]] => Code[T]
}
object Row {
  def apply(fs: Field*): Row = ???
}







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
  //}
  
  //abstract class Column(name: String) extends Field(name) {
  abstract class Column(override val name: String, val isPrimary: Bool, val foreignKey: Option[Relation # Column]) extends Field(name) {
  //abstract case class Column(override val name: String)(val isPrimary: Bool, val foreignKey: Option[Relation # Column]) extends Field(name) { // using a second param list so these additional params do not appear in the toString...
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
    val src = scala.io.Source.fromFile(fileName)
    
    val tbl = table.value
    
    tbl.loadData(src.getLines())
    
  }
  
  //def select(cols: Column*): TableQuery = ???
  
  //override def toString: String = s""
}

trait TableQuery[+T] {
  //type ValueQuery = Code[Bool]
  
  //def project(fields: Field*): TableQuery = ???
  //def filter(pred: ValueQuery): TableQuery = ???
  //def groupBy(sch: Schema): TableQuery = GroupBy(this, sch)
  
  
}

trait QueryBuilder { // relarion extends this?
  
  def select(c0: Field): TableQuery[c0.T] = ???
  def select(c0: Field, c1: Field): TableQuery[(c0.T,c1.T)] = ???
  def select(c0: Field, c1: Field, c2: Field): TableQuery[(c0.T,c1.T,c2.T)] = ???
  
}

// Can make freestanding fields not associated with a table
abstract class Field(val name: String) { thisField => // TODO extend Embedding.IR ?
  type T
  implicit val IRTypeT: IRType[T]
  implicit val SerialT: Serial[T]
  //def toCode: Code[T] = ir"field[T](${Const(name)},None)"
  def toCode: Code[T] = ir"field[T](${Const(name)})"
  def in (that: dbstage.query.Query): Field{type T = thisField.T} = ??? // TODO query id to remove ambiguities (eg in self-joins)
  override def toString = s"Field[${IRTypeT.rep}]($name)"
}
object Field {
  def apply[S:IRType:Serial](name: String) = new Field(name) {
    type T = S
    val IRTypeT: IRType[T] = implicitly
    val SerialT: Serial[T] = implicitly
  }
  // TODO distinguish Field/SerialField
  
  // FIXME: Rwr have problems with unapply referring to the <unapply-selector>.Typ
  /*
  //def unapply[T:Serial](x: Code[T]): Option[Field] = x match {
  //def unapply[S:IRType](x: IR[S,_]): Option[Field{type T = S}] = x match {
  //def unapply[S](x: IR[S,_]): Option[Field{type T = S}] = {
  //def unapply(x: IR[Any,_]): Option[Field{type T = x.Typ}] = {
  //def unapply(x: IR[_,_]): Option[Field{type T = x.Typ}] = {
  def unapply[S](x: IR[S,_]): Option[Field{type T = x.Typ}] = {
    //implicit val tp: IRType[S] = x.typ
    //implicit val tp = x.typ
    //implicit val tp: IRType[x.Typ] = x.typ
    implicit val tp: IRType[x.Typ] = Embedding.irTyp(x)
    x match {
      //case ir"field[$tp](${Const(name)})" => 
      //case ir"field[S](${Const(name)})" =>
      //  //implicit val _ = new Serial[tp.Typ](_ => ir"???", _ => ir"???")
      //  //Some(Field[tp.Typ](name))
      //  implicit val _ = new Serial[S](_ => ir"???", _ => ir"???")
      //  Some(Field[S](name))
      case ir"field[$$tp](${Const(name)})" =>
        implicit val _ = new Serial[x.Typ](_ => ir"???", _ => ir"???")
        Some(Field[x.Typ](name))
      case _ => None
    }
  }
  */
  def unapply(x: Code[_]): Option[Field] = x match {
    case ir"field[$tp](${Const(name)})" =>
      implicit val _ = new Serial[tp.Typ](_ => ir"???", _ => ir"???")
      Some(Field[tp.Typ](name))
    case _ => None
  }
}
case class CodeField[S:IRType:Serial](override val name: String, override val toCode: Code[S]) extends Field(name) {
  type T = S
  val IRTypeT: IRType[T] = implicitly
  val SerialT: Serial[T] = implicitly
}









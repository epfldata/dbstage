package dbstage
package frontend

import scala.collection.mutable
import squid.utils._
import Embedding.Predef._
import runtime._

/*
    TODO: implement hybrid row-store/col-store
      Partition the columns into groups of columns that are frequently accessed together and should thus be stored in the same array.
*/
class Relation {
  protected val curColumns = mutable.ArrayBuffer[Column]()
  
  var indexByKeys = true
  var columnStore = false
  
  val table = Lazy {
    val (keys,values) = columns.partition(_.isPrimary)
    if (keys.nonEmpty && indexByKeys) UniqueIndexedTable(keys, values, columns.map(_.name))
    else {
      val (foreignKeys,values) = columns.partition(_.isForeignKey)
      if (foreignKeys.nonEmpty && indexByKeys) GeneralIndexedTable(foreignKeys, values, columns.map(_.name))
      else if (columnStore) ColumnStore(values)
      else Table(values)
    }
  }
  
  def columns: Seq[Column] = curColumns
  
  // The Column/Field/FieldRef organization is a bit messy; could probably do things in a clearer, less confusing way
  abstract class Column(override val name: String, val isPrimary: Bool, val foreignKey: Option[Relation # Column]) extends Field(name) {
    assert(!table.computed, "Cannot add a column after the relation has started being used! Make a new relation first.")
    curColumns += this
    def isForeignKey = foreignKey.nonEmpty
  }
  object Column {
    def apply[T0:IRType:Serial](name: String, primary: Bool = false, foreign: Relation # Column = null) = {
      new Column(name,primary,Option(foreign)) {
        type T = T0
        val IRTypeT: IRType[T] = implicitly
        val SerialT: Serial[T] = implicitly
      }
    }
  }
  
  def loadDataFromFile(fileName: String, compileCode: Bool = true) = {
    log(s"Loading data from file $fileName into table $this")
    val src = scala.io.Source.fromFile(fileName)
    
    val tbl = table.value
    
    // Loading data with `tbl.loadData` uses specialized runtime-compiled code
    if (compileCode) tbl.loadData(src.getLines(), '|')
    // `tbl.mkDataLoader` currently uses the same code but interpreted; it might be more efficient to just use generic code!
    else tbl.mkDataLoader('|').run(src.getLines())
    
  }
  
}

// Freestanding fields not associated with a table
// Note: the `Field` class could extend Embedding.IR so we wouldn't need implicit conversions!
abstract class Field(override val name: String, in: Option[Int] = None) extends FieldRef(name,in) { thisField =>
  implicit val SerialT: Serial[T]
  def toCode: Code[T] = in.fold(ir"field[T](${Const(name)})") { q => ir"fieldIn[T](${Const(name)},${Const(q)})" }
  def withId (id: Int): Field = Field[T](name,Some(id))
}
object Field {
  def apply[S:IRType:Serial](name: String, in: Option[Int] = None) = new Field(name,in) {
    type T = S
    val IRTypeT: IRType[T] = implicitly
    val SerialT: Serial[T] = implicitly
  }
  // TODO distinguish Field/SerialField
}

abstract class FieldRef(val name: String, val id: Option[Int]) {
  type T
  implicit val IRTypeT: IRType[T]
  override def toString = s"Field[${IRTypeT.rep}]($name)${id.fold(""){ q => s" in $q"}}"
  def conformsTo(f: FieldRef) = f.name == name && id.forall(_ => id == f.id)
}
object FieldRef {
  def apply[S:IRType](name: String, id: Option[Int] = None) = new FieldRef(name,id) {
    type T = S
    val IRTypeT: IRType[T] = implicitly
  }
  def unapply(x: Code[_]): Option[FieldRef] = x match {
    // TODO use irreftable Const xtors...
    case ir"field[$tp](${Const(name)})" =>
      Some(FieldRef[tp.Typ](name,None))
    case ir"fieldIn[$tp](${Const(name)}, ${Const(id)})" =>
      Some(FieldRef[tp.Typ](name,Some(id)))
    case _ => None
  }
}
case class CodeField[S:IRType:Serial](override val name: String, override val toCode: Code[S]) extends Field(name) {
  type T = S
  val IRTypeT: IRType[T] = implicitly
  val SerialT: Serial[T] = implicitly
}









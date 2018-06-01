package dbstage
package db

import java.util.Scanner

import squid.utils._
import Embedding.Predef._

case class Database(relations: Relation*) {
  
  //class Instance {
  //  // store a type repr for the actual database table repr
  //}
  
  def instance(implicit config: DatabaseConfig): DatabaseInstance =
    //???
    new DatabaseInstance(this)
  
  override def toString = s"Database(${relations.mkString(", ")})"
}

//case class Relation(columns: String -> CodeType[_] *)
case class Relation(name: String, tableRepr: Option[TableBase] = None)(val columns: Column*) {
  override def toString = s"$name(${columns.mkString(", ")})"
}

case class DatabaseConfig(
  useHashMaps: Bool = true,
) {
  // here to override if needed
  def mkTable(r: Relation): TableBase = r.tableRepr getOrElse mkColumnStore(r.columns.toList.map(mkArrayTable(_)))
  def mkArrayTable(c: Column): ArrayTable[c.Typ] = {
    implicit val ct = c.Typ
    //implicit val cs = new Serial[c.Typ] {
    //  def reader = code"???"
    //}
    implicit val cs = mkSerial[c.Typ].getOrElse(throw new Exception(s"could not find serializer for: ${c.Typ}"))
    new ArrayTable[c.Typ]
  }
  def mkColumnStore[K:CodeType](cs: List[Table[K]]): ColumnStore[K] = cs match {
    case c :: cs => ColumnStoreCons(c, mkColumnStore(cs))
    case Nil => ColumnStoreNil()
  }
  def mkSerial[T:CodeType]: Option[Serial[T]] = (code"Set.empty[T]".erase |>? {
    case code"$_:Set[Int]" => Serial(code"(_:Scanner).nextInt")
    //case code"$_:Set[String]" => Serial(code"(_:Scanner).next") // FIXME fix ModularEmbedding
    case code"$_:Set[String]" => Serial(code"Serial.nextString")
    case code"$_:Set[Bool]" => Serial(code"(_:Scanner).nextBoolean")
  }).asInstanceOf[Option[Serial[T]]]
}


// store a type repr for the actual database table repr
// TODO rename... (to DatabaseImpl or DatabaseRepr?) and have a separate structure for storing actual runtime data
class DatabaseInstance(db: Database)(implicit config: DatabaseConfig) {
  
  val tables = db.relations.map(r => r -> config.mkTable(r))
  
  override def toString = s"Instance($db) where${tables.map{case r->t => s"\n\t${r.name} = $t"}.mkString}"
  
}

abstract class Column(name: String, primary: Bool) {
  type Typ
  implicit val Typ: CodeType[Typ]
  
  override def toString = s"[$name: ${Typ.rep}]"
  
}
object Column {
  def apply[T: CodeType](name: String, primary: Bool = false): Column = new Column(name, primary) {
    type Typ = T
    val Typ = codeTypeOf[T]
  }
}

trait Serial[T] {
  def reader: ClosedCode[Scanner => T]
}
object Serial {
  def apply[T](r: ClosedCode[Scanner => T]): Serial[T] = new Serial[T] { def reader = r }
  def nextString: Scanner => String = _.next() // helper because of bug in ModularEmbedding – see in mkSerial
}




package dbstage2

import squid.utils._
import Embedding.Predef._


//class Schema(val cols: ColumnAPI*)
case class Schema(columns: Column*) {
  {
    val dups = columns.groupBy(_.name).filter(_._2.size > 1)
    assert(dups isEmpty, s"Duplicated column names: ${dups.values.toSet}") // TODO better error reporting
  }
  val column = columns map (c => c.name -> c) toMap
  lazy 
  val names = column.keySet
  def ++ (that: Schema) = Schema(columns ++ that.columns : _*)
  def U (that: Schema) = Schema(columns ++ that.columns.filterNot(names contains _.name) : _*)
  
  override def toString: String = columns mkString ("{","","}")
}

trait Column {
  val name: String
  //type T
  type Typ
  type T = Typ
  implicit val IRTypeT: IRType[T]
  implicit val SerialT: Serial[T]
}
//case class ColumnOf[T0:IRType:Serial](name: String) extends IR(ir"runtime.GenericOps.col[${implicitly[IRType[T]]}]") with Column {
case class ColumnOf[T0:IRType:Serial](name: String) extends IR[T0,Any] with Column {
  //type T = T0
  type Typ = T0
  val IRTypeT: IRType[T] = implicitly
  val SerialT: Serial[T] = implicitly
  //val rep = (ir"runtime.GenericOps.col[${implicitly[IRType[T0]]}](${Const(name)})")
  val rep = ir"runtime.GenericOps.col[${IRTypeT}](${Const(name)})".rep
  def rename(newName: String): ColumnOf[T] = ???
  def in (r: Relation) = this // TODO
}

////class Column[T:IRType:Serial](name: String)
////trait ColumnAPI {
//abstract class Column { colSelf =>
//  val name: String
//  type T
//  implicit val IRTypeT: IRType[T]
//  implicit val SerialT: Serial[T]
//  
//  val primary_? : Bool
//  val foreignIn: Option[Schema]
//  
//  def primary = copy(primary = false)
//  def foreignIn(sch: Schema) = copy(foreign = Some(sch)) // TODO should the arg really be a schema? 
//  //def copy(primary: Bool = primary_?): Column.Of[T] = new Column {
//  //  type T = colSelf.T
//  //  val IRTypeT: IRType[T] = colSelf.IRTypeT
//  //  val SerialT: Serial[T] = colSelf.SerialT
//  //  val primary_? : Bool = primary
//  //}
//  def copy(primary: Bool = primary_?, foreign: Option[Schema] = None): Column.Of[T] = Column[T](name, primary, foreign)
//  
//  override def toString: String = s"[$name:${IRTypeT.rep}]"
//}
//object Column {
//  def apply[T0:IRType:Serial](name0: String, primary0: Bool = false, foreign0: Option[Schema] = None) = new Column {
//    val name: String = name0
//    type T = T0
//    val IRTypeT: IRType[T] = implicitly
//    val SerialT: Serial[T] = implicitly
//    val primary_? : Bool = primary0
//    val foreignIn = foreign0
//    foreignIn foreach { sch =>
//      assert(sch.names(name), s"Foreign key $name is not in schema $sch")
//      //assert(sch.column(name).primary_?) // not necessary
//    }
//  }
//  type Of[A] = Column { type T = A }
//}
////case class Column[T0:IRType:Serial](name: String) extends ColumnAPI {
////  type T = T0
////  val IRTypeT: IRType[T] = implicitly
////  val SerialT: Serial[T] = implicitly
////}

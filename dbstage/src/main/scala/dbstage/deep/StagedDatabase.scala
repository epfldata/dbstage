package dbstage.deep

import dbstage.lang.{Table, TableView}

import scala.language.implicitConversions
import scala.language.existentials

import scala.collection.mutable
import sourcecode.Name
import IR.Predef._
import IR.Quasicodes._
import IR.TopLevel._
import java.lang.reflect.Method

/** This class deals with representing staged databases and their queries,
 * and can compile them to lower level code. */
class StagedDatabase(implicit name: Name)
  extends QueryLifter
     with DatabaseCompiler
     with QueryCompiler
{ db =>
  
  val dbName = name.value
  
  /** Ctx represents the scope of this database;
   * it will be used as the context type for queries that make references
   * to this database's tables. */
  type Ctx
  
  /** Ctx is a phantom type that stands for the types of several
   * dynamically-created variables, which we pass through this function
   * so they acquire the right type. */
  def adaptVariable[T](v: Variable[T]): Variable[T] { type Ctx = db.Ctx } =
    v.asInstanceOf[Variable[T]{ type Ctx = db.Ctx }]
  
  protected val knownClasses = mutable.Set.empty[Clasz[_]]
  protected val knownMethods = mutable.Map.empty[IR.MtdSymbol, ClassMethod]
  protected val knownQueries = mutable.Set.empty[Query[_]]
  protected val knownFieldGetters = mutable.Map.empty[IR.MtdSymbol, ClassGetter]
  protected val knownFieldSetters = mutable.Map.empty[IR.MtdSymbol, ClassSetter]
  
  protected val tablesMapping = mutable.Map.empty[IR.Rep, TableRep[_]]
  def getTable[T](cde: Code[Table[T], _]): Option[TableRep[T]] =
    tablesMapping.get(cde.rep).asInstanceOf[Option[TableRep[T]]]
  
  /** The representation of a table that lives in this staged database. */
  protected class TableRep[T0: CodeType](cls: Clasz[T0])(implicit name: Name) {
    type T = T0
    val T = codeTypeOf[T]
    knownClasses += cls
    cls.methods.foreach { mtd =>
      knownMethods += mtd.symbol ->
        ClassMethod(cls, mtd.symbol, mtd.tparams, mtd.vparamss, mtd.body)
    }
    val variable = adaptVariable(Variable[Table[T]])
    tablesMapping += variable.toCode.rep -> this
    cls.fields.foreach { field =>
      val ind = cls.fields.indexOf(field) + 1
    
      // Getter
      val body = s"${cls.self.toCode.showScala}._${ind}"
      knownFieldGetters += field.get ->
        ClassGetter(cls, field.symbol, ind, field.A.rep)
        
      // Setter
      val setterOpt = field.set
      if (setterOpt.isDefined) {
        val setter = setterOpt.get

        knownFieldSetters += setter ->
          ClassSetter(cls, setter, ind)
      }
    }
    
    // Helpers for the generated code:
    val variableInGeneratedCode = adaptVariable(Variable[mutable.ArrayBuffer[T]])
    def getSize: Code[Int, Ctx] =
      code{ $(variableInGeneratedCode).size }.unsafe_asClosedCode // FIXME scope // What does it mean?
    def getAt: Code[Int => T, Ctx] =
      code{ i: Int => $(variableInGeneratedCode)(i) }.unsafe_asClosedCode // FIXME scope
    def append: Code[T => Unit, Ctx] =
      code{ t: T => $(variableInGeneratedCode).append(t) }.unsafe_asClosedCode // FIXME scope
  }
  
  /** The representation of a query expressed in this staged database. */
  protected class Query[T: CodeType](val name: String, val cde: Code[T, Ctx]) {
    knownQueries += this
    lazy val rep = liftQuery(cde)
  }
  def query[T: CodeType](cde: Code[T, Ctx])(implicit name: Name) =
    new Query(name.value, cde)
  
  /** This is needed in order to let users insert table references
   * with syntax `... $(myDB.someTableRep) ...` */
  implicit def toCode[T](tbl: TableRep[T]): Code[Table[T], Ctx] =
    tbl.variable.toCode
  
}

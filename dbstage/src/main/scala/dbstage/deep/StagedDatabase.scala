package dbstage.deep

import dbstage.lang.{Record, TableView, Str, LMDBTable, Ptr}
import dbstage.lang.LMDBTable._

import scala.language.implicitConversions
import scala.language.existentials

import scala.collection.mutable
import scala.collection.immutable
import sourcecode.Name
import IR.Predef._
import IR.Quasicodes._
import IR.TopLevel._
import java.lang.reflect.Method
import squid.utils.meta.RuntimeUniverseHelpers.sru.Symbol

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
  
  protected val knownClasses = mutable.Map.empty[Symbol, TableRep[_]]
  protected val knownMethods = mutable.Map.empty[IR.MtdSymbol, ClassMethod]
  protected val knownQueries = mutable.Set.empty[Query[_]]
  protected val knownConstructors = mutable.Map.empty[IR.MtdSymbol, ClassConstructor]
  protected val knownFieldGetters = mutable.Map.empty[IR.MtdSymbol, ClassGetter]
  protected val knownFieldSetters = mutable.Map.empty[IR.MtdSymbol, ClassSetter]
  protected val knownDeleters = mutable.Map.empty[Symbol, ClassDeleter]

  protected var openDbis = code{}
  protected var closeDbis = code{}

  protected val toCString = Variable[String => CString]("toCString")
  register(Str.reflect(IR))

  def register[T0 <: Record: CodeType](cls: Clasz[T0])(implicit name: Name): Unit = {
    // Table
    val tableRep = new TableRep(cls)
    knownClasses += cls.C.rep.tpe.typeSymbol -> tableRep
    knownDeleters += cls.C.rep.tpe.typeSymbol -> new ClassDeleter(cls)

    openDbis = code{$(openDbis); $(tableRep.getDbi)}.unsafe_asClosedCode
    closeDbis = code{$(closeDbis); $(tableRep.closeDbi)}.unsafe_asClosedCode

    cls.methods.foreach { mtd =>
      knownMethods += mtd.symbol ->
        ClassMethod(cls, mtd.symbol, mtd.tparams, mtd.vparamss, mtd.body, mtd.A.rep)
    }

    // Getters and setters
    cls.fields.foreach { field =>
      // Getter
      knownFieldGetters += field.get ->
        ClassGetter(cls, field.symbol, field.name, field.A.rep)
        
      // Setter
      val setterOpt = field.set
      if (setterOpt.isDefined) {
        val setter = setterOpt.get

        knownFieldSetters += setter ->
          ClassSetter(cls, field.symbol, field.name, field.A.rep)
      }
    }

    // Constructor
    knownConstructors += cls.constructor.symbol ->
      ClassConstructor(cls, cls.constructor.symbol, cls.constructor.vparamss.head)
  }
  
  /** The representation of a table that lives in this staged database. */
  protected class TableRep[T0: CodeType](val cls: Clasz[T0])(implicit name: Name) {
    type T = T0
    val T = codeTypeOf[T]

    // Helpers for the generated code:
    val variable = adaptVariable(Variable[LMDBTable[T]](s"${cls.name}_table"))

    /** `logicalFields` represents the "real fields"",
     * ignoring potential user-defined key fields, stored in `keyField`. */
    lazy val (keyField, logicalFields) = {
      val (kfs, lfs) = cls.fields.partition(_.name == "key ")
      if (kfs.size > 1) throw new IllegalArgumentException("Several key fields")
      val kfo = kfs.headOption
      kfo.foreach { f =>
        require(f.A =:= codeTypeOf[Long], s"Key field with wrong type: ${f.A}")
        require(f.init.isEmpty, "Key field cannot have an initial value")
      }
      require(lfs.nonEmpty,
        // [LP] ^ I put that here because the stringifier is bugged with empty fields
        "A lifted class cannot have no data fields")
      (kfo, lfs)
    }

    // Getters and putters LMDB
    val getter = Variable[Long => T](s"get_${cls.name}")
    val putter = Variable[T => Unit](s"put_${cls.name}")

    def getDbi: Code[Unit, Ctx] =
      code{ $(variable).dbiOpen() }.unsafe_asClosedCode
    def getCursor: Code[Cursor, Ctx] =
      code{ $(variable).cursorOpen() }.unsafe_asClosedCode

    def closeDbi: Code[Unit, Ctx] =
      code{ $(variable).dbiClose() }.unsafe_asClosedCode
    def closeCursor: Code[Cursor => Unit, Ctx] =
      code{ cursor: Cursor => $(variable).cursorClose(cursor) }.unsafe_asClosedCode

    def getFirst: Code[Cursor => T, Ctx] = 
      code{ cursor: Cursor => $(variable).first(cursor) }.unsafe_asClosedCode
    def getNext: Code[Cursor => T, Ctx] = 
      code{ cursor: Cursor => $(variable).next(cursor) }.unsafe_asClosedCode
    def getSize: Code[Int, Ctx] =
      code{ $(variable).size }.unsafe_asClosedCode // FIXME scope // What does it mean?
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
  implicit def toCode[T](tbl: TableRep[T]): Code[LMDBTable[T], Ctx] =
    tbl.variable.toCode
  
}

package dbstage.deep

import dbstage.lang.{TableView, Str, LMDBTable}

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

  case class TableGetter(
    val owner: IR.TopLevel.Clasz[_]
  ) {
    val getter = Variable[(Any, Any) => Any](s"get_${owner.name}")
  }

  case class TablePutter(
    val owner: IR.TopLevel.Clasz[_]
  ) {
    val putter = Variable[(Any, Any) => Unit](s"put_${owner.name}")
  }
  
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
  protected val knownTableGetters = mutable.Map.empty[Symbol, TableGetter]
  protected val knownTablePutters = mutable.Map.empty[Symbol, TablePutter]
  protected val knownMethods = mutable.Map.empty[IR.MtdSymbol, ClassMethod]
  protected val knownQueries = mutable.Set.empty[Query[_]]
  protected val knownConstructors = mutable.Map.empty[IR.MtdSymbol, ClassConstructor]
  protected val knownFieldGetters = mutable.Map.empty[IR.MtdSymbol, ClassGetter]
  protected val knownFieldSetters = mutable.Map.empty[IR.MtdSymbol, ClassSetter]

  private val strCls = Str.reflect(IR)
  protected val strConstructor: StrConstructor = StrConstructor(strCls, strCls.constructor.symbol)
  protected val strMethods: immutable.Map[IR.MtdSymbol, StrMethod] = strCls.methods.map(mtd =>
    mtd.symbol ->
      StrMethod(strCls, mtd.symbol, mtd.vparamss.headOption.getOrElse(Nil), mtd.A.rep)
  ).toMap

  def register[T0: CodeType](cls: Clasz[T0])(implicit name: Name): Unit = {
    // Table
    knownTableGetters += cls.C.rep.tpe.typeSymbol -> TableGetter(cls)
    knownTablePutters += cls.C.rep.tpe.typeSymbol -> TablePutter(cls)
    val tableRep = new TableRep(cls)
    knownClasses += cls.C.rep.tpe.typeSymbol -> tableRep

    cls.methods.foreach { mtd =>
      knownMethods += mtd.symbol ->
        ClassMethod(cls, mtd.symbol, mtd.tparams, mtd.vparamss, mtd.body)
    }

    // Getters and setters
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

    // Getters and putters LMDB
    val getter = knownTableGetters(cls.C.rep.tpe.typeSymbol).getter
    val putter = knownTablePutters(cls.C.rep.tpe.typeSymbol).putter

    def getSize: Code[Int, Ctx] =
      code{ $(variable).size }.unsafe_asClosedCode // FIXME scope // What does it mean?
    def getAt: Code[Int => T, Ctx] =
      code{ i: Int => $(getter)($(variable), i) }.asInstanceOf[Code[Int => T, Ctx]].unsafe_asClosedCode // FIXME scope
    def append: Code[T => Unit, Ctx] =
      code{ t: T => $(putter)($(variable), t) }.unsafe_asClosedCode // FIXME scope
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

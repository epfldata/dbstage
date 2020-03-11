package dbstage.deep

import dbstage.lang.{Table, TableView}

import IR.Predef._
import IR.Quasicodes._
import IR.MethodApplication

/** This class lifts Squid representations into mixed staged-database
 * representations that are more amenable for query compilation. */
trait QueryLifter { db: StagedDatabase =>
  
  /** Represents a method defined in one of the database's data classes. */
  case class ClassMethod(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val tparams: List[IR.TypParam],
    val vparamss: List[List[Variable[_]]],
    val body: OpenCode[_]
  ) {
    // A variable that refers to the method's implementation in the generated code.
    val variable = Variable[Any => Any](symbol.name.toString)
  }
  
  def liftingError(msg: String): Nothing = throw new Exception("lifting error: " + msg)
  
  def liftQuery[T: CodeType, C](queryCode: Code[T, C]): QueryRep[T, C] =
  // IR.debugFor // enable this to have a trace of the pattern-matching process to debug match failures
  {
    val res: QueryRep[_, C] = queryCode match {
      case code"($tbl: TableView[$ty]).filter($pred)" =>
        Filter[ty.Typ, C](liftQuery(tbl), adaptCode(pred))
      case code"($view: TableView[$ty]).size" =>
        Size(liftQuery(view))
      case code"($view: TableView[$ty]).map($f)" =>
        Map(liftQuery(view), adaptCode(f))
      case _ =>
        queryCode match {
          case code"($tableCode: Table[$ty]).view" =>
            val tbl = getTable(tableCode)
              .getOrElse(liftingError(s"cannot lift table reference: $tableCode"))
            View(tbl)
          //case code"""{val ${x} = ${s}; ${x}.${f}}""" => code"$s.$f"
          case _ =>
            liftingError(s"do not know how to lift query: ${queryCode.showScala}")
        }
    }
    res.asInstanceOf[QueryRep[T, C]] // required due to limitation of scalac
  }
  
  /** Rewrites all calls to methods defined in known data classes of the database
   * into calls to a new function that will be code-generated later. */
  def adaptCode[T, C](cde: Code[T, C]): Code[T, C] = cde rewrite {
    case code"${MethodApplication(app)}: $ty" if knownMethods.contains(app.symbol) =>
      val m = knownMethods(app.symbol)
      assert(app.args.size == 1) // we will handle methods with parameters later
      val thisArg = app.args.head.head
      code{$(m.variable)($(thisArg)).asInstanceOf[ty.Typ]}
  }
  
  /** Internal representation of database queries. */
  sealed abstract class QueryRep[T: CodeType, C] {
    type Res = T
    implicit val Res = codeTypeOf[T]
  }
  case class View[T: CodeType, C](tbl: TableRep[T])
    extends QueryRep[TableView[T], C]

  case class Filter[T: CodeType, C]
    (q: QueryRep[TableView[T], C], pred: Code[T => Boolean, C])
    extends QueryRep[TableView[T], C]

  case class Map[T: CodeType, C]
    (q: QueryRep[TableView[T], C], f: Code[T => T, C])
    extends QueryRep[TableView[T], C]

  case class Size[T: CodeType, C](q: QueryRep[TableView[T], C])
    extends QueryRep[Int, C] {
      type Row = T
      implicit val Row = codeTypeOf[Row]
    }
  
}

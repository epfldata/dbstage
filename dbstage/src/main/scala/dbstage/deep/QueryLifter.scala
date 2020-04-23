package dbstage.deep

import dbstage.lang.{Table, TableView}

import IR.Predef._
import IR.Quasicodes._
import IR.MethodApplication

/** This class lifts Squid representations into mixed staged-database
 * representations that are more amenable for query compilation. */
trait QueryLifter { db: StagedDatabase =>

  case class StrConstructor(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol
  ) {
    assert(symbol.name.toString == "<init>")
    val constructor = Variable[Any => Any](s"init_${owner.name}")
  }

  case class StrMethod(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val params: List[IR.Variable[_]],
    val typ: IR.TypeRep
  ) {
    val variable = Variable[Any => Any](symbol.name.toString)
  }
  
  /** Represents a method defined in one of the database's data classes. */
  case class ClassMethod(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val tparams: List[IR.TypParam],
    val vparamss: List[List[Variable[_]]],
    val body_0: OpenCode[_]
  ) {
    // A variable that refers to the method's implementation in the generated code.
    val variable = Variable[Any => Any](symbol.name.toString)

    lazy val body = adaptCode(body_0)
  }

  case class ClassConstructor(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val params: List[IR.Variable[_]]
  ) {
    assert(symbol.name.toString == "<init>")
    val constructor = Variable[Any => Any](s"init_${owner.name}")
  }

  case class ClassGetter(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val index: Int,
    val typ: IR.TypeRep
  ) {
    val getter = Variable[Any => Any](symbol.name.toString)
  }

  case class ClassSetter(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val index: Int
  ) {
    val field = owner.fields(index-1)
    val setter = Variable[(Any, Any) => Unit](symbol.name.toString)
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
          case code"TableView.all[$ty]" =>
            val tbl = knownClasses.getOrElse(ty.rep.tpe.typeSymbol, liftingError(s"cannot lift table reference: $ty")).asInstanceOf[TableRep[ty.Typ]]
            View(tbl)
          case code"($t0: $tx) => ($tableCode: Table[$ty]).insert($t1)" => // Write this better
            // Doesn't work, just has a reference
            val tbl = getTable(tableCode)
              .getOrElse(liftingError(s"cannot lift table reference: $tableCode"))
            println(queryCode.showScala)
            Insert[ty.Typ, C](tbl, t1.asInstanceOf[IR.Code[ty.Typ, C]]) // Erasing t0 ctx?
          case _ =>
            liftingError(s"do not know how to lift query: ${queryCode.showScala}")
        }
    }
    res.asInstanceOf[QueryRep[T, C]] // required due to limitation of scalac
  }
  
  /** Rewrites all calls to methods defined in known data classes of the database
   * into calls to a new function that will be code-generated later. */
  def adaptCode[T, C](cde: Code[T, C]): Code[T, C] = cde rewrite {
    case code"${MethodApplication(app)}: $ty" if strMethods.contains(app.symbol) =>
      val m = strMethods(app.symbol)
      val thisArg = app.args.head.head
      val args = if (app.args.size == 1) Nil else app.args(1)
      (args.length match {  // How to give arguments nicely?
        case 0 => code{$(m.variable)($(thisArg))}
        case 1 => code{$(m.variable)($(thisArg), $(args(0)))}
        case _ => liftingError(s"do not know how to lift Str method: ${m.symbol.name}")
      }).asInstanceOf[Code[ty.Typ, C]]
    case code"${MethodApplication(app)}: $ty" if app.symbol == strConstructor.symbol =>
      assert(app.args.size == 2)
      assert(app.args(1).size == 1)
      val str = app.args(1)(0)
      code{$(strConstructor.constructor)($(str))}.asInstanceOf[Code[ty.Typ, C]]
    case code"${MethodApplication(app)}: $ty" if knownMethods.contains(app.symbol) =>
      val m = knownMethods(app.symbol)
      assert(app.args.size == 1) // we will handle methods with parameters later
      val thisArg = app.args.head.head
      code{$(m.variable)($(thisArg)).asInstanceOf[ty.Typ]}
    case code"${MethodApplication(app)}: $ty" if knownFieldGetters.contains(app.symbol) =>
      val g = knownFieldGetters(app.symbol)
      assert(app.args.size == 1)
      val thisArg = app.args.head.head
      code{$(g.getter)($(thisArg))}.asInstanceOf[Code[ty.Typ, C]]
    case code"${MethodApplication(app)}: $ty" if knownFieldSetters.contains(app.symbol) =>
      val s = knownFieldSetters(app.symbol)
      assert(app.args.size == 2)
      val thisArg = app.args.head.head
      val newValue = app.args.tail.head.head
      code{$(s.setter)($(thisArg), $(newValue))}.asInstanceOf[Code[ty.Typ, C]]
    case code"${MethodApplication(app)}: $ty" if knownConstructors.contains(app.symbol) =>
      val c = knownConstructors(app.symbol)
      assert(app.args.size == 2)
      val args = app.args(1)
      (args.length match {  // How to give arguments nicely?
        case 1 => code{$(c.constructor)($(args(0)))}
        case 2 => code{$(c.constructor)($(args(0)), $(args(1)))}
        case 3 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)))}
        case 4 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)))}
        case _ => liftingError(s"do not know how to lift constructor: ${c.symbol.name}")
      }).asInstanceOf[Code[ty.Typ, C]]
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
  
  case class Insert[T: CodeType, C]
    (tbl: TableRep[T], el: Code[T, C]) extends QueryRep[TableView[T], C] {
      type Row = T
      implicit val Row = codeTypeOf[Row]
    }
  
}

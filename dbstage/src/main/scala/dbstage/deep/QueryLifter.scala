package dbstage.deep

import dbstage.lang.TableView

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
    val body_0: OpenCode[_],
    val typ: IR.TypeRep
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
    val name: String,
    val typ: IR.TypeRep
  ) {
    val getter = Variable[Any => Any](symbol.name.toString)
  }

  case class ClassSetter(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val index: Int,
    val name: String
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
          case code"TableView.all[$ty]" =>
            val tbl = knownClasses.getOrElse(ty.rep.tpe.typeSymbol, liftingError(s"cannot lift table reference: $ty")).asInstanceOf[TableRep[ty.Typ]]
            View(tbl)
          // case code"($tableCode: Table[$ty]).insert($t1)" =>
          //   // Doesn't work for some reason because of IR
          //   val tbl = getTable(tableCode)
          //     .getOrElse(liftingError(s"cannot lift table reference: $tableCode"))
          //   Insert[ty.Typ, C](tbl, t1)
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
      val thisArg = app.args.head.head
      val args = if (app.args.size == 1) Nil else app.args(1)
      (args.length match {  // How to give arguments nicely?
        case 0 => code{$(m.variable)($(thisArg)).asInstanceOf[ty.Typ]}
        case 1 => code{$(m.variable)($(thisArg), $(args(0))).asInstanceOf[ty.Typ]}
        case _ => liftingError(s"do not know how to lift method: ${m.symbol.name}")
      })
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
      
      val args = app.args(1).map(arg => {
        if (arg.Typ.rep.tpe.typeSymbol == codeTypeOf[String].rep.tpe.typeSymbol) {
          code{ $(toCString)($(arg.asInstanceOf[Code[String, _]])) }
        } else {
          arg
        }
      })

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

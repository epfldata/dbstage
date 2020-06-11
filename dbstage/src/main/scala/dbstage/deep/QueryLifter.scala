package dbstage.deep

import dbstage.lang.{Table, TableView, Str}

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
    val name: String,
    val typ: IR.TypeRep
  ) {
    val getter = Variable[Any => Any](s"get_${symbol.name.toString}")
    lazy val index = get_index(name, owner.fields.map(f => (f.name, f.A.rep)))
  }

  case class ClassSetter(
    val owner: IR.TopLevel.Clasz[_],
    val symbol: IR.MtdSymbol,
    val name: String,
    val typ: IR.TypeRep
  ) {
    val setter = Variable[(Any, Any) => Unit](s"set_${symbol.name.toString}")
    lazy val index = get_index(name, owner.fields.map(f => (f.name, f.A.rep)))
  }

  case class ClassDeleter(
    val owner: IR.TopLevel.Clasz[_]
  ) {
    val deleter = Variable[Any => Unit](s"delete_${owner.name}")
  }

  def get_index(name: String, fields: List[(String, IR.TypeRep)]): Int = {
    if (name == "key ") return 1

    var index = 1

    fields.foreach{ case (f_name, typ) => {
      if(f_name != "key ") index += 1
      val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == typ.tpe.typeSymbol)
      if (knownDataType) index += 1
      if (name == f_name) return index
    }}

    liftingError(s"Did not find index of field ${name}:\n\tIn fields: ${fields.mkString("(", ",", ")")}")
  }
  
  def liftingError(msg: String): Nothing = throw new Exception("lifting error: " + msg)
  
  def liftQuery[T: CodeType, C](queryCode: Code[T, C]): QueryRep[T, C] =
  // IR.debugFor // enable this to have a trace of the pattern-matching process to debug match failures
  {
    val res: QueryRep[_, C] = queryCode match {
      case code"val $x: $xt = $v; $rest: T" =>
        LetBinding(Some(x),
                  liftQuery(v),
                  liftQuery(rest))
      case code"$e; $rest: T" =>
        LetBinding(None,
                  liftQuery(e),
                  liftQuery(rest))
      case code"($arg: $typ) => $exp: $ret" =>
        FunctionRep(arg, liftQuery(exp))
      case code"while($cond) $e" =>
        While(liftQuery(cond),
              liftQuery(e))
      case code"()" =>
        NoOp()
      case code"TableView.all[$ty]" =>
        val tbl = knownClasses.getOrElse(ty.rep.tpe.typeSymbol, liftingError(s"cannot lift table reference: $ty")).asInstanceOf[TableRep[ty.Typ]]
        View(tbl)
      case code"($view: TableView[$ty]).size" =>
        Size(liftQuery(view))
      case code"($view: TableView[$ty]).forEach($f)" =>
        ForEach(liftQuery(view),
                  liftQuery(f))
      case code"($view: TableView[$ty]).filter($pred)" =>
        Filter(liftQuery(view),
              liftQuery(pred))
      case code"($view: TableView[$ty]).map[$tres]($f)" =>
        Map(liftQuery(view),
            liftQuery(f))
      case code"($view: TableView[$ty]).join($otherView: TableView[$tother])" =>
        Join(liftQuery(view), liftQuery(otherView))
      case code"($view: TableView[$ty]).aggregate[$tres]($init, $acc)" =>
        Aggregate(liftQuery(view),
                    liftQuery(init),
                    liftQuery(acc))
      case _ =>
        val cde = adaptCode(queryCode)
        checkScalaCode(cde)
        CodeRep(cde)
    }
    res.asInstanceOf[QueryRep[T, C]] // required due to limitation of scalac
  }
  
  /** Rewrites all calls to methods defined in known data classes of the database
   * into calls to a new function that will be code-generated later. */
  def adaptCode[T, C](cde: Code[T, C]): Code[T, C] = cde rewrite {
    // Need to look at field getters and setters
    case code"Table.delete[$ty]($el)" if knownDeleters.contains(ty.rep.tpe.typeSymbol) =>
      val deleter = knownDeleters(ty.rep.tpe.typeSymbol)
      code{$(deleter.deleter)($(el))}
    case code"${MethodApplication(app)}: $ty" if knownMethods.contains(app.symbol) =>
      val m = knownMethods(app.symbol)
      val thisArg = app.args.head.head
      val args = if (app.args.size == 1) Nil else app.args(1).map(arg => {
        if (m.owner.C =:= codeTypeOf[Str] && arg.Typ.rep.tpe.typeSymbol == codeTypeOf[String].rep.tpe.typeSymbol) {
          // Convert String to CString in Str methods
          code{$(toCString)($(arg.asInstanceOf[Code[String, arg.Ctx]]))}
        } else arg
      })
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
        case 5 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)))}
        case 6 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)))}
        case 7 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)))}
        case 8 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)))}
        case 9 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)))}
        case 10 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)))}
        case 11 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)), $(args(10)))}
        case 12 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)), $(args(10)), $(args(11)))}
        case 13 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)), $(args(10)), $(args(11)), $(args(12)))}
        case 14 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)), $(args(10)), $(args(11)), $(args(12)), $(args(13)))}
        case 15 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)), $(args(10)), $(args(11)), $(args(12)), $(args(13)), $(args(14)))}
        case 16 => code{$(c.constructor)($(args(0)), $(args(1)), $(args(2)), $(args(3)), $(args(4)), $(args(5)), $(args(6)), $(args(7)), $(args(8)), $(args(9)), $(args(10)), $(args(11)), $(args(12)), $(args(13)), $(args(14)), $(args(15)))}
        case _ => liftingError(s"do not know how to lift constructor: ${c.symbol.name}")
      }).asInstanceOf[Code[ty.Typ, C]]
  }

  // Can probably write this in a better way
  def checkScalaCode[T, C](cde: Code[T, C]): Unit = cde analyse {
    case code"TableView.all[$ty]" => liftingError(s"Unsupported code '.all' inside query: ${cde.showScala}")
    case code"($view: TableView[$ty]).size" => liftingError(s"Unsupported code '.size' inside query: ${cde.showScala}")
    case code"($view: TableView[$ty]).forEach($f)" => liftingError(s"Unsupported code '.forEach' inside query: ${cde.showScala}")
    case code"($view: TableView[$ty]).filter($pred)" => liftingError(s"Unsupported code '.filter' inside query: ${cde.showScala}")
    case code"($view: TableView[$ty]).map[$tres]($f)" => liftingError(s"Unsupported code '.map' inside query: ${cde.showScala}")
    case code"($view: TableView[$ty]).join($otherView: TableView[$tother])" => liftingError(s"Unsupported code '.join' inside query: ${cde.showScala}")
    case code"($view: TableView[$ty]).aggregate[$tres]($init, $acc)" => liftingError(s"Unsupported code '.aggregate' inside query: ${cde.showScala}")
  }
  
  /** Internal representation of database queries. */
  sealed abstract class QueryRep[T: CodeType, C] {
    type Res = T
    implicit val Res = codeTypeOf[T]
  }
  case class View[T: CodeType, C](tbl: TableRep[T])
    extends QueryRep[TableView[T], C]

  case class CodeRep[T: CodeType, C](cde: Code[T, C])
    extends QueryRep[T, C]

  case class FunctionRep[R: CodeType, T: CodeType, C, CWithArg <: C](arg: Variable[R], cde: QueryRep[T, CWithArg])
    extends QueryRep[R => T, C] {
      type Arg = R
      implicit val Arg = codeTypeOf[Arg]
      type Ret = T
      implicit val Ret = codeTypeOf[Ret]
    }

  case class Filter[T: CodeType, C]
    (q: QueryRep[TableView[T], C], pred: QueryRep[T => Boolean, C])
    extends QueryRep[TableView[T], C]

  case class Map[T: CodeType, R: CodeType, C]
    (q: QueryRep[TableView[T], C], f: QueryRep[T => R, C])
    extends QueryRep[TableView[R], C] {
      type Row = T
      implicit val Row = codeTypeOf[Row]
    }

  case class Size[T: CodeType, C](q: QueryRep[TableView[T], C])
    extends QueryRep[Int, C] {
      type Row = T
      implicit val Row = codeTypeOf[Row]
    }

  case class Join[T: CodeType, R: CodeType, C]
    (q1: QueryRep[TableView[T], C], q2: QueryRep[TableView[R], C])
    extends QueryRep[TableView[(T, R)], C] {
      type Row1 = T
      implicit val Row1 = codeTypeOf[Row1]
      type Row2 = R
      implicit val Row2 = codeTypeOf[Row2]
  }

  case class Aggregate[T: CodeType, Res: CodeType, C]
    (q: QueryRep[TableView[T], C], init: QueryRep[Res, C], acc: QueryRep[(T, Res) => Res, C])
    extends QueryRep[Res, C] {
      type Row = T
      implicit val Row = codeTypeOf[Row]
    }

  case class ForEach[T: CodeType, C]
    (q: QueryRep[TableView[T], C], f: QueryRep[T => Unit, C])
    extends QueryRep[Unit, C] {
      type Row = T
      implicit val Row = codeTypeOf[Row]
    }

  case class While[T: CodeType, C]
    (cond: QueryRep[Boolean, C], e: QueryRep[T, C])
    extends QueryRep[Unit, C] {
      type Val = T
      implicit val Val = codeTypeOf[T]
    }

  case class LetBinding[R: CodeType, T: CodeType, C, CWithX <: C]
    (x: Option[Variable[R]], value: QueryRep[R, C], rest: QueryRep[T, CWithX])
    extends QueryRep[T, C] {
      type Val = R
      implicit val Val = codeTypeOf[Val]
    }

  case class NoOp[C]() extends QueryRep[Unit, C]
}

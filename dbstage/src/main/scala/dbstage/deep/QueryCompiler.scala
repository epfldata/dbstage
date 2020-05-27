package dbstage.deep

import dbstage.lang.TableView

import IR.Predef._
import IR.Quasicodes._
import dbstage.lang.LMDBTable
import squid.lib.MutVar

/** This class turns queries into query plans
 * and compiles query plans into low-level code. */
trait QueryCompiler { self: StagedDatabase =>

  def compilingError(msg: String): Nothing = throw new Exception("compiling error: " + msg)
  
  // For now, this is a very naive approach to query and database compilation!
  
  /** Turn a query into a query plan. */
  def planQuery[T, C >: Ctx](rep: QueryRep[T, C]): QueryPlan[T, Ctx] = {
    val res: QueryPlan[_, Ctx] = rep match {
      case s: Size[_, C @unchecked] => // the erasure warning on C is spurious!
        import s.Row // to get the right implicit type representations in scope
        QueryAggregate[Row, Int, Ctx](planIteration(s.q),
                                      QueryCode(code{0}),
                                      QueryCode(code{ (row: Row, acc: Int) => acc+1}))
      case a: Aggregate[_, _, C @unchecked] =>
        import a.{Row, Res}
        QueryAggregate[Row, Res, Ctx](planIteration(a.q), planQuery(a.init), planQuery(a.acc))
      case f: ForEach[_, C @unchecked] =>
        import f.Row
        QueryForEach[Row, Ctx](planIteration(f.q), planQuery(f.f))
      case w: While[t, C @unchecked] =>
        import w.Val
        QueryWhile(planQuery(w.cond), planQuery(w.e))
      case f: FunctionRep[targ, tres, C @unchecked, cWithArg] => 
        import f.{Arg, Res, Ret}
        QueryFunction(f.arg, planQuery(f.cde.asInstanceOf[QueryRep[tres, C]]))
      case c: CodeRep[t, C @unchecked] =>
        import c.Res
        QueryCode(c.cde)
      case NoOp() => QueryCode(code{()})
      case l: LetBinding[tx, tres, C @unchecked, cWithX] =>
        import l.{Val, Res}
        QueryLetBinding(l.x, planQuery(l.value), planQuery(l.rest.asInstanceOf[QueryRep[tres, C]]), l.mutable)
      case _ => println(rep);??? // unsupported for now
    }
    res.asInstanceOf[QueryPlan[T, Ctx]]
  }
  def planIteration[T: CodeType, C >: Ctx](rep: QueryRep[TableView[T], C]): IterationPlan[T, Ctx] = {
    val res: IterationPlan[_, Ctx] = rep match {
      case View(tbl) => Scan(tbl)
      case Filter(view, pred) => Selection(planIteration(view), planQuery(pred))
      case map: Map[typ, tres, C @unchecked] =>
        import map.Row
        QueryMap(planIteration(map.q), planQuery(map.f))
      case join: Join[t1, t2, C @unchecked] =>
        import join.{Row1, Row2}
        QueryJoin(planIteration(join.q1), planIteration(join.q2))
      case _ => compilingError(s"Encountered unexpected QueryRep ${rep} when planning iteration")
    }
    res.asInstanceOf[IterationPlan[T, Ctx]]
  }
  
  /** A query plan. */
  sealed abstract class QueryPlan[Res: CodeType, -C] {
    def getCode: Code[Res, C]
  }

  case class QueryCode[Res: CodeType, C]
    (cde: Code[Res, C])
    extends QueryPlan[Res, C] {
      def getCode: Code[Res, C] = cde
    }

  case class QueryFunction[R: CodeType, Res: CodeType, C, Context <: C]
    (arg: Variable[R], cde: QueryPlan[Res, Context])
    extends QueryPlan[R => Res, C] {
      def getCode: Code[R => Res, C] = code{
        $arg: R => {
          $(cde.getCode)
        }
      }.asInstanceOf[Code[R => Res, C]]
    }
  
  case class QueryAggregate[Row: CodeType, Res: CodeType, C]
    (src: IterationPlan[Row, C], init: QueryPlan[Res, C], acc: QueryPlan[(Row, Res) => Res, C])
    extends QueryPlan[Res, C] {
      def getCode: Code[Res, C] = code{
        var res = $(init.getCode)
        $(src.foreach(code{ row: Row => res = $(acc.getCode)(row, res) }))
        res
      }
    }

  case class QueryForEach[Row: CodeType, C]
    (src: IterationPlan[Row, C], f: QueryPlan[Row => Unit, C])
    extends QueryPlan[Unit, C] {
      def getCode: Code[Unit, C] = code{
        $(src.foreach(f.getCode))
      }
    }

  case class QueryWhile[T: CodeType, C]
    (cond: QueryPlan[Boolean, C], e: QueryPlan[T, C])
    extends QueryPlan[Unit, C] {
      def getCode: Code[Unit, C] = code{
        while($(cond.getCode)) {
          $(e.getCode)
        }
      }
    }

  case class QueryLetBinding[R: CodeType, T: CodeType, C, Ctx <: C]
    (x: Option[Variable[R]], value: QueryPlan[R, C], rest: QueryPlan[T, Ctx], mutable: Boolean)
    extends QueryPlan[T, C] {
      def getCode: Code[T, C] = {
        (if(x.isDefined) {
          if(mutable) {
            val binding = x.get.asInstanceOf[Variable[MutVar[R]]]
            val valueCode = value.getCode
            val restCode = rest.getCode
            code"""$binding := $valueCode; $restCode"""
          } else {
            val binding = x.get
            code{
              val $binding = $(value.getCode)
              $(rest.getCode)
            }
          }
        } else code{
          $(value.getCode);
          $(rest.getCode)
        }).asInstanceOf[Code[T, C]]
      }
    }

  /** The plan for some iteration as part of a bigger query plan. */
  sealed abstract class IterationPlan[Row: CodeType, -C] {
    // def pull: Code[(() => T) => Unit, ] // TODO pull plans
    // What are pull plans?
    
    /** A way to generate code for pushing rows. */
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0] // Why Row => Boolean? Where is it used?
    
    def foreach[C0 <: C](step: Code[Row => Unit, C0]): Code[Unit, C0] =
      push(code{ row: Row => $(step)(row); true })
  }
  
  case class Scan[Row: CodeType, C >: Ctx]
    (src: TableRep[Row])
    extends IterationPlan[Row, C]
  {
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0] = code{
      val cursor = $(src.getCursor)

      var get = $(src.getLast)(cursor)
      while(get != null && {$(step)(get)}){ get = $(src.getPrev)(cursor) }
      
      $(src.closeCursor)(cursor)
    }.unsafe_asClosedCode // FIXME scope
  }
  
  case class Selection[Row: CodeType, C >: Ctx]
    (src: IterationPlan[Row, C], pred: QueryPlan[Row => Boolean, C])
    extends IterationPlan[Row, C]
  {
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0] =
      src.push[C0](code{ row: Row => if ($(pred.getCode)(row)) $(step)(row) else true })
  }

  case class QueryMap[Row: CodeType, RowRes: CodeType, C >: Ctx]
    (src: IterationPlan[Row, C], f: QueryPlan[Row => RowRes, C])
    extends IterationPlan[RowRes, C]
  {
    def push[C0 <: C](step: Code[RowRes => Boolean, C0]): Code[Unit, C0] =
      src.push[C0](code{ row: Row => $(step)($(f.getCode)(row))})
  }

  case class CodeQuery[Res: CodeType, C >: Ctx](cde: Code[Res, C]) extends QueryPlan[Res, C] {
    def getCode: Code[Res, C] = cde
  }

  case class QueryJoin[Row1: CodeType, Row2: CodeType, C >: Ctx]
    (it1: IterationPlan[Row1, C], it2: IterationPlan[Row2, C])
    extends IterationPlan[(Row1, Row2), C]
  {
    def push[C0 <: C](step: Code[((Row1, Row2)) => Boolean, C0]): Code[Unit, C0] = {
      it1.push[C0](code{ row1: Row1 => {
        $(it2.push(code{ row2: Row2 => {
          $(step)((row1, row2))
        }}))
        true
      }})
    }
  }
  
}

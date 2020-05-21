package dbstage.deep

import dbstage.lang.TableView

import IR.Predef._
import IR.Quasicodes._
import dbstage.lang.LMDBTable

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
        Aggregate[Row, Int, Ctx](planIteration(s.q), code{0}, code{ (row: Row, acc: Int) => acc+1})
      case c: CodeQueryRep[_, _] =>
        import c.{Res}
        CodeQuery[Res, Ctx](c.code)
      case a: AggregateRep[_, _, C @unchecked] =>
        import a.{Row, Res}
        Aggregate[Row, Res, Ctx](planIteration(a.q), a.init, a.acc)
      case f: ForEachRep[_, C @unchecked] =>
        import f.Row
        ForEach[Row, Ctx](planIteration(f.q), f.f)
      case _ => ??? // unsupported for now
    }
    res.asInstanceOf[QueryPlan[T, Ctx]]
  }
  def planIteration[T: CodeType, C >: Ctx](rep: QueryRep[TableView[T], C]): IterationPlan[T, Ctx] = {
    val res: IterationPlan[_, Ctx] = rep match {
      case View(tbl) => Scan(tbl)
      case Filter(view, pred) => Selection(planIteration(view), pred)
      case Map(view, f) => QueryMap(planIteration(view), f)
      case Join(view1, view2) => QueryJoin(planIteration(view1), planIteration(view2))
      case _ => compilingError(s"Encountered unexpected QueryRep ${rep} when planning iteration")
    }
    res.asInstanceOf[IterationPlan[T, Ctx]]
  }
  
  /** A query plan. */
  sealed abstract class QueryPlan[Res: CodeType, -C] {
    def getCode: Code[Res, C]
  }
  
  case class Aggregate[Row: CodeType, Res: CodeType, C]
    (src: IterationPlan[Row, C], init: Code[Res, C], acc: Code[(Row, Res) => Res, C])
    extends QueryPlan[Res, C] {
      def getCode: Code[Res, C] = code{
        var res = $(init)
        $(src.foreach(code{ row: Row => res = $(acc)(row, res) }))
        res
      }
    }

  case class ForEach[Row: CodeType, C]
    (src: IterationPlan[Row, C], f: Code[Row => Unit, C])
    extends QueryPlan[Unit, C] {
      def getCode: Code[Unit, C] = code{
        $(src.foreach(f))
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
    (src: IterationPlan[Row, C], pred: Code[Row => Boolean, C])
    extends IterationPlan[Row, C]
  {
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0] =
      src.push[C0](code{ row: Row => if ($(pred)(row)) $(step)(row) else true })
  }

  case class QueryMap[Row: CodeType, RowRes: CodeType, C >: Ctx]
    (src: IterationPlan[Row, C], f: Code[Row => RowRes, C])
    extends IterationPlan[RowRes, C]
  {
    def push[C0 <: C](step: Code[RowRes => Boolean, C0]): Code[Unit, C0] =
      src.push[C0](code{ row: Row => $(step)($(f)(row))})
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

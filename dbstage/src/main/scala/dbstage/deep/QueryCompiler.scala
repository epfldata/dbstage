package dbstage.deep

import dbstage.lang.{Table, TableView}

import IR.Predef._
import IR.Quasicodes._

/** This class turns queries into query plans
 * and compiles query plans into low-level code. */
trait QueryCompiler { self: StagedDatabase =>
  
  // For now, this is a very naive approach to query and database compilation!
  
  /** Turn a query into a query plan. */
  def planQuery[T, C >: Ctx](rep: QueryRep[T, C]): QueryPlan[T, Ctx] = {
    val res: QueryPlan[_, Ctx] = rep match {
      case s: Size[_, C @unchecked] => // the erasure warning on C is spurious!
        import s.{Res, Row} // to get the right implicit type representations in scope
        Aggregate[Row, Int, Ctx](planIteration(s.q),
          code{ 0 }, code{ (_: Row, acc: Int) => acc + 1 })
      case _ => ??? // unsupported for now
    }
    res.asInstanceOf[QueryPlan[T, Ctx]]
  }
  def planIteration[T: CodeType, C >: Ctx](rep: QueryRep[TableView[T], C]): IterationPlan[T, Ctx] = {
    val res: IterationPlan[_, Ctx] = rep match {
      case View(tbl) => Scan(tbl)
      case Filter(view, pred) => Selection(planIteration(view), pred)
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
  
  /** The plan for some iteration as part of a bigger query plan. */
  sealed abstract class IterationPlan[Row: CodeType, -C] {
    // def pull: Code[(() => T) => Unit, ] // TODO pull plans
    
    /** A way to generate code for pushing rows. */
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0]
    
    def foreach[C0 <: C](step: Code[Row => Unit, C0]): Code[Unit, C0] =
      push(code{ row: Row => $(step)(row); true })
  }
  
  case class Scan[Row: CodeType, C >: Ctx]
    (src: TableRep[Row])
    extends IterationPlan[Row, C]
  {
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0] = code{
      val size = $(src.getSize)
      var i = 0
      while(i < size && {val next = $(src.getAt)(i); $(step)(next)}){ i += 1 }
    }.unsafe_asClosedCode // FIXME scope
  }
  
  case class Selection[Row: CodeType, C >: Ctx]
    (src: IterationPlan[Row, C], pred: Code[Row => Boolean, C])
    extends IterationPlan[Row, C]
  {
    def push[C0 <: C](step: Code[Row => Boolean, C0]): Code[Unit, C0] =
      src.push[C0](code{ row: Row => if ($(pred)(row)) $(step)(row) else true })
  }
  
}

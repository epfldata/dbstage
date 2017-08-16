package dbstage
package query

import squid.utils._
import frontend._
import runtime._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}

//abstract class Operator[T] {
//  //def init(step: Code[T => Unit]): Code[Unit]
//  def init(step: Code[T => Unit]): CrossStage[Unit]
//}

//abstract class AdHocPoly[X] { def apply[T](t: Code[T]): Code[X] => Code[T] }
//object NeatClosure3 {
//  def close[X:IRType,R,S](f: Code[X] => Code[R])(k: (Code[R], AdHocPoly[X]) => S): S = {
//    import base._
//    val fv = freshBoundVal(typeRepOf[X])
//    val body = f(IR(base.readVal(fv)))
//    k(body, new AdHocPoly[X] { 
//      def apply[T](b:Code[T]) = x => 
//        IR(base.inline(fv,b.rep,x.rep))
//    })
//  }
//}

sealed trait Query {
  lazy val bestPlan: QueryPlan = ???
}
sealed trait QueryPlan {
  val uid = 42 // TODO
  val rowFormat: RowFormat = null
  type Row = rowFormat.Repr
  import rowFormat.Repr
  lazy val cost: Double = ???
  //def mkPrgm: CrossStage[Any] = ???
  //def mkPrgm: Operator[rowFormat.Repr] = ???
  //def mkPrgm: CrossStage[Any] = this match {
  //  case Scan(tbl) => 
  //    //tbl.scan(cols => ir"List(${cols.asInstanceOf[Seq[Field{type T=Any}]]}*).foreach(println)") alsoApply println
  //    //tbl.scan(cols => ir"List(${cols map (field2Code) asInstanceOf[Seq[Code[Any]]]}*).foreach(println)") alsoApply println
  //    //tbl.scan(cols => dbg_ir"List(${cols map (field2CodeOf[Any])}*).foreach(println)") alsoApply println // FIXME: this should work!
  //    //tbl.scan { cols => 
  //    //  val xs = cols map (field2CodeOf[Any])
  //    //  ir"List($xs*).foreach(println)" } alsoApply println
  //    ???
  //  case Filter(that, pred) =>
  //    println(that.rowFormat.lift(pred,uid))
  //    //println(that.rowFormat.lift2 { fr => })
  //    ???
  //  case GeneralJoin(lhs, rhs, pred) =>
  //    ???
  //}
  //def printLines
  def push(step: Code[Row => Bool]): CrossStage[Unit] = pushRec(step) map (_ transformWith LogicFlow)
  def pushRec(step: Code[Row => Bool]): CrossStage[Unit] = ???
  def foreachCode(f: Code[Row => Unit]) = push(ir"(x:Row) => {$f(x); true}")
  //def foreach(f: Row => Unit) = (for {
  //  nextF <- CrossStage(f)(identity)
  //  fe <- foreachCode(nextF)
  //} yield fe).compile()
  //def foreach = ir"(f: Row => Unit) => ${
  //  (f: Code[Row => Unit]) => foreachCode(f) } yield fe).compile() }"
  //def foreach = NeatClosure3.close((f: Code[Row => Unit]) => )
  def foreach = (f: Row => Unit) => CrossStage.magic((f: Code[Row => Unit]) => foreachCode(f)).compile()(f)
}
case class Scan(tbl: Table) extends QueryPlan {
  override val rowFormat: tbl.rowFmt.type = tbl.rowFmt
  /*
  //override def mkPrgm: CrossStage[Any] = CrossStage(tbl.scan)
  override def mkPrgm: Operator[rowFormat.Repr] = {
    //for { fe <- tbl.foreach } yield ir"()"
    new Operator[rowFormat.Repr] {
      def init(step: Code[rowFormat.Repr => Unit]): CrossStage[Unit] = {
        //tbl.scan(tbl.rowFormat.lift(pred,uid))
        tbl.scan(step)
      }
    }
    //???
  }
  */
  override def pushRec(step: Code[Row => Bool]): CrossStage[Unit] = tbl.push(step)
}
case class Project(that: QueryPlan, cols: Seq[Field]) extends QueryPlan
case class Filter(that: QueryPlan, pred: Code[Bool]) extends QueryPlan {
  override val rowFormat: that.rowFormat.type = that.rowFormat
  import rowFormat.Repr
  /*
  override def mkPrgm: Operator[rowFormat.Repr] = {
    new Operator[rowFormat.Repr] {
      def init(step: Code[rowFormat.Repr => Unit]): CrossStage[Unit] = {
        that.mkPrgm.init(step)
      }
    }
  }
  */
  override def pushRec(step: Code[Row => Bool]): CrossStage[Unit] =
    //that.push(ir"(x:Row) => !${rowFormat.lift(pred,uid)}(x) || $step(x)")
    that.pushRec(ir"(x:Row) => if (${rowFormat.lift(pred,uid)}(x)) $step(x) else true")
}
case class GeneralJoin(lhs: QueryPlan, rhs: QueryPlan, pred: Code[Bool]) extends QueryPlan
case class EquiJoin(lhs: QueryPlan, rhs: QueryPlan, eqCols: Seq[Field]) extends QueryPlan
case class Print(that: QueryPlan) extends QueryPlan {
  import that.rowFormat.Repr
  //override val rowFormat = new SingleColumnFormat(Field[String]("StringRep"))
  override val rowFormat = SingleColumnFormat[String]("StringRep")
  override def pushRec(step: Code[Row => Bool]): CrossStage[Unit] = {
    //val colsToString = rowFormat.columns
    //var colsToString = ir""""|""""
    var colsToString = Const("|")
    for (c <- that.rowFormat.columns) colsToString = {
      //ir"$colsToString + ${c.SerialT.unparse}($c)"
      import c._
      ir"""$colsToString + ${SerialT.unparse}($toCode) + "|""""
    }
    //println(colsToString)
    that.pushRec(ir"(x:that.Row) => $step(${that.rowFormat.lift(colsToString,uid)}(x))")
  }
}


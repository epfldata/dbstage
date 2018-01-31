package dbstage2

import dbstage2.Embedding.Predef._
import dbstage2.Embedding.ClosedCode
import dbstage2.queryplan.QueryPlan

case class Database2(rels: Relation[_]*) {
  
  object AccessibleRelation {
    //def unapply[T<:Record,C](cde: Code[Relation[T],C]): Option[Relation[T]] = cde match {
    def unapply[T,C](cde: Code[Relation[T],C]): Option[Relation[T]] = cde match {
      case ClosedCode(cc) =>
        //Some(if (cc.rep.effect == squid.ir.SimpleEffect.Pure) cc.run
        Some(if (!cc.rep.effect.immediate) cc.run
        else {
          println(s"Impure relation: $cc")
          ???})
      case _ =>
        None // TODO warn
    }
  }
  
  def analyse(q:Code[Any,_]) = {
    q analyse {
      //case ClosedCode(cc) =>
      //case code"${ClosedCode(cc)}:Relation[$t where (t <:< Record)]" =>
      //  println(cc,cc.rep.effect)
      //case KnownRelation(r) =>
      //case code"${KnownRelation(r)}:Relation[$t where (t <:< Record)]" =>
      case code"${AccessibleRelation(r)}:Relation[$t]" =>
        println(r,r.primaryKeys.map(_.size))
    }
  }
  
  def lift[T:CodeType](q:ClosedCode[Query[T]]): QueryPlan[T] = {
    
    println(q)
    
    q match {
      case code"($q0:Query[$t]).count" =>
        //val qp = queryplan.Count(lift(q0))
        //dbg_code"qp:QueryPlan[T]".run
        // ^ TODO more flexible subtyping evidence to congrue into constructors?
        queryplan.Count(lift(q0)).asInstanceOf[QueryPlan[T]]
      case code"($q0:Query[T]).filter($p)" =>
        queryplan.Filter(lift(q0),p)
      //case code"(${AccessibleRelation(r)}:Relation[$t]).query" =>
      case code"(${AccessibleRelation(r)}:Relation[T]).query" =>
        queryplan.Scan(r)
    }
    
    //null
    //???
    
  }
  
  /*
    import QueryHK.PlainQuery
  
  def liftHK[T:CodeType](q:ClosedCode[PlainQuery[T]]): QueryHK.StagedQuery[T,Any] = {
    
    //q match {
    //    case code"($q0:PlainQuery[T]).filter($p)" =>
    //    queryplan.Filter(lift(q0),p)
    //}
    
    //dbg.implicitType[QueryHK.Identity[Any]]
    dbg.implicitType[PlainQuery[Any]]
    
    ???
    
  }
  */
  
  import PlainQuery._
  import dbstage2.{StagedQuery=>SQ}
  
  //def liftHK[T:CodeType,C](q:ClosedCode[Query[T]]): StagedQuery[C]#Query[T] = {
  //  object SQ extends StagedQuery[C]
  //  q match {
  //    case code"($q0:Query[$t]).count" =>
  //      //val qp = queryplan.Count(lift(q0))
  //      //dbg_code"qp:QueryPlan[T]".run
  //      // ^ TODO more flexible subtyping evidence to congrue into constructors?
  //      SQ.CountQuery(liftHK(q0)).asInstanceOf[QueryPlan[T]]
  //    case code"($q0:Query[T]).filter($p)" =>
  //      SQ.Filter(liftHK(q0),p)
  //    //case code"(${AccessibleRelation(r)}:Relation[$t]).query" =>
  //    case code"(${AccessibleRelation(r)}:Relation[T]).query" =>
  //      SQ.Scan(r)
  //  }
  //def liftHK[T:CodeType,C](q:ClosedCode[Query[T,Any]]): SQ.Query[T,C] = {
  def liftHK[T:CodeType,C](q:Code[Query[T,Any],C]): SQ.Query[T,C] = {
    
    q match {
      case code"($q0:Query[$t,Any]).count" =>
        //val qp = queryplan.Count(lift(q0))
        //dbg_code"qp:QueryPlan[T]".run
        // ^ TODO more flexible subtyping evidence to congrue into constructors?
        SQ.CountQuery(liftHK/*[t.Typ,C]*/(q0)).asInstanceOf[SQ.Query[T,C]]
      case code"($q0:Query[T,Any]).filter($p)" =>
        SQ.Filter(liftHK(q0),p)
      //case code"(${AccessibleRelation(r)}:Relation[$t]).query" =>
      case code"(${AccessibleRelation(r)}:Relation[T]).queryHK" =>
        SQ.Scan(r)
    }
    
    //???
    
  }
  
  
  
}

package dbstage

import dbstage2.Embedding
import squid.utils._
import dbstage2.Embedding.Predef._
import dbstage2.Embedding.Quasicodes._
import dbstage2.Embedding.ClosedCode

object QueryCompiler {
  
  def compile[T:CodeType](cde: ClosedCode[T]) = {
    lift(cde)
    //liftParametrized(cde) // TODO
  }
  //def liftParametrized[T:CodeType](cde: ClosedCode[T]): Query[T,Any] = cde match {
  //  case code"($x: $xt) => $body:$bt" => new Parametrized[T] {
  //    type Ctx = this.type
  //    val body: Query[T, this.type] = _
  //  }
  //  case _ => lift(cde)
  //}
  // TODO lift nested queries inside the filter predicate...
  def lift[T:CodeType,C](cde: Code[T,C]): Query[T,C] = {
    //println(s"LIFT $cde")
    def liftSource[S:CodeType](cde: Code[QuerySource[S],C]): StagedSource[S,C] = cde match {
      //case code"($qs:QuerySource[$ta]).withFilter($pred)" =>
      //  Filter(liftSource(qs), code"(x:T) => $pred(x)")
      case code"($qs:QuerySource[S]).withFilter($pred)" =>
        Filter(liftSource(qs), pred)
      case code"($qs:QuerySource[S]).naturallyJoining[S,$ta]($r)($pairs)" => // TODO S0<:S ?
        println(qs,r)
        //???
        NaturalJoin(liftSource(qs), r)
      case ClosedCode(cc) =>
        if (!cc.rep.effect.immediate) {
          //if (!(codeTypeOf[S] <:< codeTypeOf[Relation[Any]]))
          if (!(cde.Typ <:< codeTypeOf[Relation[Any]]))
            System.err.println(s"Warning: running unrecognized source $cc")
          From(cc.run, Some(cc))
        }
        else die
    }
    cde match {
      //case code"($qs:QuerySource[$ta]).map[$tb]($f)($ev)" =>
      //  lift(code"($qs:QuerySource[$ta]).flatMap[$tb]($f)($ev):T")
      //case code"($qs:QuerySource[$ta]).flatMap[$tb]($f)($ev)" =>
      //  println(qs,ta,tb)
      case code"($qs:QuerySource[$ta]).map[T]($f)($ev)" =>
        lift(code"$qs.flatMap($f)($ev)")
      case code"($qs:QuerySource[$ta]).flatMap[T]($x => $body)($ev)" =>
        //println(qs,x)
        //val q = lift(body)
        //new FlatMapWith[ta.Typ,T,C](liftSource(qs)) {
        //  val v: x.type = x
        //  val query = lift(body)
        //}
        FlatMapWith(x)(liftSource(qs),lift(body))
      case code"($qs:QuerySource[$ta]).flatMap[T]($f)($ev)" =>
        System.err.println(s"Warning: could not inspect query code: $f")
        val q = liftSource(qs)
        //???
        FlatMap(q, f)
      //case code"$effect; $q:T" => lift(q) // FIXME effect
      case code"$effect; $body:T" => new WithComputation[T,C] {
        type V = Unit
        val v = new Variable[Unit]{ type Ctx = Any } // Note: unsafe usage of Variable type!
        val computation = code"$effect; ()"
        val query = lift(body)
      }
      case code"val $x: $xt = $xv; $body:T" => new WithComputation[T,C] {
        type V = xt.Typ
        val v: x.type = x
        val computation = xv
        val query = lift(body)
      }
      case els => Produce(els) // TODO check not nested type... (no Bag & co.?); check no nest queries! – the whole query can be captured here
    }
  }
  
  
}



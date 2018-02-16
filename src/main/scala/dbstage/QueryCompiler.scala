package dbstage

import squid.utils._
import Embedding.Predef._
import Embedding.Quasicodes._
import Embedding.ClosedCode

object QueryCompiler {
  
  // TOOD: make all operators wrap the result into some Query data type; warn when only partially lifting queries
  
  def compile[T:CodeType](cde: ClosedCode[T]) = {
    val q = lift(cde)
    println(s"Query:\n$q")
    val p = q.directPlan
    println(s"Plan:\n${p}")
    code"() => $p"
    //liftParametrized(cde) // TODO
  }
  //def lift[T:CodeType,C](cde: Code[T,C]): Either[String,Query[T,C]] = {
  /** @throws */
  def lift[T:CodeType,C](cde: Code[T,C]): Query[T,C] = {
    cde match {
      case code"() => $body:T" => // TODO parametrized
        die  
      case code"($qs:DataSource[$ta]).map[T]($f)($ev)" =>
        lift(code"$qs.flatMap($f)($ev)")
      case code"($ds:DataSource[Unit]).flatMap[T]($f)($ev)" if isPure(ds) =>
        lift(code"$f(Unit)")
      case code"($qs:DataSource[$ta]).flatMap[T]($x => $body)($ev)" =>
        FlatMap.build(x)(liftSource(qs),lift(body),liftMonoid(ev))
      case code"($qs:DataSource[$ta]).flatMap[T]($f)($ev)" =>
        die
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
      case els => Produce(els)
    }
    //???
  }
  def liftSource[S:CodeType,C](cde: Code[DataSource[S],C]): StagedSource[S,C] = cde match {
    case code"($qs:DataSource[S]).withFilter($pred)" =>
      //Filter(liftSource(qs), pred)
      liftSource(qs).filter(pred)
    case code"($qs:DataSource[S]).filter($pred)" => liftSource(qs).filter(pred)
    case code"($qs:DataSource[S]).where($pred)"  => liftSource(qs).filter(pred)
    case ClosedCode(cc) =>
      if (!cc.rep.effect.immediate) {
        //if (!(codeTypeOf[S] <:< codeTypeOf[Relation[Any]]))
        if (!(cde.Typ <:< codeTypeOf[StagedDataSource[Any]]))
          System.err.println(s"Warning: running unrecognized source $cc")
        //From(cc.run, Some(cc))
        StagedSource(cc.run,Some(cc),None)
      }
      else die
  }
  
  import cats.Monoid
  def liftMonoid[S:CodeType,C](cde: Code[Monoid[S],C]): StagedMonoid[S,C] = cde match {
    //case code"cats.instances.all.catsKernelStdGroupForInt" => IntMonoid
    case code"($_:cats.kernel.instances.IntInstances).catsKernelStdGroupForInt" =>
      IntMonoid.asInstanceOf[StagedMonoid[S,C]] // FIXME Squid utility for external coercion
    case code"($_:cats.kernel.instances.StringInstances).catsKernelStdMonoidForString" =>
      StringMonoid.asInstanceOf[StagedMonoid[S,C]] // FIXME
    case code"GroupedBag.monoidGroupedBag[$tk,$ta]($asem)" =>
      GroupedBagMonoid[tk.Typ,ta.Typ,C](asem).asInstanceOf[StagedMonoid[S,C]] // FIXME Squid utility for external coercion
    case code"ArgMin.monoid[$tt,$ta]($tord,$asem)" =>
      ArgMinMonoid(tord,asem)
          .asInstanceOf[StagedMonoid[S,C]] // FIXME
    case _ => RawStagedMonoid(cde)
  }
  
  
  //Nil.foldRight()
  //(Nil:Traversable[Int]).foldRight()
  //(_:collection.mutable.ArrayBuffer[Int]).foldRight()
}

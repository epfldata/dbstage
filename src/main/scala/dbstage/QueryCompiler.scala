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
  /** Careful! we match against T sometimes in an invariant position (cf: Monoid[T]), so if T is upcasted we will miss some matches
    * TODO match on tubtype of T...
    * 
    * @throws */
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
      //case code"($qs:DataSource[$ta]).orderBy[$o]($desc)($ord,$proj)" =>
      //case dbg_code"(($qs:$ds where (ds <:< DataSource[ta])):DataSource[$ta]).orderBy[$o]($desc)($ord,$proj)" =>
      case code"(($qs:$ds where (ds <:< DataSource[ta])):DataSource[$ta]).orderBy[$o]($desc)($ord,$proj)" =>
        /* ^ don't just match `$qs:DataSource[$ta]`, because that would recurse with lift[T,C] where T = DataSource[$ta],
         * and later on mismatch with the precise Monoid type being extracted; cf. "class Groups and trait DataSource cannot be matched invariantly" */
        //println(ta,ds,o)
        OrderBy[ta.Typ,o.Typ,C](
          //base debugFor
          lift[ds.Typ,C](qs),ord,proj,desc).asInstanceOf[Query[T,C]] // FIXME
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
  
  // TODO
  //import cats.Semigroup
  //def liftMonoid[S:CodeType,C](cde: Code[Monoid[S],C]): StagedMonoid[S,C] = BagLike.unapply(cde) getOrElse (cde:Code[Any,C]) match {
  //  case 
  //}
  
  import cats.Monoid
  def liftMonoid[S:CodeType,C](cde: Code[Monoid[S],C]): StagedMonoid[S,C] = ((cde:Code[Any,C]) match {
  //def liftMonoid[S:CodeType,C](cde: Code[Monoid[S],C]): StagedMonoid[S,C] = BagLike.unapply(cde) getOrElse ((cde:Code[Any,C]) match {
    //case BagLike(sbl) => sbl
    //case code"cats.instances.all.catsKernelStdGroupForInt" => IntMonoid
    case code"($_:cats.kernel.instances.IntInstances).catsKernelStdGroupForInt" =>
      IntMonoid.asInstanceOf[StagedMonoid[S,C]] // FIXME Squid utility for external coercion
    case code"($_:cats.kernel.instances.StringInstances).catsKernelStdMonoidForString" =>
      StringMonoid.asInstanceOf[StagedMonoid[S,C]] // FIXME
    //case code"GroupedBag.monoidGroupedBag[$tk,$ta]($asem)" =>
    //  GroupedBagMonoid[tk.Typ,ta.Typ,C](asem).asInstanceOf[StagedMonoid[S,C]] // FIXME Squid utility for external coercion
    //case code"BagOrdering.bagLike[$bt,$tt,$ot,False]($choice,$bl,$p,$ord)" =>
    //  //println(choice)
    //  assert(choice =~= code"Choice.FalseChoice")
    //  System.err.println(cde)
    //  ???.asInstanceOf[StagedMonoid[S,C]] // FIXME
    case code"ArgMin.monoid[$tt,$ta]($tord,$asem)" =>
      ArgMinMonoid(tord,asem)
          .asInstanceOf[StagedMonoid[S,C]] // FIXME
    case _ => RawStagedMonoid(cde)
  })
  /*
  //def liftBagLike[S:CodeType,C](cde: Code[Monoid[S],C]): Option[StagedBagLike[S,C]] = (cde:Code[Any,C]) |>? {
  //  case code"BagOrdering.bagLike[$bt,$tt,$ot,False]($choice,$bl,$p,$ord)" =>
  //    liftBagLike(bl).map(_.ord[ot.Typ](p,ord,false))
  //}
  object BagLike {
    //def unapply[S:CodeType,C](cde: Code[Monoid[S],C]) = cde |>? {
    def unapply[S:CodeType,C](cde: Code[Monoid[S],C]) = cde match {
      case code"$bl:BagLike[S,$te]" => 
        //println(s"??????? ${liftBagLike(bl)}")
        liftBagLike(bl)
    //def unapply[C](cde: Code[Any,C]) = cde |>? {
    //  case code"$bl:BagLike[$ts,$te]" => liftBagLike(bl)
      case _ => None
    }
  }
  def liftBagLike[B:CodeType,E:CodeType,C](cde: Code[BagLike[B,E],C]): Option[StagedBagLike[B,E,C]] = (cde:Code[Any,C]) match {
    //case code"BagOrdering.bagLike[$bt,$tt,$ot,False]($choice,$bl,$p,$ord)" =>
    case code"BagOrdering.bagLike[$bt,E,$ot,False]($choice,$bl,$p,$ord)" =>
    //case code"BagOrdering.bagLike[B,E,$ot,False]($choice,$bl,$p,$ord)" =>
    //case code"BagOrdering.bagLike[BagOrdering[B,E,ot,False],E,$ot,False]($choice,$bl,$p,$ord)" =>
      liftBagLike(bl).map(_.ord[ot.Typ](p,ord,false).asInstanceOf[StagedBagLike[B,E,C]])
    case code"GroupedBag2.bagLike[$kt,$vt]($vsem)" =>
      //liftBagLike(bl).map(_.group[kt.Typ,vt.Typ](vsem).asInstanceOf[StagedBagLike[B,E,C]])
      Some(StagedBagGrouping[kt.Typ,vt.Typ,C](vsem).asInstanceOf[StagedBagLike[B,E,C]])
    case _ =>
      //cde match {
      //  case code"BagOrdering.bagLike[$bt,$et,$ot,False]($choice,$bl,$p,$ord)" =>
      //    println(bt,et)
      //    println(bt=:=codeTypeOf[B])
      //    println(et=:=codeTypeOf[E])
      //}
      //System.err.println(s"${codeTypeOf[B]} ${codeTypeOf[E]}")
      System.err.println(cde.toString)
      //???
      None
  }
  */
  
  //Nil.foldRight()
  //(Nil:Traversable[Int]).foldRight()
  //(_:collection.mutable.ArrayBuffer[Int]).foldRight()
}

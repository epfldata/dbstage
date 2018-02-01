package dbstage

import dbstage2.Embedding
import squid.utils._
import dbstage2.Embedding.Predef._
import dbstage2.Embedding.Quasicodes._
import dbstage2.Embedding.ClosedCode

import scala.annotation.unchecked.uncheckedVariance

// TODO unify with class Relation? 
sealed abstract class StagedSource[+T,C] {
//sealed abstract class StagedSource[+T,-C] {
  def plans: List[SimpleQueryPlan[T,C]] = this match {
    case From(r,src) => Scan[T,C](r,src)::Nil
    // TODO:
    //case Filter(q,p) => for {q <- q.plans} yield SimpleSelection[T,C](q,p)
  }
  
  //def map[B](f: ClosedCode[T] => ClosedCode[B]): StagedSource[B,C] =
  /*
  def map[T0>:T,B:CodeType](f: ClosedCode[T0] => ClosedCode[B])(implicit ev:CodeType[T0]): Query[B,C] = // FIXME ev
    new FlatMapWith[T0,B,C](this) {
      //val v: x.type = x
      val v = new Variable[T0]//{type Ctx=Any}//FIXME
      val query = Produce[B,C&v.Ctx](f(v.toCode.unsafe_asClosedCode))
    }
  */
  def map[B:CodeType](f: ClosedCode[T] => ClosedCode[B])(implicit ev:CodeType[T @uncheckedVariance]): Query[B,C] = // FIXME ev
    //new FlatMapWith[T,B,C](this) {
    //  //val v: x.type = x
    //  val v = new Variable[T]//{type Ctx=Any}//FIXME
    //  val query = Produce[B,C&v.Ctx](f(v.toCode.unsafe_asClosedCode))
    //}
    {
      val v = new Variable[T]
      FlatMapWith(v)(this,Produce[B,C&v.Ctx](f(v.toCode.unsafe_asClosedCode)))
    }
  def flatMap[B:CodeType](f: ClosedCode[T] => Query[B,C])(implicit ev:CodeType[T @uncheckedVariance]): Query[B,C] = // FIXME ev
    //new FlatMapWith[T,B,C](this) {
    //  val v = new Variable[T]//{type Ctx=Any}//FIXME
    //  val query = f(v.toCode.unsafe_asClosedCode)
    //}
    {
      val v = new Variable[T]
      FlatMapWith(v)(this,f(v.toCode.unsafe_asClosedCode))
    }
}
sealed abstract class Query[T,-C] {
  //def run(implicit ev: C <:< {}): Iterable[T] = ???  // TODO
  def plans = Query.plansOf(this)
}
object Query {
  def plansOf[T,C](q: Query[T,C]): List[QueryPlan[T,C]] = q match {
    //case From(r,src) => Scan(r,src)::Nil
    //case fmw @ FlatMapWith(src,v,q) => 
    case fmw: FlatMapWith[a,T,C] => val (src,v,q) = (fmw.src, fmw.v, fmw.query)
      for {
        vp <- src.plans
        qp <- q.plans
      //} yield Join[T,C](vp,qp)
      } yield ???
      //???
  }
}
//case class From[T,C](r: Relation[T]) extends Query[T,C] {
case class From[+T,C](rel: QuerySource[T], src: Option[ClosedCode[QuerySource[T]]]) extends StagedSource[T,C] {
//case class From[+T](r: QuerySource[T], src: Option[ClosedCode[QuerySource[T]]]) extends StagedSource[T,Any] {
  //def run = ???
  override def toString = s"From(${src.fold(rel.toString)(cde => cde.rep|>base.showRep)})"
}
//case class Where[+T,C](cond: Code[Bool,C]) extends StagedSource[T,C] {
case class Where(cond: ClosedCode[Bool]) extends StagedSource[Unit,Any] {
  
}
////case class CountQuery[T<:Record](r: Query[T,C]) extends Query[Count::NoFields] {
//case class Fold[T,C](r: Query[T,C]) extends Query[T,C] {
//  //def run = ???
//}

//case class Filter[T<:Record](q: Query[T,C], pred: T => Bool) extends Query[T,C] {
//case class Filter[T,C](q: StagedSource[T,C], pred: Code[T => Bool, C]) extends StagedSource[T,C] {
////abstract case class Filter[T,C](q: StagedSource[T,C], pred: PreciseCode[T => Bool, C]) extends StagedSource[T,C] {
//  //def run = ???
//}
//object Filter {
//  def apply[T,C](q: StagedSource[T,C], pred: PreciseCode[T => Bool, C]): StagedSource[T,C] = q match {
//    //case Filter(q0,pred0) => Filter(q0, code"(x:T) => $pred0(x) && $pred(x)")
//    //case Filter(q0, code"($x:$t) => $pred0") => 
//    //  //Filter(q0, code"($x => $pred0 && $pred($x.asInstanceOf[T]))")
//    //  Filter[t.Typ,C](q0, code"($x => $pred0 && $pred(${x.asInstanceOf[Code[T,C]]}))")
//    //case f:Filter[t,C] =>
//    //  //type S = f.pred.Typ
//    //  //implicit val S = f.pred.Typ
//    //  //Filter(f.q, code"(x:S) => ${f.pred}(x) && $pred(x)")
//    //  Filter[T,C](f.q, code"(x:${f.pred.Typ}) => ${f.pred}(x) && $pred(x)")
//    case _ => new Filter(q,pred){}
//  }
//}
abstract class Filter[T,C](val src: StagedSource[T,C]) extends StagedSource[T,C] {
  val v: Variable[T]
  val pred: Code[Bool, C & v.Ctx]
  
  override def toString: String = s"$src WHERE ${v.rep|>base.showRep} => ${pred.rep|>base.showRep}"
  //override def toString: String = s"$src WHERE ${
  //  val w:v.type=v
  //  code"($w => $pred)".rep|>base.showRep}"
}
object Filter {
  def apply[T:CodeType,C](s: StagedSource[T,C], pred: Code[T => Bool, C]): StagedSource[T,C] = pred match {
    case code"($x => $body)" => Filter(x)(s, body)
    case _ => 
      val x = new Variable[T]
      Filter(x)(s, code"$pred($x)")
  }
  def apply[T,C](x: Variable[T])(s: StagedSource[T,C], p: Code[Bool, C & x.Ctx]): StagedSource[T,C] = s match {
    case f: Filter[T,C] =>
      val v: f.v.type = f.v
      //println(code"(${v} => ${f.pred})":Code[T=>Bool,C])
      //val p = code"(${v} => ${f.pred})":Code[T=>Bool,C]
      Filter(v)(f.src, code"${f.pred} && ${x.substitute[Bool,C & v.Ctx](p, v.toCode)}")
    case _ => new Filter[T,C](s) { val v: x.type = x; val pred = p }
  }
}


// It doesn't actually seem possible to have a generic Join impl that can be reordered
// Instead, it seems it would make more sense to do the reordering in a direct manner while constructing the possible plans

//case class Join[A,B,C](lhs: Query[A,C], rhs: Query[B,C], pred: (A,B) => Bool) extends Query[(A,B),C] {
//case class Join[A,B,C](lhs: StagedSource[A,C], rhs: Query[B,C], pred: (A,B) => Bool) extends Query[(A,B),C] {
//  //def run = ???
//}
//abstract class Join[A,B,C](lhsv: Variable[A], rhsv: Variable[B]) extends Query[(A,B),C] {
//  val lhs: StagedSource[A,C & lhsv.Ctx]
//  val rhs: Query[B,C & lhsv.Ctx]
//  val pred: Code[Bool, C & lhsv.Ctx & rhsv.Ctx]
//}
//abstract class Join[A,B,C](val src: StagedSource[A,C], val v: Variable[B]) extends Query[B,C] {
//  val rhs: Query[B, C & v.Ctx]
//  val pred: Code[Bool, C & v.Ctx]
//}

//abstract class Join[A,B,C](val src: StagedSource[A,C]) extends Query[B,C] {
//  val v: Variable[B]
//  //val rhs: Query[B, C & v.Ctx]
//  val rhs: Query[B, C]
//  val pred: Code[Bool, C & v.Ctx]
//}
abstract class Join[A,B,C](val src: StagedSource[A,C]) extends Query[B,C] {
  val lhsv: Variable[A]
  val rhsv: Variable[B]
  //val rhs: Query[B, C & v.Ctx]
  val rhs: Query[B, C & lhsv.Ctx]
  val pred: Code[Bool, C & lhsv.Ctx & rhsv.Ctx]
}

//abstract class Join[A,B,D,C](val lhs: StagedSource[A,C], val rhs: StagedSource[B,C]) extends Query[D,C] {
//  val lhsv: Variable[A]
//  val rhsv: Variable[B]
//  val query: Query[D, C & lhsv.Ctx & rhsv.Ctx]
//  //override def toString = s"Join(${lhsv.rep|>base.showRep},${rhsv.rep|>base.showRep})" +
//  //  s"\n${indentString(lhs.toString)}\n${indentString(rhs.toString)}\n${indentString(query.toString)}"
//  override def toString = s"Join(${lhsv.rep|>base.showRep} = $lhs, ${rhsv.rep|>base.showRep} = $rhs)\n${indentString(query.toString)}"
//    //s"\n${indentString(s"${lhsv.rep|>base.showRep} = $lhs")}\n${indentString(rhs.toString)}\n${indentString(query.toString)}"
//}
//object Join {
//  def apply[A,B,D,C]
//  (lhs: StagedSource[A,C], rhs: StagedSource[B,C])
//  (_lhsv: Variable[A], _rhsv: Variable[B])
//  (_query: Query[D, C & _lhsv.Ctx & _rhsv.Ctx]): Query[D,C] = new Join[A,B,D,C](lhs, rhs) {
//    val lhsv: _lhsv.type = _lhsv
//    val rhsv: _rhsv.type = _rhsv
//    val query = _query
//  }
//}
//case class OrderBy[A,C](q: Query[A,C], cmp: Code[Ordering[A],C]) extends Query[A,C] {
//  //def run = ???
//}
case class FlatMap[A,B,C](q: StagedSource[A,C], f: Code[A => B,C]) extends Query[B,C] {
  //def run = ???
}
abstract class WithComputation[T,C] extends Query[T,C] {
  type V
  val v: Variable[V]
  val computation: Code[V,C]
  val query: Query[T, C & v.Ctx]
  override def toString = s"<$v=$computation; $query>"
}
abstract class FlatMapWith[A,B,C](val src: StagedSource[A,C]) extends Query[B,C] {
  val v: Variable[A]
  val query: Query[B, C & v.Ctx]
  //override def toString = s"ProjectionW($q, $v => $query)"
  //override def toString = s"FlatMap(\n\t$q,\n\t$v => $query\n)"
  //override def toString = s"FlatMap(\n${indentString(src.toString)},\n${
  //  indentString(s"$v =>\n${indentString(query.toString)}")
  //}\n)"
  override def toString = s"FlatMap\n${indentString(s"${v.rep|>base.showRep} = $src")}\n${indentString(query.toString)}"
  //def inspectSrc(): Option[()] = src match {
  //  case fi: Filter[A,C] =>
  //}
  //def inspectSrc(): (Option[]) = src match {
}
object FlatMapWith {
  def build[A,B,C](x: Variable[A])(src: StagedSource[A,C], qu: Query[B,C & x.Ctx]): FlatMapWith[A,B,C] { val v: x.type } =
    new FlatMapWith[A,B,C](src) {
      val v: x.type = x
      val query: Query[B,C & v.Ctx] = qu
    }
  def apply[A,B,C](x: Variable[A])(src: StagedSource[A,C], qu: Query[B,C & x.Ctx]): Query[B,C] = qu match {
    case fmw: FlatMapWith[a,B,C & x.Ctx] 
      if false 
    =>
      type A0 = a
      // TODO handle filtered source
      //println(fmw)
      //fmw.v.tryClose()
      
      fmw.src match {
        //case fi: Filter[a,C @unchecked] =>
        case fi: Filter[A0,C @unchecked] =>
          fi.src match {
            //case fr: From[`a`,C] =>
            case fr: From[A0,C] =>
              //new Join[A,B,C](src,fi.v) {
              //  val rhs: Query[B, C & v.Ctx] = ???
              //  val pred: Code[Bool, C & v.Ctx] = ???
              //}
        
              // compiler stack overflow:
              //new Join[A,a,B,C](src, From[a,C](fr.r,None)) {
              //  val lhsv: Variable[A] = ???
              //  val rhsv: x.type = ???
              //  val query: Query[B, C & lhsv.Ctx & rhsv.Ctx] = ???
              //}
        
              ////val rhsv = new Variable[a]
              ////Join[A,a,B,C](src, From[a,C](fr.r,None))(x, ???)(???)
              //Join[A,a,B,C](src, fr)(x, fmw.v)(fmw.query) // FIXME missing filter
              
              //new Join[A,B,C](src) {
              //  val v: fi.v.type = fi.v // no
              //  val rhs: Query[B, C & v.Ctx] = ??? //FlatMapWith()
              //  val pred: Code[Bool, C & v.Ctx] = fi.pred
              //}
              //fi.v:Int
              new Join[A,B,C](src) {
                //val lhsv: fi.v.type = fi.v
                //val lhsv: Variable[A] = ???
                val lhsv: x.type = x
                lazy val rhsv: Variable[B] = ???
                lazy val rhs: Query[B, C & lhsv.Ctx] = ??? //FlatMapWith()
                val pred: Code[Bool, C & lhsv.Ctx & rhsv.Ctx] = fi.v.tryClose(fi.pred).getOrElse(throw new AssertionError(s"Some ${fi.v} in ${fi.pred}"))
              }
              
      
          }
        //case fi: Filter[a,C @unchecked] =>
      }
      
      //new Join[A,a,B,C](src, fmw.src) {
      //  val lhsv: Variable[A] = ???
      //  val rhsv: x.type = ???
      //  val query: Query[B, C & lhsv.Ctx & rhsv.Ctx] = ???
      //}
      
      //???
    case _ => build(x)(src,qu)
  }
  def unapply[A,B,C](fmw: FlatMapWith[A,B,C]): Some[(StagedSource[A,C], fmw.v.type,Query[B, C & fmw.v.Ctx])] = Some(fmw.src,fmw.v,fmw.query)
}
case class NaturalJoin[A,B,C](q: StagedSource[A,C], r: Code[B,C]) extends StagedSource[A,C] {
  
}
case class Produce[T,C](r: Code[T,C]) extends Query[T,C]

// For compiling parametrized queries only once
abstract class Parametrized[A] extends Query[A,Any] {
  type Ctx
  val body: Query[A,Ctx]
  def close(cde: Code[A,Ctx]): ClosedCode[A]
}

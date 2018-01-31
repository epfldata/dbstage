package dbstage2

// Taken from squid/experimental; TODO have it in main Squid

import squid.utils._

import Embedding._
//import Embedding.Predef._

/*
// Make this a trait?
class Variable[Typ:CodeType](name: String = "x") { vari =>
  type Ctx
  protected val bound: BoundVal = bindVal(name, typeRepOf[Typ], Nil) // TODO annot?
  def toCode: Code[Typ,Ctx] = Code(readVal(bound))
  //def withValue(v: Typ) = new AssignmentBase {
  //  type Ctx = vari.Ctx
  //  type Abstracted[T] = Typ => T
  //  def abstracted[T,D](pgrm: Code[T,Ctx with D]): Code[Abstracted[T], D] =
  //    IR(lambda(bound::Nil, pgrm.rep))
  //  protected def applyAbstracted[T](abs: Abstracted[T]): T = abs(v)
  //  override def toString = s"Valuation($bound = $v)"
  //}
  //def -> (v: Typ) = withValue(v)
  object ref {
    def apply[T](): Code[Typ,Ctx] = toCode
    def unapply[T](x:AnyCode[T]): Bool = {
      import base._
      x.rep match {
        case RepDef(`bound`) => true
        case RepDef(Ascribe(r,t)) => unapply(r)
        case _ => false
      }
    }
  }
}
object Variable {
  import scala.language.implicitConversions
  implicit def toCode[T](fv: Variable[T]): Code[T,fv.Ctx] = fv.toCode
  // TODO:
  //def unapply[T,C](x: Code[T,C]): Option[Variable[T,C]] = ???
  //def of[T](bound: BoundVal) = new Variable[T](bound)
}
*/

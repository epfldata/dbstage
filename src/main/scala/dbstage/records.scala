package dbstage

import scala.annotation.implicitNotFound
import squid.utils._
import cats.Monoid
import cats.Semigroup
import cats.syntax.all._
import squid.lib.transparencyPropagating
import squid.quasi.{phase, embed}

case class ~[A,B](lhs: A, rhs: B) {
  override def toString: String = rhs match {
    case ~(a,b) => s"$lhs ~ ($rhs)"
    case _ => s"$lhs ~ $rhs"
  }
}
object ~ {
  @transparencyPropagating
  implicit def monoid[A:Monoid,B:Monoid]: Monoid[A ~ B] =
    Monoid.instance(Monoid[A].empty ~ Monoid[B].empty)((x,y) => (x.lhs |+| y.lhs) ~ (x.rhs |+| y.rhs))
  @transparencyPropagating
  implicit def semigroup[A:Semigroup,B:Semigroup]: Semigroup[A ~ B] =
    Semigroup.instance((x,y) => (x.lhs |+| y.lhs) ~ (x.rhs |+| y.rhs))
}

final class RecordSyntax[A](private val self: A) extends AnyVal {
  def ~ [B] (that: B) = new ~(self,that)
  /** Note that when defined, this is equivalent to project; but project can additionally reorder fields */
  def select[B](implicit ev: A CanAccess B): B = ev(self)
  // TODO: a selectFirst that is left-biased; use in apply? -- and a selectLast; reformulate RecordAccess in terms of these two?
  def project[B](implicit ev: A ProjectsOn B): B = ev(self)
  /** shortcut for project */
  def p[B](implicit ev: A ProjectsOn B): B = project(ev)
  //def apply[F](implicit access: CanAccess[A,F]): F#Typ = access(self).value
  def apply[F](implicit accessF: CanAccess[A,F], ws: WrapsBase[F]): ws.Typ = apply(ws.instance)
  def apply[F,V](w: F Wraps V)(implicit accessF: A CanAccess F): V = accessF(self) |> w.deapply
  
  //def map[F,V](w: F Wraps V)(f: V => V)(implicit accessF: A CanAccess F): A = ???
  // TODO ^ CanAccess needs a deapp method; better: a generic replacement capability that can change the field type...
  
  // works well:
  //def focus[F,V](w: F Wraps V)(implicit accessF: CanAccess[A,F]) = new FocusSyntax[A,F,V](self, accessF, w)
  
}
// works well:
//final class FocusSyntax[A,F,V](self: A, accessF: CanAccess[A,F], w: F Wraps V) {
//  def map(f: V => V): A = ???
//}

// TODO rename to `contains`?
@implicitNotFound("Type ${R} is not known to be accessible in ${A}")
case class CanAccess[A,R](fun: A => R) extends (A => R) { def apply(a: A) = fun(a) } // TODO rem indirection; make abstract class?
@embed
object CanAccess {
  @desugar
  implicit def fromLHS[A,B,T](implicit ev: A CanAccess T): (A ~ B) CanAccess T = CanAccess(ev compose (_.lhs))
  @desugar
  implicit def fromRHS[A,B,T](implicit ev: B CanAccess T): (A ~ B) CanAccess T = CanAccess(ev compose (_.rhs))
  @desugar
  implicit def fromT[T]: T CanAccess T = CanAccess(identity)
}

//trait ProjectsOnVar[-A,+B] extends (A => B)  // implicit search doesn't find it

@implicitNotFound("Type ${A} cannot be projected onto type ${B}")
case class ProjectsOn[-A,B](fun: A => B) extends (A => B) { def apply(a: A) = fun(a) }
//case class ProjectsOn[A,B](fun: A => B) extends (A => B) with ProjectsOnVar[A,B] { def apply(a: A) = fun(a) }
@embed
object ProjectsOn extends ProjectLowPrio {
  //implicit object projectUnit extends Project[Any,Unit](_ => ())  // TODO make Project contravariant?
  @desugar
  implicit def projectUnit[T] = ProjectsOn[T,Unit](_ => ())
  @desugar
  implicit def projectLHS[A,B,T](implicit ev: A CanAccess T): (A ~ B) ProjectsOn T = ProjectsOn(ev compose (_.lhs))
  @desugar
  implicit def projectRHS[A,B,T](implicit ev: B CanAccess T): (A ~ B) ProjectsOn T = ProjectsOn(ev compose (_.rhs))
  //implicit def projectBoth[A,B,T](implicit evLHS: T Project A, evRHS: T Project B): T Project (A ~ B) = Project(t => evLHS(t) ~ evRHS(t))
  
  @desugar
  implicit def projectSet[A,B](implicit ev: A ProjectsOn B): Set[A] ProjectsOn Set[B] = ProjectsOn(_ map ev)
  
}
@embed
class ProjectLowPrio extends ProjectLowPrio2 {
  @desugar
  implicit def projectBoth[A,B,T](implicit evLHS: T ProjectsOn A, evRHS: T ProjectsOn B): T ProjectsOn (A ~ B) =
    ProjectsOn(t => evLHS(t) ~ evRHS(t))
}
@embed
class ProjectLowPrio2 {
  @desugar
  implicit def projectT[T]: T ProjectsOn T = ProjectsOn(identity)
}


/** Type class for finding the normal form AN of record type A; ie. where all ~ are left-associated. */
case class Normalizes[AN,A](fun: A => AN) extends (A => AN) { def apply(a:A) = fun(a) }

@embed object Normalizes extends NormalizesLowPriority0 {
  @desugar implicit def reassoc[A,B,C,ABCN](implicit norm: ABCN Normalizes (A ~ B ~ C)): ABCN Normalizes (A ~ (B ~ C)) =
    Normalizes(abc => norm(abc.lhs ~ abc.rhs.lhs ~ abc.rhs.rhs))
}
@embed class NormalizesLowPriority0 extends NormalizesLowPriority1 {
  @desugar implicit def propagate[A,AN,B,BN,ABN]
  (implicit normA: AN Normalizes A, normB: BN Normalizes B, normAB: ABN Normalizes (AN ~ BN)): ABN Normalizes (A ~ B) =
    Normalizes(ab => normAB(normA(ab.lhs) ~ normB(ab.rhs)))
}
@embed class NormalizesLowPriority1 {
  @desugar implicit def isNormal[A]: A Normalizes A = Normalizes(identity)
}

/** A pair of two values of the same type F; we use a type member instead of a type parameter because Squid does not
  * currently support existentials, as in `List[FieldPair[_]]`. */
sealed abstract class FieldPair {
  type F
  val l: F; val r: F
  override def toString: String = s"<$l,$r>"
}
case class FieldPairImpl[F0](l: F0, r: F0) extends FieldPair { type F = F0 }
object FieldPair {
  @transparencyPropagating
  def apply[F](l: F, r: F): FieldPair = FieldPairImpl(l,r)
  def unapply(fp: FieldPair): Some[(fp.F,fp.F)] = Some(fp.l,fp.r)
}

/** Implicit for accumulating a list of pairs of fields with the same type in both L and R. */
class PairUp[L,R](val ls: (L,R) => List[FieldPair])

@embed object PairUp extends PairUpLowPriority0 {
  @desugar implicit def pairUpNorm[L,R,RN](implicit norm: RN Normalizes R, pu: PairUpNorm[L,RN]): PairUp[L,R] =
    new PairUp((l,r) => pu.ls(l,norm(r)))
}

/** Same as PairUp, but only works when R is in normal form (for implicit resolution performance reasons). */
class PairUpNorm[L,R](val ls: (L,R) => List[FieldPair])

@embed object PairUpNorm extends PairUpLowPriority0 {
  @desugar def apply[L,R](ls: (L,R) => List[FieldPair]) = new PairUpNorm(ls)
  @desugar implicit def isPair[T,H](implicit acc: T CanAccess H): PairUpNorm[T,H] =
    PairUpNorm((t0,t1) => FieldPair[H](acc(t0),t1)::Nil)
}
@embed class PairUpLowPriority0 extends PairUpLowPriority1 {
  @desugar implicit def pairHead[L,H,T](implicit pu: PairUpNorm[L,T], acc: L CanAccess H): PairUpNorm[L,T~H] =
    PairUpNorm((l,ht) => FieldPair(acc(l),ht.rhs) :: pu.ls(l,ht.lhs))
}
@embed class PairUpLowPriority1 extends PairUpLowPriority2 {
  @desugar implicit def pairNoHead[L,H,T](implicit pu: PairUpNorm[L,T]): PairUpNorm[L,T~H] =
    PairUpNorm((l,ht) => pu.ls(l,ht.lhs))
}
@embed class PairUpLowPriority2 {
  @desugar implicit def pairNone[L,T]: PairUpNorm[L,T] = PairUpNorm((l,r) => Nil)
}

// Works, but impractically slow for big record types!!!
/*
@embed object PairUp extends PairUpLowPriority {
  @transparencyPropagating
  def apply[L,R](ls: (L,R) => List[FieldPair]) = new PairUp(ls)
  @desugar implicit def isPair[T]: PairUp[T,T] =
    PairUp((t0,t1) => FieldPair[T](t0,t1)::Nil)
}
@embed class PairUpLowPriority extends PairUpLowPriority1 {
  @desugar implicit def decomposeRight[L,R0,R1](implicit pairs0: PairUp[L,R0], pairs1: PairUp[L,R1]): PairUp[L,R0~R1] =
    PairUp((l,r) => pairs0.ls(l,r.lhs) ++ pairs1.ls(l,r.rhs))
}
@embed class PairUpLowPriority1 extends PairUpLowPriority2 {
  @desugar implicit def decomposeLeft[L0,L1,R](implicit pairs0: PairUp[L0,R], pairs1: PairUp[L1,R]): PairUp[L0~L1,R] =
    PairUp((l,r) => pairs0.ls(l.lhs,r) ++ pairs1.ls(l.rhs,r))
}
@embed class PairUpLowPriority2 {
  @desugar implicit def doesNotHavePair[L,R]: PairUp[L,R] =
    PairUp((_,_) => Nil)
}
*/

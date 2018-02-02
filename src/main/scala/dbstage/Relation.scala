package dbstage
import dbstage2.{FieldModule, ::, Project, Record, Access, PairUp, NoFields}
import dbstage2.example.RecordTestsDefs._
import dbstage2.example._
import squid.utils._
import cats.{Semigroup, Monoid}
import dbstage2.FieldPair
import squid.lib.transparencyPropagating
import dbstage2.Embedding.Predef.{CodeType,codeTypeOf}
//import cats._
//import cats.instances.tuple
//import cats.instances.all
import cats.implicits._
import shapeless.the

//abstract class Relation[+A:CodeType] extends QuerySource[A] {
abstract class Relation[A:CodeType] extends QuerySource[A] {
  def staged = From(this, None)
  
  import dbstage2.Embedding.Predef.implicitType
  
  //val primaryKeys: Set[] = Set()
  def withPrimaryKey[K:CodeType](implicit p: A Project (K::NoFields)) = withPrimaryKeys[K::NoFields]
  def withPrimaryKeys[Ks:CodeType](implicit p: A Project Ks) = Relation[A]//(primaryKeyTypes + codeTypeOf[Ks])
  //def withForeighKey[K<:FieldModule] = new withForeighKey[K::NoFields]
  
  //override def toString = s"Relation[${codeTypeOf[A].rep.tpe}]@${System.identityHashCode(this).toHexString}"
  override def toString = s"Relation@${System.identityHashCode(this).toHexString}"
}
object Relation {
  def apply[A:CodeType] = new Relation[A] { def iterator = ??? }
  def of[A:CodeType](it: Iterable[A]) = new Relation[A] { def iterator = it.iterator }
}

sealed trait QuerySource[+A] { self =>
  def iterator: Iterator[A]
  //private def qr[B:QueryResult] = the[QueryResult[B]]
  @transparencyPropagating
  def map[B:Monoid](f: A => B): B = flatMap(f)
  @transparencyPropagating
  def flatMap[B:Monoid](f: A => B): B = iterator.map(f).fold(Monoid[B].empty)(Monoid[B].combine)
  @transparencyPropagating
  def withFilter(pred: A => Bool): QuerySource[A] = new QuerySource[A] { def iterator = self.iterator.filter(pred) }
  
  // TODO warning or error if no common fields found!
  // TODO join predicate
  @transparencyPropagating
  def naturallyJoining[A0>:A,B](b: B)(implicit pairs: A0 PairUp B): QuerySource[A] = {
    //println(pairs)
    //this
    new QuerySource[A] {
      def iterator = self.iterator.filter{ a =>
        //println(s"? $a $b ${pairs.ls(a,b)}")
        pairs.ls(a,b).forall{case FieldPair(x,y)=>x===y}
      }
    }
  }
}

//case class GroupedBag[A,B](as: A*)(bs: B*) {
class GroupedBag[A,B](val mapping: Map[A,B]) {  // FIXME use ordering!
  val sorting: Option[Ordering[A]] = None
  def orderBy[F<:FieldModule](implicit acc: F Access A, ord: Ordering[F#Typ]): GroupedBag[A,B] = new GroupedBag[A,B](mapping) {
    //override val sorting = Some(the[Ordering[F#Typ]])
    override val sorting = Some(Ordering.by(acc.fun))
  }
  override def toString = s"{${mapping mkString ", "}}"
}
object GroupedBag {
  def apply[A,B](a:A)(b:B) = new GroupedBag(Map(a->b))
  implicit def semigroupBag[A,B:Monoid]: Monoid[GroupedBag[A,B]] =
    Monoid.instance[GroupedBag[A,B]](new GroupedBag(Map.empty)){ (xs,ys) => 
      new GroupedBag(xs.mapping ++ ys.mapping.map { 
        case (k,v) => k -> xs.mapping.get(k).fold(v)(v0 => Monoid[B].combine(v,v0)) }) } // FIXedME proper merger
}
case class Bag[A](xs: A*) { //extends QueryResult[Bag[A]] {
  def orderBy[F<:FieldModule](implicit acc: F Access A, ord: Ordering[F#Typ]): SortedBag[A] = 
    //SortedBag.ofValues(xs map acc.fun: _*)
    SortedBag.ofValues(xs:_*)(Ordering.by(acc.fun))
  // or name: orderByMulti
  def orderBySeveral[R](implicit proj: A Project R): SortedBag[A] = ??? // TODO request congrued Ordering – have a generic way to do that!
  def sortBy[B](f: A => B)(implicit ord: Ordering[B]): SortedBag[A] = ???
  def nonEmpty = xs.nonEmpty
  
  //def groupBy[F<:FieldModule](implicit acc: F Access A): Bag[A] = ???
  def groupBy[R](r: R)(implicit mon: Monoid[A]): GroupedBag[R,A] = 
    Monoid[GroupedBag[R,A]].combineAll(xs.map(x => GroupedBag(r)(x)))
  
  override def toString = s"{${xs mkString ", "}}"
}
object Bag {
  implicit def semigroupBag[A]: Monoid[Bag[A]] =
    new Monoid[Bag[A]] {
      def empty = Bag[A]()
      def combine(a: Bag[A], b: Bag[A]): Bag[A] = Bag(a.xs ++ b.xs : _*)
    }
}
////case class SortedBag[A:Ordering](xs: List[A]) { def ord = the[Ordering[A]] }
//case class SortedBag[A](xs: A*)(implicit val ord: Ordering[A])
//object SortedBag {
//  implicit def semigroupSortedBag[A]: Monoid[SortedBag[A]] =
//    //Monoid.instance(_.xs ++ _.xs |> (_.sorted()) |> SortedBag)
//    //Monoid.instance((a,b) => (a.xs ++ b.xs).sorted(a.ord) |> SortedBag.apply)
//    //Monoid.instance{(a,b) => import a.ord; (a.xs ++ b.xs).sorted |> SortedBag.apply } // No implicit Ordering defined for A. – ???
//    //Monoid.instance{(a,b) => import a.ord; SortedBag((a.xs ++ b.xs).sorted) }
//    new Monoid[SortedBag[A]] {
//      def empty = SortedBag[A]()
//      def combine(a: SortedBag[A], b: SortedBag[A]): A = {import a.ord; SortedBag((a.xs ++ b.xs).sorted) }
//    }
//}
//abstract class SortedBag[A] { val xs: Iterable[A] }
class SortedBag[+A](val xs: Iterable[A]) {
  //def ++ (that: SortedBag[A]) = (this,that) match {
  def ++ [B>:A] (that: SortedBag[B]) = (this,that) match {
    case (_,EmptySortedBag) => this
    case (EmptySortedBag,_) => that
    case (a:NonEmptySortedBag[A],b:NonEmptySortedBag[A]) => 
      //NonEmptySortedBag(a.head, b.head +: (a.rest ++ b.rest))
      import a.ord
      val hd +: tl = a.head +: b.head +: (a.rest ++ b.rest) sorted;
      NonEmptySortedBag(hd, tl: _*)
  }
  //override def toString = s"SortedBag(${xs mkString ", "})"
  //override def toString = s"[${xs mkString ", "}]"
  override def toString = {
    val rows = xs mkString ", "
    if (rows.length > 120) s"[\n${indentString(xs mkString ",\n")}\n]"
    else s"[$rows]"
  }
}
case object EmptySortedBag extends SortedBag[Nothing](Nil)
case class NonEmptySortedBag[A](head: A, rest: A*)(implicit val ord: Ordering[A]) extends SortedBag[A](head +: rest)
object SortedBag {
  def apply[A](): EmptySortedBag.type = EmptySortedBag
  def apply[A](head: A, rest: A*)(implicit ord: Ordering[A]): NonEmptySortedBag[A] = NonEmptySortedBag(head, rest: _*)
  def ofValues[A:Ordering](xs: A*) = xs match {
    case Seq() => EmptySortedBag
    case hd +: tl => NonEmptySortedBag(hd, tl: _*)
  }
  implicit def semigroupSortedBag[A]: Monoid[SortedBag[A]] =
    //Monoid.instance(_.xs ++ _.xs |> (_.sorted()) |> SortedBag)
    //Monoid.instance((a,b) => (a.xs ++ b.xs).sorted(a.ord) |> SortedBag.apply)
    //Monoid.instance{(a,b) => import a.ord; (a.xs ++ b.xs).sorted |> SortedBag.apply } // No implicit Ordering defined for A. – ???
    //Monoid.instance{(a,b) => import a.ord; SortedBag((a.xs ++ b.xs).sorted) }
    new Monoid[SortedBag[A]] {
      def empty = SortedBag[A]()
      //def combine(a: SortedBag[A], b: SortedBag[A]): A = {import a.ord; SortedBag((a.xs ++ b.xs).sorted) }
      def combine(a: SortedBag[A], b: SortedBag[A]): SortedBag[A] = a ++ b
    }
  implicit def semigroupNonEmptySortedBag[A]: Monoid[NonEmptySortedBag[A]] =
    semigroupSortedBag[A].asInstanceOf[Monoid[NonEmptySortedBag[A]]]
}

case class Count(n: Int = 1)   // TODO actually make it a Field, and inherit Field's Monoid? (if it works...)
object Count {
  implicit val semigroupCount: Monoid[Count] = 
    //implicitly[Monoid[Int]].
    //Monoid.instance(_.n + _.n |> Count.apply)
    new Monoid[Count] {
      def empty = Count(0)
      def combine(a: Count, b: Count): Count = Count(a.n + b.n)
    }
}



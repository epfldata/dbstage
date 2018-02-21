package dbstage

import squid.utils._
import cats.{Semigroup, Monoid}
import cats.syntax.semigroup._
import cats.instances.unit._
import cats.instances.map._
import squid.lib.transparencyPropagating
import squid.quasi.overloadEncoding
import squid.quasi.{phase, embed}

import scala.collection.immutable.SortedMap


@embed
class MonoidSyntax[A](private val self: A)(implicit mon: A |> Monoid) {
  //@phase('Sugar)
  def pairWith[B](f: A => B) = self ~ f(self)
  //@phase('Sugar)
  def whereMin[T:Ordering](t:T): ArgMin[T,A] = ???
  //@phase('Sugar)
  //def groupBy[B](that: B) = GroupedBag(Map(that -> self),None,None,None,None)
  //@phase('Sugar)
  //def groupByAndSelect[B](that: B) = GroupedBag(Map(that -> that ~ self),None,None,None,None)
}
@embed
class SemigroupSyntax[A](private val self: A)(implicit sem: A |> Semigroup) {
  //@phase('Sugar)  // exposes private value `self`
  @transparencyPropagating
  def groupBy[B](that: B) = GroupedBag(Map(that -> self),None,None,None,None)
  @transparencyPropagating
  def groupingBy[B](that: B) = Groups(Map(that -> self))
}

// TODO have a reified Bool type parameter saying whether it should be displayed as a Min or a Max
sealed abstract class ArgMin[+T,+A]  // TODO change to Option[ArgMin] with only one ArgMin ctor?
case class SomeMin[T,A](res: T, a: A) extends ArgMin[T,A] {
  override def toString = a match {
    case () => s"Min($res)"
    case _  => s"ArgMin($res ~> $a)"
  }
}
case object NoMin extends ArgMin[Nothing,Nothing]
object ArgMin {
  @transparencyPropagating
  implicit def monoid[T:Ordering,A:Semigroup]: Monoid[ArgMin[T,A]] =
    Monoid.instance(NoMin:ArgMin[T,A]){ (x, y) =>
      x -> y match {
        case NoMin -> NoMin => NoMin
        case NoMin -> a => a
        case a -> NoMin => a
        case SomeMin(r0,a0) -> SomeMin(r1,a1) => Ordering[T].compare(r0,r1) match {
          case 0 => SomeMin(r0, a0 |+| a1)
          case n if n > 0 => y
          case n if n < 0 => x
        }
      }
    }
}
object Min {
  def apply[T,A](t:T,a:A): ArgMin[T,A] = SomeMin(t,a)
  def apply[T](t:T): ArgMin[T,Unit] = SomeMin(t,())
}

class Count(val n: Int) {
  override def toString: String = s"Count(${n})"
}
//@embed // overloading doesn't really work with @embed
object Count extends (Count Wraps Int) {
  //@phase('Sugar)
  //def dflt = true
  @transparencyPropagating
  //@phase('Sugar)
  @overloadEncoding // to avoid references to Count.apply$default$1
  def apply(where: Bool = true): Count = Count(if (where) 1 else 0)
  protected def applyImpl(v: Int) = new Count(v)
  protected def deapplyImpl(x: Count) = x.n
}

class ConcatIterable[A](lhs: Iterable[A], rhs: Iterable[A]) extends Iterable[A] { def iterator = lhs.iterator ++ rhs.iterator }


// TODO a common interface between Bag, IndexedBag, and GroupedBag?

// TODO optimize: no need to wrap/box Iterable objects...
case class Bag[A](it: Iterable[A], pred: Option[A => Bool], lim: Option[Int]) extends DataSource[A] {
  def iterator = {
    val it0 = pred.fold(it.iterator)(it.iterator.filter)
    lim.fold(it0)(it0.take)
  }
  def orderBy[B:Ordering](b: B): IndexedBag[B,A] = 
    //GeneralIndexedBag(it.map(b -> _),None,None)
    IndexedBag(groupAndOrderBy(b),None)
  
  def groupAndOrderBy[B:Ordering](b: B): GroupedBag[B,Bag[A]] = GroupedBag(SortedMap(b -> this),None,None,None,None)
  
  //def groupBy[B](b: B): IndexedBag[B,A] = IndexedBag(GroupedBag(Map(b -> this),None,None,None),None)
  // ^ TODO?
  
  def distinct = ??? // TODOdef distinct = Set(xs: _*)
  
  def limit(n: Int) = copy(lim = Some(lim.fold(n)(_ min n)))
  def head = iterator.next
  
  override def filter(p: A => Bool): Bag[A] = copy(pred = Some(pred.fold(p)(pred => x => pred(x) && p(x))))
  override def where(p: A => Bool): Bag[A] = filter(p)
  
  def isEmpty = iterator.isEmpty
  def nonEmpty = !isEmpty
  
  override def toString = s"{ ${iterator mkString "; "} }"
}
object Bag {
  @transparencyPropagating
  def apply[A](a: A): Bag[A] = Bag(a::Nil,None,None)
  implicit def monoidBag[A]: Monoid[Bag[A]] =
    Monoid.instance(Bag[A](Nil,None,None)) {
      case Bag(it0,p0,l0) -> Bag(it1,p1,l1) => Bag(new ConcatIterable(it0,it1),p0 orElse p1, l0 orElse l1)
    }
}

// Thin wrapper to hide the inner bag nesting
case class IndexedBag[O,A](underlying: GroupedBag[O,Bag[A]], lim: Option[Int]) extends DataSource[A] {
  def iterator: Iterator[A] = {
    val it0 = underlying.iterator.flatMap(_.iterator)
    lim.fold(it0)(it0.take)
  }
  def limit(n: Int) = copy(lim = Some(lim.fold(n)(_ min n)))
  
  //def first = xs.headOption.fold(NoMin:ArgMin[Key,A]){case (k,v) => SomeMin(k,v)}
  
  override def filter(pred: A => Bool): IndexedBag[O,A] = copy(underlying applying (_ filter pred))
  // TODO GroupedBag should have special map construct... (named `applying`?)
  override def where(p: A => Bool): IndexedBag[O,A] = filter(p)
  
  override def toString = s"[ ${iterator mkString ", "} ]"
}
object IndexedBag {
  implicit def monoidIndexedBag[O,A]: Monoid[IndexedBag[O,A]] =
    Monoid.instance(IndexedBag(GroupedBag.empty[O,Bag[A]],None))((x, y) => 
      IndexedBag(x.underlying |+| y.underlying, x.lim orElse y.lim))
}

case class GroupedBag[K,A](toMap: Map[K,A], pred: Option[A => Bool], app: Option[A => A], postOrd: Option[Ordering[A]], lim: Option[Int]) extends DataSource[A] {
  @transparencyPropagating
  def iterator = {
    val it0 = toMap.valuesIterator
    val it1 = pred.fold(it0)(it0.filter)
    postOrd.fold(lim.fold(it1)(it1.take)) { implicit o => 
      val it2 = it1.toSeq.sorted.iterator
      lim.fold(it2)(it2.take)
    }
  }
  @transparencyPropagating
  def withKey () = projectingKey[K]
  def projectingKey[K0]()(implicit ev: K ProjectsOn K0): GroupedBag[K,Key[K]~A] =
    GroupedBag[K,Key[K]~A](
      toMap.map{case k->v=>k->(Key(k)~v)}, 
      pred.map(_ compose (_.rhs)), 
      app.map(f => {case k ~ v => k ~ f(v)}), 
      postOrd.map(implicit oa => Ordering.by(_.rhs)), 
      lim)
  override def filter(p: A => Bool): GroupedBag[K,A] = copy(pred = Some(pred.fold(p)(pred => x => pred(x) && p(x))))
  override def where(p: A => Bool): GroupedBag[K,A] = filter(p)
  def applying(f: A => A) = copy(app = Some(app.fold(f)(_ andThen f)))
  def orderByGroupKey(implicit kord: Ordering[K]): GroupedBag[K,A] = copy(toMap = SortedMap(toMap.toSeq:_*), postOrd = None)
  def orderBy[B:Ordering](f: A => B): GroupedBag[K,A] = copy(postOrd = Some(Ordering.by(f)))
  def limit(n: Int) = copy(lim = Some(lim.fold(n)(_ min n)))
  def descending() = copy(postOrd = postOrd.map(_.reverse))
  // ^ TODO restrict: only available if some Sorted boolean type parameter is True
  override def toString = if (toMap.isInstanceOf[SortedMap[_,_]] || postOrd.isDefined) s"[ ${iterator mkString ", "} ]" else s"{ ${iterator mkString "; "} }"
}
object GroupedBag {
  @transparencyPropagating
  def empty[K,A] = GroupedBag[K,A](Map.empty,None,None,None,None)
  @transparencyPropagating
  implicit def monoidGroupedBag[K,A:Semigroup]: Monoid[GroupedBag[K,A]] =
    Monoid.instance(empty[K,A]) {
      case GroupedBag(it0,p0,a0,o0,l0) -> GroupedBag(it1,p1,a1,o1,l1) =>
        GroupedBag(if (it1.isInstanceOf[SortedMap[K,A]]) it1 |+| it0 else it0 |+| it1, // tries to preserve a possible existing 'sortedness' of the merged maps
          p0 orElse p1,a0 orElse a1,o0 orElse o1, l0 orElse l1)
    }
}

//@field class Key[T](value: T)
case class Key[T](value: T)
object Key { // it's actually not wanted to have `Key[T] Wraps T`, as it would expose unwanted semantics based on T
  //implicit def wraps[T]: (Key[T] Wraps T) = new (Key[T] Wraps T) {
  //  def applyImpl(v: T) = Key(v)
  //  def deapplyImpl(x: Key[T]) = x.value
  //}
  @transparencyPropagating
  implicit def semigroupKey[T]: Semigroup[Key[T]] = Semigroup.instance((a,b) => require(a == b) thenReturn a)
  //implicit def monoidKey[T]: Monoid[Key[T]] = forget it, not wanted!
  @transparencyPropagating
  implicit def ordKey[T:Ordering]: Ordering[Key[T]] = Ordering.by(_.value)
}


import scala.language.higherKinds


//class DelayedTransfo[A,B](a: A: f: A => B) {
//  def result = f(a)
//}
//sealed abstract class QueryResult[R] { def result: R }
//implicit case class SimpleResult[R:Monoid](result: R) extends QueryResult[R]
//case class MappedResult[A:Monoid,R](preResult: A, f: A => R) extends QueryResult[R] {
//  def result = f(preResult)
//}



case class Bag2[A](it: Iterable[A]) extends DataSource[A] {
  def iterator = it.iterator
}
object Bag2 {
  implicit def monoid[E]: Monoid[Bag2[E]] = Monoid.instance(new Bag2[E](Nil))((a,b) => new Bag2[E](new ConcatIterable(a.it,b.it)))
}
//case class OrderedBag2[K,A](toMap: SortedMap[K,Bag[A]]) extends DataSource[K~A] {
case class OrderedBag2[K,V](toMap: Map[K,Bag[V]]) extends DataSource[K~V] {
  def iterator = toMap.iterator.flatMap{case(k,v)=>v.iterator.map(k~_)} //((dbstage.~.apply _).tupled)
}
object OrderedBag2 {
  implicit def monoidOrderedBag[K:Ordering,A]: Monoid[OrderedBag2[K,A]] =
    Monoid.instance(OrderedBag2[K,A](SortedMap.empty)) {
      (a,b) => OrderedBag2(a.toMap |+| b.toMap)
    }
}

case class Groups[K,V](toMap: Map[K,V]) extends DataSource[K~V] {
  def iterator = toMap.iterator.map{case(k,v)=>(k~v)} 
}
object Groups {
  //@transparencyPropagating
  //def empty[K,A] = GroupedBag[K,A](Map.empty,None,None,None,None)
  @transparencyPropagating
  implicit def monoidGroups[K,A:Semigroup]: Monoid[Groups[K,A]] =
    Monoid.instance(Groups[K,A](Map.empty)) {
      (a,b) => Groups(a.toMap |+| b.toMap)
    }
}







/*


// does not embed well: 'Embedding Error: Unknown type `qr.R` does not have a TypeTag to embed it as uninterpreted.'
/*
trait QueryResult[A] {
  type R
  def apply(as: Iterator[A]): R
}
object QueryResult {
  type Aux[A,B] = QueryResult[A] { type R = B }
  implicit def monoid[M:Monoid]: QueryResult.Aux[M,M] = ???
}
*/
trait QueryResult[A,R] {
  def apply(as: Iterator[A]): R
}
object QueryResult {
  def instance[A,R](f: Iterator[A] => R): QueryResult[A,R] = new QueryResult[A,R] { def apply(as: Iterator[A]) = f(as) }
  //def instance[A,R](f: (=> Iterator[A]) => R): QueryResult[A,R] = new QueryResult[A,R] { def apply(as: Iterator[A]) = f(as) }
  implicit def monoid[M:Monoid]: (M QueryResult M) = QueryResult.instance(xs => Monoid[M].combineAll(xs))
  implicit def dataSource[E]: (DataSource[E] QueryResult DataSource[E]) =
    //QueryResult.instance(dss => new DataSource[E] { def iterator = dss.flatMap(_.iterator) })
    QueryResult.instance(new ConcatDataSource(_))
}
class ConcatDataSource[E](dss: => Iterator[DataSource[E]]) extends DataSource[E] {
  //val buf = Lazy(dss.flatMap(_.iterator))
  def iterator = dss.flatMap(_.iterator)
  final val initNb = 16
  def init = iterator.take(initNb).toBuffer
  override def toString = s"{${(init mkString "; ")+(if (init.size>initNb) "..." else "")}}"
}

//class BagOrdering[T,O:Ordering]()
//class PostOrdering[T](t: T)
class PostOrdering[T,O](val t: T)
object PostOrdering {
  //implicit def queryResult[T,R,O:Ordering](implicit qr: (T QueryResult DataSource[R]), p: R ProjectsOn O): (PostOrdering[T,O] QueryResult DataSource[R]) =
  //  QueryResult.instance(xs => new DataSource[R] {
  //    //val buf = xs.map(_.t).flatMap(qr.apply)//.toBu
  //    //val buf = xs.flatMap(po => qr(po.t).iterator)
  //    val buf = {
  //      //println(s"Sorting ${qr(xs.map(_.t)).iterator.toBuffer}")
  //      (qr(xs.map(_.t)).iterator.toBuffer alsoApply (r=>println(s"Sorting $r"))).sortBy(p)
  //    }
  //    override def iterator: Iterator[R] = buf.iterator
  //  })
  implicit def queryResult[T,R,O:Ordering](implicit qr: (T QueryResult DataSource[R]), p: R ProjectsOn O): (PostOrdering[T,O] QueryResult OrderedBag[R,O]) =
    QueryResult.instance(xs => new OrderedBag({
      val buf = {
        (qr(xs.map(_.t)).iterator.toBuffer alsoApply (r=>println(s"Sorting $r"))).sortBy(p)
      }
      buf
    }))
}

class OrderedBag[E,O](xs: Iterable[E]) extends Bag2[E](xs) {
  println(s"OrderedBag $this")
  override def toString = s"[${iterator mkString ","}]"
}
object OrderedBag {
  //implicit def queryResult[E,O:Ordering]: (OrderedBag[E,O] QueryResult OrderedBag[E,O]) = ???
  implicit def monoid[E,O:Ordering](implicit p: E ProjectsOn O): Monoid[OrderedBag[E,O]] =
    Monoid.instance[OrderedBag[E,O]](new OrderedBag(Nil))((a,b) => new OrderedBag(mergeSort(a.iterator,b.iterator).toBuffer))
  def mergeSort[E,O:Ordering](lhs:Iterator[E], rhs:Iterator[E])(implicit p: E ProjectsOn O): Iterator[E] = new Iterator[E] {
    val blhs = lhs.buffered
    val brhs = rhs.buffered
    //println(blhs,brhs)
    //???
    def hasNext: Bool = blhs.hasNext || brhs.hasNext
    def next = {
      require(hasNext)
      if (!blhs.hasNext) brhs.next
      else if (!brhs.hasNext) blhs.next
      else Ordering[O].compare(p(blhs.head),p(brhs.next)) match {
        case n if n <= 0 => blhs.next
        case _ => brhs.next
      }
    }
  }
}



// TODO
// make bagordering that can wrap any QueryResult[_,DS[E]]; make Indexed that can accumulate non-semigroup stuff



// Note: indexed groups have NOTHING to do with groups, and should NOT be mixed with them (if anything, they're closer to set semantics)

//sealed abstract class Bag2[E] extends DataSource[E]
sealed class Bag2[E](val it: Iterable[E]) extends DataSource[E] 
{ def iterator = it.iterator }
//{ def iterator = it.iterator.map(e=>println(s"it $e") thenReturn e) }
object Bag2 {
  def apply[E](xs: E*) = new Bag2[E](xs)
  implicit def monoid[E]: Monoid[Bag2[E]] = Monoid.instance(new Bag2[E](Nil))((a,b) => new Bag2[E](new ConcatIterable(a.it,b.it)))
  //implicit def monoid[E,B<:Bag2[E]]: Monoid[BagProjection[B,R]] = //???
  //  Monoid.instance(BagProjection[B,R](Monoid[B].empty,()=>Iterator.empty))((x,y) => BagProjection(x.b |+| y.b, ))
  //implicit def queryResult[E]: (Bag2[E] QueryResult DataSource[E]) = ???
}
//case class SingletonBag[E](e: E) extends Bag2[E] {
//  def iterator = Iterator(e)
//  def orderingBy[K](key: K) = ???
//}


//case class Indexed[K,V](k: K, v: V)
case class Indexed[K,V](kv: (K,V)) extends AnyVal
object Indexed {
  //implicit def monoid[K,V:Semigroup]: Monoid[Indexed[K,V]] =
  //implicit def queryResult[K,V]: (Indexed[K,V] QueryResult DataSource[K~V]) =
  //  //QueryResult.instance(kvs => kvs.map(_.kv).toMap.iterator.map{case (k,v)=>k~v})
  //  //QueryResult.instance(kvs => MapDataSource(kvs.map(_.kv).toMap))
  //  QueryResult.instance(kvs => MapDataSource(kvs.map(_.kv).toMap alsoApply(r => println(s"> $r"))))
  implicit def queryResult[K,V,VR](implicit qr: (V QueryResult VR)): (Indexed[K,V] QueryResult DataSource[K~VR]) =
    QueryResult.instance(kvs => MapDataSource(kvs.map(_.kv).toBuffer.groupBy((_:(K,V))._1).mapValues(xs => qr(xs.unzip._2.iterator))))
}
case class MapDataSource[K,V](toMap: Map[K,V]) extends DataSource[K~V] {
  def iterator = toMap.iterator.map{case (k,v)=>k~v}
}

*/






/*
trait BagLike[F[_]] { type Elem[T] }
object BagLike {
  implicit def bag: BagLike[Bag] = ???
  //implicit def map[K]: BagLike[λ[T => Map[K,K~T |> Bag]]] = ???
  //implicit def map[K]: BagLike[λ[`+T` => Map[K,Bag[T]]]] { type Elem[T] = K~T } = ???
  implicit def map[K]: BagLike[λ[T => Map[K,Bag[T]]]] { type Elem[T] = K~T } = ???
  implicit def set: BagLike[Set] = ???
  implicit def monoid[F[_]:BagLike,T]: Monoid[F[T]] = ???
  
  //the[BagLike[λ[T => Map[Int,Bag[T]]]]]
  implicit class BagLikeSyntax[F[_]:BagLike,T](self: F[T]) { //extends AnyVal {
    def selecting[R](implicit p: T ProjectsOn R): BagLikeProjection[T,R] = ???
  }
  
}
*/

/*

//trait BagLike[B] { type Elem }
//class BagLike[B,E] extends Monoid[B] { def empty = ??? ; def combine(x:B,y:B) = ??? }
trait BagLike[B,E] extends Monoid[B] { def iterator(b: B): Iterator[E] }
object BagLike {
  implicit def bag[T]: BagLike[Bag[T],T] = ???
  implicit def map[K,V:Semigroup]: BagLike[Map[K,V],K~V] = new BagLike[Map[K,V],K~V] {
    @transparencyPropagating
    def empty = Map.empty
    @transparencyPropagating
    def combine(x: Map[K,V], y: Map[K,V]) = { def map = (); x |+| y }
    def iterator(b: Map[K,V]): Iterator[K~V] = b.iterator.map(kv=>kv._1~kv._2)
  }
  //implicit def map[K,V]: BagLike[Map[K,Bag[V]],K~V] = ???
  //implicit def map[K,V]: BagLike[Map[K,Set[V]],K~V] = ???
  implicit def set[T]: BagLike[Set[T],T] = new BagLike[Set[T],T] {
    def empty = Set.empty
    def combine(x: Set[T], y: Set[T]) = x |+| y
    def iterator(b: Set[T]): Iterator[T] = b.iterator
  }
  //implicit def monoid[B,T](implicit bl: BagLike[B,T], mon: Monoid[T]): Monoid[B] = ???
  
  implicit class BagLikeSyntax[B,T](self: B)(implicit bl: BagLike[B,T]) { //extends AnyVal {
    @transparencyPropagating
    //def selecting[R](implicit p: T ProjectsOn R): BagLikeProjection[T,R] = ???
    //def selecting[R](implicit p: T ProjectsOn R): BagLikeProjection[B,T,R] = ???
    def selecting[R]()(implicit p: T ProjectsOn R): BagProjection[B,R] = //???
      BagProjection(self)
    @transparencyPropagating
    //def limiting[N <: Singleton & Int](n:N): LimitedBagLike[B,T,N] = ???
    def limiting[N <: Singleton & Int](n:N): LimitedBagLike[B,T,N] = ??? // can work with shapeless, but won't embed with Squid
    
    /*
    def orderingBy[O]()(implicit p: T ProjectsOn O, ord: Ordering[O]): BagOrdering[B,O,True] =
      //BagOrdering[B,O,True](self)
      BagOrderingInstance[B,T,O,True](self)
    */
    @transparencyPropagating
    def orderingBy[O]()(implicit p: T ProjectsOn O, ord: Ordering[O]): BagOrdering[B,T,O,True] =
      //new BagOrdering[B,T,O,True](self, ???)
      //new BagOrdering[B,T,O,True](bl.iterator(self), BagOrdering.orderIterator(bl.iterator(self),true)(ord on p))
      new BagOrdering[B,T,O,True](bl.iterator(self), it => BagOrdering.orderIterator(bl.iterator(self),true)(ord on p))
    
  }
  
  //import scala.language.implicitConversions
  //implicit def toDataSource[B,T](b: B)(implicit bl: BagLike[B,T]): DataSource[T] = new DataSource[T] { def iterator = bl.iterator(b) }
  implicit class BagDataSource[B,T](b: B)(implicit bl: BagLike[B,T]) extends DataSource[T] { def iterator = bl.iterator(b) }
  
}

//class BagLikeProjection[B,T,R]
//object BagLikeProjection {
//  implicit def monoid[B,T,R](implicit bl: BagLike[B,T], p: T ProjectsOn R): Monoid[BagLikeProjection[B,T,R]] = ??? //Monoid.instance()
//}
//class BagProjection[B,R](b: B & Bag[R])
//class BagProjection[B,R] extends DataSource[R] { type E; def getBag(b:B): Bag[E]; def proj(e:E):R; def iterator: Iter }
//case class BagProjection[B,R](b: B, getIter: () => Iterator[R]) extends DataSource[R]
case class BagProjection[B,R](b: B) //extends DataSource[R]
object BagProjection {
  //implicit def monoid[B,T,R](implicit bl: BagLike[B,T], p: T ProjectsOn R): Monoid[BagProjection[B,R]] = //???
  //  Monoid.instance(BagProjection[B,R](Monoid[B].empty,()=>Iterator.empty))((x,y) => BagProjection(x.b |+| y.b, ))
  
  @transparencyPropagating
  implicit def proj[B,T,R](implicit bl: BagLike[B,T], p: T ProjectsOn R): BagLike[BagProjection[B,R],R] = //???
    new BagLike[BagProjection[B,R],R] {
      def empty = BagProjection(bl.empty)
      def combine(x: BagProjection[B,R], y: BagProjection[B,R]) = BagProjection(x.b |+| y.b)
      def iterator(b: BagProjection[B,R]): Iterator[R] = bl.iterator(b.b).map(p)
    }
  
}


/*
//case class BagOrdering[B,R,Asc<:BoolT](b: B) { def desc() = BagOrdering[B,R,False](b) }
//case class BagOrdering[B,R,Asc<:BoolT](b: B)(implicit bl: BagLike[B,R], ord: Ordering[R]) {
//  def desc() = BagOrdering[B,R,False](b)
//  override def toString: String = s"[${this.iterator}]"
//}
abstract class BagOrdering[B,O,Asc<:BoolT]//(val b: B)
extends DataSource[???]
{val b: B; 
  def desc(): BagOrdering[B,O,False]
  //def desc[T]()(implicit bl: BagLike[B,T], ord: Ordering[O], se: BagLike[BagOrdering[B,O,False],T]): BagOrdering[B,O,False] = BagOrderingInstance[B,T,O,False](b)
}
//case class BagOrderingInstance[B,T,O,Asc<:BoolT](b: B)(implicit bl: BagLike[B,T], ord: Ordering[O], self: BagLike[BagOrdering[B,O,Asc],T]) extends BagOrdering[B,O,Asc] {
//  //def desc() = ??? //BagOrderingInstance[B,T,O,False](b)
//  def desc() = BagOrderingInstance[B,T,O,False](b)
//  //override def toString: String = s"[${BagLike.BagLikeSyntax(this:BagOrdering[B,O,Asc]).iterator}]"
//  override def toString: String = s"[${self.iterator(this).mkString(",")}]"
//}
class BagOrderingInstance[B,T,O,Asc<:BoolT](b: B, it: Iterator[T])
*/

/*
class BagOrdering[B,T,O,Asc<:BoolT](val b: B, it: => Iterator[T]) extends DataSource[T] {
//class BagOrdering[B,T,O,Asc<:BoolT](val b: Bag[T], it: => Iterator[T]) extends DataSource[T] {
  def iterator = it
  @transparencyPropagating
  override def toString: String = s"[${iterator.mkString(",")}]"
}

object BagOrdering {
  /*
  implicit def ord[B,T,R,Asc<:BoolT:Choice](implicit bl: BagLike[B,T], p: T ProjectsOn R, ord: Ordering[R]): BagLike[BagOrdering[B,R,Asc],T] =
    //new BagLike[BagOrdering[B,R,Asc],T] {
    //  def empty = BagOrdering(bl.empty)
    //  def combine(x: BagOrdering[B,R,Asc], y: BagOrdering[B,R,Asc]) = BagOrdering(x.b |+| y.b)
    //  def iterator(b: BagOrdering[B,R,Asc]): Iterator[T] = {
    //    val buf = bl.iterator(b.b).toBuffer.sortBy(p)
    //    //ite[Asc](buf.iterator)(buf.reverseIterator)
    //    choice[Asc].ite(buf.iterator)(buf.reverseIterator)
    //  }
    //}
    new BagLike[BagOrdering[B,R,Asc],T] {
      def empty = BagOrderingInstance(bl.empty)
      def combine(x: BagOrdering[B,R,Asc], y: BagOrdering[B,R,Asc]) = BagOrderingInstance(x.b |+| y.b)
      def iterator(b: BagOrdering[B,R,Asc]): Iterator[T] = {
        val buf = bl.iterator(b.b).toBuffer.sortBy(p)
        //ite[Asc](buf.iterator)(buf.reverseIterator)
        choice[Asc].ite(buf.iterator)(buf.reverseIterator)
      }
    }
  */
  @transparencyPropagating
  implicit def bagLike[B,T,O,Asc<:BoolT:Choice](implicit bl: BagLike[B,T], p: T ProjectsOn O, ord: Ordering[O]): BagLike[BagOrdering[B,T,O,Asc],T] =
    new BagLike[BagOrdering[B,T,O,Asc],T] {
      def empty = new BagOrdering(bl.empty, Iterator.empty)
      def combine(x: BagOrdering[B,T,O,Asc], y: BagOrdering[B,T,O,Asc]) = 
        //new BagOrdering(x.b |+| y.b, ???)
        { lazy val self: BagOrdering[B,T,O,Asc] = new BagOrdering(x.b |+| y.b, iterator(self)); self }
      def iterator(b: BagOrdering[B,T,O,Asc]): Iterator[T] = {
        val buf = bl.iterator(b.b).toBuffer.sortBy(p)
        //ite[Asc](buf.iterator)(buf.reverseIterator)
        choice[Asc].ite(buf.iterator)(buf.reverseIterator)
      }
    }
  
}
*/

//class BagOrdering[B,T,O,Asc<:BoolT](_src: => Iterator[T], _ordered: => Iterator[T]) extends DataSource[T] {
class BagOrdering[B,T,O,Asc<:BoolT](_src: => Iterator[T], transfo: Iterator[T] => Iterator[T]) extends DataSource[T] {
  def src = _src
  //def ordered = _ordered
  def iterator = transfo(src)
  @transparencyPropagating
  override def toString: String = s"[${iterator.mkString(",")}]"
}
object BagOrdering {
  @transparencyPropagating
  implicit def bagLike[B,T,O,Asc<:BoolT:Choice](implicit bl: BagLike[B,T], p: T ProjectsOn O, ord: Ordering[O]): BagLike[BagOrdering[B,T,O,Asc],T] =
    new BagLike[BagOrdering[B,T,O,Asc],T] {
      //def tord = ord.on(p)
      //def asc = choice[Asc].reify
      def empty = new BagOrdering(Iterator.empty, Iterator.empty) // FIXME what if bl.empty's iterator is not empty?!
      def combine(x: BagOrdering[B,T,O,Asc], y: BagOrdering[B,T,O,Asc]) = 
        new BagOrdering(x.transfo(x.src ++ y.src), 
          orderIterator(_ |> x.transfo, choice[Asc].reify)(ord.on(p)))
          //orderIterator(x.src ++ y.src, asc)(tord))
      def iterator(b: BagOrdering[B,T,O,Asc]): Iterator[T] = b.iterator
    }
  def orderIterator[E:Ordering](it: Iterator[E], asc: Bool): Iterator[E] = {
    it.toBuffer.sorted(if (asc) Ordering[E] else Ordering[E].reverse).iterator
  }
  // val buf = bl.iterator(b.b).toBuffer.sortBy(p)
  //choice[Asc].ite(buf.iterator)(buf.reverseIterator)
}


class LimitedBagLike[T, R, N <: Singleton & Int]


//case class Bag2[T](x: T) extends DataSource[T] { def iterator = Iterator(x) }
class Bag2[T](val ite: Iterable[T]) extends DataSource[T] { def iterator = ite.iterator }
object Bag2 {
  @transparencyPropagating
  def apply[T](x: T) = new Bag2(x::Nil)
  @transparencyPropagating
  implicit def bagLike[T]: BagLike[Bag2[T],T] =
    new BagLike[Bag2[T],T] {
      def empty = new Bag2(Nil)
      def combine(x: Bag2[T], y: Bag2[T]) = new Bag2(new ConcatIterable(x.ite,y.ite))
      def iterator(b: Bag2[T]): Iterator[T] = b.iterator
    }
}

case class GroupedBag2[K,A](toMap: Map[K,A]) extends DataSource[K~A] { def iterator = toMap.iterator.map(kv=>kv._1~kv._2) }
object GroupedBag2 {
  //def apply[K,V](kv: (K,V)) = new Bag2(x::Nil)
  @transparencyPropagating
    implicit def bagLike[K,V:Semigroup]: BagLike[GroupedBag2[K,V],K~V] =
    new BagLike[GroupedBag2[K,V],K~V] {
      def empty = new GroupedBag2(Map())
      def combine(x: GroupedBag2[K,V], y: GroupedBag2[K,V]) = GroupedBag2(x.toMap |+| y.toMap)
      def iterator(b: GroupedBag2[K,V]): Iterator[K~V] = b.iterator
    }
}
*/



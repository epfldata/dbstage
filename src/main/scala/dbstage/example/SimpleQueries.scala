package dbstage
package example

import squid.utils._
import RecordDefs._
import Embedding.Predef._
import cats.instances.all._
//import cats.instances.all._ // why does this break `new InputFile` ??
//import cats.instances.int._
//import cats.instances.string._
//import cats.instances.unit._
//import cats.instances.map._
import cats.Monoid
import Gender.Read
import cats.Semigroup // ugly :-(
// TODO distinguish Wraps that should get instances for none, Read and/or Monoid

object SimpleQueries extends App {
  
  //implicitly[RecordRead[String ~ Int]]
  //implicitly[RecordRead[Age]]
  //implicitly[RecordRead[Age ~ Name]]
  //implicitly[RecordRead[Age ~ Name]]
  
  //import Staged.apply  // note: makes the compiler stack-overflow!!
  
  //"data/persons.tbl"
  
  val f = new InputFile[IdPerson]("data/Persons.csv")
  println(f.iterator.next)
  
  val sf = new StagedInputFile[IdPerson]("data/Persons.csv")
  println(sf.stagedIterator.compile.next)
  //println(sf.stagedIterator)
  
  //println(Staged.apply(1).embedded(Embedding))
  //println(the[Staged[Separator]])
  
  
}

object SimpleQueries2 extends App {
  val persons = new InputFile[IdPerson]("data/Persons.csv")
  
  //implicitly[Monoid[Int]]
  //implicitly[cats.Monoid[Min]]
  //implicitly[Semigroup[String]]
  //implicitly[Monoid[ForMin[Int,String]]]
  
  (
    for {
      male <- persons
      //if p[Age] > 18
      //if {println(male,male[Gender],male[Gender] == Male);true}
      if male[Gender] == Male
      female <- persons
      //if {println(male,female,female[Gender] == Female);true}
      if female[Gender] == Female
      //if {println(s"${male[Name]} ${female[Name]} ${male[Age] - female[Age]}");true}
    //} yield Min(math.abs(male[Age] - female[Age]))
    //} yield Min[Int,String](math.abs(male[Age] - female[Age]), s"${male[Name]} & ${female[Name]}; ") // works! TODO test...
    } yield Min(math.abs(male[Age] - female[Age]), Bag(male[Name] ~ female[Name])) // works! TODO test...
    //} yield Min(math.abs(male[Age] - female[Age])) // works! TODO test...
  ) alsoApply println
  
  (
    for {
      male   <- persons.where(_[Gender] == Male)
      female <- persons.where(_[Gender] == Female)
    } yield Min(Age(math.abs(male[Age] - female[Age])))
  ) alsoApply println
  
  
  //implicitly[Monoid[scala.collection.immutable.TreeMap[Int,Int]]]
  //implicitly[Monoid[SortedBag[Int,Int]]]
  
  (
    for {
      p <- persons
    //} yield Min(p.select[Age]) ~ (Bag(p.project[PersonId ~ Name]).orderBy[PersonId] groupBy p[Age]).orderByKey
    } yield Min(p.select[Age]) ~ (Bag(p.project[PersonId ~ Name]) orderBy p[PersonId] groupBy p[Age]).orderByGroupKey
  ) alsoApply println
  
  (
    for {
      male   <- persons where (_[Gender] == Male)
      female <- persons where (_[Gender] == Female)
    } yield Bag(male[Name] ~ female[Name]) orderBy (female[PersonId], male[PersonId])
  ) alsoApply println
  
  
  
  //(
  //  for {
  //    p <- persons
  //    lastName <- Bag(p[Name].dropWhile(_ != ' ').tail)
  //  //} yield Bag(lastName).distinct
  //  } yield Count() ~ Bag(p.project[Name ~ Age]) groupBy lastName  // TODO order by Count
  //) alsoApply println
  (
    for {
      p <- persons
      lastName <- Bag(p[Name].dropWhile(_ != ' ').tail)
    //} yield Bag(lastName).distinct
    } yield Count() ~ Bag(p.project[Name ~ Age]) groupBy lastName  // TODO order by Count
  ) alsoApply println
  
  // TODO: using a nested query:
  /*
  (
    for {
      (lname, info) <- for {
        p <- persons
        lastName <- Bag(p[Name].dropWhile(_ != ' ').tail)
      } yield Count() ~ Bag(p.project[Name ~ Age]) groupBy lastName
    //} yield Bag(info) orderBy lname   // FIXME doesn't work!
    //} yield Bag(info) orderBy info[Count] //descending   // FIXME doesn't work!
    } yield (Bag(info) orderBy info[Count]:SortedBag[Count~Bag[Name ~ Age],Int]) //descending   // FIXME doesn't work!
  ) alsoApply println
  */
  
  //import scala.collection.immutable.TreeMap
  //import cats.implicits._
  //import cats.instances.map.catsKernelStdMonoidForMap
  //println(TreeMap(4->5,1 -> 2) ++ TreeMap(6->7,1 -> 3))
  //println(TreeMap(4->5,1 -> 2) |+| TreeMap(6->7,1 -> 3))
  //TreeMap(4->5,1 -> 2).foldLeft()
  //println(TreeMap(1 -> 1).updated(2, 2).updated(0, 0).updated(3, 3).getClass)
  
  
}

object SimpleQueries3 extends App {
  val persons = new InputFile[IdPerson]("data/Persons.csv")
  
  (
    for {
      male   <- persons where (_[Gender] == Male)
      female <- persons where (_[Gender] == Female)
    } yield Bag(male[Name] ~ female[Name]) orderBy (female[PersonId], male[PersonId])
  ) alsoApply println
  
  (
    for {
      p <- persons
      lastName <- Bag(p[Name].dropWhile(_ != ' ').tail)
    //} yield Bag(lastName).distinct
    //} yield Count() ~ Bag2(p.project[Name ~ Age]) groupBy2 lastName orderingBy (_[Count]) // TODO descending
    } yield Count() ~ Bag(p.project[Name ~ Age]) groupBy lastName orderBy (_[Count]) descending() limit 3
  ) alsoApply println
  
  // TODO having (_[Count] > 1); aka persistent filter (also make filter itself persistent?)
  // TODO orderingBy on Bag2
  // TODO limit?
  
  //println(the[Ordering[Unit]])
  //println(implicitly[Ordering[Unit]] == implicitly[Ordering[Unit]]) // false when using cat's instance...
  
  
  (
    for {
      p <- persons
    } yield Bag(p) orderBy p[Name]
  ) alsoApply println
  
  (
    for {
      p <- persons
    } yield Bag(p[Name] ~ p[Age]) orderBy p[Age] limit 7
  ) alsoApply println
  
  
  
  //println(implicitly[Monoid[Gender]])
  
  
  // NOTE: don't yet have a good way to do:
  //   ORDER BY Country ASC, CustomerName DESC;
  
  // NOTE: outer [left|right]joins can probably be emulated by just adding a null/None value to the head of a joined data source... which is incidentally much clearer semantically
  
  
}

//trait Base { type Rep; implicit class RepOps(private val rep: Rep) { def foo = 123 } }
//trait Base { self => type Rep[T]; implicit class RepOps[T](private val rep: Rep[T]) { def foo = 123 }; object Predef { type Rep[T] = self.Rep[T] } }
//trait Base { self => type Rep[T]; object Predef { type Rep[T] = self.Rep[T] } }
//trait Base2 extends Base { self => implicit class RepOps[T](private val rep: Rep[T]) { def foo = 123 } }
//object Derived extends Base2

object SimpleQueries4 extends App {
  import cats.syntax.all._
  
  //import Derived.RepOps
  //Option.empty[Derived.Rep].map(x => x.foo)
  //Option.empty[Derived.Rep[Int]].map(x => x.foo)
  //import Derived.Rep
  //import Derived.Predef._
  //Option.empty[Rep[Int]].map(x => x.foo)
  
  
  //println(_) $ the[Monoid[Bag2[Int]]]
  //implicitly[Monoid[Bag2[Int]]] alsoApply println
  
  //def foo[T:Monoid](x:T)=x
  //foo(SingletonBag(1):Bag2[Int])
  //foo(SingletonBag(1)) // nope
  
  //def foo[T<:T0,T0](x:T)(implicit mon: Monoid[T0])=x
  //def foo[T0,T<:T0](x:T)(implicit mon: Monoid[T0])=x
  //foo(SingletonBag(1):Bag2[Int])
  //foo(SingletonBag(1)) // nope
  
  /*
  Debug show
  println(
    for {
      x <- Bag2(1,2,3,4)
      //y <- Bag2('a,'b,'c)
      y <- Bag2("a","b","c")
      if y.startsWith("a") || x%2==0
    //} yield Indexed(x,y ~ Count())
    } yield new PostOrdering[Indexed[Int,String~Count],Count](Indexed(x,y ~ Count()))
  )
  */
  
  
  /*
  import BagLike.BagLikeSyntax
  import BagLike.BagDataSource
  
  val s = Set(p)
  
  //println(s.project[Set[Age]])
  //println((1 ~ s).project[Set[Age]]) // nope
  
  //println(s.selecting[Age])
  
  //val m = Map(1 -> Bag(p))
  //val m = Map(1 -> Set(p))
  //println(m.selecting[Set[Age]])
  
  val m = Map(1 -> Count() ~ Bag(p))
  
  //val m2 = m.selecting[Age]
  val m2 = m.selecting[Count].orderingBy[Count].desc
  println(m2)
  val m3 = m2 |+| Map(1 -> Count() ~ Bag(p),2->Count()~Bag(p)).selecting[Count].orderingBy[Count].desc
  println(m3)
  println(m3.iterator.toList)
  */
  
  //import shapeless.syntax.singleton._
  
  //println(s.limiting(2.narrow))
  
  //implicitly[Monoid[Set[Person]]]
  //implicitly[Monoid[BagProjection[Set[Person],Age]]]
  //implicitly[BagLike[BagProjection[Set[Person],Age],Age]]
  
  println()
  
  //code"2.narrow"
  
  //def nar[T](x:T):T{}=x
  //def nar[T](x:T{}):T{}=x
  //code"nar(2)" alsoApply println
  
  //(1 ~ p):dbstage.~[Int,dbstage.Bag[dbstage.~[dbstage.~[dbstage.~[dbstage.example.Name,dbstage.example.Age],dbstage.example.Gender],dbstage.example.Address]]]
  //(1 ~ p).project[Age]
  //implicitly[dbstage.~[Int,dbstage.Bag[dbstage.~[dbstage.~[dbstage.~[dbstage.example.Name,dbstage.example.Age],dbstage.example.Gender],dbstage.example.Address]]] ProjectsOn dbstage.example.Age]
  
  
  
  
  //new BagLikeSyntax(m) // nope
  //new BagLikeSyntax(m)(BagLike.map[Int]) // nope
  //new BagLikeSyntax[λ[T => Map[Int,Bag[T]]],Person](m)
  
  //implicitly[BagLike[]]
  
}

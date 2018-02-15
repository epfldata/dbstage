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

package dbstage
package example.tpch

import squid.utils._
import cats.instances.all._
import squid.quasi.phase
//import cats.instances.tuple._
import dbstage2.{FieldModule, ::, Field, Project, Record, Access, NoFields}
import dbstage2.Embedding.Predef._
import Relations._
import cats.kernel.Monoid


object QueryTests extends App {
  import Queries._
  
  //implicitly[BuildField[OrderKey]]
  //implicitly[Read[OrderKey]]
  //implicitly[Read[OrderKey]](Read.readField(BuildField.build,Read.readInt))
  //implicitly[Read[OrderKey]](Read.readField[OrderKey](BuildField.build,Read.readInt))
  
  //implicitly[RecordRead[Order]]
  
  //val orders = new InputFile[Order]("data/orders.csv") // .withPrimaryKey[OrderKey] // TODO
  val orders = new InputFile[Order]("data/orders.tbl.u1.1") // .withPrimaryKey[OrderKey] // TODO
  //val lineitem = new InputFile[LineItem]("data/lineitem.csv")
  val lineitem = new InputFile[LineItem]("data/lineitem.tbl.u1.1")
  
  //println(orders.rr.read('|')("1|2|ok|0.5|0|-1"))
  //println(orders.data)
  //println(lineitem.data)
  
  println(Q4(orders,lineitem))
  
}

// TODO @embed to be able to compile
object Queries {
  
  //println(Build.build[OrderKey])
  //
  //{
  //  case class OrderKey2(value: Int) extends Field[Int]
  //  println(Build.build[OrderKey2])
  //}
  
  // fixed:
  //implicitly[Monoid[OrderKey]] // diverging
  //implicitly[Monoid[OrderKey]](Field.monoid) // ambiguous
  //implicitly[Monoid[OrderKey]](Field.monoid[Int,OrderKey])
  
  //def pred(l:LineItem) = ({for {
  //    l <- lineitem
  //    if (l[OrderKey] === o[OrderKey]
  //    && l[CommitDate] < l[ReceiptDate])
  //  } yield l}).nonEmpty
  
  @phase('Sugar)
  def Q4(orders: Relation[Order], lineitem: Relation[LineItem]) = {
    
    val DATE = Date("1993-07-01")
    
    val res = for {
      o <- orders
      //if o[OrderDate] >= Date("'[DATE]'")
      //if o[OrderDate] < Date("'[DATE]' + interval '3' month")
      //if {println(s"? O $o");true}
      if o[OrderDate] >= DATE
      //if o[OrderDate] < DATE.+_interval(3)
      if o[OrderDate] < DATE + Interval(3)
      //if {println(s"√ O $o");true}
      if (for {
            l <- lineitem
            //if {println(s"? L $l");true}
            if l[OrderKey] === o[OrderKey]
            if l[CommitDate] < l[ReceiptDate]
            //if {println(s"√ L $l");true}
          } yield Bag(l)).nonEmpty
    //} yield (Bag(o.select[OrderPriority] :: NoFields).orderBy[OrderPriority], 
    //    //Count())
    //    OrderCount(Count()))
    } yield (Bag(o.select[OrderPriority] :: OrderCount(Count()) :: NoFields).orderBy[OrderPriority])
    
    res
  }
  
  /*
  
    if (o[OrderDate] >= Date("'[DATE]'")
    && o[OrderDate] < Date("'[DATE]' + interval '3' month")
    && (for {
      l <- lineitem
      if l[OrderKey] === o[OrderKey]
      && l[CommitDate] < l[ReceiptDate]
    } yield l).nonEmpty)
  
  */
}

case class Interval(months: Int)
//class Date {
//  def < (that: Date): Bool = ???
//  def >= (that: Date): Bool = ???
//  //def +_interval(months: Int): Date = ???
//  def + (invl: Interval): Date = ???
//}
object Date {
  //val df = java.text.DateFormat.getInstance()
  val df = new java.text.SimpleDateFormat("yyyy-mm-dd")
  //def apply(strRep: String) = new Date(new java.util.Date(strRep))
  def apply(strRep: String) = new Date(df.parse(strRep))
}
class Date(val underlying: java.util.Date) {
  def < (that: Date): Bool = underlying before that.underlying
  //def >= (that: Date): Bool = (underlying after that) || (underlying == that)
  def >= (that: Date): Bool = (underlying compareTo that.underlying) >= 0
  //def + (invl: Interval): Date = new Date(new java.util.Date(underlying))
  //def + (invl: Interval): Date = new Date(new java.util.Date(underlying.getTime) alsoApply (_.add(java.util.Calendar.MONTH, 1)))
  //def + (invl: Interval): Date = new Date(new java.util.Date(underlying.getTime) alsoApply (_.setMonth(java.util.Calendar.MONTH, 1)))
  import java.util.Calendar
  def + (invl: Interval): Date = new Date({
    val cal = Calendar.getInstance()
    cal.setTime(underlying)
    cal.add(Calendar.MONTH, +1)
    //new java.util.Date(cal.getTime)
    cal.getTime
  })
}

case class OrderCount(value: Count) extends Field[Count]

case class OrderKey(value: Int) extends Field[Int]
case class CustKey(value: Int) extends Field[Int]
case class OrderStatus(value: String) extends Field[String]
case class TotalPrice(value: Double) extends Field[Double]
case class OrderDate(value: Date) extends Field[Date]
case class OrderPriority(value: String) extends Field[String]

//case class CommitDate(value: Date) extends Field[Date]
//case class ReceiptDate(value: Date) extends Field[Date]

//case class OrderKey(value: Long) extends Field[Long]
case class PartKey(value: Long) extends Field[Long]
case class SuppKey(value: Long) extends Field[Long]
case class LineNumber(value: Int) extends Field[Int]
case class Quantity(value: Double) extends Field[Double]
case class ExtendedPrice(value: Double) extends Field[Double]
case class Discount(value: Double) extends Field[Double]
case class Tax(value: Double) extends Field[Double]
case class ReturnFlag(value: String) extends Field[String]
case class Linestatus(value: String) extends Field[String]
case class ShipDate(value: Date) extends Field[Date]
case class CommitDate(value: Date) extends Field[Date]
case class ReceiptDate(value: Date) extends Field[Date]
case class ShipInstruct(value: String) extends Field[String]
case class ShipMode(value: String) extends Field[String]
case class Comment(value: String) extends Field[String]

object Relations {
  
  
  
  type Order = OrderKey :: CustKey :: OrderStatus :: TotalPrice :: OrderDate :: OrderPriority :: NoFields
  //type LineItem = OrderKey :: CommitDate :: ReceiptDate :: NoFields
  type LineItem =
    OrderKey ::
    PartKey ::
    SuppKey ::
    LineNumber ::
    Quantity ::
    ExtendedPrice ::
    Discount ::
    Tax ::
    ReturnFlag ::
    Linestatus ::
    ShipDate ::
    CommitDate ::
    ReceiptDate ::
    ShipInstruct ::
    ShipMode :: 
    Comment :: 
    NoFields
  
  //val orders = Relation[Order].withPrimaryKey[OrderKey]
  
  //val lineitem = Relation[LineItem]
  
  //val orders = Relation.of(List(
  //))
  
}

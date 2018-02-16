package dbstage
package example.tpch

import squid.lib.transparencyPropagating
import squid.lib.transparent
import squid.utils._

@field class OrderKey(value: Int)
@field class CustKey(value: Int)
@field class OrderStatus(value: String)
@field class TotalPrice(value: Double)
@field class OrderDate(value: Date)
@field class OrderPriority(value: String)

@field class PartKey(value: Long)
@field class SuppKey(value: Long)
@field class LineNumber(value: Int)
@field class Quantity(value: Double)
@field class ExtendedPrice(value: Double)
@field class Discount(value: Double)
@field class Tax(value: Double)
@field class ReturnFlag(value: String)
@field class Linestatus(value: String)
@field class ShipDate(value: Date)
@field class CommitDate(value: Date)
@field class ReceiptDate(value: Date)
@field class ShipInstruct(value: String)
@field class ShipMode(value: String)
@field class Comment(value: String)

case class Interval(months: Int) extends AnyVal
object Date {
  val df = new java.text.SimpleDateFormat("yyyy-mm-dd")
  @transparent
  def apply(strRep: String) = new Date(df.parse(strRep))
}
class Date(val underlying: java.util.Date) extends AnyVal {
  @transparent
  def < (that: Date): Bool = underlying before that.underlying
  @transparent
  def >= (that: Date): Bool = (underlying compareTo that.underlying) >= 0
  import java.util.Calendar
  @transparent
  def + (invl: Interval): Date = new Date({
    val cal = Calendar.getInstance()
    cal.setTime(underlying)
    cal.add(Calendar.MONTH, +1)
    cal.getTime
  })
  @transparent
  override def toString = underlying.toString
}


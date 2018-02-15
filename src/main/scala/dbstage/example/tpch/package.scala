package dbstage
package example

package object tpch {
  
  type Order = OrderKey ~ CustKey ~ OrderStatus ~ TotalPrice ~ OrderDate ~ OrderPriority
  
  type LineItem =
    OrderKey ~
    PartKey ~
    SuppKey ~
    LineNumber ~
    Quantity ~
    ExtendedPrice ~
    Discount ~
    Tax ~
    ReturnFlag ~
    Linestatus ~
    ShipDate ~
    CommitDate ~
    ReceiptDate ~
    ShipInstruct ~
    ShipMode ~ 
    Comment
  
  
  
}

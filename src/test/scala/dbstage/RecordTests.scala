package dbstage

import Embedding.Predef._
import cats.Monoid
import squid.utils._
import example._
import cats.implicits._

import RecordDefs.{p,j}

import org.scalatest.FunSuite

class RecordTests extends FunSuite {
  
  test("Field Selection") {
    
    assert(p.select[Name] == Name("Jo"))
    assert(p.select[Age] == Age(42))
    assert(p.select[Address] == Address("DTC"))
    
  }
  
  test("Field Access") {
    
    assert(p[Name] == "Jo")
    assert(p(Name) == "Jo")
    
    assert(p[Age] == 42)
    assert(p(Age) == 42)
    
    assert(p[Address] == "DTC")
    assert(p(Address) == "DTC")
    
    // Note: this doesn't work although CanAccess[A,R] extends (A => R), so it's surprising:
    assertDoesNotCompile(""" p:Age """)
    
    // Note: these don't work, which is expected:
    assertDoesNotCompile(""" Age(0):Int """)
    assertDoesNotCompile(""" 0:Age """)
    
  }
  
  test("Fields are Monoids") {
    
    implicitly[Monoid[Age]]
    
    assert((Age(4) |+| Age(6)) == Age(10))
    assert((Name("A") ~ Age(4) |+| Name("B") ~ Age(6)) == Name("AB") ~ Age(10))
    
  }
  
  test("Record Normalization") {
    
    assert(("a"~("b"~1)).normalize == "a"~"b"~1)
    
    val model = 1 ~ 2 ~ 3 ~ 4
    assert((1 ~ 2 ~ 3 ~ 4).normalize == model)
    assert((1 ~ 2 ~ (3 ~ 4)).normalize == model)
    assert((1 ~ (2 ~ (3 ~ 4))).normalize == model)
    assert(((1 ~ 2) ~ (3 ~ 4)).normalize == model)
    assert((1 ~ (2 ~ 3) ~ 4).normalize == model)
    assert((1 ~ ((2 ~ 3) ~ 4)).normalize == model)
    
    assert((1 ~ ((2 ~ 3) ~ 4) ~ (5 ~ 6)).normalize == 1 ~ 2 ~ 3 ~ 4 ~ 5 ~ 6)
    
  }
  
  test("Pairing Up Fields") {
    
    assert(the[PairUp[Int,Int]].ls(0,1).toSet == Set(FieldPair(0,1)))
    assert(the[PairUp[Int~String,Int]].ls(0~"ok",1).toSet == Set(FieldPair(0,1)))
    assert(the[PairUp[Int,String~Int]].ls(0,"ko"~1).toSet == Set(FieldPair(0,1)))
    assert(the[PairUp[Int~String,String~Int]].ls(0~"ok","ko"~1).toSet == Set(FieldPair(0,1), FieldPair("ok","ko")))
    
    assert(the[PairUp[Int~String~Symbol,Symbol~Int~String]].ls(0~"ok"~'a,'b~1~"ko").toSet ==
      Set(FieldPair(0,1), FieldPair("ok","ko"), FieldPair('a,'b)))
    
    assert(the[PairUp[Int~String~Symbol,Symbol~String~Int]].ls(0~"ok"~'a,'b~"ko"~1).toSet ==
      Set(FieldPair(0,1), FieldPair("ok","ko"), FieldPair('a,'b)))
    
  }
  
  /*
  test("Funky Fields Stuff") {
    //val p2 = p.map[Age](_ + 1)
    //val p2 = p.focus[Age].map(_ + 1) // missing parameter type
    //val p2 = p.focus[Age].map[Int](_ + 1)
    //val p2 = p.focus(Age).map(_ + 1) // ok
    val p2 = p.map(Age)(_ + 1)
    // ^ TODO impl
  }
  */
  
}

class RecordEmbeddingTests extends FunSuite {
  import Embedding.Predef._
  import Embedding.Quasicodes._
  
  test("Removal of Wrapping Abstractions") {
    
    assert(code{Name("A") ~ Age(4) |+| Name(readLine) ~ Age(readInt)} =~= 
           code{Name("A" + readLine) ~ Age(4 + readInt)})
    
    assert(code{Name(readDouble.toString) ~ Age(readDouble.toInt) |+| Name(readLine) ~ Age(readInt)} =~= 
           code{ // effects order is maintained:
             val rds = readDouble.toString
             val rdi = readDouble.toInt
             Name(rds + readLine) ~ Age(rdi + readInt)})
    
    assert(code{Name("A") ~ Age(4) |+| Name("B") ~ Age(6)} =~= 
           code{Name("A" + ${Const("B")}) ~ Age(4 + ${Const(6)})})
    
  }
  
  test("Removal of Access Abstractions") {
    
    assert(code"p[Age]" =~= code"Age.deapply(p.lhs.lhs.rhs)")
    
    assert(code"p(Age)" =~= code"Age.deapply(p.lhs.lhs.rhs)")
    
    assert(code{ val a = Name("Jo") ~ Age(readInt) ~ Gender(Male); a[Age] + a[Age] }
       =~= code{ val r = readInt; r + r })
    
    assert(code{ val a = Name(readLine) ~ Age(readInt) ~ Gender(Male); a[Age] + (Address("DTC") ~ Age(1))[Age] }
       =~= code{ readLine; readInt + 1 })
    
  }
  
  test("Removal of Project Abstraction") {
    
    assert(code"p.project[Age ~ Name]" =~= code"p.lhs.lhs.rhs ~ p.lhs.lhs.lhs")
    
  }
  
  test("Removal of Normalization Abstraction") {
    
    assert(code"(1 ~ ((2 ~ 3) ~ 4)).normalize" == code"1 ~ 2 ~ 3 ~ 4")
    // ^ without normaliation, this generates dozens of lines of code!!
    
  }
  
  test("Removal of Natural Join Abstraction") {
    import example.tpch.OrderKey
    import example.tpch.QueryTests.{lineitem,orders}
    
    assert(code"lineitem naturallyJoining orders.head" == 
           code"val h = orders.head; lineitem.filter(_.select[OrderKey] == h.select[OrderKey])")
    
    // ^ Note: without normalization of PairUp, we end up with:
    
    /*
    {
      val x_0 = tpch.QueryTests.orders.head;
      val x_3 = new PairUpNorm[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], tpch.OrderKey](((t0_1: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], t1_2: tpch.OrderKey) => scala.collection.immutable.Nil.::[FieldPair](FieldPair.apply[tpch.OrderKey](t0_1.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs.lhs, t1_2))));
      val x_6 = new PairUpNorm[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[tpch.OrderKey, tpch.CustKey]](((l_4: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ht_5: ~[tpch.OrderKey, tpch.CustKey]) => x_3.ls.apply(l_4, ht_5.lhs)));
      val x_9 = new PairUpNorm[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus]](((l_7: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ht_8: ~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus]) => x_6.ls.apply(l_7, ht_8.lhs)));
      val x_12 = new PairUpNorm[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice]](((l_10: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ht_11: ~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice]) => x_9.ls.apply(l_10, ht_11.lhs)));
      val x_15 = new PairUpNorm[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate]](((l_13: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ht_14: ~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate]) => x_12.ls.apply(l_13, ht_14.lhs)));
      val x_18 = new PairUpNorm[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate], tpch.OrderPriority]](((l_16: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ht_17: ~[~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate], tpch.OrderPriority]) => x_15.ls.apply(l_16, ht_17.lhs)));
      val x_21 = new PairUp[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate], tpch.OrderPriority]](((l_19: ~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], r_20: ~[~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate], tpch.OrderPriority]) => x_18.ls.apply(l_19, ~.apply[~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate], tpch.OrderPriority](r_20.lhs, r_20.rhs))));
      tpch.QueryTests.lineitem.naturallyJoining[~[~[~[~[~[~[~[~[~[~[~[~[~[~[~[tpch.OrderKey, tpch.PartKey], tpch.SuppKey], tpch.LineNumber], tpch.Quantity], tpch.ExtendedPrice], tpch.Discount], tpch.Tax], tpch.ReturnFlag], tpch.Linestatus], tpch.ShipDate], tpch.CommitDate], tpch.ReceiptDate], tpch.ShipInstruct], tpch.ShipMode], tpch.Comment], ~[~[~[~[~[tpch.OrderKey, tpch.CustKey], tpch.OrderStatus], tpch.TotalPrice], tpch.OrderDate], tpch.OrderPriority]](x_0)(x_21)
    }"""
    */
    
  }
  
}

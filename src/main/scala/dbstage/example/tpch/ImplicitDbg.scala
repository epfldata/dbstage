package dbstage
package example.tpch

import dbstage2.{FieldModule, ::, Project, Record, Access, PairUp, Field, NoFields}

object ImplicitDbg extends App {
  
  //implicitly[Read[OrderKey{type Typ=Int}]]
  //implicitly[Read[OrderKey]]
  //implicitly[Read[Field[Int]]]
  //implicitly[Read[Field[String]]]
  //implicitly[Read[OrderStatus]]
  
}

package dbstage2

import squid.utils._
import Embedding.Predef._
import Embedding.SimplePredef.{Rep=>Code,_}

class Serial[T](val parse: Code[String] => Code[T], val unparse: Code[T] => Code[String])
object Serial {
  implicit val SerialString = new Serial[String](identity, identity)
  implicit val SerialInt = new Serial[Int](str => ir"$str.toInt", x => ir"$x.toString")
  implicit val SerialBool = new Serial[Bool](str => ir"$str.toBoolean", x => ir"$x.toString")
  implicit def SerialOption[A:IRType:Serial] = {
    val serialA = implicitly[Serial[A]]
    new Serial[Option[A]](
      str => ir"""if ($str == "NULL") None else Some(${serialA.parse(str)})""", 
      x   => ir"""$x.fold("NULL")(${serialA.unparse})"""
    )
  }
}

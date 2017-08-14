package dbstage2
package runtime

import squid.utils._

case class Column(name: String, data: Array[Any])
case class Table(cols: Array[Column]) {
  //assert(cols.map(_.data.length).fold[Bool](true)(_ == _))
  val num = cols.headOption.map(_.data.length)
  assert(cols.forall(_.data.length == num.get))
  override def toString: String = num.fold("<empty table>"){ num =>
    //s"${cols map (_.name) mkString "|"}"
    val colStrs = cols.map(c => c.name -> c.data.map(_.toString)).toMap
    //s"${cols map (_.name) mkString "|"}"
    //def printLine(line: A)
    //var i = 0
    //while(i < num)
    //  cols.map { c =>
    //    colStrs(c.name)(i)
    //  } mkString ("|","|","|")
    //""
    def pad(str: String, len: Int) = if (str.length < len) str + " " * (len - str.length) else str
    //val colStrMaxLens = colStrs.mapValues(_.maxBy(_.length))
    val colStrMaxLens = colStrs.mapValues(_.iterator.map(_.length).max)
    s"${cols map (_.name) map (n => pad(n,colStrMaxLens(n))) mkString ("|","|","|")}\n${
      "-" * colStrMaxLens.valuesIterator.fold(0)(_ + _ + 2)
    }${
      (for (i <- 0 until num iterator) yield cols.map(c => pad(colStrs(c.name)(i),colStrMaxLens(c.name))) mkString ("|","|","|")) mkString "\n"
    }"
  }
}


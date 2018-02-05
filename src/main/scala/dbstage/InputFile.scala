package dbstage

import dbstage.example.tpch.Date
import shapeless.the
import dbstage2.Embedding.Predef.{CodeType, codeTypeOf}
import dbstage2.{FieldModule, ::, Project, Record, Access, PairUp, NoFields}

class InputFile[A:CodeType:RecordRead](fileName: String)(implicit codec: scala.io.Codec) extends Relation[A] {
  //private[this] 
  val rr = the[RecordRead[A]]
  val separator = '|'
  
  lazy val data = scala.io.Source.fromFile(fileName).getLines.map(rr.read(separator)).toBuffer
  
  def iterator = data.iterator
  
}

import dbstage2.Embedding.Predef._

abstract class BuildField[F<:FieldModule] extends (F#Typ => F) {
//abstract class BuildField[F<:FieldModule:CodeType](implicit Typ: CodeType[Typ]) extends (F#Typ => F) {
  def staged: ClosedCode[F#Typ => F]
}
object BuildField {
  import scala.language.experimental.macros
  implicit def build[T]: BuildField[T] = macro BuildImplicitGen.buildImplicitGen[T]
  def fromFunction[A,F<:FieldModule{type Typ=A}](f: A => F) = new BuildField[F] {
    def apply(a: A): F = f(a)
    def staged = ???
  }
}

//trait Read[+T] { def read: String => T }
//trait Read[+T] { def apply(str: String): T }
abstract class Read[T:CodeType] { def apply(str: String): T; def staged: ClosedCode[String => T] }
// TODO add implicitNotFound
object Read {
  //implicit def build[]: Build = ???
  
  //implicit def readField[F<:FieldModule:BuildField](implicit readTyp: Read[F#Typ]): Read[F] = ???
  implicit def readField[F<:FieldModule:BuildField:CodeType](implicit readTyp: Read[F#Typ], Typ: CodeType[F#Typ]): Read[F] = new Read[F] {
    def apply(str: String) = the[BuildField[F]].apply(readTyp(str))
    def staged = code"${the[BuildField[F]].staged} apply ${readTyp.staged}(_:String)"
  }
  
  implicit object readDouble extends Read[Double] { def apply(str: String) = str.toDouble; val staged = code"(_:String).toDouble" }
  implicit object readInt extends Read[Int] { def apply(str: String) = str.toInt; val staged = code"(_:String).toInt" }
  implicit object readLong extends Read[Long] { def apply(str: String) = str.toLong; val staged = code"(_:String).toLong" }
  implicit object readString extends Read[String] { def apply(str: String) = str; val staged = code"(s:String)=>s" }
  //implicit def readString: Read[String] = new Read[String] { def apply(str: String) = str }
  implicit object readDate extends Read[Date] { def apply(str: String) = Date(str); val staged = code"Date(_:String)" }
}
trait RecordRead[T] { def read(separator: Char): String => T }
object RecordRead {
  implicit object readNoFields extends RecordRead[NoFields] { def read(separator: Char) = _ => NoFields }
  implicit def readField[F<:FieldModule,R](implicit readF: Read[F], next: RecordRead[R]): RecordRead[F::R] =
    new RecordRead[F::R] {
      def read(separator: Char) = str => {
        val (hd,tl) = str.span(_ != separator)
        //println(s"Read '$str' = '$hd'+'$tl'")
        ::(readF(hd), next.read(separator)(if (tl.isEmpty) tl else tl.tail))
      }
    }
}


import shapeless._
import shapeless.record._
import shapeless.poly._
import ops.hlist.ToList
import ops.record.{Keys, Values}
import squid.lib.transparencyPropagating

package object dbstage2 extends RecordsPredef {
  
  //type NoFields = NoFields.type
  //type NF = NoFields.type
  
  import scala.language.implicitConversions
  
  @transparencyPropagating
  implicit def toAccessOps[T](self: T): AccessOps[T] = new AccessOps(self)
  
  //type PlainQuery[T] = PlainQuery.Query[T,Any]
  
  
  import Embedding.Predef._
  /*
  /*
  //implicit def rowRepBase[T:IRType]: RowRep[T] = new RowRep[T] 
  //implicit def rowRepExt[T0:IRType,T1 <: HList:IRType]: RowRep[T0 :: T1] = new RowRep[T0 :: T1]
  implicit def rowRepBase[T:IRType]: RowRep[T] = new RowRepBase 
  implicit def rowRepExt[T0:IRType,T1 <: HList:RowRep]: RowRep[T0 :: T1] = 
    //RowRepExt(the[RowRep[T0]],the[RowRep[T1]]) 
    new RowRepExt 
  */
  
  class RowRep[+L <: HList:Keys:RowTypesRep]
  //sealed abstract class RowTypesRep[L]
  //case class RowTypesRepBase[T](implicit val T: IRType[T]) extends RowTypesRep[A]
  //case class RowTypesRepExt[T0,T1 <: HList](implicit val T0: RowTypesRep[T0], implicit val T1: RowTypesRep[T1]) extends RowTypesRep[T0 :: T1]
  class RowTypesRep[+L](val typs: List[IRType[_]])
  case class RowTypesRepBase[T](implicit val T: IRType[T]) extends RowTypesRep[T :: HNil](T :: Nil)
  case class RowTypesRepExt[T0,T1 <: HList](implicit val T0: IRType[T0], implicit val T1: RowTypesRep[T1]) extends RowTypesRep[T0 :: T1](T0 :: T1.typs)
  */
}


package dbstagetests
package v4

import squid.utils._

import Ops._
import SimpleRecordTests.p
import SimpleRecordTests.Person

object RecordTestsDebug extends App {
  
  //p.project[NoFields]
  //p.project[Age :: NoFields]
  
  //implicitly[(Age :: NoFields) Project NoFields]
  //implicitly[(Age :: NoFields) Project (Age :: NoFields)]
/* ^^^
[info] v4/RecordTestsDebug.scala:16: v4.this.Access.accessFirst is not a valid implicit value for dbstage.v4.Access[dbstage.v4.Age,dbstage.v4.::[dbstage.v4.Age,dbstage.v4.Ops.NoFields],A] because:
[info] typing TypeApply reported errors for the implicit tree: type arguments [dbstage.v4.Age,Any,Nothing] do not conform to method accessFirst's type parameter bounds [F <: dbstage.v4.Field[A],R,A]
[info]   implicitly[(Age :: NoFields) Project (Age :: NoFields)]
[info]             ^
*/
  
  //implicitly[Access[Age,Age :: NoFields,Int]]
  //implicitly[Access[Age,Person,Int]]
  
  
}

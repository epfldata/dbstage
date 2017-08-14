package dbstage2
package runtime

import squid.lib.transparencyPropagating
import squid.utils._

object QueryOps {
  
  class Table
  class RowStore extends Table
  class ColumnStore extends Table
  
  //def hashJoin
  
  
  
}

object GenericOps {
  
  @transparencyPropagating def add(lhs: Any, rhs: Any): Any = ???
  
  @transparencyPropagating def equ(lhs: Any, rhs: Any): Bool = ???
  @transparencyPropagating def lt(lhs: Any, rhs: Any): Bool = ???
  @transparencyPropagating def gt(lhs: Any, rhs: Any): Bool = ???
  @transparencyPropagating def and(lhs: Any, rhs: Any): Bool = ???
  @transparencyPropagating def not(self: Any): Bool = ???
  
  //@transparencyPropagating def col[A](hole: A): A = hole
  ////@transparencyPropagating def colQualified[A](hole: A, isFromLhs: Bool): A = hole
  //@transparencyPropagating def colFromLHS[A](hole: A): A = hole
  //@transparencyPropagating def colFromRHS[A](hole: A): A = hole
  
  @transparencyPropagating def col[A](name: String): A = ???
  
}
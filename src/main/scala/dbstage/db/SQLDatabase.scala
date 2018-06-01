package dbstage.db

// TODO
class SQLDatabase {
  
  def createTable(columns: (String,String)*): this.type = ???
  def createTable_as(): this.type = ???
  
  /** extracts the result into a usable Database object */
  def database: Database = ???
  
}
object SQLDatabase {
  def fromText(text: String): SQLDatabase = ??? // TODO use dblab parsing infrastructure
}


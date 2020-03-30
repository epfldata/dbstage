package dbstage.deep

import dbstage.lang.{Table, TableView}

import IR.Predef._

/** This class creates the String representation of the compiled
 * low-level database implementation. */
trait DatabaseCompiler { self: StagedDatabase =>
  
  // TODO use better code-stringification facilities (LP will add them later)
  
  def compile: String = {
    
    // Temporary representation of classes; will change/be configurable
    val dataClasses = knownClasses.map { cls =>
      val body = cls.fields.foldRight("") {
        case (f, acc) =>
          import f.A
          val init = f.init match {
            case Some(value) => s" = ${value.showScala}"
            case None => ""
          }
          val previous = if (acc.nonEmpty) s", $acc" else ""

          s"  ${f.name}: ${A.rep}$init$previous"
      }
      s"case class ${cls.name}${
        if (cls.tparams.isEmpty) "" else cls.tparams.map(_.name).mkString("[", ", ", "]")
      } (" +
        body +
      ")"
    }
    val methods = knownMethods.map { case (_, mtd) =>
      s"def ${mtd.variable.toCode.showScala}(${mtd.owner.self.toCode.showScala}: ${mtd.owner.C.rep}): " +
        s"${mtd.body.Typ.rep} = ${mtd.body.showScala}"
    }
    val fieldGetters = knownFieldGetters.map { case (_, getter) =>
      s"def ${getter.getter.toCode.showScala}(${getter.owner.self.toCode.showScala}: ${getter.owner.C.rep}): " +
        s"${getter.typ} = ${getter.owner.self.toCode.showScala}._${getter.index}"
    }
    val fieldSetters = knownFieldSetters.map { case (_, setter) =>
      s"def ${setter.setter.toCode.showScala}(${setter.owner.self.toCode.showScala}: ${setter.owner.C.rep}, ${setter.field.name}: ${setter.field.A.rep}): " +
        s"Unit = ${setter.owner.self.toCode.showScala}._${setter.index} = ${setter.field.name}"
    }
    // For now, use an mutable.ArrayBuffer; in the future it will use LMDB
    val tables = tablesMapping.map { case (_, tbl) =>
      s"val ${tbl.variableInGeneratedCode.toCode.showScala} = mutable.ArrayBuffer.empty[${tbl.T.rep}]"
    }
    val queries = knownQueries.map { q =>
      val rep = q.rep
      s"def ${q.name} = " + planQuery(rep).getCode.showScala
    }
    
    s"""
    import scala.collection.mutable
    object $dbName {
      ${dataClasses.mkString("\n")}
      ${methods.mkString("\n")}
      ${fieldGetters.mkString("\n")}
      ${fieldSetters.mkString("\n")}
      ${tables.mkString("\n")}
      ${queries.mkString("\n")}
    }
    """.replaceAll("@", "_")
  }
  
}

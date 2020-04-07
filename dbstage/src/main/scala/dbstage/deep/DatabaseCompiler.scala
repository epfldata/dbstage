package dbstage.deep

import dbstage.lang.{Table, TableView, Str}

import IR.Predef._

/** This class creates the String representation of the compiled
 * low-level database implementation. */
trait DatabaseCompiler { self: StagedDatabase =>
  
  // TODO use better code-stringification facilities (LP will add them later)

  def createParamTuple(params: List[IR.Variable[_]]): (String, List[String]) = {
    if (params.length > 1) {
      (s"params: Tuple${params.length}[${params.map(p => p.Typ.rep).mkString(", ")}]", params.zipWithIndex.map(p => s"params._${p._2+1}"))
    } else if (params.length == 1) {
      (s"param: ${params.head.Typ.rep}", List("param"))
    } else {
      throw new IllegalArgumentException
    }
  }
  
  def compile: String = {
    
    // Temporary representation of classes; will change/be configurable
    val dataClasses = knownClasses.values.map( tableRep => tableRep.cls )
    val classes = dataClasses.map { cls =>
      cls.fields.foreach( field => {
        val knownDataType = dataClasses.exists(c => c.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        val knownPrimitiveType = field.A =:= codeTypeOf[Int] || field.A =:= codeTypeOf[Double] ||
                                 field.A =:= codeTypeOf[Str]

        if (field.A.=:=(codeTypeOf[String])) {
          throw new IllegalArgumentException(s"Class ${cls.name} has parameter with unsupported type String, please use Str")
        } else if(!(knownPrimitiveType || knownDataType)) {
          throw new IllegalArgumentException(s"Class ${cls.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        }
      })

      s"type ${cls.name} = CStruct${cls.fields.size}${
        cls.fields.map(f => s"${f.A.rep}").mkString("[", ", ", "]")
      }"
    }
    val cstringConstructor = {
      s"def ${strConstructor.constructor.toCode.showScala}(str: String): ${strConstructor.owner.C.rep} = " +
      "toCString(str)"
    }
    val cstringMethods = strMethods.map { case (_, mtd) => 
      val (paramTypes, params) = createParamTuple(mtd.owner.self :: mtd.params)
      s"def ${mtd.variable.toCode.showScala}(${paramTypes})" +
        s": ${mtd.typ} = ${mtd.symbol.name}" +
        s"(${params.mkString(",")})"
    }
    val constructors = knownConstructors.map { case (_, constructor) =>
      val (paramTypes, params) = createParamTuple(constructor.params)
      s"def ${constructor.constructor.toCode.showScala}(${paramTypes})" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val ${constructor.owner.self.toCode.showScala}: ${constructor.owner.C.rep} = alloc[${constructor.owner.C.rep}]\n" +
        (for ( i <- 0 until constructor.params.length) yield s"${constructor.owner.self.toCode.showScala}._${i+1} = ${params(i)}").mkString("\n") +
        s"\n${constructor.owner.self.toCode.showScala}" +
        "\n}"
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
    
    val program = s"""
    import scala.collection.mutable
    import scala.scalanative.unsafe._
    import lib.string._
    import lib.str._

    object $dbName {
      ${cstringConstructor}\n
      ${cstringMethods.mkString("\n")}\n
      ${classes.mkString("\n")}\n
      ${constructors.mkString("\n")}\n
      ${methods.mkString("\n")}\n
      ${fieldGetters.mkString("\n")}\n
      ${fieldSetters.mkString("\n")}\n
      ${tables.mkString("\n")}\n
      ${queries.mkString("\n")}
    }
    """.replaceAll("@", "_")
    .replaceAll("dbstage\\.lang\\.Str", "CString")

    val regex = "\\.apply(?:\\[[^\\n\\r]+\\])?(\\([^\\n\\r]+\\))"
    val matches = regex.r.findAllMatchIn(program)
    matches.foreach(m => println(s"Replacing ${m.source.subSequence(m.start, m.end)} with ${m.group(1)}"))

    program.replaceAll(regex, "$1")
  }
  
}

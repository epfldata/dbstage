package dbstage.deep

import dbstage.lang.{TableView, Str}

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
    val implicitZoneParam: String = "(implicit zone: Zone)"
    val keyType: String = "Long" // TODO: Put key type somewhere else and use it everywhere (getAt, ...)

    val strSymbol = codeTypeOf[Str].rep.tpe.typeSymbol

    val sizes = List("Int", keyType, "Double").map(typ => s"val size${typ} = sizeof[${typ}].toInt")
    
    // Temporary representation of classes; will change/be configurable
    val classes = knownClasses.filter(p => p._1 != strSymbol).values.map { tableRep =>
      val cls = tableRep.cls

      val fields = cls.fields.map( field => {
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        val knownPrimitiveType = field.A =:= codeTypeOf[Int] || field.A =:= codeTypeOf[Double]

        // Add String back?
        if (field.A =:= codeTypeOf[String]) {
          throw new IllegalArgumentException(s"Class ${cls.name} has parameter with unsupported type String, please use Str")
        }

        if (knownDataType) {
          s"var ${field.name.replaceAll("\\s+", "")}Id: ${keyType}, var ${field.name}: ${field.A.rep} = null"
        } else if (knownPrimitiveType) {
          s"var ${field.name}: ${field.A.rep}"
        } else {
          throw new IllegalArgumentException(s"Class ${cls.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        }
      })

      val typesKey = cls.fields.map(field => {
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        if (knownDataType) {
          s"size${keyType}"
        } else {
          s"size${field.A.rep}"
        }
      })

      s"class ${cls.name}(val key: ${keyType}, ${fields.mkString(",")})\n" +
      s"val size${cls.name} = ${typesKey.mkString("+")}\n" +
      s"""lazy val ${tableRep.variable.toCode.showScala} = new LMDBTable[${tableRep.T.rep}]("${cls.name}")\n"""
    }

    val strClass = {
      val tableRep = knownClasses(strSymbol)
      val cls = tableRep.cls

      assert(cls.fields.length == 1)
      val field = cls.fields(0)
      assert(field.A =:= codeTypeOf[String])

      s"class ${cls.name}(val key: ${keyType}, var ${field.name}: CString)\n" +
      s"""lazy val ${tableRep.variable.toCode.showScala} = new LMDBTable[${tableRep.T.rep}]("${cls.name}")\n"""
    }

    val tableGetters = knownTableGetters.map { case (_, tblGetter) =>
      val ptrName = "ptr"
      val valueName = "value"

      val computeValues = tblGetter.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)

        (if (field.A =:= codeTypeOf[Int]) {
          s"val ${valueName}${index} = intget(${ptrName}${index}, size${field.A.rep})\n" +
          s"val ${ptrName}${index+1} = ${ptrName}${index} + size${field.A.rep}"
        } else if (knownDataType) {
          s"val ${valueName}${index} = longget(${ptrName}${index}, size${keyType})\n" +
          s"val ${ptrName}${index+1} = ${ptrName}${index} + size${keyType}"
        } else if (field.A =:= codeTypeOf[String]) {
          s"val ${valueName}${index} = ${ptrName}${index}"
          // String only in Str and last field -> no need to compute size
        } else {
          throw new IllegalArgumentException(s"Class ${tblGetter.owner.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        })
      }}

      val args = tblGetter.owner.fields.zipWithIndex.map(f => s"${valueName}${f._2+1}")
      val tupleArgs = if(args.length == 1) s"${args(0)}" else s"(${args.mkString(",")})"

      s"def ${tblGetter.getter.toCode.showScala}(table: LMDBTable[${tblGetter.owner.C.rep}], key: ${keyType})${implicitZoneParam}: ${tblGetter.owner.C.rep} = {\n" +
      s"val ${ptrName}1 = table.get(key)\n" +
      s"${computeValues.mkString("\n")}\n" +
      s"${knownConstructors(tblGetter.owner.constructor.symbol).constructor.toCode.showScala}(key, ${tupleArgs})\n" +
      "}"
    }

    val tablePutters = knownTablePutters.map { case (_, tblPutter) =>
      val paramNames = "new_value"
      val valueName = "value"

      val size = if(tblPutter.owner.C =:= codeTypeOf[Str]) {
        s"strlen(${paramNames}.string)"
      } else {
        s"size${tblPutter.owner.name}"
      }

      val fillInValue = tblPutter.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)

        (if (field.A =:= codeTypeOf[String]) {
          s"strcpy(${valueName}${index}, ${paramNames}.${field.name})"
          // String only in Str and last field -> no need to compute size
        } else if (field.A =:= codeTypeOf[Int]) {
          s"intcpy(${valueName}${index}, ${paramNames}._${index}, size${field.A.rep})\n" +
          s"val ${valueName}${index+1} = ${valueName}${index} + size${field.A.rep}"
        } else if (knownDataType) {
          s"longcpy(${valueName}${index}, ${paramNames}._${index}, size${keyType})\n" +
          s"val ${valueName}${index+1} = ${valueName}${index} + size${keyType}"
        } else {
          throw new IllegalArgumentException(s"Class ${tblPutter.owner.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        })
      }}

      s"def ${tblPutter.putter.toCode.showScala}(table: LMDBTable[${tblPutter.owner.C.rep}], ${paramNames}: ${tblPutter.owner.C.rep})${implicitZoneParam}: Unit = {\n" +
      s"val key = table.size\n" +
      s"val size = ${size}\n" +
      s"val value1 = alloc[Byte](size)\n" +
      s"${fillInValue.mkString("\n")}\n" +
      s"table.put(key, size, value1)\n" +
      "}"
    }

    val constructors = knownConstructors.flatMap { case (_, constructor) =>
      val dataTypeField = constructor.owner.fields.exists(field => knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol))
      val (paramTypes, _) = createParamTuple(constructor.params)
      val typesKey = constructor.params.map(p => {
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == p.Typ.rep.tpe.typeSymbol)
        if (knownDataType) {
          keyType
        } else {
          s"${p.Typ.rep}"
        }
      })
      val paramTypesKey = if (typesKey.length == 1) {
        s"param: ${typesKey(0)}"
      } else {
        s"params: Tuple${typesKey.length}[${typesKey.mkString(", ")}]"
      }

      val (keyConstructor, valueConstructor) = constructor.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        val param = if(constructor.params.length == 1) {
          "param"
        } else {
          s"params._${index}"
        }
        
        if (knownDataType) {
          (s"${param}, null", s"${param}.key, ${param}")
        } else {
          (s"${param}", s"${param}")
        }
      }}.unzip

      val table = knownClasses(constructor.owner.C.rep.tpe.typeSymbol).variable.toCode.showScala
      val put = knownClasses(constructor.owner.C.rep.tpe.typeSymbol).putter.toCode.showScala

      val valueConstructors = List(s"def ${constructor.constructor.toCode.showScala}(key: ${keyType}, ${paramTypes})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = new ${constructor.owner.name}(key, ${valueConstructor.mkString(",")})\n" +
        s"new_val\n" +
        "}",
        s"def ${constructor.constructor.toCode.showScala}(${paramTypes})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = new ${constructor.owner.name}(${table}.size, ${valueConstructor.mkString(",")})\n" +
        s"${put}(${table}, new_val)\n" +
        s"new_val\n" +
        "}")

      if (dataTypeField) {
        List(s"def ${constructor.constructor.toCode.showScala}(key: ${keyType}, ${paramTypesKey})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = new ${constructor.owner.name}(key, ${keyConstructor.mkString(",")})\n" +
        s"new_val\n" +
        "}",
        s"def ${constructor.constructor.toCode.showScala}(${paramTypesKey})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = new ${constructor.owner.name}(${table}.size, ${keyConstructor.mkString(",")})\n" +
        s"${put}(${table}, new_val)\n" +
        s"new_val\n" +
        "}") ++ valueConstructors
      } else {
        valueConstructors
      }
    }

    val methods = knownMethods.filter(p => p._2.owner.C.rep.tpe.typeSymbol != strSymbol).map { case (_, mtd) =>
      s"def ${mtd.variable.toCode.showScala}(${mtd.owner.self.toCode.showScala}: ${mtd.owner.C.rep})${implicitZoneParam}: " +
        s"${mtd.body.Typ.rep} = ${mtd.body.showScala}"
    }

    val stringMethods = knownMethods.filter(p => p._2.owner.C.rep.tpe.typeSymbol == strSymbol).map { case (_, mtd) => 
      val (paramTypes, params) = createParamTuple(mtd.owner.self :: mtd.vparamss.headOption.getOrElse(Nil))
      s"def ${mtd.variable.toCode.showScala}(${paramTypes})${implicitZoneParam}" +
        s": ${mtd.typ} = ${mtd.symbol.name}" +
        s"(${params.mkString(",")})"
    }

    val fieldGetters = knownFieldGetters.map { case (_, getter) =>
      s"def ${getter.getter.toCode.showScala}(${getter.owner.self.toCode.showScala}: ${getter.owner.C.rep})${implicitZoneParam}: " +
        s"${getter.typ} = ${getter.owner.self.toCode.showScala}.${getter.name}"
    }

    val fieldSetters = knownFieldSetters.map { case (_, setter) =>
      s"def ${setter.setter.toCode.showScala}(${setter.owner.self.toCode.showScala}: ${setter.owner.C.rep}, ${setter.name}: ${setter.field.A.rep})" +
        s"${implicitZoneParam}: Unit = ${setter.owner.self.toCode.showScala}.${setter.name} = ${setter.name}"
    }

    val queries = knownQueries.map { q =>
      val rep = q.rep
      s"def ${q.name} = Zone { implicit zone => \n" + planQuery(rep).getCode.showScala + "\n}"
    }
    
    val program = s"""
    import scala.collection.mutable
    import scala.scalanative.unsafe._
    import lib.string._
    import lib.str._
    import lib.LMDBTable
    import lib.LMDBTable._

    object $dbName {
      ${sizes.mkString("\n")}\n
      ${classes.mkString("\n")}\n
      ${strClass}\n
      ${tableGetters.mkString("\n")}\n
      ${tablePutters.mkString("\n")}\n
      ${constructors.mkString("\n")}\n
      ${methods.mkString("\n")}\n
      ${stringMethods.mkString("\n")}\n
      ${fieldGetters.mkString("\n")}\n
      ${fieldSetters.mkString("\n")}\n
      ${queries.mkString("\n")}
    }
    """.replaceAll("@", "_")
    .replaceAll("dbstage\\.lang\\.Str", "Str")

    val regex = "\\.apply(?:\\[[^\\n\\r]+\\])?(\\([^\\n\\r]+\\))"
    val matches = regex.r.findAllMatchIn(program)
    matches.foreach(m => println(s"Replacing ${m.source.subSequence(m.start, m.end)} with ${m.group(1)}"))

    program.replaceAll(regex, "$1")
  }
  
}

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

      s"class ${cls.name}(val key: ${keyType}, ${fields.mkString(",")})\n" +
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
      val sizeName = "size"
      val paramNames = "fields"
      val ptrName = "ptr"
      val valueName = "value"

      val computeSizes = tblGetter.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        s"val ${sizeName}${index} = sizeof[${field.A.rep}].toInt"
      }}
      val computeSize = s"val size = ${tblGetter.owner.fields.zipWithIndex.map(f => s"size${f._2+1}").mkString("+")}"

      val computeValues = tblGetter.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1

        s"val ${ptrName}${index} = ${ptrName}${index-1} + ${sizeName}${index-1}\n" +
        (if (field.A =:= codeTypeOf[Int]) {
          s"val ${valueName}${index} = intget(${ptrName}${index}, ${sizeName}${index})"
        } else {
          throw new IllegalArgumentException(s"Class ${tblGetter.owner.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        })
      }}

      s"def ${tblGetter.getter.toCode.showScala}(table: LMDBTable[${tblGetter.owner.C.rep}], key: ${keyType})${implicitZoneParam}: ${tblGetter.owner.C.rep} = {\n" +
      s"val ${ptrName}0 = table.get(key)\n" +
      s"val ${sizeName}0 = 0l\n" +
      s"${computeSizes.mkString("\n")}\n" +
      s"${computeSize}\n" +
      s"${computeValues.mkString("\n")}\n" +
      s"${knownConstructors(tblGetter.owner.constructor.symbol).constructor.toCode.showScala}(${tblGetter.owner.fields.zipWithIndex.map(f => s"${valueName}${f._2+1}").mkString(",")})\n" +
      "}"
    }

    val tablePutters = knownTablePutters.map { case (_, tblPutter) =>
      val sizeName = "size"
      val paramNames = "fields"
      val valueName = "value"

      val computeSizes = tblPutter.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        if (field.A =:= codeTypeOf[Str]) {
          s"val ${sizeName}${index} = strlen(${paramNames}._${index})"
        } else {
          // TODO: Permit to have other class data (Job, ...) types, how?
          s"val ${sizeName}${index} = sizeof[${field.A.rep}]"
        }
      }}
      val computeSize = s"val size = ${tblPutter.owner.fields.zipWithIndex.map(f => s"size${f._2+1}").mkString("+")}"

      val fillInValue = tblPutter.owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1

        s"val ${valueName}${index} = value${index-1} + size${index-1}\n" +
        (if (field.A =:= codeTypeOf[Str]) {
          s"strcpy(${valueName}${index}, ${paramNames}._${index})"
        } else if (field.A =:= codeTypeOf[Int]) {
          s"intcpy(${valueName}${index}, ${paramNames}._${index}, ${sizeName}${index})"
        } else {
          throw new IllegalArgumentException(s"Class ${tblPutter.owner.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        })
      }}

      s"def ${tblPutter.putter.toCode.showScala}(table: LMDBTable[${tblPutter.owner.C.rep}], ${paramNames}: ${tblPutter.owner.C.rep})${implicitZoneParam}: Unit = {\n" +
      s"val key = table.size\n" +
      s"val size0 = 0l\n" +
      s"${computeSizes.mkString("\n")}\n" +
      s"${computeSize}\n" +
      s"val value0 = alloc[Byte](${sizeName})\n" +
      s"${fillInValue.mkString("\n")}\n" +
      s"val ${valueName} = ${valueName}0\n" +
      s"table.put(key, ${sizeName}, ${valueName})\n" +
      "}"
    }

    val cstringConstructor = {
      s"def ${strConstructor.constructor.toCode.showScala}(str: String)${implicitZoneParam}: ${strConstructor.owner.C.rep} = " +
      "toCString(str)"
    }

    val cstringMethods = strMethods.map { case (_, mtd) => 
      val (paramTypes, params) = createParamTuple(mtd.owner.self :: mtd.params)
      s"def ${mtd.variable.toCode.showScala}(${paramTypes})${implicitZoneParam}" +
        s": ${mtd.typ} = ${mtd.symbol.name}" +
        s"(${params.mkString(",")})"
    }

    val constructors = knownConstructors.map { case (_, constructor) =>
      val (paramTypes, params) = createParamTuple(constructor.params)
      s"def ${constructor.constructor.toCode.showScala}(${paramTypes})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val ${constructor.owner.self.toCode.showScala}: ${constructor.owner.C.rep} = alloc[${constructor.owner.C.rep}Data]\n" +
        (for ( i <- 0 until constructor.params.length) yield s"${constructor.owner.self.toCode.showScala}._${i+1} = ${params(i)}").mkString("\n") +
        s"\n${constructor.owner.self.toCode.showScala}" +
        "\n}"
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

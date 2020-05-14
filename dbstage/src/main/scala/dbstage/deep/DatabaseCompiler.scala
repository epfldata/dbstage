package dbstage.deep

import dbstage.lang.{TableView, Str, LMDBTable}

import IR.Predef._
import IR.Quasicodes._

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

    val cString = {
      s"def ${toCString.toCode.showScala}(string: String)${implicitZoneParam}: CString = {\n" +
      s"toCString(string)\n}"
    }

    val tableGetters = knownClasses.map { case (_, tableRep) =>
      val owner = tableRep.cls
      val table = tableRep.variable.toCode.showScala
      val fromPtrByte = tableRep.fromPtrByte.toCode.showScala

      s"def ${tableRep.getter.toCode.showScala}(key: ${keyType})${implicitZoneParam}: ${owner.C.rep} = {\n" +
      s"val value = ${table}.get(key)\n" +
      s"${fromPtrByte}(key, value)\n" +
      "}"
    }

    // https://github.com/epfldata/squid/pull/62
    code[Unit]{
        
    }    

    val tablePutters = knownClasses.map { case (_, tableRep) =>
      val owner = tableRep.cls
      val table = tableRep.variable.toCode.showScala
      val toPtrByte = tableRep.toPtrByte.toCode.showScala

      s"def ${tableRep.putter.toCode.showScala}(data_el: ${owner.C.rep})${implicitZoneParam}: Unit = {\n" +
      s"val (key, size, value) = ${toPtrByte}(data_el)\n" +
      s"${table}.put(key, size, value)\n" +
      "}"
    }

    val fromPtrByte = knownClasses.map { case (_, tableRep ) =>
      val owner = tableRep.cls
      val table = knownClasses(owner.C.rep.tpe.typeSymbol).variable.toCode.showScala
      var constructor = knownConstructors(owner.constructor.symbol).constructor.toCode.showScala
      val ptrName = "ptr"
      val valueName = "value"
      var dataTypeField = false

      val computeValues = owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        dataTypeField = dataTypeField || knownDataType

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
          throw new IllegalArgumentException(s"Class ${owner.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        })
      }}

      if (dataTypeField) constructor += "_key"
      val args = owner.fields.zipWithIndex.map(f => s"${valueName}${f._2+1}")
      val tupleArgs = if(args.length == 1) s"${args(0)}" else s"(${args.mkString(",")})"

      s"def ${tableRep.fromPtrByte.toCode.showScala}(key: ${keyType}, ${ptrName}1: Ptr[Byte])${implicitZoneParam}: ${owner.C.rep} = {\n" +
      s"${computeValues.mkString("\n")}\n" +
      s"${constructor}(key, ${tupleArgs})\n" +
      "}"
    }

    val toPtrByte = knownClasses.map { case (_, tableRep) => 
      val owner = tableRep.cls
      val table = knownClasses(owner.C.rep.tpe.typeSymbol).variable.toCode.showScala
      val paramNames = "new_value"
      val valueName = "value"

      val size = if(owner.C =:= codeTypeOf[Str]) {
        s"strlen(${paramNames}.string).toInt"
      } else {
        s"size${owner.name}"
      }

      val fillInValue = owner.fields.zipWithIndex.map { case (field, i) => {
        val index = i+1
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)

        (if (field.A =:= codeTypeOf[String]) {
          s"strcpy(${valueName}${index}, ${paramNames}.${field.name})"
          // String only in Str and last field -> no need to compute size
        } else if (field.A =:= codeTypeOf[Int]) {
          s"intcpy(${valueName}${index}, ${paramNames}.${field.name}, size${field.A.rep})\n" +
          s"val ${valueName}${index+1} = ${valueName}${index} + size${field.A.rep}"
        } else if (knownDataType) {
          s"longcpy(${valueName}${index}, ${paramNames}.${field.name.replaceAll("\\s+", "")}Id, size${keyType})\n" +
          s"val ${valueName}${index+1} = ${valueName}${index} + size${keyType}"
        } else {
          throw new IllegalArgumentException(s"Class ${owner.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol}")
        })
      }}

      s"def ${tableRep.toPtrByte.toCode.showScala}(${paramNames}: ${owner.C.rep})${implicitZoneParam}: (Long, Int, Ptr[Byte]) = {\n" +
      s"val key = ${paramNames}.key\n" +
      s"val size = ${size}\n" +
      s"val value1 = alloc[Byte](size)\n" +
      s"${fillInValue.mkString("\n")}\n" +
      s"(key, size, value1)\n" +
      "}"
    }

    val constructors = knownConstructors.flatMap { case (_, constructor) =>
      val dataTypeField = constructor.owner.fields.exists(field => knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol))
      val paramTypes = createParamTuple(constructor.params)._1.replaceAll("String", "CString")
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
        s"${put}(new_val)\n" +
        s"new_val\n" +
        "}")

      if (dataTypeField) {
        List(s"def ${constructor.constructor.toCode.showScala}_key(key: ${keyType}, ${paramTypesKey})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = new ${constructor.owner.name}(key, ${keyConstructor.mkString(",")})\n" +
        s"new_val\n" +
        "}",
        s"def ${constructor.constructor.toCode.showScala}_key(${paramTypesKey})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = new ${constructor.owner.name}(${table}.size, ${keyConstructor.mkString(",")})\n" +
        s"${put}(new_val)\n" +
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
      val (paramTypes, params_) = createParamTuple(mtd.owner.self :: mtd.vparamss.headOption.getOrElse(Nil))
      val params = params_.head + ".string" :: params_.tail
      s"def ${mtd.variable.toCode.showScala}(${paramTypes})${implicitZoneParam}" +
        s": ${mtd.typ} = ${mtd.symbol.name}" +
        s"(${params.mkString(",")})"
    }

    val fieldGetters = knownFieldGetters.map { case (_, getter) =>
      val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == getter.typ.tpe.typeSymbol)
      val returnType = if (getter.typ =:= codeTypeOf[String].rep) "CString" else getter.typ.toString

      val optionalGet = if (knownDataType) {
        val get = knownClasses(getter.typ.tpe.typeSymbol).getter.toCode.showScala

        s"if (${getter.owner.self.toCode.showScala}.${getter.name} == null) {\n" +
        s"${getter.owner.self.toCode.showScala}.${getter.name} = ${get}(${getter.owner.self.toCode.showScala}.${getter.name.replaceAll("\\s+", "")}Id)\n}\n"
      } else ""

      s"def ${getter.getter.toCode.showScala}(${getter.owner.self.toCode.showScala}: ${getter.owner.C.rep})${implicitZoneParam}: ${returnType} = {\n" +
      s"${optionalGet}" +  
      s"${getter.owner.self.toCode.showScala}.${getter.name}\n" +
      "}"
    }

    val fieldSetters = knownFieldSetters.map { case (_, setter) =>
      val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == setter.field.A.rep.tpe.typeSymbol)

      val optionalIdSet = if (knownDataType) {
        s"${setter.owner.self.toCode.showScala}.${setter.name.replaceAll("\\s+", "")}Id = ${setter.name}.key\n"
      } else ""
      
      s"def ${setter.setter.toCode.showScala}(${setter.owner.self.toCode.showScala}: ${setter.owner.C.rep}, ${setter.name}: ${setter.field.A.rep})${implicitZoneParam}: Unit = {\n" +
      s"${optionalIdSet}" +
      s"${setter.owner.self.toCode.showScala}.${setter.name} = ${setter.name}\n" +
      "}"
    }

    val queries = knownQueries.map { q =>
      val rep = q.rep
      val cde = planQuery(rep).getCode

      s"def ${q.name}: ${rep.Res.rep} = Zone { implicit zone => \n" +
      code{
        LMDBTable.txnBegin()
        $(openDbis)
        val res: rep.Res = $(cde.asInstanceOf[Code[rep.Res, Ctx]])
        LMDBTable.txnCommit()
        $(closeDbis)
        res
      }.showScala +
      "\n}"
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
      ${cString}\n
      ${tableGetters.mkString("\n")}\n
      ${tablePutters.mkString("\n")}\n
      ${fromPtrByte.mkString("\n")}\n
      ${toPtrByte.mkString("\n")}\n
      ${constructors.mkString("\n")}\n
      ${methods.mkString("\n")}\n
      ${stringMethods.mkString("\n")}\n
      ${fieldGetters.mkString("\n")}\n
      ${fieldSetters.mkString("\n")}\n
      ${queries.mkString("\n")}
    }
    """.replaceAll("@", "_")
    .replaceAll("dbstage\\.lang\\.Str", "Str")
    .replaceAll("dbstage\\.lang\\.Ptr", "Ptr")
    .replaceAll("dbstage\\.lang\\.LMDBTable", "LMDBTable")

    val regex = "\\.apply(?:\\[[^\\n\\r]+\\])?(\\([^\\n\\r]+\\))"
    val matches = regex.r.findAllMatchIn(program)
    matches.foreach(m => println(s"Replacing ${m.source.subSequence(m.start, m.end)} with ${m.group(1)}"))

    program.replaceAll(regex, "$1")
  }
  
}

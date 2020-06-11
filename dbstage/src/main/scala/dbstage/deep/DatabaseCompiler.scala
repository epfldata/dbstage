package dbstage.deep

import dbstage.lang.{TableView, Str, LMDBTable}

import IR.Predef._
import IR.Quasicodes._
import dbstage.lang.KeyedRecord

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
    
    val sortedClasses = knownClasses.toList.sortBy(_._1.name.toString)
    
    // Temporary representation of classes; will change/be configurable
    val classes = sortedClasses.collect { case (sym, tableRep) if sym != strSymbol =>
      val cls = tableRep.cls

      var numberOfFields = 1

      val fields = tableRep.logicalFields.map { field =>
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        val knownPrimitiveType = field.A =:= codeTypeOf[Int] || field.A =:= codeTypeOf[Double] || field.A =:= codeTypeOf[Long]

        // Add String back?
        if (field.A =:= codeTypeOf[String]) {
          throw new IllegalArgumentException(s"Class ${cls.name} has parameter with unsupported type String, please use Str")
        }

        if (knownDataType) {
          numberOfFields += 2
          s"${keyType}, ${field.A.rep}"
        } else if (knownPrimitiveType) {
          numberOfFields += 1
          s"${field.A.rep}"
        } else {
          throw new IllegalArgumentException(s"Class ${cls.name} has parameter with unsupported type ${field.A.rep.tpe.typeSymbol.name}")
        }
      }

      val finalFields = keyType :: fields
      s"type ${cls.name}Data = CStruct${numberOfFields}[${finalFields.mkString(",")}]\n" +
      s"type ${cls.name} = Ptr[${cls.name}Data]\n" +
      s"val size${cls.name} = sizeof[${cls.name}Data]\n" +
      s"""lazy val ${tableRep.variable.toCode.showScala} = new LMDBTable[${tableRep.T.rep}]("${cls.name}")\n"""
    }

    val strClass = {
      val tableRep = knownClasses(strSymbol)
      val cls = tableRep.cls

      assert(cls.fields.length == 1)
      val field = cls.fields(0)
      assert(field.A =:= codeTypeOf[String])

      s"type ${cls.name}Data = CStruct2[${keyType}, CString]\n" +
      s"type ${cls.name} = Ptr[${cls.name}Data]\n" +
      s"""lazy val ${tableRep.variable.toCode.showScala} = new LMDBTable[CString]("${cls.name}")\n"""
    }

    val defToCString = {
      s"def ${toCString.toCode.showScala}(string: String)${implicitZoneParam}: CString = {\n" +
      s"toCString(string)\n}"
    }

    val defFromCString = {
      s"def ${fromCString.toCode.showScala}(string: CString)${implicitZoneParam}: String = {\n" +
      s"fromCString(string)\n}"
    }

    val emptyPointers = sortedClasses.collect { case (sym, tableRep) if sym != strSymbol =>
      var index = 1
      val removePtrs = tableRep.logicalFields.map { field =>
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == field.A.rep.tpe.typeSymbol)
        index += 1

        if(knownDataType) {
          index += 1
          s"value._${index} = null\n"
        } else ""
      }

      s"def ${tableRep.emptyPointers.toCode.showScala}(value: ${tableRep.cls.C.rep}): ${tableRep.cls.C.rep} = {\n" +
      s"if(value != null) {\n${removePtrs.mkString("")}}\n" +
      s"value\n" +
      s"}"
    }

    val tableGetters = sortedClasses.collect { case (sym, tableRep) if sym != strSymbol =>
      val owner = tableRep.cls
      val removePointers = tableRep.emptyPointers.toCode.showScala
      val table = tableRep.variable.toCode.showScala

      s"def ${tableRep.getter.toCode.showScala}(key: ${keyType})${implicitZoneParam}: ${owner.C.rep} = {\n" +
      s"val value = ${table}.get(key)\n" +
      s"${removePointers}(value)\n" +
      "}"
    }

    val strGetter = sortedClasses.collect { case (sym, tableRep) if sym == strSymbol =>
      val owner = tableRep.cls
      val table = tableRep.variable.toCode.showScala

      s"def ${tableRep.getter.toCode.showScala}(key: ${keyType})${implicitZoneParam}: ${owner.C.rep} = {\n" +
      s"val value = alloc[StrData]\n" +
      s"val string = ${table}.get(key)\n" +
      s"value._1 = key\n" +
      s"value._2 = string\n" +
      s"value\n" +
      "}"
    }

    val tablePutters = sortedClasses.collect { case (sym, tableRep) if sym != strSymbol =>
      val owner = tableRep.cls
      val table = tableRep.variable.toCode.showScala

      s"def ${tableRep.putter.toCode.showScala}(data_el: ${owner.C.rep})${implicitZoneParam}: Unit = {\n" +
      s"${table}.put(data_el._1, size${owner.name}, data_el.asInstanceOf[Ptr[Byte]])\n" +
      "}"
    }

    val strPutter = sortedClasses.collect { case (sym, tableRep) if sym == strSymbol =>
      val owner = tableRep.cls
      val table = tableRep.variable.toCode.showScala

      s"def ${tableRep.putter.toCode.showScala}(str: ${owner.C.rep})${implicitZoneParam}: Unit = {\n" +
      s"${table}.put(str._1, strlen(str._2)+1, str._2)\n" +
      "}"
    }

    val tableDeleters = knownDeleters.toList.sortBy(_._1.name.toString).map { case (_, deleter) =>
      val table = knownClasses(deleter.owner.C.rep.tpe.typeSymbol).variable.toCode.showScala

      s"def ${deleter.deleter.toCode.showScala}(el: ${deleter.owner.C.rep})${implicitZoneParam}: Unit = {\n" +
      s"${table}.delete(el._1)\n" +
      "}"
    }

    val constructors = knownConstructors.toList.sortBy(_._1.name.toString).map { case (_, constructor) =>
      val table = knownClasses(constructor.owner.C.rep.tpe.typeSymbol).variable.toCode.showScala
      val putter = knownClasses(constructor.owner.C.rep.tpe.typeSymbol).putter.toCode.showScala
      val getter = knownClasses(constructor.owner.C.rep.tpe.typeSymbol).getter.toCode.showScala

      val (fieldTypes, fieldNames) = createParamTuple(constructor.params)

      val getNewKeyOptional = if(constructor.owner.C <:< codeTypeOf[KeyedRecord]) "" else
        s"new_val._1 = ${table}.getNextKey\n"

      var index = 1
      val fillInFields = constructor.params.zip(fieldNames).map{ case (param, fieldName) => {
        val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == param.Typ.rep.tpe.typeSymbol)
        val name = param.toString.stripPrefix(s"Variable[${param.Typ.rep}](").split('@')(0) // Better way to check?
        
        if(knownDataType) {
          index += 2
          s"new_val._${index-1} = ${fieldName}._1\n" +
          s"new_val._${index} = ${fieldName}"
        } else if(name == "key") {
          s"new_val._1 = ${fieldName}"
        } else {
          index += 1
          s"new_val._${index} = ${fieldName}"
        }
      }}

      s"def ${constructor.constructor.toCode.showScala}(${fieldTypes.replaceAll("String", "CString")})${implicitZoneParam}" +
        s": ${constructor.owner.C.rep} = {\n" +
        s"val new_val = alloc[${constructor.owner.name}Data]\n" +
        s"${getNewKeyOptional}" +
        s"${fillInFields.mkString("\n")}\n" +
        s"${putter}(new_val)\n" +
        s"${getter}(new_val._1)\n" +
        "}"
    }

    val sortedMethods = knownMethods.toList.sortBy(_._1.name.toString)

    val methods = sortedMethods.filter(p => p._2.owner.C.rep.tpe.typeSymbol != strSymbol).map { case (_, mtd) =>
      s"def ${mtd.variable.toCode.showScala}(${mtd.owner.self.toCode.showScala}: ${mtd.owner.C.rep})${implicitZoneParam}: " +
        s"${mtd.body.Typ.rep} = ${mtd.body.showScala}"
    }

    val stringMethods = sortedMethods.filter(p => p._2.owner.C.rep.tpe.typeSymbol == strSymbol).map { case (_, mtd) => 
      val (paramTypes_, params_) = createParamTuple(mtd.owner.self :: mtd.vparamss.headOption.getOrElse(Nil))
      val paramTypes = paramTypes_.replaceAll("\\bString\\b", "CString")
      val params = params_.head + "._2" :: params_.tail
      s"def ${mtd.variable.toCode.showScala}(${paramTypes})${implicitZoneParam}" +
        s": ${mtd.typ} = ${mtd.symbol.name}" +
        s"(${params.mkString(",")})"
    }

    val fieldGetters = knownFieldGetters.toList.sortBy(_._1.name.toString).map { case (_, getter) =>
      val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == getter.typ.tpe.typeSymbol)

      val body = if (getter.typ =:= codeTypeOf[String].rep) {
        s"${fromCString.toCode.showScala}(${getter.owner.self.toCode.showScala}._${getter.index})"
      } else {
        s"${getter.owner.self.toCode.showScala}._${getter.index}"
      }

      val optionalGet = if (knownDataType) {
        val get = knownClasses(getter.typ.tpe.typeSymbol).getter.toCode.showScala

        s"if (${getter.owner.self.toCode.showScala}._${getter.index} == null) {\n" +
        s"${getter.owner.self.toCode.showScala}._${getter.index} = ${get}(${getter.owner.self.toCode.showScala}._${getter.index-1})\n}\n"
      } else ""

      s"def ${getter.getter.toCode.showScala}(${getter.owner.self.toCode.showScala}: ${getter.owner.C.rep})${implicitZoneParam}: ${getter.typ} = {\n" +
      s"${optionalGet}" +  
      s"${body}\n" +
      "}"
    }

    val fieldSetters = knownFieldSetters.toList.sortBy(_._1.name.toString).map { case (_, setter) =>
      val knownDataType = knownClasses.values.exists(tbl => tbl.cls.C.rep.tpe.typeSymbol == setter.typ.tpe.typeSymbol)

      val optionalIdSet = if (knownDataType) {
        s"${setter.owner.self.toCode.showScala}._${setter.index-1} = ${setter.name}._1\n"
      } else ""
      
      s"def ${setter.setter.toCode.showScala}(${setter.owner.self.toCode.showScala}: ${setter.owner.C.rep}, ${setter.name}: ${setter.typ})${implicitZoneParam}: Unit = {\n" +
      s"${optionalIdSet}" +
      s"${setter.owner.self.toCode.showScala}._${setter.index} = ${setter.name}\n" +
      "}"
    }

    val queries = knownQueries.toList.sortBy(_.name).map { q =>
      val rep = q.rep
      val cde = planQuery(rep).getCode

      s"def ${q.name}: ${rep.Res.rep.tpe} = Zone { implicit zone => \n" +
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
      ${defToCString}\n
      ${defFromCString}\n
      ${emptyPointers.mkString("\n")}\n
      ${tableGetters.mkString("\n")}\n
      ${strGetter.mkString("\n")}\n
      ${tablePutters.mkString("\n")}\n
      ${strPutter.mkString("\n")}\n
      ${tableDeleters.mkString("\n")}\n
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

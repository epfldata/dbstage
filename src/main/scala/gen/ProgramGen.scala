package gen

import scala.language.higherKinds
import scala.language.implicitConversions
import scala.collection.mutable
import scala.reflect.runtime.{universe => sru}
import scala.reflect.runtime.universe.{internal => srui}
import sourcecode.{Name => SrcName}
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}

import squid.utils._
//import dbstage.Embedding
//import Embedding.Predef._
//import dbstage.showC


//trait ProgramGenBase {
//trait ProgramGenBase { selfIR: squid.ir.AST =>
abstract class ProgramGenBaseClass extends ProgramGenBase // To avoid recompilation of Embedding on change to ProgramGenBase
trait ProgramGenBase extends squid.ir.AST {
//val IR: squid.lang.IntermediateBase
//val IR: squid.ir.AST // ^ TODO generalize to squid.lang.IntermediateBase
//import IR.Predef._
//import IR.IntermediateCodeOps
//import selfIR.Predef._
import Predef._

//private val freshTypeSymbols: Set[]
protected lazy val freshMethodSymbols: mutable.Map[(TypSymbol,String,Int), MtdSymbol] = mutable.Map()
// ^ Note: making it private makes it null in the loadMtdSymbol override called too early!

override def loadMtdSymbol(typ: ScalaTypeSymbol, symName: String, index: Option[Int], static: Bool): MtdSymbol =
  //println(freshMethodSymbols) thenReturn 
  freshMethodSymbols.getOrElse((typ,symName,index.getOrElse(0)), super.loadMtdSymbol(typ, symName, index, static))

protected lazy val freshTypeSymbols: mutable.Map[String, TypSymbol] = mutable.Map()
override def loadTypSymbol(fullName: String): ScalaTypeSymbol =
  //println(fullName,freshTypeSymbols,freshTypeSymbols.get(fullName)) thenReturn 
  freshTypeSymbols.getOrElse(fullName, super.loadTypSymbol(fullName))

//object ProgramGen
abstract class ProgramGen {
//class ProgramGen(implicit rootSym: sru.TypeTag[]) {
  type World
  
  protected def showC(cde: OpenCode[Any]) = cde.rep|>base.showRep // TODO mv to Squid?
  
  protected class Root private(val sym: sru.Symbol)
  val root: Root
  //def here(implicit rootTyp: sru.TypeTag[this.type]) = {
  object Root { def apply(implicit rootTyp: sru.TypeTag[ProgramGen.this.type]) = {
    val rootSym = rootTyp.tpe.typeSymbol
    assert(rootSym.isStatic, s"root symbol $rootSym is not static") // TODO B/E
    new Root(rootSym)
  }}
  lazy val rootSym = root.sym
  
  protected val symTable: mutable.Map[base.BoundVal,String] = mutable.Map()
  
  protected val fatalFrozenFieldError = false
  
  //trait SymbolLoadingKludge extends squid.ir.RuntimeSymbols {
  //  override def loadMtdSymbol(typ: ScalaTypeSymbol, symName: String, index: Option[Int], static: Bool): MtdSymbol =
  //    ???
  //}
  
  trait Definition {
    def toScalaTree: sru.Tree
    def showCode = sru.showCode(toScalaTree)
  }
  //class MethodBase[T:CodeType](name: String, mkBody: => Code[T,Ctx & args.Ctx]) extends Definition {
  //  
  //}
  
  protected def freshTypeSymbol(name: String) = {
    val sym = sru.internal.reificationSupport.newNestedSymbol(rootSym,
      sru.TypeName(name),
      sru.NoPosition, sru.internal.reificationSupport.FlagsRepr.apply(80L), false)
    sru.internal.reificationSupport.setInfo[sru.Symbol](sym,
      //sru.internal.reificationSupport.ClassInfoType(Nil,sru.internal.reificationSupport.newScopeWith(),sym)
      sru.internal.reificationSupport.ClassInfoType(sru.typeOf[Any]::Nil,sru.internal.reificationSupport.newScopeWith(),sym)
    )
    //println(sym,sym.fullName)
    sym.asType alsoApply (s => freshTypeSymbols += ruh.encodedTypeSymbol(s) -> s)
  }
  
  // TODO handle overloading
  def freshMethodSymbol(tsym: TypSymbol, name: String, typ: base.TypeRep) = {
    val $m: sru.Mirror = scala.reflect.runtime.universe.runtimeMirror(classOf[ProgramGen].getClassLoader());
    val symdef$foo1: sru.Symbol = sru.internal.reificationSupport.newNestedSymbol(tsym, sru.TermName.apply(name), sru.NoPosition, sru.internal.reificationSupport.FlagsRepr.apply(80L), false);
    sru.internal.reificationSupport.setInfo[sru.Symbol](symdef$foo1, sru.internal.reificationSupport.NullaryMethodType(typ.tpe));
    //println(symdef$foo1,symdef$foo1.owner,symdef$foo1.fullName)
    // ^ note: for some reason, we get (method isMajor,type Person,gen.Gen.isMajor); instead of the last one bein gen.Gen.Person.isMajor
    symdef$foo1.asMethod alsoApply {sym => freshMethodSymbols += ((tsym,name,0) -> sym)}
  }
  
  //trait Scope { scp =>
  trait Scope[-C] { scp =>
    //type Ctx //<: World
    //type Method[T] <: MethodBase[T]
    //protected val classes: mutable.Buffer[Class[_]] = mutable.Buffer()
    private[this] val _classes: mutable.Buffer[Class[C]] = mutable.Buffer()
    protected def classes: Seq[Class[C]] = _classes
    //protected val objects: mutable.Buffer[Object[_]] = mutable.Buffer()
    //trait SubScope {
    //  type Ctx <: scp.Ctx
    //}
  }
  
  implicit def toRef[T,C](mtd: Class[C]#Method[T]): Code[Class[C]#Repr => T,C] =
    mtd.toLambda.asInstanceOf[Code[Class[C]#Repr => T,C]]
  
  //class ClassImpl extends Scope {
  //  type Ctx <: World
  class Class[-C](implicit srcName: SrcName) extends Scope[C] with Definition with DelayedInit { selfClass =>
    // TODO method, staticMethod, param, field
    val defName = srcName.value
    //type Ctx >: C
    type Ctx >: C & self.Ctx
    type Repr
    implicit val Repr = {
      base.CodeType[Repr](base.staticTypeApp(tsym, Nil))
    }
    val self = Variable[Repr]("self")
    private val SelfBound = self.`internal bound`
    protected lazy val tsym = {
      // FIXME
      //base.loadTypSymbol("gen.ProgramGen")
      //println(defName)
      //val sym = sru.internal.reificationSupport.newNestedSymbol(sru.symbolOf[ProgramGen.type],
      freshTypeSymbol(defName)
    }
    
    protected val methods: mutable.Buffer[Method[_]] = mutable.Buffer()
    private val params: mutable.Buffer[Param[_]] = mutable.Buffer()
    private val fields: mutable.Buffer[Field[_]] = mutable.Buffer()
    
    protected implicit def toRef[T](mtd: Method[T]): Code[T,Ctx] = mtd.ref
    
    def apply[C](args: Code[Any,C]*): Code[Repr,C] = {
      def location = s"in ${defName}(${args.map(showC).mkString(", ")})"
      frozenFields = true
      assert(args.size === params.size, s"Wrong number of parameters $location for $this")
      //(params zip args) foreach {
      val as = (params zip args) map {
        case p -> a =>
          assert(a.Typ <:< p.Typ, s"Type of argument $a: ${a.Typ} is incompatible with type of parameter $p; $location")
          a.rep
      }
      val inst = base.methodApp(base.newObject(Repr.rep),ctor,Nil,base.Args(as:_*)::Nil,Repr.rep)
      base.Code(inst)
    }
    
    lazy val ctor = {
      freshMethodSymbol(tsym, "<init>", Repr.rep)
    }
    
    // rename to TermMember?
    abstract class MethodBase[T:CodeType] {
      type Typ = T
      implicit val Typ = codeTypeOf[T] // extracted to MethodBase to avoid causing trouble...
      
    }
    abstract class Method[T:CodeType](name: String) extends MethodBase[T] with Definition {
      def mkBody: Code[T,Ctx & self.Ctx] // FIXME self.Ctx useless?
      lazy val body: Code[Typ,Ctx & self.Ctx] = mkBody
      //methods += this
      // TODO upd symTable
      //val ref: Variable[T] = Variable[T]
      //def ref: Code[T,Ctx]
      /*
      private[gen] val access = Variable[T](name).asInstanceOf[Variable[T]{type Ctx = selfClass.Ctx}]
      def ref: Code[T,Ctx] = access.toCode
      symTable += access.`internal bound` -> name
      */
      val sym = { // TODO put purity annotation depending on body!
        freshMethodSymbol(tsym, name, typeRepOf[T])
      }
      def ref: Code[T,Ctx] = base.Code(base.methodApp(self.rep, sym, Nil, Nil, typeRepOf[T]))
      def toLambda: Code[Repr => T,C] = code"($self: Repr) => $ref".asInstanceOf[Code[Repr => T,C]] // FIXME
      //def toScalaTree = base.scalaTree(body.rep, bv => sru.Ident(sru.TermName(symTable(bv))))
      //override def toString = s"def $name = ${sru.showCode(toScalaTree)}"
      def bodyToScalaTree = base.scalaTree(body.rep, {
        case SelfBound =>
          import sru._
          q"${TypeName(defName)}.this"
        case bv => sru.Ident(sru.TermName(symTable(bv)))
      })
      def toScalaTree: sru.ValOrDefDef = {
        import sru._
        q"def ${TermName(name)}: ${typeRepOf[T].tpe} = $bodyToScalaTree"
      }
      override def toString = sru.showCode(toScalaTree)
    }
    //abstract class Field[T:CodeType](name: String, mkBody: => Code[T,Ctx & args.Ctx]) extends Method(name, mkBody) {
    abstract class Field[T:CodeType](name: String) extends Method[T](name) {
      //def mkBody: Code[T,Ctx & access.Ctx]
      //def mkBody: Code[T,Ctx]
      def mkBody: Code[T,Ctx & self.Ctx]
      //if (frozenFields) System.err.println(s"Field ${selfClass.name}.$name was added after class ${selfClass.name} was finalized; " +
      if (frozenFields) {
        val e = new Exception(s"field ${selfClass.defName}.$name was added after class ${selfClass.defName} was finalized; " +
          s"this could lead to problems down the line. Override '' to make this error fatal.")
        if (fatalFrozenFieldError) throw e else e.printStackTrace()
      }
      //fields += this
      override def toString = s"val $name = ${sru.showCode(toScalaTree)}"
    }
    class Param[T:CodeType](name: String) extends Field[T](name) {
      //val accessor = Variable[T]("accessor")
      def mkBody: Code[T,Ctx & self.Ctx] = ref // Q: makes sense?
      override def toScalaTree = {
        import sru._
        q"val ${TermName(name)}: ${typeRepOf[T].tpe}"
      }
    }
    protected def method[T:CodeType](bodyFun: => Code[T,Ctx])(implicit srcName: SrcName): Method[T] =
      new Method[T](srcName.value) { def mkBody = bodyFun } alsoApply {methods += _}
    protected def field[T:CodeType](bodyFun: => Code[T, Ctx])(implicit srcName: SrcName): Field[T] =
      new Field[T](srcName.value) { def mkBody = bodyFun } alsoApply {fields += _}
    protected def param[T:CodeType](implicit srcName: SrcName): Param[T] =
      new Param[T](srcName.value) alsoApply {params += _}
    
    def toScalaTree = {
      import sru._
      q"""class ${TypeName(defName)}(..${params.map(_.toScalaTree)}) {
        ..${fields.map(_.toScalaTree)}
        ..${methods.map(_.toScalaTree)}
      }"""
    }
    override def toString =
      if (params.isEmpty) s"class $defName"
      else s"class $defName(${params.map(sru showCode _.toScalaTree).mkString(", ")})"
    
    private[this] var frozenFields = false
    def delayedInit(body: => Unit) = {
      //println(s"Init ${selfClass.name}")
      frozenFields = false // it turns out that `delayedInit` can actually be called several times...
      body
      frozenFields = true
      //println(s"Done ${selfClass.name}")
    }
  }
  
  class ScalaUnit extends Scope[World] {
    //type Ctx = World
    def definitions = classes
  }
  
}
}

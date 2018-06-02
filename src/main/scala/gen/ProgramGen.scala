package gen

import scala.language.higherKinds
import scala.language.implicitConversions
import scala.collection.mutable
import scala.reflect.runtime.{universe => sru}
import scala.reflect.runtime.universe.{internal => srui}
import sourcecode.{Name => SrcName}

import squid.utils._
import dbstage.Embedding
import Embedding.Predef._
import dbstage.showC

class ProgramGen {
  type World
  
  protected val symTable: mutable.Map[base.BoundVal,String] = mutable.Map()
  
  protected val fatalFrozenFieldError = false
  
  trait Definition {
    def toScalaTree: sru.Tree
    def showCode = sru.showCode(toScalaTree)
  }
  //class MethodBase[T:CodeType](name: String, mkBody: => Code[T,Ctx & args.Ctx]) extends Definition {
  //  
  //}
  
  def freshMethodSymbol(name: String, typ: base.TypeRep) = {
    val $m: sru.Mirror = scala.reflect.runtime.universe.runtimeMirror(classOf[ProgramGen].getClassLoader());
    val symdef$foo1: sru.Symbol = sru.internal.reificationSupport.newNestedSymbol(sru.symbolOf[ProgramGen], sru.TermName.apply(name), sru.NoPosition, sru.internal.reificationSupport.FlagsRepr.apply(80L), false);
    sru.internal.reificationSupport.setInfo[sru.Symbol](symdef$foo1, sru.internal.reificationSupport.NullaryMethodType(typ.tpe));
    symdef$foo1.asMethod
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
  
  //class ClassImpl extends Scope {
  //  type Ctx <: World
  class Class[-C](implicit srcName: SrcName) extends Scope[C] with Definition with DelayedInit { selfClass =>
    // TODO method, staticMethod, param, field
    //type Ctx >: C
    type Ctx >: C & self.Ctx
    type Repr
    implicit val Repr = {
      // FIXME
      base.CodeType[Repr](base.staticTypeApp(base.loadTypSymbol("gen.ProgramGen"), Nil))
    }
    val self = Variable[Repr]("self")
    private val SelfBound = self.`internal bound`
    val defName = srcName.value
    
    protected val methods: mutable.Buffer[Method[_]] = mutable.Buffer()
    private val params: mutable.Buffer[Param[_]] = mutable.Buffer()
    private val fields: mutable.Buffer[Field[_]] = mutable.Buffer()
    
    implicit def toRef[T](mtd: Method[T]): Code[T,Ctx] = mtd.ref
    
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
      freshMethodSymbol("<init>", Repr.rep)
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
        //srui.newMethodSymbol(sru.symbolOf[ProgramGen], sru.TermName(name))
        //srui.reificationSupport.se
        /*
        val $u: reflect.runtime.universe.type = scala.reflect.runtime.`package`.universe;
        val $m: $u.Mirror = scala.reflect.runtime.`package`.universe.runtimeMirror(classOf[ProgramGen].getClassLoader());
        //val $u: U = $m$untyped.universe;
        //val $m: $u.Mirror = $m$untyped.asInstanceOf[$u.Mirror];
        //val anon1: $u.Symbol = $u.internal.reificationSupport.newNestedSymbol($u.internal.reificationSupport.selectTerm($m.staticModule("Main").asModule.moduleClass, "main"), $u.TypeName.apply("$anon"), $u.NoPosition, $u.internal.reificationSupport.FlagsRepr.apply(32L), true);
        //val anon1: $u.Symbol = $u.internal.reificationSupport.newNestedSymbol(sru.symbolOf[ProgramGen], $u.TypeName.apply("$anon"), $u.NoPosition, $u.internal.reificationSupport.FlagsRepr.apply(32L), true);
        //val `lessrefinement>1`: $u.Symbol = $u.internal.reificationSupport.newNestedSymbol(anon1, $u.TypeName.apply("<refinement>"), $u.NoPosition, $u.internal.reificationSupport.FlagsRepr.apply(0L), true);
        //val symdef$foo1: $u.Symbol = $u.internal.reificationSupport.newNestedSymbol(`lessrefinement>1`, $u.TermName.apply("foo"), $u.NoPosition, $u.internal.reificationSupport.FlagsRepr.apply(80L), false);
        val symdef$foo1: $u.Symbol = $u.internal.reificationSupport.newNestedSymbol(sru.symbolOf[ProgramGen], $u.TermName.apply(name), $u.NoPosition, $u.internal.reificationSupport.FlagsRepr.apply(80L), false);
        //val lessinit>1: $u.Symbol = $u.internal.reificationSupport.newNestedSymbol(anon1, $u.TermName.apply("<init>"), $u.NoPosition, $u.internal.reificationSupport.FlagsRepr.apply(64L), false);
        //$u.internal.reificationSupport.setInfo[$u.Symbol](anon1, $u.internal.reificationSupport.ClassInfoType(scala.collection.immutable.List.apply[$u.Type]($u.internal.reificationSupport.TypeRef($u.internal.reificationSupport.ThisType($m.staticPackage("scala").asModule.moduleClass), $u.internal.reificationSupport.selectType($m.staticPackage("scala").asModule.moduleClass, "AnyRef"), immutable.this.Nil)), $u.internal.reificationSupport.newScopeWith(lessinit>1), anon1));
        //$u.internal.reificationSupport.setInfo[$u.Symbol](`lessrefinement>1`, $u.internal.reificationSupport.RefinedType(scala.collection.immutable.List.apply[$u.Type]($u.internal.reificationSupport.TypeRef($u.internal.reificationSupport.ThisType($m.staticPackage("scala").asModule.moduleClass), $u.internal.reificationSupport.selectType($m.staticPackage("scala").asModule.moduleClass, "AnyRef"), immutable.this.Nil)), $u.internal.reificationSupport.newScopeWith(symdef$foo1), `lessrefinement>1`));
        $u.internal.reificationSupport.setInfo[$u.Symbol](symdef$foo1, $u.internal.reificationSupport.NullaryMethodType($m.staticClass("scala.Int").asType.toTypeConstructor));
        symdef$foo1.asMethod
        */
        freshMethodSymbol(name, typeRepOf[T])
      }
      def ref: Code[T,Ctx] = base.Code(base.methodApp(self.rep, sym, Nil, Nil, typeRepOf[T]))
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

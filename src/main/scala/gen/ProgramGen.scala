package gen

import scala.language.higherKinds
import scala.language.implicitConversions
import scala.collection.mutable
import scala.reflect.runtime.{universe => sru}
import scala.reflect.runtime.universe.{internal => srui}
import sourcecode.{Name => SrcName}
import squid.ir.SimpleEffect
import squid.ir.SimpleEffects
import squid.ir.SimpleRuleBasedTransformer
import squid.utils.meta.{RuntimeUniverseHelpers => ruh}
import squid.utils._

import scala.annotation.compileTimeOnly
//import dbstage.Embedding
//import Embedding.Predef._
//import dbstage.showC

object Helper {
  class Dummy[T]
  @compileTimeOnly("dummy")
  def Dummy[T](arg: => T): Dummy[T] = ???
}
import Helper.Dummy

//trait ProgramGenBase {
//trait ProgramGenBase { selfIR: squid.ir.AST =>
abstract class ProgramGenBaseClass extends ProgramGenBase // To avoid recompilation of Embedding on change to ProgramGenBase
trait ProgramGenBase extends squid.ir.AST with SimpleEffects { IR =>
//val IR: squid.lang.IntermediateBase
//val IR: squid.ir.AST // ^ TODO generalize to squid.lang.IntermediateBase
//import IR.Predef._
//import IR.IntermediateCodeOps
//import selfIR.Predef._
import Predef._

val programGenTransformer: squid.ir.RuleBasedTransformer { val base: IR.type } =
  //new squid.ir.IdentityTransformer { val base: IR.type = IR  }
  new SimpleRuleBasedTransformer with SelfTransformer //{ val base: IR.type = IR  }

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

override def postProcess(r: Rep): Rep = super.postProcess(r)

//object ProgramGen
abstract class ProgramGen {
//class ProgramGen(implicit rootSym: sru.TypeTag[]) {
  type World
  
  protected def showC(cde: OpenCode[Any]) = cde.rep|>base.showRep // TODO mv to Squid?
  
  protected class Root private(val sym: sru.Symbol)
  val root: Root
  //def here(implicit rootTyp: sru.TypeTag[this.type]) = {
    object Root { def thisEnclosingInstance(implicit rootTyp: sru.TypeTag[ProgramGen.this.type]) = {
    val rootSym = rootTyp.tpe.typeSymbol
    assert(rootSym.isStatic, s"root symbol $rootSym is not static") // TODO B/E
    new Root(rootSym)
  }}
  lazy val rootSym = root.sym
  
  protected val symTable: mutable.Map[base.BoundVal,String] = mutable.Map()
  
  protected val fatalFrozenFieldError = false
  
  protected def BASE_NAME_INDEX = 1
  
  private val Nothing = codeTypeOf[Nothing]
  
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
  def freshMethodSymbol(tsym: TypSymbol, name: String, typ: base.TypeRep, effect: SimpleEffect) = {
    val $m: sru.Mirror = scala.reflect.runtime.universe.runtimeMirror(classOf[ProgramGen].getClassLoader());
    val symdef$foo1: sru.Symbol = sru.internal.reificationSupport.newNestedSymbol(tsym, sru.TermName.apply(name), sru.NoPosition, sru.internal.reificationSupport.FlagsRepr.apply(80L), false);
    sru.internal.reificationSupport.setInfo[sru.Symbol](symdef$foo1, sru.internal.reificationSupport.NullaryMethodType(typ.tpe));
    //println(symdef$foo1,symdef$foo1.owner,symdef$foo1.fullName)
    // ^ note: for some reason, we get (method isMajor,type Person,gen.Gen.isMajor); instead of the last one bein gen.Gen.Person.isMajor
    symdef$foo1.asMethod alsoApply { sym => freshMethodSymbols += ((tsym,name,0) -> sym); transparencyPropagatingMtds += sym }
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
  
  //implicit def toRef[T,C](mtd: Class[C]#Method[T]): Code[Class[C]#Self => T,C] =
  //  mtd.asLambda.asInstanceOf[Code[Class[C]#Self => T,C]]
  
  // too complicated for Scala:
  //implicit def toRef[T,C,Cls<:Class[C] with Singleton](mtd: Cls#Method[T]): Code[Cls#Self => T,C] =
  //  mtd.asLambda.asInstanceOf[Code[Cls#Self => T,C]]
  
  
  //class ClassImpl extends Scope {
  //  type Ctx <: World
  class Class[-C](implicit srcName: SrcName) extends ClassImpl[C] {
    
    protected implicit def toInsideRef[T](mtd: Method[T]): Code[T,Ctx] = mtd.insideRef
    
  }
  class ClassImpl[-C](implicit srcName: SrcName) extends Scope[C] with Definition with DelayedInit { selfClass =>
    // TODO method, staticMethod, param, field
    val defName = srcName.value
    //type Ctx >: C
    type Ctx >: C & self.Ctx
    type Self
    implicit val Self = {
      base.CodeType[Self](base.staticTypeApp(tsym, Nil))
    }
    val self = Variable[Self]("self")
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
    private val fields: mutable.Buffer[Field[_]] = mutable.Buffer() // note: fields with empty name are considered ctor effects
    //private val usedTermNames, usedTypeNames: mutable.Map[String,Int] = new mutable.HashMap[String,Int] with 
    private val usedTermNames, usedTypeNames: mutable.Map[String,Int] = mutable.Map().withDefaultValue(BASE_NAME_INDEX) 
    
    //protected implicit def toRef[T](mtd: Method[T]): Code[T,Ctx] = mtd.insideRef
    // conflicts
    //implicit def toRef[T](mtd: Method[T]): Code[Self => T,C] =
    implicit def toOutsideRef[T](mtd: Method[T]): Code[Self => T,C] =
      mtd.asLambda//.asInstanceOf[Code[Self => T,C]]
    
    def apply[C](args: Code[Any,C]*)(implicit pos: Pos): Code[Self,C] = {
      def location = s"in ${defName}(${args.map(showC).mkString(", ")})\n\tat line ${pos.file}:${pos.line}"
      frozenFields = true
      //assert(args.size === params.size, s"Wrong number of parameters $location for $this")
      assert(args.size === params.size, s"Wrong number of parameters for $this; $location")
      //(params zip args) foreach {
      val as = (params zip args) map {
        case p -> a =>
          assert(a.Typ <:< p.Typ, s"Type of argument $a: ${a.Typ} is incompatible with type of parameter $p; $location")
          a.rep
      }
      val inst = New(as)
      base.Code(inst)
    }
    def unapplySeq[C](cde: Code[Any,C]): Option[Seq[Code[Any,C]]] = cde.rep |>? {
    //def unapply[C](cde: Code[Any,C]): Option[Seq[Code[Any,C]]] = cde.rep |>? {
    //  case RepDef(MethodApp(RepDef(NewObject(_)),this.ctor,Nil,Args(as@_*)::Nil,ret)) => as.map(Code.apply)
      case New(as) => as.map(Code.apply)
    }
    protected object New {
      def unapply(r: Rep): Option[Seq[Rep]] = r |>? {
        case RepDef(MethodApp(RepDef(NewObject(_)),ClassImpl.this.ctor,Nil,Args(as@_*)::Nil,ret)) => as
      }
      def apply(as: Seq[Rep]) =
        base.methodApp(base.newObject(Self.rep),ctor,Nil,base.Args(as:_*)::Nil,Self.rep)
    }
    
    lazy val ctorEffect = {
      //frozenFields = true
      requireFrozenFields
      //???
      //println(tsym,(fields ++ params).map(_.effect))
      (fields ++ params).map(_.effect).reduceOption(_ | _).getOrElse(SimpleEffect.Pure)
    }
    lazy val ctor = {
      //frozenFields = true
      //requireFrozenFields
      //println(tsym,ctorEffect)
      freshMethodSymbol(tsym, "<init>", Self.rep, ctorEffect)
    }
    
    // rename to TermMember?
    abstract class MethodBase[T:CodeType](val name: String) {
      type Typ = T
      implicit val Typ = codeTypeOf[T] // extracted to MethodBase to avoid causing trouble...
      
    }
    abstract class Method[T:CodeType](name: String) extends MethodBase[T](name) with Definition {
      def mkBody: Code[T,Ctx & self.Ctx] // FIXME self.Ctx useless?
      lazy val body: Code[Typ,Ctx & self.Ctx] = mkBody
      def effect = effectCached(body.rep)
      //methods += this
      // TODO upd symTable
      //val ref: Variable[T] = Variable[T]
      //def ref: Code[T,Ctx]
      /*
      private[gen] val access = Variable[T](name).asInstanceOf[Variable[T]{type Ctx = selfClass.Ctx}]
      def ref: Code[T,Ctx] = access.toCode
      symTable += access.`internal bound` -> name
      */
      lazy val sym = { // TODO put purity annotation depending on body!
        //println(s"Sym of $tsym.$name")
        freshMethodSymbol(tsym, name, typeRepOf[T], effect)
      }
      def insideRef: Code[T,Ctx] = base.Code(base.methodApp(self.rep, sym, Nil, Nil, typeRepOf[T]))
      // TODO tryInline in:
      def asLambda: Code[Self => T,C] = code"($self: Self) => $insideRef".asInstanceOf[Code[Self => T,C]] // FIXME
      def inlined: Code[Self => T,C] = code"($self: Self) => $body".asInstanceOf[Code[Self => T,C]] // FIXME
      
      //def apply[C](self: Code[Self,C])(xs: Code[Any,C]*): Code[Any,C]
      def apply[D](self: Code[Self,D]): Code[T,C&D] = code"$asLambda($self)"
      def unapply[C](expr: Code[T,C]): Option[Code[Self,C]] = expr.rep match {
        case RepDef(MethodApp(self,this.sym,Nil,Nil,_)) => // TODO make sure args always Nil?
          Some(Code(self))
        case _ => None
      }
      
      //implicit def toRef[T]: Code[Self => T,C] = asLambda.asInstanceOf[Code[Self => T,C]]
      
      //def toScalaTree = base.scalaTree(body.rep, bv => sru.Ident(sru.TermName(symTable(bv))))
      //override def toString = s"def $name = ${sru.showCode(toScalaTree)}"
      def exprToScalaTree(expr: OpenCode[_]) = base.scalaTree(expr.rep, {
        case SelfBound =>
          import sru._
          q"${TypeName(defName)}.this"
        case bv => sru.Ident(sru.TermName(symTable(bv)))
      })
      final def toScalaTree = toScalaTreeImpl(true)
      /** Generate Scala tree for a method def, where methods of function types are transformed to methods with arguments. */
      //def toScalaTree(withBody: Bool): sru.ValOrDefDef = {  // TODO also convert applications of Y combinator to recursive def!
      def toScalaTreeImpl(withBody: Bool): sru.Tree = {  // TODO also convert applications of Y combinator to recursive def!
        import sru._
        //q"def ${TermName(name)}: ${newBody.Typ.rep.tpe} = ${if (withBody) exprToScalaTree(newBody) else EmptyTree}"
        def getArgLists[T](body: OpenCode[T], bodyTyp: TypeRep): (List[List[ValDef]], OpenCode[_], TypeRep) = {
          val wrapped = code"Dummy[${body.Typ}](${body.withUnderlyingTyp})"
          wrapped match { // wrapped so we can match on the types invariantly (otherwise all kind of nuisances happen)
            case c"Dummy[($t0) => $tr]($b)" =>
              val x -> nb = b match {
                case c"($x:$$t0) => ($nb:$$tr)" => x.`internal bound` -> nb.asOpenCode
                case _ => bindVal("_",t0.rep,Nil) -> b
              }
              val name = x.name + "_" + freshName
              val tn = TermName(name)
              symTable += x -> tn.toString // TODO use weakHashMap for symTable?
              val (rest,newBody,newBodyTyp) = getArgLists(nb, tr.rep)
              (((q"val $tn: ${x.typ.tpe}" :: Nil)::rest), newBody, newBodyTyp)
            case c"Dummy[($t0,$t1) => $tr]($b)" =>
              val (x0,x1) -> nb = b match {
                case c"($x0:$$t0, $x1:$$t1) => ($nb:$$tr)" => (x0.`internal bound`, x1.`internal bound`) -> nb.asOpenCode
                case _ => (bindVal("_",t0.rep,Nil), bindVal("_",t1.rep,Nil)) -> b
              }
              val tn0 = TermName(x0.name + "_" + freshName)
              val tn1 = TermName(x1.name + "_" + freshName)
              symTable += x0 -> tn0.toString
              symTable += x1 -> tn1.toString
              val (rest,newBody,newBodyTyp) = getArgLists(nb, tr.rep)
              (((q"val $tn0: ${x0.typ.tpe}" :: q"val $tn1: ${x1.typ.tpe}" :: Nil)::rest), newBody, newBodyTyp)
            case c"Dummy[($t0,$t1,$t2) => $tr]($b)" =>
              val (x0,x1,x2) -> nb = b match {
                case c"($x0:$$t0, $x1:$$t1, $x2:$$t2) => ($nb:$$tr)" =>
                  (x0.`internal bound`, x1.`internal bound`, x2.`internal bound`) -> nb.asOpenCode
                case _ => (bindVal("_",t0.rep,Nil), bindVal("_",t1.rep,Nil), bindVal("_",t2.rep,Nil)) -> b
              }
              val tn0 = TermName(x0.name + "_" + freshName)
              val tn1 = TermName(x1.name + "_" + freshName)
              val tn2 = TermName(x2.name + "_" + freshName)
              symTable += x0 -> tn0.toString
              symTable += x1 -> tn1.toString
              symTable += x2 -> tn1.toString
              val (rest,newBody,newBodyTyp) = getArgLists(nb, tr.rep)
              (((q"val $tn0: ${x0.typ.tpe}" :: q"val $tn1: ${x1.typ.tpe}" :: q"val $tn2: ${x2.typ.tpe}" :: Nil)::rest), newBody, newBodyTyp)
            case _ => (Nil, body, bodyTyp)
          }
        }
        val (argss,newBody,bodyTyp) = getArgLists(body,typeRepOf[T])
        q"def ${TermName(name)}(...$argss): ${bodyTyp.tpe} = ${if (withBody) exprToScalaTree(newBody) else EmptyTree}"
      }
      //override def toString = sru.showCode(toScalaTree)
      override def toString = defName+"#"+sru.showCode(toScalaTreeImpl(false))
      /*
      //val ANF = IR.asInstanceOf[squid.ir.SimpleANFBase]
      programGenTransformer.registerRule(
        //code"123".rep,
        //const(123),
        wrapExtract(const(123)), // without wrapExtract, it tries to rewrite it with itself!!!
        //ANF.Rep(Constant(123).asInstanceOf[ANF.Def]).asInstanceOf[Rep],
        //const(123) alsoApply println alsoApply ???,
        xtr => 
        //println(s"Matched $xtr") thenReturn 
        Some(code"666".rep)
        //  base.debugFor(Some(const(666))) thenReturn
        //??? thenReturn
        //Some(const(666))
      )
      */
    }
    //abstract class Field[T:CodeType](name: String, mkBody: => Code[T,Ctx & args.Ctx]) extends Method(name, mkBody) {
    abstract class Field[T:CodeType] private[ProgramGen](name: String) extends Method[T](name) {
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
      override def toScalaTreeImpl(withBody: Bool) = { // TODO use withBody?
        import sru._
        val scalaBody = exprToScalaTree(body)
        if (name.isEmpty) scalaBody else q"val ${TermName(name)}: ${typeRepOf[T].tpe} = $scalaBody"
      }
      override def toString = s"val $name = ${sru.showCode(toScalaTree)}"
    }
    class Param[T:CodeType] private[ProgramGen](name: String) extends Field[T](name) {
      //val accessor = Variable[T]("accessor")
      def mkBody: Code[T,Ctx & self.Ctx] = insideRef // Q: makes sense?
      override def effect: SimpleEffect = SimpleEffect.Pure // or cyclic def!!
      override def toScalaTreeImpl(withBody: Bool) = {
        import sru._
        q"val ${TermName(name)}: ${typeRepOf[T].tpe}"
      }
    }
    protected def allocateName(isTerm: Bool)(name: String) = {
      val m = if (isTerm) usedTermNames else usedTypeNames
      val idx = m(name)
      m(name) = idx + 1
      if (idx > BASE_NAME_INDEX) s"${name}_$idx" else name
    }
    def method[T:CodeType](bodyFun: => Code[T,Ctx])(implicit srcName: SrcName): Method[T] =
      new Method[T](srcName.value |> allocateName(false)) { def mkBody = bodyFun } alsoApply {methods += _}
    protected def field[T:CodeType](bodyFun: => Code[T, Ctx])(implicit srcName: SrcName): Field[T] =
      new Field[T](srcName.value |> allocateName(false)) { def mkBody = bodyFun } alsoApply {fields += _}
    protected def effect[T:CodeType](bodyFun: => Code[T, Ctx]): Field[T] =
      new Field[T]("") { def mkBody = bodyFun } alsoApply {fields += _}
    protected def param[T:CodeType](implicit srcName: SrcName): Param[T] =
      new Param[T](srcName.value |> allocateName(false)) alsoApply {params += _}
    
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
    
    def requireFrozenFields = assert(frozenFields)
    private[this] var frozenFields = false
    def delayedInit(body: => Unit) = {
      //println(s"Init ${selfClass.defName} ${frozenFields}")
      val earlyInit = selfClass.defName === null
      // ^ it turns out that `delayedInit` can actually be called several times... the first time (where defName is null) it's too early to register rules!
      frozenFields = false
      body
      if (!earlyInit) {
        frozenFields = true
        registerReductions()
      }
      //println(s"Done ${selfClass.name}")
    }
    private def registerReductions() = {
      requireFrozenFields
      // TODOne if pure
      //println(ctor,ctorEffect,ctorEffect.immediate)
      if (!ctorEffect.immediate) // we could also inline effects, but that could be dangerous (increases program size)
      for {
        //(f,i) <- (fields ++ params).zipWithIndex
        (f,i) <- params.zipWithIndex
      } yield {
        val pnames = params.map(p => p -> (p.name+"_hole"))
        programGenTransformer.registerRule(
        //code"123".rep,
        //const(123),
          wrapExtract{
            //rep(MethodApp(hole("self",Self.rep),f.sym,Nil,Nil,f.Typ.rep))
            rep(MethodApp(New(pnames.map{case p->n=>hole(n,p.Typ.rep)}),f.sym,Nil,Nil,f.Typ.rep))
          },
          xtr => {
            val args = pnames.map(_._2).map(xtr._1)
            // inlining effects: this impl is buggy as it does not take care of self references!
            /*
            val res = args(i)
            val effects = fields.filter(_.effect.immediate).map(_.body.rep)
            //val ret = effects.foldLeft()
            val ret = Imperative(effects:_*)(res)
            Some(ret)
            */
            Some(args(i))
            //Some(const(123))
          }
        )
      }
    }
  }
  
  class ScalaUnit extends Scope[World] {
    //type Ctx = World
    def definitions = classes
  }
  
}
}

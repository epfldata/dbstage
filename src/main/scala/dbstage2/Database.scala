package dbstage2

import squid.utils._
import Embedding.Predef._
import Assignment.Assignment
import squid.ir.BottomUpTransformer
import squid.ir.FixPointRuleBasedTransformer
import squid.ir.FixPointTransformer
import squid.ir.RuleBasedTransformer
import squid.ir.SimpleRuleBasedTransformer

///**  */
//case class Database(rels: Relation) {
//  
//}

abstract class Database[Ctx] { self =>
  ////type Abs[T]
  ////def abs[A]: Abs[A]
  //type Abstracted[T]
  //def abstracted[T,D](pgrm: Code[T, Ctx & D]): Code[Abstracted[T], D]
  //
  //def withRelation[A<:Record](r: Relation[A]) = new Database[Ctx & r.Ctx] {
  //  type Abstracted[T] = self.Abstracted[r.type => T]
  //  def abstracted[T,E](pgrm: Code[T, Ctx with E]): Code[Abstracted[T], E] = {
  //    //val a = that.abstracted[T, self.Ctx with E](pgrm)
  //    self.abstracted[r.type => T,E](r)
  //  }
  //}
  
  val assgnt: Assignment[Ctx]
  
  def withRelation[A<:Record](r: Relation[A]): Database[Ctx & r.Ctx] = new Database[Ctx & r.Ctx] {
    val assgnt: Assignment[Ctx & r.Ctx] = {
      //self.assgnt && r
      self.assgnt.&&[r.Ctx](r)
      //???
    }
  }
  
  // the desugaring/partial-eval of Record stuff was moved to be online in Embedding
  
  // TODO mv this out
  //val recordAccessApply = base.loadMtdSymbol(base.loadTypSymbol("dbstage.`package`$"), "toAccessOps")
  //val recordAccessApply = base.loadMtdSymbol(base.loadTypSymbol("dbstage.package$"), "toAccessOps")
  val recordAccessApply = base.loadMtdSymbol(base.loadTypSymbol("dbstage.AccessOps"), "apply")
  val fieldValue = base.loadMtdSymbol(base.loadTypSymbol("dbstage.Field"), "value")
  
  //object ManualDesug extends Embedding.SelfTransformer with SimpleRuleBasedTransformer {
  object ManualDesug extends Embedding.SelfTransformer with FixPointRuleBasedTransformer {
    //def handleApply[C](args: Code[Any,C]) = 
    def handleApply[R<:FieldModule:CodeType](args: Seq[Seq[OpenCode[Any]]]): ClosedCode[R] = args match {
      case Seq(Seq(ops),Seq(acc)) =>
        ops match {
          case code"toAccessOps[$s]($self)" =>
            //self.asInstanceOf[ClosedCode[Nothing]]
            val fld = code"${acc.asInstanceOf[OpenCode[Access[R,s.Typ]]]}.field($self)"
            //.asInstanceOf[ClosedCode[R]]
            base.Code(base.methodApp(fld.rep,fieldValue,Nil,Nil,codeTypeOf[R].rep))
        }
    }
    rewrite {
      case code"::[$hdt,$tlt]($hd,$tl).hd" => hd 
      case code"::[$hdt,$tlt]($hd,$tl).tl" => tl 
      case code"Access[$f where (f <:< FieldModule),$r]($idx,$field).idx" => idx 
      case code"Access[$f where (f <:< FieldModule),$r]($idx,$field).field" => field 
      case code"${base.MethodApplication(ma)}:($t where (t <:< FieldModule))" if ma.symbol === recordAccessApply =>
        println(ma)
        handleApply[t.Typ](ma.args)
      case code"false" => code"false"
    }
  }
  //object Trans extends Embedding.TransformerWrapper(Embedding.Desugaring,ManualDesug) with FixPointTransformer with BottomUpTransformer
  object Trans extends Embedding.TransformerWrapper(Embedding.Desugaring,ManualDesug) with BottomUpTransformer with FixPointTransformer
  
  //def compile[T](pgrm: Code[T,Ctx]): T = {
  def compile[T<:Record:CodeType](pgrm: Code[Query[T],Ctx]): () => Iterable[T] = {
    //(pgrm:Code[_,Ctx]) analyse {
    //  case code"${AsVariable(v)}:$t" =>
    //    println(s"VAR $v ${assgnt.vars(v)}")
    //}
    println(s"PGRM: $pgrm")
    // Simplify Record operations
    
    /*
    val res = pgrm transformWith Embedding.Desugaring fix_rewrite {
      //case code"AccessOps[$t]($self).apply[$tf where (tf <:< FieldModule)]($acc)" => code"???" // Embedding Error: Unknown type `tf#Typ` does not have a TypeTag to embed it as uninterpreted.
      //case code"AccessOps[$t]($self)" => code"???"
      case code"::[$hdt,$tlt]($hd,$tl).hd" => hd 
      case code"::[$hdt,$tlt]($hd,$tl).tl" => tl 
      case code"Access[$f where (f <:< FieldModule),$r]($idx,$field).idx" => idx 
      case code"Access[$f where (f <:< FieldModule),$r]($idx,$field).field" => field 
      case code"${base.MethodApplication(ma)}:($t where (t <:< FieldModule))" if ma.symbol === recordAccessApply =>
        println(ma)
        //ma.args.head.head.asInstanceOf[ClosedCode[t.Typ]]
        //ma.args.head.head match {
        //  case code"toAccessOps[$s]($self)" =>
        //    self.asInstanceOf[ClosedCode[t.Typ]]
        //}
        ManualDesug.handleApply[t.Typ](ma.args)
        //???
      case code"false" => code"false"
    } transformWith Embedding.Desugaring
    */
    //val res = pgrm transformWith Trans
    val res = pgrm transformWith Trans transformWith Trans
    
    //pgrm rewrite {
    //  case 
    //}
    assgnt.compile(code"() => $res.run")
  }
  
}
object Database {
  def apply() = new Database[Any] {
    ////type Abs[T] = T
    ////def abs[T] = identity
    //type Abstracted[T] = T
    //def abstracted[T,E](pgrm: Code[T, E]): Code[Abstracted[T], E] = pgrm
    val assgnt = Assignment.empty
  }
}

object AsVariable {
  def unapply[T,C](c: Code[T,C]) = c.rep |>? {
    case base.RepDef(bv:base.BoundVal) =>
      base.mkVariable(bv)
  }
}

import scala.language.higherKinds

class SelfAssignedVariable[Typ:CodeType] extends Variable[Typ] with AssignmentBase { self: Typ =>
  def vars = Set(this)
  type Abstracted[T] = Typ => T
  def abstracted[T,D](pgrm: Code[T,Ctx with D]): Code[Abstracted[T], D] =
    base.Code(base.lambda(bound::Nil, pgrm.rep))
  protected def applyAbstracted[T](abs: Abstracted[T]): T = abs(self)
}
object Assignment {
  type Assignment[+C] = AssignmentBase { type Ctx <: C }
  def empty = new AssignmentBase {
    def vars = Set.empty
    type Ctx = Any
    type Abstracted[T] = T
    def abstracted[T,D](pgrm: Code[T,Ctx with D]): Code[Abstracted[T], D] = pgrm
    protected def applyAbstracted[T](abs: Abstracted[T]): T = abs
  }
}
trait AssignmentBase { self =>
  def vars: Set[Variable[_]]
  type Ctx
  type Abstracted[T]
  def abstracted[T,D](pgrm: Code[T,Ctx with D]): Code[Abstracted[T], D]
  def && [D](that: Assignment[D]) = new AssignmentBase {
    def vars = self.vars ++ that.vars
    type Ctx = self.Ctx with D
    type Abstracted[T] = self.Abstracted[that.Abstracted[T]]
    def abstracted[T,E](pgrm: Code[T,Ctx with E]): Code[Abstracted[T], E] = {
      val a = that.abstracted[T,self.Ctx with E](pgrm)
      self.abstracted[that.Abstracted[T],E](a)
    }
    protected def applyAbstracted[T](abs: Abstracted[T]): T = that.applyAbstracted(self.applyAbstracted(abs))
    override def toString = ???
  }
  def run[T](pgrm: Code[T,Ctx]): T = applyAbstracted[T](abstracted(pgrm).run)
  def compile[T](pgrm: Code[T,Ctx]): T = applyAbstracted(abstracted(pgrm).compile)
  protected def applyAbstracted[T](abs: Abstracted[T]): T
}

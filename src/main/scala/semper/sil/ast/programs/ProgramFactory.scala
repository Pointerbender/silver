package semper.sil.ast.programs {

import semper.sil.ast.source.SourceLocation
import semper.sil.ast.domains._
import symbols._
import semper.sil.ast.types._
import semper.sil.ast.expressions.ExpressionFactory
import semper.sil.ast.methods.{ScopeFactory, MethodFactory}
import collection.mutable

final class ProgramFactory
(
  val name: String
  )(val sourceLocation: SourceLocation, val comment: List[String])
  extends NodeFactory
  with DataTypeFactory
  with ExpressionFactory
  with ScopeFactory {
  //////////////////////////////////////////////////////////////////////////
  override val programFactory = this
  override val parentFactory = None

  //////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////
  def getProgram: Program = {
    for (df <- domainFactories) df.compile()
    for (pf <- predicateFactories) pf.compile()
    for (mf <- methodFactories) mf.compile()
    for (ff <- functionFactories) ff.compile()

    val program = new Program(name, domains, fields, functions, predicates, (for (mf <- methodFactories) yield mf.method).toSet, this)(sourceLocation, comment)
    //    scope = Some[Scope](program)
    program
  }

  //////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////
  def getDomainFactory(name: String, typeVariableNames: Seq[(SourceLocation, String, List[String])], sourceLocation: SourceLocation, comment: List[String] = Nil): DomainTemplateFactory = {
    require(domainFactories.forall(_.name != name))
    val result = new DomainTemplateFactory(this, name, typeVariableNames)(sourceLocation, comment)
    domainFactories += result
    result
  }

  //////////////////////////////////////////////////////////////////////////
  def getMethodFactory(name: String)(sourceLocation: SourceLocation, comment: List[String] = Nil): MethodFactory = {
    require(methodFactories.forall(_.name != name))
    val result = new MethodFactory(this, name)(sourceLocation, comment)
    methodFactories += result
    result
  }

  //////////////////////////////////////////////////////////////////////////
  def getPredicateFactory(name: String, sourceLocation: SourceLocation, comment: List[String] = Nil): PredicateFactory = {
    require(predicateFactories.forall(_.name != name))
    val result = new PredicateFactory(this, name)(sourceLocation, comment)
    predicateFactories += result
    result
  }

  //////////////////////////////////////////////////////////////////////////
  def getFunctionFactory(name: String, params: Seq[(SourceLocation, String, DataType)], resultType: DataType, sourceLocation: SourceLocation, comment: List[String] = Nil): FunctionFactory = {
    require(functionFactories.forall(_.name != name))
    require(params.forall(dataTypes contains _._3))
    require(params.forall((x) => params.forall((y) => x == y || x._2 != y._2)))
    val result = new FunctionFactory(this, name, params, resultType)(sourceLocation, comment)
    functionFactories += result
    result
  }

  def makeDomainInstance(factory: DomainTemplateFactory, ta: DataTypeSequence): Domain = {
    require(ta.forall(dataTypes contains _))
    var r = factory.getInstance(ta)
    pDomains += r
    r
  }


  //////////////////////////////////////////////////////////////////////////
  //@Symbol construction
  //////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////
  def defineField(name: String, dataType: DataType)(sourceLocation: SourceLocation): Field = {
    require(fields.forall(_.name != name))
    require(dataTypes contains dataType)
    require(dataType.freeTypeVariables.isEmpty)
    defineField(
      dataType match {
        case d: NonReferenceDataType => new NonReferenceField(name, d)(sourceLocation, "lala" :: Nil)
        case r: ReferenceDataType => new ReferenceField(name)(sourceLocation, "lalo" :: Nil)
        case _ => throw new Exception("Tried to create field of non groud type")
      }
    )
  }

  //////////////////////////////////////////////////////////////////////////
  private def defineField(f: Field): Field = {
    fields += f
    f
  }

  //////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////////////////////////////////////
  protected[sil] override val domainFactories = new mutable.HashSet[DomainTemplateFactory]
  protected[sil] override val methodFactories = new mutable.HashSet[MethodFactory]

  protected[sil] val predicateFactories = new mutable.HashSet[PredicateFactory]
  protected[sil] val functionFactories = new mutable.HashSet[FunctionFactory]

  override val fields: collection.mutable.Set[Field] = new mutable.HashSet[Field]

  override def functions: Set[Function] = (for (ff <- functionFactories) yield ff.pFunction).toSet

  override def predicates = (for (pf <- predicateFactories) yield pf.pPredicate).toSet

  /*  pDataTypes += integerType
    pDataTypes += permissionType
    pDataTypes += referenceType
  */
  override def dataTypes: collection.Set[DataType] =
    pDataTypes ++
      //    Set(integerType,permissionType,referenceType,booleanType) ++
      (for (d <- domains) yield d.getType).toSet

  def emptyDTSequence = new DataTypeSequence(List.empty[DataType])

  private[sil] val pDomains: mutable.HashSet[Domain] = mutable.HashSet(integerDomain, permissionDomain, referenceDomain, booleanDomain)

  def domains: collection.Set[Domain] =
    pDomains //++
  //      ( for (df <- domainFactories) yield df.domainTemplate).toSet ++
  //      (for (df <- domainFactories) yield df.pDomainTemplate.instances).flatten

  def domainTemplates: collection.Set[DomainTemplate] = (for (dt <- domainFactories) yield dt.pDomainTemplate)

  override def domainFunctions: collection.Set[DomainFunction] =
    domains.flatMap(_.functions)

  /*union
       (for (f <-
             (for (d <- domainFactories) yield
               if (d.typeVariables.isEmpty) d.domainTemplate.functions
               else collection.Set[DomainFunction]()).flatten)
       yield f)*/

  override def domainPredicates: collection.Set[DomainPredicate] =
    domains.flatMap(_.predicates)

  /*union
       (for (p <-
             (for (d <- domainFactories) yield
               if (d.typeVariables.isEmpty) d.domainTemplate.predicates
               else collection.Set[DomainPredicate]()).flatten)
       yield p)                      */

  override val typeVariables = collection.Set[TypeVariable]()
  override val programVariables = collection.Set[ProgramVariable]()
  override val inputProgramVariables = collection.Set[ProgramVariable]()
  override val outputProgramVariables = collection.Set[ProgramVariable]()
}

}



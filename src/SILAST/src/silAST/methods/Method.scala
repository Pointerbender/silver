package silAST.methods

import implementations.Implementation
import silAST.ASTNode
import silAST.source.SourceLocation
import collection.Set
import collection.mutable.HashSet

final class Method private[silAST](
                                    sl: SourceLocation,
                                    val name: String,
                                    val signature: MethodSignature
                                    ) extends ASTNode(sl) {
  override def toString = "method " + name + signature.toString +
    (for (i <- implementations) yield i).mkString("\n","\n","\n")


  private[silAST] val pImplementations = new HashSet[Implementation]

  def implementations: Set[Implementation] = pImplementations.toSet

}
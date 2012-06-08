package silAST.programs.symbols

import silAST.ASTNode
import silAST.expressions.Expression
import silAST.source.SourceLocation

final class Predicate private[silAST](
         val name: String
         )(val sourceLocation: SourceLocation,val factory : PredicateFactory, val comment:List[String])
  extends ASTNode
{
  override def toString = "location " + name + " = " + expression.toString

  private[silAST] var pExpression: Option[Expression] = None

  def expression: Expression = pExpression.get

  override def equals(other: Any): Boolean = {
    other match {
      case p: Predicate => this eq p
      case _ => false
    }
  }

  override def hashCode(): Int = name.hashCode()

}
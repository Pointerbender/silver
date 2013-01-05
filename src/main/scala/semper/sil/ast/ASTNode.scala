package semper.sil.ast

import source.SourceLocation

//TODO: check equality/hashCode
abstract class ASTNode protected[sil] {
  def sourceLocation: SourceLocation

  override def toString: String //String representation

  override def equals(other: Any): Boolean

  override def hashCode(): Int

  def comment: scala.collection.immutable.List[String] //comments
}
package silAST.expressions.assertion.terms

import silAST.AtomicNode

trait GAtomicTerm[+T <:GTerm[T]] extends GTerm[T] {
	override def subTerms = Nil
}
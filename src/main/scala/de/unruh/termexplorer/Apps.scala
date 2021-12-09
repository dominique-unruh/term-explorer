package de.unruh.termexplorer

import de.unruh.isabelle.pure.{App, Term}

import scala.annotation.tailrec

/** Pattern matcher for repeated applications in Isabelle terms. E.g., `f a b` (of type [[Term]]) matches `App(f,a,b)` (unless `f` is an application itself) */
object Apps {
  @tailrec
  private def destruct(term: Term, args: List[Term]): List[Term] = term match {
    case App(head, arg) => destruct(head, arg :: args)
    case _ => term :: args
  }

  def unapplySeq(term: Term): Option[Seq[Term]] = term match {
    case App(head, arg) => Some(destruct(head, List(arg)))
    case _ => None
  }
}

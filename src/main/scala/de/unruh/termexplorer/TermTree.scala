package de.unruh.termexplorer

import de.unruh.isabelle.pure._
import de.unruh.termexplorer.TermTree.Node
import de.unruh.termexplorer.TermTree.TermNode

import java.util
import javax.swing.JTree
import javax.swing.tree.TreeNode
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters._

/** Swing component that shows an Isabelle term (or typ) as a tree.
 *
 * @param root root node, e.g., a [[TermNode]] with null parent
 **/
class TermTree(root: Node) extends JTree(root) {
  assert(root.getParent==null)
}

object TermTree {
  def apply(term: Term, context: Context) = new TermTree(new TermNode(term, context, parent=null))

  /** A node in a term/type tree */
  abstract class Node(parent: Node) extends TreeNode {
    /** All children, as an immutable sequence.
     * Should be implemented as a `lazy val` unless it is guaranteed that there are no or only few (recursive) descendants. */
    val childNodes: Seq[Node]

    override def getChildAt(childIndex: Int): TreeNode = childNodes(childIndex)
    override def getChildCount: Int = childNodes.length
    override def getParent: TreeNode = parent
    override def getIndex(node: TreeNode): Int =
      childNodes.indexOf(node)
    override def getAllowsChildren: Boolean = true
    override def isLeaf: Boolean = childNodes.isEmpty
    override def children(): util.Enumeration[_ <: TreeNode] =
      java.util.Collections.enumeration(childNodes.asJava)
  }

  class TermNode(val term: Term, context: Context, parent: Node) extends Node(parent) {
    override val toString: String = term.pretty(context)

    private def makeChild(t: Term) = new TermNode(t, context, this)
    private def makeChild(t: Typ) = new TypNode(t, context, this)
    private def makeChild(str: String) = new StringNode(str, this)

    private def typeOf(t: Term): Node =
      try makeChild(t.fastType)
      catch {
        case e: Throwable => makeChild("no type: " + e)
      }

    override lazy val childNodes: Seq[Node] = {
      typeOf(term) ::
        (term match {
          case Apps(terms@_*) => terms.map(makeChild).toList
          case Abs(n, typ, body) => List(makeChild(n), makeChild(typ), makeChild(body))
          case _ => Nil
        })
    }
  }

  class TypNode(val typ: Typ, context: Context, parent: Node) extends Node(parent) {
    println(s"Creating ${typ.pretty(context)}")
    override val toString: String = typ.pretty(context)

    private def makeChild(t: Typ) = new TypNode(t, context, this)
    private def makeChild(str: String) = new StringNode(str, this)

    override lazy val childNodes: Seq[Node] = {
      typ match {
        case Type(name, args@_*) => makeChild(name) :: args.toList.map(makeChild)
        case _ => Nil
      }
    }
  }

  class StringNode(val string: String, parent: Node) extends Node(parent) {
    override val childNodes: Seq[Node] = Nil
    override val toString: String = string
  }
}

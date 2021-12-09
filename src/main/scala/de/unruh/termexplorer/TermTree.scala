package de.unruh.termexplorer

import de.unruh.isabelle.control.Isabelle
import de.unruh.isabelle.control.Isabelle.Setup
import de.unruh.isabelle.control.IsabelleComponent.isabelle
import de.unruh.isabelle.pure._

import java.awt.Dimension
import java.awt.event.{KeyEvent, KeyListener}
import java.nio.file.Path
import java.util
import javax.swing.tree.TreeNode
import javax.swing.{JFrame, JScrollPane, JTree, WindowConstants}
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.jdk.CollectionConverters._

class TermTree(root: Node) extends JTree(root) {
}

object Viewer {
  def showViewer(isabelle: Isabelle, context: Context, term: Term): Unit = {
    implicit val i : Isabelle = isabelle

    val root = new TermNode(term, context, null)
    val tree = new TermTree(root)
    val treeView = new JScrollPane(tree)

    val frame = new JFrame("Term Tree Demo")
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)

    frame.setPreferredSize(new Dimension(600,600))

    frame.add(treeView)

    frame.addKeyListener(new KeyListener {
      override def keyTyped(e: KeyEvent): Unit = println(e)
      override def keyPressed(e: KeyEvent): Unit = println(e)
      override def keyReleased(e: KeyEvent): Unit = println(e)
    })

    frame.pack()
    frame.setVisible(true)
  }
  def main(args: Array[String]): Unit = {
     implicit val isabelle: Isabelle = new Isabelle(Setup(
      isabelleHome = Path.of("/opt/Isabelle2021-1-RC5")))
    val context = Context("Main")
    val term = Term(context, "!x. x+1 = 1+(x::nat)")
    showViewer(isabelle, context, term)
  }
}

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

abstract class Node(parent: Node) extends TreeNode {
  val childNodes : Seq[Node]

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
  println(s"Creating ${term.pretty(context)}")
  override val toString: String = term.pretty(context)

  private def makeChild(t: Term) = new TermNode(t, context, this)
  private def makeChild(t: Typ) = new TypNode(t, context, this)
  private def makeChild(str: String) = new StringNode(str, this)

  private def typeOf(t: Term): Node =
    try makeChild(t.fastType)
    catch { case e: Throwable => makeChild("no type: " + e) }

  override lazy val childNodes: Seq[Node] = {
    typeOf(term) ::
      (term match {
        case Apps(terms @ _*) => terms.map(makeChild).toList
        case Abs(n,typ,body) => List(makeChild(n), makeChild(typ), makeChild(body))
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
        case Type(name, args @_*) => makeChild(name) :: args.toList.map(makeChild)
        case _ => Nil
      }
  }
}

class StringNode(val string: String, parent: Node) extends Node(parent) {
  override val childNodes: Seq[Node] = Nil
  override val toString: String = string
}

/*
class TestTreeNode(data: List[Int], parent: TestTreeNode) extends TreeNode {
  println(s"Creating $data")
  override def toString: String = if (data.isEmpty) "root" else data.mkString(":")

  override def getChildAt(childIndex: Int): TreeNode = myChildren(childIndex)

  lazy val myChildren: List[TestTreeNode] = for (i <- (1 to Random.nextInt(5)).toList)
    yield new TestTreeNode(i::data, this)

  override def getChildCount: Int = myChildren.length

  override def getParent: TreeNode = parent

  override def getIndex(node: TreeNode): Int =
    myChildren.indexOf(node)

  override def getAllowsChildren: Boolean = true

  override def isLeaf: Boolean = myChildren.isEmpty

  override def children(): util.Enumeration[_ <: TreeNode] =
    java.util.Collections.enumeration(myChildren.asJava)
}
*/

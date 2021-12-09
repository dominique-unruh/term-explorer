package de.unruh.termexplorer

import de.unruh.isabelle.control.Isabelle
import de.unruh.isabelle.control.Isabelle.{ID, Setup}
import de.unruh.isabelle.pure.{Context, Term}

import java.awt.Dimension
import java.awt.event.{ActionEvent, InputEvent, KeyEvent}
import java.nio.file.Path
import javax.swing._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.io.Source

object DemoWindow {
  def showDemoWindow(context: Context, term: Term): Unit = {
    val frame = new JFrame("Term Tree Demo")
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE)
    frame.setPreferredSize(new Dimension(600, 600))

    // Configuring Ctrl-W, Q, Esc to close this frame
    val inputMap = frame.getRootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK), "close window")
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close window")
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0), "close window")
    frame.getRootPane.getActionMap.put("close window", new AbstractAction() {
      override def actionPerformed(e: ActionEvent): Unit = frame.dispose()
    })

    val tree = TermTree(term, context)
    frame.add(new JScrollPane(tree))

    frame.pack()
    frame.setVisible(true)
  }

  def main(args: Array[String]): Unit = {
    // Load the path of the local Isabelle installation from the file .isabelle-home
    val source = Source.fromFile(".isabelle-home")
    val isabelleHome = Path.of(source.mkString.trim)
    source.close()

    implicit val isabelle: Isabelle = new Isabelle(Setup(isabelleHome))
    val context = Context("Main")
    val id : ID = Await.result(context.mlValue.id, Duration.Inf)
    val term = Term(context, "!x. x+1 = 1+(x::nat)")
    term.isabelle
    showDemoWindow(context, term)
  }
}

package de.unruh.termexplorer

import de.unruh.isabelle.control.IsabelleComponent
import de.unruh.isabelle.pure.{Context, Term}
import de.unruh.termexplorer.TermExplorerComponent.showTermExplorer
import isabelle.{Bytes, Scala_Project}
import isabelle.Scala.{Fun, Fun_String, Fun_Strings, Functions}
import de.unruh.isabelle.control.IsabelleComponent.isabelle
import de.unruh.isabelle.pure.Implicits._

import scala.concurrent.ExecutionContext.Implicits.global

class TermExplorerComponent extends Functions(showTermExplorer)

object TermExplorerComponent {
  object showTermExplorer extends Fun_Strings("showTermExplorer") {
    override val here: Scala_Project.Here = Scala_Project.here
    override def apply(args: List[String]): List[String] = {
      val List(contextID, termID) = args
      val context = IsabelleComponent.unsafeMLValueFromNumericID[Context](contextID.toLong).retrieveNow
      val term = IsabelleComponent.unsafeMLValueFromNumericID[Term](termID.toLong).retrieveNow
      val thread = new Thread(() => Viewer.showViewer(IsabelleComponent.isabelle, context, term))
      thread.setDaemon(true)
      thread.start()
      Nil
    }
  }
}

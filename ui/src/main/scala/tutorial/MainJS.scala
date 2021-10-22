package tutorial

import org.scalajs.dom
import org.scalajs.dom.document

object MainJS {
  def main(args: Array[String]): Unit = {}
//    appendPar(document.body, "hello world")

  def appendPar(targetNode: dom.Node, text: String): Unit =
    val parNode = document.createElement("p")
    parNode.textContent = text
    targetNode.appendChild(parNode)

}

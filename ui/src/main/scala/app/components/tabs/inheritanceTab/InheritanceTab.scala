package app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import org.scalajs.dom.{Element, Node}
import com.raquo.laminar.nodes.ChildNode
import org.scalajs.dom
import org.jpablo.typeexplorer.TextDocumentsWithSource

def svgToLaminar(svg: Element) =
  new ChildNode[Element] { val ref = svg }

def inheritanceTab(
  $documents: EventStream[List[TextDocumentsWithSource]],
  $svgDiagram: EventStream[Element]
) =
  div(
    cls := "text-document-areas",
    div(cls := "structure",
      children <-- InheritanceTree.buildTree($documents)
    ),
    div (
      cls := "inheritance-container",
      div(child <-- $svgDiagram.map(svgToLaminar))
    )
  )

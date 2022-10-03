package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTree
import org.scalajs.dom

def svgToLaminar(svg: dom.Element) =
  new ChildNode[dom.Element] { val ref = svg }

def inheritanceTab(
  $documents  : EventStream[List[TextDocumentsWithSource]],
  $svgDiagram : EventStream[dom.Element],
  $classes    : EventStream[InheritanceDiagram],
  $selectedUri: EventBus[String]
) =
  div(
    cls := "text-document-areas",
    div(cls := "structure",
      children <-- InheritanceTree.build($classes, $selectedUri)
    ),
    div (
      cls := "inheritance-container",
      div(child <-- $svgDiagram.map(svgToLaminar))
    )
  )

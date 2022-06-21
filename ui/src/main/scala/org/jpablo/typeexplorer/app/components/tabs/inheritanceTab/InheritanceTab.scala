package org.jpablo.typeexplorer.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.TextDocumentsWithSource
import org.jpablo.typeexplorer.inheritance.InheritanceDiagram
import org.scalajs.dom

def svgToLaminar(svg: dom.Element) =
  new ChildNode[dom.Element] { val ref = svg }

def inheritanceTab(
  $documents  : EventStream[List[TextDocumentsWithSource]],
  $svgDiagram : EventStream[dom.Element],
  $classes    : EventStream[InheritanceDiagram]
) =
  div(
    cls := "text-document-areas",
    div(cls := "structure",
      children <-- InheritanceTree.build($classes)
    ),
    div (
      cls := "inheritance-container",
      div(child <-- $svgDiagram.map(svgToLaminar))
    )
  )

package app.components.tabs

import com.raquo.laminar.api.L.*
import org.scalajs.dom.{Element, Node}
import com.raquo.laminar.nodes.ChildNode
import org.scalajs.dom

def svgToLaminar(svg: Element) =
  new ChildNode[Element] { val ref = svg }

def inheritanceTab(svgDiagram: EventStream[Element]) =
  div (
    idAttr := "te-center-column",
    div (
      child <-- svgDiagram.map(svgToLaminar)
    )
  )

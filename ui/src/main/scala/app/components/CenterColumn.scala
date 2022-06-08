package app.components

import com.raquo.laminar.api.L.*
import org.scalajs.dom.{Element, Node}
import com.raquo.laminar.nodes.ChildNode
import org.scalajs.dom

def svgToLaminar (svg: Element) =
  new ChildNode[Element] { val ref = svg }

def centerColumn (newDiagramStream: EventStream[Element]) =
  div (
    idAttr := "te-center-column",
    div("center"),
    div (
      child <-- newDiagramStream.map(svgToLaminar)
    )
  )

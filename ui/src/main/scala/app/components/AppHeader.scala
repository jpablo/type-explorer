package app.components

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import bootstrap.{dropdown, navbar}

object AppHeader {

}

enum DiagramType:
  case Inheritance
  case CallGraph

import DiagramType.*

def appHeader(selectionBus: EventBus[DiagramType]) =
  navbar (
    id    = "te-header",
    brand = "Type Explorer",
    dropdown (
      label = "Diagram",
      elements = List(Inheritance, CallGraph),
      selectionBus = selectionBus
    )
  )


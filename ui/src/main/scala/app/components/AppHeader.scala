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

def appHeader(selection: EventBus[DiagramType], projectPath: Var[String]) =
  navbar (
    id    = "te-header",
    brand = "Type Explorer",
    projectPath = projectPath,
    dropdown (
      label = "Diagram",
      elements = List(Inheritance, CallGraph),
      selection = selection
    )
  )


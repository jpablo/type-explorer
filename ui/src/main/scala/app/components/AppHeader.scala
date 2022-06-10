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
  div(
    idAttr := "te-header",
    navbar (
      id    = "te-header-content",
      brand = "Type Explorer",
      projectPath = projectPath,
      dropdown (
        label = "Diagram",
        elements = List(Inheritance, CallGraph),
        selection = selection
      )
    )
  )


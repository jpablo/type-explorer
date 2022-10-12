package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.bootstrap.{dropdown, navbar}
import io.laminext.core.*


enum DiagramType:
  case Inheritance
  case CallGraph

import DiagramType.*

def appHeader(selection: EventBus[DiagramType], projectPath: StoredString) =
  val onEnterPress = onKeyPress.filter(_.keyCode == dom.ext.KeyCode.Enter)
  div(
    idAttr := "te-header",
    navbar(
      id    = "te-header-content",
      brand = "Type Explorer",
      projectPath = projectPath,

      li(cls := "nav-item", span(cls := "nav-link", b("base path:"))),

      li(cls := "nav-item", a(cls := "nav-link", href := "#", child.text <-- projectPath.signal) ),

      li(cls := "nav-item", 
        form (cls := "d-flex me-2",
          input (
            cls := "form-control me-2",
            tpe := "search",
            onEnterPress.preventDefault.mapToValue --> projectPath.set,
            value <-- projectPath.signal
          ),
          button (cls := "btn btn-outline-success", tpe := "button", "go")
        ),
      ),
      // li (cls := "nav-item",
      //   dropdown (
      //     label = "Diagram",
      //     elements = List(Inheritance, CallGraph),
      //     selection = selection
      //   )
      // )

    )
  )

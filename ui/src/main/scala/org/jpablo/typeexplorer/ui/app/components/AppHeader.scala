package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.bootstrap.*
import io.laminext.core.*
import io.laminext.syntax.core.*


enum DiagramType:
  case Inheritance
  case CallGraph

import DiagramType.*

def AppHeader(diagramType: EventBus[DiagramType], projectPath: StoredString) =
  val onEnterPress  = onKeyPress.filter(_.keyCode == dom.ext.KeyCode.Enter)
  val onEscapePress = onKeyDown.filter(_.keyCode == dom.ext.KeyCode.Escape)
  val editBasePath  = Var(false)
  // a() --[a:onClick ==> editBasePath = true]--> input() --[input:onEnterPress|onEscapePress ==> editBasePath = false]--> a()
  val searchInput = 
    Search(
      cls := "me-2",
      onMountFocus,
      value <-- projectPath.signal,
      onEnterPress.preventDefault.mapToValue --> { v => 
        projectPath.set(v)
        editBasePath.set(false)
      },
      onEscapePress.mapTo(false) --> editBasePath,
    )
  // ------- render -------
  div(
    idAttr := "te-header",
    Navbar(
      id    = "te-header-content",
      brand = "Type Explorer",

      NavItem(span(cls := "nav-link", b("base path:"))),

      NavItem(
        editBasePath.signal.childWhenTrue {
          form(
            cls := "d-flex me-2",
            searchInput,
            Button(
              onClick.mapTo(false) --> { b => 
                projectPath.set(searchInput.ref.value)
                editBasePath.set(b)
              },
              "Ok"
            ).sm.outlineSuccess
          )
        },

        editBasePath.signal.childWhenFalse {
          a(
            cls := "nav-link base-path", 
            href := "#", 
            child.text <-- projectPath.signal,
            onClick.mapTo(true) --> editBasePath
          )
        }
      ),
    ),
      // li (cls := "nav-item",
      //   Dropdown (
      //     label = "Diagram",
      //     elements = List(Inheritance, CallGraph),
      //     selection = selection
      //   )
      // )

  )

package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import io.laminext.core.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.bootstrap.*
import org.scalajs.dom
import zio.prelude.fx.ZPure

enum DiagramType:
  case Inheritance
  case CallGraph


def AppHeader =
  for
    projectPath <- AppState.projectPath
  yield
    val onEnterPress  = onKeyPress.filter(_.keyCode == dom.KeyCode.Enter)
    val onEscapePress = onKeyDown.filter(_.keyCode == dom.KeyCode.Escape)
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
              ).small.outlineSuccess
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

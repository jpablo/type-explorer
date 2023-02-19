package org.jpablo.typeexplorer.ui.app.components

import com.raquo.laminar.api.L.*
import io.laminext.core.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.daisyui.*
import org.scalajs.dom

enum DiagramType:
  case Inheritance
  case CallGraph


def AppHeader(): Div =
  val onEnterPress  = onKeyPress.filter(_.keyCode == dom.KeyCode.Enter)
  val onEscapePress = onKeyDown.filter(_.keyCode == dom.KeyCode.Escape)
  val editBasePath  = Var(false)
  // a() --[a:onClick ==> editBasePath = true]--> input() --[input:onEnterPress|onEscapePress ==> editBasePath = false]--> a()
  val searchInput =
    Search(
      cls := "me-2",
      onMountFocus,
//      value <-- $projectPath.signal,
//      onEnterPress.preventDefault.mapToValue --> { v =>
//        $projectPath.set(v)
//        editBasePath.set(false)
//      },
//      onEscapePress.mapTo(false) --> editBasePath,
    )
  // ------- render -------
  div(
    cls := "border-b border-slate-300",

    Navbar(
      brand = "Type Explorer",

      NavItem(span(b("base path:"))),

      NavItem(
//        editBasePath.signal
//          .combineWith($projectPath.signal.map(_.isEmpty))
//          .map(_ || _)
//          .childWhenTrue:
//            form(
//              cls := "d-flex",
//              searchInput,
//              Button(
//                onClick.mapTo(false) --> { b =>
//                  $projectPath.set(searchInput.ref.value)
//                  editBasePath.set(b)
//                },
//                "Ok"
//              ).small.outline.success
//            ),
        editBasePath.signal.childWhenFalse:
          a(
            href := "#",
//            child.text <-- $projectPath.signal,
            onClick.mapTo(true) --> editBasePath
          ),
        NavItem(
          label(
            forId := "drawer-1",
            cls := "drawer-button bi bi-gear"
          ),
//          Button(
//            onClick.mapTo(true) --> showAppConfig,
//            cls := "bi bi-gear"
//          ).small.circle.ghost
        )
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

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


def AppHeader(basePaths: Signal[List[Path]]): Div =
  div(
    cls := "border-b border-slate-300",
    Navbar(
      brand = "Type Explorer",
      NavItem(
        span(b("base path:")),
        code(child.text <-- basePaths.map(_.mkString(", "))),
      ),
      NavItem(
        label(
          forId := "drawer-1",
          cls := "drawer-button bi bi-gear"
        )
      )
    )
  )

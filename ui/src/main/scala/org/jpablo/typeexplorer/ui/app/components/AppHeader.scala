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
  val searchModalId = "search-modal"
  div(cls := "border-b border-slate-300",
    Navbar(
      brand = "Type Explorer",

      center =
        List(
          ButtonGroup(label(cls := "btn btn-xs btn-outline", forId := searchModalId, "Search"))
        ),

      end =
        List(
          b("base path:"),
          ul(cls := "menu menu-horizontal",
            li(
              a(child.text <--
                basePaths.map: ps =>
                  ps.headOption.map(_.toString).getOrElse("None") + (if ps.size > 1 then s" (+${ps.size - 1})" else "")
              ),
              ul(children <--
                basePaths.map(_.tail.map(s => li(a(s.toString))))
              )
            )
          ),
          button(cls := "btn btn-ghost btn-sm btn-circle",
            label(forId := "drawer-1", cls := "drawer-button bi bi-gear")
          )
        )
    ),
    SearchModal(searchModalId)
  )


def SearchModal(searchModalId: String) =
  List(
    input(tpe := "checkbox", idAttr := searchModalId, cls := "modal-toggle"),
    label(forId := searchModalId, cls := "modal cursor-pointer",
      label(cls := "modal-box relative", forId := "",
        // TODO: investigate how to focus this element when the modal is opened
        input(cls := "input w-full", tpe := "search",
          tabIndex := 0,
          onMountFocus, // this doesn't work
          placeholder := "Search..."),
      )
    )
  )

package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.widgets.Dialog
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def PackagesDialog(
    appState: AppState,
    tabState: InheritanceTabState
) =
  Dialog(
    cls := "modal",
    cls.toggle("modal-open") <-- tabState.packagesDialogOpen.signal,
    div(
      cls := "modal-box",
      PackagesTreeComponent(appState, tabState),
      div(
        cls := "modal-action",
        form(
          method := "dialog",
          button(
            cls := "btn",
            "close",
            onClick --> tabState.packagesDialogOpen.set(false)
          )
        )
      )
    )
  )
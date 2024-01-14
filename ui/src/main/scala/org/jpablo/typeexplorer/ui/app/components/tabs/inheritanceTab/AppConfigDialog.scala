package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.AppConfigForm
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.widgets.Dialog
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def AppConfigDialog(appState: AppState) =
  Dialog(
    cls := "modal",
    cls.toggle("modal-open") <-- appState.appConfigDialogOpenV.signal,
    div(
      cls := "modal-box",
      AppConfigForm(appState.activeProject.project),
      div(
        cls := "modal-action",
        form(
          method := "dialog",
          button(
            cls := "btn",
            "close",
            onClick --> appState.appConfigDialogOpenV.set(false)
          )
        )
      )
    )
  )

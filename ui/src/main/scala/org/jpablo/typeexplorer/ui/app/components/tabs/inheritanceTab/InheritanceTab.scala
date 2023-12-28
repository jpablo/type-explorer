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

def InheritanceTab(
    appState: AppState,
    tabState: InheritanceTabState
): Div =
  val inheritanceSvgDiagram = tabState.svgDiagram(appState.basePaths)
  val canvasContainer = CanvasContainer(tabState, inheritanceSvgDiagram)
  val rect = canvasContainer.ref.getBoundingClientRect()
  val packagesDialog =
    PackagesDialog(appState, tabState, tabState.packagesDialogOpen)

  div(
    cls := "h-full w-full relative",
    canvasContainer,
    Toolbar(
      appState,
      tabState,
      inheritanceSvgDiagram,
      rect,
      tabState.packagesDialogOpen
    ),
    SelectionSidebar(appState, tabState, inheritanceSvgDiagram),
    packagesDialog.tag
  )

def PackagesDialog(
    appState: AppState,
    tabState: InheritanceTabState,
    open: Var[Boolean]
) =
  Dialog(
    cls := "modal",
    cls.toggle("modal-open") <-- open.signal,
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
            onClick --> open.set(false)
          )
        )
      )
    )
  )

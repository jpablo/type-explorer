package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.widgets.Dialog
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def InheritanceTab(appState: AppState)(
    tabState: InheritanceTabState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]
): ReactiveHtmlElement[HTMLDivElement] =
  val canvasContainer = CanvasContainer(tabState, inheritanceSvgDiagram)
  val rect = canvasContainer.ref.getBoundingClientRect()
  val packagesDialogOpen = Var(false)
  val packagesDialog =
    PackagesDialog(
      appState,
      tabState,
      appState.activeProject.name,
      packagesDialogOpen
    )

  div(
    cls := "h-full w-full relative",
    canvasContainer,
    Toolbar(
      appState,
      tabState,
      inheritanceSvgDiagram,
      rect,
      packagesDialogOpen
    ),
    SelectionSidebar(appState, tabState, inheritanceSvgDiagram),
    packagesDialog.tag
  )

def PackagesDialog(
    appState: AppState,
    tabState: InheritanceTabState,
    title: Var[String],
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
          button(cls := "btn", "close", onClick.mapTo(false) --> open.set)
        )
      )
    )
  )

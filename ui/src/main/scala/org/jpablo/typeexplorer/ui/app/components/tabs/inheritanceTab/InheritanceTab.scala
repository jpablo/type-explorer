package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def InheritanceTab(appState: AppState)(
    tabState: InheritanceTabState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]
): ReactiveHtmlElement[HTMLDivElement] =
  val canvasContainer = CanvasContainer(tabState, inheritanceSvgDiagram)
  val rect = canvasContainer.ref.getBoundingClientRect()
  div(
    cls := "h-full w-full relative",
    canvasContainer,
    Toolbar(appState, tabState, inheritanceSvgDiagram, rect),
    SelectionSidebar(appState, tabState, inheritanceSvgDiagram)
  )

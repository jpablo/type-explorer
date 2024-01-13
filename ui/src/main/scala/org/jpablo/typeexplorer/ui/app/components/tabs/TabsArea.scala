package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState, Page}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.*
import org.jpablo.typeexplorer.ui.domUtils.*
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def TabsArea(
    appState:  AppState,
    documents: EventStream[List[TextDocumentsWithSource]]
): Div =
  div(
    role := "tablist",
    cls  := "te-parent-3 tabs tabs-lifted mt-1",
    children <--
      appState.activeProject.pages
        .split(_.id)(renderTab(appState))
        .map(_.flatten)
  )

def renderTab(appState: AppState)(pageId: String, p: Page, pageS: Signal[Page]): Seq[Base] =
  val zoomValue = Var(1.0)
  val fitDiagram = EventBus[Unit]()
  val tabState = InheritanceTabState(appState, pageId)
  Seq(
    input(
      role := "tab",
      tpe  := "radio",
      checked <-- appState.activeProject.getActivePageId.map(pageId == _),
      cls       := "tab",
      name      := "tabs_area",
      ariaLabel := s"Inheritance",
      onClick --> appState.setActivePage(pageId)
    ),
    div(
      role := "tabpanel",
      cls  := "te-parent-2 tab-content bg-base-100 border-base-300 rounded-box",
      div(
        cls := "te-parent-1",
        CanvasContainer(tabState.inheritanceSvgDiagram, tabState.canvasSelection, zoomValue, fitDiagram.events),
        Toolbar(appState.fullGraph, tabState, zoomValue, fitDiagram),
        SelectionSidebar(appState, tabState),
        PackagesDialog(appState, tabState).tag
      )
    )
  )

package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveHtmlElement.Base
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.models.GraphSymbol
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState, Page}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.*
import org.jpablo.typeexplorer.ui.domUtils.*
import org.scalajs.dom.HTMLDivElement

def TabsArea(
    appState: AppState,
    documents: EventStream[List[TextDocumentsWithSource]]
): Div =
  div(
    role := "tablist",
    cls := "tabs tabs-lifted h-full w-full te-tabs-area",
    children <--
      appState.activeProject.pages
        .split(_.id)(renderTab(appState))
        .map(_.flatten)
  )

def renderTab(
    appState: AppState
)(pageId: String, p: Page, pageS: Signal[Page]): Seq[Base] =
  val tabState =
    InheritanceTabState(
      appState.basePaths,
      fullGraph = appState.fullGraph,
      canvasSelectionV = Var(Set.empty[GraphSymbol]),
      pageV = appState.activeProject.pageV(pageId),
      pageId = pageId
    )

  val canvasContainer =
    CanvasContainer(
      tabState.inheritanceSvgDiagram,
      tabState.canvasSelection
    )

  val rect = canvasContainer.ref.getBoundingClientRect()

  Seq(
    input(
      role := "tab",
      tpe := "radio",
      checked <-- appState.activeProject.getActivePageId.map(pageId == _),
      cls := "tab",
      name := "tabs_area",
      ariaLabel := s"Inheritance",
      onClick --> appState.setActivePage(pageId)
    ),
    div(
      role := "tabpanel",
      cls := "tab-content bg-base-100 border-base-300 rounded-box h-full",
      // --------------------
      div(
        cls := "h-full w-full relative",
        canvasContainer,
        Toolbar(appState, tabState, rect),
        SelectionSidebar(appState, tabState),
        PackagesDialog(appState, tabState).tag
      )
    )
  )

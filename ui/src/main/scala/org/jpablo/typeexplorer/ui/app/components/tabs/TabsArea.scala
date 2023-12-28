package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.{
  InheritanceSvgDiagram,
  InheritanceTab
}
import com.raquo.laminar.api.features.unitArrows
import org.jpablo.typeexplorer.ui.domUtils.*
import org.scalajs.dom.{HTMLDivElement, HTMLElement}

def TabsArea(
    appState: AppState,
    inheritanceSvgDiagram: Signal[Vector[Signal[InheritanceSvgDiagram]]],
    documents: EventStream[List[TextDocumentsWithSource]]
): Div =
  val tabs: Signal[Seq[ReactiveHtmlElement[HTMLElement]]] =
    appState.tabStates
      .combineWith(inheritanceSvgDiagram)
      .map(_ zip _)
      .map(_.map((t, d) => t.pageId -> InheritanceTab(appState)(t, d)))
      .combineWith(appState.activeProject.getActivePageId)
      .map: (tabPanels, activePageId) =>
        tabPanels // .zipWithIndex
          .flatMap { (pageId, tabPanel) =>
            renderTab(
              pageId = pageId,
              tabPanel = tabPanel,
              activePageId = activePageId,
              setActivePage = appState.setActivePage
            )
          }
  // -----------------------------
  div(
    role := "tablist",
    cls := "tabs tabs-lifted h-full w-full te-tabs-area",
    children <-- tabs
  )

def renderTab(
    pageId: String,
    tabPanel: Div,
    activePageId: String,
    setActivePage: String => Unit
): Seq[ReactiveHtmlElement[HTMLElement]] =
  Seq(
    input(
      role := "tab",
      tpe := "radio",
      checked := pageId == activePageId,
      cls := "tab",
      name := "tabs_area",
      ariaLabel := s"Inheritance",
      onClick --> setActivePage(pageId)
    ),
    div(
      role := "tabpanel",
      cls := "tab-content bg-base-100 border-base-300 rounded-box h-full",
      tabPanel
    )
  )

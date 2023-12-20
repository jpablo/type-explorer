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
import org.jpablo.typeexplorer.ui.domUtils.*
import org.scalajs.dom.HTMLDivElement

def TabsArea(
    appState: AppState,
    inheritanceSvgDiagram: Signal[Vector[Signal[InheritanceSvgDiagram]]],
    documents: EventStream[List[TextDocumentsWithSource]]
): Div =
  val tabsR: Signal[Vector[ReactiveHtmlElement[HTMLDivElement]]] =
    appState.tabStates
      .combineWith(inheritanceSvgDiagram)
      .map((states, diagrams) =>
        states.zip(diagrams).map(InheritanceTab(appState))
      )
  // -----------------------------
  val activePage = appState.activeProject.getActivePageIndex
  div(
    role := "tablist",
    cls := "tabs tabs-lifted h-full w-full te-tabs-area",
    children <--
      tabsR.combineWith(activePage).map: (tabs, activePageIndex) =>
        tabs.zipWithIndex.flatMap: (tab, tabIndex) =>
          Vector(
            input(
              role := "tab",
              tpe := "radio",
              checked := tabIndex == activePageIndex,
              dataTabIndex := tabIndex,
              cls := "tab",
              name := "tabs_area",
              ariaLabel := s"Inheritance-${tabIndex + 1}",
              onClick --> { _ =>
                appState.setActivePage(tabIndex)
              },
            ),
            div(
              role := "tabpanel",
              cls := "tab-content bg-base-100 border-base-300 rounded-box h-full",
              tab
            )
          )
  )

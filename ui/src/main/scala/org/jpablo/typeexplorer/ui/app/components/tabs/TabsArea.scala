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
  div(
    role := "tablist",
    cls := "tabs tabs-lifted h-full w-full te-tabs-area",
    children <--
      tabsR.map: tabs =>
        tabs.zipWithIndex.flatMap: (tab, i) =>
          Vector(
            input(
              role := "tab",
              tpe := "radio",
              checked := i == 0,
              cls := "tab",
              name := "tabs_area",
              ariaLabel := s"Inheritance-${i + 1}"
            ),
            div(
              role := "tabpanel",
              cls := "tab-content bg-base-100 border-base-300 rounded-box h-full",
              tab
            )
          )
  )

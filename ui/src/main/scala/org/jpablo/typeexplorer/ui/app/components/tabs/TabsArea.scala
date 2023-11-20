package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.{InheritanceSvgDiagram, InheritanceTab}
import org.jpablo.typeexplorer.ui.daisyui.*

def TabsArea(
    appState: AppState,
    inheritanceSvgDiagram: Vector[Signal[InheritanceSvgDiagram]],
    documents: EventStream[List[TextDocumentsWithSource]]
): List[Div] =
  val canvas =
    appState.tabStates.zip(inheritanceSvgDiagram).map {
      (tabState, svgDiagram) =>
        InheritanceTab(appState, tabState, svgDiagram)
    }
//  val semanticDBTabContent = SemanticDBTab(documents, appState.basePaths)
  val tabs = Tabs(List.tabulate(canvas.length)(i => s"Inheritance-${i + 1}")*)
//  val semanticDB = tabs(2)
  List(
    NavTabs(
      cls := "mt-2 -mb-px",
      tabs.map(_.NavItem)
//      appState.advancedMode.childWhenTrue(semanticDB.NavItem)
    ),
    TabContent(
      cls := "flex-1 overflow-auto border-t border-slate-300",
      tabs.zip(canvas).map((t, c) => t.Pane(c))
//      appState.advancedMode.childWhenTrue(semanticDB.Pane(semanticDBTabContent))
    )
  )

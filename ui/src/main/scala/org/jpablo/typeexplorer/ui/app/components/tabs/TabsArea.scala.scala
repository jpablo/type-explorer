package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.{
  InheritanceSvgDiagram,
  InheritanceTab
}
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.SemanticDBTab
import org.jpablo.typeexplorer.ui.daisyui.*

def TabsArea(
    state: AppState,
    $inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    $documents: EventStream[List[TextDocumentsWithSource]]
): List[Div] =
  val inheritanceCanvas = InheritanceTab(state, $inheritanceSvgDiagram)
  val semanticDBTabContent = SemanticDBTab($documents, state.basePaths)
  val tabs = Tabs("Inheritance", "SemanticDB")
  val inheritance = tabs(0)
  val semanticDB = tabs(1)
  List(
    NavTabs(
      cls := "mt-2 -mb-px",
      inheritance.NavItem,
      state.projectConfig.signal
        .map(_.advancedMode)
        .childWhenTrue(semanticDB.NavItem)
    ),
    TabContent(
      cls := "flex-1 overflow-auto border-t border-slate-300",
      inheritance.Pane(inheritanceCanvas),
      state.projectConfig.signal
        .map(_.advancedMode)
        .childWhenTrue(semanticDB.Pane(semanticDBTabContent))
    )
  )

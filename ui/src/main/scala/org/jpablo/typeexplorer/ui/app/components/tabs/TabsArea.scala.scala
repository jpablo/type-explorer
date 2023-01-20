package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.{InheritanceTab, InheritanceSvgDiagram}
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.SemanticDBTab
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.daisyui.*
import io.laminext.syntax.core.*

def TabsArea(
  appState: AppState,
  $inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
  $documents: EventStream[List[TextDocumentsWithSource]]
) =
  val inheritanceCanvas    = InheritanceTab.build(appState.inheritanceTabState, $inheritanceSvgDiagram)
  val semanticDBTabContent = SemanticDBTab($documents, appState.$projectPath)
  val tabs = Tabs("Inheritance", "SemanticDB")
  val inheritance = tabs(0)
  val semanticDB  = tabs(1)
  List(
    NavTabs(
      cls := "mt-2 -mb-px",
      inheritance.NavItem,
      appState.$devMode.signal.childWhenTrue(semanticDB.NavItem),
      div(cls :="tab tab-lifted mr-6 flex-1 cursor-default [--tab-border-color:transparent]")
    ),

    TabContent(
      cls := "flex-1 overflow-auto border-t border-slate-300",
      inheritance.Pane(inheritanceCanvas),
      appState.$devMode.signal.childWhenTrue(inheritance.Pane(semanticDBTabContent))
    )
  )



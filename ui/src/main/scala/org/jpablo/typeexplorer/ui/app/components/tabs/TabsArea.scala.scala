package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTab
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.SemanticDBTab
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.jpablo.typeexplorer.ui.daisyui.*

def TabsArea =
  for
    inheritanceCanvas    <- InheritanceTab.build
    semanticDBTabContent <- SemanticDBTab
  yield
    val tabs = Tabs("Inheritance", "SemanticDB")
    val inheritance = tabs(0)
    val semanticDB  = tabs(1)
    List(
      NavTabs(
        cls := "mt-2 -mb-px",
        inheritance.NavItem,
        semanticDB.NavItem,
        div(cls :="tab tab-lifted mr-6 flex-1 cursor-default [--tab-border-color:transparent]")
      ),

      TabContent(
        cls := "te-tabs-container overflow-auto pt-2 border-t border-slate-300",
        inheritance.Pane("inheritance-tab-pane", inheritanceCanvas),
        semanticDB.Pane("semanticdb-tab-pane", semanticDBTabContent),
      )
    )



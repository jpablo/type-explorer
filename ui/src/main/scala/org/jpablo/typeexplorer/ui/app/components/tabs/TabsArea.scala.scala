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
import org.jpablo.typeexplorer.ui.bootstrap.*

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
        cls := "te-tabs-header",
        inheritance.NavItem,
        semanticDB.NavItem,
      ),

      TabContent(
        cls := "te-tabs-container",
        inheritance.Pane("inheritance-tab-pane", inheritanceCanvas),
        semanticDB.Pane("semanticdb-tab-pane", semanticDBTabContent),
      )
    )



package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTab
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.SemanticDBTab
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbols
import org.jpablo.typeexplorer.ui.bootstrap.*

def TabsArea =
  for
    inheritanceTabContent <- InheritanceTab.build
    semanticDBTabContent <- SemanticDBTab
  yield
    val inheritance = Tab("inheritance-tab-pane", active = true)
    val semanticDB  = Tab("semanticdb-tab-pane")
    List(
      NavTabs(
        cls := "te-tabs-header",
        inheritance.NavItem("Inheritance"),
        semanticDB.NavItem("SemanticDB"),
      ),
      
      TabContent(
        cls := "te-tabs-container",
        inheritance.Pane(0, inheritanceTabContent),
        semanticDB.Pane(1, semanticDBTabContent),
      )
    )
  


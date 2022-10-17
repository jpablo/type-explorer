package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTab
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.SemanticDBTab
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbols
import org.jpablo.typeexplorer.ui.bootstrap.Tab
import com.raquo.laminar.nodes.ReactiveHtmlElement

def TabsArea(
  $projectPath    : Signal[Path], 
  $documents      : EventStream[List[TextDocumentsWithSource]],
  $inheritance    : EventStream[dom.Element],
  $classes        : EventStream[InheritanceDiagram],
  selectedSymbol  : SelectedSymbols,
) =
  val inheritance = Tab(true, "inheritance-tab-pane")
  val semanticDB  = Tab(false, "semanticdb-tab-pane")
  List(
    // Tab headers
    div(
      idAttr := "te-tabs-header",
      ul(
        cls := "nav nav-tabs",
        role := "tablist",

        inheritance.header("Inheritance"),
        semanticDB.header("SemanticDB"),
      )
    ),
    
    // Tab bodies
    div(
      cls := "te-tabs-container",
      div(
        cls := "tab-content",
        inheritance.body(0, InheritanceTab($inheritance, $classes, selectedSymbol)),
        semanticDB.body(1, SemanticDBTab($projectPath, $documents)),
      )
    )
  )
  


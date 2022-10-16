package org.jpablo.typeexplorer.ui.app.components.tabs

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.inheritanceTab
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.semanticDBTab
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbols
import com.raquo.laminar.nodes.ReactiveHtmlElement

def tabsArea(
  $projectPath    : Signal[Path], 
  $documents      : EventStream[List[TextDocumentsWithSource]],
  $inheritance    : EventStream[dom.Element],
  $classes        : EventStream[InheritanceDiagram],
  selectedSymbol  : SelectedSymbols,
  $selectedUri    : EventBus[Path]
) =
  val inheritance = Tab(false, "inheritance-tab-pane")
  val semanticDB  = Tab(true, "semanticdb-tab-pane")
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
        inheritance.body(0, inheritanceTab($inheritance, $classes, selectedSymbol)),
        semanticDB.body(1, semanticDBTab($projectPath, $documents, $selectedUri)),
      )
    )
  )
  

class Tab(active: Boolean, target: String):
  def header(title: String) =
    li(
      cls := "nav-item",
      role := "presentation",
      button(
        cls := "nav-link",
        cls := (if active then "active" else ""),
        dataAttr("bs-toggle") := "tab",
        dataAttr("bs-target") := "#" + target,
        tpe := "button",
        role := "tab",
        title
      )
    )
  
  def body(index: Int, content: ReactiveHtmlElement[dom.html.Element]) =
    div(
      idAttr := target,
      cls := "tab-pane fade",
      cls := (if active then "show active" else ""),
      role := "tabpanel",
      tabIndex := index,
      content
    )




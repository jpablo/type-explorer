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

def tabsArea(
  $projectPath    : Signal[Path], 
  $documents      : EventStream[List[TextDocumentsWithSource]],
  $inheritance    : EventStream[dom.Element],
  $classes        : EventStream[InheritanceDiagram],
  selectedSymbol  : SelectedSymbols,
  $selectedUri    : EventBus[Path]
) =
  List(
    div(
      idAttr := "te-tabs-header",
      ul(
        cls := "nav nav-tabs",
        role := "tablist",
        li(
          cls := "nav-item",
          role := "presentation",
          button(
            cls := "nav-link active",
            dataAttr("bs-toggle") := "tab",
            dataAttr("bs-target") := "#classes-tab-pane",
            tpe := "button",
            role := "tab",
            "Inheritance"
          )
        ),
        li(
          cls := "nav-item",
          a(
            cls := "nav-link",
            href := "#",
            dataAttr("bs-toggle") := "tab",
            dataAttr("bs-target") := "#semanticdb-tab-pane",
            tpe := "button",
            role := "tab",
            "SemanticDB"
          )
        )
      )
    ),
    div(
      cls := "te-tabs-container",
      div(
        cls := "tab-content",
        div(
          idAttr := "classes-tab-pane",
          cls := "tab-pane fade show active",
          role := "tabpanel",
          tabIndex := 0,
          inheritanceTab($inheritance, $classes, selectedSymbol)
        ),
        div(
          idAttr := "semanticdb-tab-pane",
          cls := "tab-pane fade",
          role := "tabpanel",
          tabIndex := 0,
          semanticDBTab($projectPath, $documents, $selectedUri)
        )
      )
    )
  )
  

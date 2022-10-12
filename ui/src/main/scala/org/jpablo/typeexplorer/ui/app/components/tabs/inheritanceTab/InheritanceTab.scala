package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTree
import org.scalajs.dom

import org.jpablo.typeexplorer.shared.models
import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models
import com.raquo.airstream.core.Observer
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models
import com.raquo.airstream.eventbus.WriteBus
import com.raquo.airstream.state.StrictSignal
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbols
import com.raquo.domtypes.generic.codecs.StringAsIsCodec

def svgToLaminar(svg: dom.Element) =
  new ChildNode[dom.Element] { val ref = svg }

def inheritanceTab(
  $svgDiagram    : EventStream[dom.Element],
  $diagrams      : EventStream[InheritanceDiagram],
  selectedSymbol : SelectedSymbols
) =
  val autocomplete = customProp("autocomplete", StringAsIsCodec)
  val $filter = Var("")
  val $filteredDiagrams = 
    $diagrams.toSignal(InheritanceDiagram.empty)
      .combineWith($filter.signal)
      .changes
      .map((event, w) => if w.isBlank then event else event.filterBySymbols(w))

  div( cls := "text-document-areas",

    form(cls := "inheritance-tree-search",
      input(
        cls := "form-control form-control-sm",
        tpe := "search",
        placeholder := "filter",
        controlled(value <-- $filter, onInput.mapToValue --> $filter)
      ),
    ),
    
    div( cls := "structure",
      children <-- InheritanceTree.build($filteredDiagrams, selectedSymbol)
    ),

    div(cls := "inheritance-container-toolbar", 
      div(
        cls := "btn-toolbar mb-3",
        role := "toolbar",
        // ariaLabel := "Toolbar with button groups",
        div(
          cls := "btn-group btn-group-sm me-2",
          role := "group",
          // ariaLabel := "First group",
          input( tpe := "checkbox", cls := "btn-check", idAttr := "1", autocomplete := "off"),
          label( cls := "btn btn-outline-primary", forId := "1", "fields"),
          
          input( tpe := "checkbox", cls := "btn-check", idAttr := "2", autocomplete := "off"),
          label( cls := "btn btn-outline-primary", forId := "2", "signatures"),

          button( tpe := "button", cls := "btn btn-outline-secondary", "fit"),
          button( tpe := "button", cls := "btn btn-outline-secondary", "zoom")
        )
      )
    ),

    div( cls := "inheritance-container",
      div(child <-- $svgDiagram.map(svgToLaminar))
    )
  )

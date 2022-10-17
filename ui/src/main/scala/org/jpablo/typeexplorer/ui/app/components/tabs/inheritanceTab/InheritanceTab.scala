package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.{Signal, EventStream, Observer}
import com.raquo.airstream.eventbus.WriteBus
import com.raquo.airstream.state.StrictSignal
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbols
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceTree
import org.jpablo.typeexplorer.ui.app.console
import org.scalajs.dom
import org.scalajs.dom.EventTarget
import com.raquo.domtypes.jsdom.defs.events.TypedTargetMouseEvent


def svgToLaminar(svg: dom.Element) =
  new ChildNode[dom.Element] { val ref = svg }


def InheritanceTab(
  $svgDiagram    : EventStream[dom.Element],
  $diagram       : EventStream[InheritanceDiagram],
  selectedSymbol : SelectedSymbols,
) =
  val autocomplete = customProp("autocomplete", StringAsIsCodec)
  val $filter = Var("")
  val modifySelection = modifyLens[Options]
  val $filteredDiagrams = 
    $diagram.toSignal(InheritanceDiagram.empty)
      .combineWith($filter.signal)
      .changes
      .map((event, w) => if w.isBlank then event else event.filterBySymbols(w))


  def ControlledCheckbox(id: String, labelStr: String, field: Options => Boolean, modifyField: PathLazyModify[Options, Boolean]) =
    List(
      input( tpe := "checkbox", cls := "btn-check", idAttr := id, autocomplete := "off",
        controlled(
          checked <-- selectedSymbol.options.signal.map(field),
          onClick.mapToChecked --> selectedSymbol.options.updater[Boolean]((options, b) => modifyField.setTo(b)(options))
        )
      ),
      label(cls := "btn btn-outline-primary", forId := id, labelStr)
    )

  // -------------- render --------------------------------
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
          ControlledCheckbox("fields-checkbox-1", "fields",     _.fields,     modifySelection(_.fields)),
          ControlledCheckbox("fields-checkbox-2", "signatures", _.signatures, modifySelection(_.signatures)),
          
          button( tpe := "button", cls := "btn btn-outline-secondary", disabled := true, "fit"),
          button( tpe := "button", cls := "btn btn-outline-secondary", disabled := true, "zoom")
        )
      )
    ),

    div( cls := "inheritance-container",
      div(
        child <-- $svgDiagram.map(svgToLaminar),
        onClick --> { e => 
          (e.target +: parents(e.target))
            .takeWhile(_.isInstanceOf[dom.SVGElement])
            .find(isNamespace)
            .map(NameSpaceElement.apply)
            .foreach(_.selectToggle)
            // TODO: emit event to update selected symbols
        }
      )
    )
  )

def parents(e: dom.Element) = 
  LazyList.unfold(e)(e => Option(e.parentNode.asInstanceOf[dom.Element]).map(e => (e, e)))

def isDiagramElement(e: dom.Element)(prefix: String) = 
  e.tagName == "g" && e.hasAttribute("id") && e.getAttribute("id").startsWith(prefix)

def isNamespace(e: dom.Element) = isDiagramElement(e)("elem_")
def isPackage(e: dom.Element) = isDiagramElement(e)("cluster_")

extension (e: dom.Element)
  def fill = e.getAttribute("fill") 
  def fill_=(c: String) = e.setAttribute("fill", c) 

case class NameSpaceElement(ref: dom.Element):
  val selectedFill = "red"
  val defaultFill = "#F1F1F1"

  lazy val id = ref.id.stripPrefix("elem_")
  lazy val symbol = Symbol(id)
  def box = 
    ref.getElementsByTagName("rect")
    .find(_.getAttribute("id") == id)

  def selectToggle =
    for box <- box do
      box.fill = 
        if box.fill == defaultFill then selectedFill else defaultFill

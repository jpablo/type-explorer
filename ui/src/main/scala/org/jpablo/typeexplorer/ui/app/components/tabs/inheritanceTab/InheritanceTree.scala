package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.softwaremill.quicklens.*
import org.scalajs.dom.html
import scalajs.js.URIUtils.encodeURIComponent

import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.fileTree.FileTree
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind, Symbol}
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbol
import org.jpablo.typeexplorer.ui.app.components.state.Selection
import org.jpablo.typeexplorer.ui.widgets.{collapsableTree, collapsable2}

object InheritanceTree:

  def build(
    $diagrams: EventStream[InheritanceDiagram],
    selectedSymbol: SelectedSymbol
  ): EventStream[List[HtmlElement]] =
    for diagram <- $diagrams yield
      for fileTree <- diagram.toFileTrees yield
        collapsableTree(fileTree)(
          renderBranch = b => span(cls := "collapsable-branch-label", b),
          renderLeaf = renderNamespace(selectedSymbol)
        )

  private def renderNamespace(selectedSymbol: SelectedSymbol)(name: String, ns: Namespace) =
    val uri = encodeURIComponent(ns.symbol.toString)
    val modifySelection = modifyLens[Selection]
    val $selection = 
      selectedSymbol.symbols.signal.map(_.getOrElse(ns.symbol, Selection()))

    def controlledCheckbox(field: Selection => Boolean, modifyField: PathLazyModify[Selection, Boolean]) = 
      input(cls := "form-check-input mt-0", tpe := "checkbox", 
        controlled(
          checked <-- $selection.map(field), 
          onClick.mapToChecked --> 
            selectedSymbol.symbols.updater[Boolean] { (symbols, b) =>
              val selection = 
                modifyField.setTo(b)(symbols.getOrElse(ns.symbol, Selection()))
              if selection.allEmpty then
                symbols - ns.symbol
              else
                symbols + (ns.symbol -> selection)
          }
        )
      )

    collapsable2(
      branchLabel =
        div(
          display := "inline",
          stereotype(ns),
          span(" "),
          a(href := "#elem_" + uri, title := ns.symbol.toString, ns.displayName),
          span(" "),
          controlledCheckbox(_.current, modifySelection(_.current)),
          span(" "),
          controlledCheckbox(_.parents, modifySelection(_.parents)),
          span(" "),
          controlledCheckbox(_.children, modifySelection(_.children)),
        ),
      contents =
        ns.methods.map(m => a(m.displayName, title := m.symbol.toString))
    )

  private def stereotype(ns: Namespace): ReactiveHtmlElement[html.Span] =
    val elem =
      ns.kind match
        case NamespaceKind.Object        => span("O", backgroundColor := "orchid")
        case NamespaceKind.PackageObject => span("P", backgroundColor := "lightblue")
        case NamespaceKind.Class         => span("C", backgroundColor := "rgb(173, 209, 178)")
        case NamespaceKind.Trait         => span("T", backgroundColor := "pink")
        case other                       => span(other.toString)
    elem.amend(
      borderRadius := "8px",
      paddingLeft  := "4px",
      paddingRight := "4px",
      fontWeight   := "bold"
    )

end InheritanceTree

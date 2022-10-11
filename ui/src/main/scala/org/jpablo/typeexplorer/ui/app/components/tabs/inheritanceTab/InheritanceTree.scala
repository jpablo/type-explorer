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
import org.jpablo.typeexplorer.ui.app.components.state.SelectedSymbols
import org.jpablo.typeexplorer.ui.app.components.state.Selection
import org.jpablo.typeexplorer.ui.widgets.{collapsableTree, collapsable2}

object InheritanceTree:

  /** Builds a collapasable tree based on the given inheritance diagram.
    * 
    * @param $diagram The diagram
    * @param selectedSymbols The checked status of each symbol
    * @return A List of trees, one for each top level package name in the diagram: e.g. ["com..., ", "java.io..."]
    */
  def build($diagram: EventStream[InheritanceDiagram], selectedSymbols: SelectedSymbols): EventStream[List[HtmlElement]] =
    for diagram <- $diagram yield
      for fileTree <- diagram.toFileTrees yield
        collapsableTree(fileTree)(
          renderBranch = b => span(cls := "collapsable-branch-label", b),
          renderLeaf = renderNamespace(selectedSymbols)
        )

  private def renderNamespace(selectedSymbols: SelectedSymbols)(name: String, ns: Namespace) =
    val uri = encodeURIComponent(ns.symbol.toString)
    val modifySelection = modifyLens[Selection]
    val $selection = 
      selectedSymbols.signal.map(_.getOrElse(ns.symbol, Selection.empty))

    def controlledCheckbox(field: Selection => Boolean, modifyField: PathLazyModify[Selection, Boolean]) = 
      input(
        cls := "form-check-input mt-0", 
        tpe := "checkbox", 
        controlled(
          checked <-- $selection.map(field), 
          onClick.mapToChecked --> 
            selectedSymbols.updater[Boolean] { (symbols, b) =>
              val selection = 
                modifyField.setTo(b)(symbols.getOrElse(ns.symbol, Selection.empty))
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

  /** The "stereotype" is an element indicating which kind of namespace we have:
    * an Object, a Class, etc.
    */
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

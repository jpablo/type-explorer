package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.fileTree.FileTree
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind, Symbol}
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.state.PackageTreeState
import org.jpablo.typeexplorer.ui.app.components.state.Selection
import org.jpablo.typeexplorer.ui.widgets.{collapsableTree, collapsable2}
import scalajs.js
import scalajs.js.URIUtils.encodeURIComponent
import org.jpablo.typeexplorer.ui.bootstrap.*
import org.scalajs.dom.html
import zio.prelude.fx.ZPure

object InheritanceTree:


  /** Builds a collapasable tree based on the given inheritance diagram.
    * 
    * @param $diagram The diagram
    * @param selectedSymbols The checked status of each symbol
    * @return A List of trees, one for each top level package name in the diagram: e.g. ["com..., ", "java.io..."]
    */
  def build =
    for
      renderNamespace <- renderNamespaceZ
    yield
      ($diagram: EventStream[InheritanceDiagram]) =>
        for diagram <- $diagram yield
          // TODO: diagram.toFileTrees can be called *before* filtering
          for fileTree <- diagram.toFileTrees yield
            collapsableTree(fileTree)(
              renderBranch = b => span(cls := "collapsable-branch-label", b),
              renderLeaf = renderNamespace(diagram)
            )

  private def renderNamespaceZ =
    for
      selectedSymbols <- AppState.selectedSymbols
    yield
      (diagram: InheritanceDiagram) => (name: String, ns: Namespace) =>
        val uri = encodeURIComponent(ns.symbol.toString)
        val modifySelection = modifyLens[Selection]
        val $selection = selectedSymbols.selection(ns.symbol)

        def controlledCheckbox(field: Selection => Boolean, modifyField: PathLazyModify[Selection, Boolean], title: String) = 
          input(
            cls := "form-check-input mt-0", 
            tpe := "checkbox", 
            dataAttr("bs-toggle")  := "tooltip",
            dataAttr("bs-trigger") := "hover",
            dataAttr("bs-title")   := title,
            onMountCallback(ctx => 
              js.Dynamic.newInstance(js.Dynamic.global.bootstrap.Tooltip)(ctx.thisNode.ref)
            ),
            controlled(
              checked <-- $selection.map(field), 
              onClick.mapToChecked --> selectedSymbols.symbolsUpdater(ns, modifyField)
            )
          )
          
        val $isSelected = $selection.map(!_.allEmpty)
        collapsable2(
          branchLabel =
            div(
              display := "inline",
              stereotype(ns),
              span(" "),
              a(cls := "inheritance-namespace-symbol", href := "#elem_" + uri, title := ns.symbol.toString, ns.displayName,
                onClick.mapTo(true) --> selectedSymbols.symbolsUpdater(ns, modifySelection(_.current))
              ),
              div( cls := "inheritance-namespace-selection hide", cls.toggle("show-inline", "hide") <-- $isSelected,
                span(" "),
                
                // miniButton("p", onClick.mapTo(true) --> symbolsUpdater(modifySelection(_.parents))),

                miniButton("p", onClick.mapTo(ns.symbol) --> selectedSymbols.enableParents(diagram)),
                
                miniButton("c", onClick.mapTo(true) --> selectedSymbols.symbolsUpdater(ns, modifySelection(_.children))),
                
                controlledCheckbox(_.current, modifySelection(_.current), "current"),
                span(" "),
                controlledCheckbox(_.parents, modifySelection(_.parents), "parents"),
                span(" "),
                controlledCheckbox(_.children, modifySelection(_.children), "children"),
              )
            ),
          contents =
            ns.methods.map(m => a(m.displayName, title := m.symbol.toString))
        )

  def miniButton(label: String, mods: Modifier[ReactiveHtmlElement[html.Element]]*) =
    span(cls := "te-mini-button", label, mods)

  /** The "stereotype" is an element indicating which kind of namespace we have:
    * an Object, a Class, etc.
    */
  private def stereotype(ns: Namespace): Span =
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

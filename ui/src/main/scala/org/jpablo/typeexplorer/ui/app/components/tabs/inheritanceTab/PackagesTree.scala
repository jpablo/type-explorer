package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind, Symbol}
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.jpablo.typeexplorer.ui.widgets.{collapsableTree, collapsable2}
import scalajs.js
import scalajs.js.URIUtils.encodeURIComponent
import org.jpablo.typeexplorer.ui.daisyui.*
import org.scalajs.dom.html
import zio.prelude.fx.ZPure

object PackagesTree:


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
          for fileTree <- diagram.toTrees yield
            collapsableTree(fileTree)(
              renderBranch = { b =>
                // renders package name
                div(
                  cls := "whitespace-nowrap inline-block w-full focus:bg-blue-100",
                  tabIndex := 0,
                  a(
                    b,
                    onClick --> { _ => println(b) }
                  )
                )
              },
              renderLeaf = renderNamespace
            )

  private def renderNamespaceZ =
    for
      inheritanceTabState <- AppState.inheritanceTabState
    yield
      (name: String, ns: Namespace) =>
        val uri = encodeURIComponent(ns.symbol.toString)
        val $isActive = inheritanceTabState.$activeSymbols.signal.map(_.contains(ns.symbol))

        collapsable2(
          branchLabel =
            div(
              idAttr := ns.symbol.toString,
              cls := "inline-block w-full focus:bg-blue-100",
              tabIndex := 0,
              span(
                cls := "inline-block w-5",
                stereotype(ns)
              ),
              a(
                cls := "p-0.5",
                cls.toggle("bg-blue-200 rounded", "noop") <-- $isActive,
                href  := "#elem_" + uri,
                title := ns.symbol.toString,
                ns.displayName,
                onClick --> { _ =>
                  inheritanceTabState.toggleActiveSymbol(ns.symbol)
                  inheritanceTabState.toggleCanvasSelection(ns.symbol)
                }
              ),
            ),
          contents =
            ns.methods.map(m => a(m.displayName, title := m.symbol.toString))
        )

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

end PackagesTree

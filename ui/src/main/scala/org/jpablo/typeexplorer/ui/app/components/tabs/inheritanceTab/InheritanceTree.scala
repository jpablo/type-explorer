package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.fileTree.FileTree
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind}
import org.jpablo.typeexplorer.ui.widgets.{collapsableTree, collapsable2}
import scalajs.js.URIUtils.encodeURIComponent
import org.scalajs.dom.html
import com.raquo.laminar.nodes.ReactiveHtmlElement

object InheritanceTree:

  def build(
    $classes: EventStream[InheritanceDiagram],
    $selectedUri: EventBus[String]
  ): EventStream[List[HtmlElement]] =
    for diagram <- $classes yield
      for fileTree <- diagram.toFileTrees yield
        collapsableTree(fileTree)(
          renderBranch = b => span(cls := "collapsable-branch-label", b),
          renderLeaf = renderNamespace($selectedUri)
        )

  private def renderNamespace($selectedUri: EventBus[String])(name: String, ns: Namespace) =
    val uri = encodeURIComponent(ns.symbol.toString)
    collapsable2(
      branchLabel =
        div(
          display := "inline",
          stereotype(ns),
          span(" "),
          a(
            href := "#elem_" + uri,
            ns.displayName, 
            title := ns.symbol.toString,
            onClick.mapTo(uri) --> $selectedUri
          )
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
        case other                       => span(other.toString)
    elem.amend(
      borderRadius := "8px",
      paddingLeft  := "4px",
      paddingRight := "4px",
      fontWeight   := "bold"
    )

end InheritanceTree

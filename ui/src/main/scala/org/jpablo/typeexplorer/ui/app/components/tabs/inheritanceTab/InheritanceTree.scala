package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.fileTree.FileTree
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind}
import org.jpablo.typeexplorer.ui.widgets.collapsableTree

object InheritanceTree:

  def build($classes: EventStream[InheritanceDiagram]): EventStream[List[HtmlElement]] =
    for diagram <- $classes yield
      for fileTree <- diagram.toFileTrees yield
        collapsableTree(fileTree)(
          renderBranch = b => span(cls := "collapsable-branch-label", b),
          renderLeaf = (_, ns) =>
            div(
              stereotype(ns),
              span(" "),
              ns.displayName + " (" + ns.symbol.toString + ")"
            )
        )

  private def stereotype(ns: Namespace) =
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

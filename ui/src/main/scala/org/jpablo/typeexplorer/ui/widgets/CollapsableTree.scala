package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.tree.Tree

def collapsableTree[A](
  t: Tree[A]
)(
  renderBranch: String => HtmlElement,
  renderLeaf  : (String, A) => HtmlElement
)
: HtmlElement = t match
  case Tree.Node(name, _, trees) =>
    collapsable2(
      branchLabel = renderBranch(name),
      contents    = trees.map(tree => collapsableTree(tree)(renderBranch, renderLeaf)),
      open        = true
    )
  case Tree.Leaf(name, data) =>
    renderLeaf(name, data)

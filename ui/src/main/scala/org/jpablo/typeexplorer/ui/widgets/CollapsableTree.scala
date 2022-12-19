package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.tree.Tree

def collapsableTree[A](
  t: Tree[A]
)(
  renderBranch: (String, List[String]) => HtmlElement,
  renderLeaf  : (String, A) => HtmlElement,
  open        : Boolean = false
)
: HtmlElement = t match
  case Tree.Node(name, path, trees) =>
    collapsable2(
      branchLabel = renderBranch(name, path),
      contents    = trees.map(tree => collapsableTree(tree)(renderBranch, renderLeaf, open)),
      open        = open
    )
  case Tree.Leaf(name, data) =>
    renderLeaf(name, data)

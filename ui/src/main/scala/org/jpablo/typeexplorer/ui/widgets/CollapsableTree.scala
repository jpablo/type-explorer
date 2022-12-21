package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.tree.Tree

def CollapsableTree[A](
  t: Tree[A]
)(
  renderBranch: (String, List[String]) => HtmlElement,
  renderLeaf  : (String, A) => HtmlElement,
  open        : Boolean = false
)
: HtmlElement = t match
  case Tree.Node(label, path, children) =>
    Collapsable(
      branchLabel = renderBranch(label, path),
      contents    = children.map(tree => CollapsableTree(tree)(renderBranch, renderLeaf, open)),
      open        = open
    )
  case Tree.Leaf(label, data) =>
    renderLeaf(label, data)

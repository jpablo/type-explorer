package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.fileTree.FileTree

def collapsableTree[A](
  t: FileTree[A]
)(
  renderBranch: String => HtmlElement,
  renderLeaf  : (String, A) => HtmlElement
)
: HtmlElement = t match
  case FileTree.Directory(name, trees) =>
    collapsable2(
      branchLabel = renderBranch(name),
      contents    = trees.map(tree => collapsableTree(tree)(renderBranch, renderLeaf)),
      open        = true
    )
  case FileTree.File(name, data) =>
    renderLeaf(name, data)

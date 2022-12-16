package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom

class InheritanceSvgDiagram(svg: dom.SVGElement):

  svg.classList.add("bg-orange-100")

  // Remove inline style so we can use our own style
  svg.setStyle("background", "")

  // set a solid fill color for clusters
  for
    elem <- clusters
    box <- elem.box
  do
    box.setAttribute("fill", "white")


  def elements =
    svg.querySelectorAll(NamespaceElement.selector)
      .flatMap(NamespaceElement.from)

  def clusterElements(cluster: ClusterElement) =
    svg.querySelectorAll(NamespaceElement.selector)
      .flatMap(NamespaceElement.from)
      // hack: cluster id can't contain '/' so it has '.' for now
      // (plantUML limitation)
      .filter(_.id.startsWith(cluster.id.replace('.', '/')))

  def clusters =
    svg.querySelectorAll(ClusterElement.selector)
      .flatMap(ClusterElement.from)

  def elementSymbols: Set[models.Symbol] =
    elements.map(_.symbol).toSet

  def select(symbols: Set[models.Symbol]) =
    for elem <- elements if symbols.contains(elem.symbol) do
      elem.select()

  def unselectAll() =
    elements.foreach(_.unselect())

  def toLaminar =
    new ChildNode[dom.SVGElement] { val ref = svg }


object InheritanceSvgDiagram:
  val empty = InheritanceSvgDiagram(svg.svg().ref)


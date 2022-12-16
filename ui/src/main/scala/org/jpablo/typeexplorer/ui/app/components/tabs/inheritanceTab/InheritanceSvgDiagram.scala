package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom

class InheritanceSvgDiagram(svg: dom.SVGElement):
  // Remove inline style so we can use our own style
  svg.setStyle("background", "")
  svg.classList.add("bg-orange-100")
  // set a solid fill color for clusters
  for
    elem <- clusters
    box <- elem.box
  do
    box.setAttribute("fill", "white")

  def elements =
    NamespaceElement.selectAll(svg)

  def clusterElements(cluster: ClusterElement) =
    elements.filter(_.id.startsWith(cluster.idWithSlashes))

  def clusters =
    ClusterElement.selectAll(svg)

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


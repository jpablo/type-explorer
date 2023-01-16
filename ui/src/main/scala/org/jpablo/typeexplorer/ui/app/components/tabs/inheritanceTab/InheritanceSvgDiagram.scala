package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom

class InheritanceSvgDiagram(svg: dom.SVGElement):
  svg.removeStyle("background")
  // (more styles are set in style.scss)

  private def setDimensions(w: Int, h: Int) =
    svg.setStyle("width",  s"${w}px")
    svg.setStyle("height", s"${h}px")

  private def width = svg.getBoundingClientRect().width
  private def height = svg.getBoundingClientRect().height

  def zoom(r: Double) =
    setDimensions((width * r).toInt, (height * r).toInt)

  def fitToRect(rect: dom.DOMRect) =
    zoom(scala.math.min(rect.width / width, rect.height / height))


  private def elements =
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


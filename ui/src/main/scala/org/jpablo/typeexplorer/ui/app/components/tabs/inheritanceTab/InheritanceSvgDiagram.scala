package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom

class InheritanceSvgDiagram(svg: dom.SVGElement):
  svg.removeStyle("background")
  // (more styles are set in style.scss)

  private def setDimensions(w: Int, h: Int): Unit =
    svg.setStyle("width" -> s"${w}px", "height" -> s"${h}px")

  private def width = svg.getBoundingClientRect().width
  private def height = svg.getBoundingClientRect().height

  def zoom(r: Double): Unit =
    setDimensions((width * r).toInt, (height * r).toInt)

  def fitToRect(rect: dom.DOMRect): Unit =
    zoom(scala.math.min(rect.width / width, rect.height / height))

  private def selectableElements =
    NamespaceElement.selectAll(svg) ++ LinkElement.selectAll(svg)

  private def namespaceElements =
    NamespaceElement.selectAll(svg)

  def clusterElements(cluster: ClusterElement) =
    namespaceElements.filter(_.id.startsWith(cluster.idWithSlashes))

  def clusters =
    ClusterElement.selectAll(svg)

  def elementSymbols: Set[models.Symbol] =
    namespaceElements.map(_.symbol).toSet

  def select(symbols: Set[models.Symbol]): Unit =
    for elem <- selectableElements if symbols.contains(elem.symbol) do
      elem.select()

  def unselectAll(): Unit =
    selectableElements.foreach(_.unselect())

  def toLaminar =
    foreignSvgElement(svg)

  def toSVG: String =
    svg.outerHTML


object InheritanceSvgDiagram:
  val empty = InheritanceSvgDiagram(svg.svg().ref)


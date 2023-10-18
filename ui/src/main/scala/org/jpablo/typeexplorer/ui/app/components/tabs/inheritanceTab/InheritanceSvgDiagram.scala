package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.svgGroupElement.{
  ClusterElement,
  LinkElement,
  NamespaceElement,
  removeStyle,
  updateStyle
}
import org.scalajs.dom
import com.raquo.laminar.DomApi

class InheritanceSvgDiagram(svgElement: dom.SVGElement):
  export svgElement.querySelector

  svgElement.removeStyle("background")
  // (more styles are set in style.scss)

  private def setDimensions(w: Int, h: Int): Unit =
    svgElement.updateStyle("width" -> s"${w}px", "height" -> s"${h}px")

  private def width = svgElement.getBoundingClientRect().width
  private def height = svgElement.getBoundingClientRect().height

  def zoom(r: Double): Unit =
    setDimensions((width * r).toInt, (height * r).toInt)

  def fitToRect(rect: dom.DOMRect): Unit =
    zoom(scala.math.min(rect.width / width, rect.height / height))

  private def selectableElements =
    NamespaceElement.selectAll(svgElement) ++ LinkElement.selectAll(svgElement)

  private def namespaceElements =
    NamespaceElement.selectAll(svgElement)

  def clusterElements(cluster: ClusterElement) =
    namespaceElements.filter(_.id.startsWith(cluster.idWithSlashes))

  def clusters =
    ClusterElement.selectAll(svgElement)

  def elementSymbols: Set[models.Symbol] =
    namespaceElements.map(_.symbol).toSet

  def select(symbols: Set[models.Symbol]): Unit =
    for elem <- selectableElements if symbols.contains(elem.symbol) do
      elem.select()

  def unselectAll(): Unit =
    selectableElements.foreach(_.unselect())

  def toLaminar =
    foreignSvgElement(svgElement)

  def toSVGText: String =
    svgElement.outerHTML

  def toSVGText(id: String): String =
    val el = getElementById("elem_" + id).asInstanceOf[dom.SVGSVGElement]
    val t = el.outerHTML
    val e = DomApi.unsafeParseSvgString(t)
    val bbox = el.getBBox()
    val s = svg.svg(
      svg.xmlns := "http://www.w3.org/2000/svg",
      svg.viewBox := s"${bbox.x} ${bbox.y} ${bbox.width} ${bbox.height}",
      foreignSvgElement(e)
    )
    s.ref.outerHTML

  def getElementById(id: String): dom.Element =
    svgElement.querySelector(s"[id='$id']")

object InheritanceSvgDiagram:
  val empty = InheritanceSvgDiagram(svg.svg().ref)

package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import scala.util.matching.Regex

class InheritanceSvgDiagram(svg: dom.SVGElement):

  svg.classList.add("bg-orange-100")

  // Remove inline style so we can use our own style
  svg.setStyle("background", "")

  // set a solid fill color for clusters
  for
    elem <- clusters
    box <- elem.box
  do
    box.setStyle("fill", "white")


  def elements =
    svg.querySelectorAll("g[id ^= elem_]")
      .map(el => NamespaceElement(el.asInstanceOf[dom.SVGGElement]))

  def clusters =
    svg.querySelectorAll("g[id ^= cluster_]")
      .map(el => ClusterElement(el.asInstanceOf[dom.SVGGElement]))

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



extension (e: dom.Element)
  def parents =
    LazyList.unfold(e)(e => Option(e.parentNode.asInstanceOf[dom.Element]).map(e => (e, e)))

  def isDiagramElement(prefix: String) =
    e.tagName == "g" && e.hasAttribute("id") && e.getAttribute("id").startsWith(prefix)

  def isNamespace = e.isDiagramElement("elem_")
  def isPackage = e.isDiagramElement("cluster_")

  def fill = e.getAttribute("fill")
  def fill_=(c: String) = e.setAttribute("fill", c)


  private def stylePattern(styleName: String): Regex =
    s"$styleName:([^;]*;)".r

  def setStyle(styleName: String, styleValue: String): Unit =
    val style: String | Null = e.getAttribute("style")
    val pair = s"$styleName:$styleValue;"
    e.setAttribute("style",
      if style != null then
        if style.contains(styleName + ":") then
          style.replaceFirst(stylePattern(styleName).regex, pair)
        else
          style + ":" + pair
      else
        pair
    )

  def removeStyle(styleName: String): Unit =
    if getStyle(styleName).isDefined then
      setStyle(styleName, "")

  def getStyle(styleName: String): Option[String] =
    for
      style <- Option(e.getAttribute("style"))
      rMatch <- stylePattern(styleName).findFirstMatchIn(style)
    yield
      rMatch.group(1)

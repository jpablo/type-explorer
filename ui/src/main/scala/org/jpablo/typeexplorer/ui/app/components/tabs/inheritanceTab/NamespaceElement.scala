package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import org.scalajs.dom.console
import scala.util.matching.Regex


sealed trait SvgGroupElement(val ref: dom.SVGGElement):
  def prefix : String
  def box: Option[dom.SVGElement]

  private val selectStyle = "3px solid rgb(245 158 11)"
  val id = ref.id.stripPrefix(prefix)
  val symbol = models.Symbol(id)

  def select() =
    ref.setStyle("outline", selectStyle)

  def unselect() =
    ref.removeStyle("outline")

  def selectToggle() =
    ref.getStyle("outline") match
      case Some(value) =>
        if value == selectStyle then unselect() else select()
      case None => select()


class NamespaceElement(ref: dom.SVGGElement) extends SvgGroupElement(ref):
  def prefix = NamespaceElement.prefix
  private val boxTagName = "rect"

  def box: Option[dom.SVGElement] =
    ref.getElementsByTagName(boxTagName)
      .find(_.getAttribute("id") == id)
      .map(_.asInstanceOf[dom.SVGElement])

object NamespaceElement:
  val prefix = "elem_"
  val selector = s"g[id ^= $prefix]"

  def from(e: dom.Element): Option[NamespaceElement] =
    if e.isNamespace then
      Some(NamespaceElement(e.asInstanceOf[dom.SVGGElement]))
    else None


class ClusterElement(ref: dom.SVGGElement) extends SvgGroupElement(ref):
  def prefix = ClusterElement.prefix
  private val boxTagName = "path"

  def box: Option[dom.SVGElement] =
    ref.getElementsByTagName(boxTagName)
      .headOption
      .map(_.asInstanceOf[dom.SVGElement])


object ClusterElement:
  val prefix = "cluster_"
  val selector = s"g[id ^= $prefix]"

  def from(e: dom.Element): Option[ClusterElement] =
    if e.isPackage then
      Some(ClusterElement(e.asInstanceOf[dom.SVGGElement]))
    else
      None


extension (e: dom.Element)
  def path =
    e +: LazyList.unfold(e)(e => Option(e.parentNode.asInstanceOf[dom.Element]).map(e => (e, e)))

  def isDiagramElement(prefix: String) =
    e.tagName == "g" && e.hasAttribute("id") && e.getAttribute("id").startsWith(prefix)

  def isNamespace = e.isDiagramElement(NamespaceElement.prefix)
  def isPackage = e.isDiagramElement(ClusterElement.prefix)

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

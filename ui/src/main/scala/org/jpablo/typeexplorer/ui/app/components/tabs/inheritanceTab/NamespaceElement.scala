package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import org.scalajs.dom.console

trait SvgGroupElement(val ref: dom.SVGGElement):
  lazy val prefix : String
  private val selectStyle = "3px solid rgb(245 158 11)"
  lazy val id = ref.id.stripPrefix(prefix)
  lazy val symbol = models.Symbol(id)

  def box: Option[dom.SVGElement]

  def select() = {
    ref.setStyle("outline", selectStyle)
  }

  def unselect() =
    ref.removeStyle("outline")

  def selectToggle() =
    ref.getStyle("outline") match
      case Some(value) =>
        if value == selectStyle then unselect() else select()
      case None => select()


class NamespaceElement(ref: dom.SVGGElement) extends SvgGroupElement(ref):
  lazy val prefix = NamespaceElement.prefix
  private val boxTagName = "rect"

  def box: Option[dom.SVGElement] =
    ref.getElementsByTagName(boxTagName)
      .find(_.getAttribute("id") == id)
      .map(_.asInstanceOf[dom.SVGElement])

object NamespaceElement:
  val prefix = "elem_"
  val selector = s"g[id ^= $prefix]"


class ClusterElement(ref: dom.SVGGElement) extends SvgGroupElement(ref):
  lazy val prefix = ClusterElement.prefix
  private val boxTagName = "path"

  def box: Option[dom.SVGElement] =
    ref.getElementsByTagName(boxTagName)
      .headOption
      .map(_.asInstanceOf[dom.SVGElement])

object ClusterElement:
  val prefix = "cluster_"
  val selector = s"g[id ^= $prefix]"





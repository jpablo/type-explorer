package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import org.scalajs.dom.console

trait SvgGroupElement(val ref: dom.SVGGElement):
  lazy val selectedFill : String
  lazy val defaultFill  : String
  lazy val hoverClass   : String
  lazy val prefix       : String
  lazy val boxTagName   : String
  val selectStyle = "3px solid rgb(245 158 11)"

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
  lazy val selectedFill : String = "rgb(254 202 202)"
  lazy val defaultFill  : String = "#F1F1F1"
  lazy val hoverClass   : String = "hover:fill-blue-200"
  lazy val prefix       : String = "elem_"
  lazy val boxTagName   : String = "rect"

  def box: Option[dom.SVGElement] =
    ref.getElementsByTagName(boxTagName)
      .find(_.getAttribute("id") == id)
      .map(_.asInstanceOf[dom.SVGElement])


class ClusterElement(ref: dom.SVGGElement) extends SvgGroupElement(ref):
  lazy val selectedFill : String = "yellow"
  lazy val defaultFill  : String = "none"
  lazy val hoverClass   : String = "hover:fill-blue-200"
  lazy val prefix       : String = "cluster_"
  lazy val boxTagName   : String = "path"

  def box: Option[dom.SVGElement] =
    ref.getElementsByTagName(boxTagName)
      .headOption
      .map(_.asInstanceOf[dom.SVGElement])





package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ChildNode
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import org.scalajs.dom.console

class InheritanceSvgDiagram(svg: dom.SVGElement):

  def elements =
    svg.querySelectorAll("g[id ^= elem_]")
      .map(el => NameSpaceElement(el.asInstanceOf[dom.SVGGElement]))

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


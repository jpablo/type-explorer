package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import org.scalajs.dom.Element

class NameSpaceElement(ref: dom.Element):
  private val selectedFill = "yellow"
  private val defaultFill = "#F1F1F1"

  lazy val id = ref.id.stripPrefix("elem_")

  lazy val symbol = models.Symbol(id)

  private def box: Option[Element] =
    ref.getElementsByTagName("rect")
      .find(_.getAttribute("id") == id)

  def select() =
    box.foreach(_.fill = selectedFill)

  def unselect() =
    box.foreach(_.fill = defaultFill)

  def selectToggle() =
    for box <- box do
      box.fill =
        if box.fill == defaultFill then selectedFill else defaultFill

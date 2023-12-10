package org.jpablo.typeexplorer.ui.widgets

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.tags.HtmlTag
import org.scalajs.dom

object Icons:

  def chevron(
    $isOpen: Signal[Boolean],
    mods   : Modifier[Anchor]*
  ) =
    a(
      cls := "bi inline-block w-5",
      cls <-- $isOpen.map(o => if o then "bi-chevron-down" else "bi-chevron-right"),
    ).amend(mods)

  extension (tag: HtmlTag[dom.HTMLElement])
    def fileBinaryIcon = tag(cls := "bi bi-file-binary")
    def fileCodeIcon = tag(cls := "bi bi-file-code")
    def folderIcon = tag(cls := "bi bi-folder")
    def closeIcon = tag(cls := "bi bi-x-circle")
    def listIcon = tag(cls := "bi bi-list")
    def plusCircleIcon = tag(cls := "bi bi-plus-circle")
    def folderPlusIcon = tag(cls := "bi bi-folder-plus")
    def barChartSteps = tag(cls := "bi bi-bar-chart-steps")
    def boxes = tag(cls := "bi bi-boxes")




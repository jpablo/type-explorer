package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.domUtils.dialog
import org.scalajs.dom.HTMLDialogElement

case class Dialog(mods: Modifier[ReactiveHtmlElement.Base]*):
  val tag: ReactiveHtmlElement[HTMLDialogElement] =
    dialog(mods)
  export tag.ref, tag.ref.showModal

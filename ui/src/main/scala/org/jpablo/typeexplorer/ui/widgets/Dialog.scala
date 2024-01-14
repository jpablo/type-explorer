package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.domUtils.dialog
import org.scalajs.dom.HTMLDialogElement
import com.raquo.laminar.api.features.unitArrows

def SimpleDialog(open: Var[Boolean], contents: Modifier[ReactiveHtmlElement.Base]*) =
  Dialog(
    mods = cls.toggle("modal-open") <-- open.signal
  )(
    contents
  )(
    action = button(cls := "btn", "close", onClick --> open.set(false))
  )

def Dialog(
    mods: Modifier[ReactiveHtmlElement.Base]*
)(contents: Modifier[ReactiveHtmlElement.Base]*)(action: Modifier[ReactiveHtmlElement.Base]*) =
  dialog(
    cls := "modal",
    mods,
    div(
      cls := "modal-box",
      contents,
      div(cls := "modal-action", form(method := "dialog", action))
    )
  )

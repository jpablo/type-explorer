package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement


def Button(mods: Modifier[ReactiveHtmlElement.Base]*): Button =
  button(cls := "btn", mods)


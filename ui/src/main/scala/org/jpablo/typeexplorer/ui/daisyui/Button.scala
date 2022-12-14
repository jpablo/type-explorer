package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.html
import scala.annotation.targetName

type ReactiveElement = ReactiveHtmlElement[html.Element]

def Button(mods: Modifier[ReactiveElement]*): Button =
  button(cls := "btn", mods)


object ButtonGroup:
  opaque type ButtonGroup <: Div = Div

  def apply(mods: Modifier[ReactiveElement]*): ButtonGroup =
    div(cls := "btn-group flex-wrap content-center", mods)


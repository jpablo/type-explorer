package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.html
import scala.annotation.targetName

type ReactiveElement = ReactiveHtmlElement[html.Element]

def Button(mods: Modifier[ReactiveElement]*): Button =
  button(tpe := "button", cls := "btn", mods)


object ButtonGroup:
  opaque type ButtonGroup <: Div = Div

  def apply(mods: Modifier[ReactiveElement]*): ButtonGroup =
    div(cls := "btn-group", role := "group", mods)
    
  extension (btnGroup: ButtonGroup)
    def small: ButtonGroup = 
      btnGroup.amend(cls := "btn-group-sm")


def ButtonToolbar(mods: Modifier[ReactiveElement]*): Div =
  div(cls := "btn-toolbar", role := "toolbar", mods)



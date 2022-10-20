package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

def Button(mods: Modifier[ReactiveHtmlElement.Base]*): Button =
  button(tpe := "button", cls := "btn", mods)

extension (btn: Button)
  def outlineSecondary: Button = btn.amend(cls := "btn-outline-secondary")
  def outlineSuccess  : Button = btn.amend(cls := "btn-outline-success")
  def sm              : Button = btn.amend(cls := "btn-sm")    


object ButtonGroup:
  opaque type ButtonGroup <: Div = Div

  def apply(mods: Modifier[ReactiveHtmlElement.Base]*): ButtonGroup =
    div(cls := "btn-group", role := "group", mods)

  extension (btnGroup: ButtonGroup)
    def sm: ButtonGroup = 
      btnGroup.amend(cls := "btn-group-sm")


def ButtonToolbar(mods: Modifier[ReactiveHtmlElement.Base]*): Div =
  div(cls := "btn-toolbar", role := "toolbar", mods)
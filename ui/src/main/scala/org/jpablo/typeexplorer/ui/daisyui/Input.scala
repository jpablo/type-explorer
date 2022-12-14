package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement


def Checkbox(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "checkbox", cls := "toggle", mods)

def Search(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "search", cls := "input input-bordered input-xs input-primary w-full max-w-xs", mods)


package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.L.svg as S
import org.scalajs.dom
import io.laminext.core.*
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement

def Navbar(brand: String, items: Li*): Div =
  div(
    cls := "navbar bg-base-100",
    div(
      cls := "flex-1",
      a(cls := "btn btn-ghost normal-case text-xl", brand)
    ),
    div(
      cls := "flex-none",
      ul(
        cls := "menu menu-compact menu-horizontal",
        items
      )
    )
  )

def NavItem(mods: Modifier[ReactiveHtmlElement.Base]*) =
  li(mods)

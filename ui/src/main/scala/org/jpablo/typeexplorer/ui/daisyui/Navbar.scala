package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.{ReactiveElement, ReactiveHtmlElement}

def Navbar(brand: String, items: ReactiveElement.Base*): Div =
  div(
    cls := "navbar bg-base-100",
    div(
      cls := "flex-1",
      a(cls := "btn btn-ghost normal-case text-xl", brand)
    ),
    div(
      cls := "flex-none gap-3",
      items
//      ul(
//        cls := "menu menu-horizontal",
//      )
    )
  )

def NavItem(mods: Modifier[ReactiveHtmlElement.Base]*) =
  div(mods)

package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.L.svg as S
import org.scalajs.dom
import io.laminext.core.*
import com.raquo.laminar.nodes.ReactiveHtmlElement

type Mods = Modifier[ReactiveHtmlElement.Base]

def Navbar(brand: String, center: List[Mods], end: List[Mods]): Div =
  div(cls := "navbar bg-base-100",

    div(cls := "navbar-start",
      a(cls := "btn btn-ghost normal-case text-xl", brand)
    ),

    div(cls := "navbar-center").amend(center*),
    div(cls := "navbar-end").amend(end*)
  )

def NavItem(mods: Mods*) =
  li(mods)


package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*

def Drawer(
    id:      String,
    content: Div => Div = identity,
    sidebar: Div => Div = identity
) =
  div(
    cls := "drawer drawer-end",
    input(idAttr := id, tpe := "checkbox", cls := "drawer-toggle"),
    content(div(cls := "drawer-content")),
    sidebar(
      div(
        cls := "drawer-side z-20 overflow-hidden",
        label(cls := "drawer-overlay", forId := id)
      )
    )
  )

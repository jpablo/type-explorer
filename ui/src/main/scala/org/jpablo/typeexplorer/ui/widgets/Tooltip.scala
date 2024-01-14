package org.jpablo.typeexplorer.ui.widgets

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.daisyui.ReactiveElement
import org.jpablo.typeexplorer.ui.domUtils.dataTip

def Tooltip(text: String, mods: Modifier[ReactiveElement]*) =
  div(
    cls := "flex-none tooltip tooltip-bottom",
    dataTip := text,
    mods
  )


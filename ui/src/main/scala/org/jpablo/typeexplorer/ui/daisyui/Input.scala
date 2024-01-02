package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.domUtils.autocomplete

def Checkbox(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "checkbox", cls := "checkbox", mods)

def Search(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(
    tpe := "search",
    cls := "input input-bordered input-xs input-primary w-full",
    mods
  )

def LabeledCheckbox(
    id: String,
    labelStr: String,
    isChecked: Signal[Boolean],
    isDisabled: Signal[Boolean] = Signal.fromValue(false),
    clickHandler: Observer[Boolean],
    toggle: Boolean = false
) =
  div(
    cls := "_form-control",
    label(
      forId := id,
      cls := "label cursor-pointer",
      span(cls := "label-text pr-1", labelStr),
      input(
        idAttr := id,
        autocomplete := "off",
        tpe := "checkbox",
        disabled <-- isDisabled,
        cls := (if toggle then "toggle toggle-xs" else "checkbox checkbox-xs"),
        controlled(
          checked <-- isChecked,
          onClick.mapToChecked --> clickHandler
        )
      )
    )
  )

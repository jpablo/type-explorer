package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.domtypes.generic.codecs.StringAsIsCodec

def Checkbox(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "checkbox", cls := "toggle", mods)

def Search(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "search", cls := "input input-bordered input-xs input-primary w-full max-w-xs", mods)

private val autocomplete = customProp("autocomplete", StringAsIsCodec)

def LabeledCheckbox(id: String, labelStr: String, $checked: Signal[Boolean], clickHandler: Observer[Boolean]) =
  div(cls := "form-control",
    label(forId := id, cls := "label cursor-pointer",
      span(cls := "label-text pr-1", labelStr),
      Checkbox(idAttr := id, autocomplete := "off", cls := "toggle-xs",
        controlled(
          checked <-- $checked,
          onClick.mapToChecked --> clickHandler
        )
      ),
    ),
  )

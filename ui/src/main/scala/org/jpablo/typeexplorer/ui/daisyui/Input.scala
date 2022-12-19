package org.jpablo.typeexplorer.ui.daisyui

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.domtypes.generic.codecs.StringAsIsCodec


def Checkbox(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "checkbox", cls := "checkbox", mods)

def Search(mods: Modifier[ReactiveHtmlElement.Base]*): Input =
  input(tpe := "search", cls := "input input-bordered input-xs input-primary w-full max-w-xs", mods)

private val autocomplete = customProp("autocomplete", StringAsIsCodec)

def LabeledCheckbox(id: String, labelStr: String, $checked: Signal[Boolean], clickHandler: Observer[Boolean], toggle: Boolean = false) =
  div(cls := "form-control",
    label(forId := id, cls := "label cursor-pointer",
      span(cls := "label-text pr-1", labelStr),
      input(idAttr := id, autocomplete := "off",
        tpe := "checkbox",
        cls := (if toggle then "toggle toggle-xs" else "checkbox checkbox-xs"),
        controlled(
          checked <-- $checked,
          onClick.mapToChecked --> clickHandler
        )
      ),
    ),
  )

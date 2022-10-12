package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import io.laminext.core.*
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.generic.codecs.BooleanAsAttrPresenceCodec
import com.raquo.domtypes.generic.codecs.BooleanAsIsCodec

def navbar(id: String, brand: String, projectPath: StoredString, items: Li*): Element =
  div(
    idAttr := id,
    nav(
      cls := "navbar navbar-expand-lg bg-light",
      div(
        cls :="container-fluid",
        a(cls :="navbar-brand", brand),
        button(
          cls := "navbar-toggler",
          tpe := "button",
          dataAttr("bs-toggle") := "collapse",
          dataAttr("bs-target") := "navbarSupportedContent",
        ),
        div(
          cls := "collapse navbar-collapse",
          idAttr := "navbarSupportedContent",
          ul(cls := "navbar-nav",
            items
          )
        )
      )
    )
  )

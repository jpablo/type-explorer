package org.jpablo.typeexplorer.ui.bootstrap

import com.raquo.laminar.api.L.*
import org.scalajs.dom
import io.laminext.core.*

def navbar(id: String, brand: String, projectPath: StoredString, items: Div*): Element =
  val onEnterPress = onKeyPress.filter(_.keyCode == dom.ext.KeyCode.Enter)
  div(
    idAttr := id,

    nav (
      cls := "navbar navbar-expand-lg bg-light",
      div (
        cls :="container-fluid",
        a (cls :="navbar-brand", brand),
        button (
            cls := "navbar-toggler",
            tpe := "button",
            dataAttr("bs-toggle") := "collapse",
            dataAttr("bs-target") := "navbarSupportedContent",
          ),
        div (
          cls := "collapse navbar-collapse",
          idAttr := "navbarSupportedContent",
          form (cls := "d-flex me-2",
            input (
              cls := "form-control me-2",
              tpe := "search",
              onEnterPress.preventDefault.mapToValue --> projectPath.set,
              value <-- projectPath.signal
            ),
            button (cls := "btn btn-outline-success", tpe := "button", "go")
          ),
          ul (cls := "navbar-nav",
            li (cls := "nav-item",
              items,
            )
          )
        )
      )
    )
  )

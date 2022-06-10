package bootstrap

import com.raquo.laminar.api.L.*
import org.scalajs.dom

def navbar(id: String, brand: String, projectPath: Var[String], items: Div*): Element =
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
              value := "/Users/jpablo/proyectos/playground/type-explorer",
              onEnterPress.preventDefault.mapToValue --> projectPath
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


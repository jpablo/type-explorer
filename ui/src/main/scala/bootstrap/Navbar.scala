package bootstrap

import com.raquo.laminar.api.L.*

def navbar(id: String, brand: String, items: Div*) =
  nav (cls := "navbar navbar-expand-lg bg-light",
    idAttr := id,
    div (cls :="container-fluid",
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
            value := "/path/to/my/project"
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


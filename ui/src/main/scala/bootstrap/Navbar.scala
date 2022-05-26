package bootstrap

import com.raquo.laminar.api.L.{*, given}

def navbar(id: String, brand: String, items: Div*) =
  nav(idAttr := id, cls := "navbar navbar-expand-lg bg-light",
    div(cls :="container-fluid",
      a(cls :="navbar-brand", brand),
      ul(cls := "navbar-nav",
        li(cls := "nav-item",
          items,
        )
      )
    )
  )


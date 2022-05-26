package bootstrap

import com.raquo.laminar.api.L.{*, given}

def dropdown(label: String, elements: List[String], selectionBus: EventBus[String]): Div =
  div (cls := "dropdown",
    button (
      cls := "btn btn-primary dropdown-toggle",
      tpe := "button",
      dataAttr("bs-toggle") := "dropdown",
      label
    ),
    ul (cls := "dropdown-menu",
      for elem <- elements yield
        li (cls := "dropdown-item", href := "#",
          elem,
          onClick.map(_ => elem) --> selectionBus
        )
    )
  )


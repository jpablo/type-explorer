package bootstrap

import com.raquo.laminar.api.L.{*, given}

def dropdown[A](label: String, elements: List[A], selectionBus: EventBus[A]): Div =
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
          elem.toString,
          onClick.map(_ => elem) --> selectionBus
        )
    )
  )


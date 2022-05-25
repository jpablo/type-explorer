package app

import app.LeftColumn.leftColumn
import org.scalajs.dom
import com.raquo.laminar.api.L.*


object Layout {

  def container =
    div(cls := "container",

      div(cls := "row",
        leftColumn(),
        div(cls := "col-6",
          p("Col 2")
        ),
        div(cls := "col",
          p("Col 3")
        )
      )
    )
}

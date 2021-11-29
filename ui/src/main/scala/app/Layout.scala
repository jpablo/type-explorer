package app

import org.scalajs.dom
import com.raquo.laminar.api.L.*


object Layout {

  val fileBus = new EventBus[String]


  fileBus.events.map { e =>
    println(e)
  }

  val container =
    div(cls := "container",

      div(cls := "row",
        div(cls := "col",
          p("Col 1")
        ),

        div(cls := "col-6",
          input(tpe := "file",
            onChange.mapToValue --> fileBus,
            p("hello")
          ),
          hr(),
          div(child.text <-- fileBus)
        ),
        div(cls := "col",
          p("Col 3")
        )
      )
    )
}

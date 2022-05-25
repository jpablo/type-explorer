package app

import com.raquo.laminar.api.L.*

object CenterColumn {

  def centerColumn =
    div(idAttr := "te-center-column", cls := "col-6",
      p("Col 2")
    )


}

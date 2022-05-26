package app

import com.raquo.laminar.api.L.*

object CenterColumn {

  def centerColumn(newDiagramStream: EventStream[String]) =
    div(idAttr := "te-center-column", cls := "col-6",
      p(child.text <-- newDiagramStream)
    )


}

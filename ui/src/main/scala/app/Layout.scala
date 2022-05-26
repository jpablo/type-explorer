package app

import app.CenterColumn.centerColumn
import app.LeftColumn.leftColumn
import app.RightColumn.rightColumn
import app.Header.header
import org.scalajs.dom
import com.raquo.laminar.api.L.*


object Layout {

  val newDiagramBus = new EventBus[String]

  def container =
    div(idAttr := "te-toplevel",
      header(newDiagramBus),
      div(idAttr := "te-main-area", cls := "container-fluid",
        div(cls := "row", styleAttr := "height: 100%",
          leftColumn,
          centerColumn(newDiagramBus.events),
          rightColumn
        ),
      ),
      div(idAttr := "te-footer", "Environment: DEV")
    )

}

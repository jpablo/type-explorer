package app

import app.CenterColumn.centerColumn
import app.LeftColumn.leftColumn
import app.RightColumn.rightColumn
import app.{header, footer}
import org.scalajs.dom
import com.raquo.laminar.api.L.*


object Layout {

  val newDiagramBus = new EventBus[String]

  def container: Div =
    div (idAttr := "te-toplevel",
      header (newDiagramBus),
      div (idAttr := "te-main-area", cls := "container-fluid",
        div (cls := "row", styleAttr := "height: 100%",
          leftColumn,
          centerColumn (newDiagramBus.events),
          rightColumn
        ),
      ),
      footer
    )

}

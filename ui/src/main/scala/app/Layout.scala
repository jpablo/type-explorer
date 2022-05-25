package app

import app.CenterColumn.centerColumn
import app.LeftColumn.leftColumn
import app.RightColumn.rightColumn
import org.scalajs.dom
import com.raquo.laminar.api.L.*


object Layout {

  def container =
    div(idAttr := "te-toplevel",
      div(idAttr := "te-header", "header"),
      div(idAttr := "te-main-area", cls := "container-fluid",
        div(cls := "row", styleAttr := "height: 100%",
          leftColumn,
          centerColumn,
          rightColumn
        ),
      ),
      div(idAttr := "te-footer", "footer")
    )

}

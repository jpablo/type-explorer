package app.components

import com.raquo.laminar.api.L.*

object TopLevel {

  val newDiagramBus = new EventBus[String]

  def topLevel: Div =
    div (
      idAttr := "te-toplevel",
      appHeader (newDiagramBus),
      div (
        idAttr := "te-main-area", 
        cls := "container-fluid",
        div (
          cls := "row", 
          styleAttr := "height: 100%",
          leftColumn,
          centerColumn (newDiagramBus.events),
          rightColumn
        ),
      ),
      appFooter
    )
}


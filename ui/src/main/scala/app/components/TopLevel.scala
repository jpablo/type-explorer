package app.components

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.scalajs.dom

object TopLevel {

  val newDiagramBus = new EventBus[DiagramType]
  val parser = new dom.DOMParser()

  var svgStream: EventStream[dom.Document] =
    for
      dt <- newDiagramBus.events
      fetchEventStreamBuilder =
        dt match
          case DiagramType.Inheritance => Fetch.get("http://localhost:8090/inheritance")
          case DiagramType.CallGraph => Fetch.get("http://localhost:8090/call-graph")
      response: FetchResponse[String] <- fetchEventStreamBuilder.text
    yield
      parser.parseFromString(response.data, dom.MIMEType.`image/svg+xml`)

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
          centerColumn (svgStream),
          rightColumn
        ),
      ),
      appFooter
    )
}


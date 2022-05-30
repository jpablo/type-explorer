package app.components

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.scalajs.dom

object TopLevel {

  val newDiagramBus = new EventBus[DiagramType]
  val parser = new dom.DOMParser()

  var svgStream: EventStream[dom.Element] =
    for
      event <- newDiagramBus.events
      fetchEventStreamBuilder = event match
        case DiagramType.Inheritance => Fetch.get("http://localhost:8090/inheritance")
        case DiagramType.CallGraph => Fetch.get("http://localhost:8090/call-graph")
      fetchResponse <- fetchEventStreamBuilder.text
      doc = parser.parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`)
//      errorNode = doc.querySelector("parsererror")
    yield
      doc.documentElement

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


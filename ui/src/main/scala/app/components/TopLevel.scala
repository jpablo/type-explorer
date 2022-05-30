package app.components

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.scalajs.dom
import org.scalajs.dom.Document

import scala.scalajs.js.Object.keys

object TopLevel {

  val newDiagramBus = new EventBus[DiagramType]
  val parser = new dom.DOMParser()

  var svgStream: EventStream[dom.Element] =
    for
      event <- newDiagramBus.events // EventStream[DiagramType]
      fetchEventStreamBuilder: FetchEventStreamBuilder =
        event match
          case DiagramType.Inheritance => Fetch.get("http://localhost:8090/inheritance")
          case DiagramType.CallGraph => Fetch.get("http://localhost:8090/call-graph")
      response: FetchResponse[String] <- fetchEventStreamBuilder.text // EventStream[FetchResponse[String]]
    yield
      val mimeType =
        dom.MIMEType.`image/svg+xml`
      val d: Document = parser.parseFromString(response.data, mimeType)
//      val errorNode = d.querySelector("parsererror")
      d.documentElement

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


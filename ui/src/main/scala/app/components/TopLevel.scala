package app.components

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.scalajs.dom
import models.Type
import io.laminext.fetch.*
import io.laminext.fetch.circe.*


object TopLevel {

  val $newDiagramType = new EventBus[DiagramType]
  val $projectPath = Var[String] ("")
  val parser = new dom.DOMParser()

  def topLevel: Div =
    div (
      idAttr := "te-toplevel",
      appHeader ($newDiagramType, $projectPath),
      div (
        idAttr := "te-main-area", 
        cls := "container-fluid",
        div (
          cls := "row", 
          styleAttr := "height: 100%",
          leftColumn (getClasses ($projectPath.signal)),
          centerColumn (svgStream ($newDiagramType.events)),
          rightColumn
        ),
      ),
      appFooter
    )


  def getClasses ($projectPath: Signal[String]): EventStream[List[Type]] =
    for
      pp <- $projectPath
      response <- Fetch.get("http://localhost:8090/classes?path=" + pp).decode[List[Type]]
    yield
      response.data


  def svgStream ($diagramType: EventStream[DiagramType]): EventStream[dom.Element] =
    for
      event <- $diagramType
      fetchEventStreamBuilder = event match
        case DiagramType.Inheritance => Fetch.get ("http://localhost:8090/inheritance")
        case DiagramType.CallGraph => Fetch.get ("http://localhost:8090/call-graph")
      fetchResponse <- fetchEventStreamBuilder.text
      doc = parser.parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`)
//      errorNode = doc.querySelector("parsererror")
    yield
      doc.documentElement

      
}


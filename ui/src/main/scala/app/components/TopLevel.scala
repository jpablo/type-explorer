package app.components

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.scalajs.dom
import models.Type
import io.laminext.fetch.*
import io.laminext.fetch.circe.*
import scala.scalajs.js.typedarray.Int8Array
import scala.meta.internal.semanticdb.{TextDocuments, TextDocument}

object TopLevel {

  val $newDiagramType = new EventBus[DiagramType]
  val $projectPath = Var[String]("/Users/jpablo/proyectos/playground/type-explorer")
  val parser = new dom.DOMParser()

  def topLevel: Div =
    div (
      idAttr := "te-toplevel",
      appHeader($newDiagramType, $projectPath),
      leftColumn(getClasses($projectPath.signal)),
      centerColumn(svgStream($newDiagramType.events)),
      rightColumn,
      appFooter
    )


  def getClasses($projectPath: Signal[String]): EventStream[List[TextDocument]] =
    for
      pp <- $projectPath
      response <- Fetch.get("http://localhost:8090/semanticdb?path=" + pp).arrayBuffer
    yield
      val ia = Int8Array(response.data, 0, length = response.data.byteLength)
      TextDocuments.parseFrom(ia.toArray).documents.toList


  def svgStream($diagramType: EventStream[DiagramType]): EventStream[dom.Element] =
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


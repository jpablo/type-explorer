package app.client

import org.scalajs.dom
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import io.laminext.fetch.*
import scala.scalajs.js.typedarray.Int8Array
import models.Type
import app.components.DiagramType


def fetchBase(path: String): FetchEventStreamBuilder =
  Fetch.get(s"http://localhost:8090/$path")


def fetchDocuments(projectPath: Signal[String]): EventStream[List[TextDocumentsWithSource]] =
  for
    path <- projectPath
    response <- fetchBase("semanticdb?path=" + path).arrayBuffer
  yield
    val ia = Int8Array(response.data, 0, length = response.data.byteLength)
    TextDocumentsWithSourceSeq.parseFrom(ia.toArray).documentsWithSource.toList.sortBy(_.semanticDbUri)



def fetchSVGDiagram(diagram: Signal[(DiagramType, String)]): EventStream[dom.Element] =
  val parser = dom.DOMParser()
  for
    (diagramType, path) <- diagram
    fetchEventStreamBuilder = diagramType match
      case DiagramType.Inheritance => fetchBase("inheritance?path=" + path)
      case DiagramType.CallGraph => fetchBase("call-graph?path=" + path)
    fetchResponse <- fetchEventStreamBuilder.text
    doc = parser.parseFromString(
      fetchResponse.data,
      dom.MIMEType.`image/svg+xml`
    )
//      errorNode = doc.querySelector("parsererror")
  yield doc.documentElement

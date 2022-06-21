package org.jpablo.typeexplorer.ui.app.client

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.Namespace
import org.jpablo.typeexplorer.ui.app.components.DiagramType
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.scalajs.dom
import scala.scalajs.js.typedarray.Int8Array
import zio.json.*


def fetchBase(path: String): FetchEventStreamBuilder =
  Fetch.get(s"http://localhost:8090/$path")


def fetchDocuments(projectPath: Signal[String]): EventStream[List[TextDocumentsWithSource]] =
  for
    path <- projectPath
    response <- fetchBase("semanticdb?path=" + path).arrayBuffer
  yield
    val ia = Int8Array(response.data, 0, length = response.data.byteLength)
    TextDocumentsWithSourceSeq.parseFrom(ia.toArray).documentsWithSource.toList.sortBy(_.semanticDbUri)


def fetchClasses(projectPath: Signal[String]): EventStream[InheritanceDiagram] =
  for
    path     <- projectPath
    response <- fetchBase("classes?path=" + path).text
    classes  <- EventStream.fromTry {
      response.data
        .fromJson[InheritanceDiagram].left
        .map(Exception(_))
        .toTry
    }
  yield
    classes

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

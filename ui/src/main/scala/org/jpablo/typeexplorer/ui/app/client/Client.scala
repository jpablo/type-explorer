package org.jpablo.typeexplorer.ui.app.client

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.Namespace
import org.jpablo.typeexplorer.ui.app.components.DiagramType
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.scalajs.dom
import scalajs.js.URIUtils.encodeURIComponent
import scala.scalajs.js.typedarray.Int8Array
import zio.json.*
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.Related
import concurrent.ExecutionContext.Implicits.global

def fetchBase(path: String): FetchEventStreamBuilder =
  Fetch.get(s"http://localhost:8090/$path")


def fetchDocuments(projectPath: Signal[Path]): EventStream[List[TextDocumentsWithSource]] =
  for
  // Checar si la ruta es vacía
    path <- projectPath
    // mandar esto a un if en otro for comprehension
    lst <-
      if path.toString.isEmpty then EventStream.fromValue(List.empty)
      else
        for response <- fetchBase("semanticdb?path=" + path).arrayBuffer yield
          val ia = Int8Array(response.data, 0, length = response.data.byteLength)
          TextDocumentsWithSourceSeq.parseFrom(ia.toArray).documentsWithSource.toList.sortBy(_.semanticDbUri)
  yield
    lst


def fetchClasses(projectPath: Signal[Path]): EventStream[InheritanceDiagram] =
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

def fetchInheritanceSVGDiagram($projectPath: Signal[Path])(symbols: Set[models.Symbol], related: Option[Related] = None): EventStream[dom.Element] =
  val parser = dom.DOMParser()
  for
    projectPath <- $projectPath
    doc <- 
      if projectPath.toString.isEmpty then 
        EventStream.fromValue(div().ref)
      else
        val queryString = List(
          s"path=$projectPath",
          related.map(r => s"related=$r").getOrElse(""),
          symbols.map(s => s"symbol=${encodeURIComponent(s.toString)}").mkString("&")
        )
        fetchBase(s"inheritance?" + queryString.mkString("&")).text.map { fetchResponse =>
          parser.parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`).documentElement
        }
  yield 
    doc

def fetchCallGraphSVGDiagram(diagram: Signal[(DiagramType, Path)]): EventStream[dom.Element] =
  val parser = dom.DOMParser()
  for
    (diagramType, path) <- diagram
    doc <- if path.toString.isEmpty
    then
      EventStream.fromValue(div().ref)
    else
      val fetchEventStreamBuilder = diagramType match
        case DiagramType.Inheritance => fetchBase("inheritance?path=" + path)
        case DiagramType.CallGraph   => fetchBase("call-graph?path=" + path)

      fetchEventStreamBuilder.text.map { fetchResponse =>
        parser.parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`).documentElement
      }
//      errorNode = doc.querySelector("parsererror")
  yield doc

def fetchSourceCode($projectPath: Signal[Path])(selectedPath: Path): EventStream[String] =
  for
    projectPath <- $projectPath
    response    <- fetchBase(s"source?path=$projectPath/$selectedPath").text
  yield
    response.data

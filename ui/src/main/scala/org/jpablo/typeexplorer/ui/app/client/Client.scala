package org.jpablo.typeexplorer.ui.app.client

import com.raquo.laminar.api.L.*
import concurrent.ExecutionContext.Implicits.global
import io.laminext.fetch.*
import org.scalajs.dom
import scala.scalajs.js.typedarray.Int8Array
import scalajs.js.URIUtils.encodeURIComponent
import zio.json.*

import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.shared.models.{Symbol, Namespace}
import org.jpablo.typeexplorer.shared.webApp.InheritanceReq
import org.jpablo.typeexplorer.ui.app.components.DiagramType
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.console

val basePath = "http://localhost:8090/" 

def fetchBase(path: String): FetchEventStreamBuilder =
  Fetch.get(basePath + path)


def fetchDocuments($projectPath: Signal[Path]): EventStream[List[TextDocumentsWithSource]] =
  for
    path <- $projectPath
    lst <-
      if path.toString.isEmpty then
        EventStream.fromValue(List.empty)
      else 
        for response <- fetchBase("semanticdb?path=" + path).arrayBuffer yield
          val ia = Int8Array(response.data, 0, length = response.data.byteLength)
          TextDocumentsWithSourceSeq.parseFrom(ia.toArray).documentsWithSource.toList.sortBy(_.semanticDbUri)
  yield
    lst


def fetchClasses($projectPath: Signal[Path]): EventStream[InheritanceDiagram] =
  for
    path     <- $projectPath
    response <- fetchBase("classes?path=" + path).text
    classes  <- EventStream.fromTry {
      response.data
        .fromJson[InheritanceDiagram].left
        .map(Exception(_))
        .toTry
    }
  yield
    classes

def fetchInheritanceSVGDiagram(projectPath: Path, symbols: Set[(Symbol, Set[Related])], options: Options): EventStream[dom.SVGElement] =
  val parser = dom.DOMParser()
  if projectPath.toString.isEmpty then 
    EventStream.fromValue(svg.svg().ref)
  else
    val body = InheritanceReq(List(projectPath.toString), symbols, InheritanceReq.Config(options.fields, options.signatures))
    val req  = Fetch.post(basePath + "inheritance", body.toJson)
    req.text.map { fetchResponse =>
      parser
        .parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`)
        .documentElement
        .asInstanceOf[dom.SVGElement]
    }

def fetchCallGraphSVGDiagram($diagram: Signal[(DiagramType, Path)]): EventStream[dom.Element] =
  val parser = dom.DOMParser()
  for
    (diagramType, path) <- $diagram
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

def fetchSourceCode =
  AppState.$projectPath.map { $projectPath => (selectedPath: Path) =>
    for
      projectPath <- $projectPath
      response    <- fetchBase(s"source?path=$projectPath/$selectedPath").text
    yield
      response.data
  }

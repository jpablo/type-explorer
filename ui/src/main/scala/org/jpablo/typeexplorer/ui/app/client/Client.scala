package org.jpablo.typeexplorer.ui.app.client

import com.raquo.laminar.api.L.*
import concurrent.ExecutionContext.Implicits.global
import io.laminext.fetch.*
import io.laminext.syntax.core.StoredString
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.jpablo.typeexplorer.shared.inheritance.DiagramOptions
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models.{Namespace, Symbol}
import org.jpablo.typeexplorer.shared.webApp.InheritanceRequest
import org.jpablo.typeexplorer.shared.webApp.Routes
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.DiagramType
import org.jpablo.typeexplorer.ui.app.components.state.Project
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom
import scala.scalajs.js.typedarray.Int8Array
import scalajs.js.URIUtils.encodeURIComponent
import zio.json.*

val basePath = "http://localhost:8090/"

def fetchBase(path: String): FetchEventStreamBuilder =
  Fetch.get(basePath + path)


def fetchDocuments(paths: Signal[List[Path]]): EventStream[List[TextDocumentsWithSource]] =
  for
    path <- paths
    lst <-
      if path.isEmpty then
        EventStream.fromValue(List.empty)
      else
        val qs = path.map(p => "path=" + p).mkString("&")
        for response <- fetchBase("semanticdb?" + qs).arrayBuffer yield
          val ia = Int8Array(response.data, 0, length = response.data.byteLength)
          TextDocumentsWithSourceSeq.parseFrom(ia.toArray).documentsWithSource.toList.sortBy(_.semanticDbUri)
  yield
    lst


def fetchInheritanceDiagram(basePaths: List[Path]): Signal[InheritanceDiagram] = {
  if basePaths.isEmpty then
    EventStream.empty
  else
    val qs = basePaths.map("path=" + _).mkString("&")
    for
      response <- fetchBase(s"${Routes.classes}?$qs").text
      classes  <- EventStream.fromTry {
        response.data
          .fromJson[InheritanceDiagram].left
          .map(Exception(_))
          .toTry
      }
    yield
      classes
}.startWith(InheritanceDiagram.empty)

def fetchInheritanceSVGDiagram(project: Project): EventStream[InheritanceSvgDiagram] =
  val combined =
    project.basePaths
      .combineWith(
        project.inheritanceTabState.activeSymbolsR.signal,
        project.appConfig.signal.map(_.diagramOptions)
      )
  for
    (basePaths: List[Path], symbols: ActiveSymbols, options) <- combined
    parser = dom.DOMParser()
    svgElement <-
      if basePaths.isEmpty then
        EventStream.fromValue(svg.svg().ref)
      else
        val body = InheritanceRequest(basePaths.map(_.toString), symbols.toList, options)
        val req = Fetch.post(s"$basePath${Routes.inheritanceDiagram}", body.toJson)
        req.text.map { fetchResponse =>
          parser
            .parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`)
            .documentElement
            .asInstanceOf[dom.SVGElement]
        }
  yield
    InheritanceSvgDiagram(svgElement)


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

def fetchSourceCode(paths: Signal[Path])(docPath: Path) =
  for
    path <- paths
    response    <- fetchBase(s"source?path=${encodeURIComponent(path.toString + "/" + docPath)}").text
  yield
    response.data

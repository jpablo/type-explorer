package org.jpablo.typeexplorer.ui.app.client

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.jpablo.typeexplorer.protos.{TextDocumentsWithSource, TextDocumentsWithSourceSeq}
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceGraph, Path}
import org.jpablo.typeexplorer.shared.webApp.{Endpoints, InheritanceRequest, port}
import org.jpablo.typeexplorer.ui.app.components.DiagramType
import org.jpablo.typeexplorer.ui.app.components.state.Page
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom
import zio.json.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.URIUtils.encodeURIComponent
import scala.scalajs.js.typedarray.Int8Array

val basePath = s"http://localhost:$port/"

def fetchBase(path: String): FetchEventStreamBuilder =
  Fetch.get(basePath + path)

def fetchDocuments(
    paths: Signal[List[Path]]
): EventStream[List[TextDocumentsWithSource]] =
  for
    path <- paths
    lst <-
      if path.isEmpty then EventStream.fromValue(List.empty)
      else
        val qs = path.map(p => "path=" + p).mkString("&")
        for response <- fetchBase("semanticdb?" + qs).arrayBuffer yield
          val ia =
            Int8Array(response.data, 0, length = response.data.byteLength)
          TextDocumentsWithSourceSeq
            .parseFrom(ia.toArray)
            .documentsWithSource
            .toList
            .sortBy(_.semanticDbUri)
  yield lst

def fetchFullInheritanceGraph(
    basePaths: List[Path]
): Signal[InheritanceGraph] = {
  if basePaths.isEmpty then EventStream.empty
  else
    val qs = basePaths.map("path=" + _).mkString("&")
    for
      response <- fetchBase(s"${Endpoints.classes}?$qs").text
      classes <- EventStream.fromTry {
        response.data
          .fromJson[InheritanceGraph]
          .left
          .map(Exception(_))
          .toTry
      }
    yield classes
}.startWith(InheritanceGraph.empty)

def fetchInheritanceSVGDiagram(
    basePaths: List[Path],
    page:      Page
): EventStream[InheritanceSvgDiagram] =
  if basePaths.isEmpty then EventStream.fromValue(InheritanceSvgDiagram.empty)
  else
    Fetch
      .post(
        url  = s"$basePath${Endpoints.inheritanceDiagram}",
        body = InheritanceRequest(basePaths.map(_.toString), page.activeSymbols, page.diagramOptions).toJson
      )
      .text
      .map: fetchResponse =>
        dom
          .DOMParser()
          .parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`)
          .documentElement
          .asInstanceOf[dom.SVGSVGElement]
      .map(InheritanceSvgDiagram(_))

def fetchCallGraphSVGDiagram(
    diagram: Signal[(DiagramType, Path)]
): EventStream[dom.Element] =
  val parser = dom.DOMParser()
  for
    (diagramType, path) <- diagram
    doc <-
      if path.toString.isEmpty
      then EventStream.fromValue(div().ref)
      else
        val fetchEventStreamBuilder = diagramType match
          case DiagramType.Inheritance => fetchBase("inheritance?path=" + path)
          case DiagramType.CallGraph   => fetchBase("call-graph?path=" + path)

        fetchEventStreamBuilder.text.map: fetchResponse =>
          parser
            .parseFromString(fetchResponse.data, dom.MIMEType.`image/svg+xml`)
            .documentElement
//      errorNode = doc.querySelector("parsererror")
  yield doc

def fetchSourceCode(paths: Signal[Path])(docPath: Path) =
  for
    path <- paths
    response <- fetchBase(
      s"source?path=${encodeURIComponent(path.toString + "/" + docPath)}"
    ).text
  yield response.data

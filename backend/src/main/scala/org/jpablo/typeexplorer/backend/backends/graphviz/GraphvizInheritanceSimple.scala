package org.jpablo.typeexplorer.backend.backends.graphviz

import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.engine.{Format, Graphviz}
import guru.nidi.graphviz.model.Graph
import zio.*


type SvgText = String

extension (graph: Graph)
  def toSVG: Task[String] =
    val renderer =
      Graphviz
        .fromGraph(
          graph.graphAttr().`with`(Color.TRANSPARENT.background())
        )
        .height(800)
        .render(Format.SVG_STANDALONE)
    ZIO.attemptBlockingIO(renderer.toString)



//@main
//def plantumlExample(): Unit =
//  val path = file.Paths.get("/Users/jpablo/GitHub/scala-js-dom")
//  val docs = TextDocuments(All.scan(path).flatMap(_._2.documents))
//  val diagram = InheritanceDiagram.fromTextDocumentsWithSource(docs)
//    .subdiagram(
//      Set(
//        models.Symbol("org/scalajs/dom/experimental/domparser/package."),
//        models.Symbol("org/scalajs/dom/EventTarget#")
//      )
//    )
//  val diagramStr = PlantumlInheritance.fromInheritanceDiagram(diagram, Map.empty)
//  println(diagramStr.diagram)
//  println("-------------------")
//  var svg = renderDiagram("laminar", diagram)
//  println(svg)

package org.jpablo.typeexplorer.backend.backends.graphviz

import guru.nidi.graphviz.attribute.Color
import guru.nidi.graphviz.model.Graph
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}
import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples, PlantUML, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind}
import zio.*
import zio.ZIO.ZIOConstructor
import guru.nidi.graphviz.engine.{Format, Graphviz}

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.file
import scala.meta.internal.semanticdb.{TextDocuments, TypeSignature}
import scala.util.{Try, Using}


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

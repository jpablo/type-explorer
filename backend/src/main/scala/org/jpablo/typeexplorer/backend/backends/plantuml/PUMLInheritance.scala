package org.jpablo.typeexplorer.backend.backends.plantuml

import org.jpablo.typeexplorer.shared.inheritance.{PlantUML, PlantumlInheritance}
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}
import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples}
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind}
import org.jpablo.typeexplorer.shared.models

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.file
import scala.meta.internal.semanticdb.{TextDocuments, TypeSignature}
import scala.util.Using
import zio.*
import scala.util.Try
import zio.ZIO.ZIOConstructor


type SvgText = String

extension (puml: PlantUML)
  def toSVG(name: String): Task[String] =
    for
      reader <- ZIO.from(SourceStringReader(puml.diagram))
      createStream = ZIO.succeed(new ByteArrayOutputStream)
      svg <-
        ZIO.acquireReleaseWith(createStream)(os => ZIO.succeed(os.close())) { os =>
          ZIO.attemptBlocking {
            reader.outputImage(os, FileFormatOption(FileFormat.SVG))
            os.toString(Charset.defaultCharset())
          }
        }
    yield
      svg



@main
def plantumlExample(): Unit =
  val path = file.Paths.get("/Users/jpablo/GitHub/scala-js-dom")
  val docs = TextDocuments(All.scan(path).flatMap(_._2.documents))
  val diagram = InheritanceDiagram.fromTextDocuments(docs)
    .subdiagram(
      Set(
        models.Symbol("org/scalajs/dom/experimental/domparser/package."),
        models.Symbol("org/scalajs/dom/EventTarget#")
      )
    )
  val diagramStr = PlantumlInheritance.fromInheritanceDiagram(diagram, Map.empty)
  println(diagramStr.diagram)
//  println("-------------------")
//  var svg = renderDiagram("laminar", diagram)
//  println(svg)

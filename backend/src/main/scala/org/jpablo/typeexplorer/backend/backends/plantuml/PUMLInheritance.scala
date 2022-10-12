package org.jpablo.typeexplorer.backend.backends.plantuml

import org.jpablo.typeexplorer.shared.inheritance.{PlantUML, PlantumlInheritance}
import net.sourceforge.plantuml.{FileFormat, FileFormatOption, SourceStringReader}
import org.jpablo.typeexplorer.backend.semanticdb.All
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples}
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind}

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.file
import scala.meta.internal.semanticdb.{TextDocuments, TypeSignature}


extension (puml: PlantUML)
  def toSVG(name: String): String =
    val reader = new SourceStringReader(puml.diagram)
    val os = new ByteArrayOutputStream
    // Write the first image to "os"
    val desc: String = reader.generateImage(os, new FileFormatOption(FileFormat.SVG))
    os.close()
    // The XML is stored into svg
    new String(os.toByteArray, Charset.forName("UTF-8"))


@main
def plantumlExample(): Unit =
  val path = file.Paths.get("/Users/jpablo/proyectos/playground/type-explorer")
  val docs = TextDocuments(All.scan(path).flatMap(_._2.documents))
  val diagram = InheritanceDiagram.fromTextDocuments(docs)
  val diagramStr = PlantumlInheritance.fromInheritanceDiagram(diagram)
  println(diagramStr)
//  println("-------------------")
//  var svg = renderDiagram("laminar", diagram)
//  println(svg)

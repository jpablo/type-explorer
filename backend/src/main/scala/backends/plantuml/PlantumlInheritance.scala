package backends.plantuml

import backends.plantuml.PlantumlInheritance.{toDiagramString, renderDiagramString}
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import inheritance.{InheritanceDiagram, InheritanceExamples}
import semanticdb.{All, ClassesList}

import java.nio.file
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, SymbolInformation, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import scala.util.chaining.*
import models.*

object PlantumlInheritance {

  def toDiagramString(diagram: InheritanceDiagram): String =
    val declarations =
      for case ns <- diagram.namespaces yield ns.kind match
        case NamespaceKind.Object        => s""" class "${ns.displayName}" as ${ns.symbol} << (O, orchid) >>"""
        case NamespaceKind.PackageObject => s""" class "${ns.displayName}" as ${ns.symbol} << (P, lightblue) >>"""
        case NamespaceKind.Class         => s""" class "${ns.displayName}" as ${ns.symbol}"""
        case other                       => s""" class "${ns.displayName}" as ${ns.symbol} <<$other>>}"""

    val inheritance =
      for (source, target) <- diagram.pairs yield
        s""""${target}" <|-- "${source}""""

    val fields =
      for
        ns     <- diagram.namespaces
        method <- ns.methods
      yield
        s""""${ns.symbol}" : "${method.displayName}"${method.returnType.map(o => " : " + o.displayName).getOrElse("")}"""

    s"""@startuml
       |set namespaceSeparator none
       |${declarations.distinct mkString "\n"}
       |${inheritance mkString "\n"}
       |${fields mkString "\n"}
       |@enduml""".stripMargin

  def renderDiagramString(name: String, diagram: String) =
    val reader = new SourceStringReader(diagram)
    val os = new ByteArrayOutputStream
    // Write the first image to "os"
    val desc: String = reader.generateImage(os, new FileFormatOption(FileFormat.SVG))
    os.close()
    // The XML is stored into svg
    new String(os.toByteArray, Charset.forName("UTF-8"))

}


@main
def plantumlExample(): Unit = {
  val path = file.Paths.get("/Users/jpablo/proyectos/playground/type-explorer")
  val docs = TextDocuments(All.scan(path).flatMap(_._2.documents))
  val diagram = ClassesList.fromTextDocuments(docs)
  val diagramStr = PlantumlInheritance.toDiagramString(diagram)
  println(diagramStr)
//  println("-------------------")
//  var svg = renderDiagram("laminar", diagram)
//  println(svg)
}

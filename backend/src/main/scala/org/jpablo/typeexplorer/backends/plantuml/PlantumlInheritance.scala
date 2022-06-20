package org.jpablo.typeexplorer.backends.plantuml

import PlantumlInheritance.{renderDiagramString, toDiagramString}
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader
import org.jpablo.typeexplorer.fileTree.FileTree
import org.jpablo.typeexplorer.inheritance.{InheritanceDiagram, InheritanceExamples}
import org.jpablo.typeexplorer.models.{Method, Namespace, NamespaceKind}

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.file
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, SymbolInformation, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import scala.util.chaining.*
import org.jpablo.typeexplorer.semanticdb.{All, ClassesList}

object PlantumlInheritance:

  def toDiagramString(diagram: InheritanceDiagram): String =

    def renderTree(t: FileTree[Namespace]): String = t match
      case FileTree.Directory(name, contents) =>
        s"""
           |namespace $name {
           |  ${contents.map(renderTree) mkString "\n"}
           |}
           |""".stripMargin
      case FileTree.File(_, ns) =>
        renderNamespace(ns)

    val declarations: List[String] =
      diagram.toFileTree.map(renderTree)

    val inheritance =
      for (source, target) <- diagram.pairs yield
        s""""${target}" <|-- "${source}""""

    s"""@startuml
       |set namespaceSeparator none
       |${declarations.distinct mkString "\n"}
       |${inheritance mkString "\n"}
       |@enduml""".stripMargin

  def renderDiagramString(name: String, diagram: String) =
    val reader = new SourceStringReader(diagram)
    val os = new ByteArrayOutputStream
    // Write the first image to "os"
    val desc: String = reader.generateImage(os, new FileFormatOption(FileFormat.SVG))
    os.close()
    // The XML is stored into svg
    new String(os.toByteArray, Charset.forName("UTF-8"))

  private def renderNamespace(ns: Namespace): String =
    val header = s"""class "${ns.displayName}" as ${ns.symbol}"""
    val stereotype = ns.kind match
      case NamespaceKind.Object        => """ << (O, orchid) >>"""
      case NamespaceKind.PackageObject => """ << (P, lightblue) >>"""
      case NamespaceKind.Class         => ""
      case other                       => s""" <<$other>>"""
    val fields = ns.methods.map(renderField)
    header + stereotype + fields.mkString(" {\n", "\n", "\n}\n")

  private def renderField(m: Method): String =
    s"""  ${m.displayName} ${m.returnType.map(o => " : " + o.displayName).getOrElse("")}  \n' ${m.symbol} """



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

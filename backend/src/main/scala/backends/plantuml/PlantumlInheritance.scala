package backends.plantuml

import backends.plantuml.PlantumlInheritance.{renderDiagram, fromTextDocuments}
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import inheritance.{InheritanceDiagram, InheritanceExamples}
import semanticdb.All

import java.nio.file
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, SymbolInformation, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}

object PlantumlInheritance {

  def fromTextDocuments(textDocuments: TextDocuments): String =
    val tuples =
      for
        doc <- textDocuments.documents
        symbol <-  doc.symbols
        signature <- symbol.signature.asNonEmpty.toSeq
        classSignature <- signature match
          case cs @ ClassSignature(typeParameters, parents, self, declarations) => List(cs)
          case _ =>  List.empty
      yield
        (symbol.kind, symbol.symbol, symbol.displayName, classSignature.parents, classSignature.declarations.map(_.symlinks).toSeq.flatten)

    val symbolId: Map[String, Int] =
      tuples.zipWithIndex.map { case (t, i) => t._2 -> i }.toMap

    val declarations =
      for
        case (kind, symbol, displayName, _, _) <- tuples
      yield
        if kind == Kind.OBJECT then
          s""" class "$displayName" as $symbol << (O, orchid) >>"""
        else if kind == Kind.PACKAGE_OBJECT then
          s""" class "$displayName" as $symbol << (P, lightblue) >>"""
        else if kind == Kind.CLASS then
          s""" class "$displayName" as $symbol"""
        else
          s""" class "$displayName" as $symbol <<$kind>>"""

//        s""" class "$symbol" as ${symbolId(symbol)} """

    val pairs =
      for
        (_, symbol, displayName, parents, _) <- tuples
        parent <- parents
        parent <- parent.asNonEmpty.toSeq
        parentSymbol <- parent match
          case TypeRef(prefix, symbol, typeArguments) => List(symbol)
          case _ => List.empty
      yield
        s""" "$parentSymbol" <|-- "$symbol" """

//    val fields =
//      for
//        (_, symbol, displayName, _, declarations) <- tuples
//        declaration <- declarations
//      yield
//        s"$displayName : $declaration"


//       |${fields mkString "\n"}
    s"""@startuml
       |set namespaceSeparator none
       |${declarations.distinct mkString "\n"}
       |${pairs.distinct mkString "\n"}
       |@enduml""".stripMargin


  def toDiagram(diagram: InheritanceDiagram): String =
    val classes =
      for (source, target) <- diagram.pairs yield
        s""""${target.name}" <|-- "${source.name}""""

    val fields =
      for
        tpe <- diagram.types
        method <- tpe.methods
      yield
        s"${tpe.name} : ${method.name}${method.returnType.map(o => " : " + o.name).getOrElse("")}"

    s"""@startuml
       |${classes mkString "\n"}
       |${fields mkString "\n"}
       |@enduml""".stripMargin

  def renderDiagram(name: String, source: String) =
    val reader = new SourceStringReader(source)
    val os = new ByteArrayOutputStream
    // Write the first image to "os"
    val desc: String = reader.generateImage(os, new FileFormatOption(FileFormat.SVG))
    os.close()
    // The XML is stored into svg
    new String(os.toByteArray, Charset.forName("UTF-8"))

}


@main
def plantumlExample(): Unit = {
  import scala.util.chaining.*

  val docs =
    All.scan (file.Paths.get("/Users/jpablo/proyectos/playground/type-explorer")) flatMap (_._2.documents) pipe TextDocuments.apply
  val diagram: String = fromTextDocuments(docs)
  println(diagram)
//  println("-------------------")
//  var svg = renderDiagram("laminar", diagram)
//  println(svg)
}

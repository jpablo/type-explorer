package backends.plantuml

import backends.plantuml.PlantumlInheritance.{renderDiagram, toDiagram}
import net.sourceforge.plantuml.FileFormat
import net.sourceforge.plantuml.FileFormatOption
import net.sourceforge.plantuml.SourceStringReader

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import inheritance.{InheritanceDiagram, InheritanceExamples}

object PlantumlInheritance {

  def toDiagram(diagram: InheritanceDiagram): String =
    val classes =
      for (source, target) <- diagram.pairs yield
        s"${target.name} <|-- ${source.name}"

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
def plantumlExample() = {
  val diagram = toDiagram(InheritanceExamples.laminar)
  println(diagram)
  println("-------------------")
  var svg = renderDiagram("laminar", diagram)
  println(svg)
}

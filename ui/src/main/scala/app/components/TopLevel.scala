package app.components

import com.raquo.laminar.api.L.*
import io.laminext.fetch.*
import org.scalajs.dom
import models.Type
import io.laminext.fetch.*
import io.laminext.fetch.circe.*
import scala.scalajs.js.typedarray.Int8Array
import scala.meta.internal.semanticdb.{TextDocuments, TextDocument}
import app.client.{fetchSVGDiagram, fetchDocuments}

object TopLevel {

  val newDiagramType = new EventBus[DiagramType]
  val projectPath = Var[String]("/Users/jpablo/proyectos/playground/type-explorer")
  var documents = fetchDocuments(projectPath.signal)
  var inheritance = fetchSVGDiagram(projectPath.signal.map(path => (DiagramType.Inheritance, path)))
  val parser = new dom.DOMParser()

  def topLevel: Div =
    div (
      idAttr := "te-toplevel",
      appHeader(newDiagramType, projectPath),
      div(idAttr := "structure", "Structure"),
      tabsArea(
        documents,
        inheritance
      ),
      appFooter
    )

}

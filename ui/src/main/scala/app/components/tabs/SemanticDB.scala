package app.components.tabs

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}
import scalapb.GeneratedMessage


def semanticDBTab(documents: EventStream[List[TextDocument]]) =
  div(
    cls := "text-document-container",
    children <-- documents.split(_.uri)(SemanticDB.renderTextDocument)
  )


object SemanticDB:

  def renderTextDocument(id: String, initial: TextDocument, elem: EventStream[TextDocument]) = 
    div(
      cls := "text-document",
      b(
        child.text <-- elem.map(_.uri)
      ),
      div(
        cls := "symbol-information-container",
        children <-- elem.map(_.symbols).split(_.symbol)(renderGeneratedMessage("symbol-information"))
      ),
      div(
        cls := "occurrences-container",
        children <-- elem.map(_.occurrences).split(_.symbol)(renderGeneratedMessage("occurrence"))
      ),
      div(
        cls := "synthetics-container",
        children <-- elem.map(_.synthetics).split(_.range.map(_.toProtoString).getOrElse(""))(renderGeneratedMessage("synthetic"))
      )
    )

  def renderGeneratedMessage(className: String)(id: String, initial: GeneratedMessage, elem: EventStream[GeneratedMessage]) =
    div(
      cls := ("card", className),
      div(
        cls := "card-body",
        pre(
          child.text <-- elem.map(_.toProtoString)
        )          
      )
    )
    
end SemanticDB
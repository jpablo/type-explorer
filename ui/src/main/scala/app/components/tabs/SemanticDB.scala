package app.components.tabs

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage


def semanticDBTab(documentsWithSource: EventStream[List[TextDocumentsWithSource]]) =
  div(
    cls := "text-document-areas",
    SemanticDB.structure(documentsWithSource),
    ol(
      cls := "semanticdb-document-container",
      children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDB.renderTextDocumentsWithSource)
    )
  )


object SemanticDB:

  // TODO: render individual elements using .split
  def structure(documents: EventStream[List[TextDocumentsWithSource]]) =
    ol(
      cls := "structure", 
      children <-- 
        documents.map { docs =>
          for docWithSource <- docs yield
            li(
              whiteSpace := "nowrap",
              a(
                href := "#" + docWithSource.semanticDbUri,
                docWithSource.semanticDbUri
              ),
              ul(
                for doc <- docWithSource.documents yield
                  li(
                    a(
                      href := "#" + doc.uri,
                      doc.uri
                    ),
                    ul(
                      for sym <- doc.symbols yield
                        li(
                          sym.symbol
                        )
                    )
                  )
              )
            )
        }
    )
  

  def renderTextDocumentsWithSource(id: String, initial: TextDocumentsWithSource, elem: EventStream[TextDocumentsWithSource]) =
    li(
      idAttr := id,
      cls := "semanticdb-document",
      b(
        "path:",
        child.text <-- elem.map(_.semanticDbUri)
      ),
      div(
        cls := "text-document-container",
        children <-- elem.map(_.documents).split(_.uri)(SemanticDB.renderTextDocument)
      )
    )

  def renderTextDocument(id: String, initial: TextDocument, elem: EventStream[TextDocument]) = 
    div(
      idAttr := id,
      cls := "text-document",
      b(
        "uri: ", 
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
      idAttr := id,
      cls := ("card", className),
      div(
        cls := "card-body",
        pre(
          child.text <-- elem.map(_.toProtoString)
        )          
      )
    )

end SemanticDB
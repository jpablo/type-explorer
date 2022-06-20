package org.jpablo.typeexplorer.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage

import scalajs.js.URIUtils.encodeURIComponent
import scala.meta.internal.semanticdb.TextDocument


object SemanticDBText:


  def renderTextDocumentsWithSource(id: String, initial: TextDocumentsWithSource, elem: EventStream[TextDocumentsWithSource]) =
    li(
      idAttr := id,
      cls := "semanticdb-document",
      b(
        child.text <-- elem.map(_.semanticDbUri)
      ),
      hr(),
      div(
        cls := "text-document-container",
        children <-- elem.map(_.documents).split(_.uri)(renderTextDocument)
      )
    )

  def renderTextDocument(id: String, initial: TextDocument, elem: EventStream[TextDocument]) =
    div(
      idAttr := id,
      cls := "text-document",
      textCard("", "card-uri", elem.map(d => s"uri: ${d.uri}")),
      div(
        cls := "symbol-information-container",
        children <-- elem.map(_.symbols.sortBy(_.symbol)).split(si => encodeURIComponent(si.symbol))(renderGeneratedMessage("card-symbol-information"))
      ),
      div(
        cls := "occurrences-container",
        children <-- elem.map(_.occurrences).split(_.symbol)(renderGeneratedMessage("card-occurrence"))
      ),
      div(
        cls := "synthetics-container",
        children <-- elem.map(_.synthetics).split(_.range.map(_.toProtoString).getOrElse(""))(renderGeneratedMessage("card-synthetic"))
      )
    )

  def renderGeneratedMessage(className: String)(id: String, initial: GeneratedMessage, elem: EventStream[GeneratedMessage]) =
    textCard(id, className, elem.map(_.toProtoString))

  def textCard(id: String, className: String, $text: EventStream[String]) =
    div(
      idAttr := id,
      cls := ("card", className),
      div(
        cls := "card-body",
        pre(
          child.text <-- $text
        )
      )
    )

end SemanticDBText

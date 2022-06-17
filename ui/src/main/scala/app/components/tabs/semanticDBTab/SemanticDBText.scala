package app.components.tabs.semanticDBTab

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Namespace
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveElement.Base
import com.raquo.airstream.core.Signal
import scalajs.js.URIUtils.encodeURIComponent
import widgets.collapsable


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


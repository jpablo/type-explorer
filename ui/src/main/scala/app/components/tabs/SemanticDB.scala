package app.components.tabs

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation, SymbolOccurrence, Synthetic}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement


def semanticDBTab(documentsWithSource: EventStream[List[TextDocumentsWithSource]]) =
  div(
    cls := "text-document-areas",
    ol(
      cls := "structure",
      children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDB.structureLevel1),
    ),
    ol(
      cls := "semanticdb-document-container",
      children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDB.renderTextDocumentsWithSource)
    )
  )


object SemanticDB:

  // -------- Structure --------------

  def structureLevel1(id: String, initial: TextDocumentsWithSource, elem: EventStream[TextDocumentsWithSource]) =
    li(
      whiteSpace := "nowrap",
      child <-- elem.map(doc => a(href := "#" + doc.semanticDbUri, doc.semanticDbUri)),
      ul(
        children <-- 
          elem.map(_.documents).split(_.uri)(structureLevel2),
      )
    )

  def structureLevel2(id: String, initial: TextDocument, elem: EventStream[TextDocument]) =
    li(
      a(cls := "bi bi-chevron-right"),
      "uri: ",
      child <-- elem.map(doc => a(href := "#" + doc.uri, doc.uri)),
      ul(
        children <-- 
          elem.map(_.symbols.sortBy(_.symbol)).split(_.symbol)(structureLevel3),
      )
    )

  def collapsable(head: HtmlElement, contentStream: EventStream[List[HtmlElement]]) =
    val open = Var[Boolean](false)
    val contents = 
      open.signal.changes
        .combineWith(contentStream)
        .map((o, content) => if o then content else List.empty)
    
    li(
      a(
        cls := "bi",
        cls <-- open.signal.map(o => if o then "bi-chevron-down" else "bi-chevron-right"),
        onClick --> { _ => open.update(v => !v) }
      ),               
      head,
      ul( children <-- contents )
    )

  def structureLevel3(id: String, initial: SymbolInformation, elem: EventStream[SymbolInformation]) =
    collapsable(
      span(child.text <-- elem.map(sym => s"${sym.kind}: ${sym.symbol}")),
      elem.map(sym =>             
        List(
          li( "kind: "  + sym.kind ),
          li( "displayName: "  + sym.displayName ),
        )
      )
    )

  // -------- protobuf text --------------

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
        children <-- elem.map(_.symbols.sortBy(_.symbol)).split(_.symbol)(renderGeneratedMessage("symbol-information"))
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
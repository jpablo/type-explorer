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
import com.raquo.laminar.nodes.ReactiveElement.Base
import com.raquo.airstream.core.Signal


def semanticDBTab(documentsWithSource: EventStream[List[TextDocumentsWithSource]]) =
  div(
    cls := "text-document-areas",
    div(
      cls := "structure",
      ol(
        children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDB.structureLevel1),
      )
    ),
    div(
      cls := "semanticdb-document-container",
      ol(
        children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDB.renderTextDocumentsWithSource)
      )
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
    val $body = elem.startWith(initial).map(_.symbols.sortBy(_.symbol)).split(_.symbol)(structureLevel3)
    val head = 
      span("uri: ", child <-- elem.map(doc => a(href := "#" + doc.uri, doc.uri) ))

    collapsable(head, $body)


  def structureLevel3(id: String, initial: SymbolInformation, elem: Signal[SymbolInformation]): ReactiveHtmlElement[LI] =
    collapsable(
      head = span(children <-- elem.map(sym =>  
          List(
            span(sym.kind.toString), 
            span(": "), 
            a(href := "#" + sym.symbol,  sym.displayName)
          )
      )),
      $body = elem.map(sym =>
        List(
          li( "kind: "  + sym.kind ),
          li( "symbol: "  + sym.symbol ),
        )
      )
    )


  def collapsable(head: HtmlElement, $body: Signal[Seq[HtmlElement]], open: Boolean = false) =
    val $open = Var(open)
    val $managedChildren = 
      $open.signal.combineWith($body).mapN((o, children) => if o then children else Seq.empty)

    li(
      cls := "collapsable",
      a(
        cls := "bi",
        cls <-- $open.signal.map(o => if o then "bi-chevron-down" else "bi-chevron-right"),
        onClick --> $open.updater((v, _) => !v)
      ),               
      head,
      ul( children <-- $managedChildren )
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

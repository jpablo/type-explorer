package app.components.tabs.semanticDBTab

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
import scalajs.js.URIUtils.encodeURIComponent
import widgets.collapsable


object SemanticDBStructureFlat:


  def structureLevel1(id: String, initial: TextDocumentsWithSource, elem: EventStream[TextDocumentsWithSource]) =
    li(
      whiteSpace := "nowrap",
      child <-- elem.map(doc => a(href := "#" + doc.semanticDbUri, doc.semanticDbUri)),
      ul(
        cls := "collapsable-wrapper",
        children <-- 
          elem.map(_.documents).split(_.uri)(structureLevel2),
      )
    )

  def structureLevel2(id: String, initial: TextDocument, elem: EventStream[TextDocument]) =
    val $body = elem.startWith(initial).map(_.symbols.sortBy(_.symbol)).split(_.symbol)(structureLevel3)
    val head = 
      span("uri: ", child <-- elem.map(doc => a(href := "#" + encodeURIComponent(doc.uri), doc.uri) ))

    collapsable(head, $body)


  def structureLevel3(id: String, initial: SymbolInformation, elem: Signal[SymbolInformation]) =
    collapsable(
      head = span(children <-- elem.map(sym =>  
          List(
            span(sym.kind.toString), 
            span(": "), 
            a(href := "#" + encodeURIComponent(sym.symbol),  sym.displayName)
          )
      )),
      $children = elem.map(sym =>
        List(
          li( "kind: "  + sym.kind ),
          li( "symbol: "  + sym.symbol ),
        )
      )
    )

end SemanticDBStructureFlat

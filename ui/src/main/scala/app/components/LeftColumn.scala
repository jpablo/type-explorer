package app.components

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream
import scala.meta.internal.semanticdb.{TextDocument, SymbolInformation}

def leftColumn(documents: EventStream[List[TextDocument]]) =
  div(
    idAttr := "te-left-column",
    cls := "col",
    div(
      cls := "list-group",
      children <-- documents.split(_.uri)(LeftColumn.renderTextDocument)
    )
  )

  
object LeftColumn:

  def renderTextDocument(id: String, initial: TextDocument, elem: EventStream[TextDocument]) = 
    div(
      cls := "list-group-item",
      b(
        child.text <-- elem.map(_.uri)
      ),
      div(
        cls := "list-group",
        children <-- elem.map(_.symbols).split(_.symbol)(renderSymbolInformation)
      )
    )

  def renderSymbolInformation(id: String, initial: SymbolInformation, elem: EventStream[SymbolInformation]) =
    div(
      cls := "list-group-item",
      div(
        cls := "card",
        div(
          cls := "card-body",
          pre(
            child.text <-- elem.map(_.toProtoString)
          )          
        )
      )
    )
  
end LeftColumn
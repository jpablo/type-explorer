package app.components.tabs.semanticDBTab

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream

import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, Synthetic, TextDocument}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveElement.Base
import com.raquo.airstream.core.Signal

import scalajs.js.URIUtils.encodeURIComponent
import app.components.tabs.semanticDBTab.SemanticDBStructureFlat
import app.components.tabs.semanticDBTab.SemanticDBText
import app.components.tabs.semanticDBTab.SemanticDBTree.*
import widgets.{Icons, collapsable}
import FileTree.*
import util.Operators.*
import scala.compiletime.ops.int.ToString
import com.raquo.airstream.state.Val
import jdk.jfr.consumer


def semanticDBTab($documents: EventStream[List[TextDocumentsWithSource]]) =
  div(
    cls := "text-document-areas",
    div(
      cls := "structure",
      div(""), // TODO: add controls to expand / collapse all
      children <-- SemanticDBTree.buildTree($documents)
//      ol(
//        children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDBStructureFlat.structureLevel1),
//      )
    ),
    div(
      cls := "semanticdb-document-container",
      ol(
        children <-- $documents.split(_.semanticDbUri)(SemanticDBText.renderTextDocumentsWithSource)
      )
    )
  )

package app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import com.raquo.airstream.core.EventStream
import org.jpablo.typeexplorer.TextDocumentsWithSource
import app.components.tabs.semanticDBTab.SemanticDBText


def semanticDBTab($documents: EventStream[List[TextDocumentsWithSource]]) =
  div(
    cls := "text-document-areas",
    div(
      cls := "structure",
      div(""), // TODO: add controls to expand / collapse all
      children <-- SemanticDBTree.buildTree($documents)
    ),
    div(
      cls := "semanticdb-document-container",
      ol(
        children <-- $documents.split(_.semanticDbUri)(SemanticDBText.renderTextDocumentsWithSource)
      )
    )
  )

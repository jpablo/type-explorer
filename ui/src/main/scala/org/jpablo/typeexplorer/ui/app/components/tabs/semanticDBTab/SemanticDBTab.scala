package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import com.raquo.airstream.core.EventStream
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.client.fetchSourceCode
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.{SemanticDBText, SemanticDBTree}
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models

def SemanticDBTab(
  $projectPath: Signal[Path], 
  $documents: EventStream[List[TextDocumentsWithSource]], 
  $selectedUri: EventBus[Path]
) =
  val $selectedSemanticDb = EventBus[Path]
  div(
    cls := "text-document-areas",

    div(
      cls := "structure",
      div(""), // TODO: add controls to expand / collapse all
      children <-- SemanticDBTree($documents, $selectedUri, $selectedSemanticDb)
    ),

    div(
      cls := "semanticdb-document-container",
      child <--
        $selectedSemanticDb.events.combineWith($documents).map { (path, documents) =>
          documents.find(_.semanticDbUri == path.toString) match
            case Some(document) => 
              SemanticDBText(document)
            case None =>
              li(s"Document not found: $path")
        }
    ),

    div(
      cls := "semanticdb-source-container",
      SourceCodeTab($selectedUri.events.flatMap(fetchSourceCode($projectPath)))
    )
  )

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
) =
  val $selectedSemanticDb = EventBus[Path]

  val $selectedDocument = 
    $selectedSemanticDb.events.combineWith($documents)
      .map { (path, documents) => 
        path -> documents.find(_.semanticDbUri == path.toString)
      }

  val $sourceCode = 
    $selectedDocument
      .collect { case (path, Some(documentsWithSource)) => documentsWithSource.documents.headOption }
      .collect { case Some(doc) => Path(doc.uri) }
      .flatMap(fetchSourceCode($projectPath))

  div(
    cls := "text-document-areas",

    div(
      cls := "structure",
      div(""), // TODO: add controls to expand / collapse all
      children <-- SemanticDBTree($documents, $selectedSemanticDb)
    ),

    div(
      cls := "semanticdb-document-container",
      child <--
        $selectedDocument.map {
          case (_, Some(document)) => SemanticDBText(document)
          case (path, None) => li(s"Document not found: $path")
        }
    ),

    div(
      cls := "semanticdb-source-container",
      SourceCodeTab($sourceCode)
    )
  )

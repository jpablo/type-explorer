package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.client.fetchSourceCode
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.{SemanticDBText, SemanticDBTree}
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.AppState


  def SemanticDBTab(
    $documents: EventStream[List[TextDocumentsWithSource]],
    $projectPath: Signal[Path]
  ) =
    val $selectedSemanticDb = EventBus[Path]

    val $selectedDocument =
      $selectedSemanticDb.events.combineWith($documents)
        .map { (path, documents: List[TextDocumentsWithSource]) =>
          path -> documents.find(_.semanticDbUri == path.toString)
        }

    val $sourceCode =
      $selectedDocument
        .collect { case (_, Some(documentsWithSource)) => documentsWithSource.documents.headOption }
        .collect { case Some(doc) => Path(doc.uri) }
        .flatMap(fetchSourceCode($projectPath))

    div(
      cls := "grid h-full grid-cols-3",

      div(
        cls := "overflow-auto h-full p-1",
        div(""), // TODO: add controls to expand / collapse all
        children <-- SemanticDBTree.build($documents, $selectedSemanticDb)
      ),

      div(
        cls := "h-full overflow-auto border-l border-slate-300",
        child <--
          $selectedDocument.map {
            case (_, Some(document)) => SemanticDBText(document)
            case (path, None) => li(s"Document not found: $path")
          }
      ),

      div(
        cls := "semanticdb-source-container h-full overflow-auto border-l border-slate-300",
        SourceCodeTab($sourceCode)
      )
    )

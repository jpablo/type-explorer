package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import com.raquo.airstream.core.EventStream
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.client.fetchSourceCode
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.{SemanticDBText, SemanticDBTree}
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.shared.models
import zio.prelude.fx.ZPure
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import com.raquo.airstream.core.Signal
import org.scalajs.dom.html
import com.raquo.laminar.nodes.ReactiveHtmlElement


def SemanticDBTab =
  for
    fetchSourceCode <- fetchSourceCode
    $documents  <- AppState.$documents
  yield
    val $selectedSemanticDb = EventBus[Path]

    val $selectedDocument =
      $selectedSemanticDb.events.combineWith($documents)
        .map { (path, documents) =>
          path -> documents.find(_.semanticDbUri == path.toString)
        }

    val $sourceCode =
      $selectedDocument
        .collect { case (_, Some(documentsWithSource)) => documentsWithSource.documents.headOption }
        .collect { case Some(doc) => Path(doc.uri) }
        .flatMap(fetchSourceCode)

    div(
      cls := "text-document-areas",

      div(
        cls := "structure overflow-auto h-full p-1",
        div(""), // TODO: add controls to expand / collapse all
        children <-- SemanticDBTree.build($documents, $selectedSemanticDb)
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

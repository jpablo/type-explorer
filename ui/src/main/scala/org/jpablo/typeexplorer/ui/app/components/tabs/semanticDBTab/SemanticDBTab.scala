package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.ui.app.client.fetchSourceCode
import org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab.{SemanticDBText, SemanticDBTree}
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.AppState


  def SemanticDBTab(
    documentsR  : EventStream[List[TextDocumentsWithSource]],
    projectPathR: Signal[List[Path]]
  ) =
    val selectedSemanticDbR = EventBus[Path]()

    val selectedDocumentR =
      selectedSemanticDbR.events.combineWith(documentsR)
        .map { (path, documents: List[TextDocumentsWithSource]) =>
          path -> documents.find(_.semanticDbUri == path.toString)
        }

    val sourceCodeR =
      selectedDocumentR
        .collect { case (_, Some(documentsWithSource)) => documentsWithSource.documents.headOption }
        .collect { case Some(doc) => Path(doc.uri) }
        .flatMap(fetchSourceCode(projectPathR.map(_.head)))

    div(
      cls := "grid h-full grid-cols-3",

      div(
        cls := "overflow-auto h-full p-1",
        div(""), // TODO: add controls to expand / collapse all
        children <-- SemanticDBTree.build(documentsR, selectedSemanticDbR)
      ),

      div(
        cls := "h-full overflow-auto border-l border-slate-300",
        child <--
          selectedDocumentR.map {
            case (_, Some(document)) => SemanticDBText(document)
            case (path, None) => li(s"Document not found: $path")
          }
      ),

      div(
        cls := "semanticdb-source-container h-full overflow-auto border-l border-slate-300",
        SourceCodeTab(sourceCodeR)
      )
    )

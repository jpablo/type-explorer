package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.ui.widgets.{Icons, Collapsable, CollapsableTree}
import scala.meta.internal.semanticdb.{SymbolInformation, TextDocument}
import scalajs.js.URIUtils.encodeURIComponent
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path
import zio.prelude.fx.ZPure
import org.jpablo.typeexplorer.ui.app.components.state.AppState

object SemanticDBTree:

  def build(
    $documents: EventStream[List[TextDocumentsWithSource]],
    $selectedSemanticDb: EventBus[Path]
  ) =
    for documentsWithSource <- $documents yield
      val $open = Var(Map.empty[String, Boolean])
      for fileTree <- Tree.fromPaths(documentsWithSource.map(buildPath)) yield
        CollapsableTree(fileTree)(
          renderNode = (b, path) => span(cls := "whitespace-nowrap", b),
          renderLeaf = renderDocWithSource($selectedSemanticDb, $open),
          initial = true,
          $open
        )

  private def buildPath(doc: TextDocumentsWithSource) =
    val all = doc.semanticDbUri.split("/").toList.filter(_.nonEmpty)
    (all.init, all.last, doc)


  private def renderDocWithSource($selectedSemanticDb: EventBus[Path], $open: Var[Map[String, Boolean]])(name: String, docWithSource: TextDocumentsWithSource) = {
    val uri = docWithSource.semanticDbUri
    Collapsable(
      nodeLabel =
        span(
          Icons.fileBinary,
          a(
            href := "#" + uri,
            onClick.preventDefault.mapTo(Path(uri)) --> $selectedSemanticDb,
            name
          )
        ),
      nodeContents = docWithSource.documents.map(renderTextDocument($open)),
      Collapsable.Control.from(uri, false, $open)
    )
  }

  private def renderTextDocument($open: Var[Map[String, Boolean]])(doc: TextDocument) =
    Collapsable(
      nodeLabel =
        span(
          Icons.fileCode,
          doc.uri
        ),
      nodeContents =
        doc.symbols.sortBy(_.symbol).map(renderSymbolInformation($open)),
      Collapsable.Control.from(doc.uri, false, $open)
    )

  private def renderSymbolInformation($open: Var[Map[String, Boolean]])(si: SymbolInformation) =
    Collapsable(
      nodeLabel =
        span(
          span(si.kind.toString),
          span(": "),
          a(href := "#" + encodeURIComponent(si.symbol), si.displayName)
        ),
      nodeContents =
        List(li("symbol: ", si.symbol)),
      Collapsable.Control.from(si.symbol, false, $open)
    )

end SemanticDBTree

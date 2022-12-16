package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.ui.widgets.{Icons, collapsable, collapsable2, collapsableTree}
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
      for fileTree <- Tree.fromPaths(documentsWithSource.map(buildPath)) yield
        collapsableTree(fileTree)(
          renderBranch = b => span(cls := "whitespace-nowrap", b),
          renderLeaf = renderDocWithSource($selectedSemanticDb)
        )

  private def buildPath(doc: TextDocumentsWithSource) =
    val all = doc.semanticDbUri.split("/").toList.filter(_.nonEmpty)
    (all.init, all.last, doc)


  private def renderDocWithSource($selectedSemanticDb: EventBus[Path])(name: String, docWithSource: TextDocumentsWithSource) =
    collapsable2(
      branchLabel =
        span(
          Icons.fileBinary,
          a(
            href := "#" + docWithSource.semanticDbUri,
            onClick.preventDefault.mapTo(Path(docWithSource.semanticDbUri)) --> $selectedSemanticDb,
            name
          )
        ),
      contents = docWithSource.documents.map(renderTextDocument),
      open = false
    )

  private def renderTextDocument(doc: TextDocument) =
    collapsable(
      branchLabel =
        span(
          Icons.fileCode,
          doc.uri
        ),
      $children =
        Signal.fromValue(doc).map(_.symbols.sortBy(_.symbol)).split(_.symbol)(renderSymbolInformation),
      open = false
    )

  private def renderSymbolInformation(id: String, initial: SymbolInformation, elem: Signal[SymbolInformation]) =
    collapsable(
      branchLabel =
        span(
          children <-- elem.map(sym =>
            List(
              span(sym.kind.toString),
              span(": "),
              a(href := "#" + encodeURIComponent(sym.symbol), sym.displayName)
            )
          )
        ),
      $children = elem.map(sym =>
        List(
          li("symbol: ", sym.symbol),
        )
      )
    )

end SemanticDBTree

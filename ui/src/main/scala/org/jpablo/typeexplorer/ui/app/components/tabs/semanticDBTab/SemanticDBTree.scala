package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.fileTree.FileTree
import org.jpablo.typeexplorer.ui.bootstrap.navbar
import org.jpablo.typeexplorer.ui.widgets.{Icons, collapsable, collapsable2, collapsableTree}
import scala.meta.internal.semanticdb.{SymbolInformation, TextDocument}
import scalajs.js.URIUtils.encodeURIComponent
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path

object SemanticDBTree:

  def build($documents: EventStream[List[TextDocumentsWithSource]], $selectedUri: EventBus[Path]): EventStream[List[HtmlElement]] =
    for documentsWithSource <- $documents yield
      for fileTree <- FileTree.build(documentsWithSource)(buildPath) yield
        collapsableTree(fileTree)(
          renderBranch = b => span(cls := "collapsable-branch-label", b),
          renderLeaf = renderDocWithSource($selectedUri)
        )

  private def buildPath(doc: TextDocumentsWithSource) =
    val all = doc.semanticDbUri.split("/").toList.filter(_.nonEmpty)
    (doc, all.last, all.init)


  private def renderDocWithSource($selectedUri: EventBus[Path])(name: String, docWithSource: TextDocumentsWithSource) =
    collapsable2(
      branchLabel =
        span(
          cls := "collapsable-leaf",
          Icons.fileBinary,
          a(href := "#" + docWithSource.semanticDbUri, name)
        ),
      contents = docWithSource.documents.map(renderTextDocument($selectedUri)),
      open = true
    )

  private def renderTextDocument($selectedUri: EventBus[Path])(doc: TextDocument) =
    collapsable(
      branchLabel =
        span(
          Icons.fileCode,
          a(
            href := "#" + encodeURIComponent(doc.uri),
            doc.uri,
            onClick.mapTo(Path(doc.uri)) --> $selectedUri
        )
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

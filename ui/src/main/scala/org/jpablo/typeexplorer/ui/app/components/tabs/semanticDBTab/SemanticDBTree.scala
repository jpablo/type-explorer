package org.jpablo.typeexplorer.ui.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.inheritance.Path
import org.jpablo.typeexplorer.ui.widgets.Icons.{fileBinaryIcon, fileCodeIcon}
import org.jpablo.typeexplorer.ui.widgets.{Collapsable, CollapsableTree, Icons}

import scala.meta.internal.semanticdb.{SymbolInformation, TextDocument}
import scala.scalajs.js.URIUtils.encodeURIComponent

object SemanticDBTree:

  def build(
    documents          : EventStream[List[TextDocumentsWithSource]],
    selectedSemanticDb : EventBus[Path]
  ) =
    for documentsWithSource <- documents yield
      val open = Var(Map.empty[String, Boolean])
      for fileTree <- Tree.fromPaths(documentsWithSource.map(buildPath)).children yield
        val mkControl = Collapsable.Control(false, open)
        CollapsableTree(fileTree)(
          renderNode = (b, path) => span(cls := "whitespace-nowrap", b),
          renderLeaf = renderDocWithSource(selectedSemanticDb, mkControl),
          mkControl
        )

  private def buildPath(doc: TextDocumentsWithSource) =
    val all = doc.semanticDbUri.split("/").toList.filter(_.nonEmpty)
    (all.init, all.last, doc)


  private def renderDocWithSource($selectedSemanticDb: EventBus[Path], mkControl: String => Collapsable.Control)(name: String, docWithSource: TextDocumentsWithSource) = {
    val uri = docWithSource.semanticDbUri
    Collapsable(
      nodeLabel =
        span(
          i.fileBinaryIcon,
          a(
            href := "#" + uri,
            onClick.preventDefault.mapTo(Path(uri)) --> $selectedSemanticDb,
            name
          )
        ),
      nodeContents = docWithSource.documents.map(renderTextDocument(mkControl)),
      mkControl(uri)
    )
  }

  private def renderTextDocument(mkControl: String => Collapsable.Control)(doc: TextDocument) =
    Collapsable(
      nodeLabel =
        span(
          i.fileCodeIcon,
          doc.uri
        ),
      nodeContents =
        doc.symbols.sortBy(_.symbol).map(renderSymbolInformation(mkControl)),
      mkControl(doc.uri)
    )

  private def renderSymbolInformation(mkControl: String => Collapsable.Control)(si: SymbolInformation) =
    Collapsable(
      nodeLabel =
        span(
          span(si.kind.toString),
          span(": "),
          a(href := "#" + encodeURIComponent(si.symbol), si.displayName)
        ),
      nodeContents =
        List(li("symbol: ", si.symbol)),
      mkControl(si.symbol)
    )

end SemanticDBTree

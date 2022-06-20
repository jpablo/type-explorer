package org.jpablo.typeexplorer.app.components.tabs.semanticDBTab

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.TextDocumentsWithSource
import org.jpablo.typeexplorer.widgets.{Icons, collapsable}
import org.jpablo.typeexplorer.bootstrap.navbar
import org.jpablo.typeexplorer.fileTree.FileTree
import scalajs.js.URIUtils.encodeURIComponent
import scala.meta.internal.semanticdb.{SymbolInformation, TextDocument}

object SemanticDBTree:

  def buildPath(doc: TextDocumentsWithSource) =
    val all = doc.semanticDbUri.split("/").toList.filter(_.nonEmpty)
    (doc, all.last, all.init)

  def buildTree($documents: EventStream[List[TextDocumentsWithSource]]): EventStream[List[HtmlElement]] =
    for documentsWithSource <- $documents yield
      for fileTree <- FileTree.build(documentsWithSource)(buildPath) yield
        fromFileTree(fileTree)(
          renderBranch = b => span(cls := "collapsable-branch-label", b),
          renderLeaf = renderDocWithSource
        )


  def fromFileTree[A]
  (t: FileTree[A])
    (
      renderBranch: String => HtmlElement,
      renderLeaf  : (String, A) => HtmlElement
    )
  : HtmlElement = t match
    case FileTree.Directory(name, files) =>
      collapsable(
        branchLabel = renderBranch(name),
        $children =
          Signal.fromValue {
            for f <- files yield
              fromFileTree(f)(renderBranch, renderLeaf)
          },
        open = true
      )
    case FileTree.File(name, data) =>
      renderLeaf(name, data)


  def renderDocWithSource(name: String, docWithSource: TextDocumentsWithSource) =
    collapsable(
      branchLabel =
        span(
          cls := "collapsable-leaf",
          Icons.fileBinary,
          a(href := "#" + docWithSource.semanticDbUri, name)
        ),
      $children =
        Signal.fromValue(docWithSource.documents.map(renderTextDocument)),
      open = true
    )

  def renderTextDocument(doc: TextDocument) =
    collapsable(
      branchLabel =
        span(Icons.fileCode, a(href := "#" + encodeURIComponent(doc.uri), doc.uri)),
      $children =
        Signal.fromValue(doc).map(_.symbols.sortBy(_.symbol)).split(_.symbol)(renderSymbolInformation),
      open = false
    )

  def renderSymbolInformation(id: String, initial: SymbolInformation, elem: Signal[SymbolInformation]) =
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

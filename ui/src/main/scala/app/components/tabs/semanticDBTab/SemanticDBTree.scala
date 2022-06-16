package app.components.tabs.semanticDBTab

import app.components.tabs.semanticDBTab.FileTree.*
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.TextDocumentsWithSource
import widgets.{Icons, collapsable}
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, Synthetic, TextDocument}
import scala.scalajs.js.URIUtils.encodeURIComponent


object SemanticDBTree:

  def buildTree($documents: EventStream[List[TextDocumentsWithSource]]): EventStream[List[HtmlElement]] =
    for documentsWithSource <- $documents yield
      for fileTree <- FileTree.build(documentsWithSource)(_.semanticDbUri) yield
        fromFileTree(fileTree)(
          renderBranch = b =>
            span(cls := "collapsable-branch-label", b),
          renderLeaf = renderDocWithSource
        )


  def fromFileTree[A]
    (t: FileTree[A])
    (
      renderBranch: String => HtmlElement,
      renderLeaf: (String, A) => HtmlElement
    )
  : HtmlElement = t match
    case Directory(name, files) =>
      collapsable(
        branchLabel = renderBranch(name),
        $children =
          Signal.fromValue {
            for f <- files yield
              fromFileTree(f)(renderBranch, renderLeaf)
          },
        open = true
      )
    case File(name, data) =>
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
              a(href := "#" + encodeURIComponent(sym.symbol),  sym.displayName)
            )
          )
        ),
      $children = elem.map(sym =>
        List(
          li( "symbol: ", sym.symbol),
        )
      )
    )

end SemanticDBTree

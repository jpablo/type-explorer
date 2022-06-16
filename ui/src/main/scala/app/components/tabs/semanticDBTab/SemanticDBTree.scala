package app.components.tabs.semanticDBTab

import app.components.tabs.semanticDBTab.FileTree.*
import app.components.tabs.semanticDBTab.{SemanticDBStructureFlat, SemanticDBText}
import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.airstream.state.Val
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveElement.Base
import com.raquo.laminar.nodes.ReactiveHtmlElement
import jdk.jfr.consumer
import models.Type
import org.jpablo.typeexplorer.TextDocumentsWithSource
import org.scalajs.dom.html.LI
import scalapb.GeneratedMessage
import util.Operators.*
import widgets.{Icons, collapsable}

import scala.compiletime.ops.int.ToString
import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, Synthetic, TextDocument}
import scala.scalajs.js.URIUtils.encodeURIComponent


object SemanticDBTree:

  def buildTree($documents: EventStream[List[TextDocumentsWithSource]]): EventStream[List[HtmlElement]] =
    for documentsWithSource <- $documents yield
      for fileTree <- FileTree.build(documentsWithSource)(_.semanticDbUri) yield
        fromFileTree(fileTree)(
          renderBranch =
            b =>
              span(cls := "collapsable-branch-label", b),

          renderLeaf =
            (name, docWithSource: TextDocumentsWithSource) =>
              collapsable(
                branchLabel =
                  span(cls := "collapsable-leaf", Icons.fileBinary, a(href := "#" + docWithSource.semanticDbUri, name)),

                $children =
                  Signal.fromValue {
                    for doc <- docWithSource.documents yield
                      collapsable(
                        branchLabel = 
                          span("uri: ", a(href := "#" + encodeURIComponent(doc.uri), doc.uri)),

                        $children =
                          Signal.fromValue(doc).map(_.symbols.sortBy(_.symbol)).split(_.symbol)(renderSymbolInformation),

                        open = true
                      )
                  },
                open = true
              )
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
          li( "kind: "  + sym.kind ),
          li( "symbol: "  + sym.symbol ),
        )
      )
    )

end SemanticDBTree

package app.components.tabs.semanticDBTab

import bootstrap.Accordion.{`accordion-flush`, accordion, open}
import com.raquo.laminar.api.L.*
import models.Type
import com.raquo.airstream.core.EventStream

import scala.meta.internal.semanticdb.{SymbolInformation, SymbolOccurrence, Synthetic, TextDocument}
import org.jpablo.typeexplorer.TextDocumentsWithSource
import scalapb.GeneratedMessage
import org.scalajs.dom.html.LI
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.raquo.laminar.nodes.ReactiveElement.Base
import com.raquo.airstream.core.Signal

import scalajs.js.URIUtils.encodeURIComponent
import app.components.tabs.semanticDBTab.SemanticDBStructureFlat
import app.components.tabs.semanticDBTab.SemanticDBText
import widgets.{Icons, collapsable}
import FileTree.*
import util.Operators.*

import scala.compiletime.ops.int.ToString

val tree =
  Directory("Users", List(
    Directory("jpablo",
      List.tabulate(10)(i => File(s"file$i.semanticdb", i))
    ),
    Directory("other_dir", List(
      Directory.apply("nested", List(
        File("file3.semanticdb", 3)
      )),
    )),
  ))


def renderTree[A]
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
            renderTree(f)(renderBranch, renderLeaf)
        },
      open = true
    )
  case File(name, data) =>
    renderLeaf(name, data)


def semanticDBTab($documents: EventStream[List[TextDocumentsWithSource]]) =
  val $tree =
    for documents <- $documents yield
      for fileTree <- FileTree.build(documents)(_.semanticDbUri) yield
        renderTree(fileTree)(
          renderBranch = b =>
            span(cls := "collapsable-branch-label", b),
          renderLeaf = (name, doc) =>
            span(
              cls := "collapsable-leaf",
              Icons.fileBinary,
              a(href := "#" + doc.semanticDbUri, name)
            )
        )

  div(
    cls := "text-document-areas",
    div(
      cls := "structure",
      div(""), // TODO: add controls to expand / collapse all
      children <-- $tree
//      ol(
//        children <-- documentsWithSource.split(_.semanticDbUri)(SemanticDBStructureFlat.structureLevel1),
//      )
    ),
    div(
      cls := "semanticdb-document-container",
      ol(
        children <-- $documents.split(_.semanticDbUri)(SemanticDBText.renderTextDocumentsWithSource)
      )
    )
  )

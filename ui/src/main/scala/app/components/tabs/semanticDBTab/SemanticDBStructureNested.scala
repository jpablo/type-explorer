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
import widgets.collapsable

import scala.collection.immutable


enum FileTree[+A]:
  case Directory(name: String, files: Seq[FileTree[A]])
  case File(name: String, data: A)

  def name: String

object FileTree:
  type Pairs = List[(List[String], TextDocumentsWithSource)]

  def fromTextDocuments(documentsWithSource: List[TextDocumentsWithSource]): FileTree[TextDocumentsWithSource] =
    val pairs =
      for doc <- documentsWithSource yield
        (doc.semanticDbUri.split("/").toList.filter(_.nonEmpty), doc)

//    go(List())
//
//    go(List(
//      List("a", "b.txt") -> ???,  // groupBy
//      List("a", "c.txt") -> ???,  // groupBy
//      List("d.txt")      -> ???,  // ?
//      List()             -> ???,  // ignore
//    ))

    List(List("a", "b.txt"), List("a", "c.txt"), List("d.txt")).groupBy(_.head)
    Map(
      "d.txt" -> Seq(List("d.txt") -> 1),
      "a"     -> Seq(List("a", "b.txt") -> 2, List("a", "c.txt") -> 3),
      "e"     -> Seq(List("e", "f.txt") -> 4)
    )

    Map(
      "d.txt" -> Seq(List() -> 1),
      "a"     -> Seq(List("b.txt") -> 2, List("c.txt") -> 3),
      "e"     -> Seq(List("f.txt") -> 4)
    )

    def go(pairs: Pairs): Seq[FileTree[TextDocumentsWithSource]] =
      val tails: Map[String, Pairs] =
        pairs.groupBy(_._1.head).transform((_, groups) => groups.map { case (ss, d) => ss.tail -> d})

      tails
        .map { case (head, groups: List[(List[String], TextDocumentsWithSource)]) =>
          groups match
            case (Nil -> doc) :: Nil => File(head, doc)
            case _ => Directory(head, go(groups))
        }.toSeq

    Directory("/", go(pairs))




object SemanticDBStructureNested:


  def structureLevel0(documentsWithSource:List[TextDocumentsWithSource]) =
    documentsWithSource.map(_.semanticDbUri.split("/").toList)










  def structureLevel1(id: String, initial: TextDocumentsWithSource, elem: EventStream[TextDocumentsWithSource]) =
    val head = 
      span(child <-- elem.map(doc => a(href := "#" + encodeURIComponent(doc.semanticDbUri), doc.semanticDbUri) ))


    li(
      whiteSpace := "nowrap",
      child <-- elem.map(doc => a(href := "#" + doc.semanticDbUri, doc.semanticDbUri)),
      ul(
        cls := "collapsable-wrapper",
        children <-- 
          elem.map(_.documents).split(_.uri)(structureLevel2),
      )
    )

  def structureLevel2(id: String, initial: TextDocument, elem: EventStream[TextDocument]) =
    val $body = elem.startWith(initial).map(_.symbols.sortBy(_.symbol)).split(_.symbol)(structureLevel3)
    val head = 
      span("uri: ", child <-- elem.map(doc => a(href := "#" + encodeURIComponent(doc.uri), doc.uri) ))

    collapsable(head, $body)


  def structureLevel3(id: String, initial: SymbolInformation, elem: Signal[SymbolInformation]) =
    collapsable(
      head = span(children <-- elem.map(sym =>  
          List(
            span(sym.kind.toString), 
            span(": "), 
            a(href := "#" + encodeURIComponent(sym.symbol),  sym.displayName)
          )
      )),
      $children = elem.map(sym =>
        List(
          li( "kind: "  + sym.kind ),
          li( "symbol: "  + sym.symbol ),
        )
      )
    )

end SemanticDBStructureNested

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


type Name = String

enum FileTree[+A]:
  case Directory(name: Name, contents: List[FileTree[A]])
  case File(name: Name, data: A)
  def name: Name

object FileTree:

  type Segments[A] = List[(A, List[Name])]

  def build[A](files: List[A])(getPath: A => String): List[FileTree[A]] =
    buildRec(buildSegments(files, getPath))

  // -----------------------

  private def buildRec[A](segments: Segments[A]): List[FileTree[A]] =
    val tails =
      segments
        .groupBy(_._2.head)
        .transform((_, groups) => dropHeads(groups))
    for (name, nextSegments) <- tails.toList yield nextSegments match
      case (doc -> Nil) :: Nil => File(name, doc)
      case _ => buildRec(nextSegments) match
        // join directories with a single directory child
        case Directory(nextName, nextContents) :: Nil => Directory(name + "/" + nextName, nextContents)
        case contents                                 => Directory(name, contents)

  private def buildSegments[A](files: List[A], getPath: A => String): Segments[A] =
    for
      file <- files
      path = getPath(file)
      segments = path.split("/").toList.filter(_.nonEmpty)
      withRoot =
        if path.startsWith("/")
        then ("/" + segments.head) :: segments.tail
        else segments
    yield
      (file, withRoot)


  private def dropHeads[A](segments: Segments[A]): Segments[A] =
    for case (file, _ :: next) <- segments yield
      file -> next

object SemanticDBStructureNested:


  def structureLevel0(documentsWithSource:List[TextDocumentsWithSource]) =
    documentsWithSource.map(_.semanticDbUri.split("/").toList)


end SemanticDBStructureNested

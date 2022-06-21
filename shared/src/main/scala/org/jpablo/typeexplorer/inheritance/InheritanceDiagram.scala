package org.jpablo.typeexplorer.inheritance

import org.jpablo.typeexplorer.fileTree.FileTree
import org.jpablo.typeexplorer.models.{Namespace, NamespaceKind, Symbol}
import zio.json.*
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import org.jpablo.typeexplorer.models.{Method, Namespace, NamespaceKind, Symbol}
import scala.meta.internal.semanticdb

case class InheritanceDiagram(
  arrows    : List[(Symbol, Symbol)],
  namespaces: List[Namespace] = List.empty,
):
//  lazy val bySymbol: Map[Symbol, Namespace] =
//    namespaces.groupMapReduce(_.symbol)(identity)((a,_) => a)
//
//  lazy val pairsFull: List[(Namespace, Namespace)] =
//    pairs.map { case (a: Symbol, b: Symbol) =>
//      val na = bySymbol.getOrElse(a, Namespace(a, a.toString, NamespaceKind.Unknown))
//      val nb = bySymbol.getOrElse(b, Namespace(b, b.toString, NamespaceKind.Unknown))
//      na -> nb
//    }

  lazy val toFileTrees: List[FileTree[Namespace]] =
    FileTree.build(namespaces, ".") { ns =>
      (ns, ns.displayName, ns.symbol.toString.split("/").init.toList)
    }

object InheritanceDiagram:
  given JsonEncoder[InheritanceDiagram] = DeriveJsonEncoder.gen
  given JsonDecoder[InheritanceDiagram] = DeriveJsonDecoder.gen

  def fromTextDocuments(textDocuments: TextDocuments): InheritanceDiagram =
    val allSymbols: Map[Symbol, SymbolInformation] =
      textDocuments.documents
        .flatMap(_.symbols).distinct
        .sortBy(_.symbol)
        .map(s => Symbol(s.symbol) -> s)
        .toMap

    val namespaces =
      for
        (symbol, symbolInfo) <- allSymbols
        signature    <- symbolInfo.signature.asNonEmpty.toSeq
        clsSignature <- signature match
          case cs: ClassSignature => List(cs)
          case _ =>  List.empty
        nsKind       = translateKind(symbolInfo.kind)
        declarations = clsSignature.declarations.map(_.symlinks.map(Symbol(_))).toSeq.flatten
        methods      = for decl <- declarations yield Method(decl, allSymbols.get(decl).map(_.displayName).getOrElse(decl.toString), None)
        namespace    =
          Namespace(
            symbol      = symbol,
            displayName = symbolInfo.displayName,
            kind        = nsKind,
            methods     = methods.toList.sortBy(_.displayName)
          )
      yield
        namespace -> clsSignature.parents

    val arrows =
      for
        (ns, parents) <- namespaces
        parent        <- parents
        parentType    <- parent.asNonEmpty.toSeq
        parentSymbol  <- parentType match
          case tr: TypeRef => List(Symbol(tr.symbol))
          case _ => List.empty
      yield
        ns.symbol -> parentSymbol

    InheritanceDiagram(
      arrows     = arrows.toList,
      namespaces = namespaces.keys.toList
    )

  end fromTextDocuments

  private def translateKind(kind: Kind) = kind match
    case Kind.OBJECT         => NamespaceKind.Object
    case Kind.PACKAGE        => NamespaceKind.Package
    case Kind.PACKAGE_OBJECT => NamespaceKind.PackageObject
    case Kind.CLASS          => NamespaceKind.Class
    case Kind.TRAIT          => NamespaceKind.Trait
    case other               => NamespaceKind.Other(other.toString)


end InheritanceDiagram


//@main
//def testScanClasses(): Unit =
//  import java.net.URI
//  import java.nio.file.Path
//  import java.nio.file.Paths
//  import org.jpablo.typeexplorer.semanticdb.All
//
//  val paths = Paths.get(URI("file:///Users/jpablo/proyectos/playground/type-explorer/.type-explorer/"))
//  val textDocuments = TextDocuments(All.scan(paths).flatMap(_._2.documents))
//  val diagram = InheritanceDiagram.fromTextDocuments(textDocuments)
//  diagram.namespaces.foreach(println)

package semanticdb

import inheritance.InheritanceDiagram

import java.nio.file.Path
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import java.nio.file.Paths
import java.net.URI
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import models.*

object ClassesList:

  private def translateKind(kind: Kind) = kind match
    case Kind.OBJECT         => NamespaceKind.Object
    case Kind.PACKAGE        => NamespaceKind.Package
    case Kind.PACKAGE_OBJECT => NamespaceKind.PackageObject
    case Kind.CLASS          => NamespaceKind.Class
    case Kind.TRAIT          => NamespaceKind.Trait
    case other               => NamespaceKind.Other(other.toString)

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

    val pairs =
      for
        (ns, parents) <- namespaces
        parent       <- parents
        parentType   <- parent.asNonEmpty.toSeq
        parentSymbol <- parentType match
          case tr: TypeRef => List(Symbol(tr.symbol))
          case _ => List.empty
      yield
        ns.symbol -> parentSymbol

    InheritanceDiagram(
      pairs = pairs.toList,
      namespaces = namespaces.keys.toList
    )

  end fromTextDocuments

  def scan(p: Path): List[Namespace] =
    val kinds = Set(Kind.OBJECT, Kind.CLASS, Kind.TRAIT)
    val symbols =
      collection.mutable.ArrayBuffer.empty[SymbolInformation]
    // --- load all documents ----
    semanticdb.Locator(p) { (_, textDocuments: TextDocuments) =>
      for
        document <- textDocuments.documents
        symbol <- document.symbols
//        k: Kind = symbol.kind
//        if kinds contains ??? //symbol.kind
      do
        symbols += symbol
    }
    // --------------------------

    symbols.toList.map { (symbol: SymbolInformation) =>
      val symlinks =
        for
          sig <- symbol.signature.asNonEmpty.toList
          scope <- sig match
            case cs: ClassSignature => cs.declarations.toList
            case _ => List.empty
          symlink <- scope.symlinks
        yield
          symlink

      val methods =
        symlinks.map { fullName =>
          val parts = fullName.split("""\.""")
          val name = if parts.length > 1 then parts(1) else ""
          Method(name)
        }

      Namespace(
        symbol = Symbol(symbol.symbol),
        displayName = symbol.displayName,
        methods = methods
      )
    }
  end scan

end ClassesList


@main
def testScanClasses(): Unit =
  val paths = Paths.get(URI("file:///Users/jpablo/proyectos/playground/type-explorer/.type-explorer/"))
  val textDocuments = TextDocuments(All.scan(paths).flatMap(_._2.documents))
  val diagram = ClassesList.fromTextDocuments(textDocuments)
  diagram.namespaces.foreach(println)

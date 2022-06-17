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

  def fromTextDocuments(textDocuments: TextDocuments): InheritanceDiagram =
    val allSymbols =
      textDocuments.documents.flatMap(_.symbols).distinct

    val symbolsData: Seq[(Kind, Symbol, String, Seq[Type], Seq[Symbol])] =
      for
        symbol       <- allSymbols
        signature    <- symbol.signature.asNonEmpty.toSeq
        clsSignature <- signature match
          case cs: ClassSignature => List(cs)
          case _ =>  List.empty
      yield
        (symbol.kind, Symbol(symbol.symbol), symbol.displayName, clsSignature.parents, clsSignature.declarations.map(_.symlinks.map(Symbol(_))).toSeq.flatten)

    val declarations =
      for
        (kind, symbol, displayName, parents, declarations) <- symbolsData
        nsKind = kind match
          case Kind.OBJECT         => NamespaceKind.Object
          case Kind.PACKAGE        => NamespaceKind.Package
          case Kind.PACKAGE_OBJECT => NamespaceKind.PackageObject
          case Kind.CLASS          => NamespaceKind.Class
          case Kind.TRAIT          => NamespaceKind.Trait
          case other               => NamespaceKind.Other(other.toString)
        methods = declarations.map(sym => Method(sym, sym.toString, None)).toList
      yield
        symbol -> Namespace(symbol, displayName, nsKind, Some(symbol.pkg), methods)

    val pairs =
      for
        (_, symbol, displayName, parents, _) <- symbolsData
        parent       <- parents
        parentType   <- parent.asNonEmpty.toSeq
        parentSymbol <- parentType match
          case TypeRef(prefix, symbol, typeArguments) => List(Symbol(symbol))
          case _ => List.empty
      yield
        symbol -> parentSymbol

    InheritanceDiagram(
      pairs.toList,
      declarations.map(_._2).toList
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
        pkg = Some(Package(symbol.symbol.split("/").init.mkString("/"))),
        methods = methods
      )
    }
  end scan

end ClassesList


@main
def testScanClasses =
  val paths = Paths.get(URI("file:///Users/jpablo/proyectos/playground/type-explorer/.type-explorer/"))
  val textDocuments = TextDocuments(All.scan(paths).flatMap(_._2.documents))
  val diagram = ClassesList.fromTextDocuments(textDocuments)
  diagram.namespaces.foreach(println)

package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.shared.fileTree.FileTree
import zio.json.*
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, Symbol}
import scala.meta.internal.semanticdb
import java.util.jar.Attributes.Name


enum Related:
  case Parents, Children

import Related.*

type Arrow = (Symbol, Symbol)

/**
  * A simplified representation of entities and subtype relationships
  *
  * @param arrows An pair `(sym1, sym2)` means that `sym1` is a subtype of `sym2`
  * @param namespaces Classes, Objects, Traits, etc
  */
case class InheritanceDiagram(
  arrows    : List[Arrow],
  namespaces: List[Namespace] = List.empty,
):

  def findParents(symbols: List[Symbol]): List[Arrow] =

    val allDirectParents: Map[Symbol, List[Symbol]] =
      arrows.groupBy(_._1).transform((_, ss) => ss.map(_._2))

    def go(symbols: List[Symbol], pending: List[(List[Symbol], List[Arrow])], visited: Set[Symbol], acc: (Set[Symbol], List[Arrow])): (Set[Symbol], List[Arrow]) =
      val visited1 = visited ++ symbols.toSet

      val newGroups = 
        symbols.map { symbol =>
          val parents = allDirectParents.getOrElse(symbol, List.empty)
          (parents, parents.map(p => symbol -> p))
        }

      (newGroups ++ pending) match
        case Nil => acc
        case (parents, arrows) :: pending1 =>
          val (newVisited, newArrows) = acc
          go(
            symbols = parents.filterNot(visited1.contains), 
            pending = pending1, 
            visited = visited1, 
            acc     = (newVisited ++ visited1, newArrows ++ arrows)
          )

    go(symbols, List.empty, Set.empty, (Set.empty, List.empty))._2


  lazy val toFileTrees: List[FileTree[Namespace]] =
    FileTree.build(namespaces, ".") { ns =>
      (ns, ns.displayName, ns.symbol.toString.split("/").init.toList)
    }

  def ++ (other: InheritanceDiagram): InheritanceDiagram =
    InheritanceDiagram(
      arrows = (arrows ++ other.arrows).distinct,
      namespaces = (namespaces ++ other.namespaces).distinct
    )

  def filterSymbol(symbol: Symbol, related: Set[Related]): InheritanceDiagram =
    def toPredicate(symbol: Symbol, relation: Related)(candidate: Symbol): Boolean =
      relation match
        case Parents  => arrows.contains((symbol, candidate))
        case Children => arrows.contains((candidate, symbol))
    val predicate =
      related.foldLeft((_: Symbol) == symbol)((acc, rel) => sym => toPredicate(symbol, rel)(sym) || acc(sym))

    InheritanceDiagram(List.empty, namespaces.filter(ns => predicate(ns.symbol)))


  def filterSymbols(symbols: List[(Symbol, Set[Related])]): InheritanceDiagram =
    symbols.map(filterSymbol).foldLeft(InheritanceDiagram.empty)( _ ++ _)


end InheritanceDiagram



object InheritanceDiagram:

  given JsonEncoder[InheritanceDiagram] = DeriveJsonEncoder.gen
  given JsonDecoder[InheritanceDiagram] = DeriveJsonDecoder.gen

  // In Scala 3.2 the type annotation is needed (TODO: report bug)
  val empty: InheritanceDiagram = new InheritanceDiagram(List.empty)

  def fromTextDocuments(textDocuments: TextDocuments): InheritanceDiagram =
    val allSymbols: Map[Symbol, SymbolInformation] =
      textDocuments.documents
        .flatMap(_.symbols)
        .distinct
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
        methods      = declarations.map(method(allSymbols))
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
        (ns, parents) <- namespaces.toSeq
        parent        <- parents
        parentType    <- parent.asNonEmpty.toSeq
        parentSymbol  <- parentType match
          case tr: TypeRef => List(Symbol(tr.symbol))
          case _ => List.empty
      yield
        ns.symbol -> parentSymbol

    InheritanceDiagram(
      arrows     = arrows.toList,
      namespaces = namespaces.keys.toList ++ missingSymbols(arrows.map(_._2).toList, allSymbols)
    )

  end fromTextDocuments

  private def missingSymbols(parents: List[Symbol], allSymbols: Map[Symbol, SymbolInformation]) =
    for
      to <- parents.distinct if ! (allSymbols contains to)
    yield
      val last = to.toString.split("/").last
      val kind =
        if last.endsWith("#") then
          NamespaceKind.Class
        else if last.endsWith(".") then
          NamespaceKind.Object
        else
          NamespaceKind.Unknown
      Namespace(
        symbol = to,
        displayName = last.replace(".", "").replace("#", ""),
        kind = kind
      )


  private def translateKind(kind: Kind) = kind match
    case Kind.OBJECT         => NamespaceKind.Object
    case Kind.PACKAGE        => NamespaceKind.Package
    case Kind.PACKAGE_OBJECT => NamespaceKind.PackageObject
    case Kind.CLASS          => NamespaceKind.Class
    case Kind.TRAIT          => NamespaceKind.Trait
    case other               => NamespaceKind.Other(other.toString)


  private def method(allSymbols: Map[Symbol, SymbolInformation])(decl: Symbol) =
    Method(
      symbol      = decl,
      displayName = allSymbols.get(decl).map(_.displayName).getOrElse(missingMethodName(decl)),
      returnType  = None
    )

  private def missingMethodName(s: Symbol) =
    val str = s.toString
    if str.endsWith("`<init>`().") then
      "<init>"
    else if str.endsWith("#main().") then
      "main()"
    else
      str

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

package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.shared.fileTree.FileTree
import zio.json.*
import zio.Chunk

import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, Symbol}

import scala.meta.internal.semanticdb
import java.util.jar.Attributes.Name
import scala.annotation.tailrec


enum Related:
  case Parents, Children

import Related.*

type Arrow = (Symbol, Symbol)

/**
  * A simplified representation of entities and subtype relationships
  *
  * @param arrows A pair `(sym1, sym2)` means that `sym1` is a subtype of `sym2`
  * @param namespaces Classes, Objects, Traits, etc
  */
case class InheritanceDiagram(
  arrows    : List[Arrow],
  namespaces: List[Namespace] = List.empty,
):

  lazy val directParents: Map[Symbol, List[Symbol]] =
    arrows.groupBy(_._1)
    .transform((_, ss) => ss.map(_._2))
    .withDefaultValue(List.empty)

  lazy val directChildren: Map[Symbol, List[Symbol]] =
    arrows.groupBy(_._2)
    .transform((_, ss) => ss.map(_._1))
    .withDefaultValue(List.empty)

  /**
    * Follow all arrows related to the given symbols.
    */
  def findRelated(symbols: List[Symbol], related: Related): InheritanceDiagram =
    /**
      * Symbols with out or in-arrows. A group refers to the symbols directly related to a given symbol.
      * - out-arrows correspond to parents (supertypes)
      * - in-arrows correspond to children (subtypes)
      */
    type SymbolGroup = (Set[Symbol], Chunk[Arrow])

    /**
      * symbols -> pending -> acc
      */
    @tailrec
    def go(symbols: Chunk[Symbol], pending: Chunk[SymbolGroup], visited: Set[Symbol], acc: SymbolGroup, related: Related): SymbolGroup =
      // first collect all directly related symbols + arrows.
      val newGroups: Chunk[SymbolGroup] =
        val parentRequested = related == Parents
        for symbol <- symbols yield
          val newSymbols = if parentRequested then directParents(symbol) else directChildren(symbol)
          val arrows  = Chunk.fromIterable(newSymbols.map(s => if parentRequested then symbol -> s else s -> symbol))
          (newSymbols.toSet, arrows)

      // combine newly discovered groups with existing groups
      val allGroups = newGroups ++ pending

      if allGroups.isEmpty then
        acc
      else
        val (newSymbols, newArrows) = allGroups.head
        val (accVisited, accArrows) = acc
        val newVisited = visited ++ symbols.toSet
        go(
          symbols = Chunk.fromIterable(newSymbols -- newVisited),
          pending = allGroups.tail,
          visited = newVisited,
          acc     = (accVisited ++ newVisited, accArrows ++ newArrows),
          related = related
        )

    val (foundSymbols, arrows) = 
      go(Chunk.fromIterable(symbols), Chunk.empty, Set.empty, (Set.empty, Chunk.empty), related)

    InheritanceDiagram(arrows.toList, namespaces.filter(ns => foundSymbols.contains(ns.symbol)))


  lazy val toFileTrees: List[FileTree[Namespace]] =
    FileTree.build(namespaces, ".") { ns =>
      (ns, ns.displayName, ns.symbol.toString.split("/").init.toList)
    }

  def ++ (other: InheritanceDiagram): InheritanceDiagram =
    InheritanceDiagram(
      arrows = (arrows ++ other.arrows).distinct,
      namespaces = (namespaces ++ other.namespaces).distinct
    )

  def filterSymbol(symbol: Symbol, related: Set[Related]): InheritanceDiagram = related.toList match
    case Nil =>
      InheritanceDiagram(List.empty, namespaces.filter(_.symbol == symbol))
    case r :: Nil =>
      findRelated(List(symbol), r)
    case r1 :: r2 :: Nil => 
      findRelated(List(symbol), r1) ++ findRelated(List(symbol), r2)
    case _ => throw new Exception("Error found")


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

package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.protos.TextDocumentsWithSourceSeq
import org.jpablo.typeexplorer.shared.models.Method
import org.jpablo.typeexplorer.shared.models.Namespace
import org.jpablo.typeexplorer.shared.models.NamespaceKind
import org.jpablo.typeexplorer.shared.models.GraphSymbol
import org.jpablo.typeexplorer.shared.models.SymbolRange
import org.jpablo.typeexplorer.shared.tree.Tree
import zio.json.*
import zio.prelude.Commutative
import zio.prelude.Identity

import scala.annotation.targetName
import scala.meta.internal.semanticdb
import scala.meta.internal.semanticdb.ClassSignature
import scala.meta.internal.semanticdb.Signature
import scala.meta.internal.semanticdb.SymbolInformation
import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.SymbolOccurrence
import scala.meta.internal.semanticdb.Type
import scala.meta.internal.semanticdb.TypeRef

type Arrow = (GraphSymbol, GraphSymbol)

/** A simplified representation of entities and subtype relationships
  *
  * @param arrows A pair `(a, b)` means that `a` is a subtype of `b`
  * @param namespaces Classes, Objects, Traits, etc
  */
case class InheritanceGraph(
  arrows    : Set[Arrow],
  namespaces: Set[Namespace] = Set.empty,
) derives JsonCodec:
  lazy val symbols =
    namespaces.map(_.symbol)

  lazy val directParents: GraphSymbol => Set[GraphSymbol] =
    arrows
      .groupBy(_._1)
      .transform((_, ss) => ss.map(_._2))
      .withDefaultValue(Set.empty)

  private lazy val directChildren: GraphSymbol => Set[GraphSymbol] =
    arrows
      .groupBy(_._2)
      .transform((_, ss) => ss.map(_._1))
      .withDefaultValue(Set.empty)

  lazy val nsBySymbol: Map[GraphSymbol, Namespace] =
    namespaces.groupMapReduce(_.symbol)(identity)((_, b) => b)

  private lazy val nsByKind: Map[NamespaceKind, Set[Namespace]] =
    namespaces.groupBy(_.kind)

  // /**
  //   * Follow all arrows related to the given symbol.
  //   */
  // def findRelated1(symbol: Symbol, related: Related): InheritanceDiagram =
  //   @tailrec
  //   def go(symbol: Symbol, related: Related, pending: Set[Symbol], visited: Set[Symbol], arrows: Chunk[Arrow]): (Set[Symbol], Chunk[Arrow]) =
  //     assume(!pending.contains(symbol))
  //     assume(!visited.contains(symbol))
  //     assume(pending.intersect(visited).isEmpty)

  //     val newSymbols = directRelatives(related)(symbol).toSet
  //     assert(!newSymbols.contains(symbol))
  //     // Note: Arrows can come from previously visited symbols, so we collect them before removing visited symbols below
  //     val newArrows = Chunk.from(newSymbols.map(s => if related == Parents then symbol -> s else s -> symbol))
  //     val pending1 = pending ++ (newSymbols -- visited)
  //     assert(!pending1.contains(symbol))
  //     val visited1 = visited + symbol
  //     val arrows1  = arrows ++ newArrows

  //     if pending1.isEmpty then
  //       (visited1, arrows1)
  //     else
  //       go(
  //         symbol  = pending1.head,
  //         related = related,
  //         pending = pending1.tail,
  //         visited = visited1,
  //         arrows  = arrows1
  //       )

  //   val (foundSymbols, arrows) =
  //     go(symbol, related, Set.empty, Set.empty, Chunk.empty)

  //   assert(foundSymbols contains symbol)
  //   InheritanceDiagram(arrows.toSet, namespaces.filter(ns => foundSymbols.contains(ns.symbol)))

  // ---------------------------------------------

  private def arrowsForSymbols(symbols: Set[GraphSymbol]) =
    for
      arrow @ (a, b) <- arrows
      if (symbols contains a) && (symbols contains b)
    yield arrow

  /** Creates a diagram containing the given symbols and the arrows between them.
    */
  def subdiagram(symbols: Set[GraphSymbol]): InheritanceGraph =
    val foundSymbols = nsBySymbol.keySet.intersect(symbols)
    val foundNS      = foundSymbols.map(nsBySymbol)
    InheritanceGraph(arrowsForSymbols(foundSymbols), foundNS)

  def subdiagramByKinds(kinds: Set[NamespaceKind]): InheritanceGraph =
    val foundKinds  = nsByKind.filter((kind, _) => kinds.contains(kind))
    val foundNS     = foundKinds.values.flatten.toSet
    InheritanceGraph(arrowsForSymbols(foundNS.map(_.symbol)), foundNS)

  // Note: doesn't handle loops.
  // How efficient is this compared to the tail rec version above?
  def unfold(symbols: Set[GraphSymbol], related: GraphSymbol => Set[GraphSymbol]): Set[GraphSymbol] =
    Set.unfold(symbols) { ss =>
      val ss2 = ss.flatMap(related)
      if ss2.isEmpty then None else Some((ss2, ss2))
    }.flatten


  private def allRelated(ss: Set[GraphSymbol], r: GraphSymbol => Set[GraphSymbol]): InheritanceGraph =
    subdiagram(unfold(ss, r) ++ ss)

  def parentsOfAll(symbols: Set[GraphSymbol]): InheritanceGraph = allRelated(symbols, directParents)
  def childrenOfAll(symbols: Set[GraphSymbol]): InheritanceGraph = allRelated(symbols, directChildren)

  def parentsOf(symbol: GraphSymbol): InheritanceGraph = allRelated(Set(symbol), directParents)
  def childrenOf(symbol: GraphSymbol): InheritanceGraph = allRelated(Set(symbol), directChildren)


  lazy val toTrees: Tree[Namespace] =
    val paths =
      for ns <- namespaces.toList yield
        (ns.symbol.toString.split("/").init.toList, ns.displayName, ns)
    Tree.fromPaths(paths, ".")

  /** Combines the diagram on the left with the diagram on the right.
    * No new arrows are introduced beyond those present in both diagrams.
    */
  @targetName("combine")
  def ++ (other: InheritanceGraph): InheritanceGraph =
    InheritanceGraph(
      arrows = arrows ++ other.arrows,
      namespaces = namespaces ++ other.namespaces
    )

  /** Creates a new subdiagram with all the symbols containing the given String.
    */
  def filterBySymbolName(str: String): InheritanceGraph =
    subdiagram(symbols.filter(_.toString.toLowerCase.contains(str.toLowerCase)))

  def filterBy(p: Namespace => Boolean): InheritanceGraph =
    subdiagram(namespaces.filter(p).map(_.symbol))

end InheritanceGraph

object InheritanceGraph:

  given Commutative[InheritanceGraph] with Identity[InheritanceGraph] with
    def identity = InheritanceGraph.empty
    def combine(l: => InheritanceGraph, r: => InheritanceGraph) = l ++ r

  // TODO: make this configurable
  val excluded =
    Set(
      "scala/AnyRef#",
      "scala/AnyVal#",
      "java/io/Serializable#",
      "copy$default$",
      "local",
      "<init>"
    )

  // In Scala 3.2 the type annotation is needed.
  val empty: InheritanceGraph = new InheritanceGraph(Set.empty)

  def from(textDocuments: TextDocumentsWithSourceSeq): InheritanceGraph =
    val allSymbols =
      for
        docWithSource <- textDocuments.documentsWithSource
        doc <- docWithSource.documents
        occurrences = doc.occurrences.map(so => (so.symbol, so)).toMap
        si <- doc.symbols
        if !excluded.exists(si.symbol.contains)
      yield
        GraphSymbol(si.symbol) -> (si, docWithSource.semanticDbUri, doc.uri, docWithSource.basePath, occurrences.get(si.symbol))

    val symbolInfosMap = allSymbols.map((s, t) => s -> t._1).toMap

    val namespaces =
      for
        (symbol, (symbolInfo, semanticDbUri, docURI, basePath, symbolOcc: Option[SymbolOccurrence])) <- allSymbols
        signature    <- symbolInfo.signature.asNonEmpty.toSeq
        clsSignature <- signature match
          case cs: ClassSignature => List(cs)
          case _                  => List.empty
        nsKind = translateKind(symbolInfo.kind)
        declarations = clsSignature
          .declarations
          .map:
            _.symlinks
              .filterNot(si => excluded.exists(si.contains))
              .map(GraphSymbol(_))
          .toSeq.flatten

        methods = declarations.map(method(symbolInfosMap))
        namespace =
          Namespace(
            symbol        = symbol,
            displayName   = symbolInfo.displayName,
            kind          = nsKind,
            methods       = methods.toList.sortBy(_.displayName),
            documentURI   = Some(docURI),
            semanticDbUri = Some(semanticDbUri),
            basePath      = Some(basePath),
            range         = symbolOcc.map(SymbolRange.from)
          )
      yield
        namespace -> clsSignature.parents

    val arrows =
      for
        (ns, parents) <- namespaces.toSeq
        parent        <- parents
        parentType    <- parent.asNonEmpty.toSeq
        parentSymbol  <- parentType match
          case tr: TypeRef => List(GraphSymbol(tr.symbol))
          case _ => List.empty
        if !excluded.contains(parentSymbol.toString)
      yield
        ns.symbol -> parentSymbol

    InheritanceGraph(
      arrows     = arrows.toSet,
      namespaces = namespaces.map(_._1).toSet ++ missingSymbols(arrows.map(_._2).toList, symbolInfosMap)
    )

  end from

  private def missingSymbols(parents: List[GraphSymbol], allSymbols: Map[GraphSymbol, SymbolInformation]) =
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
    case _                   => ???

  private def method(allSymbols: Map[GraphSymbol, SymbolInformation])(decl: GraphSymbol) =
    Method(
      symbol      = decl,
      displayName = allSymbols.get(decl).map(_.displayName).getOrElse(missingMethodName(decl)),
      returnType  = None
    )

  private def missingMethodName(s: GraphSymbol) =
    val str = s.toString
    if str.endsWith("`<init>`().") then
      "<init>"
    else if str.endsWith("#main().") then
      "main()"
    else
      str

end InheritanceGraph

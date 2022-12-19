package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.shared.tree.Tree
import zio.json.*
import zio.Chunk

import scala.meta.internal.semanticdb.SymbolInformation.Kind
import scala.meta.internal.semanticdb.{ClassSignature, MethodSignature, Scope, Signature, SymbolInformation, SymbolOccurrence, TextDocument, TextDocuments, Type, TypeRef, TypeSignature, ValueSignature}
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, Symbol}

import scala.meta.internal.semanticdb
import java.util.jar.Attributes.Name
import scala.annotation.{tailrec, targetName}

type Arrow = (Symbol, Symbol)

enum Related:
  case Parents, Children

object Related:
  given JsonCodec[Related] =
    JsonCodec(JsonEncoder[String].contramap(_.toString), JsonDecoder[String].map(Related.valueOf))


import Related.*



/** A simplified representation of entities and subtype relationships
  *
  * @param arrows A pair `(a, b)` means that `a` is a subtype of `b`
  * @param namespaces Classes, Objects, Traits, etc
  */
case class InheritanceDiagram(
  arrows    : Set[Arrow],
  namespaces: Set[Namespace] = Set.empty,
):
  lazy val symbols =
    namespaces.map(_.symbol)

  lazy val directParents: Symbol => Set[Symbol] =
    arrows.groupBy(_._1)
    .transform((_, ss) => ss.map(_._2))
    .withDefaultValue(Set.empty)

  private lazy val directChildren: Symbol => Set[Symbol] =
    arrows.groupBy(_._2)
    .transform((_, ss) => ss.map(_._1))
    .withDefaultValue(Set.empty)

  private lazy val nsBySymbol: Map[Symbol, Namespace] =
    namespaces.groupMapReduce(_.symbol)(identity)((_, b) => b)

  private lazy val nsByKind: Map[NamespaceKind, Set[Namespace]] =
    namespaces.groupBy(_.kind)

  def directRelatives(related: Related): Symbol => Set[Symbol] =
    if related == Parents then directParents else directChildren

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

  private def arrowsForSymbols(symbols: Set[Symbol]) =
    for
      arrow@(a, b) <- arrows
      if (symbols contains a) && (symbols contains b)
    yield
      arrow

  /** Creates a diagram containing the given symbols and the arrows between them.
    */
  def subdiagram(symbols: Set[Symbol]): InheritanceDiagram =
    val foundSymbols = nsBySymbol.keySet.intersect(symbols)
    val foundNS      = foundSymbols.map(nsBySymbol)
    InheritanceDiagram(arrowsForSymbols(foundSymbols), foundNS)

  def subdiagramByKinds(kinds: Set[NamespaceKind]): InheritanceDiagram =
    val foundKinds  = nsByKind.filter((kind, _) => kinds.contains(kind))
    val foundNS     = foundKinds.values.flatten.toSet
    InheritanceDiagram(arrowsForSymbols(foundNS.map(_.symbol)), foundNS)

  // Note: doesn't handle loops.
  // How efficient is this compared to the tail rec version above?
  def unfold(symbol: Symbol, related: Symbol => Set[Symbol]): Set[Symbol] =
    Set.unfold(Set(symbol)) { ss =>
      val ss2 = ss.flatMap(related)
      if ss2.isEmpty then None else Some((ss2, ss2))
    }.flatten


  private def allRelated(s: Symbol, r: Symbol => Set[Symbol]): InheritanceDiagram =
    subdiagram(unfold(s, r) + s)

  def allParents(symbol: Symbol): InheritanceDiagram =
    allRelated(symbol, directParents)

  def allChildren(symbol: Symbol): InheritanceDiagram =
    allRelated(symbol, directChildren)


  lazy val toTrees: List[Tree[Namespace]] =
    val paths =
      for ns <- namespaces.toList yield
        (ns.symbol.toString.split("/").init.toList, ns.displayName, ns)
    Tree.fromPaths(paths, ".")

  /** Combines the diagram on the left with the diagram on the right.
    * No new arrows are introduced beyond those present in both diagrams.
    */
  @targetName("combine")
  def ++ (other: InheritanceDiagram): InheritanceDiagram =
    InheritanceDiagram(
      arrows = arrows ++ other.arrows,
      namespaces = namespaces ++ other.namespaces
    )

  /** Creates a new subdiagram with all related symbols.
    *
    * @param symbol
    * @param related If empty only `symbol` will be used.
    * @return
    */
  def findRelated(symbol: Symbol, related: Set[Related]): InheritanceDiagram =
    related.foldLeft(subdiagram(Set(symbol))) { case (acc, r) =>
      val d = r match
        case Parents => allParents(symbol)
        case Children => allChildren(symbol)
      acc ++ d
    }

  /** Creates a new subdiagram with all related symbols.
    */
  def filterSymbols(symbols: Set[(Symbol, Set[Related])]): InheritanceDiagram =
    val initial = subdiagram(symbols.map(_._1))
    val disjoint = symbols.map(findRelated).foldLeft(initial)(_ ++ _)
    subdiagram(disjoint.symbols)


  /** Creates a new subdiagram with all the symbols containing the given String.
    */
  def filterBySymbols(str: String): InheritanceDiagram =
    subdiagram(symbols.filter(_.toString.toLowerCase.contains(str.toLowerCase)))


end InheritanceDiagram



object InheritanceDiagram:

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

  given JsonCodec[InheritanceDiagram] = DeriveJsonCodec.gen

  // In Scala 3.2 the type annotation is needed.
  val empty: InheritanceDiagram = new InheritanceDiagram(Set.empty)

  def fromTextDocuments(textDocuments: TextDocuments): InheritanceDiagram =
    val allSymbols: Map[Symbol, SymbolInformation] =
      textDocuments.documents
        .flatMap(_.symbols)
        .distinct
        .filterNot(si => excluded.exists(si.symbol.contains))
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
        declarations = clsSignature
          .declarations
          .map(_.symlinks
            .filterNot(si => excluded.exists(si.contains))
            .map(Symbol(_))
          )
          .toSeq.flatten

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
        if !excluded.contains(parentSymbol.toString)
      yield
        ns.symbol -> parentSymbol

    InheritanceDiagram(
      arrows     = arrows.toSet,
      namespaces = namespaces.keys.toSet ++ missingSymbols(arrows.map(_._2).toList, allSymbols)
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
//    case other               => NamespaceKind.Other(other.toString)


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


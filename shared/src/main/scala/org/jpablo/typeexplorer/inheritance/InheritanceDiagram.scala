package org.jpablo.typeexplorer.inheritance

import org.jpablo.typeexplorer.fileTree.FileTree
import org.jpablo.typeexplorer.models.{Namespace, NamespaceKind, Symbol}
import zio.json.*

case class InheritanceDiagram(
  pairs     : List[(Symbol, Symbol)],
  namespaces: List[Namespace] = List.empty,
):
  lazy val bySymbol: Map[Symbol, Namespace] =
    namespaces.groupMapReduce(_.symbol)(identity)((a,_) => a)

  lazy val pairsFull: List[(Namespace, Namespace)] =
    pairs.map { case (a: Symbol, b: Symbol) =>
      val na = bySymbol.getOrElse(a, Namespace(a, a.toString, NamespaceKind.Unknown))
      val nb = bySymbol.getOrElse(b, Namespace(b, b.toString, NamespaceKind.Unknown))
      na -> nb
    }

  lazy val toFileTree: List[FileTree[Namespace]] =
    FileTree.build(namespaces, ".") { ns =>
      (ns, ns.displayName, ns.symbol.toString.split("/").init.toList)
    }

object InheritanceDiagram:
  given JsonEncoder[InheritanceDiagram] = DeriveJsonEncoder.gen
  given JsonDecoder[InheritanceDiagram] = DeriveJsonDecoder.gen

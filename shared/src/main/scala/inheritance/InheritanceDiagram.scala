package inheritance

import models.*
//import io.circe.*, io.circe.generic.semiauto.*

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
//object InheritanceDiagram:
//  given Encoder[InheritanceDiagram] = deriveEncoder

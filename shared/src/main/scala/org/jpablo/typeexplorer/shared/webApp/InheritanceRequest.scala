package org.jpablo.typeexplorer.shared.webApp

import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, SymbolOptions}
import org.jpablo.typeexplorer.shared.models.Symbol
import zio.json.*

type ActiveSymbolsSeq = List[(Symbol, Option[SymbolOptions])]

case class InheritanceRequest[A](
  paths  : List[A],
  symbols: ActiveSymbolsSeq,
  options: DiagramOptions = DiagramOptions(),
) derives JsonCodec


object Routes:
  val inheritance = "inheritance"
  val inheritanceDot = "inheritance.dot"
  val inheritancePuml = "inheritance.puml"
  val semanticdb = "semanticdb"
  val classes = "classes"
  val source = "source"


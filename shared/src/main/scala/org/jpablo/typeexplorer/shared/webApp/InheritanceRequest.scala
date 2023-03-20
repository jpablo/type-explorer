package org.jpablo.typeexplorer.shared.webApp

import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.SymbolOptions
import zio.json.*
import org.jpablo.typeexplorer.shared.models.Symbol
import java.nio.file

type ActiveSymbolsSeq = List[(Symbol, Option[SymbolOptions])]

case class InheritanceRequest[A](
  paths  : List[A],
  symbols: ActiveSymbolsSeq,
  options: PlantumlInheritance.DiagramOptions = PlantumlInheritance.DiagramOptions(),
) derives JsonCodec


object Routes:
  val inheritanceDiagram = "inheritance"
  val semanticdb = "semanticdb"
  val classes = "classes"
  val source = "source"


package org.jpablo.typeexplorer.shared.webApp

import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, ProjectSettings, SymbolOptions}
import org.jpablo.typeexplorer.shared.models.GraphSymbol
import zio.json.*

type ActiveSymbolsSeq = List[(GraphSymbol, Option[SymbolOptions])]

case class InheritanceRequest[A](
  paths        : List[A],
  activeSymbols: ActiveSymbolsSeq,
  options      : DiagramOptions = DiagramOptions(),
  projectSettings: ProjectSettings = ProjectSettings(),
) derives JsonCodec


object Routes:
  val inheritanceDiagram = "inheritance"
  val semanticdb = "semanticdb"
  val classes = "classes"
  val source = "source"


package org.jpablo.typeexplorer.shared.inheritance

case class GraphvizInheritance(diagram: String)

extension (diagram: InheritanceGraph)
  def toGraphviz(
      symbols:        Map[Symbol, Option[SymbolOptions]],
      diagramOptions: DiagramOptions = DiagramOptions()
  ): GraphvizInheritance = ???

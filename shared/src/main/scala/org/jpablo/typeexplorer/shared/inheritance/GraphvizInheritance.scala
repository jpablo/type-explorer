package org.jpablo.typeexplorer.shared.inheritance


case class GraphvizInheritance(diagram: String)

extension (diagram: InheritanceDiagram)
  def toGraphviz(
    symbols: Map[Symbol, Option[SymbolOptions]],
    diagramOptions: DiagramOptions = DiagramOptions()
  ): GraphvizInheritance = ???

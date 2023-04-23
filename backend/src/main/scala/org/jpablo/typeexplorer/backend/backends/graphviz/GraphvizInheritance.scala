package org.jpablo.typeexplorer.backend.backends.graphviz

import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.attribute.Label.Location
import guru.nidi.graphviz.attribute.Rank.RankDir
import guru.nidi.graphviz.engine.{Format, Graphviz}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.{Graph, LinkSource, LinkTarget, Node}
import org.jpablo.typeexplorer.backend.backends.graphviz.GraphvizInheritance.toGraph
import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, InheritanceDiagram, InheritanceExamples, SymbolOptions}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.tree.Tree
import scalatags.Text

import java.io.File


extension (diagram: InheritanceDiagram)
  def toGraphviz(name: String, symbolOptions: Map[models.Symbol, Option[SymbolOptions]], diagramOptions: DiagramOptions) =
    GraphvizInheritance.toGraph(name, diagram, symbolOptions, diagramOptions)

object GraphvizInheritance:

  def toGraph(
    name          : String,
    diagram       : InheritanceDiagram,
    symbolOptions : Map[models.Symbol, Option[SymbolOptions]],
    diagramOptions: DiagramOptions
  ): Graph =

    val filteredDiagram =
      diagram.filterBy(ns => !diagramOptions.hiddenSymbols.contains(ns.symbol))

    val declarations =
      filteredDiagram.toTrees.map(renderTree(diagramOptions, symbolOptions))

    val nodes =
      filteredDiagram.namespaces
        .map(tpe => tpe.symbol -> node(tpe.symbol.toString))
        .toMap

    val arrows =
      filteredDiagram.arrows.toSeq.map: (source, target) =>
        nodes(source) `link` to(nodes(target))

    graph(name)
      .directed
      .graphAttr.`with`(Rank.dir(RankDir.BOTTOM_TO_TOP))
      .nodeAttr.`with`(Style.FILLED, Shape.RECT, Color.rgb("#b7c9e3").fill)
      .linkAttr.`with`(Arrow.EMPTY)
      .`with`(declarations*)
      .`with`(arrows*)


  private def renderTree(diagramOptions: DiagramOptions, symbolOptions: Map[models.Symbol, Option[SymbolOptions]]): Tree[models.Namespace] => (LinkSource & LinkTarget) =
    case Tree.Node(label, path, children: List[Tree[models.Namespace]]) =>
      println((label, path))
      // 'FontSize 20
      // 'FontName "JetBrains Mono"
      graph(path.mkString("/")).cluster
        .graphAttr.`with`(Label.html(label).locate(Location.BOTTOM))
        .`with`(
          children.map(renderTree(diagramOptions, symbolOptions))*
        )

    case Tree.Leaf(_, ns) =>
      toNode(ns, diagramOptions, symbolOptions(ns.symbol))

  import scalatags.Text.all.*
  // Graphviz specific attributes
  private val border   = attr("BORDER")
  private val cBorder  = attr("CELLBORDER")
  private val cPadding = attr("CELLPADDING")
  private val cSpacing = attr("CELLSPACING")

  private def toNode(ns: models.Namespace, diagramOptions: DiagramOptions, symbolOptions: Option[SymbolOptions]): Node =
    val showFields = symbolOptions.map(_.showFields).getOrElse(diagramOptions.showFields)
//    val showSignatures = symbolOptions.map(_.showSignatures).getOrElse(diagramOptions.showSignatures)
    val fields =
      if showFields then
        for m <- ns.methods yield
          tr(
            td(
              m.displayName
            )
          )
      else
        List.empty

    val nodeData =
      table(border := 0, cBorder := 0, cPadding := 0, cSpacing := 0, //style:="ROUNDED", bgColor := "LIGHTBLUE",
        th(td(b(ns.displayName))),
        fields
      )
    node(ns.symbol.toString).`with`(Label.html(nodeData.toString))

  type PortId = String

end GraphvizInheritance

@main
def graphVizInheritanceExample: File =
  val g = toGraph("laminar",  InheritanceExamples.laminar, Map.empty, DiagramOptions())
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/laminar1.svg"))

package backends.graphviz

import backends.graphviz.GraphvizCallGraph.toGraph
import callGraph.{CallGraph, CallGraphExamples}
import guru.nidi.graphviz.attribute.Label
import models.{Method, Type}
import scalatags.Text
import guru.nidi.graphviz.attribute.Rank.RankDir
import guru.nidi.graphviz.model.{Graph, Link, LinkSource, LinkTarget, Node, PortNode}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.attribute.Records.*
import guru.nidi.graphviz.attribute.{Arrow, Color, Rank, Records, Shape, Style}
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz

import java.io.File

object GraphvizCallGraph:

  def toGraph (name: String, diagram: CallGraph): Graph =
    val (subGraphs, nodes) =
      diagram.namesSpaces
        .map (toSubgraph)
        .unzip

    val combinedNodes: Map[Method, Node] =
      nodes.foldLeft (Map.empty) (_ ++ _)

    val links =
      for (source, target) <- diagram.pairs yield
        combinedNodes (source) link to (combinedNodes (target))

    graph (name)
      .directed
      .nodeAttr.`with`(Style.FILLED, Shape.RECT, Color.rgb("#b7c9e3").fill)
      .`with`(subGraphs*)
      .`with`(links*)

  def toSubgraph (ns: Type): (Graph, Map[Method, Node]) =
    val methods =
      ns.methods
        .map (m => m -> toNode (m))
        .toMap
    val g =
      graph (ns.name)
        .cluster
        .graphAttr.`with`(Label.of(ns.name))
        .`with`(methods.values.toSeq*)
    (g, methods)


  private def toNode (box: Method): Node =
    node (box.name)

end GraphvizCallGraph


@main
def graphVizCallGraphExample: File =
  val g = toGraph("call-graph-example", CallGraphExamples.callGraphExample)
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/call-graph.svg"))



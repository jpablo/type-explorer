package org.jpablo.typeexplorer.backend.backends.graphviz

import GraphvizCallGraph.toGraph
import guru.nidi.graphviz.attribute.Label
import scalatags.Text
import guru.nidi.graphviz.attribute.Rank.RankDir
import guru.nidi.graphviz.model.{Graph, Link, LinkSource, LinkTarget, Node, PortNode}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.attribute.Records.*
import guru.nidi.graphviz.attribute.{Arrow, Color, Rank, Records, Shape, Style}
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import org.jpablo.typeexplorer.shared.callGraph.{CallGraph, CallGraphExamples}
import org.jpablo.typeexplorer.shared.models.{Method, Namespace}

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
        combinedNodes (source) `link` to (combinedNodes (target))

    graph (name)
      .directed
      .nodeAttr.`with`(Style.FILLED, Shape.RECT, Color.rgb("#b7c9e3").fill)
      .`with`(subGraphs*)
      .`with`(links*)

  def toSubgraph (ns: Namespace): (Graph, Map[Method, Node]) =
    val methods =
      ns.methods
        .map (m => m -> toNode (m))
        .toMap
    val g =
      graph (ns.displayName)
        .cluster
        .graphAttr.`with`(Label.of(ns.displayName))
        .`with`(methods.values.toSeq*)
    (g, methods)


  private def toNode (box: Method): Node =
    node (box.displayName)

end GraphvizCallGraph


@main
def graphVizCallGraphExample: File =
  val g = toGraph("call-graph-example", CallGraphExamples.callGraphExample)
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/call-graph.svg"))



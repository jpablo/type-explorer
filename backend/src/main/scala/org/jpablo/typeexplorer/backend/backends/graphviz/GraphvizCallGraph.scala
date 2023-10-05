package org.jpablo.typeexplorer.backend.backends.graphviz

import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.engine.{Format, Graphviz}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.{Graph, LinkTarget, Node}
import org.jpablo.typeexplorer.backend.backends.graphviz.GraphvizCallGraph.toGraph
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



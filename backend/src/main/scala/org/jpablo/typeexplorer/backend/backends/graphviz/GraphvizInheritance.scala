package org.jpablo.typeexplorer.backend.backends.graphviz

import GraphvizInheritance.toGraph
import guru.nidi.graphviz.attribute.Label
import scalatags.Text
import guru.nidi.graphviz.attribute.Rank.RankDir
import guru.nidi.graphviz.model.{Graph, Link, LinkSource, LinkTarget, Node, PortNode}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.attribute.Records.*
import guru.nidi.graphviz.attribute.{Arrow, Color, Rank, Records, Shape, Style}
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, InheritanceExamples}
import org.jpablo.typeexplorer.shared.models.Namespace

import java.io.File


extension (diagram: InheritanceDiagram)
  def toGraphviz(name: String) = GraphvizInheritance.toGraph(name, diagram)

object GraphvizInheritance:

  def toGraph(name: String, diagram: InheritanceDiagram): Graph =
    val nodes =
      diagram.namespaces
        .map(tpe => tpe.symbol -> toNode(tpe))
        .toMap

    val arrows =
      diagram.arrows.toSeq.map: (source, target) =>
        nodes(source) `link` to(nodes(target))

    graph(name)
      .directed
      .graphAttr.`with`(Rank.dir(RankDir.BOTTOM_TO_TOP))
      .nodeAttr.`with`(Style.FILLED, Shape.RECT, Color.rgb("#b7c9e3").fill())
      .linkAttr.`with`(Arrow.EMPTY)
      .`with`(nodes.values.toSeq*)
      .`with`(arrows*)

  private def toNode(box: Namespace): Node =
    import scalatags.Text.all.*
    import scala.language.implicitConversions
    // Graphviz specific attributes
    val border   = attr("BORDER")
    val cBorder  = attr("CELLBORDER")
    val cPadding = attr("CELLPADDING")
    val cSpacing = attr("CELLSPACING")
    val t =
      table(border := 0, cBorder := 0, cPadding := 0, cSpacing := 0, //style:="ROUNDED", bgColor := "LIGHTBLUE",
        th(
          td(
            b(box.displayName))
        ),
        for m <- box.methods yield
          tr(
            td(
              m.displayName
            )
          )
      )

    node(box.displayName).`with`(Label.html(t.toString))
  end toNode

  type PortId = String

end GraphvizInheritance

@main
def graphVizInheritanceExample: File =
  val g = toGraph("laminar",  InheritanceExamples.laminar)
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/laminar1.svg"))

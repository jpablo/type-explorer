package backends.graphvizJava

import backends.graphvizJava.GraphvizJava.fromInheritanceDiagram
import guru.nidi.graphviz.attribute.Attributes.attr
import guru.nidi.graphviz.attribute.Rank.RankDir
import inheritance.{InheritanceDiagram, InheritanceExamples}
import guru.nidi.graphviz.model.{Graph, Link, LinkSource, LinkTarget, Node, PortNode}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.attribute.{Arrow, Color, Rank, Shape, Style}
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz

import java.io.File


object GraphvizJava {

  def fromInheritanceDiagram(name: String, diagram: InheritanceDiagram) =
    graph(name)
      .directed
      .graphAttr.`with`(Rank.dir(RankDir.BOTTOM_TO_TOP))
      .nodeAttr.`with`(Style.FILLED, Shape.RECT, Color.rgb("#b7c9e3").fill())
      .linkAttr.`with`(Arrow.EMPTY)
      .`with`(
        diagram.pairs.map { case (source, target) =>
          node(source.name) link to(node(target.name))
        }:_*
      )
}


@main
def graphVizJavaExample =
  val g = fromInheritanceDiagram("laminar",  InheritanceExamples.laminar)
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/laminar.svg"))

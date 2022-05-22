package backends.graphvizJava

import inheritance.{InheritanceDiagram, InheritanceExamples, Type}
import backends.graphvizJava.GraphvizJavaInheritance.fromInheritanceDiagram
import guru.nidi.graphviz.attribute.Label
import scalatags.Text
//import guru.nidi.graphviz.attribute.Attributes.attr
import guru.nidi.graphviz.attribute.Rank.RankDir
import guru.nidi.graphviz.model.{Graph, Link, LinkSource, LinkTarget, Node, PortNode}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.attribute.Records.*
import guru.nidi.graphviz.attribute.{Arrow, Color, Rank, Records, Shape, Style}
import guru.nidi.graphviz.engine.Format
import guru.nidi.graphviz.engine.Graphviz

import java.io.File


object GraphvizJavaInheritance:

  def fromInheritanceDiagram(name: String, diagram: InheritanceDiagram) =
    val nodes: Map[Type, Node] =
      diagram.types
        .map(tpe => tpe -> toNode(tpe))
        .toMap

    graph(name)
      .directed
      .graphAttr.`with`(Rank.dir(RankDir.BOTTOM_TO_TOP))
      .nodeAttr.`with`(Style.FILLED, Shape.RECT, Color.rgb("#b7c9e3").fill())
      .linkAttr.`with`(Arrow.EMPTY)
      .`with`(
        diagram.pairs.map { case (source, target) =>
          nodes(source) link to(nodes(target))
        }*
      )

  private def toNode(box: Type): Node =
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
            b(box.name))
        ),
        for m <- box.methods yield
          tr(
            td(
              m.name
            )
          )
      )

    node(box.name).`with`(Label.html(t.toString))
  end toNode

  type PortId = String

end GraphvizJavaInheritance


@main
def graphVizJavaExample =
  val g = fromInheritanceDiagram("laminar",  InheritanceExamples.laminar)
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/laminar.svg"))

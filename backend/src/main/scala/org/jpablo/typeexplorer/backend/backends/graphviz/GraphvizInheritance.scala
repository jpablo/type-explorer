package org.jpablo.typeexplorer.backend.backends.graphviz

import guru.nidi.graphviz.attribute.*
import guru.nidi.graphviz.attribute.Label.{Justification, Location}
import guru.nidi.graphviz.attribute.Rank.RankDir
import guru.nidi.graphviz.engine.{Format, Graphviz}
import guru.nidi.graphviz.model.Factory.*
import guru.nidi.graphviz.model.{Graph, LinkSource, LinkTarget, Node}
import org.jpablo.typeexplorer.backend.backends.graphviz.GraphvizInheritance.toGraph
import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, InheritanceGraph, InheritanceExamples, ProjectSettings, SymbolOptions}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.tree.Tree
import scalatags.Text

import java.io.File


extension (iGraph: InheritanceGraph)
  def toGraphviz(name: String, symbolOptions: Map[models.GraphSymbol, Option[SymbolOptions]], diagramOptions: DiagramOptions, projectSettings: ProjectSettings) =
    GraphvizInheritance.toGraph(name, iGraph, symbolOptions, diagramOptions, projectSettings)

object GraphvizInheritance:

  def toGraph(
    name           : String,
    iGraph         : InheritanceGraph,
    symbolOptions  : Map[models.GraphSymbol, Option[SymbolOptions]],
    diagramOptions : DiagramOptions,
    projectSettings: ProjectSettings
  ): Graph =

    val filteredDiagram =
      iGraph.filterBy(ns => !projectSettings.hiddenSymbols.contains(ns.symbol))

    val declarations =
      filteredDiagram.toTrees.children.map(renderTree(diagramOptions, projectSettings, symbolOptions))

    val arrows =
      filteredDiagram.arrows.toSeq.map: (source, target) =>
        node(source.toString) `link` to(node(target.toString))

    graph(name)
      .directed
      .graphAttr.`with`(Rank.dir(RankDir.BOTTOM_TO_TOP))
      // https://graphviz.org/doc/info/shapes.html#html
      // ...In effect, shape=plain is shorthand for shape=none width=0 height=0 margin=0...
      .nodeAttr.`with`(Shape.PLAIN)
      .linkAttr.`with`(Arrow.EMPTY)
      .`with`(declarations*)
      .`with`(arrows*)

  private def renderTree(diagramOptions: DiagramOptions, projectSettings: ProjectSettings, symbolOptions: Map[models.GraphSymbol, Option[SymbolOptions]]): Tree[models.Namespace] => (LinkSource & LinkTarget) =
    case Tree.Branch(label, path, children) =>
      val clusterName = path.mkString("/")
      graph(clusterName)
        .cluster
        .graphAttr.`with`(
          Label.html(label)
            .locate(Location.BOTTOM)
            .justify(Justification.LEFT)
        )
        .`with`(
          children.map(renderTree(diagramOptions, projectSettings, symbolOptions)) *
        )

    case Tree.Leaf(_, ns) =>
      renderNamespace(ns, diagramOptions, projectSettings, symbolOptions(ns.symbol))

  import scalatags.Text.all.*
  // Graphviz specific attributes
  private val border   = attr("BORDER")
  private val cBorder  = attr("CELLBORDER")
  private val cPadding = attr("CELLPADDING")
  private val cSpacing = attr("CELLSPACING")
  private val bgColor  = attr("BGCOLOR")
  private val align  = attr("ALIGN")
  private val sides  = attr("SIDES")
  private val face  = attr("FACE")
  private val fontGV: ConcreteHtmlTag[String] = tag("font")

  case class TableStyle(border: Int, cellBorder: Int, cellPadding: Int, cellSpacing: Int)
  implicit def tableStyleNode(t: TableStyle): Modifier =
    List(border := t.border, cBorder := t.cellBorder, cPadding := t.cellPadding, cSpacing := t.cellSpacing)

  val ts =
    TableStyle(
      border = 1,
      cellBorder = 0,
      cellPadding = 2,
      cellSpacing = 0
  )
  val emptyStyle = TableStyle(0, 0, 0, 0)
  val monoFontFace = face := "Monospace"

  def stereotype(ns: models.Namespace) = ns.kind match
    case models.NamespaceKind.Object => "(O)" //""" << (O, #44ad7d) >>"""
    case models.NamespaceKind.PackageObject => "(P)" //""" << (P, lightblue) >>"""
    case models.NamespaceKind.Trait => "(T)" //""" << (T, pink) >>"""
    case models.NamespaceKind.Class => "(C)"
    case other => s"""($other)"""


  private def renderNamespace(ns: models.Namespace, diagramOptions: DiagramOptions, projectSettings: ProjectSettings, symbolOptions: Option[SymbolOptions]): Node =
    val showFields = symbolOptions.map(_.showFields).getOrElse(diagramOptions.showFields)
    // val showSignatures = symbolOptions.map(_.showSignatures).getOrElse(diagramOptions.showSignatures)
    val fields =
      if showFields then
        if ns.methods.isEmpty then
          None
        else
          Some(
            tr(
              td(
                table(emptyStyle.copy(border = 1, cellPadding = 3),
                  for
                    m <- ns.methods
                    if !projectSettings.hiddenFields.contains(m.displayName)
                  yield
                    tr(
                      td(cPadding := 0, align := "LEFT",
                        fontGV(monoFontFace, m.displayName)
                      )
                    )
                )
              )
            )
          )
      else
        None

    val nodeContents =
      table(emptyStyle, bgColor := "#F1F1F1",//style:="ROUNDED",
        tr(td(
          // title
          table(emptyStyle.copy(border = 1, cellPadding = 3),
            tr(
              td(
                fontGV(monoFontFace, stereotype(ns))
              ),
              td(
                fontGV(monoFontFace, ns.displayName)
              )
            )
          )
        )),
        fields,
      )
    node(ns.symbol.toString).`with`(Label.html(nodeContents.toString))

  type PortId = String

end GraphvizInheritance

@main
def graphVizInheritanceExample: File =
  val g = toGraph("laminar",  InheritanceExamples.laminar, Map.empty, DiagramOptions(), ProjectSettings())
  Graphviz.fromGraph(g).height(500).render(Format.SVG_STANDALONE).toFile(new File("examples/laminar1.svg"))

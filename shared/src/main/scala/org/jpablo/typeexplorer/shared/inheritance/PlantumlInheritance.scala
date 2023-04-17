package org.jpablo.typeexplorer.shared.inheritance

import zio.json.*
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, Symbol}


case class PlantUML(diagram: String)


extension (diagram: InheritanceDiagram)
  def toPlantUML(
    symbols: Map[Symbol, Option[SymbolOptions]],
    diagramOptions: DiagramOptions = DiagramOptions()
  ): PlantUML =
    PlantumlInheritance.toPlantUML(diagram, symbols, diagramOptions)

object PlantumlInheritance:

  def toPlantUML(
    diagram: InheritanceDiagram,
    symbols: Map[Symbol, Option[SymbolOptions]],
    diagramOptions: DiagramOptions = DiagramOptions()
  ): PlantUML =
    val filteredDiagram =
      diagram.filterBy(ns => !diagramOptions.hiddenSymbols.contains(ns.symbol))
    val declarations =
      filteredDiagram.toTrees.map(renderTree(diagramOptions, symbols))

    val inheritance =
      for (source, target) <- filteredDiagram.arrows yield
        s""""${target}" <|-- "${source}""""

    PlantUML(
      s"""@startuml
         |set namespaceSeparator none
         |${declarations.distinct mkString "\n"}
         |${inheritance mkString "\n"}
         |@enduml""".stripMargin
    )

  // ----------------------------------------------------

  private def renderTree(diagramOptions: DiagramOptions, symbols: Map[Symbol, Option[SymbolOptions]]): Tree[Namespace] => String =
    case Tree.Node(label, path, children) =>
      s"""
         |skinparam class {
         |  'FontSize 20
         |  'FontName "JetBrains Mono"
         |}
         |
         |namespace "$label" as ${path.mkString(".")} {
         |  ${children.map(renderTree(diagramOptions, symbols)) mkString "\n"}
         |}
         |""".stripMargin
    case Tree.Leaf(_, ns) =>
      renderNamespace(ns, diagramOptions, symbols(ns.symbol))

  // certain characters are interpreted by plantuml, so we use unicode codes instead
  private val replacementTable = Map(
    "|" -> "&#124;"
  )
  private def replaceMultiple(s: String) =
    var s1 = s
    replacementTable.foreach((k, v) => s1 = s1.replace(k, v))
    s1

  private def renderNamespace(ns: Namespace, diagramOptions: DiagramOptions, symbolOptions: Option[SymbolOptions]): String =
    val header = s"""class "${replaceMultiple(ns.displayName)}" as ${ns.symbol}"""
    val stereotype = ns.kind match
      case NamespaceKind.Object        => """ << (O, #44ad7d) >>"""
      case NamespaceKind.PackageObject => """ << (P, lightblue) >>"""
      case NamespaceKind.Trait         => """ << (T, pink) >>"""
      case NamespaceKind.Class         => ""
      case other                       => s""" <<$other>>"""
    val showFields = symbolOptions.map(_.showFields).getOrElse(diagramOptions.showFields)
    val showSignatures = symbolOptions.map(_.showSignatures).getOrElse(diagramOptions.showSignatures)
    val fields =
      if showFields then
        if showSignatures then
          ns.methods.map(renderField(0)).mkString(" {\n", "\n", "\n}\n")
        else
          ns.methods
          .filterNot(m => diagramOptions.hiddenFields.contains(m.displayName))
          .groupBy(_.displayName)
          .toList.sortBy(_._1)
          .map((_, ms) => renderField(ms.length)(ms.head)).mkString(" {\n", "\n", "\n}\n")
      else
        ""
    header + stereotype + fields

  private def renderField(count: Int)(m: Method): String =
    val countStr = if count > 1 then s"($count)" else ""
    s"""  ${m.displayName}$countStr ${m.returnType.map(o => " : " + o.displayName).getOrElse("")}  \n' ${m.symbol} """




package org.jpablo.typeexplorer.shared.inheritance

import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, GraphSymbol}


case class PlantUML(diagram: String)


extension (diagram: InheritanceGraph)
  def toPlantUML(
    symbols: Map[GraphSymbol, Option[SymbolOptions]],
    diagramOptions: DiagramOptions = DiagramOptions(),
    projectSettings: ProjectSettings = ProjectSettings()
  ): PlantUML =
    PlantumlInheritance.toPlantUML(diagram, symbols, diagramOptions, projectSettings)

object PlantumlInheritance:

  def toPlantUML(
    iGraph         : InheritanceGraph,
    symbols        : Map[GraphSymbol, Option[SymbolOptions]],
    diagramOptions : DiagramOptions = DiagramOptions(),
    projectSettings: ProjectSettings
  ): PlantUML =
    val filteredDiagram =
      iGraph.filterBy(ns => !projectSettings.hiddenSymbols.contains(ns.symbol))
    val declarations =
      filteredDiagram.toTrees.children.map(renderTree(diagramOptions, projectSettings, symbols))

    val inheritance =
      for (source, target) <- filteredDiagram.arrows yield
        s""""${target}" <|-- "${source}""""

    PlantUML(
      s"""@startuml
         |!pragma layout smetana
         |set namespaceSeparator none
         |skinparam class {
         |  'FontSize 20
         |  'FontName "JetBrains Mono"
         |}
         |
         |'declarations
         |
         |${declarations.distinct mkString "\n"}
         |
         |'inheritance
         |
         |${inheritance mkString "\n"}
         |@enduml""".stripMargin
    )

  // ----------------------------------------------------

  private def renderTree(diagramOptions: DiagramOptions, projectSettings: ProjectSettings, symbols: Map[GraphSymbol, Option[SymbolOptions]]): Tree[Namespace] => String =
    case Tree.Branch(label, path, children) =>
      s"""
         |namespace "$label" as ${path.mkString(".")} {
         |  ${children.map(renderTree(diagramOptions, projectSettings, symbols)) mkString "\n"}
         |}
         |""".stripMargin
    case Tree.Leaf(_, ns) =>
      renderNamespace(ns, diagramOptions, projectSettings, symbols.getOrElse(ns.symbol, None))

  // certain characters are interpreted by plantuml, so we use unicode codes instead
  private val replacementTable = Map(
    "|" -> "&#124;"
  )
  private def replaceMultiple(s: String) =
    var s1 = s
    replacementTable.foreach((k, v) => s1 = s1.replace(k, v))
    s1

  private def renderNamespace(ns: Namespace, diagramOptions: DiagramOptions, projectSettings: ProjectSettings, symbolOptions: Option[SymbolOptions]): String =
    val header = s"""class "${replaceMultiple(ns.displayName)}" as ${ns.symbol}"""
    val stereotype = ns.kind match
      case NamespaceKind.Object        => """ << (O, #44ad7d) >>"""
      case NamespaceKind.PackageObject => """ << (P, lightblue) >>"""
      case NamespaceKind.Trait         => """ << (T, pink) >>"""
      case NamespaceKind.Class         => ""
      case other                       => s""" <<$other>>"""

    val showFields = symbolOptions.map(_.showFields).getOrElse(diagramOptions.showFields)
    val showSignatures = symbolOptions.map(_.showSignatures).getOrElse(diagramOptions.showSignatures)
    val filteredMethods = ns.methods.filterNot(m => projectSettings.hiddenFields.contains(m.displayName))
    val fields =
      if showFields then
        if showSignatures then
          filteredMethods
            .map(renderField(0)).mkString(" {\n", "\n", "\n}\n")
        else
          filteredMethods
            .groupBy(_.displayName)
            .toList.sortBy(_._1)
            .map((_, ms) => renderField(ms.length)(ms.head)).mkString(" {\n", "\n", "\n}\n")
      else
        ""
    header + stereotype + fields

  private def renderField(count: Int)(m: Method): String =
    val countStr = if count > 1 then s"($count)" else ""
    val returnType = m.returnType.map(o => " : " + o.displayName).getOrElse("")
    val symbolComment = s"' ${m.symbol}"
    s"""  ${m.displayName}${countStr}${returnType}""" + "\n" + symbolComment




package org.jpablo.typeexplorer.shared.inheritance

import zio.json.*
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind, Symbol}
import org.jpablo.typeexplorer.shared.webApp.InheritanceRequest


case class PlantUML(diagram: String)


object PlantumlInheritance:

  case class DiagramOptions(showFields: Boolean = false, showSignatures: Boolean = false)

  object DiagramOptions:
    given JsonCodec[DiagramOptions] = DeriveJsonCodec.gen

  case class SymbolOptions(showFields: Boolean = false, showSignatures: Boolean = false)

  object SymbolOptions:
    given JsonCodec[SymbolOptions] = DeriveJsonCodec.gen


  def fromInheritanceDiagram(diagram: InheritanceDiagram, symbols: Map[Symbol, Option[SymbolOptions]], options: DiagramOptions = DiagramOptions()): PlantUML =
    val declarations =
      diagram.toTrees.map(renderTree(options, symbols))

    val inheritance =
      for (source, target) <- diagram.arrows yield
        s""""${target}" <|-- "${source}""""

    PlantUML(
      s"""@startuml
         |set namespaceSeparator none
         |${declarations.distinct mkString "\n"}
         |${inheritance mkString "\n"}
         |@enduml""".stripMargin
    )

  // ----------------------------------------------------

  private def renderTree(options: DiagramOptions, symbols: Map[Symbol, Option[SymbolOptions]]): Tree[Namespace] => String =
    case Tree.Node(label, path, children) =>
      s"""
         |skinparam class {
         |  'FontSize 20
         |  'FontName "JetBrains Mono"
         |}
         |
         |namespace "$label" as ${path.mkString(".")} {
         |  ${children.map(renderTree(options, symbols)) mkString "\n"}
         |}
         |""".stripMargin
    case Tree.Leaf(_, ns) =>
      renderNamespace(ns, options, symbols(ns.symbol))

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
          .groupBy(_.displayName)
          .toList.sortBy(_._1)
          .map((_, ms) => renderField(ms.length)(ms.head)).mkString(" {\n", "\n", "\n}\n")
      else
        ""
    header + stereotype + fields

  private def renderField(count: Int)(m: Method): String =
    val countStr = if count > 1 then s"($count)" else ""
    s"""  ${m.displayName}$countStr ${m.returnType.map(o => " : " + o.displayName).getOrElse("")}  \n' ${m.symbol} """




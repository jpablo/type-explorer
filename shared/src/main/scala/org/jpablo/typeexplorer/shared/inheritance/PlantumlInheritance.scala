package org.jpablo.typeexplorer.shared.inheritance

import zio.json.*
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.models.{Method, Namespace, NamespaceKind}


case class PlantUML(diagram: String)


object PlantumlInheritance:

  case class Options(fields: Boolean = false, signatures: Boolean = false)

  def fromInheritanceDiagram(diagram: InheritanceDiagram, options: Options = Options()): PlantUML =
    val declarations =
      diagram.toTrees.map(renderTree(options))

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

  private def renderTree(options: Options): Tree[Namespace] => String =
    case Tree.Node(label, path, children) =>
      s"""
         |namespace "$label" as ${path.mkString(".")} {
         |  ${children.map(renderTree(options)) mkString "\n"}
         |}
         |""".stripMargin
    case Tree.Leaf(_, ns) =>
      renderNamespace(ns, options)


  private def renderNamespace(ns: Namespace, options: Options): String =
    val header = s"""class "${ns.displayName}" as ${ns.symbol}"""
    val stereotype = ns.kind match
      case NamespaceKind.Object        => """ << (O, orchid) >>"""
      case NamespaceKind.PackageObject => """ << (P, lightblue) >>"""
      case NamespaceKind.Trait         => """ << (T, pink) >>"""
      case NamespaceKind.Class         => ""
      case other                       => s""" <<$other>>"""
    val fields =
      if options.fields then
        if options.signatures then
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




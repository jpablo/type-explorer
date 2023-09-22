package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind, Symbol}
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.svgGroupElement.path
import org.jpablo.typeexplorer.ui.widgets.{Collapsable, CollapsableTree}

import scalajs.js
import scalajs.js.URIUtils.encodeURIComponent
import org.scalajs.dom
import org.scalajs.dom.HTMLAnchorElement

object PackagesTree:


  /** Builds a collapsable tree based on the given inheritance diagram.
    *
    * @param diagrams The diagram
    * @return A List of trees, one for each top level package in the diagram: e.g. ["com..., ", "java.io..."]
    */
  def apply(inheritanceTabState: InheritanceTabState, diagrams: EventStream[InheritanceDiagram]): EventStream[List[HtmlElement]] =
    val openState = Var(Map.empty[String, Boolean])
    for diagram <- diagrams yield
      for tree <- diagram.toTrees yield
        CollapsableTree(tree)(
          renderNode = renderPackage(inheritanceTabState),
          renderLeaf = renderNamespace(inheritanceTabState, Collapsable.Control(startOpen = false, openState)),
          Collapsable.Control(startOpen = true, openState)
        )

  private def renderPackage(tabState: InheritanceTabState)(packageLabel: String, packagePath: List[String]) =
    div(
      cls := "whitespace-nowrap inline-block w-full focus:bg-blue-100",
      tabIndex := 0,
      a(
        packageLabel,
        inContext { thisNode =>
          onClick --> { _ =>
            // TODO: move "/" to a named constant
            val prefix = packagePath.mkString("/")
            // Rather hacky: find visible children with the given prefix
            for parent <- thisNode.ref.path.find(_.classList.contains("te-package-name")) do
              val symbols = parent.querySelectorAll(s"[id ^= '$prefix']").map(e => Symbol(e.id))
              tabState.activeSymbols.extend(symbols)
              tabState.canvasSelection.extend(symbols.toSet)
          }
        }
      )
    )

  private def renderNamespace(inheritanceTabState: InheritanceTabState, mkControl: String => Collapsable.Control)(s: String, ns: Namespace) =
    val symStr = ns.symbol.toString
    val uri = encodeURIComponent(symStr)
    val isActive = inheritanceTabState.activeSymbolsR.signal.map(_.contains(ns.symbol))

    Collapsable(
      nodeLabel =
        div(
          idAttr := symStr,
          cls := "inline-block w-full focus:bg-blue-100",
          tabIndex := 0,
          div(
            cls := "inline-block w-5",
            stereotype(ns)
          ),
          a(
            cls := "p-0.5 font-['JetBrains_Mono']",
            cls.toggle("bg-blue-200 rounded") <-- isActive,
            href  := "#elem_" + uri,
            title := symStr,
            ns.displayName,
            onClick --> { _ =>
              inheritanceTabState.activeSymbols.toggle(ns.symbol)
              inheritanceTabState.canvasSelection.toggle(ns.symbol)
            }
          ),
        ),
      nodeContents =
        ns.methods.map { m =>
          a(
            cls := "font-['JetBrains_Mono']",
            title := m.symbol.toString,
            m.displayName
          )
        },
      control = mkControl(symStr)
    )

  /** The "stereotype" is an element indicating which kind of namespace we have:
    * an Object, a Class, etc.
    */
  private def stereotype(ns: Namespace) =
    def circle(s: String, color: String) =
      div(cls := "avatar placeholder",
        div(cls := s"text-neutral-content rounded-full w-4",
          backgroundColor := color, span(cls := "text-xs", s)
        )
      )
    ns.kind match
      case NamespaceKind.Object        => circle("o", "rgb(44, 107, 141)")
      case NamespaceKind.PackageObject => circle("p", "lightblue")
      case NamespaceKind.Class         => circle("c", "rgb(68, 173, 125)")
      case NamespaceKind.Trait         => circle("t", "rgb(24, 170, 207)")
      case other                       => div(other.toString)

end PackagesTree

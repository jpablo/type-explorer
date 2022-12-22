package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind, Symbol}
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.jpablo.typeexplorer.ui.widgets.{CollapsableTree, Collapsable}
import scalajs.js
import scalajs.js.URIUtils.encodeURIComponent
import org.jpablo.typeexplorer.ui.daisyui.*
import org.scalajs.dom
import org.scalajs.dom.html
import zio.prelude.fx.ZPure

object PackagesTree:


  /** Builds a collapsable tree based on the given inheritance diagram.
    *
    * @param $diagram The diagram
    * @return A List of trees, one for each top level package name in the diagram: e.g. ["com..., ", "java.io..."]
    */
  def apply(inheritanceTabState: InheritanceTabState, $diagram: EventStream[InheritanceDiagram]) =
    val $open = Var(Map.empty[String, Boolean])
    for diagram <- $diagram yield
      for tree <- diagram.toTrees yield
        CollapsableTree(tree)(
          renderNode = renderPackage(inheritanceTabState),
          renderLeaf = renderNamespace(inheritanceTabState, $open),
          initial = true,
          $open
        )

  private def renderPackage(inheritanceTabState: InheritanceTabState)(packageLabel: String, packagePath: List[String]) =
    div(
      cls := "whitespace-nowrap inline-block w-full focus:bg-blue-100",
      tabIndex := 0,
      a(
        packageLabel,
        onClick --> { ev =>
          // TODO: move "/" to a named constant
          val prefix = packagePath.mkString("/")
          // Rather hacky: find visible children with the given prefix
          for parent <- ev.target.path.find(_.classList.contains("te-package-name")) do
            val symbols = parent.querySelectorAll(s"[id ^= '$prefix']").map(e => Symbol(e.id))
            inheritanceTabState.activeSymbols.extend(symbols)
            inheritanceTabState.canvasSelection.extend(symbols)
        }
      )
    )

  private def renderNamespace(inheritanceTabState: InheritanceTabState, $open: Var[Map[String, Boolean]])(s: String, ns: Namespace) =
    val symStr = ns.symbol.toString
    val uri = encodeURIComponent(symStr)
    val $isActive = inheritanceTabState.$activeSymbols.signal.map(_.contains(ns.symbol))

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
            cls.toggle("bg-blue-200 rounded", "noop") <-- $isActive,
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
      // namespaces start closed
      control =
        Collapsable.Control.from(symStr, initial = false, $open)
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

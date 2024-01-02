package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.EventStream
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.shared.inheritance.InheritanceGraph
import org.jpablo.typeexplorer.shared.models.{Namespace, NamespaceKind, GraphSymbol}
import org.jpablo.typeexplorer.shared.tree.Tree
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.jpablo.typeexplorer.ui.extensions.*
import org.jpablo.typeexplorer.ui.domUtils.{details, summary, open}
import org.scalajs.dom
import org.scalajs.dom.{HTMLAnchorElement, HTMLLIElement, HTMLUListElement}

/** Builds a collapsable tree based on the given inheritance diagram.
  *
  * @param diagrams
  *   The diagram
  * @return
  *   A List of trees, one for each top level package in the diagram: e.g. ["com..., ", "java.io..."]
  */
def PackagesTree(
    tabState: InheritanceTabState,
    diagrams: EventStream[InheritanceGraph]
): EventStream[ReactiveHtmlElement[HTMLUListElement]] =
  val treeElement = TreeElement(tabState)
  for diagram <- diagrams
  yield treeElement
    .render(diagram.toTrees.children)
    .amend(
      cls := "menu menu-xs rounded-box rounded-lg w-full mb-1"
    )

val fullLabel: Tree[Namespace] => String =
  case Tree.Branch(_, path, _) => path.mkString("/")
  case Tree.Leaf(_, ns)        => ns.symbol.toString

val leafSymbols: Tree[Namespace] => List[GraphSymbol] =
  case Tree.Branch(_, _, children) => children.flatMap(leafSymbols)
  case Tree.Leaf(_, ns)            => List(ns.symbol)

class TreeElement(tabState: InheritanceTabState):
  private val openBranches = Var(Set.empty[List[String]])

  def render(
      trees: List[Tree[Namespace]]
  ): ReactiveHtmlElement[HTMLUListElement] =
    ul(
      children <--
        EventStream.fromSeq(List(trees)).split(fullLabel)(TreeRow)
    )

  private def TreeRow(
      id:         String,
      initial:    Tree[Namespace],
      treeSignal: Signal[Tree[Namespace]]
  ): ReactiveHtmlElement[HTMLLIElement] =
    li(
      child <--
        treeSignal.map:
          case l: Tree.Leaf[?]   => PackageMember(l.data)
          case b: Tree.Branch[?] => Package(b.label, b.path, b.children)
    )

  private def PackageMember(ns: Namespace) =
    val isActive = tabState.activeSymbols.signal.map(_.contains(ns.symbol))
    a(
      idAttr := ns.symbol.toString,
      cls    := "font-['JetBrains_Mono'] rounded-box p-1 m-px cursor-pointer",
      cls.toggle("active") <-- isActive,
      TreeElement.stereotype(ns),
      div(
        cls := "truncate",
        ns.displayName
      ),
      onClick.preventDefault.stopPropagation --> { _ =>
        tabState.activeSymbols.toggle(ns.symbol)
        tabState.canvasSelection.toggle(ns.symbol)
      }
    )

  def Package(
      packageLabel: String,
      packagePath:  List[String],
      children:     List[Tree[Namespace]]
  ) =
    details(
      open <-- openBranches.signal.map(s => s.contains(packagePath)),
      onClick.preventDefault.stopPropagation --> openBranches.update(
        _.toggle(packagePath)
      ),
      summary(
        span(
          cls := "link link-hover",
          packageLabel,
          onClick.preventDefault.stopPropagation --> {
            val symbols = children.flatMap(leafSymbols)
            tabState.activeSymbols.extend(symbols)
            tabState.canvasSelection.extend(symbols.toSet)
          }
        )
      ),
      render(children)
    )
end TreeElement

object TreeElement:

  /** The "stereotype" is an element indicating which kind of namespace we have: an Object, a Class, etc.
    */
  private def stereotype(ns: Namespace) =
    def circle(s: String, color: String) =
      div(
        cls := "avatar placeholder",
        div(
          cls             := s"text-neutral-content rounded-full w-4",
          backgroundColor := color,
          span(cls := "text-xs", s)
        )
      )

    ns.kind match
      case NamespaceKind.Object        => circle("o", "rgb(44, 107, 141)")
      case NamespaceKind.PackageObject => circle("p", "lightblue")
      case NamespaceKind.Class         => circle("c", "rgb(68, 173, 125)")
      case NamespaceKind.Trait         => circle("t", "rgb(24, 170, 207)")
      case other                       => div(other.toString)

end TreeElement

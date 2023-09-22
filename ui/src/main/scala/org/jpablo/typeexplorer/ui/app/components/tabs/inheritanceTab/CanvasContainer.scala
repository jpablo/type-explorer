package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.{
  CanvasSelectionOps,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.svgGroupElement.{
  ClusterElement,
  LinkElement,
  NamespaceElement,
  SvgGroupElement,
  path
}
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

private def CanvasContainer(
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    tabState: InheritanceTabState
) =
  div(
    cls := "h-full overflow-auto p-1 row-start-2 row-end-3",
    backgroundImage := "radial-gradient(hsla(var(--bc)/.2) .5px,hsla(var(--b2)/1) .5px)",
    backgroundSize := "5px 5px",
    child <-- inheritanceSvgDiagram.map: diagram =>
      val selection = tabState.canvasSelection.now()
      diagram.select(selection)
      // remove elements not present in the new diagram (such elements did exist in the previous diagram)
      tabState.canvasSelection.remove(selection -- diagram.elementSymbols)
//      tabState.canvasSelectionR.update(
//        _ -- (selection -- diagram.elementSymbols)
//      )
      diagram.toLaminar
    ,
    onClick.preventDefault
      .compose(_.withCurrentValueOf(inheritanceSvgDiagram)) --> handleSvgClick(
      tabState.canvasSelection
    ).tupled
  )

private def handleSvgClick(canvasSelection: CanvasSelectionOps)(
    ev: dom.MouseEvent,
    diagram: InheritanceSvgDiagram
): Unit =

  // 1. Identify and parse the element that was clicked
  val selectedElement: Option[SvgGroupElement] =
    ev.target
      .asInstanceOf[dom.Element]
      .path
      .takeWhile(_.isInstanceOf[dom.SVGElement])
      .map(SvgGroupElement.fromDomSvgElement)
      .collectFirst { case Some(g) => g }

  // 2. Update selected element's appearance
  selectedElement match
    case Some(g) =>
      g match

        case _: (LinkElement | NamespaceElement) =>
          if ev.metaKey then
            g.toggle()
            canvasSelection.toggle(g.symbol)
          else
            diagram.unselectAll()
            g.select()
            canvasSelection.replace(g.symbol)

        case cluster: ClusterElement =>
          if !ev.metaKey then
            diagram.unselectAll()
            canvasSelection.clear()
          // select all boxes inside this cluster
          for ns <- diagram.clusterElements(cluster) do
            ns.select()
            canvasSelection.extend(ns.symbol)

    case None =>
      diagram.unselectAll()
      canvasSelection.clear()

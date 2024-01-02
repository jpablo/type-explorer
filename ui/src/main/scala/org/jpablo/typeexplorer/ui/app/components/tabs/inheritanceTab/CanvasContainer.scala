package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.CanvasSelectionOps
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.svgGroupElement.*
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def CanvasContainer(
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    canvasSelection:       CanvasSelectionOps
) =
  div(
    cls             := "h-full w-full overflow-auto relative p-1 z-10",
    backgroundImage := "radial-gradient(oklch(var(--bc)/.2) .5px,oklch(var(--b2)/1) .5px)",
    backgroundSize  := "5px 5px",
    child <-- inheritanceSvgDiagram.map: diagram =>
      val selection = canvasSelection.now()
      diagram.select(selection)
      // remove elements not present in the new diagram (such elements did exist in the previous diagram)
      canvasSelection.remove(selection -- diagram.elementSymbols)
      diagram.toLaminar
    ,
    onClick.preventDefault
      .compose(_.withCurrentValueOf(inheritanceSvgDiagram)) --> handleSvgClick(
      canvasSelection
    ).tupled
  )

private def handleSvgClick(canvasSelection: CanvasSelectionOps)(
    ev:      dom.MouseEvent,
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

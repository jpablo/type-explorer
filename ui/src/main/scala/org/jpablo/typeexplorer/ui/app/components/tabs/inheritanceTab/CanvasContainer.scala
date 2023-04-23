package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement


private def CanvasContainer(inheritanceSvgDiagram: Signal[InheritanceSvgDiagram], inheritanceTabState: InheritanceTabState) =
  div(cls := "h-full overflow-auto border-t border-slate-300 p-1 row-start-2 row-end-3",
    backgroundImage := "radial-gradient(hsla(var(--bc)/.2) .5px,hsla(var(--b2)/1) .5px)",
    backgroundSize := "5px 5px",
    child <-- inheritanceSvgDiagram.map: diagram =>
      val selection = inheritanceTabState.canvasSelectionR.now()
      diagram.select(selection)
      // remove elements not present in the new diagram (such elements did exist in the previous diagram)
      inheritanceTabState.canvasSelectionR.update(_ -- (selection -- diagram.elementSymbols))
      diagram.toLaminar,
    onClick.preventDefault.compose(_.withCurrentValueOf(inheritanceSvgDiagram)) -->
      handleSvgClick(inheritanceTabState).tupled,
  )

private def handleSvgClick
(inheritanceTabState: InheritanceTabState)
  (ev: dom.MouseEvent, diagram: InheritanceSvgDiagram): Unit =
  val selectedElement: Option[SvgGroupElement] =
    ev.target.asInstanceOf[dom.Element].path
      .takeWhile(_.isInstanceOf[dom.SVGElement])
      .map(e => NamespaceElement.from(e) orElse ClusterElement.from(e) orElse LinkElement.from(e))
      .collectFirst { case Some(g) => g }

  selectedElement match
    case Some(g) => g match

      case _: (LinkElement | NamespaceElement) =>
        if ev.metaKey then
          g.toggle()
          inheritanceTabState.canvasSelection.toggle(g.symbol)
        else
          diagram.unselectAll()
          g.select()
          inheritanceTabState.canvasSelection.replace(g.symbol)

      case cluster: ClusterElement =>
        if !ev.metaKey then
          diagram.unselectAll()
          inheritanceTabState.canvasSelection.clear()
        // select all boxes inside this cluster
        for ns <- diagram.clusterElements(cluster) do
          ns.select()
          inheritanceTabState.canvasSelection.extend(ns.symbol)

    case None =>
      diagram.unselectAll()
      inheritanceTabState.canvasSelection.clear()

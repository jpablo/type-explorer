package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.{EventStream, Signal}
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.components.state.CanvasSelectionOps
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.svgGroupElement.*
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

def CanvasContainer(
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    canvasSelection:       CanvasSelectionOps,
    zoomValue:             Var[Double],
    fitDiagram:            EventStream[Unit]
) =
  val svgSize =
    zoomValue.signal
      .combineWith(inheritanceSvgDiagram)
      .map((z, diagram) => (z * diagram.origW, z * diagram.origH))
  div(
    cls             := "te-parent p-1 z-10",
    backgroundImage := "radial-gradient(oklch(var(--bc)/.2) .5px,oklch(var(--b2)/1) .5px)",
    backgroundSize  := "5px 5px",
    onClick.preventDefault
      .compose(_.withCurrentValueOf(inheritanceSvgDiagram)) --> handleSvgClick(canvasSelection).tupled,
    inContext { svgParent =>
      def parentSizeNow() = (svgParent.ref.offsetWidth, svgParent.ref.offsetHeight)
      // scale the diagram to fit the parent container whenever the "fit" button is clicked
      fitDiagram
        .sample(inheritanceSvgDiagram)
        .foreach { diagram =>
          val (parentWidth, parentHeight) = parentSizeNow()
          val z = math.min(parentWidth / diagram.origW, parentHeight / diagram.origH)
          zoomValue.set(z)
          // TODO: is there a way to avoid unsafeWindowOwner here?
        }(unsafeWindowOwner)

      // Center the diagram *only* when the parent container is wider than the svg diagram.
      // Otherwise the left side of the diagram is not accessible with scroll bars (as they only extend to the right).
      val flexJustification =
        windowEvents(_.onResize).mapToUnit
          .startWith(())
          .combineWith(svgSize)
          .map: (svgWidth, svgHeight) =>
            val (parentWidth, parentHeight) = parentSizeNow()
            Seq(
              if parentWidth < svgWidth then "justify-start" else "justify-center",
              if parentHeight < svgHeight then "items-start" else "items-center"
            )
      Seq(
        cls <-- flexJustification,
        child <-- inheritanceSvgDiagram.map: diagram =>
          val selection = canvasSelection.now()
          diagram.select(selection)
          // remove elements not present in the new diagram (such elements did exist in the previous diagram)
          canvasSelection.remove(selection -- diagram.elementSymbols)

          diagram.toLaminar.amend(
            svg.width <-- svgSize.map(_._1.toString + "px"),
            svg.height <-- svgSize.map(_._2.toString + "px")
          )
      )
    }
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

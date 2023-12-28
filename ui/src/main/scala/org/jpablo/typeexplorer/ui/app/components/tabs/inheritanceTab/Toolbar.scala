package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceGraph, toPlantUML}
import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState
}
import org.jpablo.typeexplorer.ui.daisyui.*
import org.jpablo.typeexplorer.ui.domUtils
import org.jpablo.typeexplorer.ui.domUtils.dataTip
import org.jpablo.typeexplorer.ui.widgets.Icons.*
import org.scalajs.dom
import org.scalajs.dom.{HTMLDivElement, HTMLElement}

def Toolbar(
    appState: AppState,
    tabState: InheritanceTabState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    containerBoundingClientRect: => dom.DOMRect,
    packagesDialogOpen: Var[Boolean]
) =
  val zoomValue = Var(100.0)
  val minZoom = 25
  val maxZoom = 400
  inheritanceSvgDiagram
    .combineWith(zoomValue.signal)
    .foreach { (diagram, zoom) =>
      val actualZoom = Math.min(maxZoom, Math.max(minZoom, zoom))
      diagram.absoluteZoom(actualZoom)
    }(owner = unsafeWindowOwner)

  div(
    cls := "shadow bg-base-100 rounded-box flex items-center gap-4 p-0.5 absolute top-1 left-2/4 -translate-x-2/4 z-10",
    // -------- package selector --------
    Join(
      div(
        cls := "flex-none tooltip tooltip-bottom",
        dataTip := "Package Selector",
        button.boxesIcon.amend(
          cls := "btn btn-ghost btn-sm",
          onClick --> packagesDialogOpen.set(true)
        )
      )
    ),
    // -------- fields and signatures --------
    Join(
      LabeledCheckbox(
        id = "fields-checkbox-1",
        labelStr = "fields",
        isChecked = tabState.diagramOptions.signal.map(_.showFields),
        clickHandler = tabState.diagramOptions
          .updater(_.modify(_.showFields).setTo(_)),
        toggle = true
      )
    ),
    // -------- actions toolbar --------
    Join(
      Button(
        "remove all",
        onClick --> tabState.activeSymbols.clear()
      ).tiny,
      div(
        cls := "dropdown dropdown-hover",
        label(
          tabIndex := 0,
          cls := "btn btn-xs join-item whitespace-nowrap",
          "Copy as"
        ),
        ul(
          tabIndex := 0,
          cls := "dropdown-content z-[1] menu p-2 shadow bg-base-100 rounded-box w-52",
          li(
            a(
              "svg",
              onClick.compose(_.sample(inheritanceSvgDiagram)) --> { diagram =>
                dom.window.navigator.clipboard.writeText(diagram.toSVGText)
              }
            )
          ),
          li(
            a(
              "plantuml",
              onPlantUMLClicked(appState.fullGraph, tabState)
            )
          )
        )
      ),
      Button(
        "fit",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> { diagram =>
          zoomValue.set(
            100 * diagram.getFitProportion(containerBoundingClientRect)
          )
        }
      ).tiny
    ),
    // ----------
    Join(
      Button(
        span.dashIcon,
        onClick --> zoomValue.update(_ * 0.9)
      ).tiny,
      input(
        tpe := "range",
        cls := "bg-base-200",
        domUtils.min := minZoom,
        domUtils.max := maxZoom,
        value := "100",
        controlled(
          value <-- zoomValue.signal.map(_.toString),
          onInput.mapToValue.map(_.toDouble) --> zoomValue
        )
      ),
      Button(
        span.plusIcon,
        onClick --> zoomValue.update(_ * 1.1)
      ).tiny
    )
  )

private def onPlantUMLClicked(
    fullGraph: Signal[InheritanceGraph],
    tabState: InheritanceTabState
) =
  onClick.compose(
    _.sample(
      fullGraph,
      tabState.activeSymbols.signal,
      tabState.diagramOptions
    )
  ) --> { (fullDiagram: InheritanceGraph, activeSymbols, options) =>
    dom.window.navigator.clipboard.writeText(
      fullDiagram
        .subdiagram(activeSymbols.keySet)
        .toPlantUML(activeSymbols, options)
        .diagram
    )
  }

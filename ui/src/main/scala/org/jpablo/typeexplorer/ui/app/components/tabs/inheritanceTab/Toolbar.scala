package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.ui.app.components.state.{
  AppState,
  InheritanceTabState,
  Project
}
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.daisyui.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.{HTMLDivElement, HTMLElement}
import org.jpablo.typeexplorer.shared.inheritance.{
  DiagramOptions,
  InheritanceGraph,
  toPlantUML
}
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.ui.domUtils

def Toolbar(
    appState: AppState,
    tabState: InheritanceTabState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    containerBoundingClientRect: => dom.DOMRect
) =
//  val tabState = appState.inheritanceTab
  val modifySelection = modifyLens[Project]
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
    cls := "bg-base-100 rounded-box flex items-center gap-4 ml-2 absolute top-0",
    // -------- fields and signatures --------
    Join(
      OptionsToggle(
        "fields-checkbox-1",
        "fields",
        _.pages.head.diagramOptions.showFields,
        modifySelection(_.pages.at(0).diagramOptions.showFields),
        appState
      )
//      OptionsToggle(
//        "fields-checkbox-2",
//        "signatures",
//        _.pages.head.diagramOptions.showSignatures,
//        modifySelection(_.pages.at(0).diagramOptions.showSignatures),
//        appState
//      )
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
          cls := "btn btn-xs join-item",
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
              onPlantUMLClicked(appState /*, tabState*/ )
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
      ).tiny,
      Button(
        "-",
        onClick --> zoomValue.update(_ * 0.9)
      ).tiny,
      input(
        tpe := "range",
        domUtils.min := minZoom,
        domUtils.max := maxZoom,
        value := "100",
        controlled(
          value <-- zoomValue.signal.map(_.toString),
          onInput.mapToValue.map(_.toDouble) --> zoomValue
        )
      ),
      Button(
        "+",
        onClick --> zoomValue.update(_ * 1.1)
      ).tiny
    )
  )

private def OptionsToggle(
    id: String,
    labelStr: String,
    field: Project => Boolean,
    modifyField: PathLazyModify[Project, Boolean],
    state: AppState
) =
  LabeledCheckbox(
    id = id,
    labelStr = labelStr,
    isChecked = state.activeProject.signal.map(field),
    clickHandler = state.activeProject.updater[Boolean]((config, b) =>
      modifyField.setTo(b)(config)
    ),
    toggle = true
  )

private def onPlantUMLClicked(
    appState: AppState
//    tabState: InheritanceTabState
) =
  onClick.compose(
    _.sample(
      appState.fullGraph,
//      tabState.activeSymbols.signal,
      appState.diagramOptions
    )
  ) --> { (fullDiagram: InheritanceGraph, options) =>
    dom.window.navigator.clipboard.writeText(
      fullDiagram
//        .subdiagram(symbols.keySet)
        .subdiagram(Set.empty)
        .toPlantUML(
          Map.empty,
          options.head
        )
        .diagram
    )
  }

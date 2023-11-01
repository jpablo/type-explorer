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
  InheritanceDiagram,
  toPlantUML
}
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols

def Toolbar(
    appState: AppState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    containerBoundingClientRect: => dom.DOMRect
) =
  val tabState = appState.inheritanceTab
  val modifySelection = modifyLens[Project]
  div(
    cls := "bg-base-100 rounded-box flex items-center gap-4 ml-2 absolute top-0",
    // -------- fields and signatures --------
    Join(
      OptionsToggle(
        "fields-checkbox-1",
        "fields",
        _.diagramOptions.showFields,
        modifySelection(_.diagramOptions.showFields),
        appState
      ),
      OptionsToggle(
        "fields-checkbox-2",
        "signatures",
        _.diagramOptions.showSignatures,
        modifySelection(_.diagramOptions.showSignatures),
        appState
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
              onPlantUMLClicked(appState, tabState)
            )
          )
        )
      ),
      Button(
        "fit",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> { diagram =>
          diagram.fitToRect(containerBoundingClientRect)
        }
      ).tiny,
      Button(
        "zoom +",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> (_.zoom(1.1))
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
    appState: AppState,
    tabState: InheritanceTabState
) =
  onClick.compose(
    _.sample(
      tabState.fullInheritanceDiagram,
      tabState.activeSymbols.signal,
      appState.diagramOptions
    )
  ) --> { (fullDiagram: InheritanceDiagram, symbols: ActiveSymbols, options) =>
    dom.window.navigator.clipboard.writeText(
      fullDiagram
        .subdiagram(symbols.keySet)
        .toPlantUML(symbols, options)
        .diagram
    )
  }

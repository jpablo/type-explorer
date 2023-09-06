package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.ui.app.components.state.{AppConfig, AppState}
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.daisyui.*
import com.raquo.laminar.api.features.unitArrows
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.{HTMLDivElement, HTMLElement}
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, toPlantUML}
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols

def Toolbar(
    appState: AppState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram],
    containerBoundingClientRect: => dom.DOMRect
) =
  val state = appState.inheritanceTabState
  val modifySelection = modifyLens[AppConfig]
  div(
    cls := "flex items-center gap-4 ml-2",
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
    Join(
      Button(
        "remove all",
        onClick --> state.activeSymbols.clear()
      ).tiny.amend(cls := "join-item"),
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
              onClick.compose(
                _.sample(
                  state.fullInheritanceDiagramR,
                  state.activeSymbolsR.signal,
                  appState.appConfig.signal.map(_.diagramOptions)
                )
              ) --> { case (fullDiagram: InheritanceDiagram, symbols: ActiveSymbols, options) =>
                dom.window.navigator.clipboard.writeText(
                  fullDiagram.subdiagram(symbols.keySet).toPlantUML(symbols, options).diagram
                )
              }
            )
          )
//          li(a("graphviz"))
        )
      ),
      Button(
        "fit",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> { diagram =>
          diagram.fitToRect(containerBoundingClientRect)
        }
      ).tiny.amend(cls := "join-item"),
      Button(
        "zoom +",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> (_.zoom(1.1))
      ).tiny.amend(cls := "join-item")
    )
  )

private def OptionsToggle(
    id: String,
    labelStr: String,
    field: AppConfig => Boolean,
    modifyField: PathLazyModify[AppConfig, Boolean],
    appState: AppState
) =
  LabeledCheckbox(
    id = id,
    labelStr = labelStr,
    isChecked = appState.appConfig.signal.map(field),
    clickHandler = appState.appConfig.updater[Boolean]((config, b) =>
      modifyField.setTo(b)(config)
    ),
    toggle = true
  )
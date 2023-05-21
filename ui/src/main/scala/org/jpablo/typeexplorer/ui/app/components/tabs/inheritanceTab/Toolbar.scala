package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.ui.app.components.state.{AppConfig, AppState}
import org.jpablo.typeexplorer.ui.daisyui.*
import org.scalajs.dom


def Toolbar(appState: AppState, inheritanceSvgDiagram: Signal[InheritanceSvgDiagram], containerBoundingClientRect: => dom.DOMRect) =
  val modifySelection = modifyLens[AppConfig]
  div(cls := "flex gap-4 ml-2",
    ButtonGroup(
      OptionsToggle("fields-checkbox-1", "fields", _.diagramOptions.showFields, modifySelection(_.diagramOptions.showFields), appState),
      OptionsToggle("fields-checkbox-2", "signatures", _.diagramOptions.showSignatures, modifySelection(_.diagramOptions.showSignatures), appState),
    ),
    ButtonGroup(
      Button("remove all",
        onClick --> appState.inheritanceTabState.activeSymbols.clear()
      ).tiny,
      Button("Copy to clipboard",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> { diagram =>
          dom.window.navigator.clipboard.writeText(diagram.toSVG)
        }
      ).tiny,
      Button("Copy state",
        onClick --> { _ =>
          dom.window.navigator.clipboard.writeText(appState.toString)
        }
      ).tiny,
      Button("fit",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> (_.fitToRect(containerBoundingClientRect))
      ).tiny,
      Button("zoom +",
        onClick.compose(_.sample(inheritanceSvgDiagram)) --> (_.zoom(1.1))
      ).tiny
    )
  )

private def OptionsToggle(
  id         : String,
  labelStr   : String,
  field      : AppConfig => Boolean,
  modifyField: PathLazyModify[AppConfig, Boolean],
  appState   : AppState
) =
  LabeledCheckbox(
    id = id,
    labelStr = labelStr,
    isChecked = appState.appConfig.signal.map(field),
    clickHandler = appState.appConfig.updater[Boolean]((config, b) => modifyField.setTo(b)(config)),
    toggle = true
  )


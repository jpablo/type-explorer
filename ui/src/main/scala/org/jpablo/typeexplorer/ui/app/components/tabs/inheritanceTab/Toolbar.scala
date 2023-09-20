package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.ui.app.components.state.{ProjectConfig, Project}
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
    project                    : Project,
    inheritanceSvgDiagram      : Signal[InheritanceSvgDiagram],
    containerBoundingClientRect: => dom.DOMRect
) =
  val state = project.inheritanceTabState
  val modifySelection = modifyLens[ProjectConfig]
  div(
    cls := "flex items-center gap-4 ml-2 border-b border-slate-300",
    Join(
      OptionsToggle(
        "fields-checkbox-1",
        "fields",
        _.diagramOptions.showFields,
        modifySelection(_.diagramOptions.showFields),
        project
      ),
      OptionsToggle(
        "fields-checkbox-2",
        "signatures",
        _.diagramOptions.showSignatures,
        modifySelection(_.diagramOptions.showSignatures),
        project
      )
    ),
    Join(
      Button(
        "remove all",
        onClick --> state.activeSymbols.clear()
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
              onClick.compose(
                _.sample(
                  state.fullInheritanceDiagramR,
                  state.activeSymbolsR.signal,
                  project.projectConfig.signal.map(_.diagramOptions)
                )
              ) --> { case (fullDiagram: InheritanceDiagram, symbols: ActiveSymbols, options) =>
                dom.window.navigator.clipboard.writeText(
                  fullDiagram.subdiagram(symbols.keySet).toPlantUML(symbols, options).diagram
                )
              }
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
    field: ProjectConfig => Boolean,
    modifyField: PathLazyModify[ProjectConfig, Boolean],
    project: Project
) =
  LabeledCheckbox(
    id = id,
    labelStr = labelStr,
    isChecked = project.projectConfig.signal.map(field),
    clickHandler = project.projectConfig.updater[Boolean]((config, b) =>
      modifyField.setTo(b)(config)
    ),
    toggle = true
  )

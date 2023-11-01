package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.daisyui.*
import org.scalajs.dom

private def SelectionSidebar(
    appState: AppState,
    inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]
) =
  val tabState = appState.inheritanceTab
  val selectionEmpty =
    tabState.canvasSelection.signal.map(_.isEmpty)
  div(
    cls := "absolute right-0 top-2",
    ul(
      cls := "menu bg-base-100 rounded-box m-2 p-0",
      li(
        h2(cls := "menu-title", span("selection")),
        ul(
          // ----- remove selection -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Remove",
              disabled <-- selectionEmpty,
              tabState.activeSymbols.applyOnSelection((all, sel) => all -- sel)(
                onClick
              )
            )
          ),
          // ----- remove complement -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Keep",
              disabled <-- selectionEmpty,
              tabState.activeSymbols.applyOnSelection((all, sel) =>
                all.filter((k, _) => sel.contains(k))
              )(onClick)
            )
          ),
          // ----- copy as svg -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Copy as SVG",
              disabled <-- selectionEmpty,
              onClick.compose(
                _.sample(inheritanceSvgDiagram, tabState.canvasSelectionR)
              ) --> { (svgDiagram, canvasSelection) =>
                val id = canvasSelection.head.toString()
                dom.window.navigator.clipboard.writeText(svgDiagram.toSVGText(canvasSelection))
              }
            )
          ),
          // ----- augment selection with parents -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Add parents",
              disabled <-- selectionEmpty,
              tabState.activeSymbols.addSelectionParents(onClick)
            )
          ),
          // ----- augment selection with children -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Add children",
              disabled <-- selectionEmpty,
              tabState.activeSymbols.addSelectionChildren(onClick)
            )
          ),
          // ----- add selection to set of hidden symbols -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Hide",
              disabled <-- selectionEmpty,
              onClick -->
                appState.updateActiveProject:
                  _.modify(_.diagramOptions.hiddenSymbols)
                    .using(_ ++ tabState.canvasSelection.now())
            )
          ),
          // ----- select parents -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Select parents",
              disabled <-- selectionEmpty,
              onClick.compose(
                _.sample(
                  tabState.fullInheritanceDiagram,
                  inheritanceSvgDiagram
                )
              ) -->
                tabState.canvasSelection.selectParents.tupled
            )
          ),
          // ----- select children -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            a(
              "Select children",
              onClick.compose(
                _.sample(
                  tabState.fullInheritanceDiagram,
                  inheritanceSvgDiagram
                )
              ) -->
                tabState.canvasSelection.selectChildren.tupled
            )
          ),
          // ----- show fields -----
          li(
            cls.toggle("disabled") <-- selectionEmpty,
            LabeledCheckbox(
              id = "fields-checkbox-3",
              labelStr = "Show fields",
              isChecked = tabState.activeSymbolsR.signal
                .combineWith(tabState.canvasSelectionR.signal)
                .map: (activeSymbols, selection) =>
                  val activeSelection =
                    activeSymbols.filter((s, _) => selection.contains(s))
                  // true when activeSelection is nonEmpty AND every option exists and showFields == true
                  activeSelection.nonEmpty && activeSelection.forall((_, o) =>
                    o.exists(_.showFields)
                  )
              ,
              isDisabled = selectionEmpty,
              clickHandler = Observer: b =>
                tabState.activeSymbols.updateSelectionOptions(
                  _.copy(showFields = b)
                ),
              toggle = true
            )
          )
        )
      )
    )
  )

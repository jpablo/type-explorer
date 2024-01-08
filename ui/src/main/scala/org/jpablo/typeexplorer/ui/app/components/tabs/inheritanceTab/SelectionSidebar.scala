package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.airstream.core.Signal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.shared.inheritance.InheritanceGraph
import org.jpablo.typeexplorer.ui.app
import org.jpablo.typeexplorer.ui.app.components.state.*
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.ui.daisyui.*
import org.scalajs.dom

def SelectionSidebar(
    appState: AppState,
    tabState: InheritanceTabState
) =

  val activeSymbols: Signal[ActiveSymbols] =
    tabState.activeSymbols.signal

  val selectionEmpty =
    tabState.canvasSelection.signal.map(_.isEmpty)
  div(
    cls := "absolute right-0 top-2 z-10",
    selectionEmpty.childWhenFalse(
      ul(
        cls := "menu shadow bg-base-100 rounded-box m-2 p-0",
        li(
          h2(cls := "menu-title", span("selection")),
          ul(
            // ----- remove selection -----
            li(
              cls.toggle("disabled") <-- selectionEmpty,
              a(
                "Remove",
                disabled <-- selectionEmpty,
                tabState.activeSymbols.applyOnSelection((all, sel) => all -- sel)(onClick)
              )
            ),
            // ----- remove complement -----
            li(
              cls.toggle("disabled") <-- selectionEmpty,
              a(
                "Keep",
                disabled <-- selectionEmpty,
                tabState.activeSymbols.applyOnSelection((all, sel) => all.filter((k, _) => sel.contains(k)))(onClick)
              )
            ),
            // ----- copy as svg -----
            li(
              cls.toggle("disabled") <-- selectionEmpty,
              a(
                "Copy as SVG",
                disabled <-- selectionEmpty,
                onClick.compose(
                  _.sample(tabState.inheritanceSvgDiagram, tabState.canvasSelection.signal)
                ) --> { (svgDiagram, canvasSelection) =>
                  dom.window.navigator.clipboard
                    .writeText(svgDiagram.toSVGText(canvasSelection))
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
                    _.modify(_.projectSettings.hiddenSymbols)
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
                    appState.fullGraph,
                    tabState.inheritanceSvgDiagram,
                    activeSymbols
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
                    appState.fullGraph,
                    tabState.inheritanceSvgDiagram,
                    activeSymbols
                  )
                ) -->
                  tabState.canvasSelection.selectChildren.tupled
              )
            ),
            // ----- show fields -----
            li(
              cls.toggle("disabled") <-- selectionEmpty,
              LabeledCheckbox(
                id       = "fields-checkbox-3",
                labelStr = "Show fields",
                isChecked = tabState.activeSymbolsV.signal
                  .combineWith(tabState.canvasSelection.signal)
                  .map: (activeSymbols, selection) =>
                    val activeSelection =
                      activeSymbols.filter((s, _) => selection.contains(s))
                    // true when activeSelection is nonEmpty AND every option exists and showFields == true
                    activeSelection.nonEmpty && activeSelection.forall((_, o) => o.exists(_.showFields))
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
  )

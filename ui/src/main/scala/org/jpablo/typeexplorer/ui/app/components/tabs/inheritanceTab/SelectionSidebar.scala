package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.daisyui.*


private def SelectionSidebar(appState: AppState, inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]) =
  val state = appState.inheritanceTabState
  val selectionEmpty = state.canvasSelectionR.signal.map(_.isEmpty)
  div(cls := "row-start-1 row-end-3 border-l border-slate-300 col-start-4 col-end-5",
    ul(cls := "menu menu-compact",
      li(cls := "menu-title",
        span("selection")
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Remove", disabled <-- selectionEmpty, state.applyOnSelection((all, sel) => all -- sel)(onClick))
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Keep", disabled <-- selectionEmpty, state.applyOnSelection((all, sel) => all.filter((k, _) => sel.contains(k)))(onClick))
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Add parents", disabled <-- selectionEmpty, state.addSelectionParents(onClick))
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Add children", disabled <-- selectionEmpty, state.addSelectionChildren(onClick))
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Hide", disabled <-- selectionEmpty,
          onClick -->
            appState.appConfig.update:
              _.modify(_.diagramOptions.hiddenSymbols)
                .using(_ ++ state.canvasSelectionR.now())
        )
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Select parents", disabled <-- selectionEmpty,
          onClick.compose(_.sample(state.inheritanceDiagramR, inheritanceSvgDiagram)) -->
            state.canvasSelection.selectParents.tupled
        )
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        a("Select children",
          onClick.compose(_.sample(state.inheritanceDiagramR, inheritanceSvgDiagram)) -->
            state.canvasSelection.selectChildren.tupled
        )
      ),
      li(cls.toggle("disabled") <-- selectionEmpty,
        LabeledCheckbox(
          id = "fields-checkbox-3",
          labelStr = "Show fields",
          isChecked =
            state.activeSymbolsR.signal
              .combineWith(state.canvasSelectionR.signal)
              .map: (activeSymbols, selection) =>
                val activeSelection = activeSymbols.filter((s, _) => selection.contains(s))
                // true when activeSelection is nonEmpty AND every option exists and showFields == true
                activeSelection.nonEmpty && activeSelection.forall((_, o) => o.exists(_.showFields)),
          isDisabled = selectionEmpty,
          clickHandler = Observer: b =>
            state.activeSymbols.updateSelectionOptions(_.copy(showFields = b)),
          toggle = true
        )
      )
    )
  )

package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState, Project}
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.ui.daisyui.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceGraph, PackagesOptions}
import org.jpablo.typeexplorer.ui.extensions.*


def PackagesTreeComponent(appState: AppState, tabState: InheritanceTabState) =
  val showOptions = Var(false)
  val filterBySymbolName = Var("")
  val filteredDiagram: EventStream[InheritanceGraph] =
    appState.fullGraph
      .combineWith(
        appState.packagesOptions,
        filterBySymbolName.signal,
        // TODO: consider another approach where changing activeSymbols does not trigger
        // a full tree redraw, but just modifies the relevant nodes
        tabState.activeSymbols.signal
      )
      .changes
      .debounce(300)
      .map:
        (
            diagram: InheritanceGraph,
            packagesOptions: PackagesOptions,
            w: String,
            activeSymbols: ActiveSymbols
        ) =>
          diagram
            .orElse(w.isBlank, _.filterBySymbolName(w))
            .subdiagramByKinds(packagesOptions.nsKind)
            .orElse(
              !packagesOptions.onlyActive,
              _.subdiagram(activeSymbols.keySet)
            )
            .orElse(packagesOptions.onlyTests, _.filterBy(!_.inTest))

  div(
    cls := "bg-base-100 rounded-box overflow-auto p-1 z-10",
    // --- controls ---
    form(
      LabeledCheckbox(
        "show-options-toggle",
        "options",
        showOptions.signal,
        clickHandler = Observer(_ => showOptions.update(!_)),
        toggle = true
      ),
      showOptions.signal.childWhenTrue:
        Options(appState)
      ,
      Search(
        placeholder := "filter",
        controlled(
          value <-- filterBySymbolName,
          onInput.mapToValue --> filterBySymbolName
        )
      ).small
    ),
    div(
      cls := "overflow-auto mt-1",
      child <-- PackagesTree(tabState, filteredDiagram)
    )
  )

private def Options(appState: AppState) =
  div(
    cls := "card card-compact p-1 m-2 mb-2 border-slate-300 border-[1px]",
    div(
      cls := "card-body p-1",
      LabeledCheckbox(
        s"filter-by-active",
        "only active",
        isChecked = appState.packagesOptions.map(_.onlyActive),
        clickHandler = Observer: _ =>
          appState.updateActiveProject(
            _.modify(_.packagesOptions.onlyActive).using(!_)
          ),
        toggle = true
      ),
      hr(),
      LabeledCheckbox(
        s"filter-by-scope",
        "Tests",
        isChecked = appState.packagesOptions.map(_.onlyTests),
        clickHandler = Observer: _ =>
          appState.updateActiveProject(
            _.modify(_.packagesOptions.onlyTests).using(!_)
          ),
        toggle = true
      ),
      hr(),
      for kind <- models.NamespaceKind.values.toList
      yield LabeledCheckbox(
        id = s"show-ns-kind-$kind",
        kind.toString,
        isChecked = appState.packagesOptions
          .map(_.nsKind)
          .map(_.contains(kind)),
        clickHandler = Observer: b =>
          appState.updateActiveProject(
            _.modify(_.packagesOptions.nsKind)
              .using(_.toggleWith(kind, b))
          )
      )
    )
  )

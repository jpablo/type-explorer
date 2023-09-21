package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab

import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.ui.app.components.state.{Project, AppState, InheritanceTabState, PackagesOptions}
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.*
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.ui.daisyui.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.toggleWith


extension[A] (a: A)
  private def orElse(b: Boolean, f: A => A): A =
    if b then a else f(a)


private def PackagesTreeComponent(appState: AppState) =
  val showOptions = Var(false)
  val filterBySymbolName = Var("")
  val filteredDiagram: EventStream[InheritanceDiagram] =
    appState.inheritanceTabState.fullInheritanceDiagram
      .combineWith(
        appState.packagesOptions,
        filterBySymbolName.signal,
        appState.inheritanceTabState.activeSymbolsR.signal
      )
      .changes
      .debounce(300)
      .map:
        (
        diagram        : InheritanceDiagram,
        packagesOptions: PackagesOptions,
        w              : String,
        activeSymbols  : ActiveSymbols
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
    cls := "overflow-auto p-1 row-start-1 row-end-3 col-start-2 col-end-3 border-r border-slate-300 flex flex-col",
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
        Options(appState),
      Search(
        placeholder := "filter",
        controlled(
          value <-- filterBySymbolName,
          onInput.mapToValue --> filterBySymbolName
        )
      ).small
    ),
    div(
      cls := "overflow-auto",
      children <-- PackagesTree(appState.inheritanceTabState, filteredDiagram)
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
        isChecked =
          appState.packagesOptions.map(_.onlyActive),
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
        isChecked =
          appState.packagesOptions.map(_.onlyTests),
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

package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.{EventStream, Observer, Signal}
import com.raquo.airstream.eventbus.WriteBus
import com.raquo.airstream.state.StrictSignal
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.DiagramOptions
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.jpablo.typeexplorer.ui.app.components.state.{AppConfig, AppState, InheritanceTabState, PackagesOptions}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.PackagesTree
import org.jpablo.typeexplorer.ui.app.toggleWith
import org.jpablo.typeexplorer.ui.daisyui.*
import org.jpablo.typeexplorer.ui.widgets.Icons
import org.scalajs.dom
import org.scalajs.dom.{DOMRect, EventTarget, HTMLDivElement, console}

object InheritanceTab:

  def apply(appState: AppState, inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]) =
    val canvasContainer = CanvasContainer(inheritanceSvgDiagram, appState.inheritanceTabState)

    val showPackagesTree = Var(false)
    val setColumns =
      showPackagesTree.signal
        .switch((3, 4), (2, 4))
        .map((s, e) => s"col-start-$s col-end-$e")

    // --- grid container: 4 columns, 2 rows ---
    div(cls := "grid h-full grid-cols-[46px_1fr_4fr_0.75fr] grid-rows-[3em_auto]",
      LeftSideMenu(showPackagesTree),
      PackagesTreeComponent(appState).amend(cls.toggle("hidden") <-- !showPackagesTree.signal),
      Toolbar(appState, inheritanceSvgDiagram, canvasContainer.ref.getBoundingClientRect()).amend(cls <-- setColumns),
      canvasContainer.amend(cls <-- setColumns),
      SelectionSidebar(appState, inheritanceSvgDiagram)
    )

  private def LeftSideMenu(active: Var[Boolean]) =
    div(cls := "row-start-1 row-end-3 flex justify-center bg-slate-100 border-r border-slate-300",
      ul(cls := "menu menu-compact",
        li(cls.toggle("bg-primary") <-- active.signal,
          Icons.folder.amend(onClick --> active.toggle()),
        )
      )
    )

  extension[A] (a: A)
    private def orElse(b: Boolean, f: A => A): A =
      if b then a else f(a)


  private def PackagesTreeComponent(appState: AppState) =
    val showOptions = Var(false)
    val filterBySymbolName = Var("")
    val filteredDiagram: EventStream[InheritanceDiagram] =
      appState.inheritanceTabState.inheritanceDiagramR
        .combineWith(
          appState.appConfig.signal.map(_.packagesOptions),
          filterBySymbolName.signal,
          appState.inheritanceTabState.activeSymbolsR.signal
        )
        .changes
        .debounce(300)
        .map: (diagram: InheritanceDiagram, packagesOptions: PackagesOptions, w: String, activeSymbols: ActiveSymbols) =>
          diagram
            .orElse(w.isBlank, _.filterBySymbolName(w))
            .subdiagramByKinds(packagesOptions.nsKind)
            .orElse(!packagesOptions.onlyActive, _.subdiagram(activeSymbols.keySet))
            .orElse(packagesOptions.onlyTests, _.filterBy(!_.inTest))

    div(cls := "overflow-auto p-1 row-start-1 row-end-3 col-start-2 col-end-3 border-r border-slate-300 flex flex-col",
      // --- controls ---
      form(
        LabeledCheckbox("show-options-toggle", "options",
          showOptions.signal,
          clickHandler = Observer(_ => showOptions.update(!_)),
          toggle = true
        ),
        showOptions.signal.childWhenTrue:
          div(cls := "card card-compact p-1 mb-2 border-slate-300 border-[1px]",
            div(cls := "card-body p-1",
              LabeledCheckbox(s"filter-by-active", "only active",
                isChecked = appState.appConfig.signal.map(_.packagesOptions.onlyActive),
                clickHandler = Observer: _ =>
                  appState.updateAppConfig(_.modify(_.packagesOptions.onlyActive).using(!_)),
                toggle = true
              ),
              hr(),
              LabeledCheckbox(s"filter-by-scope", "Tests",
                isChecked = appState.appConfig.signal.map(_.packagesOptions.onlyTests),
                clickHandler = Observer: _ =>
                  appState.updateAppConfig(_.modify(_.packagesOptions.onlyTests).using(!_)),
                toggle = true
              ),
              hr(),
              for kind <- models.NamespaceKind.values.toList yield
                LabeledCheckbox(
                  id = s"show-ns-kind-$kind",
                  kind.toString,
                  isChecked = appState.appConfig.signal.map(_.packagesOptions.nsKind).map(_.contains(kind)),
                  clickHandler = Observer: b =>
                    appState.updateAppConfig(_.modify(_.packagesOptions.nsKind).using(_.toggleWith(kind, b)))
                )
            ),
          ),
        Search(placeholder := "filter", controlled(value <-- filterBySymbolName, onInput.mapToValue --> filterBySymbolName)).small
      ),
      div(cls := "overflow-auto", children <-- PackagesTree(appState.inheritanceTabState, filteredDiagram))
    )

  private def Toolbar(appState: AppState, inheritanceSvgDiagram: Signal[InheritanceSvgDiagram], containerBoundingClientRect: => dom.DOMRect) =
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
        Button("fit",
          onClick.compose(_.sample(inheritanceSvgDiagram)) --> (_.fitToRect(containerBoundingClientRect))
        ).tiny,
        Button("zoom +",
          onClick.compose(_.sample(inheritanceSvgDiagram)) --> (_.zoom(1.1))
        ).tiny
      )
    )

  private def CanvasContainer(inheritanceSvgDiagram: Signal[InheritanceSvgDiagram], inheritanceTabState: InheritanceTabState) =
    div(cls := "h-full overflow-auto border-t border-slate-300 p-1 row-start-2 row-end-3",
      backgroundImage := "radial-gradient(hsla(var(--bc)/.2) .5px,hsla(var(--b2)/1) .5px)",
      backgroundSize := "5px 5px",
      child <-- inheritanceSvgDiagram.map: diagram =>
        val selection = inheritanceTabState.canvasSelectionR.now()
        diagram.select(selection)
        // remove elements not present in the new diagram (such elements did exist in the previous diagram)
        inheritanceTabState.canvasSelectionR.update(_ -- (selection -- diagram.elementSymbols))
        diagram.toLaminar,
      onClick.preventDefault.compose(_.withCurrentValueOf(inheritanceSvgDiagram)) -->
        handleSvgClick(inheritanceTabState).tupled,
    )

  private def SelectionSidebar(appState: AppState, inheritanceSvgDiagram: Signal[InheritanceSvgDiagram]) =
    val inheritanceTabState = appState.inheritanceTabState
    val selectionEmpty = inheritanceTabState.canvasSelectionR.signal.map(_.isEmpty)
    div(cls := "row-start-1 row-end-3 border-l border-slate-300 col-start-4 col-end-5",
      ul(cls := "menu menu-compact",
        li(cls := "menu-title",
          span("selection")
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Remove", disabled <-- selectionEmpty, inheritanceTabState.applyOnSelection((all, sel) => all -- sel)(onClick))
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Keep", disabled <-- selectionEmpty, inheritanceTabState.applyOnSelection((all, sel) => all.filter((k, _) => sel.contains(k)))(onClick))
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Add parents", disabled <-- selectionEmpty, inheritanceTabState.addSelectionParents(onClick))
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Add children", disabled <-- selectionEmpty, inheritanceTabState.addSelectionChildren(onClick))
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Hide", disabled <-- selectionEmpty,
            onClick -->
              appState.appConfig.update:
                _.modify(_.diagramOptions.hiddenSymbols)
                  .using(_ ++ inheritanceTabState.canvasSelectionR.now())
          )
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Select parents", disabled <-- selectionEmpty,
            onClick.compose(_.sample(inheritanceTabState.inheritanceDiagramR, inheritanceSvgDiagram)) -->
              inheritanceTabState.canvasSelection.selectParents.tupled
          )
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          a("Select children",
            onClick.compose(_.sample(inheritanceTabState.inheritanceDiagramR, inheritanceSvgDiagram)) -->
              inheritanceTabState.canvasSelection.selectChildren.tupled
          )
        ),
        li(cls.toggle("disabled") <-- selectionEmpty,
          LabeledCheckbox(
            id = "fields-checkbox-3",
            labelStr = "Show fields",
            isChecked =
              inheritanceTabState.activeSymbolsR.signal
                .combineWith(inheritanceTabState.canvasSelectionR.signal)
                .map: (activeSymbols, selection) =>
                  val activeSelection = activeSymbols.filter((s, _) => selection.contains(s))
                  // true when activeSelection is nonEmpty AND every option exists and showFields == true
                  activeSelection.nonEmpty && activeSelection.forall((_, o) => o.exists(_.showFields)),
            isDisabled = selectionEmpty,
            clickHandler = Observer: b =>
              inheritanceTabState.activeSymbols.updateSelectionOptions(_.copy(showFields = b)),
            toggle = true
          )
        )
      )
    )

  private def handleSvgClick
    (inheritanceTabState: InheritanceTabState)
    (ev: dom.MouseEvent, diagram: InheritanceSvgDiagram): Unit =
    val selectedElement: Option[SvgGroupElement] =
      ev.target.asInstanceOf[dom.Element].path
        .takeWhile(_.isInstanceOf[dom.SVGElement])
        .map(e => NamespaceElement.from(e) orElse ClusterElement.from(e) orElse LinkElement.from(e))
        .collectFirst { case Some(g) => g }

    selectedElement match
      case Some(g) => g match

        case _: (LinkElement | NamespaceElement) =>
          if ev.metaKey then
            g.toggle()
            inheritanceTabState.canvasSelection.toggle(g.symbol)
          else
            diagram.unselectAll()
            g.select()
            inheritanceTabState.canvasSelection.replace(g.symbol)

        case cluster: ClusterElement =>
          if !ev.metaKey then
            diagram.unselectAll()
            inheritanceTabState.canvasSelection.clear()
          // select all boxes inside this cluster
          for ns <- diagram.clusterElements(cluster) do
            ns.select()
            inheritanceTabState.canvasSelection.extend(ns.symbol)

      case None =>
        diagram.unselectAll()
        inheritanceTabState.canvasSelection.clear()

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
      clickHandler = appState.appConfig.updater[Boolean]((config, b) => modifyField.setTo(b)(config)),
      toggle = true
    )

end InheritanceTab


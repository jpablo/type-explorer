package org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab


import com.raquo.airstream.core.{EventStream, Observer, Signal}
import com.raquo.airstream.eventbus.WriteBus
import com.raquo.airstream.state.StrictSignal
import com.raquo.domtypes.generic.codecs.StringAsIsCodec
import com.raquo.domtypes.jsdom.defs.events.TypedTargetMouseEvent
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.PackagesTree
import org.jpablo.typeexplorer.ui.daisyui.*
import io.laminext.syntax.core.*
import org.jpablo.typeexplorer.ui.app.toggleWith
import org.scalajs.dom
import org.scalajs.dom.{EventTarget, console}


object InheritanceTab:

  extension [A](a: A)
    def orElse(b: Boolean, f: A => A): A =
      if b then a else f(a)

  def build =
    for
      $inheritanceSvgDiagram <- AppState.$inheritanceSvgDiagram
      $diagram               <- AppState.$inheritanceDiagram
      packagesTree        <- PackagesTree.build
      inheritanceTabState <- AppState.inheritanceTabState
    yield
      val $filterBySymbolName = Var("")
      val $filterByNsKind     = Var(models.NamespaceKind.values.toSet)
      val $filterByActive     = Var(false)
      val $showOptions        = Var(false)
      val modifySelection = modifyLens[Options]
      val $filteredDiagram =
        $diagram
          .combineWith($filterBySymbolName.signal, $filterByNsKind.signal, $filterByActive.signal, inheritanceTabState.$activeSymbols.signal)
          .changes
          .debounce(300)
          .map { (diagram: InheritanceDiagram, w, nsKind, filterByActive, activeSymbols) =>
            diagram
              .orElse(w.isBlank, _.filterBySymbols(w))
              .subdiagramByKinds(nsKind)
              .orElse(!filterByActive, _.subdiagram(activeSymbols))
          }
      val $selectionEmpty = inheritanceTabState.$canvasSelection.signal.map(_.isEmpty)
      // --- container: two columns, two rows ---
      div(cls := "grid h-full grid-cols-[1fr_4fr] grid-rows-[3em_auto]",
        // --- packages tree ---

        div(cls := "overflow-auto p-1 row-start-1 row-end-3 border-r border-slate-300 flex flex-col",
          // --- controls ---
          form(cls := "p-1",
            LabeledCheckbox("show-options-toggle", "options",
              $showOptions.signal,
              Observer[Boolean](_ => $showOptions.update(!_)),
              toggle = true
            ),
            $showOptions.signal.childWhenTrue(
              div(
                cls := "bg-slate-200 p-1 mb-2",
                LabeledCheckbox(s"filter-by-active", "only active",
                  $filterByActive.signal,
                  Observer[Boolean](_ => $filterByActive.update(!_)),
                  toggle = true
                ),
                for kind <- models.NamespaceKind.values.toList yield
                  LabeledCheckbox(s"show-ns-kind-$kind", kind.toString,
                    $filterByNsKind.signal.map(_.contains(kind)),
                    $filterByNsKind.updater[Boolean]((set, b) => set.toggleWith(kind, b))
                  )
              ),
            ),
            Search(placeholder := "filter", controlled(value <-- $filterBySymbolName, onInput.mapToValue --> $filterBySymbolName)).small
          ),
          div(cls := "overflow-auto",
            children <-- packagesTree($filteredDiagram)
          )
        ),
        // --- toolbar ---
        div(cls := "flex gap-4 ml-2",
          ButtonGroup(
            OptionsToggle("fields-checkbox-1", "fields",     _.fields,     modifySelection(_.fields), inheritanceTabState),
            OptionsToggle("fields-checkbox-2", "signatures", _.signatures, modifySelection(_.signatures), inheritanceTabState),
          ),
          ButtonGroup(
            Button(disabled := false, "remove all", onClick --> (_ => inheritanceTabState.activeSymbols.clear())).outline.secondary.tiny,
            Button(disabled := true, "fit").outline.secondary.tiny,
            Button(disabled := true, "zoom").outline.secondary.tiny
          ),
          ButtonGroup(
            Button("children", disabled <-- $selectionEmpty, inheritanceTabState.addSelectionChildren(onClick)).outline.secondary.tiny,
            Button("parents",  disabled <-- $selectionEmpty, inheritanceTabState.addSelectionParents(onClick)).outline.secondary.tiny,
            Button("remove",   disabled <-- $selectionEmpty, inheritanceTabState.removeSelection(onClick)).outline.secondary.tiny,
          )
        ),
        // --- canvas ---
        div(cls := "h-full overflow-auto border-t border-slate-300 p-1 row-start-2 row-end-3 bg-orange-100",
          child <-- $inheritanceSvgDiagram.map { diagram =>
            val selection = inheritanceTabState.$canvasSelection.now()
            diagram.select(selection)
            // remove elements not present in the new diagram
            // (such elements did exist in the previous diagram)
            val missingSymbols = selection -- diagram.elementSymbols
            inheritanceTabState.$canvasSelection.update(_ -- missingSymbols)
            diagram.toLaminar
          },
          composeEvents(onClick.preventDefault)(_.withCurrentValueOf($inheritanceSvgDiagram)) -->
            handleSvgClick(inheritanceTabState).tupled,
        )
      )


  private def handleSvgClick
    (inheritanceTabState: InheritanceTabState)
    (e: TypedTargetMouseEvent[dom.Element], diagram: InheritanceSvgDiagram) =
    val selectedElement: Option[SvgGroupElement] =
      e.target.path
        .takeWhile(_.isInstanceOf[dom.SVGElement])
        .map(e => NamespaceElement.from(e) orElse ClusterElement.from(e))
        .collectFirst { case Some(g) => g }

    selectedElement match
      case Some(g) => g match

        case ns: NamespaceElement =>
          if e.metaKey then
            ns.select()
            inheritanceTabState.canvasSelection.extend(ns.symbol)
          else
            diagram.unselectAll()
            ns.select()
            inheritanceTabState.canvasSelection.replace(ns.symbol)

        case cluster: ClusterElement =>
          if !e.metaKey then
            diagram.unselectAll()
            inheritanceTabState.canvasSelection.clear()
          // select all boxes inside this cluster
          for ns <- diagram.clusterElements(cluster) do
            ns.select()
            inheritanceTabState.canvasSelection.extend(ns.symbol)

      case None =>
        diagram.unselectAll()
        inheritanceTabState.canvasSelection.clear()


  private def OptionsToggle(id: String, labelStr: String, field: Options => Boolean, modifyField: PathLazyModify[Options, Boolean], selectedSymbols: InheritanceTabState) =
    LabeledCheckbox(
      id = id,
      labelStr = labelStr,
      $checked = selectedSymbols.$options.signal.map(field),
      clickHandler = selectedSymbols.$options.updater[Boolean]((options, b) => modifyField.setTo(b)(options)),
      toggle = true
    )

end InheritanceTab

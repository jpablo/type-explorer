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
import org.scalajs.dom
import org.scalajs.dom.{EventTarget, console}


object InheritanceTab:

  private val autocomplete = customProp("autocomplete", StringAsIsCodec)

  def build =
    for
      $inheritanceSvgDiagram <- AppState.$inheritanceSvgDiagram
      $diagram               <- AppState.$inheritanceDiagram
      packagesTree        <- PackagesTree.build
      inheritanceTabState <- AppState.inheritanceTabState
    yield
      val $filter = Var("")
      val modifySelection = modifyLens[Options]
      val $filteredDiagram =
        $diagram
          .combineWith($filter.signal)
          .changes
          .debounce(300)
          .map((diagram, w) => if w.isBlank then diagram else diagram.filterBySymbols(w))
      val $selectionEmpty = inheritanceTabState.$canvasSelection.signal.map(_.isEmpty)
      // --- container: two columns, two rows ---
      div(cls := "grid h-full grid-cols-[1fr_4fr] grid-rows-[3em_auto]",
        // --- packages tree ---
        div(cls := "overflow-auto p-1 row-start-1 row-end-3 border-r border-slate-300 flex flex-col",
          // --- controls ---
          form(cls := "p-1",
            Search(placeholder := "filter", controlled(value <-- $filter, onInput.mapToValue --> $filter)).small
          ),
          div(cls := "overflow-auto",
            children <-- packagesTree($filteredDiagram)
          )
        ),
        // --- toolbar ---
        div(cls := "flex gap-4 ml-2",
          ButtonGroup(
            ControlledCheckbox("fields-checkbox-1", "fields",     _.fields,     modifySelection(_.fields), inheritanceTabState),
            ControlledCheckbox("fields-checkbox-2", "signatures", _.signatures, modifySelection(_.signatures), inheritanceTabState),
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
        .collectFirst { case Some(elem) => elem }

    selectedElement match
      case Some(elem) => elem match

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


  private def ControlledCheckbox(id: String, labelStr: String, field: Options => Boolean, modifyField: PathLazyModify[Options, Boolean], selectedSymbols: InheritanceTabState) =
    div(cls := "form-control",
      label(forId := id, cls := "label cursor-pointer",
        span(cls := "label-text pr-1", labelStr),
        Checkbox(idAttr := id, autocomplete := "off", cls := "toggle-xs",
          controlled(
            checked <-- selectedSymbols.$options.signal.map(field),
            onClick.mapToChecked --> selectedSymbols.$options.updater[Boolean]((options, b) => modifyField.setTo(b)(options))
          )
        ),
      ),
    )
end InheritanceTab

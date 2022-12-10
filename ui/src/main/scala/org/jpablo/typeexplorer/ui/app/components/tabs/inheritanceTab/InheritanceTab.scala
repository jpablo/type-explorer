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
import org.jpablo.typeexplorer.ui.app.console
import org.jpablo.typeexplorer.ui.bootstrap.*
import org.scalajs.dom
import org.scalajs.dom.EventTarget


object InheritanceTab:

  enum UserSelectionCommand:
    case Replace(symbol: models.Symbol)
    case Extend(symbol: models.Symbol)
    case Clear

  private val autocomplete = customProp("autocomplete", StringAsIsCodec)

  def build =
    for
      $inheritanceSvgDiagram <- AppState.$inheritanceSvgDiagram
      $diagram               <- AppState.$inheritanceDiagram
      $userSelectionCommand  <- AppState.$userSelectionCommand
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
      // -------------- render --------------------------------
      div( cls := "text-document-areas",

        form(cls := "inheritance-tree-search",
          Search(placeholder := "filter", controlled(value <-- $filter, onInput.mapToValue --> $filter)).small
        ),

        div(cls := "structure", children <-- packagesTree($filteredDiagram)),

        div(cls := "inheritance-container-toolbar",
          ButtonToolbar(
            cls := "mb-3",
            ButtonGroup(
              cls := "me-2",
              ControlledCheckbox("fields-checkbox-1", "fields",     _.fields,     modifySelection(_.fields), inheritanceTabState),
              ControlledCheckbox("fields-checkbox-2", "signatures", _.signatures, modifySelection(_.signatures), inheritanceTabState),
              Button(disabled := false, "clear", onClick --> (_ => inheritanceTabState.removeAll())).outlineSecondary,
              Button(disabled := true, "fit").outlineSecondary,
              Button(disabled := true, "zoom").outlineSecondary
            ).small,
            ButtonGroup(
              cls := "me-2",
              Button("children", disabled <-- $selectionEmpty, inheritanceTabState.addSelectionChildren(onClick)).outlineSecondary,
              Button("parents",  disabled <-- $selectionEmpty, inheritanceTabState.addSelectionParents(onClick)).outlineSecondary,
              Button("remove",   disabled <-- $selectionEmpty, inheritanceTabState.removeSelection(onClick)).outlineSecondary,
            ).small
          ),
        ),

        div( cls := "inheritance-container",
          div(
            child <-- $inheritanceSvgDiagram.map { diagram =>
              val selection = inheritanceTabState.$canvasSelection.now()
              diagram.select(selection)
              // remove elements not present in the new diagram
              // (such elements did exist in the previous diagram)
              val missingSymbols = selection -- diagram.elementSymbols
              inheritanceTabState.$canvasSelection.update(_ -- missingSymbols)
              diagram.toLaminar
            },
            composeEvents(onClick.preventDefault)(_.combineWith($inheritanceSvgDiagram)) --> handleSvgClick($userSelectionCommand).tupled,
          )
        )
      )


  private def handleSvgClick($command: EventBus[UserSelectionCommand])(e: TypedTargetMouseEvent[dom.Element], diagram: InheritanceSvgDiagram) =
    (e.target +: parents(e.target))
      .takeWhile(_.isInstanceOf[dom.SVGElement])
      .find(isNamespace)
      .map(NameSpaceElement(_)) match
        case Some(nsElement) =>
          if e.shiftKey then
            nsElement.selectToggle()
            $command.emit(UserSelectionCommand.Extend(nsElement.symbol))
          else
            diagram.unselectAll()
            nsElement.select()
            $command.emit(UserSelectionCommand.Replace(nsElement.symbol))
        case None =>
          diagram.unselectAll()
          $command.emit(UserSelectionCommand.Clear)

  private def ControlledCheckbox(id: String, labelStr: String, field: Options => Boolean, modifyField: PathLazyModify[Options, Boolean], selectedSymbols: InheritanceTabState) =
    List(
      Checkbox(idAttr := id, autocomplete := "off",
        controlled(
          checked <-- selectedSymbols.$options.signal.map(field),
          onClick.mapToChecked --> selectedSymbols.$options.updater[Boolean]((options, b) => modifyField.setTo(b)(options))
        )
      ),
      Label(forIdAttr = id, labelStr)
    )
end InheritanceTab



// TODO: move to a new file
extension (e: dom.Element)
  def parents =
    LazyList.unfold(e)(e => Option(e.parentNode.asInstanceOf[dom.Element]).map(e => (e, e)))

  def isDiagramElement(prefix: String) =
    e.tagName == "g" && e.hasAttribute("id") && e.getAttribute("id").startsWith(prefix)

  def isNamespace = e.isDiagramElement("elem_")
  def isPackage = e.isDiagramElement("cluster_")

  def fill = e.getAttribute("fill")
  def fill_=(c: String) = e.setAttribute("fill", c)

package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.inheritance.{DiagramOptions, InheritanceGraph, Path, SymbolOptions}
import org.jpablo.typeexplorer.shared.models.GraphSymbol
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.extensions.*
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import InheritanceTabState.ActiveSymbols
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.laminar.modifiers.Binder.Base
import org.jpablo.typeexplorer.ui.app.client.fetchInheritanceSVGDiagram

object InheritanceTabState:
  type ActiveSymbols = Map[GraphSymbol, Option[SymbolOptions]]

case class InheritanceTabState(appState: AppState, pageId: String):
  // TODO: verify that subscriptions are killed when the tab is closed
  given owner: Owner = OneTimeOwner(() => ())
  // this should be a subset of activeSymbols' keys
  private val canvasSelectionV = Var(Set.empty[GraphSymbol])
  private val pageV = appState.activeProject.pageV(pageId)

  val activeSymbolsV: Var[Map[GraphSymbol, Option[SymbolOptions]]] =
    pageV.zoom(_.activeSymbols.toMap)((p, s) => p.copy(activeSymbols = s.toList))

  val diagramOptionsV: Var[DiagramOptions] =
    pageV.zoom(_.diagramOptions)((p, s) => p.copy(diagramOptions = s))

  val canvasSelection =
    CanvasSelectionOps(canvasSelectionV)

  val activeSymbols =
    ActiveSymbolsOps(activeSymbolsV, appState.fullGraph, canvasSelectionV)

  val inheritanceSvgDiagram: Signal[InheritanceSvgDiagram] =
    appState.basePaths
      .combineWith(pageV.signal.distinct)
      .flatMap(fetchInheritanceSVGDiagram)
      .startWith(InheritanceSvgDiagram.empty)

end InheritanceTabState

class CanvasSelectionOps(
    canvasSelectionV: Var[Set[GraphSymbol]] = Var(Set.empty)
):
  export canvasSelectionV.now
  val signal = canvasSelectionV.signal

  def toggle(symbol: GraphSymbol): Unit =
    canvasSelectionV.update(_.toggle(symbol))

  def replace(symbol: GraphSymbol): Unit =
    canvasSelectionV.set(Set(symbol))

  def extend(symbol: GraphSymbol): Unit =
    canvasSelectionV.update(_ + symbol)

  def extend(symbols: Set[GraphSymbol]): Unit =
    canvasSelectionV.update(_ ++ symbols)

  def remove(symbols: Set[GraphSymbol]): Unit =
    canvasSelectionV.update(_ -- symbols)

  def clear(): Unit =
    canvasSelectionV.set(Set.empty)

  def selectParents(
      fullDiagram:           InheritanceGraph,
      inheritanceSvgDiagram: InheritanceSvgDiagram,
      activeSymbols:         ActiveSymbols
  ): Unit =
    selectRelated(
      _.parentsOfAll(_),
      fullDiagram,
      inheritanceSvgDiagram,
      activeSymbols
    )

  def selectChildren(
      fullDiagram:           InheritanceGraph,
      inheritanceSvgDiagram: InheritanceSvgDiagram,
      activeSymbols:         ActiveSymbols
  ): Unit =
    selectRelated(
      _.childrenOfAll(_),
      fullDiagram,
      inheritanceSvgDiagram,
      activeSymbols
    )

  private def selectRelated(
      selector:              (InheritanceGraph, Set[GraphSymbol]) => InheritanceGraph,
      fullDiagram:           InheritanceGraph,
      inheritanceSvgDiagram: InheritanceSvgDiagram,
      activeSymbols:         ActiveSymbols
  ): Unit =
    val svgDiagram = fullDiagram.subdiagram(activeSymbols.keySet)
    val selection = canvasSelectionV.now()
    val relatedDiagram = selector(svgDiagram, selection)
    val arrowSymbols =
      relatedDiagram.arrows.map((a, b) => GraphSymbol(s"${b}_$a"))
    extend(relatedDiagram.symbols)
    extend(arrowSymbols)
    inheritanceSvgDiagram.select(relatedDiagram.symbols)
    inheritanceSvgDiagram.select(arrowSymbols)

end CanvasSelectionOps

class ActiveSymbolsOps(
    val activeSymbolsR:       Var[ActiveSymbols],
    val fullInheritanceGraph: Signal[InheritanceGraph],
    val canvasSelectionR:     Var[Set[GraphSymbol]]
):

  val signal = activeSymbolsR.signal

  def toggle(symbol: GraphSymbol): Unit =
    activeSymbolsR.update: activeSymbols =>
      if activeSymbols.contains(symbol) then activeSymbols - symbol
      else activeSymbols + (symbol -> None)

  def extend(symbol: GraphSymbol): Unit =
    activeSymbolsR.update(_ + (symbol -> None))

  def extend(symbols: collection.Seq[GraphSymbol]): Unit =
    activeSymbolsR.update(_ ++ symbols.map(_ -> None))

  def clear(): Unit =
    activeSymbolsR.set(Map.empty)

  /** Updates (selected) active symbol's options based on the given function `f`
    */
  def updateSelectionOptions(f: SymbolOptions => SymbolOptions): Unit =
    val canvasSelection = canvasSelectionR.now()
    activeSymbolsR.update:
      _.transform: (sym, options) =>
        if canvasSelection.contains(sym) then Some(f(options.getOrElse(SymbolOptions())))
        else options

  /** Modify `activeSymbols` based on the given function `f`
    */
  def applyOnSelection[E <: dom.Event](
      f: (ActiveSymbols, Set[GraphSymbol]) => ActiveSymbols
  )(ep: EventProp[E]) =
    ep.compose(_.sample(canvasSelectionR)) --> { selection =>
      activeSymbolsR.update(f(_, selection))
    }

  /** Adds all children of the canvas selection to activeSymbols
    */
  def addSelectionChildren[E <: dom.Event](ep: EventProp[E]) =
    addSelectionWith(_.childrenOf(_), ep)

  /** Adds all parents of the canvas selection to activeSymbols
    */
  def addSelectionParents[E <: dom.Event](ep: EventProp[E]) =
    addSelectionWith(_.parentsOf(_), ep)

  /** Updates activeSymbols with the given function `f` and the current canvas selection.
    */
  private def addSelectionWith[E <: dom.Event](
      f:  (InheritanceGraph, GraphSymbol) => InheritanceGraph,
      ep: EventProp[E]
  ): Base =
    val combined = fullInheritanceGraph.combineWith(canvasSelectionR.signal)
    ep.compose(_.sample(combined)) --> { (diagram, selection) =>
      if selection.nonEmpty then
        val diagram1 = selection.foldLeft(InheritanceGraph.empty)((acc, s) => f(diagram, s) ++ acc)
        extend(diagram1.symbols.toSeq)
    }
end ActiveSymbolsOps

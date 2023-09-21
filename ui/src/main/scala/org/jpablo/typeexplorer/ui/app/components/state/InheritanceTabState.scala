package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.inheritance.SymbolOptions
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom
import org.jpablo.typeexplorer.ui.app.toggle
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram

object InheritanceTabState:
  type ActiveSymbols = Map[models.Symbol, Option[SymbolOptions]]

import InheritanceTabState.ActiveSymbols


case class InheritanceTabState(
    activeSymbolsR: Var[ActiveSymbols] = Var(Map.empty),
    fullInheritanceDiagram: Signal[InheritanceDiagram] =
      Signal.fromValue(InheritanceDiagram.empty),
    // this should be a subset of activeSymbols' keys
    canvasSelectionR: Var[Set[models.Symbol]] = Var(Set.empty)
):
  val canvasSelection = CanvasSelection(activeSymbolsR, canvasSelectionR)
  val activeSymbols = ActiveSymbolsVar(activeSymbolsR, canvasSelectionR)

  /** Modify `activeSymbols` based on the given function `f`
    */
  def applyOnSelection[E <: dom.Event](
      f: (ActiveSymbols, Set[models.Symbol]) => ActiveSymbols
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

  /** Updates activeSymbols with the given function `f` and the current canvas
    * selection.
    */
  private def addSelectionWith[E <: dom.Event](
      f: (InheritanceDiagram, models.Symbol) => InheritanceDiagram,
      ep: EventProp[E]
  ) =
    val combined = fullInheritanceDiagram.combineWith(canvasSelectionR.signal)
    ep.compose(_.sample(combined)) --> { (diagram, selection) =>
      if selection.nonEmpty then
        val diagram1 = selection.foldLeft(InheritanceDiagram.empty)((acc, s) =>
          f(diagram, s) ++ acc
        )
        activeSymbols.extend(diagram1.symbols.toSeq)
    }

end InheritanceTabState

class CanvasSelection(
    activeSymbolsVar: Var[ActiveSymbols],
    symbolsVar: Var[Set[models.Symbol]] = Var(Set.empty)
):
  def toggle(symbol: models.Symbol): Unit =
    symbolsVar.update(_.toggle(symbol))

  def replace(symbol: models.Symbol): Unit =
    symbolsVar.set(Set(symbol))

  def extend(symbol: models.Symbol): Unit =
    symbolsVar.update(_ + symbol)

  def extend(symbols: Set[models.Symbol]): Unit =
    symbolsVar.update(_ ++ symbols)

  def clear(): Unit =
    symbolsVar.set(Set.empty)

  def selectParents(
      fullDiagram: InheritanceDiagram,
      inheritanceSvgDiagram: InheritanceSvgDiagram
  ): Unit =
    selectRelated(_.parentsOfAll(_), fullDiagram, inheritanceSvgDiagram)

  def selectChildren(
      fullDiagram: InheritanceDiagram,
      inheritanceSvgDiagram: InheritanceSvgDiagram
  ): Unit =
    selectRelated(_.childrenOfAll(_), fullDiagram, inheritanceSvgDiagram)

  private def selectRelated(
      selector: (
          InheritanceDiagram,
          Set[models.Symbol]
      ) => InheritanceDiagram,
      fullDiagram: InheritanceDiagram,
      inheritanceSvgDiagram: InheritanceSvgDiagram
  ): Unit =
    val svgDiagram = fullDiagram.subdiagram(activeSymbolsVar.now().keySet)
    val selection = symbolsVar.now()
    val relatedDiagram = selector(svgDiagram, selection)
    val arrowSymbols =
      relatedDiagram.arrows.map((a, b) => models.Symbol(s"${b}_$a"))
    extend(relatedDiagram.symbols)
    extend(arrowSymbols)
    inheritanceSvgDiagram.select(relatedDiagram.symbols)
    inheritanceSvgDiagram.select(arrowSymbols)

end CanvasSelection


class ActiveSymbolsVar(
  activeSymbolsR: Var[ActiveSymbols],
  canvasSelectionR: Var[Set[models.Symbol]]
):
  def toggle(symbol: models.Symbol): Unit =
    activeSymbolsR.update: activeSymbols =>
      if activeSymbols.contains(symbol) then activeSymbols - symbol
      else activeSymbols + (symbol -> None)

  def extend(symbol: models.Symbol): Unit =
    activeSymbolsR.update(_ + (symbol -> None))

  def extend(symbols: collection.Seq[models.Symbol]): Unit =
    activeSymbolsR.update(_ ++ symbols.map(_ -> None))

  def clear(): Unit =
    activeSymbolsR.set(Map.empty)

  /** Updates (selected) active symbol's options based on the given function `f`
   */
  def updateSelectionOptions(f: SymbolOptions => SymbolOptions): Unit =
    val canvasSelection = canvasSelectionR.now()
    activeSymbolsR.update:
      _.transform { case (sym, options) =>
        if canvasSelection.contains(sym) then
          Some(f(options.getOrElse(SymbolOptions())))
        else options
      }
end ActiveSymbolsVar

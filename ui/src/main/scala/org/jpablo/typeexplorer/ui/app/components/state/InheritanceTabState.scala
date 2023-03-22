package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.{DiagramOptions, SymbolOptions}
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path
import org.scalajs.dom
import zio.json.*
import org.jpablo.typeexplorer.ui.app.toggle
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq

object InheritanceTabState:
  type ActiveSymbols = Map[models.Symbol, Option[SymbolOptions]]

import InheritanceTabState.ActiveSymbols

class InheritanceTabState(
  val $activeSymbols: Var[ActiveSymbols] = Var(Map.empty),
  val $inheritanceDiagram: Signal[InheritanceDiagram] = Signal.fromValue(InheritanceDiagram.empty),
  // this should be a subset of $activeSymbols' keys
  val $canvasSelection   : Var[Set[models.Symbol]] = Var(Set.empty),
):
  object canvasSelection:
    def toggle(symbol: models.Symbol): Unit =
      $canvasSelection.update(_.toggle(symbol))

    def replace(symbol: models.Symbol): Unit =
      $canvasSelection.set(Set(symbol))

    def extend(symbol: models.Symbol): Unit =
      $canvasSelection.update(_ + symbol)

    def extend(symbols: Set[models.Symbol]): Unit =
      $canvasSelection.update(_ ++ symbols)

    def clear(): Unit =
      $canvasSelection.set(Set.empty)

    def selectParents(fullDiagram: InheritanceDiagram, inheritanceSvgDiagram: InheritanceSvgDiagram): Unit =
      selectRelated(_.parentsOfAll(_), fullDiagram, inheritanceSvgDiagram)

    def selectChildren(fullDiagram: InheritanceDiagram, inheritanceSvgDiagram: InheritanceSvgDiagram): Unit =
      selectRelated(_.childrenOfAll(_), fullDiagram, inheritanceSvgDiagram)

    private def selectRelated(selector: (InheritanceDiagram, Set[models.Symbol]) => InheritanceDiagram, fullDiagram: InheritanceDiagram, inheritanceSvgDiagram: InheritanceSvgDiagram): Unit =
      val svgDiagram = fullDiagram.subdiagram($activeSymbols.now().keySet)
      val selection  = $canvasSelection.now()
      val relatedDiagram = selector(svgDiagram, selection)
      val arrowSymbols = relatedDiagram.arrows.map((a, b) => models.Symbol(s"${b}_$a"))
      extend(relatedDiagram.symbols)
      extend(arrowSymbols)
      inheritanceSvgDiagram.select(relatedDiagram.symbols)
      inheritanceSvgDiagram.select(arrowSymbols)

  end canvasSelection

  object activeSymbols:
    def toggle(symbol: models.Symbol): Unit =
      $activeSymbols.update: activeSymbols =>
        if activeSymbols.contains(symbol) then
          activeSymbols - symbol
        else
          activeSymbols + (symbol -> None)

    def extend(symbol: models.Symbol): Unit =
      $activeSymbols.update(_ + (symbol -> None))

    def extend(symbols: collection.Seq[models.Symbol]): Unit =
      $activeSymbols.update(_ ++ symbols.map(_ -> None))

    def clear(): Unit =
      $activeSymbols.set(Map.empty)

    /** Updates (selected) active symbol's options based on the given function `f`
      */
    def updateSelectionOptions(f: SymbolOptions => SymbolOptions): Unit =
      val canvasSelection = $canvasSelection.now()
      $activeSymbols.update:
        _.transform { case (sym, options) =>
          if canvasSelection.contains(sym) then
            Some(f(options.getOrElse(SymbolOptions())))
          else
            options
        }

  /**
    * Modify `$activeSymbols` based on the given function `f`
    */
  def applyOnSelection[E <: dom.Event](f: (ActiveSymbols, Set[models.Symbol]) => ActiveSymbols)(ep: EventProp[E]) =
    ep.compose(_.sample($canvasSelection)) --> { selection =>
      $activeSymbols.update(f(_, selection))
    }

  /**
    * Adds all children of the canvas selection to $activeSymbols
    */
  def addSelectionChildren[E <: dom.Event](ep: EventProp[E]) =
    addSelectionWith(_.childrenOf(_), ep)

  /**
    * Adds all parents of the canvas selection to $activeSymbols
    */
  def addSelectionParents[E <: dom.Event](ep: EventProp[E]) =
    addSelectionWith(_.parentsOf(_), ep)

  /**
    * Updates $activeSymbols with the given function `f` and the current canvas selection.
    */
  private def addSelectionWith[E <: dom.Event](f: (InheritanceDiagram, models.Symbol) => InheritanceDiagram, ep: EventProp[E]) =
    val combined = $inheritanceDiagram.combineWith($canvasSelection.signal)
    ep.compose(_.sample(combined)) --> { (diagram, selection) =>
      if selection.nonEmpty then
        val diagram1 = selection.foldLeft(InheritanceDiagram.empty)((acc, s) => f(diagram, s) ++ acc)
        activeSymbols.extend(diagram1.symbols.toSeq)
    }


end InheritanceTabState


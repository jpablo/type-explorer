package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.models
import org.scalajs.dom


case class InheritanceTabState(
  /**
    * Derived from $activeSymbols
    */
  $inheritanceDiagram: Signal[InheritanceDiagram],
  /**
    * primary selection: based on direct user interactions
    */
  $activeSymbols  : Var[Set[models.Symbol]] = Var(Set.empty),
  $options        : Var[Options] = Var(Options()),
  $canvasSelection: Var[Set[models.Symbol]] = Var(Set.empty),
):

  def addSymbol(symbol: models.Symbol): Unit =
    $activeSymbols.update(_ + symbol)

  def removeAll() =
    $activeSymbols.set(Set.empty)

  /**
    * Removes all user-selected symbols (in the canvas) from $activeSymbols
    */
  def removeSelection[E <: dom.Event](ep: EventProp[E]) =
    composeEvents(ep)(_.sample($canvasSelection)) --> { selection =>
      $activeSymbols.update(_ -- selection)
    }

  /**
    * Adds all children of the canvas selection to $activeSymbols
    */
  def addSelectionChildren[E <: dom.Event](ep: EventProp[E]) =
    addSelectionWith(_.allChildren(_), ep)

  /**
    * Adds all parents of the canvas selection to $activeSymbols
    */
  def addSelectionParents[E <: dom.Event](ep: EventProp[E]) =
    addSelectionWith(_.allParents(_), ep)

  /**
    * Updates $activeSymbols with the given function `f` and the current canvas selection.
    */
  private def addSelectionWith[E <: dom.Event](f: (InheritanceDiagram, models.Symbol) => InheritanceDiagram, ep: EventProp[E]) =
    val combined = $inheritanceDiagram.combineWith($canvasSelection.signal)
    composeEvents(ep)(_.sample(combined)) --> { (diagram, selection) =>
      if selection.nonEmpty then
        val newSymbols = selection.foldLeft(diagram)(f).symbols
        $activeSymbols.update(_ ++ newSymbols)
    }


end InheritanceTabState

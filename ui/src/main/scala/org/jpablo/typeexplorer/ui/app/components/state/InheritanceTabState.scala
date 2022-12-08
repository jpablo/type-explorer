package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.state.Var
import com.raquo.airstream.core.Signal
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import com.softwaremill.quicklens.*
import com.raquo.airstream.core.Observer
import org.jpablo.typeexplorer.shared.models
import com.raquo.airstream.core.EventStream
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.models.Namespace
import org.jpablo.typeexplorer.shared.models


case class InheritanceTabState(
  $inheritanceDiagram: Signal[InheritanceDiagram],
  /**
    * primary selection: based on direct user interactions
    */
  $activeSymbols  : Var[Set[models.Symbol]] = Var(Set.empty),
  /**
    * explicitly ignored symbols
    */
  $ignored        : Var[Set[models.Symbol]] = Var(Set.empty),
  $options        : Var[Options] = Var(Options()),
  $canvasSelection: Var[Set[models.Symbol]] = Var(Set.empty),
):

  def addSymbol(symbol: models.Symbol): Unit =
    $activeSymbols.update(_ + symbol)

  def addSelectedChildren =
    addSelectedWith(_.allChildren(_))

  def addSelectedParents =
    addSelectedWith(_.allParents(_))

  private def addSelectedWith(f: (InheritanceDiagram, models.Symbol) => InheritanceDiagram)(diagram: InheritanceDiagram, selection: Set[models.Symbol]): Unit =
    $activeSymbols.set(
      selection.foldLeft(diagram)(f).symbols
    )

  def selection[A](events: EventStream[A]): EventStream[(InheritanceDiagram, Set[models.Symbol])] =
    events.sample($inheritanceDiagram.combineWith($canvasSelection.signal))


end InheritanceTabState


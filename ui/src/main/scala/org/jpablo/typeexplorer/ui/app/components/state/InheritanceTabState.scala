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
import org.jpablo.typeexplorer.shared.models.Namespace
import org.jpablo.typeexplorer.shared.models
import com.raquo.laminar.api.L.*
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


  def selectChildren[E <: dom.Event](ep: EventProp[E]) =
    selectWith(_.allChildren(_), ep)

  def selectParents[E <: dom.Event](ep: EventProp[E]) =
    selectWith(_.allParents(_), ep)

  private def selectWith[E <: dom.Event](f: (InheritanceDiagram, models.Symbol) => InheritanceDiagram, ep: EventProp[E]) =
    val combined = $inheritanceDiagram.combineWith($canvasSelection.signal)
    composeEvents(ep)(_.sample(combined)) --> {(diagram, selection) => $activeSymbols.set(selection.foldLeft(diagram)(f).symbols)}


end InheritanceTabState


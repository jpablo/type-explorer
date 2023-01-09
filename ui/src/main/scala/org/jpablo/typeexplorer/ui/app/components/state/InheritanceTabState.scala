package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Observer
import com.raquo.airstream.core.Signal
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path
import org.scalajs.dom
import zio.json.*
import org.jpablo.typeexplorer.ui.app.toggle
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram

case class InheritanceTabState(
  activeSymbolsJson  : StoredString,
  $inheritanceDiagram: Signal[InheritanceDiagram] = Signal.fromValue(InheritanceDiagram.empty),
  $activeSymbols   : Var[Set[models.Symbol]] = Var(Set.empty),
  $options         : Var[Options] = Var(Options()),
  $canvasSelection : Var[Set[models.Symbol]] = Var(Set.empty),
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
      val svgDiagram    = fullDiagram.subdiagram($activeSymbols.now())
      val selection     = $canvasSelection.now()
      val newParents    = selector(svgDiagram, selection).symbols
      extend(newParents)
      inheritanceSvgDiagram.select(newParents)



  object activeSymbols:
    def toggle(symbol: models.Symbol): Unit =
      $activeSymbols.update(_.toggle(symbol))

    def extend(symbol: models.Symbol): Unit =
      $activeSymbols.update(_ + symbol)

    def extend(symbols: collection.Seq[models.Symbol]): Unit =
      $activeSymbols.update(_ ++ symbols)

    def clear() =
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
    composeEvents(ep)(_.sample(combined)) --> { (diagram, selection) =>
      if selection.nonEmpty then
        val diagram1 = selection.foldLeft(InheritanceDiagram.empty)((acc, s) => f(diagram, s) ++ acc)
        $activeSymbols.update(_ ++ diagram1.symbols)
    }


end InheritanceTabState


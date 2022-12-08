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
  $inheritanceDiagram: Signal[InheritanceDiagram] = Signal.fromValue(InheritanceDiagram.empty),
  /**
    * primary selection: based on direct user interactions
    */
  $activeSymbols  : Var[Set[models.Symbol]] = Var(Set.empty),
  /**
    * explicitly ignored symbols
    */
  $ignored        : Var[Set[models.Symbol]] = Var(Set.empty),
  $options        : Var[Options] = Var(Options()),
  $canvasSelection: Var[Set[models.Symbol]] = Var(Set.empty)
):

//  def enableParents(diagram: InheritanceDiagram)(symbol: models.Symbol): Unit =
//    val parents: Set[models.Symbol] = diagram.allParents(symbol).namespaces.map(_.symbol)
//    $activeSymbols.update { activeSymbols =>
//      parents.foldLeft(activeSymbols) { (activeSymbols, sym) =>
//        val selection0 = activeSymbols.getOrElse(symbol, Selection.empty)
//        val selection1 = if !selection0.current then selection0.copy(current = true) else selection0
//        activeSymbols.updated(sym, selection1)
//      }
//    }

//  private def related(symbols: Set[models.Symbol, Selection]): Set[(models.Symbol, Set[Related])] =
//    symbols.transform { (symbol, selection) => selection match
//      case Selection(true, false, false) => Set.empty
//      case Selection(_   , true , false) => Set(Related.Parents)
//      case Selection(_   , false, true ) => Set(Related.Children)
//      case Selection(_   , true , true ) => Set(Related.Parents, Related.Children)
//      case _                             => throw Exception(s"Defect: symbol ${symbol} without selection found")
//    }.toSet

  /**
    * symbols indirectly selected: parents, children, etc.
    */
//  def selection(symbol: models.Symbol): Signal[Selection] =
//    $activeSymbols.signal.map(_.getOrElse(symbol, Selection.empty))

//  val $activeSymbolsChanges: EventStream[Set[(models.Symbol, Set[Related])]] =
//    $activeSymbols.signal.changes.map()

  def addSymbol(symbol: models.Symbol): Unit =
    $activeSymbols.update(_ + symbol)

  def showChildren(): Signal[Unit] =
    $inheritanceDiagram.combineWith($canvasSelection.signal).map { (diagram, selection) =>
      val symbols = selection.foldLeft(diagram)((d, s) => d.allChildren(s)).symbols
      $activeSymbols.set(symbols)
    }

  def showParents(): Unit = ()
//    showSelection(modifyLens[Selection](_.parents))


  private def symbolsUpdater(symbol: models.Symbol, modifyField: PathLazyModify[Selection, Boolean])(symbols: Map[models.Symbol, Selection], b: Boolean) =
    val selection0 = symbols.getOrElse(symbol, Selection.empty)
    val selection1 = modifyField.setTo(b)(selection0)
    if selection1.allEmpty then
      symbols - symbol
    else
      symbols + (symbol -> selection1)

//  private def showSelection(modifyField: PathLazyModify[Selection, Boolean]): Unit =
//    $activeSymbols.update { activeSymbols =>
//      $canvasSelection.now().foldLeft(activeSymbols) { (activeSymbols, symbol) =>
//        symbolsUpdater(symbol, modifyField)(activeSymbols, true)
//      }
//    }


end InheritanceTabState


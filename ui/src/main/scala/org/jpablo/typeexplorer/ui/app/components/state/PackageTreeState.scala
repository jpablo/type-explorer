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


case class PackageTreeState(
  /**
    * primary selection: based on direct user interactions
    */
  symbols: Var[Map[models.Symbol, Selection]] = Var(Map.empty),
  /**
    * explicitly ignored symbols
    */
  ignored: Var[Set[models.Symbol]] = Var(Set.empty),
  options: Var[Options] = Var(Options())
):

  def enableParents(diagram: InheritanceDiagram)(symbol: models.Symbol): Unit =
    val parents: Set[models.Symbol] = diagram.allParents(symbol).namespaces.map(_.symbol)
    symbols.update { (symbols: Map[models.Symbol, Selection]) =>
      parents.foldLeft(symbols) { (acc, sym) => 
        val selection0 = symbols.getOrElse(symbol, Selection.empty)
        val selection1 = if !selection0.current then selection0.copy(current = true) else selection0
        symbols.updated(sym, selection1)
      }
    }

  private def related(symbols: Map[models.Symbol, Selection]): Set[(models.Symbol, Set[Related])] =
    symbols.transform { (symbol, selection) => selection match
      case Selection(true, false, false) => Set.empty
      case Selection(_   , true , false) => Set(Related.Parents)
      case Selection(_   , false, true ) => Set(Related.Children)
      case Selection(_   , true , true ) => Set(Related.Parents, Related.Children)
      case _                             => throw Exception(s"Defect: symbol ${symbol} without selection found")
    }.toSet

  /**
    * symbols indirectly selected: parents, children, etc.
    */
  def $secondary(diagram: InheritanceDiagram): Signal[Set[models.Symbol]] = 
    symbols.signal.map(secondary(diagram)).map(_.namespaces.map(_.symbol))

  private def secondary(diagram: InheritanceDiagram)(symbols: Map[models.Symbol, Selection]): InheritanceDiagram = 
    diagram.filterSymbols(related(symbols))

  def symbolsUpdater(symbol: models.Symbol)(modifyField: PathLazyModify[Selection, Boolean]): Observer[Boolean] =
    symbols.updater[Boolean] { (symbols, b) =>
      val selection0 = symbols.getOrElse(symbol, Selection.empty)
      val selection1 = modifyField.setTo(b)(selection0)
      if selection1.allEmpty then
        symbols - symbol
      else
        symbols + (symbol -> selection1)
    }      

  def selection(symbol: models.Symbol): Signal[Selection] = 
    symbols.signal.map(_.getOrElse(symbol, Selection.empty))

  val $requestBody: EventStream[Set[(models.Symbol, Set[Related])]] =
    symbols.signal.changes.map(related)

  


end PackageTreeState


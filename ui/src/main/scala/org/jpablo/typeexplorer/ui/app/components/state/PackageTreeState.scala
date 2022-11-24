package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.state.Var
import com.raquo.airstream.core.Signal
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import com.softwaremill.quicklens.*


case class PackageTreeState(
  /**
    * primary selection: based on direct user interactions
    */
  symbols: Var[Map[models.Symbol, Selection]] = Var(Map.empty),
  /**
    * symbols indirectly selected: parents, children, etc.
    */
  secondary: Var[Set[models.Symbol]] = Var(Set.empty),
  /**
    * explicitly ignored symbols
    */
  ignored: Var[Set[models.Symbol]] = Var(Set.empty),
  options: Var[Options] = Var(Options())
):

  def enableParents(diagram: InheritanceDiagram)(symbol: models.Symbol): Unit =
    val parents: Set[models.Symbol] = diagram.allParents(symbol).namespaces.map(_.symbol)
    println(parents)
    symbols.update { (symbols: Map[models.Symbol, Selection]) =>
      parents.foldLeft(symbols) { (acc, sym) => 
        val selection0 = symbols.getOrElse(symbol, Selection.empty)
        val selection1 = if !selection0.current then selection0.copy(current = true) else selection0
        symbols.updated(sym, selection1)
      }
    }

  def symbolsUpdater(ns: models.Namespace, modifyField: PathLazyModify[Selection, Boolean]) =
    symbols.updater[Boolean] { (symbols, b) =>
      val selection0 = symbols.getOrElse(ns.symbol, Selection.empty)
      val selection1 = modifyField.setTo(b)(selection0)
      if selection1.allEmpty then
        symbols - ns.symbol
      else
        symbols + (ns.symbol -> selection1)
    }      

  def selection(symbol: models.Symbol): Signal[Selection] = 
    symbols.signal.map(_.getOrElse(symbol, Selection.empty))    

end PackageTreeState


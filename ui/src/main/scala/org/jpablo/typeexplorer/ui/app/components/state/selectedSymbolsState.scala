package org.jpablo.typeexplorer.ui.app.components.state

import org.scalajs.dom
import com.raquo.laminar.api.L.*

import org.jpablo.typeexplorer.shared.inheritance.Related
import org.jpablo.typeexplorer.ui.app.toggle
import org.jpablo.typeexplorer.ui.app.*
import org.jpablo.typeexplorer.ui.app.client.fetchInheritanceSVGDiagram
import org.jpablo.typeexplorer.shared.models.Symbol
import com.raquo.airstream.core.EventStream
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options


case class SelectedSymbols(
  symbols: Var[Map[Symbol, Selection]] = Var(Map.empty),
  options: Var[Options] = Var(Options())
):
  lazy val signal = symbols.signal
  def updater[A]  = symbols.updater[A]

case class Selection(
  current : Boolean = false,
  parents : Boolean = false,
  children: Boolean = false,
):
  def allEmpty = !current && !parents && !children

object Selection:
  def empty = Selection()

case class State2(
  symbols: Map[Symbol, Selection] = Map.empty
)

def selectedSymbolToDiagram(
  $selected    : Signal[Map[Symbol, Selection]], 
  $options     : Signal[Options],
  $projectPath : Signal[Path]
): EventStream[dom.Element] =
  val $requestBody =
    $selected.changes.map { symbols =>
      symbols.transform { (symbol, selection) => selection match
        case Selection(true, false, false) => Set.empty
        case Selection(_   , true , false) => Set(Related.Parents)
        case Selection(_   , false, true ) => Set(Related.Children)
        case Selection(_   , true , true ) => Set(Related.Parents, Related.Children)
        case _                             => throw Exception(s"Defect: symbol ${symbol} without selection found")
      }.toSet
    }
  
  $projectPath
    .combineWith($requestBody.toSignal(Set.empty), $options)
    .changes
    .flatMap(fetchInheritanceSVGDiagram)

  


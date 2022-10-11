package org.jpablo.typeexplorer.ui.app.components.state

import org.scalajs.dom
import com.raquo.laminar.api.L.*

import org.jpablo.typeexplorer.shared.inheritance.Related
import org.jpablo.typeexplorer.ui.app.toggle
import org.jpablo.typeexplorer.ui.app.*
import org.jpablo.typeexplorer.ui.app.client.fetchInheritanceSVGDiagram
import org.jpablo.typeexplorer.shared.models.Symbol

enum Selected { case current, parents, children }

case class SelectedSymbol(
  current : EventBus[Symbol] = EventBus(),
  parents : EventBus[Symbol] = EventBus(),
  children: EventBus[Symbol] = EventBus(),
):
  lazy val merged = 
    EventStream.merge(
      current.events.map (_ -> Selected.current),
      parents.events.map (_ -> Selected.parents),
      children.events.map(_ -> Selected.children)
    )


case class State(
  symbols: Map[Symbol, Set[Related]] = Map.empty.withDefaultValue(Set.empty)
):
  def update(s: Symbol, r: Related): State =
    State(symbols + (s -> symbols(s).toggle(r)))
  
  def toggle(s: Symbol): State =
    State(if symbols contains s then symbols - s else symbols + (s -> Set.empty))
    

def selectedSymbolToDiagram(selected: SelectedSymbol, $projectPath: Signal[Path]): EventStream[dom.Element] =
  val state =
    selected.merged
      .foldLeft(State()) { case (state @ State(symbols), (s, tpe)) => 
        tpe match
          case Selected.current  => state.toggle(s)
          case Selected.parents  => state.update(s, Related.Parents)
          case Selected.children => state.update(s, Related.Children)
      }    

  $projectPath
    .combineWith(state.map(_.symbols.toSet))
    .changes
    .flatMap(fetchInheritanceSVGDiagram)
  


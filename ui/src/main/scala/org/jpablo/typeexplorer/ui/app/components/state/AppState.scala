package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.scalajs.dom
import zio.prelude.fx.ZPure
import zio.Tag

import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.Path

case class AppState(
  selectedSymbols: SelectedSymbols = SelectedSymbols(),
  projectPath: StoredString = storedString("projectPath", initial = ""),
  $diagramSelection: Var[Set[Symbol]] = Var(Set.empty)
):
  val $projectPath: Signal[Path] = 
    projectPath.signal.map(Path.apply)

  /**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  def $inheritanceSelection: EventStream[(Path, Set[(Symbol, Set[Related])], Options)] =
    val $requestBody =
      selectedSymbols.symbols.signal.changes.map { symbols =>
        symbols.transform { (symbol, selection) => selection match
          case Selection(true, false, false) => Set.empty
          case Selection(_   , true , false) => Set(Related.Parents)
          case Selection(_   , false, true ) => Set(Related.Children)
          case Selection(_   , true , true ) => Set(Related.Parents, Related.Children)
          case _                             => throw Exception(s"Defect: symbol ${symbol} without selection found")
        }.toSet
      }
    $projectPath
      .combineWith($requestBody.toSignal(Set.empty), selectedSymbols.options.signal)
      .changes


type Service[A] = 
  ZPure[Nothing, Unit, Unit, A, Nothing, A]

def service[A: Tag]: Service[A] =
  ZPure.service[Unit, A]

object AppState:
  val $diagram           = service[EventStream[InheritanceDiagram]]
  val $documents         = service[EventStream[List[TextDocumentsWithSource]]]
  val $projectPath       = service[Signal[Path]]

  val $selectedNamespace = service[EventBus[Symbol]]
  val $diagramSelection  = service[Var[Set[Symbol]]]

  val $svgDiagram        = service[EventStream[dom.SVGElement]]
  val projectPath        = service[StoredString]
  val selectedSymbols    = service[SelectedSymbols]


case class SelectedSymbols(
  symbols: Var[Map[Symbol, Selection]] = Var(Map.empty),
  options: Var[Options] = Var(Options())
)

case class Selection(
  current : Boolean = false,
  parents : Boolean = false,
  children: Boolean = false,
):
  def allEmpty = !current && !parents && !children

object Selection:
  def empty = Selection()



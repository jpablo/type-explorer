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
import org.jpablo.typeexplorer.ui.app.Path
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.models
import com.raquo.airstream.core.Signal


case class AppState(
  packageTreeState: PackageTreeState = PackageTreeState(),
  projectPath: StoredString = storedString("projectPath", initial = ""),
  $diagramSelection: Var[Set[models.Symbol]] = Var(Set.empty)
):
  val $projectPath: Signal[Path] = 
    projectPath.signal.map(Path.apply)

  /**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  def $inheritanceSelection: EventStream[(Path, Set[(models.Symbol, Set[Related])], Options)] =
    val $requestBody =
      packageTreeState.symbols.signal.changes.map { symbols =>
        symbols.transform { (symbol, selection) => selection match
          case Selection(true, false, false) => Set.empty
          case Selection(_   , true , false) => Set(Related.Parents)
          case Selection(_   , false, true ) => Set(Related.Children)
          case Selection(_   , true , true ) => Set(Related.Parents, Related.Children)
          case _                             => throw Exception(s"Defect: symbol ${symbol} without selection found")
        }.toSet
      }
    $projectPath
      .combineWith($requestBody.toSignal(Set.empty), packageTreeState.options.signal)
      .changes


end AppState



case class Selection(
  current : Boolean = false, // <--
  parents : Boolean = false,
  children: Boolean = false,
):
  def allEmpty = !current && !parents && !children

object Selection:
  def empty = Selection()





type Service[A] = 
  ZPure[Nothing, Unit, Unit, A, Nothing, A]

def service[A: Tag]: Service[A] =
  ZPure.service[Unit, A]

object AppState:
  val $diagram           = service[EventStream[InheritanceDiagram]]
  val $documents         = service[EventStream[List[TextDocumentsWithSource]]]
  val $projectPath       = service[Signal[Path]]

  val $selectedNamespace = service[EventBus[models.Symbol]]
  val $diagramSelection  = service[Var[Set[models.Symbol]]]

  val $svgDiagram        = service[EventStream[dom.SVGElement]]
  val projectPath        = service[StoredString]
  val packageTreeState    = service[PackageTreeState]


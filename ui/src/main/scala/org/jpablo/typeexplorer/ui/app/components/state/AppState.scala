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
  inheritanceTabState: InheritanceTabState,
  projectPath        : StoredString
):
  val $projectPath: Signal[Path] =
    projectPath.signal.map(Path.apply)

  /**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  def $inheritanceSelection: EventStream[(Path, Set[models.Symbol], Options)] =
    $projectPath
      .combineWith(
        inheritanceTabState.$activeSymbols.signal,
        inheritanceTabState.$options.signal
      )
      .changes
end AppState


type Service[A] =
  ZPure[Nothing, Unit, Unit, A, Nothing, A]

def service[A: Tag]: Service[A] =
  ZPure.service[Unit, A]

object AppState:
  def build(projectPath: StoredString, fetchDiagram: Path => Signal[InheritanceDiagram]) =
    val state0 =
      AppState(InheritanceTabState(Signal.fromValue(InheritanceDiagram.empty)), projectPath)
    state0
      .modify(_.inheritanceTabState.$inheritanceDiagram)
      .setTo(state0.$projectPath.flatMap(fetchDiagram))


  val $diagram            = service[Signal[InheritanceDiagram]]
  val $documents          = service[EventStream[List[TextDocumentsWithSource]]]
  val $projectPath        = service[Signal[Path]]
  val svgSymbolSelected   = service[EventBus[models.Symbol]]
  val $svgDiagram         = service[EventStream[dom.SVGElement]]
  val projectPath         = service[StoredString]
  val inheritanceTabState = service[InheritanceTabState]


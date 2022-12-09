package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.Options
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, Related}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path
import org.scalajs.dom
import zio.Tag
import zio.json.*
import zio.prelude.fx.ZPure


case class AppState(
  inheritanceTabState: InheritanceTabState,
  projectPath        : StoredString,
):
  val $projectPath: Signal[Path] =
    projectPath.signal.map(Path.apply)

  private val owner: Owner = OneTimeOwner(() => ())

  val storedActiveSymbols =
    inheritanceTabState.activeSymbolsJson.signal.map(_.fromJson[Map[Path, Set[models.Symbol]]].getOrElse(Map.empty))

  projectPath.signal.combineWith(inheritanceTabState.$activeSymbols.signal, storedActiveSymbols).foreach((path, symbols, map0) => {
    val map = map0 + (Path(path) -> symbols)
    inheritanceTabState.activeSymbolsJson.set(map.toJson)
  })(owner)


/**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  def $inheritanceSelection: Signal[(Path, Set[models.Symbol], Options)] =
    $projectPath
      .combineWith(
        inheritanceTabState.$activeSymbols.signal,
        inheritanceTabState.$options.signal
      )

end AppState


type Service[A] =
  ZPure[Nothing, Unit, Unit, A, Nothing, A]

def service[A: Tag]: Service[A] =
  ZPure.service[Unit, A]

object AppState:
  def build(projectPath: StoredString, fetchDiagram: Path => Signal[InheritanceDiagram]) =

    val owner: Owner = OneTimeOwner(() => ())
    val activeSymbolsJson = storedString("activeSymbols", initial = "{}")

    val storedActiveSymbols = activeSymbolsJson.signal.map(_.fromJson[Map[Path, Set[models.Symbol]]].getOrElse(Map.empty))

    val $ss =
      projectPath.signal.combineWith(storedActiveSymbols).map((path, map) => map.getOrElse(Path(path), Set.empty))

    val symbols = $ss.observe(owner).now()

    println(symbols)

    val state0 =
      AppState(
        InheritanceTabState(
          Signal.fromValue(InheritanceDiagram.empty),
          activeSymbolsJson,
          Var(symbols)
        ),
        projectPath
      )
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


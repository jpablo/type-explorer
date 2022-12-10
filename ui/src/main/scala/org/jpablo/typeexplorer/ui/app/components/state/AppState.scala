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

  /**
    * A selection consists of:
    * - the basePath (aka project Path)
    * - the selected symbol with its "related" configuration (i.e. parents, children, etc)
    * - diagram options
    */
  val $inheritanceSelection: Signal[(Path, Set[models.Symbol], Options)] =
    $projectPath
      .combineWith(
        inheritanceTabState.$activeSymbols.signal,
        inheritanceTabState.$options.signal
      )

  private val owner: Owner = OneTimeOwner(() => ())

  $inheritanceSelection.foreach { (path, symbols, _) =>
    inheritanceTabState.activeSymbolsJson.update { s =>
      val map0 = s.fromJson[Map[Path, Set[models.Symbol]]].getOrElse(Map.empty)
      val map = map0 + (path -> symbols)
      map.toJson
    }
  }(owner)

end AppState


type Service[A] =
  ZPure[Nothing, Unit, Unit, A, Nothing, A]

def service[A: Tag]: Service[A] =
  ZPure.service[Unit, A]

object AppState:
  def build(fetchDiagram: Path => Signal[InheritanceDiagram]) =
    val projectPath = storedString("projectPath", initial = "")
    val activeSymbolsJson = storedString("activeSymbols", initial = "{}")

    val owner: Owner = OneTimeOwner(() => ())

    val storedActiveSymbols = activeSymbolsJson.signal.map(_.fromJson[Map[Path, Set[models.Symbol]]].getOrElse(Map.empty))

    val $ss =
      projectPath.signal.combineWith(storedActiveSymbols).map((path, map) => map.getOrElse(Path(path), Set.empty))

    val activeSymbols = $ss.observe(owner).now()

    println(activeSymbols)

    val state0 =
      AppState(
        InheritanceTabState(
          Signal.fromValue(InheritanceDiagram.empty),
          activeSymbolsJson,
        ),
        projectPath
      )
    state0
      .modify(_.inheritanceTabState.$inheritanceDiagram).setTo(state0.$projectPath.flatMap(fetchDiagram))
      .modify(_.inheritanceTabState.$activeSymbols).setTo(Var(activeSymbols))


  val $diagram            = service[Signal[InheritanceDiagram]]
  val $documents          = service[EventStream[List[TextDocumentsWithSource]]]
  val $projectPath        = service[Signal[Path]]
  val svgSymbolSelected   = service[EventBus[models.Symbol]]
  val $svgDiagram         = service[EventStream[dom.SVGElement]]
  val projectPath         = service[StoredString]
  val inheritanceTabState = service[InheritanceTabState]


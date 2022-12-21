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
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
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

  private def storedActiveSymbols: Set[models.Symbol] =
    val $storedActiveSymbols = inheritanceTabState.activeSymbolsJson.signal.map(parseStoredSymbols)
    val $symbols = $projectPath.combineWith($storedActiveSymbols).map((path, map) => map.getOrElse(path, Set.empty))
    $symbols.observe(owner).now()

  private def parseStoredSymbols(json: String): Map[Path, Set[models.Symbol]] =
    json.fromJson[Map[Path, Set[models.Symbol]]].getOrElse(Map.empty)

  // ---------------------------------
  // Persist changes to $activeSymbols
  // ---------------------------------
  inheritanceTabState.$activeSymbols.signal.withCurrentValueOf($projectPath).foreach { (symbols, path) =>
    inheritanceTabState.activeSymbolsJson.update { json =>
      (parseStoredSymbols(json) + (path -> symbols)).toJson
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
    // first create an empty AppState with default values
    val state0 =
      AppState(
        InheritanceTabState(activeSymbolsJson),
        projectPath
      )
    // now update with some calculated values
    state0
      .modify(_.inheritanceTabState.$inheritanceDiagram).setTo(state0.$projectPath.flatMap(fetchDiagram))
      .modify(_.inheritanceTabState.$activeSymbols).setTo(Var(state0.storedActiveSymbols))


  val $documents          = service[EventStream[List[TextDocumentsWithSource]]]
  val $projectPath        = service[Signal[Path]]
  val $inheritanceSvgDiagram = service[Signal[InheritanceSvgDiagram]]
  val projectPath         = service[StoredString]
  val inheritanceTabState = service[InheritanceTabState]


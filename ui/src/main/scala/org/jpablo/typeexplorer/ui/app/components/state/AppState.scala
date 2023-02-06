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
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.DiagramOptions
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom
import zio.Tag
import zio.json.*


case class AppState(
  inheritanceTabState: InheritanceTabState,
  projectPath        : StoredString,
  $devMode           : Var[Boolean]
):
  val $projectPath: Signal[Path] =
    projectPath.signal.map(Path.apply)

  private val owner: Owner = OneTimeOwner(() => ())

  private def storedActiveSymbols: InheritanceTabState.ActiveSymbols =
    val $storedActiveSymbols = inheritanceTabState.activeSymbolsJson.signal.map(parseStoredSymbols)
    val $symbols = $projectPath.combineWith($storedActiveSymbols).map((path, map) => map.getOrElse(path, List.empty)).map(_.toMap)
    $symbols.observe(owner).now()

  private def parseStoredSymbols(json: String): Map[Path, ActiveSymbolsSeq] =
    json.fromJson[Map[Path, ActiveSymbolsSeq]].getOrElse(Map.empty)

  // ---------------------------------
  // Persist changes to $activeSymbols
  // ---------------------------------
  inheritanceTabState.$activeSymbols.signal.withCurrentValueOf($projectPath).foreach { (symbols, path) =>
    inheritanceTabState.activeSymbolsJson.update: json =>
      (parseStoredSymbols(json) + (path -> symbols.toList)).toJson
  }(owner)

end AppState

case class InheritanceConfiguration(
  showFields: Boolean,
  showSignatures: Boolean
)
case class PackagesConfiguration(
  showTests: Boolean,
  showObjects: Boolean
)

case class AppConfiguration(
  projectPath: Path,
  activeSymbols: Map[Path, Set[models.Symbol]],
  inheritanceConfiguration: InheritanceConfiguration,
  packagesConfiguration: PackagesConfiguration
)

object AppConfiguration:
  given JsonCodec[InheritanceConfiguration] = DeriveJsonCodec.gen
  given JsonCodec[PackagesConfiguration] = DeriveJsonCodec.gen
  given JsonCodec[AppConfiguration] = DeriveJsonCodec.gen

object AppState:

  private def parseStoredConfiguration(json: String): AppConfiguration =
    json.fromJson[AppConfiguration].getOrElse(
      AppConfiguration(
        Path(""), Map.empty,
        InheritanceConfiguration(true, true),
        PackagesConfiguration(true, true)
      )
    )


  def build(fetchDiagram: Path => Signal[InheritanceDiagram]) =
    val configJson = storedString("configuration", initial = "{}")

    val owner: Owner = OneTimeOwner(() => ())

    val appConfiguration: AppConfiguration =
      val $appConfiguration = configJson.signal.map(parseStoredConfiguration)
      $appConfiguration.observe(owner).now()

    println(appConfiguration)

    val projectPath = storedString("projectPath", initial = "")
    val activeSymbolsJson = storedString("activeSymbols", initial = "{}")
    // first create an empty AppState with default values
    val state0 =
      AppState(
        InheritanceTabState(activeSymbolsJson),
        projectPath,
        Var(true)
      )
    // now update with some calculated values
    state0
      .modify(_.inheritanceTabState.$inheritanceDiagram).setTo(state0.$projectPath.flatMap(fetchDiagram))
      .modify(_.inheritanceTabState.$activeSymbols).setTo(Var(state0.storedActiveSymbols))




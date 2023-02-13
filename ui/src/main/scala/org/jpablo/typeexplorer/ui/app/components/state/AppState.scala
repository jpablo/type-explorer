package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.EventStream
import com.raquo.airstream.core.Signal
import com.raquo.airstream.eventbus.EventBus
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.protos.TextDocumentsWithSource
import org.jpablo.typeexplorer.shared.inheritance.PlantumlInheritance.DiagramOptions
import org.jpablo.typeexplorer.shared.inheritance.{InheritanceDiagram, PlantumlInheritance}
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom
import zio.json.*

class Persistent[A: JsonCodec](storedString: StoredString, initial: A)(using Owner):
  private val $var: Var[A] =
    Var {
      storedString.signal
        .map(_.fromJson[A].getOrElse(initial))
        .observe
        .now()
    }
  def start() =
    $var.signal.foreach: a =>
      storedString.set(a.toJson)
    $var


case class AppState(
  inheritanceTabState: InheritanceTabState,
  projectPath        : StoredString,
  appConfigJson      : StoredString,
)(using Owner):
  val $projectPath = projectPath.signal.map(Path.apply)

  val $appConfig: Var[AppConfig] =
    Persistent(appConfigJson, AppConfig())
      .start()

  def updateAppConfig(f: AppConfig => AppConfig): Unit =
    $appConfig.update(f)



object AppState:
  def build(fetchDiagram: Path => Signal[InheritanceDiagram]) =
    given owner: Owner = OneTimeOwner(() => ())

    val appConfigJson = storedString("appConfig", initial = "{}")

    // TODO:
//    val projectJson = storedString("project", initial = "{}")
    // projectPath should be part of projectJson
    val projectPath = storedString("projectPath", initial = "")

    //    val diagramJson = storedString("diagram", initial = "{}")
    // activeSymbolsJson should be part of diagramJson
    val activeSymbolsJson = storedString("activeSymbols", initial = "{}")

    val $projectPath = projectPath.signal.map(Path.apply)
    AppState(
      inheritanceTabState = InheritanceTabState(
        activeSymbolsJson,
        $projectPath,
        $projectPath.flatMap(fetchDiagram),
      ),
      projectPath = projectPath,
      appConfigJson = appConfigJson
    )




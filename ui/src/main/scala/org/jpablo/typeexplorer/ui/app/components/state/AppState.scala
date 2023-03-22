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
import com.softwaremill.quicklens.*
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import InheritanceTabState.ActiveSymbols


def persistent[A: JsonCodec](storedString: StoredString, initial: A)(using Owner): Var[A] =
  val $var: Var[A] =
    Var {
      storedString.signal
        .map { str =>
          str.fromJson[A] match
            case Left(value) =>
              dom.console.error(s"Error parsing json: $value")
              initial
            case Right(value) => value
        }
        .observe
        .now()
    }
  $var.signal.foreach: a =>
    storedString.set(a.toJson)
  $var


case class AppState(
  inheritanceTabState: InheritanceTabState,
  appConfigJson      : StoredString,
)(using Owner):

  val appConfigs: Var[AppConfig] =
    persistent(appConfigJson, AppConfig())

  def updateAppConfig(f: AppConfig => AppConfig): Unit =
    appConfigs.update(f)

  val basePaths: Signal[List[Path]] =
    appConfigs.signal.map(_.basePaths)



object AppState:
  def build(fetchDiagram: List[Path] => Signal[InheritanceDiagram]) =
    given owner: Owner = OneTimeOwner(() => ())

    val appState0 =
      AppState(InheritanceTabState(), storedString("appConfig", initial = "{}"))

    val activeSymbols: Var[ActiveSymbols] =
      appState0.appConfigs
        .zoom(_.activeSymbols.toMap): (appConfig, activeSymbols) =>
          appConfig.modify(_.activeSymbols).setTo(activeSymbols.toList)

    appState0.copy(
      inheritanceTabState =
        InheritanceTabState(
          activeSymbols,
          appState0.basePaths.flatMap(fetchDiagram)
        )
    )




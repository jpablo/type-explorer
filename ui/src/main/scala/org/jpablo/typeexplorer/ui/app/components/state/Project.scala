package org.jpablo.typeexplorer.ui.app.components.state

import com.raquo.airstream.core.Signal
import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import com.softwaremill.quicklens.*
import io.laminext.syntax.core.{StoredString, storedString}
import org.jpablo.typeexplorer.shared.inheritance.InheritanceDiagram
import org.jpablo.typeexplorer.ui.app.Path
import org.jpablo.typeexplorer.ui.app.components.state.InheritanceTabState.ActiveSymbols
import org.scalajs.dom
import zio.json.*


def persistent[A: JsonCodec](storedString: StoredString, initial: A)(using Owner): Var[A] =
  val aVar: Var[A] =
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
  aVar.signal.foreach: a =>
    storedString.set(a.toJson)
  aVar


case class Project(
  inheritanceTabState: InheritanceTabState,
  appConfigJson      : StoredString, // global (AppConfig)
)(using Owner):

  val appConfig: Var[AppConfig] =
    persistent(appConfigJson, AppConfig())

  val documents: Var[AppConfig] =
    persistent(appConfigJson, AppConfig())

  def updateAppConfig(f: AppConfig => AppConfig): Unit =
    appConfig.update(f)

  val basePaths: Signal[List[Path]] =
    appConfig.signal.map(_.basePaths)



object Project:
  def build(fetchDiagram: List[Path] => Signal[InheritanceDiagram]) =
    given owner: Owner = OneTimeOwner(() => ())

    val appState0 =
      Project(InheritanceTabState(), storedString("appConfig", initial = "{}"))

    val activeSymbols: Var[ActiveSymbols] =
      appState0.appConfig
        .zoom(_.activeSymbols.toMap): (appConfig, activeSymbols) =>
          appConfig.modify(_.activeSymbols).setTo(activeSymbols.toList)

    appState0.copy(
      inheritanceTabState =
        InheritanceTabState(
          activeSymbols,
          appState0.basePaths.flatMap(fetchDiagram)
        )
    )

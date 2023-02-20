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

  val $appConfig: Var[AppConfig] =
    persistent(appConfigJson, AppConfig())

  def updateAppConfig(f: AppConfig => AppConfig): Unit =
    $appConfig.update(f)

  val $basePaths: Signal[List[Path]] =
    $appConfig.signal.map(_.basePaths)



object AppState:
  def build(fetchDiagram: List[Path] => Signal[InheritanceDiagram]) =
    given owner: Owner = OneTimeOwner(() => ())

    val appConfigJson = storedString("appConfig", initial = "{}")
    val appState0 = AppState(InheritanceTabState(), appConfigJson)

    val $activeSymbols: Var[ActiveSymbols] =
      appState0.$appConfig
        .zoom(appConfig =>
          appConfig.basePaths
            .flatMap { path =>
              appConfig.allActiveSymbols.get(path).toList.flatMap(_.map((s, o) => s -> (o, path)))
            }
            .toMap
        )((activeSymbols: ActiveSymbols) =>

          appState0.$appConfig.now()
            .modify(_.allActiveSymbols)
            .using { (allActiveSymbols: Map[Path, ActiveSymbolsSeq]) =>
              val as =
                activeSymbols.toList
                  .map { case (s, (o, p)) => p -> (s, o) }
                  .groupBy(_._1)
                  .transform((_, v) => v.map(_._2))

              allActiveSymbols ++ as
            }
        )

    appState0.copy(
      inheritanceTabState =
        InheritanceTabState(
          $activeSymbols,
          appState0.$basePaths.flatMap(fetchDiagram)
        )
    )




package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.client.{fetchInheritanceDiagram, fetchDocuments, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.scalajs.dom.document
import zio.ZEnvironment

object MainJS:
  def main(args: Array[String]): Unit =

    val appState     = AppState()
    val $documents   = fetchDocuments(appState.$projectPath)
    val $inheritanceDiagram = fetchInheritanceDiagram(appState.$projectPath)
    val $inheritance = appState.$inheritanceSelection.flatMap(fetchInheritanceSVGDiagram)
    val $setSymbol   = EventBus[Symbol]()

    val appEnv =
      ZEnvironment(
        appState.$projectPath,
        $documents,
        $inheritance,
        $inheritanceDiagram,
        appState.inheritanceTabState,
      ) ++ ZEnvironment(
        $setSymbol,
        appState.projectPath,
        appState.inheritanceTabState.$canvasSelection
      )

    val app =
      TopLevel.provideEnvironment(appEnv)

    render(
      document.querySelector("#app"),
      app.run
    )

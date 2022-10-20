package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.client.{fetchClasses, fetchDocuments, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.scalajs.dom.document
import zio.ZEnvironment

object MainJS:
  def main(args: Array[String]): Unit =

    val appState        = AppState()
    val $documents      = fetchDocuments(appState.$projectPath)
    val $classes        = fetchClasses(appState.$projectPath)
    val $inheritance    = appState.$inheritanceSelection.flatMap(fetchInheritanceSVGDiagram)
    val $setSymbol      = EventBus[Symbol]()

    val appEnv = 
      ZEnvironment(
        appState.$projectPath,
        $documents,
        $inheritance,
        $classes,
        appState.selectedSymbols,
      ) ++ ZEnvironment(
        $setSymbol, 
        appState.projectPath,
        appState.$diagramSelection
      )


    render(
      document.querySelector("#app"), 
      TopLevel.provideEnvironment(appEnv).run
    )
package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import io.laminext.syntax.core.storedString
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.scalajs.dom.document
import zio.ZEnvironment

object MainJS:
  def main(args: Array[String]): Unit =

    val projectPath = storedString("projectPath", initial = "")
    val $projectPath = projectPath.signal.map(Path.apply)
    val $inheritanceDiagram = fetchInheritanceDiagram($projectPath)

    val appState     = AppState(InheritanceTabState($inheritanceDiagram), projectPath)
    val $documents   = fetchDocuments($projectPath)
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

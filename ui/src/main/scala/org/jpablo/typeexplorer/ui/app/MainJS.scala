package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.scalajs.dom.document
import zio.ZEnvironment
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram

object MainJS:

  def main(args: Array[String]): Unit =
    val appState     = AppState.build(fetchInheritanceDiagram)
    val $documents   = fetchDocuments(appState.$projectPath)
    val $inheritanceSvgDiagram = fetchInheritanceSVGDiagram(appState).startWith(InheritanceSvgDiagram.empty)

    val appEnv =
      ZEnvironment(
        appState.$projectPath,
        $documents,
        $inheritanceSvgDiagram,
        appState.inheritanceTabState,
        appState.projectPath
      )

    val app =
      TopLevel.provideEnvironment(appEnv)

    render(
      document.querySelector("#app"),
      app.run
    )

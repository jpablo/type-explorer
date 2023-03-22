package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.shared.models.Symbol
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, InheritanceTabState}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.scalajs.dom.document
import zio.ZEnvironment
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import zio.json.*

object MainJS:

  def main(args: Array[String]): Unit =
    val appState: AppState = AppState.build(fetchInheritanceDiagram)

    val $documents = fetchDocuments(appState.basePaths)
    val $inheritanceSvgDiagram = fetchInheritanceSVGDiagram(appState).startWith(InheritanceSvgDiagram.empty)

    val app = TopLevel(appState, $inheritanceSvgDiagram, $documents)

    render(document.querySelector("#app"), app)

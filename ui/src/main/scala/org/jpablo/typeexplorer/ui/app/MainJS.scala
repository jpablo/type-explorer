package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom.document

object MainJS:

  def main(args: Array[String]): Unit =
    val projectId = "e96495a0-6f86-41ab-bfc0-390063ac7326"
    val appState = AppState.load(fetchInheritanceDiagram, Some(projectId))
    val documents = fetchDocuments(appState.basePaths)
    val inheritanceSvgDiagram = fetchInheritanceSVGDiagram(appState).startWith(InheritanceSvgDiagram.empty)
    val app = TopLevel(appState, inheritanceSvgDiagram, documents)
    render(document.querySelector("#app"), app)

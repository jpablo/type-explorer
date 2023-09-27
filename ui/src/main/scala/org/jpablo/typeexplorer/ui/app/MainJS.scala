package org.jpablo.typeexplorer.ui.app

import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchInheritanceDiagram, fetchInheritanceSVGDiagram}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.AppState
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom

object MainJS:

  def main(args: Array[String]): Unit =
    given OneTimeOwner(() => ())
    val projectId = dom.window.location.pathname.split("/").lastOption
    val appState = AppState.load(fetchInheritanceDiagram, projectId)
    val documents = fetchDocuments(appState.basePaths)
    val inheritanceSvgDiagram = fetchInheritanceSVGDiagram(appState).startWith(InheritanceSvgDiagram.empty)
    val selectedProject = new EventBus[String]
    selectedProject.events.foreach { projectId =>
      dom.window.location.href = s"/$projectId"
    }
    val app = TopLevel(appState, inheritanceSvgDiagram, documents, selectedProject)
    render(dom.document.querySelector("#app"), app)

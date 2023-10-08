package org.jpablo.typeexplorer.ui.app

import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.laminar.api.L.*
import org.jpablo.typeexplorer.ui.app.client.{
  fetchDocuments,
  fetchInheritanceDiagram,
  fetchInheritanceSVGDiagram
}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom

object MainJS:

  def main(args: Array[String]): Unit =
    given OneTimeOwner(() => ())
    val projectId: Option[ProjectId] =
      dom.window.location.pathname.split("/").lastOption.map(ProjectId.apply)
    val selectedProject = new EventBus[ProjectId]
    val deleteProject = new EventBus[ProjectId]
    selectedProject.events.foreach { projectId =>
      dom.window.location.href = s"/${projectId.value}"
    }
    val appState = AppState.load(fetchInheritanceDiagram, projectId)
    val documents = fetchDocuments(appState.basePaths)
    deleteProject.events.foreach(appState.deleteProject)
    val inheritanceSvgDiagram = fetchInheritanceSVGDiagram(appState).startWith(
      InheritanceSvgDiagram.empty
    )

    val app = TopLevel(
      appState,
      inheritanceSvgDiagram,
      documents,
      selectedProject,
      deleteProject
    )
    render(dom.document.querySelector("#app"), app)

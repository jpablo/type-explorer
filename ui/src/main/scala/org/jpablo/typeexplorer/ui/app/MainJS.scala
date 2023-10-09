package org.jpablo.typeexplorer.ui.app

import com.raquo.airstream.ownership.OneTimeOwner
import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.client.{
  fetchDocuments,
  fetchInheritanceDiagram,
  fetchInheritanceSVGDiagram
}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.jpablo.typeexplorer.ui.app.components.tabs.inheritanceTab.InheritanceSvgDiagram
import org.scalajs.dom
import org.scalajs.dom.HTMLDivElement

object MainJS:

  def main(args: Array[String]): Unit =
    projectIdFromLocation() match
      case None =>
        setProjectIdInLocation(ProjectId.random)

      case Some(projectId) =>
        render(dom.document.querySelector("#app"), createApp(projectId))

  def setProjectIdInLocation(projectId: ProjectId): Unit =
    dom.window.location.href = s"/${projectId.value}"

  def projectIdFromLocation(): Option[ProjectId] =
    dom.window.location.pathname.split("/").lastOption.map(ProjectId.apply)

  def createApp(projectId: ProjectId): ReactiveHtmlElement[HTMLDivElement] =
    given OneTimeOwner(() => ())
    val selectedProject = new EventBus[ProjectId]
    selectedProject.events.foreach(setProjectIdInLocation)
    val deleteProject = new EventBus[ProjectId]
    val appState = AppState.load(fetchInheritanceDiagram, projectId)
    deleteProject.events.foreach(appState.deleteProject)
    TopLevel(
      appState,
      fetchInheritanceSVGDiagram(appState)
        .startWith(InheritanceSvgDiagram.empty),
      fetchDocuments(appState.basePaths),
      selectedProject,
      deleteProject
    )

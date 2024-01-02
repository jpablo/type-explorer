package org.jpablo.typeexplorer.ui.app

import com.raquo.laminar.api.L.*
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.jpablo.typeexplorer.ui.app.client.{fetchDocuments, fetchFullInheritanceGraph}
import org.jpablo.typeexplorer.ui.app.components.TopLevel
import org.jpablo.typeexplorer.ui.app.components.state.{AppState, ProjectId}
import org.scalajs.dom

object MainJS:

  def main(args: Array[String]): Unit =
    projectIdFromLocation() match
      case None            => setProjectIdInLocation(ProjectId.random)
      case Some(projectId) => render(dom.document.querySelector("#app"), createApp(projectId))

  private def setProjectIdInLocation(projectId: ProjectId): Unit =
    dom.window.location.href = s"/${projectId.value}"

  private def projectIdFromLocation(): Option[ProjectId] =
    dom.window.location.pathname.split("/").lastOption.map(ProjectId.apply)

  private def createApp(projectId: ProjectId): ReactiveHtmlElement[dom.HTMLDivElement] =
    given Owner = unsafeWindowOwner
    val selectedProject = new EventBus[ProjectId]
    selectedProject.events.foreach(setProjectIdInLocation)
    val deleteProject = new EventBus[ProjectId]
    val appState = AppState.load(fetchFullInheritanceGraph, projectId)
    deleteProject.events.foreach(appState.deleteProject)
    TopLevel(
      appState,
      fetchDocuments(appState.basePaths),
      selectedProject,
      deleteProject,
      setupErrorHandling()
    )

  private def setupErrorHandling()(using Owner): EventBus[String] =
    val errors = new EventBus[String]
    AirstreamError.registerUnhandledErrorCallback: ex =>
      errors.emit(ex.getMessage)
    windowEvents(_.onError).foreach: e =>
      errors.emit(e.message)
    errors

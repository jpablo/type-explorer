package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.{
  DiagramOptions,
  PackagesOptions,
  ProjectSettings
}
import zio.json.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq

import scalajs.js

case class ProjectId(value: String) extends AnyVal

object ProjectId:
  given JsonCodec[ProjectId] =
    JsonCodec(
      JsonEncoder.string.contramap(_.value),
      JsonDecoder.string.map(ProjectId(_))
    )
  given JsonFieldEncoder[ProjectId] =
    JsonFieldEncoder.string.contramap(_.value)
  given JsonFieldDecoder[ProjectId] =
    JsonFieldDecoder.string.map(ProjectId(_))

  def random =
    ProjectId(js.Dynamic.global.crypto.randomUUID().toString)
end ProjectId

/** Structure of the persisted state (in local storage)
  */
case class PersistedAppState(
    projects: Map[ProjectId, Project] = Map.empty,
    lastActiveProjectId: Option[ProjectId] = None
) derives JsonCodec:

  def selectOrCreateProject(projectId: ProjectId): Project =
    projects.getOrElse(projectId, Project(projectId))

  def deleteProject(projectId: ProjectId): PersistedAppState =
    copy(
      projects = projects - projectId,
      lastActiveProjectId =
        if lastActiveProjectId.contains(projectId) then None
        else lastActiveProjectId
    )

case class Project(
    id: ProjectId,
    name: String = "",
    advancedMode: Boolean = false,
    packagesOptions: PackagesOptions = PackagesOptions(),
    projectSettings: ProjectSettings = ProjectSettings(),
    pages: Vector[Page] = Vector(Page())
) derives JsonCodec

case class Page(
    // This can't be a Map[A, Option[B]], as zio-json will remove entries with None values
    activeSymbols: ActiveSymbolsSeq = List.empty,
    diagramOptions: DiagramOptions = DiagramOptions()
) derives JsonCodec

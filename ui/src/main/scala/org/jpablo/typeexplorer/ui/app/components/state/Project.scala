package org.jpablo.typeexplorer.ui.app.components.state

import org.jpablo.typeexplorer.shared.inheritance.DiagramOptions
import zio.json.*
import org.jpablo.typeexplorer.shared.models
import org.jpablo.typeexplorer.shared.webApp.ActiveSymbolsSeq
import org.jpablo.typeexplorer.ui.app.Path
import scalajs.js

case class PackagesOptions(
    onlyActive: Boolean = false,
    onlyTests: Boolean = false,
    nsKind: Set[models.NamespaceKind] = models.NamespaceKind.values.toSet
) derives JsonCodec

case class ProjectId(value: String) extends AnyVal

object ProjectId:
  given JsonCodec[ProjectId] =
    JsonCodec(
      JsonEncoder.string.contramap(_.value),
      JsonDecoder.string.map(ProjectId(_))
    )

  given JsonFieldEncoder[ProjectId] = JsonFieldEncoder.string.contramap(_.value)
  given JsonFieldDecoder[ProjectId] = JsonFieldDecoder.string.map(ProjectId(_))

  def random =
    ProjectId(js.Dynamic.global.crypto.randomUUID().toString)
end ProjectId

/** Structure of the persisted state (in local storage)
  */
case class PersistedAppState(
    projects: Map[ProjectId, Project] = Map.empty,
    lastActiveProjectId: Option[ProjectId] = None
) derives JsonCodec:

  def lastActiveProject: Project =
    lastActiveProjectId
      .flatMap(projects.get)
      .getOrElse(Project(ProjectId.random))

  def selectProject(projectId: Option[ProjectId]): Project =
    projectId
      .flatMap(projects.get)
      .getOrElse(lastActiveProject)

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
    diagramOptions: DiagramOptions = DiagramOptions(),
    basePaths: List[Path] = List.empty,
    // This can't be a Map[A, Option[B]], as zio-json will remove entries with None values
    activeSymbols: ActiveSymbolsSeq = List.empty
) derives JsonCodec
